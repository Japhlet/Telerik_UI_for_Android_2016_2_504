package com.telerik.android.primitives.widget.sidedrawer.contents;

import android.content.Intent;

public class NavigationItem {
    private String text;
    private Intent intent;
    private Class activityClass;

    public NavigationItem(Class activityClass) {
        if(activityClass == null) {
            throw new IllegalArgumentException("activityClass cannot be null.");
        }

        this.activityClass = activityClass;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public String getText() {
        if(text == null) {
            return activityClass.getSimpleName();
        }

        return text;
    }

    public void setText(String value) {
        text = value;
    }

    public void setIntent(Intent value) {
        if(value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }

        intent = value;

        try {
            activityClass = Class.forName(value.getComponent().getClassName());
        } catch (ClassNotFoundException ex) {
            throw new Error(ex);
        }
    }

    public Intent getIntent() {
        return intent;
    }
}
