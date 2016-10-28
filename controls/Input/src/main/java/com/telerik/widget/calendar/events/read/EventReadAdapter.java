package com.telerik.widget.calendar.events.read;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;

import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.events.Event;

import java.security.AccessControlException;
import java.util.List;

/**
 * Events adapter that provides the functionality for reading the events from the calendar provider of the current phone device.
 */
public class EventReadAdapter extends EventAsyncAdapter {

    private static final int EVENT_CALENDAR_ID_INDEX = 0;
    private static final int EVENT_TITLE_INDEX = 1;
    private static final int EVENT_START_INDEX = 2;
    private static final int EVENT_END_INDEX = 3;
    private static final int EVENT_ALL_DAY_INDEX = 4;
    private static final int EVENT_RRULE_INDEX = 5;
    private static final int EVENT_DURATION_INDEX = 6;
    private static final int EVENT_COLOR_INDEX = 7;

    private static boolean preHoneyComb;

    /**
     * This projection works on older devices.
     */
    private static final String[] NEW_PROJECTION = {
            "calendar_id",
            "title",
            "dtstart",
            "dtend",
            "allDay",
            "rrule",
            "duration",
            "displayColor"
    };

    /**
     * This projection works on newer devices.
     */
    private static final String[] OLD_PROJECTION = {
            "calendar_id",
            "title",
            "dtstart",
            "dtend",
            "allDay",
            "rrule",
            "duration",
    };

    private static String[] eventProjection;

