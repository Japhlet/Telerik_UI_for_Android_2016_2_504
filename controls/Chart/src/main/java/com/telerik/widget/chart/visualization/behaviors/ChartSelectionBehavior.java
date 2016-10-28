package com.telerik.widget.chart.visualization.behaviors;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.common.ChartSeries;

import java.util.LinkedList;

/**
 * This class enables selection in RadCartesianChartView and RadPieChartView.
 */
public class ChartSelectionBehavior extends ChartBehavior {
    private ChartSelectionMode seriesSelectionMode = ChartSelectionMode.NONE;
    private ChartSelectionMode pointSelectionMode = ChartSelectionMode.SINGLE;
    private ChartSelectionChangeListener selectionChangeListener;
    private ChartSelectionContext previousSelection;
    private DataPoint previousSelectedPoint;
    private ChartSeries previousSelectedSeries;
    private LinkedList<DataPoint> selectedDataPoints = new LinkedList<DataPoint>();
    private LinkedList<ChartSeries> selectedSeries = new LinkedList<ChartSeries>();

    /**
     * Gets a value that determines the current series selection mode.
     */
    public ChartSelectionMode getSeriesSelectionMode() {
        return this.seriesSelectionMode;
    }

    /**
     * Sets the series selection mode.
     */
    public void setSeriesSelectionMode(ChartSelectionMode newValue) {
        if (this.seriesSelectionMode == newValue) {
            return;
        }

        this.seriesSelectionMode = newValue;
    }

    /**
     * Gets the data point selection mode.
     */
    public ChartSelectionMode getDataPointsSelectionMode() {
        return this.pointSelectionMode;
    }

    /**
     * Sets the data point selection mode.
     */
    public void setDataPointsSelectionMode(ChartSelectionMode newValue) {
        if (this.pointSelectionMode == newValue) {
            return;
        }

        this.pointSelectionMode = newValue;
    }

