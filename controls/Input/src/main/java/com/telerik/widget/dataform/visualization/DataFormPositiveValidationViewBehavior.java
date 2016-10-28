package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.ValidationInfo;

public class DataFormPositiveValidationViewBehavior extends DataFormValidationViewBehavior {
    protected Drawable validDrawable;
    protected Drawable validBackgroundDrawable;
    protected int validTextColor;

    public DataFormPositiveValidationViewBehavior(Context context) {
        super(context);

        this.validTextColor = context.getResources().getColor(R.color.data_form_valid_validation_color);
        this.validBackgroundDrawable = context.getResources().getDrawable(R.drawable.data_form_valid_background);
    }

    public Drawable getValidBackgroundDrawable() {
        return this.validBackgroundDrawable;
    }

    public void setValidBackgroundDrawable(int drawableId) {
        this.setValidBackgroundDrawable(this.getContext().getResources().getDrawable(drawableId));
    }

    public void setValidBackgroundDrawable(Drawable value) {
        this.validBackgroundDrawable = value;
    }

    public Drawable getValidDrawable() {
        return this.validDrawable;
    }

    public void setValidDrawable(int drawableId) {
        this.setValidDrawable(this.getContext().getResources().getDrawable(drawableId));
    }

    public void setValidDrawable(Drawable value) {
        this.validDrawable = value;
    }

    public int getValidTextColor() {
        return validTextColor;
    }

    public void setValidTextColor(int validTextColor) {
        this.validTextColor = validTextColor;
    }

    protected void showPositiveFeedback(ValidationInfo info) {
        this.messageView.setVisibility(View.VISIBLE);
        this.messageView.setTextColor(validTextColor);
        this.messageView.setText(info.message());

        if(this.validDrawable != null && this.validationIcon != null) {
            this.validationIcon.setVisibility(View.VISIBLE);
            this.validationIcon.setImageDrawable(this.validDrawable);
        }

        if(isChangeBackground()) {
            View editorParent = (View)this.editor.getEditorView().getParent();
            editorParent.setBackgroundDrawable(this.validBackgroundDrawable);
        } else {
            View editorView = this.editor.getEditorView();
            if (editorView.getBackground() != null) {
                Drawable currentDrawable = editorView.getBackground().getCurrent();
                currentDrawable.setColorFilter(this.validTextColor, PorterDuff.Mode.SRC_ATOP);
                editorView.setBackgroundDrawable(currentDrawable);
            }
        }
    }

    protected void updateUI(ValidationInfo info) {
        if(info.isValid()) {
            this.showPositiveFeedback(info);
        } else {
            this.showNegativeFeedback(info);
        }
    }
}
