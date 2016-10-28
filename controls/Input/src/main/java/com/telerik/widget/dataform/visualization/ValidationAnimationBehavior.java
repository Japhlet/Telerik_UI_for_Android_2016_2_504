package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;

import com.telerik.widget.dataform.engine.ValidationInfo;

public class ValidationAnimationBehavior extends DataFormValidationViewBehavior {
    public ValidationAnimationBehavior(Context context) {
        super(context);
    }

    @Override
    protected void showNegativeFeedback(ValidationInfo info) {
        super.showNegativeFeedback(info);

        this.animateEditor(this.editor.rootLayout());
    }

    protected void animateEditor(final View editorView) {
        ViewPropertyAnimatorCompat animator = ViewCompat.animate(editorView);
        animator.translationX(40);
        animator.setDuration(50);
        animator.withEndAction(new Runnable() {
            @Override
            public void run() {
                ViewCompat.animate(editorView).translationX(-40).setDuration(50).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ViewCompat.animate(editorView).translationX(40).setDuration(50).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ViewCompat.animate(editorView).translationX(0).setDuration(50);
                            }
                        }).start();
                    }
                }).start();
            }
        }).start();
    }
}
