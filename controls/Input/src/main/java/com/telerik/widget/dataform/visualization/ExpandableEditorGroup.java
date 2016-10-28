package com.telerik.widget.dataform.visualization;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.widget.calendar.R;

public class ExpandableEditorGroup extends EditorGroup implements View.OnClickListener {
    private boolean isExpanded;
    private boolean expandOnce;

    public ExpandableEditorGroup(Context context, String groupName) {
        this(context, groupName, R.layout.data_form_expandable_group_layout, false);
    }

    public ExpandableEditorGroup(Context context, String groupName, boolean expandOnce) {
        this(context, groupName, R.layout.data_form_expandable_group_layout, expandOnce);
    }

    public ExpandableEditorGroup(Context context, String groupName, int layoutId, boolean expandOnce) {
        super(context, groupName, layoutId);

        View expandButton = rootLayout().findViewById(R.id.data_form_expandable_group_expand_button);
        if(expandOnce) {
            expandButton.setOnClickListener(this);
        } else {
            ViewGroup ownerLayout = (ViewGroup) expandButton.getParent();
            ownerLayout.setOnClickListener(this);
        }

        this.expandOnce = expandOnce;
        isExpanded = !expandOnce;

        if(!isExpanded) {
            this.rootLayout().findViewById(R.id.data_form_editor_group_container).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        this.setIsExpanded(!this.getIsExpanded());
    }

    public boolean getIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean value) {
        this.isExpanded = value;

        if(value) {
            expandEditors();
        } else {
            collapseEditors();
        }
    }

    protected void expandEditors() {
        this.rootLayout().findViewById(R.id.data_form_editor_group_container).setVisibility(View.VISIBLE);

        View expandButton = rootLayout().findViewById(R.id.data_form_expandable_group_expand_button);
        if(expandOnce) {
            expandButton.setVisibility(View.GONE);
        } else {
            ViewCompat.animate(expandButton).setDuration(150).rotationX(0).start();
        }
    }

    protected void collapseEditors() {
        this.rootLayout().findViewById(R.id.data_form_editor_group_container).setVisibility(View.GONE);
        View expandButton = rootLayout().findViewById(R.id.data_form_expandable_group_expand_button);
        ViewCompat.animate(expandButton).setDuration(150).rotationX(-180).start();
    }
}
