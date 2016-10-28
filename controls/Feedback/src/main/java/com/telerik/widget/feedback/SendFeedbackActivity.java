package com.telerik.widget.feedback;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class SendFeedbackActivity extends ActionBarActivity implements View.OnTouchListener, CommentEditText.OnKeyPreImeListener {

    private ImageView screenShotImage;
    private FrameLayout progressBar;
    private Bitmap sourceImage;
    private RelativeLayout indicatorContainer;
    private Button btnDone;
    private CommentEditText txtEditComment;
    private LinearLayout editCommentPanel;
    private boolean touchDown;
    FeedbackIndicator activeIndicator;
    private RadFeedback feedback;
    private float historicalX;
    private float historicalY;
    private int lastOrientation;


    public SendFeedbackActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_send_feedback);
        this.lastOrientation = this.getResources().getConfiguration().orientation;
        if (savedInstanceState == null) {
            this.lockScreenOrientation();
        }
        this.feedback = RadFeedback.instance();
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayUseLogoEnabled(true);
        this.getSupportActionBar().setLogo(new ColorDrawable());

        this.indicatorContainer = (RelativeLayout) this.findViewById(R.id.indicatorContainer);
        this.txtEditComment = (CommentEditText) this.findViewById(R.id.txtEditComment);
        this.progressBar = (FrameLayout) this.findViewById(R.id.progressBar);
        this.txtEditComment.setOnKeyPreImeListener(this);
        this.txtEditComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    confirmActiveIndicator();
                    return true;
                }
                return false;
            }
        });
        this.btnDone = (Button) this.findViewById(R.id.btnCommentDone);
        this.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmActiveIndicator();
            }
        });

        this.editCommentPanel = (LinearLayout) this.findViewById(R.id.editCommentPanel);
        this.screenShotImage = (ImageView) this.findViewById(R.id.screenshotImage);
        byte[] imageBytes = this.getIntent().getByteArrayExtra("image");
        this.sourceImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        this.screenShotImage.setImageBitmap(this.sourceImage);
        this.screenShotImage.setOnTouchListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("orientation", this.lastOrientation);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.lastOrientation = this.getResources().getConfiguration().orientation;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.lastOrientation = savedInstanceState.getInt("orientation");
        this.lockScreenOrientation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_feedback_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
            return true;
        } else if (id == R.id.action_send) {
            if (this.feedback.shouldShowPrefPrompt()) {
                this.feedback.showAuthorNamePrompt(this, new RadFeedback.OnAuthorPromptConfirmedListener() {
                    @Override
                    public void onAuthorPromptConfirmed() {
                        sendFeedback();
                    }
                });
            } else {
                this.sendFeedback();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RadFeedback.instance().onSendFeedbackFinished();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.progressBar.getVisibility() == View.VISIBLE) {
            return false;
        }
        if (event.getPointerCount() > 1) {
            this.touchDown = false;
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.touchDown = true;
            this.historicalX = event.getX();
            this.historicalY = event.getY();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(this.historicalX - event.getX()) > 10 || Math.abs(this.historicalY - event.getY()) > 10) {
                this.touchDown = false;
                return false;
            }
        }

        if (this.touchDown && event.getAction() == MotionEvent.ACTION_UP) {
            if (this.activeIndicator != null) {
                this.confirmActiveIndicator();
            } else {
                MotionEvent.PointerCoords coordinates = new MotionEvent.PointerCoords();
                event.getPointerCoords(0, coordinates);
                this.positionIndicatorAt(coordinates);
            }
            this.touchDown = false;

        }
        return false;
    }

    private void positionIndicatorAt(MotionEvent.PointerCoords coords) {
        FeedbackIndicator indicator = new FeedbackIndicator(this);
        this.indicatorContainer.addView(indicator);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) this.getResources().getDimension(R.dimen.feedback_indicator_size),
                (int) this.getResources().getDimension(R.dimen.feedback_indicator_size));
        params.topMargin = (int) coords.y - params.height / 2;
        params.leftMargin = (int) coords.x - params.width / 2;
        indicator.setLayoutParams(params);
    }

    private void lockScreenOrientation() {
        switch (getResources().getConfiguration().orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if (rotation == android.view.Surface.ROTATION_90 || rotation == android.view.Surface.ROTATION_180) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
                break;

            case Configuration.ORIENTATION_LANDSCAPE:
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    if (rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    }
                }
                break;
        }
    }

    private void confirmActiveIndicator() {
        String commentText = txtEditComment.getText().toString().trim();
        activeIndicator.setFeedback(commentText);

        if (commentText.isEmpty()) {
            indicatorContainer.removeView(activeIndicator);
        }

        editCommentPanel.setVisibility(View.GONE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtEditComment.getWindowToken(), 0);
        activeIndicator = null;
    }

    private void sendFeedback() {

        ArrayList<FeedbackItem> itemsToSend = new ArrayList<FeedbackItem>();
        for (int i = 0; i < this.indicatorContainer.getChildCount(); i++) {
            FeedbackIndicator currentIndicator = (FeedbackIndicator) this.indicatorContainer.getChildAt(i);
            FeedbackItem item = new FeedbackItem();
            Bitmap resultingImage = this.getFeedbackImageFromIndicator(currentIndicator);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resultingImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            String base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
            String contentType = "image/png";
            Image feedbackImage = new Image();
            feedbackImage.setContentType(contentType);
            feedbackImage.setbase64(base64);
            item.setImage(feedbackImage);
            item.setText(currentIndicator.getFeedback());
            item.setAuthor(feedback.getCurrentAuthorName());
            itemsToSend.add(item);
        }

        if (itemsToSend.size() == 0) {
            Toast.makeText(SendFeedbackActivity.this, getString(R.string.feedback_no_data_ready), Toast.LENGTH_SHORT).show();
            return;
        } else {
            //Toast.makeText(SendFeedbackActivity.this, "Sending feedback...", Toast.LENGTH_SHORT).show();
            toggleProgressBar(true);
            RadFeedback.instance().submitFeedback(itemsToSend, null, new RadFeedback.OnSubmitFeedbackFinishedCallback() {
                @Override
                public void onSubmitFinished(Exception e) {
                    if (e != null) {
                        toggleProgressBar(false);
                        Toast.makeText(SendFeedbackActivity.this, getString(R.string.feedback_not_sent), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SendFeedbackActivity.this, getString(R.string.feedback_sent), Toast.LENGTH_SHORT).show();
                        SendFeedbackActivity.this.finish();
                    }
                }
            });
        }
    }

    void startInputForIndicator(FeedbackIndicator indicator) {
        if (this.activeIndicator != null && this.activeIndicator != indicator) {
            if (this.activeIndicator.getFeedback() == null || this.activeIndicator.getFeedback().isEmpty()) {
                this.indicatorContainer.removeView(this.activeIndicator);
            }
        }

        if (this.activeIndicator != indicator) {
            this.activeIndicator = indicator;
            this.txtEditComment.setText(indicator.getFeedback());
            this.editCommentPanel.setVisibility(View.VISIBLE);
            this.txtEditComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this.txtEditComment, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void toggleProgressBar(boolean visible) {
        if (visible) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else {
            this.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private Bitmap getFeedbackImageFromIndicator(FeedbackIndicator indicator) {
        Bitmap resultingImage = Bitmap.createBitmap(this.sourceImage.getWidth(), this.sourceImage.getHeight(), this.sourceImage.getConfig());
        Canvas drawingBoard = new Canvas(resultingImage);
        Bitmap indicatorImage = Bitmap.createBitmap(indicator.getWidth(), indicator.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas indicatorBoard = new Canvas(indicatorImage);
        indicator.draw(indicatorBoard);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
        Matrix displayMatrix = this.screenShotImage.getImageMatrix();
        float[] f = new float[9];
        displayMatrix.getValues(f);
        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        float horizontalDiff = this.screenShotImage.getWidth() - (resultingImage.getWidth() * scaleX);
        float verticalDiff = this.screenShotImage.getHeight() - (resultingImage.getHeight() * scaleY);

        float originalLeftMargin = (params.leftMargin - horizontalDiff / 2) / (resultingImage.getWidth() * scaleX);
        float originalTopMargin = (params.topMargin - verticalDiff / 2) / (resultingImage.getHeight() * scaleY);

        float leftMargin = (int) (originalLeftMargin * resultingImage.getWidth());
        float topMargin = (int) (originalTopMargin * resultingImage.getHeight());

        drawingBoard.drawBitmap(this.sourceImage, 0, 0, null);
        drawingBoard.drawBitmap(indicatorImage, leftMargin, topMargin, null);
        return resultingImage;
    }

    @Override
    public void onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.editCommentPanel.getVisibility() == View.VISIBLE) {
                this.editCommentPanel.setVisibility(View.GONE);

                if (this.activeIndicator.getFeedback() == null || this.activeIndicator.getFeedback().isEmpty()) {
                    this.indicatorContainer.removeView(this.activeIndicator);
                }
                this.activeIndicator = null;
            }
        }
    }
}
