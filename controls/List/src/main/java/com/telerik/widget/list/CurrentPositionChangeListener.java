package com.telerik.widget.list;

/**
 * Interface definition for a callback to be invoked when the current position is changed.
 */
public interface CurrentPositionChangeListener {

    /**
     * Called when the current position changes.
     */
    void onCurrentPositionChanged(int oldPosition, int newPosition);
}