    /**
     * Sets a selection change listener that will be notified when the selection changes.
     */
    public void setSelectionChangeListener(ChartSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    /**
     * Gets the selected data points.
     */
    public Iterable<DataPoint> selectedDataPoints() {
        return this.selectedDataPoints;
    }

    /**
     * Gets the selected series.
     */
    public Iterable<ChartSeries> selectedSeries() {
        return this.selectedSeries;
    }

    @Override
    public boolean onTap(MotionEvent e) {
        boolean baseResult = super.onTap(e);

        float x = e.getX();
        float y = e.getY();

        ChartSelectionContext selectionContext = this.updateSelection(new PointF(x, y));
        if (selectionContext == null)
            return baseResult;

        if (selectionContext.selectedDataPoint() != null) {
            this.selectedDataPoints.add(selectionContext.selectedDataPoint());
        }

        if (selectionContext.deselectedDataPoint() != null) {
            this.selectedDataPoints.remove(selectionContext.deselectedDataPoint());
        }

        if (selectionContext.selectedSeries() != null) {
            this.selectedSeries.add(selectionContext.selectedSeries());
        }

        if (selectionContext.deselectedSeries() != null) {
            this.selectedSeries.remove(selectionContext.deselectedSeries());
        }

        if (selectionChangeListener != null) {
            this.selectionChangeListener.onSelectionChanged(selectionContext);
            this.previousSelection = selectionContext;
        }

        return true;
    }

    @Override
    protected void onDetached() {
        super.onDetached();

        this.previousSelection = null;
    }

    private ChartSelectionContext updateSelection(PointF touchTarget) {
        ElementCollection<ChartSeriesModel> series = this.chart.chartAreaModel().getSeries();

        ValueExtractor<ChartSeries> selectedSeriesExtractor = new ValueExtractor<ChartSeries>();
        ValueExtractor<ChartSeries> deselectedSeriesExtractor = new ValueExtractor<ChartSeries>();
        ValueExtractor<DataPoint> selectedPointExtractor = new ValueExtractor<DataPoint>();
        ValueExtractor<DataPoint> deselectedPointExtractor = new ValueExtractor<DataPoint>();

        boolean seriesSelected = false;
        boolean pointSelected = false;

        if (this.pointSelectionMode != ChartSelectionMode.NONE) {
            // Iterate backwards so that the last added series, the ones drawn on top, are selected first.
            for (int i = series.size() - 1; i >= 0; --i) {
                ChartSeries view = (ChartSeries) series.get(i).getPresenter();
                DataPoint selectedPoint = view.hitTestDataPoint(touchTarget);

                if (selectedPoint == null) {
                    continue;
                }

                if (this.pointSelectionMode == ChartSelectionMode.SINGLE) {
                    this.updateSelectedPointsSingle(selectedPoint, selectedPointExtractor, deselectedPointExtractor);
                } else if (this.pointSelectionMode == ChartSelectionMode.MULTIPLE) {
                    this.updateSelectedPointsMultiple(selectedPoint, selectedPointExtractor, deselectedPointExtractor);
                }

                pointSelected = true;
                break;
            }
        }

        if (this.seriesSelectionMode != ChartSelectionMode.NONE) {
            for (int i = series.size() - 1; i >= 0; --i) {
                ChartSeries view = (ChartSeries) series.get(i).getPresenter();
                if (!view.hitTest(touchTarget)) {
                    continue;
                }

                if (this.seriesSelectionMode == ChartSelectionMode.SINGLE) {
                    this.updateSelectedSeriesSingle(view, selectedSeriesExtractor, deselectedSeriesExtractor);
                } else if (this.seriesSelectionMode == ChartSelectionMode.MULTIPLE) {
                    this.updateSelectedSeriesMultiple(view, selectedSeriesExtractor, deselectedSeriesExtractor);
                }

                seriesSelected = true;
                break;
            }
        }

        if (seriesSelected || pointSelected) {
            return new ChartSelectionContext(this, selectedPointExtractor.value, deselectedPointExtractor.value, selectedSeriesExtractor.value, deselectedSeriesExtractor.value, this.previousSelection);
        }

        return null;
    }

    private void updateSelectedPointsSingle(DataPoint point, ValueExtractor<DataPoint> selectedPointExtractor, ValueExtractor<DataPoint> deselectedPointExtractor) {
        if (point.getIsSelected()) {
            point.setIsSelected(false);
            deselectedPointExtractor.value = point;
            this.previousSelectedPoint = point;
            return;
        }

        if (this.previousSelectedPoint != null && this.previousSelectedPoint != point) {
            this.previousSelectedPoint.setIsSelected(false);
            deselectedPointExtractor.value = this.previousSelectedPoint;
        }

        point.setIsSelected(true);
        this.previousSelectedPoint = point;
        selectedPointExtractor.value = point;
    }

    private void updateSelectedPointsMultiple(DataPoint point, ValueExtractor<DataPoint> selectedPointExtractor, ValueExtractor<DataPoint> deselectedPointExtractor) {
        if (point.getIsSelected()) {
            point.setIsSelected(false);
            deselectedPointExtractor.value = point;
        } else {
            point.setIsSelected(true);
            selectedPointExtractor.value = point;
        }
    }

    private void updateSelectedSeriesSingle(ChartSeries series, ValueExtractor<ChartSeries> selectedSeriesExtractor, ValueExtractor<ChartSeries> deselectedSeriesExtractor) {
        if (series.getIsSelected()) {
            series.setIsSelected(false);
            deselectedSeriesExtractor.value = series;
            this.previousSelectedSeries = series;
            return;
        }

        if (this.previousSelectedSeries != null && this.previousSelectedSeries != series) {
            this.previousSelectedSeries.setIsSelected(false);
            deselectedSeriesExtractor.value = this.previousSelectedSeries;
        }

        series.setIsSelected(true);
        this.previousSelectedSeries = series;
        selectedSeriesExtractor.value = series;
    }

    private void updateSelectedSeriesMultiple(ChartSeries series, ValueExtractor<ChartSeries> selectedSeriesExtractor, ValueExtractor<ChartSeries> deselectedSeriesExtractor) {
        if (series.getIsSelected()) {
            series.setIsSelected(false);
            deselectedSeriesExtractor.value = series;
        } else {
            series.setIsSelected(true);
            selectedSeriesExtractor.value = series;
        }
    }
}
