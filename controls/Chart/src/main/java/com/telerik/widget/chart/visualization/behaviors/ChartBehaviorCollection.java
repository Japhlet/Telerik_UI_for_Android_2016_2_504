package com.telerik.widget.chart.visualization.behaviors;

import com.telerik.android.common.CollectionChangeAction;
import com.telerik.android.common.ObservableCollection;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

/**
 * This collection contains the behaviors for RadChart.
 */
public class ChartBehaviorCollection extends ObservableCollection<ChartBehavior> {

    private RadChartViewBase owner;

    /**
     * Creates a new instance of the {@link ChartBehaviorCollection} with a specified chart owner.
     *
     * @param owner the chart owning the collection.
     */
    public ChartBehaviorCollection(RadChartViewBase owner) {
        if (owner == null) {
            throw new IllegalArgumentException("owner cannot be null");
        }

        this.owner = owner;
    }

    private void verifyIndex(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IllegalArgumentException("index cannot be negative or greater than the size of the current collection");
        }
    }

    @Override
    public boolean add(ChartBehavior item) {
        this.add(this.size(), item);
        return true;
    }

    @Override
    public void add(int index, ChartBehavior item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }

        if (index < 0 || index > this.size()) {
            throw new IllegalArgumentException("index cannot be negative or greater than the size of this instance");
        }

        this.owner.validateBehaviourSupport(item);
        this.owner.onBehaviorsCollectionChanging(new BehaviorCollectionChangedInfo(item, null, CollectionChangeAction.ADD));

        super.add(index, item);
    }

    @Override
    public ChartBehavior remove(int index) {
        this.verifyIndex(index);

        this.owner.onBehaviorsCollectionChanging(new BehaviorCollectionChangedInfo(null, this.get(index), CollectionChangeAction.REMOVE));
        return super.remove(index);
    }

    @Override
    public ChartBehavior set(int index, ChartBehavior object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }
        this.verifyIndex(index);

        this.owner.onBehaviorsCollectionChanging(new BehaviorCollectionChangedInfo(object, this.get(index), CollectionChangeAction.REPLACE));
        super.set(index, object);
        return object;
    }

    @Override
    public void clear() {
        this.owner.onBehaviorsCollectionChanging(new BehaviorCollectionChangedInfo(null, null, CollectionChangeAction.RESET));
        super.clear();
    }
}
