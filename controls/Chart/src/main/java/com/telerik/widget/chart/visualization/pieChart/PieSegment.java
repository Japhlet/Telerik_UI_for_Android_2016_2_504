package com.telerik.widget.chart.visualization.pieChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.View;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.palettes.PaletteEntry;
import com.telerik.widget.primitives.legend.LegendItem;

import java.security.InvalidParameterException;

class PieSegment extends PropertyManager {

    static final float SEGMENT_MAX_ANGLE = 360;

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            PieSegment segment = (PieSegment)sender;
            segment.fillPaint.setColor(segment.getFillColor());

            segment.series.requestRender();
        }
    });
    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            PieSegment segment = (PieSegment)sender;
            segment.strokePaint.setColor(segment.getStrokeColor());
            segment.series.requestRender();
        }
    });
    public static final int STROKE_THICKNESS_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            PieSegment segment = (PieSegment)sender;
            segment.strokePaint.setStrokeWidth(segment.getStrokeThickness());
            segment.series.requestRender();
        }
    });
    public static final int ARC_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            PieSegment segment = (PieSegment)sender;
            segment.arcPaint.setColor(segment.getArcColor());
            segment.series.requestRender();
        }
    });
    public static final int ARC_THICKNESS_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            PieSegment segment = (PieSegment)sender;
            segment.arcPaint.setStrokeWidth(segment.getArcThickness());
            segment.series.requestRender();
        }
    });

    protected Paint fillPaint;
    protected Paint arcPaint;
    protected Paint strokePaint;

    protected Path fillPath;
    protected Path arcPath;
    protected Path strokePath;

    PieDataPoint point;
    private LegendItem legendItem = new LegendItem();
    protected PieSeries series;

    private RadPoint centerOffset = new RadPoint();
    protected RadPoint center;

    private int visibility;
    private boolean isVisibleInLegend;

    PieSegment(PieSeries series) {
        this.series = series;
        if (series == null) {
            throw new InvalidParameterException("Series can't be null.");
        }

        this.fillPath = new Path();
        this.arcPath = new Path();
        this.strokePath = new Path();

        this.fillPaint = new Paint();
        this.fillPaint.setAntiAlias(true);

        this.strokePaint = new Paint();
        this.strokePaint.setAntiAlias(true);
        this.strokePaint.setStyle(Paint.Style.STROKE);
        this.strokePaint.setColor(Color.TRANSPARENT);

        this.arcPaint = new Paint();
        this.arcPaint.setAntiAlias(true);
        this.arcPaint.setStyle(Paint.Style.STROKE);
        this.arcPaint.setColor(Color.TRANSPARENT);

        this.legendItem.setFillColor(this.fillPaint.getColor());
        this.legendItem.setStrokeColor(this.strokePaint.getColor());
    }

    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    public void setStrokeColor(int value) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, value);
    }

    public float getStrokeThickness() {
        return (float)this.getValue(STROKE_THICKNESS_PROPERTY_KEY);
    }

    public void setStrokeThickness(float value) {
        this.setValue(STROKE_THICKNESS_PROPERTY_KEY, value);
    }

    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    public int getArcColor() {
        return (int)this.getValue(ARC_COLOR_PROPERTY_KEY);
    }

    public void setArcColor(int value) {
        this.setValue(ARC_COLOR_PROPERTY_KEY, value);
    }

    public float getArcThickness() {
        return (float)this.getValue(ARC_THICKNESS_PROPERTY_KEY);
    }

    public void setArcThickness(float value) {
        this.setValue(ARC_THICKNESS_PROPERTY_KEY, value);
    }

    public RadPoint getLocation() {
        return this.center;
    }

    LegendItem getLegendItem() {
        return this.legendItem;
    }

    boolean getIsVisibleInLegend() {
        return this.isVisibleInLegend;
    }

    public boolean hitTest(PointF touchLocation) {

        RectF rect = new RectF();
        this.fillPath.computeBounds(rect, true);

        Region region = new Region();
        region.setPath(this.fillPath, new Region(Math.round(rect.left), Math.round(rect.top), Math.round(rect.right), Math.round(rect.bottom)));

        return region.contains(Math.round(touchLocation.x), Math.round(touchLocation.y));
    }

    void setIsVisibleInLegend(boolean value) {
        if (this.isVisibleInLegend == value) {
            return;
        }

        this.isVisibleInLegend = value;

        this.onIsVisibleInLegendChanged(value);
    }

    int getVisibility() {
        return this.visibility;
    }

    void setVisibility(int value) {
        if (value != View.VISIBLE && value != View.INVISIBLE && value != View.GONE) {
            throw new IllegalArgumentException("Visibility should be set to one of the following View.VISIBLE, View.INVISIBLE or View.GONE");
        }
        this.visibility = value;
    }

    void applySliceStyle(SliceStyle style) {
        this.updateFillColor(style.getFillColor());
        this.updateStrokeColor(style.getStrokeColor());
        this.updateStrokeWidth(style.getStrokeWidth());
        this.updateArcColor(style.getArcColor());
        this.updateArcWidth(style.getArcWidth());
    }

    void updatePaths(PieDataPoint point, PieUpdateContext context) {
        RadPoint centerPoint = context.center;

        if (point.getRelativeOffsetFromCenter() > 0) {

            double offsetInPixels = (int) (context.radius * point.getRelativeOffsetFromCenter());
            double middleAngle = point.startAngle() + (point.sweepAngle() / 2);

            RadPoint offset = context.getCenterWithOffset(offsetInPixels, middleAngle);
            double x = offset.getX() - centerPoint.getX();
            double y = offset.getY() - centerPoint.getY();

            this.centerOffset = new RadPoint(x, y);
        }

        double strokeWidth = this.strokePaint.getStrokeWidth();
        double strokePadding = strokeWidth / 2;
        double arcWidth = this.arcPaint.getStrokeWidth();
        double arcPadding = strokeWidth + arcWidth / 2;

        double sweepAngle = point.sweepAngle();
        double startAngle = point.startAngle();
        double middleAngle = startAngle + (sweepAngle / 2);

        double left = centerPoint.getX() - context.radius;
        double top = centerPoint.getY() - context.radius;
        double right = left + context.diameter;
        double bottom = top + context.diameter;

        RectF fillOval = new RectF((float) left, (float) top, (float) right, (float) bottom);
        RectF strokeOval = new RectF((float) (left + strokePadding), (float) (top + strokePadding), (float) (right - strokePadding), (float) (bottom - strokePadding));
        RectF arcOval = new RectF((float) (left + arcPadding), (float) (top + arcPadding), (float) (right - arcPadding), (float) (bottom - arcPadding));

        double sliceOffset = sweepAngle == 360 ? 0 : this.series.getSliceOffset();
        double halfAngleInRadians = Math.toRadians(sweepAngle / 2);
        double offsetForCenter = (sliceOffset / 2 + strokeWidth / 2) / Math.abs(Math.sin(halfAngleInRadians));
        RadPoint centerPointWithOffset = context.getCenterWithOffset(offsetForCenter, middleAngle);

        double offsetSweepAngle = this.getAngleWithOffset(sweepAngle, sliceOffset + strokeWidth, (float) context.radius);
        double angleDifference = sweepAngle - offsetSweepAngle;
        double offsetStartAngle = startAngle + angleDifference / 2;

        this.fillPath.reset();
        this.fillPath.addArc(fillOval, (float) offsetStartAngle, (float) offsetSweepAngle);

        if (this.point.normalizedValue() < 1.0)
            this.fillPath.lineTo((float) centerPointWithOffset.getX(), (float) centerPointWithOffset.getY());

        this.fillPath.close();

        this.strokePath.reset();
        this.strokePath.addArc(strokeOval, (float) offsetStartAngle, (float) offsetSweepAngle);

        if (sweepAngle < SEGMENT_MAX_ANGLE)
            this.strokePath.lineTo((float) centerPointWithOffset.getX(), (float) centerPointWithOffset.getY());

        this.strokePath.close();

        this.arcPath.reset();
        this.arcPath.addArc(arcOval, (float) offsetStartAngle, (float) offsetSweepAngle);

        this.center = RadMath.getArcPoint(offsetStartAngle + offsetSweepAngle / 2, context.center, context.radius / 2);
    }

    void applyPaletteStyle(PaletteEntry entry) {
        if (entry == null) {
            return;
        }

        this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
        this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.setValue(STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        this.setValue(ARC_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getAdditionalStroke());
        this.setValue(ARC_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, Float.parseFloat(entry.getCustomValue(PieSeries.ARC_STROKE_WIDTH_KEY, 0.0f)));
    }

    void draw(Canvas canvas) {
        canvas.save();
        canvas.translate((float) this.centerOffset.getX(), (float) this.centerOffset.getY());
        canvas.drawPath(this.fillPath, this.fillPaint);
        canvas.drawPath(this.arcPath, this.arcPaint);
        canvas.drawPath(this.strokePath, this.strokePaint);
        canvas.restore();
    }

    double getAngleWithOffset(double originalAngle, double offset, double radius) {
        double result = 2 * Math.PI * radius * originalAngle - SEGMENT_MAX_ANGLE * offset;
        result /= 2 * Math.PI * radius;
        return result;
    }

    private void updateStrokeWidth(float width) {
        this.strokePaint.setStrokeWidth(width);
    }

    private void updateArcWidth(float width) {
        this.arcPaint.setStrokeWidth(width);
    }

    private void updateFillColor(int value) {
        this.fillPaint.setColor(value);
        this.legendItem.setFillColor(value);
    }

    private void updateArcColor(int value) {
        this.arcPaint.setColor(value);
    }

    private void updateStrokeColor(int value) {
        this.strokePaint.setColor(value);
        this.legendItem.setStrokeColor(value);
    }

    private void onIsVisibleInLegendChanged(boolean value) {
        if (value) {
            this.series.getChart().getLegendInfos().add(this.legendItem);
        } else {
            this.series.getChart().getLegendInfos().remove(this.legendItem);
        }
    }
}