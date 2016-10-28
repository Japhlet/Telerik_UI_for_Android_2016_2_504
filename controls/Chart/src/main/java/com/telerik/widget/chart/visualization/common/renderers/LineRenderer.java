package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.CategoricalStrokedSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.DataPointIndicatorRenderer;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.chart.visualization.common.StrokedSeries;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.List;

/**
 * Represents a {@link ChartSeriesRenderer} and is used for rendering lines in the chart series.
 */
public class LineRenderer extends ChartSeriesRenderer {

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            LineRenderer renderer = (LineRenderer)sender;

            int value = renderer.getStrokeColor();
            renderer.strokeColor = value;
            renderer.linePaint.setColor(value);
        }
    });

    public static final int STROKE_THICKNESS_PROPERTY_KEY = registerProperty(Util.getDP(2.0f), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            LineRenderer renderer = (LineRenderer)sender;

            float value = renderer.getStrokeThickness();
            if (value < 0)
                throw new IllegalArgumentException("value cannot be negative");

            renderer.strokeThickness = value;
            renderer.linePaint.setStrokeWidth(value);
        }
    });

    /**
     * Context storing info about the currently rendered chart layout.
     *
     * @see ChartLayoutContext
     */
    public ChartLayoutContext layoutContext;

    /**
     * Instance of the {@link DataPointIndicatorRenderer} class used to render the data point indicators.
     */
    protected DataPointIndicatorRenderer indicatorRenderer;

    /**
     * The color of the line stroke.
     */
    protected Paint linePaint;

    /**
     * The path that describes the line.
     *
     * @see Path
     */
    protected Path linePath;

    /**
     * Color id of the stroke color.
     */
    protected int strokeColor = Color.RED;

    /**
     * The width of the stroke.
     */
    protected float strokeThickness = Util.getDP(2);

    /**
     * A collection holding the info about the dashes.
     */
    protected float[] dashArray;

    /**
     * Creates a new instance of the {@link LineRenderer} class.
     */
    public LineRenderer() {
        this.linePath = new Path();
        this.linePaint = new Paint();
        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setAntiAlias(true);
    }

    /**
     * Gets the current {@link DataPointIndicatorRenderer} instance used for rendering the data point indicators.
     *
     * @return the current data points indicators renderer.
     */
    public DataPointIndicatorRenderer getDataPointIndicatorRenderer() {
        return indicatorRenderer;
    }

    /**
     * Sets the current data point indicator renderer.
     *
     * @param renderer the new renderer.
     */
    public void setDataPointIndicatorRenderer(DataPointIndicatorRenderer renderer) {
        indicatorRenderer = renderer;
        indicatorRenderer.invalidatePalette();
    }

    /**
     * Sets the current chart series model.
     *
     * @param model the new model.
     * @see ChartSeriesModel
     */
    public void setModel(ChartSeriesModel model) {
        this.model = model;
    }

    /**
     * Gets the id of the stroke color.
     *
     * @return the id of the color.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the id of the stroke color.
     *
     * @param value the new color id.
     */
    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    public Paint getLinePaint() {
        return this.linePaint;
    }

    public void setStrokePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("paint argument cannot be null.");
        }

        this.linePaint = paint;
    }

    /**
     * Gets the color of the indicator stroke.
     *
     * @return the current indicator stroke color.
     */
    public int getPointIndicatorStrokeColor() {
        return getDataPointIndicatorRenderer().getPointIndicatorStrokeColor();
    }

    /**
     * Sets the current point indicator stroke color.
     *
     * @param color the new color.
     */
    public void setPointIndicatorStrokeColor(int color) {
        getDataPointIndicatorRenderer().setPointIndicatorStrokeColor(color);
    }

    /**
     * Gets the point indicator color.
     *
     * @return the current point indicator color.
     */
    public int getPointIndicatorColor() {
        return getDataPointIndicatorRenderer().getPointIndicatorColor();
    }

    /**
     * Sets the point indicator color.
     *
     * @param color the new color.
     */
    public void setPointIndicatorColor(int color) {
        getDataPointIndicatorRenderer().setPointIndicatorColor(color);
    }

    /**
     * Gets the current collection of dashes.
     *
     * @return the current collection of dashes.
     */
    public float[] getDashArray() {
        return this.dashArray;
    }

    /**
     * Sets the current collection of dashes.
     *
     * @param dashArray the new dash collection.
     */
    public void setDashArray(float[] dashArray) {
        this.dashArray = dashArray;
        this.linePaint.setPathEffect(new DashPathEffect(this.dashArray, 0));
    }

    /**
     * Gets the current width of the stroke.
     *
     * @return the current width of the stroke.
     */
    public float getStrokeThickness() {
        return (float)this.getValue(STROKE_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the current width of the stroke.
     *
     * @param value the new width.
     */
    public void setStrokeThickness(float value) {
        this.setValue(STROKE_THICKNESS_PROPERTY_KEY, value);
    }

    /**
     * Determines if the figure drawn by the renderer contains the given point.
     *
     * @param point The point to test.
     * @return True if the figure contains the point and false otherwise.
     */
    public boolean hitTest(PointF point) {
        RectF bounds = new RectF();
        Path path = this.getPath();
        path.computeBounds(bounds, true);

        Rect rectBounds = Util.RectFToRect(bounds);

        if(this.model.getPresenter() instanceof CategoricalStrokedSeries) {
            CategoricalStrokedSeries series = (CategoricalStrokedSeries) this.model.getPresenter();
            float touchRadius = series.getTouchTargetRadius();
            float halfPointRadius = touchRadius / 2.0f;
            rectBounds.left -= halfPointRadius;
            rectBounds.right += halfPointRadius;
            rectBounds.top -= halfPointRadius;
            rectBounds.bottom += halfPointRadius;
        }

        Region region = new Region();
        region.set(rectBounds);

        return region.contains(Math.round(point.x), Math.round(point.y));
    }

    /**
     * Gets the main Path object for this renderer.
     */
    protected Path getPath() {
        return this.linePath;
    }

    @Override
    public void applyPalette(ChartPalette palette) {
        super.applyPalette(palette);

        StrokedSeries strokedSeries = (StrokedSeries) this.model.getPresenter();

        if (strokedSeries == null)
            return;

        ChartSeries series = (ChartSeries) this.model.getPresenter();

        if (series.getIsSelected() && series.getChart().getSelectionPalette() != null) {
            palette = series.getChart().getSelectionPalette();
        }

        PaletteEntry entry = null;
        if (palette != null) {
            entry = palette.getEntry(series.getPaletteFamilyCore(), this.model.getPresenter().getCollectionIndex());
        }

        if (entry != null) {
            this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
            this.setValue(STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
            if (indicateDataPoints())
                this.getDataPointIndicatorRenderer().applyPalette(palette);
        }
    }

    @Override
    protected void reset() {
        this.linePath.reset();
    }

    @Override
    protected void preparePaths() {
        // we need at least two points to calculate the line
        List<DataPoint> points = this.model.visibleDataPoints();

        if (indicateDataPoints()) {
            prepareDataPointIndicators(points);
        }

        if (points.size() < 2) {
            return;
        }



        boolean isPathStartSet = false;

        for (DataPoint point : points) {
            if(this.shouldDrawPoint(point)) {
                if (point.isEmpty) {
                    isPathStartSet = false;
                    continue;
                }

                if (!isPathStartSet) {
                    linePath.moveTo((float) point.getCenterX(), (float) point.getCenterY());
                    isPathStartSet = true;
                    continue;
                }

                linePath.lineTo((float) point.getCenterX(), (float) point.getCenterY());
            }
        }
    }

    protected boolean shouldDrawPoint(DataPoint point) {
        List<DataPoint> points = this.model.visibleDataPoints();
        int i = point.collectionIndex();
        int nextIndex = i + 1;
        int prevIndex = i - 1;
        DataPoint nextPoint = points.get(nextIndex < points.size() ? nextIndex : points.size() - 1);
        DataPoint previousPoint = points.get(prevIndex >= 0 ? prevIndex : 0);

        return point.isVisible() || nextPoint.isVisible() || previousPoint.isVisible();
    }

    /**
     * Used to prepare the locations for the data point indicators.
     *
     * @param points the currently visible data points.
     */
    protected void prepareDataPointIndicators(List<DataPoint> points) {
        getDataPointIndicatorRenderer().clearDataPointLocations();

        for (DataPoint point : points) {
            if (point.isEmpty)
                continue;

            getDataPointIndicatorRenderer().addDataPointLocation(point.getCenter());
        }
    }

    @Override
    protected void renderCore(Canvas canvas) {
        if (this.linePath != null) {
            canvas.drawPath(this.linePath, linePaint);
        }

        if (indicateDataPoints()) {
            getDataPointIndicatorRenderer().drawDataPointIndicators(canvas);
        }
    }

    /**
     * States whether data points will be indicated or not.
     *
     * @return <code>true</code> if the data points will be indicated and <code>false</code> otherwise.
     */
    protected boolean indicateDataPoints() {
        return indicatorRenderer != null;
    }
}
