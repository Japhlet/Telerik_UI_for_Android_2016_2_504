package com.telerik.widget.chart.engine.chartAreas;

import com.telerik.android.common.DataTuple;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisModel;
import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
import com.telerik.widget.chart.engine.axes.categorical.AxisSupportsCombinedSeriesPlot;
import com.telerik.widget.chart.engine.axes.common.SeriesModelWithAxes;
import com.telerik.widget.chart.engine.decorations.ChartGridModel;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.elementTree.ModifyChildrenResult;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineStrategy;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A base class for all Chart models that also have support for axes. This class is an abstract class
 * and cannot be used directly in your application.
 */
public abstract class ChartAreaModelWithAxes extends ChartAreaModel {

    protected final ArrayList<AxisModel> firstAxes = new ArrayList<AxisModel>();
    protected final ArrayList<AxisModel> secondAxes = new ArrayList<AxisModel>();
    protected final Hashtable<AxisModel, ChartSeriesCombineStrategy> seriesCombineStrategies = new Hashtable<AxisModel, ChartSeriesCombineStrategy>();
    protected final ArrayList<ChartAnnotationModel> annotations = new ArrayList<ChartAnnotationModel>();

    public AxisModel primaryFirstAxis;
    public AxisModel primarySecondAxis;
    ChartGridModel grid;

    /**
     * Creates an instance of the {@link ChartAreaModelWithAxes}
     */
    public ChartAreaModelWithAxes() {
    }

    @Override
    public boolean isTreeLoaded() {
        return super.isTreeLoaded() && this.firstAxes.size() > 0 && this.secondAxes.size() > 0;
    }

    /**
     * Sets the provided {@link AxisModel} with the provided {@link AxisType} to the current {@link ChartAreaModelWithAxes}.
     *
     * @param axis the {@link AxisModel} instance describing the axis being set.
     * @param type a value from the {@link AxisType} enum specifying the type of the axis.
     */
    public void setAxis(AxisModel axis, AxisType type) {
        // Debug.Assert(axis != null, "axis should not be null!");
        if (type == AxisType.FIRST) {
            if (this.firstAxes.contains(axis)) {
                return;
            }
            this.firstAxes.add(axis);
            if (axis.getIsPrimary()) {
                this.primaryFirstAxis = axis;
            }
        } else {
            if (this.secondAxes.contains(axis)) {
                return;
            }
            this.secondAxes.add(axis);
            if (axis.getIsPrimary()) {
                this.primarySecondAxis = axis;
            }
        }

        axis.setType(type);
        this.attachAxis(axis);
    }

    /**
     * Removes the provided {@link AxisModel} from the current {@link ChartAreaModelWithAxes}.
     *
     * @param axis the {@link AxisModel} describing the axis to remove.
     */
    public void removeAxis(AxisModel axis) {
        if (axis.getType() == AxisType.FIRST) {
            this.firstAxes.remove(axis);
        } else {
            this.secondAxes.remove(axis);
        }
        this.seriesCombineStrategies.remove(axis);
        this.detachAxis(axis);
    }

    /**
     * Sets up support for drawing Grid lines on the current {@link ChartAreaModelWithAxes} instance.
     *
     * @param grid an instance of the {@link ChartGridModel} class used by the drawing engine to draw the grid lines.
     */
    public void setGrid(ChartGridModel grid) {
        if (this.grid == grid) {
            return;
        }

        if (this.grid != null) {
            this.children.remove(this.grid);
        }

        this.grid = grid;

        if (this.grid != null) {
            this.children.add(this.grid);
        }

        this.invalidate(ChartAreaInvalidateFlags.ALL);
    }

    /**
     * Adds an annotation to the current Chart model.
     *
     * @param annotation the {@link ChartAnnotationModel} instance representing the annotation.
     */
    public void addAnnotation(ChartAnnotationModel annotation) {
        this.annotations.add(annotation);

        this.children.add(annotation);
    }

    /**
     * Removes the specified annotation from the current model.
     *
     * @param annotation the {@link ChartAnnotationModel} instance representing the annotation to remove.
     */
    public void removeAnnotation(ChartAnnotationModel annotation) {
        this.annotations.remove(annotation);

        this.children.remove(annotation);
    }

