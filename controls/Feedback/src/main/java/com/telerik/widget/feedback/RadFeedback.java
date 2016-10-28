package com.telerik.widget.feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class provides infrastructure for sending feedback to the developers from withing an Android
 * application.
 */
public class RadFeedback implements AdapterView.OnItemClickListener {
    static final String STATUS_OPEN = "open";
    static final int ALL_ITEMS = 0;
    static final int OPEN_ITEMS = 1;
    static final int RESOLVED_ITEMS = 2;
    private static final String USER_PREFS_FILENAME = "feedback_user_prefs";
    private static final String USER_AUTHOR_KEY = "author";
    private static final String SHOW_PREFS_PROMPT = "showPrefsDialog";
    private static final byte PNG_COMPRESS_QUALITY = 50;
    private static final double MAX_STREAM_SIZE = 500000;
    private static final float SIZE_COEFFICIENT = 0.85f;
    private String apiKey;
    private String serviceUri;
    private String uid;
    private AlertDialog popup;
    private FeedbackItem selectedItem;
    private ArrayList<MainMenuItem> items = new ArrayList<MainMenuItem>();
    private Context initiatingContext;
    private BitmapResolver bitmapResolver;

    private String currentAuthorName;

    private JSONObject customContent;

    OnSendFeedbackFinishedListener feedbackFinishedListener;
    private static RadFeedback instance;
    private HashMap<Integer, AsyncTask> pendingTasks = new HashMap<Integer, AsyncTask>();

    /**
     * Creates an instance of the {@link com.telerik.widget.feedback.RadFeedback} component
     * with specified application key and service URI.
     */
    private RadFeedback() {
    }

    /**
     * Gets the single instance of {@link com.telerik.widget.feedback.RadFeedback}
     *
     * @return the single {@link com.telerik.widget.feedback.RadFeedback}.
     */
    public static RadFeedback instance() {
        if (instance == null) {
            instance = new RadFeedback();
        }
        return instance;
    }

    public BitmapResolver getBitmapResolver() {
        return bitmapResolver;
    }

    public void setBitmapResolver(BitmapResolver bitmapResolver) {
        if(bitmapResolver == null) {
            this.bitmapResolver = new BitmapResolverBase();
        }
        this.bitmapResolver = bitmapResolver;
    }

    /**
     * Initializes the {@link com.telerik.widget.feedback.RadFeedback} instance with the provided
     * service Uri and application key.
     *
     * @param apiKey     the application key identify the backend project to which the feedback component will connect.
     * @param serviceUri the uri of the service.
     * @param uid        identifies the current feedback client in the backend. By using this value feedback items
     *                   submitted by this client only will be visible to the end users of the application.
     */
    public void init(String apiKey, String serviceUri, String uid) {

        if (apiKey == null || apiKey.trim().toString().isEmpty()) {
            throw new InvalidParameterException("apiKey must not be null or empty.");
        }

        if (serviceUri == null || serviceUri.trim().toString().isEmpty()) {
            throw new InvalidParameterException("serviceUri must not be null or empty.");
        }

        if (uid == null || uid.trim().toString().isEmpty()) {
            throw new InvalidParameterException("uid must not be null or empty.");
        }

        this.apiKey = apiKey;
        this.serviceUri = serviceUri;
        this.uid = uid;
        this.bitmapResolver = new BitmapResolverBase();
    }

    /**
     * Sets an implementation of the {@link com.telerik.widget.feedback.RadFeedback.OnSendFeedbackFinishedListener}
     * interface which is called when the feedback procedure has ended.
     *
     * @param listener the implementation to initialize.
     */
    public void setOnFeedbackFinishedListener(OnSendFeedbackFinishedListener listener) {
        this.feedbackFinishedListener = listener;
    }

    /**
     * Gets API key that identifies the backend application
     * currently associated with this instance of {@link com.telerik.widget.feedback.RadFeedback}.
     *
     * @return a string representing the application key.
     */
    public String apiKey() {
        return this.apiKey;
    }

    /**
     * Gets URI that identifies the service to which the current
     * {@link com.telerik.widget.feedback.RadFeedback} is connected.
     *
     * @return a string representing the service URI.
     */
    public String serviceUri() {
        return this.serviceUri;
    }

    void selectItem(FeedbackItem item) {
        this.selectedItem = item;
    }

    FeedbackItem selectedItem() {
        return this.selectedItem;
    }

