package com.telerik.widget.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by ginev on 20/05/2014.
 */
public class CommentEditText extends EditText {

    private OnKeyPreImeListener keyPreImeListener;

    public CommentEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (this.keyPreImeListener != null) {
            this.keyPreImeListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyPreImeListener(OnKeyPreImeListener listener) {
        this.keyPreImeListener = listener;
    }

    public interface OnKeyPreImeListener {
        void onKeyPreIme(int keyCode, KeyEvent event);
    }
}