    @Override
    void invalidateCore(int flags) {
        if ((flags & ChartAreaInvalidateFlags.RESET_AXES) == ChartAreaInvalidateFlags.RESET_AXES) {
            for (AxisModel axis : this.firstAxes) {
                axis.resetState();
            }
            for (AxisModel axis : this.secondAxes) {
                axis.resetState();
            }
            this.seriesCombineStrategies.clear();
        }
        if ((flags & ChartAreaInvalidateFlags.INVALIDATE_AXES) == ChartAreaInvalidateFlags.INVALIDATE_AXES) {
            for (AxisModel axis : this.firstAxes) {
                axis.invalidate();
            }
            for (AxisModel axis : this.secondAxes) {
                axis.invalidate();
            }
        }
        if ((flags & ChartAreaInvalidateFlags.INVALIDATE_GRID) == ChartAreaInvalidateFlags.INVALIDATE_GRID) {
            if (this.grid != null) {
                this.grid.invalidate();
            }
        }
        if ((flags & ChartAreaInvalidateFlags.RESET_ANNOTATIONS) == ChartAreaInvalidateFlags.RESET_ANNOTATIONS) {
            for (ChartAnnotationModel annotation : this.annotations) {
                annotation.resetState();
            }
        }
        if ((flags & ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS) == ChartAreaInvalidateFlags.INVALIDATE_ANNOTATIONS) {
            for (ChartAnnotationModel annotation : this.annotations) {
                annotation.invalidate();
            }
        }

        super.invalidateCore(flags);
    }

    @Override
    public ModifyChildrenResult canAddChild(ChartNode child) {
        if (child == this.getPlotArea() || child == this.grid || child instanceof ChartAnnotationModel ||
                ((child instanceof AxisModel) && (this.firstAxes.contains(child) || this.secondAxes.contains(child)))) {
            return ModifyChildrenResult.ACCEPT;
        }

        return super.canAddChild(child);
    }

    @Override
    protected RadRect arrangeOverride(final RadRect rect) {
        this.beginUpdate();

        this.updateSeriesVisiblePoints();
        // TODO: axis update should be handled smarter, not on every arrange pass
        this.updateAxes();
        RadRect seriesRect = this.arrangeAxes(rect);
        this.getPlotArea().arrange(seriesRect);
        this.applyLayoutRounding();

        if (this.getView().getZoomWidth() > 1 || getView().getZoomHeight() > 1) {
            // update ticks and labels visibility
            // this is done after the axes and plot area are arranged so that all the layout information is available
            RadRect clipRect = this.getView().getPlotAreaClip();
            for (AxisModel axis : this.firstAxes) {
                axis.updateTicksVisibility(clipRect);
            }
            for (AxisModel axis : this.secondAxes) {
                axis.updateTicksVisibility(clipRect);
            }
        }

        // arrange the grid within the series rect
        if (this.grid != null) {
            this.grid.arrange(seriesRect);
        }

        // arrange the annotations within the series rect
        for (ChartAnnotationModel annotation : this.annotations) {
            annotation.arrange(seriesRect);
        }

        this.endUpdate(false);
        return rect;
    }

    private void updateSeriesVisiblePoints() {
        for(ChartSeriesModel series : this.getSeries()) {
            series.updateVisibleDataPoints();
        }
    }

    DataTuple convertPointToData(final RadPoint coordinates, AxisModel firstAxis, AxisModel secondAxis) {
        return new DataTuple(null, null, null);
    }

    @Override
    protected void processZoomChanged() {
        for (AxisModel axis : this.firstAxes) {
            axis.onZoomChanged();
        }
        for (AxisModel axis : this.secondAxes) {
            axis.onZoomChanged();
        }

        super.processZoomChanged();
    }

    @Override
    protected void processPanOffsetChanged() {
        for (AxisModel axis : this.firstAxes) {
            axis.onPanOffsetChanged();
        }
        for (AxisModel axis : this.secondAxes) {
            axis.onPanOffsetChanged();
        }

        super.processPanOffsetChanged();
    }

    /**
     * Attaches the provided axis to the current model and invalidates it.
     *
     * @param axis an instance of the {@link AxisModel} class representing the axis to attach.
     */
    protected void attachAxis(AxisModel axis) {
        this.children.add(axis);
        this.invalidate(ChartAreaInvalidateFlags.ALL);
    }

    /**
     * Detaches the specified axis from the current model and invalidates it.
     *
     * @param axis an instance of the {@link AxisModel} class representing the axis to detach.
     */
    protected void detachAxis(AxisModel axis) {
        this.children.remove(axis);
        this.invalidate(ChartAreaInvalidateFlags.ALL);
    }

    /**
     * Arranges the available axes in the current {@link ChartAreaModelWithAxes} instance.
     *
     * @param rect an instance of the {@link RadRect} class specifying the layout slot to arrange the axis into.
     * @return a new instance of {@link RadRect} representing the layout slot of the axes.
     */
    protected abstract RadRect arrangeAxes(final RadRect rect);

