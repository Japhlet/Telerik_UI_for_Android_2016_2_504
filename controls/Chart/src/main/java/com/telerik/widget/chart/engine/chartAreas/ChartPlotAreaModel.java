package com.telerik.widget.chart.engine.chartAreas;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;

/**
 * Provides the base infrastructure for plotting areas in different types of charts.
 */
public class ChartPlotAreaModel extends ChartElement {

    ElementCollection<ChartSeriesModel> series;

    /**
     * Creates an instance of the  {@link ChartPlotAreaModel} class with an empty
     * series collection.
     */
    ChartPlotAreaModel() {
        this.series = new ElementCollection<ChartSeriesModel>(this);
    }

    /**
     * Gets a {@link com.telerik.widget.chart.engine.elementTree.ElementCollection} instance that holds
     * the currently available Chart series.
     *
     * @return the series collection.
     */
    public ElementCollection<ChartSeriesModel> getSeries() {
        return this.series;
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child instanceof ChartSeriesModel) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }

    @Override
    protected void onChildInserted(int index, ChartNode child) {
        super.onChildInserted(index, child);
        if (this.chartArea != null) {
            this.chartArea.invalidate(ChartAreaInvalidateFlags.ALL);
        }
    }

    @Override
    protected void onChildRemoved(int index, ChartNode child) {
        super.onChildRemoved(index, child);
        if (this.chartArea != null) {
            this.chartArea.invalidate(ChartAreaInvalidateFlags.ALL);
        }
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        for (ChartSeriesModel series : this.series) {
            series.arrange(rect);
        }

        return rect;
    }

    @Override
    public RadRect arrange(final RadRect rect) {
        super.arrange(rect);
        return rect;
    }
}

