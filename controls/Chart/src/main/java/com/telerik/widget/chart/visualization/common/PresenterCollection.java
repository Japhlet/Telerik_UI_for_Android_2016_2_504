package com.telerik.widget.chart.visualization.common;

import com.telerik.android.common.ObservableCollection;

import java.util.ArrayList;

/**
 * Represents a strongly typed collection of {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter} instances.
 */
public class PresenterCollection<T extends ChartElementPresenter> extends ObservableCollection<T> {

    private RadChartViewBase chart;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.common.PresenterCollection}
     * class with a specified {@link RadChartViewBase} as an owner.
     *
     * @param control the owning {@link RadChartViewBase}.
     */
    public PresenterCollection(RadChartViewBase control) {
        this.chart = control;
    }

    /**
     * Initialises this {@link com.telerik.widget.chart.visualization.common.PresenterCollection} with the
     * specified {@link RadChartViewBase} owner.
     *
     * @param owner the owner.
     */
    public void init(RadChartViewBase owner) {
        if (this.chart != null) {
            return;
        }
        this.chart = owner;
    }

    /**
     * Resets the current owner of this {@link com.telerik.widget.chart.visualization.common.PresenterCollection}
     */
    public void reset() {
        this.chart = null;
    }

    /**
     * Returns the owning {@link RadChartViewBase} instance.
     *
     * @return the owner.
     */
    public RadChartViewBase owner() {
        return this.chart;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);

        if (this.chart != null) {
            object.setCollectionIndex(index);
            chart.onPresenterAdded(object);
        }
    }

    @Override
    public boolean add(T obj) {
        if (this.chart != null) {
            obj.setCollectionIndex(this.size());
        }

        boolean result = super.add(obj);

        if (this.chart != null) {
            chart.onPresenterAdded(obj);
        }

        return result;
    }

    @Override
    public boolean remove(Object object) {
        boolean result = super.remove(object);
        if (this.chart != null) {
            chart.onPresenterRemoved((ChartElementPresenter) object);
            ChartElementPresenter presenter = (ChartElementPresenter) object;
            presenter.setCollectionIndex(-1);
        }

        return result;
    }

    @Override
    public T remove(int index) {
        T result = super.remove(index);

        if (this.chart != null) {
            chart.onPresenterRemoved(result);
            result.setCollectionIndex(-1);
        }

        return result;
    }

    @Override
    public void clear() {
        if (this.chart != null) {
            this.chart.beginUpdate();

            for (ChartElementPresenter presenter : this)
                chart.onPresenterRemoved(presenter);
        }

        super.clear();

        if (this.chart != null)
            this.chart.endUpdate();
    }

    /* TODO c#
        /// <summary>
        /// Inserts an element into the collection at the specified index.
        /// </summary>
        /// <param name="index"></param>
        /// <param name="item"></param>
        protected override void insertItem(int index, T item)
        {
            super.insertItem(index, item);

            if (this.chart != null)
            {
                this.chart.onPresenterAdded(item);
            }
        }

        /// <summary>
        /// Removes the element at the specified index of the collection.
        /// </summary>
        /// <param name="index"></param>
        protected override void RemoveItem(int index)
        {
            T presenter = this[index];
            
            super.RemoveItem(index);

            if (this.chart != null)
            {
                this.chart.onPresenterRemoved(presenter);
            }
        }

        /// <summary>
        /// Replaces the element at the specified index.
        /// </summary>
        /// <param name="index">The zero-based index of the element to replace.</param>
        /// <param name="newPresenter">The new value for the element at the specified index. The value can be null for reference types.</param>
        /// <exception cref="T:System.ArgumentOutOfRangeException"><paramref name="index"/> is less than zero.
        /// -or-
        /// <paramref name="index"/> is greater than <see cref="P:System.Collections.ObjectModel.Collection`1.size()"/>.
        /// </exception>
        protected override void SetItem(int index, T newPresenter)
        {
            T oldPresenter = this[index];

            super.SetItem(index, newPresenter);

            if (this.chart != null)
            {
                this.chart.onPresenterRemoved(oldPresenter);
                this.chart.onPresenterAdded(newPresenter);
            }
        }
        */
}