    void setCurrentAuthorName(String name) {
        this.currentAuthorName = name;
        this.storeUserDetails(name);
    }

    String getCurrentAuthorName() {
        if (this.currentAuthorName == null) {
            this.initUserDetails();
        }
        return this.currentAuthorName;
    }


    /**
     * Downloads the currently available {@link com.telerik.widget.feedback.FeedbackItem} instances
     * associated with the initialized application key.
     *
     * @return an {@link java.util.ArrayList} containing the feedback items.
     */
    int getCurrentItems(final OnItemsDeliveredCallback callback, final int filter, final int page, final int pageSize) {
        AsyncTask getThreadsTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                final ArrayList<FeedbackItem> result = new ArrayList<FeedbackItem>();
                RequestResult requestResult = new RequestResult();
                try {

                    URL url = new URL(RadFeedback.instance().serviceUri() + "/" + RadFeedback.instance().apiKey() + "/threads");
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    urlConnection.setRequestProperty("x-sort-meta", "{\"CreatedAt\" : -1}");
                    String openFilter = "{ \"$and\" : [{ \"Uid\" : \"" + uid + "\" }, {\"State\" : \"Open\"}, {\"RootId\" : null}, {\"ProjectId\" : \"" + apiKey + "\"}]}";
                    String closedFilter = "{ \"$and\" : [{ \"Uid\" : \"" + uid + "\" }, {\"State\" : \"Resolved\"}, {\"RootId\" : null}, {\"ProjectId\" : \"" + apiKey + "\"}]}";
                    String allFilter = "{ \"$and\" : [{ \"Uid\" : \"" + uid + "\" }, {\"RootId\" : null}, {\"ProjectId\" : \"" + apiKey + "\"}]}";
                    if (filter == RadFeedback.OPEN_ITEMS) {
                        urlConnection.setRequestProperty("x-filter-meta", openFilter);
                    } else if (filter == RadFeedback.RESOLVED_ITEMS) {
                        urlConnection.setRequestProperty("x-filter-meta", closedFilter);
                    } else if (filter == RadFeedback.ALL_ITEMS) {
                        urlConnection.setRequestProperty("x-filter-meta", allFilter);
                    }

                    urlConnection.setRequestProperty("x-skip-meta", String.valueOf(page * pageSize));
                    urlConnection.setRequestProperty("x-take-meta", String.valueOf(pageSize));

                    urlConnection.connect();

                    String jsonString = convertStreamToString(urlConnection.getInputStream());
                    JSONArray jsonObject = new JSONArray(jsonString);

                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject current = (JSONObject) jsonObject.get(i);
                        FeedbackItem item = new FeedbackItem(current);
                        result.add(item);
                    }
                    requestResult.results = result;

                    urlConnection.disconnect();

                } catch (Exception e) {
                    requestResult.exception = e;
                }
                return requestResult;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (this.isCancelled()) {
                    return;
                }
                pendingTasks.remove(this.hashCode());
                if (callback != null) {
                    RequestResult result = (RequestResult) o;
                    callback.onItemsDelivered(result.exception, result.results);
                }
            }
        };
        int hash = getThreadsTask.hashCode();
        this.pendingTasks.put(hash, getThreadsTask);
        getThreadsTask.execute();
        return hash;
    }

    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * Uploads the provided {@link com.telerik.widget.feedback.FeedbackItem} to the server.
     *
     * @param items the items to upload.
     */
    int submitFeedback(final ArrayList<FeedbackItem> items, final String rootId, final OnSubmitFeedbackFinishedCallback callback) {
        AsyncTask submitFeedbackTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Exception exception = null;
                SystemInfo currentSystemInfo = getSystemInfo();
                try {
                    String commentsAppendix = rootId != null ? "/" + selectedItem().getId() : "";
                    URL url = new URL(RadFeedback.instance().serviceUri() + "/" + RadFeedback.instance().apiKey() + "/threads" + commentsAppendix);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");

                    urlConnection.setRequestProperty("Content-type", "application/json");

                    urlConnection.connect();

                    for (FeedbackItem item : items) {

                        item.setSystemInfo(currentSystemInfo);
                        item.setUid(uid);
                        // Append the custom content only if the rootID is null
                        // which means that we are not commenting existing item.
                        JSONObject itemAsJson = item.toJson();
                        if (rootId == null && customContent != null) {
                            Iterator<String> keys = customContent.keys();
                            while (keys.hasNext()) {
                                String currentKey = keys.next();
                                itemAsJson.put(currentKey, customContent.get(currentKey));
                            }
                        }
                        String jsonString = itemAsJson.toString();
                        jsonString = jsonString.replace("\\", "");

                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                        bw.write(jsonString);
                        bw.flush();
                        bw.close();

                        if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            throw new RuntimeException("Failed : HTTP error code : "
                                    + urlConnection.getResponseCode());
                        }

                        String responseString = convertStreamToString(urlConnection.getInputStream());
                        JSONObject responseObject = new JSONObject(responseString);
                        JSONHelper.init(item, responseObject);
                    }
                    urlConnection.disconnect();
                } catch (Exception e) {
                    exception = e;
                }
                return exception;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (this.isCancelled()) {
                    return;
                }
                pendingTasks.remove(this.hashCode());
                if (callback != null) {
                    Exception exception = null;
                    if (o instanceof Exception) {
                        exception = (Exception) o;
                    }
                    callback.onSubmitFinished(exception);
                }
            }
        };
        int hash = submitFeedbackTask.hashCode();
        this.pendingTasks.put(hash, submitFeedbackTask);
        submitFeedbackTask.execute();
        return hash;
    }

    void cancelTask(int taskHash) {
        if (this.pendingTasks.containsKey(taskHash)) {
            this.pendingTasks.get(taskHash).cancel(false);
            this.pendingTasks.remove(taskHash);
        }
    }

    /**
     * Retrieves the comments for the provided {@link com.telerik.widget.feedback.FeedbackItem}
     * in chronological order.
     *
     * @param item the item to retrieve the comments for.
     * @return an {@link java.util.ArrayList} containing the comments.
     */
    int getCommentsForItem(final FeedbackItem item, final OnItemsDeliveredCallback callback) {

        AsyncTask getCommentsTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                final ArrayList<FeedbackItem> result = new ArrayList<FeedbackItem>();
                RequestResult requestResult = new RequestResult();
                try {

                    URL url = new URL(RadFeedback.instance().serviceUri() + "/" + RadFeedback.instance().apiKey() + "/threads");
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("x-filter-meta", "{ \"RootId\" : " + "\"" + item.getId() + "\"" + "}");
                    urlConnection.setRequestProperty("x-sort-meta", "{\"CreatedAt\" : -1}");

                    urlConnection.connect();
                    String jsonString = convertStreamToString(urlConnection.getInputStream());
                    JSONArray jsonObject = new JSONArray(jsonString);

                    for (int i = 0; i < jsonObject.length(); i++) {
                        JSONObject current = (JSONObject) jsonObject.get(i);
                        FeedbackItem item = new FeedbackItem(current);
                        result.add(item);
                    }

                    requestResult.results = result;
                    urlConnection.disconnect();

                } catch (Exception e) {
                    requestResult.exception = e;
                }
                return requestResult;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (this.isCancelled()) {
                    return;
                }
                pendingTasks.remove(this.hashCode());
                if (callback != null) {
                    RequestResult result = (RequestResult) o;
                    callback.onItemsDelivered(result.exception, result.results);
                }
            }
        };
        int hash = getCommentsTask.hashCode();
        this.pendingTasks.put(hash, getCommentsTask);
        getCommentsTask.execute();
        return hash;
    }

    /**
     * Shows a popup containing the main options of the {@link com.telerik.widget.feedback.RadFeedback}
     * component.
     *
     * @param context the {@link Context} which will be used to show the popup.
     */
    public void show(Context context) {
        this.initiatingContext = context;
        LinearLayout rootView = (LinearLayout) View.inflate(context, R.layout.main_menu_content, null);
        ListView mainMenuList = (ListView) rootView.findViewById(R.id.menuList);
        MainMenuAdapter adapter = new MainMenuAdapter(this.items, context, R.layout.main_menu_list_item);
        this.prepareMenuItems(context);
        mainMenuList.setAdapter(adapter);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(rootView.getContext());
        this.popup = alertBuilder.create();
        this.popup.setView(rootView);
        this.popup.setCancelable(true);
        this.popup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onSendFeedbackFinished();
            }
        });
        this.popup.show();
        mainMenuList.setOnItemClickListener(this);
    }

    /**
     * This method sets a {@link org.json.JSONObject} which contains custom key-value pairs
     * which will be embedded into each feedback item created on the server. This allows
     * for appending custom content into the feedback items when needed.
     *
     * @param additionalContent an instance of the {@link org.json.JSONObject} class
     *                          containing the custom content.
     */
    public void setAdditionalContent(JSONObject additionalContent) {
        this.customContent = additionalContent;
    }

    /**
     * Starts a send feedback procedure by directly showing the Send Feedback activity.
     *
     * @param context the initiating {@link Context} instance.
     */
    public void showSendFeedback(Context context) {
        this.initiatingContext = context;
        MainMenuItem sendFeedbackItem = this.getMenuItem(R.string.action_send_feedback);
        if (sendFeedbackItem != null) {
            sendFeedbackItem.getInitAction().init(context);
            Intent intent = sendFeedbackItem.intent();
            context.startActivity(intent);
        }
    }

    private SystemInfo getSystemInfo() {
        SystemInfo result = new SystemInfo();
        result.setModel(Build.MANUFACTURER + " " + Build.MODEL);
        result.setOSVersion("Android: " + Build.VERSION.RELEASE + " API level: " + Build.VERSION.SDK_INT);

        ApplicationInfo appInfo = this.initiatingContext.getApplicationInfo();
        result.setAppId(appInfo.packageName);

        try {
            String packageName = this.initiatingContext.getPackageName();
            PackageInfo info = this.initiatingContext.getPackageManager().getPackageInfo(packageName, 0);
            result.setAppVersion(String.valueOf(info.versionCode));
        } catch (Exception e) {
            Log.e("Feedback", "Error retrieving app version.", e);
        }

        if (this.initiatingContext instanceof Activity) {
            result.setWidthInPixels(String.valueOf(((Activity) this.initiatingContext).getWindow().getDecorView().getWidth()));
            result.setHeightInPixels(String.valueOf(((Activity) this.initiatingContext).getWindow().getDecorView().getHeight()));
        }
        result.setUuid(this.uid);
        return result;
    }

    private void initUserDetails() {
        SharedPreferences preferences = this.initiatingContext.getSharedPreferences(USER_PREFS_FILENAME, 0);
        this.currentAuthorName = preferences.getString(USER_AUTHOR_KEY, null);
    }

    void storeUserDetails(String authorName) {
        SharedPreferences preferences = this.initiatingContext.getSharedPreferences(USER_PREFS_FILENAME, 0);
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(USER_AUTHOR_KEY, authorName);
        prefEditor.commit();
    }

    void storeShouldShowPrefPrompt(boolean shouldShow) {
        SharedPreferences preferences = this.initiatingContext.getSharedPreferences(USER_PREFS_FILENAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHOW_PREFS_PROMPT, shouldShow);
        editor.commit();
    }

    boolean shouldShowPrefPrompt() {
        SharedPreferences preferences = this.initiatingContext.getSharedPreferences(USER_PREFS_FILENAME, 0);
        return preferences.getBoolean(SHOW_PREFS_PROMPT, true);
    }

    void onSendFeedbackFinished() {
        if (this.feedbackFinishedListener != null) {
            this.feedbackFinishedListener.sendFeedbackFinished();
        }
    }

    void showAuthorNamePrompt(Context callingContext, final OnAuthorPromptConfirmedListener promptConfirmedListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(callingContext);

        final View rootView = View.inflate(callingContext, R.layout.popup_edit_details, null);
        final EditText txtAuthor = (EditText) rootView.findViewById(R.id.txtAuthorName);
        builder.setView(rootView);
        builder.setTitle(initiatingContext.getResources().getString(R.string.title_activity_edit_details));
        builder.setCancelable(true);

        SpannableString spString = new SpannableString(initiatingContext.getResources().getString(R.string.feedback_continue_info));
        spString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setPositiveButton(spString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String authorName = txtAuthor.getText().toString();
                setCurrentAuthorName(authorName);
                storeShouldShowPrefPrompt(false);
                promptConfirmedListener.onAuthorPromptConfirmed();
            }
        });

        builder.setNeutralButton(initiatingContext.getResources().getString(R.string.feedback_button_text_anonymous), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                promptConfirmedListener.onAuthorPromptConfirmed();
                storeShouldShowPrefPrompt(false);
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        txtAuthor.addTextChangedListener(watcher);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                txtAuthor.removeTextChangedListener(watcher);
            }
        });
        dialog.show();
        Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setEnabled(false);
    }

    private void prepareMenuItems(Context context) {
        final MainMenuItem sendItem = new MainMenuItem(context.getResources().getString(R.string.action_send_feedback),
                context.getResources().getString(R.string.send_feedback_desc), new Intent(context, SendFeedbackActivity.class));
        sendItem.setInitAction(new MainMenuItem.InitAction() {
            @Override
            public void init(Context context) {
                View screenShotView = ((Activity) context).findViewById(android.R.id.content);
                Bitmap screenShotBitmap = getBitmapResolver().getBitmapFromView(screenShotView);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                screenShotBitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPRESS_QUALITY, stream);
                int streamSize = stream.size();
                while (streamSize > MAX_STREAM_SIZE) {
                    screenShotBitmap = getScaledBitmap(screenShotBitmap, streamSize);
                    stream = new ByteArrayOutputStream();
                    screenShotBitmap.compress(Bitmap.CompressFormat.PNG, PNG_COMPRESS_QUALITY, stream);
                    streamSize = stream.size();
                }
                byte[] imageBytes = stream.toByteArray();
                sendItem.intent().putExtra("image", imageBytes);
            }
        });
        MainMenuItem readItem = new MainMenuItem(context.getResources().getString(R.string.action_view_feedback),
                context.getResources().getString(R.string.view_feedback_desc), new Intent(context, ViewFeedbackActivity.class));
        MainMenuItem detailsItem = new MainMenuItem(context.getResources().getString(R.string.action_edit_settings),
                context.getResources().getString(R.string.edit_settings_desc), new Intent(context, EditDetailsActivity.class));
        this.items.clear();
        this.items.add(sendItem);
        this.items.add(readItem);
        this.items.add(detailsItem);
    }

    private MainMenuItem getMenuItem(int id) {
        this.prepareMenuItems(this.initiatingContext);
        Resources r = this.initiatingContext.getResources();
        for (MainMenuItem item : this.items) {
            if (item.title() == r.getString(id)) {
                return item;
            }
        }

        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.popup.dismiss();
        MainMenuItem targetItem = (MainMenuItem) parent.getItemAtPosition(position);
        if (targetItem.getInitAction() != null) {
            targetItem.getInitAction().init(view.getContext());
        }
        Intent intent = targetItem.intent();
        (view.getContext()).startActivity(intent);
    }

    private Bitmap getScaledBitmap(Bitmap photo, int streamSize) {
        double coef = MAX_STREAM_SIZE / (streamSize);
        coef = Math.sqrt(coef);
        int h = (int) (photo.getHeight() * coef * SIZE_COEFFICIENT);
        int w = (int) (h * photo.getWidth()/((double) photo.getHeight()));
        photo = Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    public class MainMenuAdapter extends ArrayAdapter<MainMenuItem> {

        private ArrayList<MainMenuItem> items;

        public MainMenuAdapter(ArrayList<MainMenuItem> items, Context context, int itemResource) {
            super(context, itemResource);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inflatedView = View.inflate(this.getContext(), R.layout.main_menu_list_item, null);
            TextView title = (TextView) inflatedView.findViewById(R.id.txtTitle);
            title.setText(items.get(position).title());
            TextView description = (TextView) inflatedView.findViewById(R.id.txtDescription);
            description.setText(items.get(position).description());
            return inflatedView;
        }

        @Override
        public long getItemId(int position) {
            return this.items.get(position).hashCode();
        }

        @Override
        public int getPosition(MainMenuItem item) {
            return this.items.indexOf(item);
        }

        @Override
        public MainMenuItem getItem(int position) {
            return this.items.get(position);
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }

    public interface OnAuthorPromptConfirmedListener {
        void onAuthorPromptConfirmed();
    }

    /**
     * Implementations of this interface can receive events from
     * {@link com.telerik.widget.feedback.RadFeedback} that a procedure for sending feedback
     * has completed.
     */
    public interface OnSendFeedbackFinishedListener {
        void sendFeedbackFinished();
    }

    public interface OnItemsDeliveredCallback {
        void onItemsDelivered(Exception e, ArrayList<FeedbackItem> items);
    }

    public interface OnSubmitFeedbackFinishedCallback {
        void onSubmitFinished(Exception e);
    }

    class RequestResult {
        public ArrayList<FeedbackItem> results;
        public Exception exception;
    }
}
