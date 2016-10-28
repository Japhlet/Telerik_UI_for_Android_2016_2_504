package com.telerik.widget.chart.engine.databinding.datasources;

import android.graphics.Point;

import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.ObservableCollection;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartSeries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This is a base class for all data source classes used by the varieties of charts to hold
 * the data sources, their bindings and event listeners.
 */
public abstract class ChartSeriesDataSource implements PropertyChangeListener, CollectionChangeListener {

    protected Iterable itemsSource;
    protected ChartSeriesModel owner;
    protected ArrayList<DataPointBindingEntry> bindings;
    private LinkedList<DataBindingListener> propertyChangeListeners;
    protected boolean dataChangeScheduled;

    /**
     * Initializes a new instance of the {@link ChartSeriesDataSource} class.
     *
     * @param owner the chart series this data source belongs to.
     */
    public ChartSeriesDataSource(ChartSeriesModel owner) {

        if (owner == null) {
            throw new IllegalArgumentException("owner can not be null");
        }

        this.bindings = new ArrayList<DataPointBindingEntry>(8);
        this.propertyChangeListeners = new LinkedList<DataBindingListener>();

        this.owner = owner;
    }

    /**
     * Adds a given data binding listener to the current collection of listeners.
     *
     * @param listener the new listener to be added.
     */
    public void addBoundItemPropertyChangedListener(DataBindingListener listener) {
        this.propertyChangeListeners.add(listener);
    }

    /**
     * Removes a given data binding listener to the current collection of listeners.
     *
     * @param listener the listener to be removed.
     */
    public void removeBoundItemPropertyChangedListener(DataBindingListener listener) {
        this.propertyChangeListeners.remove(listener);
    }

    /**
     * Gets the current series owner for this instance.
     *
     * @return the current owner.
     */
    public ChartSeriesModel getOwner() {
        return this.owner;
    }

    /**
     * Gets the current items source.
     *
     * @return the current items source.
     */
    public Iterable getItemsSource() {
        return this.itemsSource;
    }

    /**
     * Sets the current items source.
     *
     * @param value the new items source.
     */
    public void setItemsSource(Iterable value) {
        this.rebind(true, value);
    }

    /**
     * Gets the binding entries corresponding to each data point in the data source.
     *
     * @return the current binding entries.
     */
    public ArrayList<DataPointBindingEntry> getBindings() {
        return this.bindings;
    }

    /**
     * Used to rebind the items source if necessary.
     *
     * @param itemsSourceChanged boolean stating whether the source of the items has changed.
     * @param newSource          the new source to be bound.
     */
    protected void rebind(boolean itemsSourceChanged, Iterable newSource) {
        this.unbind();

        if (itemsSourceChanged) {
            if(this.itemsSource instanceof ObservableCollection) {
                ((ObservableCollection)this.itemsSource).removeCollectionChangeListener(this);
            }

            this.itemsSource = newSource;

            if (newSource instanceof ObservableCollection) {
                ((ObservableCollection) newSource).addCollectionChangeListener(this);
            }
        }

        this.bind();

        for (DataBindingListener listener : this.propertyChangeListeners) {
            listener.onDataBindingComplete();
        }
    }

    /**
     * Returns a new instance of the {@link DataPoint} that is in the corresponding type for the
     * current chart.
     *
     * @return the new data point instance.
     */
    protected abstract DataPoint createDataPoint();

    /**
     * Used to handle the assignment of the passed double value to the passed data point.
     *
     * @param dataPoint data point to be assigned with the value.
     * @param value     value to be assigned to the data point.
     */
    protected abstract void processDouble(DataPoint dataPoint, double value);

    /**
     * Used to handle the assignment of the passed double values to the passed data point.
     *
     * @param dataPoint data point to be assigned with the value.
     * @param values    values to be assigned to the data point.
     */
    protected abstract void processDoubleArray(DataPoint dataPoint, double[] values);

    /**
     * Used to handle the assignment of the passed size value to the passed data point.
     *
     * @param dataPoint data point to be assigned with the value.
     * @param size      value to be assigned to the data point.
     */
    protected abstract void processSize(DataPoint dataPoint, RadSize size);

    /**
     * Used to handle the assignment of the passed point value to the passed data point.
     *
     * @param dataPoint data point to be assigned with the value.
     * @param point     value to be assigned to the data point.
     */
    protected abstract void processPoint(DataPoint dataPoint, Point point);

    /**
     * Binds the current data point corresponding to the type of chart with the given binding entry.
     *
     * @param binding binding to be updated.
     */
    protected abstract void initializeBinding(DataPointBindingEntry binding);

    /**
     * Binds the items source entries to the owners data points.
     */
    protected void bindCore() {
        for (Object item : this.itemsSource) {
            DataPoint point = this.generateDataPoint(item, -1);
            this.owner.dataPoints().add(point);
        }
    }

