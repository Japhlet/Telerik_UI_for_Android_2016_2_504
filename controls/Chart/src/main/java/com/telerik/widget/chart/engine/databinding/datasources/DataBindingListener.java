package com.telerik.widget.chart.engine.databinding.datasources;

import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;

import java.beans.PropertyChangeEvent;

/**
 * Interface for creating anonymous classes that serve as delegates which handle events associated
 * with data binding completion and certain changes in bound item's properties.
 */
public interface DataBindingListener {
    /**
     * Called after binding data source items.
     */
    void onDataBindingComplete();

    /**
     * Called when a change was made to an item that it currently bound.
     *
     * @param entry the item that was changed.
     * @param event the info about the change that was made.
     */
    void onBoundItemPropertyChanged(DataPointBindingEntry entry, PropertyChangeEvent event);
}
