package com.telerik.widget.feedback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Represents an item within the {@link com.telerik.widget.feedback.RadFeedback}
 * main menu window.
 */
public class MainMenuItem {

    private String title;
    private String description;
    private Intent intent;
    private InitAction initAction;

    public MainMenuItem(String title, String description, Intent intent){
        this.title = title;
        this.description = description;
        this.intent = intent;
    }

    /**
     * Sets an object implementing the {@link InitAction} interface
     * which contains logic that will be executed before the intent
     * associated with this item is passed to the new activity.
     * @param action the {@link InitAction} implementation.
     */
    public void setInitAction(InitAction action){
        this.initAction = action;
    }

    /**
     * Gets an object implementing the {@link InitAction} interface
     * which contains logic that will be executed before the intent
     * associated with this item is passed to the new activity.
     */
    public InitAction getInitAction(){
        return this.initAction;
    }

    /**
     * Gets the title for the current item.
     * @return a string representing the title.
     */
    public String title(){
        return this.title;
    }

    /**
     * Gets the description of the current item.
     * @return a string representing the description.
     */
    public String description(){
        return this.description;
    }

    /**
     * Returns the {@link Intent} associated with this menu item.
     * @return the associated intent.
     */
    public Intent intent(){
        return this.intent;
    }

    interface InitAction{
        void init(Context context);
    }
}