    /**
     * Generates a {@link DataPoint} instance of type corresponding to the type of the current chart
     * instance and handles the assignment from the data item to the newly generated data point.
     *
     * @param dataItem the data item to be used in generating the data point.
     * @param index    the index of the item to be used when adding it to the current bindings collection.
     * @return the newly generated data point.
     */
    protected DataPoint generateDataPoint(Object dataItem, int index) {
        DataPoint point = this.createDataPoint();
        if (dataItem == null) {
            return point;
        }

        if (dataItem instanceof Number) {
            this.processDouble(point, ((Number) dataItem).doubleValue());
        } else if (dataItem instanceof double[]) {
            this.processDoubleArray(point, (double[]) dataItem);
        } else if (dataItem instanceof RadSize) {
            this.processSize(point, (RadSize) dataItem);
        } else if (dataItem instanceof Point) {
            this.processPoint(point, (Point) dataItem);
        } else {
            DataPointBindingEntry binding = new DataPointBindingEntry(dataItem, point);

            this.initializeBinding(binding);

            if (index == -1) {
                this.bindings.add(binding);
            } else {
                this.bindings.add(index, binding);
            }

            this.hookPropertyChanged(dataItem);
        }

        point.setDataItem(dataItem);
        return point;
    }

    /**
     * Called when a property of a bound object changes.
     *
     * @param event the instance containing the change event data.
     */
    public void propertyChange(PropertyChangeEvent event) {
        DataPointBindingEntry binding = this.findBinding(event.getSource());
        if (binding != null) {
            this.updateBinding(binding);

            for (DataBindingListener listener : this.propertyChangeListeners) {
                listener.onBoundItemPropertyChanged(binding, event);
            }
        }
    }

    /**
     * Updates the binding.
     *
     * @param binding binding to be updated.
     */
    protected void updateBinding(DataPointBindingEntry binding) {
        this.initializeBinding(binding);
    }

    /**
     * Unbinds the current items source.
     */
    protected void unbind() {
        if (this.itemsSource != null) {
            for (Object item : this.itemsSource) {
                this.unhookPropertyChanged(item);
            }
        }

        this.bindings.clear();
        this.owner.dataPoints().clear();
    }

    /**
     * Binds the current items source.
     */
    private void bind() {
        if (this.itemsSource == null)
            return;

        this.bindCore();
    }

    private void hookPropertyChanged(Object item) {
        if (item instanceof PropertyChangeSupport) {
            PropertyChangeSupport propChanged = (PropertyChangeSupport) item;
            propChanged.addPropertyChangeListener(this);
        }
    }

    private void unhookPropertyChanged(Object item) {
        if (item instanceof PropertyChangeSupport) {
            PropertyChangeSupport propChanged = (PropertyChangeSupport) item;
            propChanged.removePropertyChangeListener(this);
        }
    }

    private DataPointBindingEntry findBinding(Object changedInstance) {
        for (DataPointBindingEntry binding : this.bindings) {
            if (changedInstance == binding.getDataItem()) {
                return binding;
            }
        }

        return null;
    }

    public void collectionChanged(CollectionChangedEvent e) {
        switch (e.action()) {
            case RESET:
                this.rebind(false, null);
                break;
            case ADD:
                this.handleItemAdd(e);
                break;
            case REMOVE:
                this.handleItemRemove(e);
                break;
            case REPLACE:
                this.handleItemReplace(e);
                break;
            case MOVE:
                this.handleItemMove(e);
                break;
        }

        if (!this.dataChangeScheduled)
            ((ChartSeries) this.owner.getPresenter()).getChart().requestInvalidateArrange();
    }

    private void handleItemAdd(CollectionChangedEvent e) {
        this.performAdd(e.getNewItems(), e.getNewIndex());
    }

    private void handleItemRemove(CollectionChangedEvent e) {
        this.performRemove(e.getOldItems(), e.getOldIndex());
    }

    private void handleItemReplace(CollectionChangedEvent e) {
        this.performRemove(e.getOldItems(), e.getNewIndex());
        this.performAdd(e.getNewItems(), e.getNewIndex());
    }

    private void handleItemMove(CollectionChangedEvent e) {
        this.performRemove(e.getOldItems(), e.getOldIndex());
        this.performAdd(e.getNewItems(), e.getNewIndex());
    }

    private void performAdd(Iterable newItems, int newItemIndex) {
        for(Object newDataItem : newItems) {
            DataPoint point = this.generateDataPoint(newDataItem, newItemIndex);
            this.owner.dataPoints().add(newItemIndex, point);
        }
    }

    private void performRemove(Iterable removedItems, int removedItemIndex) {
        for(Object removedDataItem : removedItems) {
            this.unhookPropertyChanged(removedDataItem);

            // try to remove an existing binding
            if (removedItemIndex >= 0 && removedItemIndex < this.bindings.size()) {
                DataPointBindingEntry binding = this.bindings.get(removedItemIndex);
                if (binding.getDataItem() == removedDataItem) {
                    this.bindings.remove(removedItemIndex);
                }
            }

            this.owner.dataPoints().remove(removedItemIndex);
        }
    }
}
