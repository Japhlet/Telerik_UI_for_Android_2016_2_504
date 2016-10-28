package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.telerik.android.common.Function;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.axes.AxisLabelModel;
import com.telerik.widget.chart.engine.axes.common.AxisLabelFitMode;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.chart.visualization.common.LabelSizeInfo;

public class CartesianAxisLabelRenderer implements ChartLabelRenderer {
    private int labelColor = Color.BLACK;
    private float labelSize = 15;
    private int fontStyle = 0;
    private Typeface labelTypeface;
    protected TextPaint labelPaint;
    protected CartesianAxis axis;

    public CartesianAxisLabelRenderer(CartesianAxis axis) {
        this.axis = axis;

        this.labelPaint = new TextPaint();
        this.labelPaint.setAntiAlias(true);
        this.labelPaint.setColor(this.labelColor);
        this.labelPaint.setTextSize(this.labelSize);
        Typeface tf = Typeface.create(this.labelTypeface, this.fontStyle);
        this.labelPaint.setTypeface(tf);
    }

    @Override
    public Typeface getLabelFont() {
        return this.labelTypeface;
    }

    @Override
    public void setLabelFont(Typeface value) {
        if (this.labelTypeface == value) {
            return;
        }

        if (this.labelTypeface != null && this.labelTypeface.equals(value)) {
            return;
        }

        this.labelTypeface = value;
        this.labelPaint.setTypeface(value);
        this.axis.requestLayout();
    }

    @Override
    public float getLabelMargin() {
        return this.axis.getModel().getLabelMargin();
    }

    @Override
    public void setLabelMargin(float value) {
        if (value < 0)
            throw new IllegalArgumentException("value cannot be negative");

        this.axis.getModel().setLabelMargin(value);
        this.axis.requestLayout();
    }

    @Override
    public int getLabelTextColor() {
        return this.labelColor;
    }

    @Override
    public void setLabelTextColor(int value) {
        this.labelColor = value;
        this.labelPaint.setColor(value);
        this.axis.requestRender();
    }

    @Override
    public int getLabelFontStyle() {
        return this.fontStyle;
    }

    @Override
    public void setLabelFontStyle(int value) {
        if (this.fontStyle == value) {
            return;
        }

        this.fontStyle = value;
        this.labelPaint.setTypeface(Typeface.create(this.labelTypeface, this.fontStyle));
        this.axis.requestLayout();
    }

    @Override
    public float getLabelSize() {
        return this.labelSize;
    }

    @Override
    public void setLabelSize(float value) {
        if (value <= 0) {
            throw new IllegalArgumentException("The label size cannot be a negative value or zero.");
        }

        this.labelSize = value;
        this.labelPaint.setTextSize(value);
        this.axis.requestLayout();
    }

    @Override
    public Function<Object, String> getLabelValueToStringConverter() {
        return this.axis.getModel().getLabelValueToStringConverter();
    }

    @Override
    public void setLabelValueToStringConverter(Function<Object, String> converter) {
        this.axis.getModel().setLabelValueToStringConverter(converter);
        this.axis.requestLayout();
    }

    @Override
    public String getLabelFormat() {
        return this.axis.getModel().getLabelFormat();
    }

    @Override
    public void setLabelFormat(String value) {
        this.axis.getModel().setLabelFormat(value);
        this.axis.requestLayout();
    }

    @Override
    public void renderLabel(Canvas canvas, ChartNode dataPoint) {
        AxisLabelModel labelModel = (AxisLabelModel) dataPoint;
        RadRect layoutSlot = this.axis.getLayoutSlot(dataPoint, this.axis.getLastLayoutContext());

        String labelContent = String.valueOf(labelModel.getContent());
        AxisLabelFitMode fitMode = this.axis.getLabelFitMode();

        if (fitMode == AxisLabelFitMode.ROTATE) {
            this.renderLabelRotate(canvas, layoutSlot, labelContent, labelModel);
        } else if (fitMode == AxisLabelFitMode.MULTI_LINE) {
            this.renderLabelMultiLine(canvas, layoutSlot, labelContent, labelModel);
        } else {
            this.renderLabelNoFitMode(canvas, layoutSlot, labelContent, labelModel);
        }
    }

    protected void renderLabelNoFitMode(Canvas canvas, RadRect layoutSlot, String labelContent, AxisLabelModel labelModel) {
        StaticLayout textInfo = labelModel.getLabelSizeInfo().textLayout;
        float x = (float) layoutSlot.getX();
        float y = (float) layoutSlot.getY() + textInfo.getLineBaseline(0);
        canvas.drawText(labelContent, x, y, labelPaint);
    }

    protected void renderLabelMultiLine(Canvas canvas, RadRect layoutSlot, String labelContent, AxisLabelModel labelModel) {
        this.renderLabelNoFitMode(canvas, layoutSlot, labelContent, labelModel);
    }

    protected void renderLabelRotate(Canvas canvas, RadRect layoutSlot, String labelContent, AxisLabelModel labelModel) {
        canvas.save();

        RadPoint center = layoutSlot.getCenter();
        canvas.rotate(this.axis.getLabelRotationAngle(),
                (float) center.getX(),
                (float) center.getY());
        canvas.drawText(labelContent,
                (float) (center.getX() - labelModel.untransformedDesiredSize().halfWidth()),
                (float) (center.getY() + labelModel.untransformedDesiredSize().halfHeight()),
                labelPaint);
        canvas.restore();
    }

    private LabelSizeInfo getLabelSizeInfo(Object content) {
        String label = "";
        if (content != null) {
            label = content.toString();
        }

        float width = this.labelPaint.measureText(label, 0, label.length());

        StaticLayout textLayout = new StaticLayout(label,
                0,
                label.length(),
                this.labelPaint,
                Math.round(width),
                Layout.Alignment.ALIGN_CENTER,
                1.0f,
                1.0f,
                false);

        LabelSizeInfo result = new LabelSizeInfo();
        result.untransformedSize = new RadSize(textLayout.getWidth(), textLayout.getHeight());

        if (this.axis.getLabelFitMode() == AxisLabelFitMode.ROTATE) {
            RadSize boundingBox = RadMath.getRotatedSize(result.untransformedSize, this.axis.getLabelRotationAngle() * RadMath.DEG_TO_RAD_FACTOR);
            result.size = new RadSize(boundingBox.getWidth(), boundingBox.getHeight());
        } else {
            result.size = result.untransformedSize;
        }

        result.textLayout = textLayout;
        return result;
    }

    public RadSize measureLabel(AxisLabelModel label, Object content) {
        LabelSizeInfo labelSize = this.getLabelSizeInfo(content);
        label.update(labelSize);
        return labelSize.size;
    }
}
