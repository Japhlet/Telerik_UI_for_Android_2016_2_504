package com.telerik.widget.feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

/**
 * Created by ginev on 09/05/2014.
 */
public class FeedbackIndicator extends FrameLayout implements View.OnClickListener {

    private String feedback;
    private AlertDialog alert;

    public FeedbackIndicator(Context context) {
        this(context, null);
    }

    public FeedbackIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedbackIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.feedback_indicator, this);
        this.setOnClickListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        SendFeedbackActivity activity = (SendFeedbackActivity)this.getContext();
        activity.startInputForIndicator(this);
    }

    /**
     * Returns the feedback submitted for the current {@link com.telerik.widget.feedback.FeedbackIndicator}.
     *
     * @return a String representing the feedback.
     */
    public String getFeedback() {
        return this.feedback;
    }

    public void setFeedback(String feedback){
        this.feedback = feedback;
    }

    @Override
    public void onClick(View v) {
        SendFeedbackActivity activity = (SendFeedbackActivity)this.getContext();
        activity.startInputForIndicator(this);
    }
}
