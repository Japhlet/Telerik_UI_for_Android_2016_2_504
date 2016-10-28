package com.telerik.widget.chart.visualization.annotations.cartesian;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathEffect;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Represents an {@link CartesianChartAnnotation} which shape may be stroked (outlined).
 */
public abstract class CartesianStrokedAnnotation extends CartesianChartAnnotation {

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianStrokedAnnotation csa = (CartesianStrokedAnnotation)sender;
            csa.strokePaint.setColor(csa.getStrokeColor());

            csa.requestRender();
        }
    });

    public static final int STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianStrokedAnnotation csa = (CartesianStrokedAnnotation)sender;
            csa.strokePaint.setStrokeWidth(csa.getStrokeWidth());

            csa.requestRender();
        }
    });

    /**
     * The paint that will be used when drawing the stroke.
     */
    protected final Paint strokePaint = new Paint();

    /**
     * Creates an instance of the {@link CartesianStrokedAnnotation} using a specified {@link Context}.
     */
    public CartesianStrokedAnnotation(CartesianAxis axis) {
        super(axis);

        this.strokePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * Gets the current stroke color id that specifies how the shape outline is painted.
     *
     * @return the current stroke color id.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current stroke color id that specifies how the shape outline is painted.
     *
     * @param value the new stroke color id.
     */
    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    /**
     * Gets the current width of the shape stroke outline.
     *
     * @return the current stroke thickness.
     */
    public float getStrokeWidth() {
        return (float)this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Sets the current width of the shape stroke outline. If you want to make your stroke disappear
     * use either a transparent paint (recommended) or set it to something really low like 0.0000001.
     *
     * @param value the new stroke thickness.
     */
    public void setStrokeWidth(float value) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, value);
    }

    /**
     * Gets the current effect path.
     *
     * @return the current effect path.
     * @see PathEffect
     */
    public PathEffect getStrokeEffect() {
        return this.strokePaint.getPathEffect();
    }

    /**
     * Sets the current effect path.
     *
     * @param value the new effect path.
     */
    public void setStrokeEffect(PathEffect value) {
        this.strokePaint.setPathEffect(value);
        this.requestRender();
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.CARTESIAN_STROKED_ANNOTATION;
    }

    /**
     * Gets a value indicating whether the stroke goes inwards by the full stroke thickness.
     *
     * @return <code>true</code> if this instance is stroke inset and <code>false</code> otherwise.
     */
    protected boolean isStrokeInset() {
        return false;
    }

    @Override
    protected void processPaletteEntry(PaletteEntry entry) {
        super.processPaletteEntry(entry);

        this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
    }
}
