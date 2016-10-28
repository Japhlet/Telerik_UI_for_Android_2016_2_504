package com.telerik.widget.primitives.legend;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A class that contains legend information.
 */
public class LegendItem {

    private String title;
    private int fillColor;
    private int strokeColor;
    private PropertyChangeListener listener;

    /**
     * Gets the current {@link java.beans.PropertyChangeListener}.
     */
    public PropertyChangeListener getPropertyChangeListener() {
        return this.listener;
    }

    /**
     * Sets the current {@link java.beans.PropertyChangeListener}.
     */
    public void setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }

    /**
     * Gets the legend item title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the legend item title.
     */
    public void setTitle(String value) {
        Object right = this.title;
        if (value == right) {
            return;
        }

        if (value != null && value.equals(this.title)) {
            return;
        }

        String oldValue = this.title;
        this.title = value;
        this.onPropertyChanged("title", value, oldValue);
    }

    /**
     * Gets the legend item fill color.
     */
    public int getFillColor() {
        return this.fillColor;
    }

    /**
     * Sets the legend item fill color.
     */
    public void setFillColor(int value) {
        if (value == this.fillColor) {
            return;
        }

        int oldValue = this.fillColor;
        this.fillColor = value;

        this.onPropertyChanged("fillColor", value, oldValue);
    }

    /**
     * Gets the legend item stroke color.
     */
    public int getStrokeColor() {
        return this.strokeColor;
    }

    /**
     * Sets the legend item stroke color.
     */
    public void setStrokeColor(int value) {
        if (value == this.strokeColor) {
            return;
        }

        int oldValue = this.strokeColor;
        this.strokeColor = value;

        this.onPropertyChanged("strokeColor", value, oldValue);
    }

    private void onPropertyChanged(String propertyName, Object newValue, Object oldValue) {
        if (this.listener == null) {
            return;
        }

        this.listener.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
    }
}
