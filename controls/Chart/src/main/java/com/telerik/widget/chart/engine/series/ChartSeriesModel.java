package com.telerik.widget.chart.engine.series;

import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.common.AxisPlotMode;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartMessage;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.MessageDispatchMode;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesPlotStrategy;
import com.telerik.widget.chart.engine.series.combination.CombinedSeriesRoundLayoutStrategy;

import java.util.List;

/**
 * Base class for all chart series.
 */
public abstract class ChartSeriesModel<T extends DataPoint> extends ChartElement {

    public static final int DATA_POINTS_MODIFIED_MESSAGE_KEY = ChartMessage.register();
    private DataPointsChangedListener dataPointsChangedListener;

    /**
     * Gets a {@link com.telerik.widget.chart.engine.dataPoints.DataPointCollection} instance that holds the currently created data points in this series.
     */
    public abstract DataPointCollection<T> dataPoints();

    /**
     * Gets the current range of visible data points according to the current pan and zoom values.
     *
     * @return the current range of visible data points.
     */
    public abstract List<T> visibleDataPoints();


    /**
     * Used to trigger the update of the currently visible points according to the current pan and zoom values. Ideally meant to be called once per arrange invalidation.
     */
    public abstract void updateVisibleDataPoints();

    /**
     * Gets the default {@link com.telerik.widget.chart.engine.axes.common.AxisPlotMode} for this series.
     */
    public AxisPlotMode getDefaultPlotMode() {
        return AxisPlotMode.ON_TICKS;
    }

    /**
     * Attempts to select the correct plot mode depending on the plot modes of the series provided.
     *
     * @param series The series based on which the overall plot mode will be decided.
     * @return Returns {@link AxisPlotMode#ON_TICKS_PADDED} if any of the series has it as the default plot mode.
     * Otherwise, if any series is set to {@link AxisPlotMode#BETWEEN_TICKS} the value returned will be between ticks.
     * Finally, if none of the above are true, {@link AxisPlotMode#ON_TICKS} is returned.
     */
    public static AxisPlotMode selectPlotMode(Iterable<ChartSeriesModel> series) {
        boolean isAnyBetweenTicks = false;
        for (ChartSeriesModel currentSeries : series) {
            if (currentSeries.getDefaultPlotMode() == AxisPlotMode.ON_TICKS_PADDED) {
                return AxisPlotMode.ON_TICKS_PADDED;
            }
            isAnyBetweenTicks |= currentSeries.getDefaultPlotMode() == AxisPlotMode.BETWEEN_TICKS;
        }

        if (isAnyBetweenTicks) {
            return AxisPlotMode.BETWEEN_TICKS;
        }

        return AxisPlotMode.ON_TICKS;
    }

    /**
     * Gets the strategy that will be used when series of this type are combined - for example Stacked - on the plot area.
     */
    public CombinedSeriesPlotStrategy getCombinedPlotStrategy() {
        return null;
    }

    /**
     * Gets the strategy that will apply layout rounding for combined series of this type.
     */
    public CombinedSeriesRoundLayoutStrategy getCombinedRoundLayoutStrategy() {
        return null;
    }

    @Override
    protected void onChildInserted(int index, ChartNode child) {
        super.onChildInserted(index, child);

        this.onDataPointsModified();
        if(this.dataPointsChangedListener != null) {
            this.dataPointsChangedListener.onPointAdded(index, (DataPoint)child);
        }
    }

    @Override
    protected void onChildRemoved(int index, ChartNode child) {
        super.onChildRemoved(index, child);

        this.onDataPointsModified();
        if(this.dataPointsChangedListener != null) {
            this.dataPointsChangedListener.onPointRemoved(index, (DataPoint)child);
        }
    }

    /**
     * Gets an instance of {@link com.telerik.android.common.math.RadRect} scaled by the chart area's zoom.
     *
     * @param rect The rectangle to be scaled.
     * @return Returns a new instance of the provided {@link RadRect} scaled by the chart area's zoom.
     */
    protected RadRect getZoomedRect(final RadRect rect) {
        return new RadRect(rect.getX(), rect.getY(), rect.getWidth() * this.chartArea.getView().getZoomWidth(), rect.getHeight() * this.chartArea.getView().getZoomHeight());
    }

    void onDataPointsModified() {
        if (this.invalidateScheduled || !this.isTreeLoaded()) {
            return;
        }

        this.invalidate();

        ChartMessage message = new ChartMessage(this, DATA_POINTS_MODIFIED_MESSAGE_KEY, null, MessageDispatchMode.BUBBLE);
        this.chartArea.getDispatcher().dispatchMessage(message);
    }

    public void setDataPointsChangedListener(DataPointsChangedListener listener) {
        this.dataPointsChangedListener = listener;
    }

    public DataPointsChangedListener getDataPointsChangedListener() {
        return this.dataPointsChangedListener;
    }

    public interface DataPointsChangedListener {
        void onPointAdded(int index, DataPoint point);
        void onPointRemoved(int index, DataPoint point);
    }
}
