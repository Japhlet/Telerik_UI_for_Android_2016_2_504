package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.ValidationInfo;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class DataFormValidationViewBehavior implements EntityPropertyEditor.OnValidationEventListener {
    private Context context;
    protected EntityPropertyEditor editor;
    private boolean negativeFeedbackVisible = false;

    private boolean changeBackground = false;

    protected TextView messageView;
    protected ImageView validationIcon;

    protected Drawable invalidDrawable;
    protected Drawable invalidBackgroundDrawable;
    protected int invalidTextColor;

    private Drawable cleanDrawable;
    private Drawable filteredDrawable;

    public DataFormValidationViewBehavior(Context context) {
        this.context = context;

        this.invalidTextColor = context.getResources().getColor(R.color.data_form_invalid_validation_color);
        this.invalidBackgroundDrawable = context.getResources().getDrawable(R.drawable.data_form_invalid_background);
    }

    public Context getContext() {
        return context;
    }

    public EntityPropertyEditor getEditor() {
        return editor;
    }

    public void setEditor(EntityPropertyEditor value) {
        if(this.editor != null) {
            this.editor.removeValidationListener(this);
        }

        this.messageView = null;
        this.validationIcon = null;
        this.editor = value;

        if(this.editor != null) {
            this.editor.addValidationListener(this);
            this.messageView = findMessageView(this.editor);
            this.validationIcon = findValidationIcon(this.editor);
            if(this.validationIcon != null) {
                this.validationIcon.setVisibility(View.GONE);
            }
        }
    }

    public void reset() {
        this.messageView = null;
        this.validationIcon = null;

        if(this.editor != null) {
            this.messageView = findMessageView(this.editor);
            this.validationIcon = findValidationIcon(this.editor);
            if(this.validationIcon != null) {
                this.validationIcon.setVisibility(View.GONE);
            }
        }
    }

    public boolean isChangeBackground() {
        return changeBackground;
    }

    public void setChangeBackground(boolean changeBackground) {
        this.changeBackground = changeBackground;
    }

    public TextView messageView() {
        return messageView;
    }

    public ImageView validationIcon() {
        return validationIcon;
    }

    protected TextView findMessageView(EntityPropertyEditor editor) {
        return (TextView)editor.rootLayout().findViewById(R.id.data_form_validation_message_view);
    }

    protected ImageView findValidationIcon(EntityPropertyEditor editor) {
        return (ImageView)editor.rootLayout().findViewById(R.id.data_form_validation_icon);
    }

    public Drawable getInvalidBackgroundDrawable() {
        return this.invalidBackgroundDrawable;
    }

    public void setInvalidBackgroundDrawable(int drawableId) {
        this.setInvalidBackgroundDrawable(this.context.getResources().getDrawable(drawableId));
    }

    public void setInvalidBackgroundDrawable(Drawable value) {
        this.invalidBackgroundDrawable = value;
    }

    public void setInvalidDrawable(int drawableId) {
        this.setInvalidDrawable(this.context.getResources().getDrawable(drawableId));
    }

    public void setInvalidDrawable(Drawable value) {
        this.invalidDrawable = value;
    }

    public Drawable getInvalidDrawable() {
        return this.invalidDrawable;
    }

    public void setInvalidTextColor(int textColor) {
        this.invalidTextColor = textColor;
    }

    public int getInvalidTextColor() {
        return this.invalidTextColor;
    }

    protected void updateUI(ValidationInfo info) {
        if(info.isValid()) {
           this.hideNegativeFeedback();
        } else {
            this.showNegativeFeedback(info);
        }
    }

    protected void showNegativeFeedback(ValidationInfo info) {
        if(negativeFeedbackVisible) {
            return;
        }
        negativeFeedbackVisible = true;
        this.messageView.setVisibility(View.VISIBLE);
        this.messageView.setTextColor(invalidTextColor);
        this.messageView.setText(info.message());

        if(this.invalidDrawable != null && this.validationIcon != null) {
            this.validationIcon.setVisibility(View.VISIBLE);
            this.validationIcon.setImageDrawable(this.invalidDrawable);
        }
        if(changeBackground) {
            View editorParent = (View)this.editor.getEditorView().getParent();
            editorParent.setBackgroundDrawable(this.invalidBackgroundDrawable);
        } else {
            View editorView = this.editor.getEditorView();
            if (editorView.getBackground() != null) {
                cleanDrawable = editorView.getBackground();
                filteredDrawable = cleanDrawable.getConstantState().newDrawable();
                filteredDrawable.setColorFilter(this.invalidTextColor, PorterDuff.Mode.SRC_ATOP);
                editorView.setBackgroundDrawable(filteredDrawable);
            }
        }
    }

    protected void hideNegativeFeedback() {
        if(!negativeFeedbackVisible) {
            return;
        }
        negativeFeedbackVisible = false;
        this.messageView.setVisibility(View.GONE);
        if(this.validationIcon != null) {
            this.validationIcon.setVisibility(View.GONE);
        }
        if(changeBackground) {
            View editorParent = (View)this.editor.getEditorView().getParent();
            editorParent.setBackgroundDrawable(null);
        } else {
            View editorView = this.editor.getEditorView();
            if (cleanDrawable != null && filteredDrawable != null) {
                filteredDrawable.clearColorFilter();
                editorView.setBackgroundDrawable(cleanDrawable);
            }
        }
    }

    @Override
    public void onValidationEvent(EntityPropertyEditor editor, ValidationInfo info) {
        this.updateUI(info);
    }
}
