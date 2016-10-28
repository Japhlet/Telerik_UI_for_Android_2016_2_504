package com.telerik.widget.chart.visualization.behaviors.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.primitives.widget.tooltip.contracts.DrawListener;
import com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior;
import com.telerik.widget.chart.visualization.behaviors.ChartPanZoomMode;
import com.telerik.widget.chart.visualization.behaviors.ChartZoomStrategy;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * The DeferredZoomPresenter renders the zoom rectangle when the {@link com.telerik.widget.chart.visualization.behaviors.ChartZoomStrategy} of the
 * {@link com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior} is set to DEFERRED. It also calculates the new zoom and pan based
 * on the zoom rectangle.
 */
public class DeferredZoomPresenter extends PropertyManager implements DrawListener {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DeferredZoomPresenter presenter = (DeferredZoomPresenter)sender;

            presenter.fillPaint.setColor(presenter.getFillColor());
        }
    });

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DeferredZoomPresenter presenter = (DeferredZoomPresenter)sender;

            presenter.strokePaint.setColor(presenter.getStrokeColor());
        }
    });

    public static final int STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            DeferredZoomPresenter presenter = (DeferredZoomPresenter)sender;

            presenter.strokePaint.setStrokeWidth(presenter.getStrokeWidth());
        }
    });

    private ChartPanAndZoomBehavior owner;
    private RadPoint p1 = RadPoint.getEmpty();
    private RadPoint p2 = RadPoint.getEmpty();
    private double zoomX = 1;
    private double zoomY = 1;
    private int panX;
    private int panY;
    private Paint strokePaint = new Paint();
    private Paint fillPaint = new Paint();
    private boolean canApplyPalette = true;

    /**
     * Creates a new instance of the {@link com.telerik.widget.chart.visualization.behaviors.views.DeferredZoomPresenter} class.
     */
    public DeferredZoomPresenter() {
        this.strokePaint.setColor(Color.parseColor("#000000"));
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setStrokeWidth(Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1));
        this.fillPaint.setColor(Color.parseColor("#B2FF0000"));
    }

    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    public float getStrokeWidth() {
        return (float)this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    public void setStrokeWidth(float value) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, value);
    }

    /**
     * Gets a value that determines if a palette can be applied.
     */
    @SuppressWarnings("unused")
    public boolean getCanApplyPalette() {
        return this.canApplyPalette;
    }

    /**
     * Sets a value that determines if a palette can be applied.
     */
    @SuppressWarnings("unused")
    public void setCanApplyPalette(boolean value) {
        this.canApplyPalette = value;
    }

    /**
     * Gets the fill paint;
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint;
     */
    public void setFillPaint(Paint value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.fillPaint = value;
    }

    /**
     * Gets the stroke paint;
     */
    public Paint getStrokePaint() {
        return this.strokePaint;
    }

    /**
     * Sets the stroke paint;
     */
    public void setStrokePaint(Paint value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        this.strokePaint = value;
    }

    /**
     * Sets the {@link com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior} owner;
     */
    public void setOwner(ChartPanAndZoomBehavior value) {
        this.owner = value;
    }

    /**
     * Gets the {@link com.telerik.widget.chart.visualization.behaviors.ChartPanAndZoomBehavior} owner;
     */
    public ChartPanAndZoomBehavior getOwner() {
        return this.owner;
    }

    /**
     * Applies the provided palette if the canApplyPalette field is true.
     */
    public void applyPalette(ChartPalette palette) {
        if (!this.canApplyPalette) {
            return;
        }

        if (palette == null) {
            return;
        }

        PaletteEntry entry = palette.getEntry("DeferredZoomPresenter");
        if (entry == null) {
            return;
        }

        this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
    }

    @Override
    public void notifyDraw(Canvas canvas) {
        if (this.owner == null ||
                !this.owner.isPinching() ||
                this.owner.getZoomStrategy() == ChartZoomStrategy.IMMEDIATE) {
            return;
        }

        RadChartViewBase chart = this.owner.chart();
        RadRect clip = chart.getPlotAreaClip();

        double left = Math.min(p1.getX(), p2.getX());
        if (left < clip.getX()) {
            left = clip.getX();
        }

        double top = Math.min(p1.getY(), p2.getY());
        if (top < clip.getY()) {
            top = clip.getY();
        }

        double right = Math.max(p1.getX(), p2.getX());
        if (right > clip.getRight()) {
            right = clip.getRight();
        }

        double bottom = Math.max(p1.getY(), p2.getY());
        if (bottom > clip.getBottom()) {
            bottom = clip.getBottom();
        }

        if (this.owner.getZoomMode() == ChartPanZoomMode.HORIZONTAL) {
            top = clip.getY();
            bottom = clip.getBottom();
        } else if (this.owner.getZoomMode() == ChartPanZoomMode.VERTICAL) {
            left = clip.getX();
            right = clip.getRight();
        }

        this.zoomX = 0;
        this.zoomY = 0;
        this.panX = 0;
        this.panY = 0;

        double zoomedWidth = clip.getWidth() * chart.getZoomWidth();
        double zoomedHeight = clip.getHeight() * chart.getZoomHeight();
        double normalizedX = ((left - clip.getX()) - chart.getPanOffsetX()) / zoomedWidth;
        double normalizedY = ((top - clip.getY()) - chart.getPanOffsetY()) / zoomedHeight;

        this.zoomX = chart.getZoomWidth() * (1 / (Math.abs(right - left) / clip.getWidth()));
        this.zoomY = chart.getZoomWidth() * (1 / (Math.abs(bottom - top) / clip.getHeight()));
        zoomedWidth = clip.getWidth() * this.zoomX;
        zoomedHeight = clip.getHeight() * this.zoomY;

        this.panX = -(int) Math.round(zoomedWidth * normalizedX);
        this.panY = -(int) Math.round(zoomedHeight * normalizedY);

        canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.fillPaint);
        canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, this.strokePaint);
    }

    /**
     * Sets the pinch points that determine the top left and bottom right of the zoom rectangle.
     * The order of the arguments does not matter, the presenter determines which is top left and which
     * is bottom right.
     * The pinch points should be set before each the draw pass.
     *
     * @param p1 The first corner point.
     * @param p2 The second corner point.
     */
    public void setPinchPoints(RadPoint p1, RadPoint p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Gets the horizontal pan resulting from the last pinch gesture.
     */
    public int getPanX() {
        return this.panX;
    }

    /**
     * Gets the vertical pan resulting from the last pinch gesture.
     */
    public int getPanY() {
        return this.panY;
    }

    /**
     * Gets the horizontal zoom resulting from the last pinch gesture.
     */
    public double getZoomX() {
        return this.zoomX;
    }

    /**
     * Gets the vertical zoom resulting from the last pinch gesture.
     */
    public double getZoomY() {
        return this.zoomY;
    }
}
