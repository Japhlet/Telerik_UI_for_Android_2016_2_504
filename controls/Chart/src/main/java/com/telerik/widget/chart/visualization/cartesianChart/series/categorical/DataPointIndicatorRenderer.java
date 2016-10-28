package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.ArrayList;
import java.util.List;

public abstract class DataPointIndicatorRenderer extends PropertyManager {

    public static final int POINT_INDICATOR_STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DataPointIndicatorRenderer renderer = (DataPointIndicatorRenderer)sender;

            renderer.pointIndicatorStrokeColor = renderer.getPointIndicatorStrokeColor();
            renderer.pointIndicatorStrokePaint.setColor(renderer.pointIndicatorStrokeColor);
        }
    });

    public static final int POINT_INDICATOR_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DataPointIndicatorRenderer renderer = (DataPointIndicatorRenderer)sender;

            renderer.pointIndicatorColor = renderer.getPointIndicatorColor();
            renderer.pointIndicatorPaint.setColor(renderer.pointIndicatorColor);
        }
    });

    public static final int POINT_INDICATOR_STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DataPointIndicatorRenderer renderer = (DataPointIndicatorRenderer)sender;

            renderer.pointIndicatorStrokeWidth = renderer.getPointIndicatorStrokeWidth();
            renderer.pointIndicatorStrokePaint.setStrokeWidth(renderer.pointIndicatorStrokeWidth);
        }
    });

    /**
     * Holds the name of the palette family entry associated with the data point indicator renderer.
     */
    protected final String PALETTE_FAMILY = "DataPointIndicators";

    /**
     * Presenter holding the current renderer.
     */
    protected final ChartSeries owner;

    /**
     * Holds the width of the data point indicator stroke.
     */
    protected float pointIndicatorStrokeWidth;

    /**
     * The color used in rendering the data point indicator.
     */
    protected int pointIndicatorColor = 0;

    /**
     * The paint used in rendering the data point indicator.
     */
    protected Paint pointIndicatorStrokePaint;

    /**
     * The color used in rendering the data point indicator.
     */
    protected int pointIndicatorStrokeColor = Color.WHITE;

    /**
     * The paint used in rendering the data point indicator.
     */
    protected Paint pointIndicatorPaint;

    /**
     * Collection holding all the data point locations which will later be used for rendering the data point indicators.
     */
    protected List<Point> dataPointLocations;

    public DataPointIndicatorRenderer(ChartSeries owner) {
        this.owner = owner;
        this.pointIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.pointIndicatorStrokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1);

        this.pointIndicatorStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.pointIndicatorStrokePaint.setStrokeWidth(this.pointIndicatorStrokeWidth);
        this.pointIndicatorStrokePaint.setStyle(Paint.Style.STROKE);
        this.pointIndicatorStrokePaint.setColor(this.pointIndicatorStrokeColor);

        this.dataPointLocations = new ArrayList<Point>();
    }

    /**
     * Gets the color of the indicator stroke.
     *
     * @return the current indicator stroke color.
     */
    public int getPointIndicatorStrokeColor() {
        return (int)this.getValue(POINT_INDICATOR_STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current point indicator stroke color.
     *
     * @param color the new color.
     */
    public void setPointIndicatorStrokeColor(int color) {
        this.setValue(POINT_INDICATOR_STROKE_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the point indicator color.
     *
     * @return the current point indicator color.
     */
    public int getPointIndicatorColor() {
        return (int)this.getValue(POINT_INDICATOR_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the point indicator color.
     *
     * @param color the new color.
     */
    public void setPointIndicatorColor(int color) {
        this.setValue(POINT_INDICATOR_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the width of the indicator stroke.
     *
     * @return the current indicator stroke width.
     */
    public float getPointIndicatorStrokeWidth() {
        return (float)getValue(POINT_INDICATOR_STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Sets the width of the indicator stroke.
     *
     * @param width the new indicator stroke width.
     */
    public void setPointIndicatorStrokeWidth(float width) {
        this.setValue(POINT_INDICATOR_STROKE_WIDTH_PROPERTY_KEY, width);
    }

    /**
     * Applies a given palette to the current indicator renderer instance.
     *
     * @param palette the palette to be applied.
     */
    public void applyPalette(ChartPalette palette) {
        if (palette == null)
            return;

        PaletteEntry entry = palette.getEntry(PALETTE_FAMILY, this.owner.getCollectionIndex());

        if (entry == null)
            return;

        this.setValue(POINT_INDICATOR_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
        this.setValue(POINT_INDICATOR_STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.setValue(POINT_INDICATOR_STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
    }

    /**
     * Used to force apply the current palette.
     */
    public void invalidatePalette() {
        ChartPalette palette = this.owner.getPalette();

        if (palette != null)
            this.applyPalette(palette);
    }

    /**
     * Adds a data point location which later will be used to render an indicator.
     *
     * @param x the x coordinate of the data point location.
     * @param y the y coordinate of the data point location.
     */
    public void addDataPointLocation(float x, float y) {
        addDataPointLocation(new Point((int) x, (int) y));
    }

    /**
     * Adds a data point location which later will be used to render an indicator.
     *
     * @param point point holding the coordinates for both x and y indicating the data point location.
     */
    public void addDataPointLocation(Point point) {
        this.dataPointLocations.add(point);
    }

    /**
     * Triggers the render process for drawing all the data point indicators.
     *
     * @param canvas the canvas to be used in rendering the data point indicators.
     */
    public void drawDataPointIndicators(Canvas canvas) {
        for (Point point : this.dataPointLocations)
            drawDataPointIndicator(canvas, point.x, point.y);
    }

    /**
     * Clears all the current data point locations.
     */
    public void clearDataPointLocations() {
        this.dataPointLocations.clear();
    }

    /**
     * This method is used for rendering a single data point indicator in whatever way described be the current inheritor.
     *
     * @param canvas the canvas to be used in rendering the data point indicator.
     * @param x      the x coordinate of the data point location.
     * @param y      the y coordinate of the data point location.
     */
    protected abstract void drawDataPointIndicator(Canvas canvas, float x, float y);
}