    // Used to set api version specific settings.
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            eventProjection = NEW_PROJECTION;
            preHoneyComb = false;
        } else {
            eventProjection = OLD_PROJECTION;
            preHoneyComb = true;
        }
    }

    private static final Uri DEFAULT_EVENTS_URI = Uri.parse("content://com.android.calendar/events");

    private EventQueryToken eventsQueryToken;
    private Uri eventsUri = DEFAULT_EVENTS_URI;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.read.EventReadAdapter} class.
     *
     * @param owner the owner of the current adapter instance.
     */
    public EventReadAdapter(RadCalendarView owner) {
        this(owner, null);
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.read.EventReadAdapter} class.
     *
     * @param owner  the owner of the current adapter instance.
     * @param events list of events that are not red from the calendar provider.
     */
    public EventReadAdapter(RadCalendarView owner, List<Event> events) {
        super(owner, events);

        this.eventsQueryToken = EventQueryToken.getDefaultCalendar();
    }

    /**
     * Gets the query tokens that will be used in the query for obtaining the events from the calendar provider.
     *
     * @return the current query tokens.
     */
    public EventQueryToken getEventsQueryToken() {
        return eventsQueryToken;
    }

    /**
     * Sets the query tokens that will be used in the query for obtaining the events from the calendar provider.
     *
     * @param eventsQueryToken the new query tokens.
     */
    public void setEventsQueryToken(EventQueryToken eventsQueryToken) {
        this.eventsQueryToken = eventsQueryToken;
    }

    /**
     * Gets the current uri that will be used to query the calendar provider. By default it will be set accordingly to the build version, no need to
     * explicitly check that.
     *
     * @return the current calendar provider uri.
     */
    public Uri getEventsUri() {
        return eventsUri;
    }

    /**
     * Sets the current uri that will be used to query the calendar provider. By default it will be set accordingly to the build version, no need to
     * explicitly check that.
     *
     * @param eventsUri the new calendar provider uri.
     */
    public void setEventsUri(Uri eventsUri) {
        this.eventsUri = eventsUri;
    }

    @Override
    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    public void readEventsAsync() {
        super.readEventsAsync();
    }

    @Override
    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    protected void loadEventsAsync(List<Event> events) {
        Cursor cursor = owner.getContext().getContentResolver().query(
                this.eventsUri,
                eventProjection,
                this.eventsQueryToken.buildSelection(),
                this.eventsQueryToken.selectionArgs,
                null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        int length = cursor.getCount();

        for (int i = 0; i < length; i++) {
            Event ce = constructEventFromCursor(cursor);
            if (ce != null) {
                if (ce.getTitle() != null && ce.getTitle().length() > 0) {
                    events.add(ce);
                }
            }

            cursor.moveToNext();
        }

        cursor.close();
    }

    private Event constructEventFromCursor(Cursor cursor) {
        Event event;
        if (cursor.getString(EVENT_RRULE_INDEX) == null) {
            if (cursor.getString(EVENT_TITLE_INDEX) != null && cursor.getLong(EVENT_END_INDEX) != 0)
                event = new Event(cursor.getString(EVENT_TITLE_INDEX), cursor.getLong(EVENT_START_INDEX), cursor.getLong(EVENT_END_INDEX));
            else
                return null;
        } else {
            event = new RecurringEvent(
                    cursor.getString(EVENT_TITLE_INDEX),
                    cursor.getLong(EVENT_START_INDEX),
                    cursor.getString(EVENT_DURATION_INDEX),
                    cursor.getString(EVENT_RRULE_INDEX)
            );
        }

        event.setCalendarId(cursor.getInt(EVENT_CALENDAR_ID_INDEX));

        if (!preHoneyComb)
            event.setEventColor(cursor.getInt(EVENT_COLOR_INDEX));

        event.setAllDay(cursor.getInt(EVENT_ALL_DAY_INDEX) > 0);
        return event;
    }

    /**
     * Used to retrieve a collection of all the available calendars for the current phone device.
     *
     * @param context the current context.
     * @return the collection holding the available calendars.
     */
    public static CalendarInfo[] getAllCalendars(Context context) {
        CalendarInfo[] calendars = null;
        try {
            String projection[] = {"_id", "ownerAccount"};
            Cursor cursor;
            cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), projection, null, null, null);
            if (cursor == null) {
                cursor = context.getContentResolver().query(Uri.parse("content://calendar/calendars"), projection, null, null, null);
                if (cursor == null)
                    return new CalendarInfo[0];
            }

            if (cursor.moveToFirst()) {
                calendars = new CalendarInfo[cursor.getCount()];
                int count = 0;
                do {
                    CalendarInfo calendar = new CalendarInfo();
                    calendar.id = cursor.getString(0);
                    calendar.ownerAccount = cursor.getString(1);

                    calendars[count++] = calendar;
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (SecurityException exception) {
            exception.printStackTrace();
            throw new AccessControlException("To get the current list of calendars you need the android.permission.READ_CALENDAR permission to be available in the manifest of the application!");
        }

        return calendars;
    }

    /**
     * Used to retrieve a collection of all the available calendars for the current phone device asynchronously. Thread safe operation - the result will be available trough the provided {@link GenericResultCallback} callback.
     *
     * @param context  the current context.
     * @param callback the callback used to carry the result to te caller.
     */
    public static void getAllCalendarsAsync(final Context context, final GenericResultCallback<CalendarInfo[]> callback) {
        new AsyncTask<Void, Void, CalendarInfo[]>() {

            @Override
            protected CalendarInfo[] doInBackground(Void... voids) {
                return getAllCalendars(context);
            }

            @Override
            protected void onPostExecute(CalendarInfo[] result) {
                super.onPostExecute(result);
                callback.onResult(result);
            }
        }.execute();
    }

    /**
     * Gets the currently logged google account.
     *
     * @param context the current context.
     * @return the name of the current account.
     */
    @RequiresPermission(Manifest.permission.GET_ACCOUNTS)
    public static String getCurrentUser(Context context) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return  ((UserManager) context.getSystemService(Context.USER_SERVICE)).getUserName();*/

        try {
            for (Account account : ((AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE)).getAccounts()) {
                if (account.type.equalsIgnoreCase("com.google")) {
                    return account.name;
                }
            }
        } catch (SecurityException exception) {
            exception.printStackTrace();
            throw new AccessControlException("To get the current user you need the android.permission.GET_ACCOUNTS permission in the manifest of the application!");
        }

        return null;
    }

    /**
     * Gets the currently logged google account asynchronously. Thread safe operation - the result will be available trough the provided {@link GenericResultCallback} callback.
     *
     * @param context  the current context.
     * @param callback the callback used to carry the result to the caller.
     */
    @RequiresPermission(Manifest.permission.GET_ACCOUNTS)
    @SuppressWarnings("MissingPermission")
    public static void getCurrentUserAsync(final Context context, final GenericResultCallback<String> callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                int hasAccountsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);
                if (hasAccountsPermission != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                return getCurrentUser(context);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                callback.onResult(result);
            }
        }.execute();
    }

    /**
     * Simple class for holding the minimum information about a calendar.
     */
    public static class CalendarInfo {
        public String id;
        public String ownerAccount;
    }

    private static boolean checkPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
