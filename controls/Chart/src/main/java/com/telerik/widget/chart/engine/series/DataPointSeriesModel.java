package com.telerik.widget.chart.engine.series;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents widget series that consist of data points.
 *
 * @param <T> Must be a {@link DataPoint}.
 */
public abstract class DataPointSeriesModel<T extends DataPoint> extends ChartSeriesModel {
    protected DataPointCollection<T> dataPoints;
    protected List<T> visibleDataPoints = new ArrayList<T>();
    private boolean virtualizationEnabled = false;

    /**
     * Creates a new instance of the {@link DataPointSeriesModel} class.
     */
    public DataPointSeriesModel() {
        this.dataPoints = new DataPointCollection<T>(this);
    }

    public boolean getVirtualizationEnabled() {
        return this.virtualizationEnabled;
    }

    public void setVirtualizationEnabled(boolean enabled) {
        this.virtualizationEnabled = enabled;
    }

    @Override
    public DataPointCollection<T> dataPoints() {
        return this.dataPoints;
    }

    @Override
    public void updateVisibleDataPoints() {
        if (!this.virtualizationEnabled || getPresenter() == null)
            return;

        this.updateVisibleDataPointsCore();
    }

    protected void updateVisibleDataPointsCore() {
    }

    @Override
    public List<T> visibleDataPoints() {
        if (this.virtualizationEnabled)
            return this.visibleDataPoints;
        else
            return this.dataPoints;
    }
}