    private void updateAxes() {
        // We are using LinkedHashMap because it preserves the order in which keys are inserted.
        // We were using HashTable before and the order of the keys was always random resulting in strange behavior in the scatter series.
        // The plot origin of the scatter series used to change from app execution to app execution because the different order of the keys
        // caused different axes to set the plot origin last.
        //
        // For example, on the first execution if the vertical axis has negative values from -30 to 30
        // its plot origin will be 0.5. However, the horizontal axis will have a plot origin of 0 if it has values from 0 to 30.
        // First the horizontal plot origin is set and then the vertical plot origin which results in correct rendering.
        // On a random future execution the horizontal axis will set its plot origin of 0 last which results in incorrect rendering.
        LinkedHashMap<AxisModel, ArrayList<ChartSeriesModel>> seriesByStackAxis = new LinkedHashMap<AxisModel, ArrayList<ChartSeriesModel>>();
        LinkedHashMap<AxisModel, ArrayList<ChartSeriesModel>> seriesByValueAxis = new LinkedHashMap<AxisModel, ArrayList<ChartSeriesModel>>();

        SeriesModelWithAxes seriesWithAxes;
        AxisModel firstAxis;
        AxisModel secondAxis;
        ArrayList<ChartSeriesModel> seriesModelsByFirstAxis;
        ArrayList<ChartSeriesModel> seriesModelsBySecondAxis;
        for (ChartSeriesModel series : this.getSeries()) {
            seriesWithAxes = (SeriesModelWithAxes) series;
            firstAxis = seriesWithAxes.getFirstAxis();
            secondAxis = seriesWithAxes.getSecondAxis();

            if (firstAxis instanceof AxisSupportsCombinedSeriesPlot) {
                if (!seriesByStackAxis.containsKey(firstAxis)) {
                    seriesModelsByFirstAxis = new ArrayList<ChartSeriesModel>();
                    seriesByStackAxis.put(firstAxis, seriesModelsByFirstAxis);
                } else {
                    seriesModelsByFirstAxis = seriesByStackAxis.get(firstAxis);
                }
                seriesModelsByFirstAxis.add(series);
            } else {
                if (!seriesByValueAxis.containsKey(firstAxis)) {
                    seriesModelsByFirstAxis = new ArrayList<ChartSeriesModel>();
                    seriesByValueAxis.put(firstAxis, seriesModelsByFirstAxis);
                } else {
                    seriesModelsByFirstAxis = seriesByValueAxis.get(firstAxis);
                }
                seriesModelsByFirstAxis.add(series);
            }

            if (secondAxis instanceof AxisSupportsCombinedSeriesPlot) {
                if (!seriesByStackAxis.containsKey(secondAxis)) {
                    seriesModelsBySecondAxis = new ArrayList<ChartSeriesModel>();
                    seriesByStackAxis.put(secondAxis, seriesModelsBySecondAxis);
                } else {
                    seriesModelsBySecondAxis = seriesByStackAxis.get(secondAxis);
                }
                seriesModelsBySecondAxis.add(series);
            } else {
                if (!seriesByValueAxis.containsKey(secondAxis)) {
                    seriesModelsBySecondAxis = new ArrayList<ChartSeriesModel>();
                    seriesByValueAxis.put(secondAxis, seriesModelsBySecondAxis);
                } else {
                    seriesModelsBySecondAxis = seriesByValueAxis.get(secondAxis);
                }
                seriesModelsBySecondAxis.add(series);
            }
        }

        // NOTE: ALL stack axes (i.e. their associated combine strategies) should be updated first
        // as the stack value axes' ranges can be affected by multiple combine strategies.
        Iterable<AxisModel> stackAxes = seriesByStackAxis.keySet();
        for (AxisModel stackAxis : stackAxes) {
            this.updateCombineStrategy(stackAxis, seriesByStackAxis.get(stackAxis));

            this.updateAxis(stackAxis, seriesByStackAxis.get(stackAxis));
        }

        Iterable<AxisModel> stackValueAxes = seriesByValueAxis.keySet();
        for (AxisModel valueAxis : stackValueAxes) {
            this.updateAxis(valueAxis, seriesByValueAxis.get(valueAxis));
        }

        // update primary axes if they are not associated with series
        if (this.primaryFirstAxis != null && !this.primaryFirstAxis.isUpdated()) {
            this.updateAxis(this.primaryFirstAxis, null);
        }
        if (this.primarySecondAxis != null && !this.primarySecondAxis.isUpdated()) {
            this.updateAxis(this.primarySecondAxis, null);
        }
    }

    private void updateCombineStrategy(AxisModel stackAxis, List<ChartSeriesModel> series) {
        ChartSeriesCombineStrategy strategy;
        if (!this.seriesCombineStrategies.contains(stackAxis)) {
            strategy = new ChartSeriesCombineStrategy();
            this.seriesCombineStrategies.put(stackAxis, strategy);
        } else {
            strategy = this.seriesCombineStrategies.get(stackAxis);
        }

        strategy.update(series, stackAxis);
    }

    private void updateAxis(AxisModel axis, ArrayList<ChartSeriesModel> series) {
        AxisUpdateContext context = new AxisUpdateContext(axis, series, this.seriesCombineStrategies.values());

        axis.update(context);

        boolean plotInvalid = axis.isPlotValid();

        // plot points
        axis.plot(context);

        if (axis instanceof AxisSupportsCombinedSeriesPlot && !plotInvalid && this.seriesCombineStrategies.containsKey(axis)) {
            this.seriesCombineStrategies.get(axis).plot();
        }
    }
}
