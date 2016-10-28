package com.telerik.widget.chart.visualization.annotations.cartesian;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.custom.CartesianCustomAnnotationModel;
import com.telerik.widget.chart.engine.decorations.annotations.custom.CustomAnnotationRenderer;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.annotations.ChartAnnotationLabelUpdateContext;
import com.telerik.widget.chart.visualization.annotations.HorizontalAlignment;
import com.telerik.widget.chart.visualization.annotations.VerticalAlignment;
import com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView;
import com.telerik.widget.chart.visualization.common.CartesianAxis;
import com.telerik.widget.palettes.ChartPalette;

/**
 * Represents a custom annotation used in a {@link com.telerik.widget.chart.visualization.cartesianChart.RadCartesianChartView}.
 */
public class CartesianCustomAnnotation extends CartesianChartAnnotation {
    private CartesianCustomAnnotationModel model;
    private CartesianAxis horizontalAxis;
    private CartesianAxis verticalAxis;
    private Object horizontalValue;
    private Object verticalValue;
    private Object content;
    private double horizontalOffset;
    private double verticalOffset;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private CustomAnnotationRenderer defaultRenderer;
    private CustomAnnotationRenderer contentRenderer;
    private Paint contentPaint = new Paint();

    /**
     * Creates a new instance of the {@link CartesianCustomAnnotation} class with a specified
     * {@link Context}, a vertical and a horizontal axes, values for the vertical and horizontal axes,
     * value for the content of the annotation, styleable attributes and a default style.
     *
     * @param verticalAxis    the axis that will be used as vertical.
     * @param horizontalAxis  the axis that will be used as horizontal.
     * @param verticalValue   the value stating the position of the annotation along the vertical axis.
     * @param horizontalValue the value stating the position of the annotation along the horizontal axis.
     * @param content         the content to be displayed in the current annotation.
     */
    public CartesianCustomAnnotation(CartesianAxis verticalAxis, CartesianAxis horizontalAxis,
                                     Object verticalValue, Object horizontalValue, Object content) {
        super(verticalAxis);
        this.validateArguments(new Object[]{verticalAxis, horizontalAxis, verticalValue, horizontalValue});

        this.model = new CartesianCustomAnnotationModel();

        this.defaultRenderer = new CustomAnnotationRenderer() {
            @Override
            public RadSize measureContent(Object content) {
                if (content == null) {
                    return RadSize.getEmpty();
                }

                String text = content.toString();
                Rect textBounds = new Rect();
                contentPaint.getTextBounds(text, 0, text.length(), textBounds);

                return new RadSize(textBounds.width(), textBounds.height());
            }

            @Override
            public void render(Object content, RadRect layoutSlot, Canvas canvas, Paint paint) {
                if (content == null) {
                    return;
                }

                String text = content.toString();
                canvas.drawText(
                        text, (float) (layoutSlot.getX() - layoutSlot.getWidth() / 2.0),
                        (float) layoutSlot.getBottom(), paint);
            }
        };

        this.setHorizontalAxis(horizontalAxis);
        this.setVerticalAxis(verticalAxis);

        this.setVerticalValue(verticalValue);
        this.setHorizontalValue(horizontalValue);
        this.setContent(content);
    }

    private void validateArguments(Object[] objects) {
        for (Object argument : objects) {
            if (argument == null) {
                throw new IllegalArgumentException("verticalAxis, horizontalAxis, horizontalValue and verticalValue cannot be null.");
            }
        }
    }

    /**
     * Gets the current horizontal alignment.
     *
     * @return the current horizontal alignment.
     * @see HorizontalAlignment
     */
    public HorizontalAlignment getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    /**
     * Sets the current horizontal alignment.
     *
     * @param value the new horizontal alignment.
     * @see HorizontalAlignment
     */
    public void setHorizontalAlignment(HorizontalAlignment value) {
        this.horizontalAlignment = value;
    }

    /**
     * Gets the current vertical alignment.
     *
     * @return the current vertical alignment.
     * @see VerticalAlignment
     */
    public VerticalAlignment getVerticalAlignment() {
        return this.verticalAlignment;
    }

    /**
     * Sets the current vertical alignment.
     *
     * @param value the new vertical alignment.
     * @see VerticalAlignment
     */
    public void setVerticalAlignment(VerticalAlignment value) {
        this.verticalAlignment = value;
    }

    /**
     * Gets the current horizontal axis.
     *
     * @return the current horizontal axis.
     * @see CartesianAxis
     */
    public CartesianAxis getHorizontalAxis() {
        return this.horizontalAxis;
    }

    /**
     * Sets the current horizontal axis.
     *
     * @param value the new horizontal axis.
     * @see CartesianAxis
     */
    public void setHorizontalAxis(CartesianAxis value) {
        if (this.horizontalAxis == value) {
            return;
        }

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        this.horizontalAxis = value;
        this.model.setFirstAxis(this.getHorizontalAxis().getModel());
    }

    /**
     * Gets the current vertical axis.
     *
     * @return the current vertical axis.
     * @see CartesianAxis
     */
    public CartesianAxis getVerticalAxis() {
        return this.verticalAxis;
    }

    /**
     * Sets the current vertical axis.
     *
     * @param value the new vertical axis.
     * @see CartesianAxis
     */
    public void setVerticalAxis(CartesianAxis value) {
        if (this.verticalAxis == value) {
            return;
        }

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        this.verticalAxis = value;
        this.model.setSecondAxis(this.getVerticalAxis().getModel());
    }

    /**
     * Gets the current horizontal value.
     *
     * @return the current horizontal value.
     */
    public Object getHorizontalValue() {
        return this.horizontalValue;
    }

    /**
     * Sets the current horizontal value.
     *
     * @param value the new horizontal value.
     */
    public void setHorizontalValue(Object value) {
        if (this.horizontalValue == value) {
            return;
        }

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        this.horizontalValue = value;
        this.model.setFirstValue(value);
    }

    /**
     * Gets the current vertical value.
     *
     * @return the current vertical value.
     */
    public Object getVerticalValue() {
        return this.verticalValue;
    }

    /**
     * Sets the current vertical value.
     *
     * @param value the new vertical value.
     */
    public void setVerticalValue(Object value) {
        if (this.verticalValue == value) {
            return;
        }

        if (value == null)
            throw new IllegalArgumentException("value cannot be null");

        this.verticalValue = value;
        this.model.setSecondValue(value);
    }

    /**
     * Gets the current content.
     *
     * @return the current content.
     */
    public Object getContent() {
        return this.content;
    }

    /**
     * Sets the current content.
     *
     * @param value the new content.
     */
    public void setContent(Object value) {
        this.content = value;
        this.invalidateContentTemplate();
    }

    /**
     * Gets the current horizontal offset.
     *
     * @return the current horizontal offset.
     */
    public double getHorizontalOffset() {
        return this.horizontalOffset;
    }

    /**
     * Sets the current horizontal offset.
     *
     * @param value the new horizontal offset.
     */
    public void setHorizontalOffset(double value) {
        this.horizontalOffset = value;
    }

    /**
     * Gets the current vertical offset.
     *
     * @return the current vertical offset.
     */
    public double getVerticalOffset() {
        return this.verticalOffset;
    }

    /**
     * Sets the current vertical offset.
     *
     * @param value the new vertical offset.
     */
    public void setVerticalOffset(double value) {
        this.verticalOffset = value;
    }

    @Override
    public ChartAnnotationModel getModel() {
        return this.model;
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.CARTESIAN_CUSTOM_ANNOTATION;
    }

    @Override
    protected RadSize measureNodeOverride(ChartNode node, Object content) {
        CustomAnnotationRenderer renderer = this.contentRenderer != null ? this.contentRenderer : this.defaultRenderer;

        return renderer.measureContent(this.content);
    }

    @Override
    protected void drawCore(Canvas canvas) {
        CustomAnnotationRenderer renderer = this.contentRenderer != null ? this.contentRenderer : this.defaultRenderer;
        renderer.render(this.content, this.model.getLayoutSlot(), canvas, this.contentPaint);
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        RadCartesianChartView cartesianChart = (RadCartesianChartView) this.getChart();

        if (cartesianChart.getHorizontalAxis() != null) {
            this.model.setFirstAxis(cartesianChart.getHorizontalAxis().getModel());
        }
        if (cartesianChart.getVerticalAxis() != null) {
            this.model.setSecondAxis(cartesianChart.getVerticalAxis().getModel());
        }
    }

    private void invalidateContentTemplate() {
        this.model.desiredSize = RadSize.getInvalid();
        this.requestRender();
    }

    public CustomAnnotationRenderer getContentRenderer() {
        return this.contentRenderer;
    }

    public void setContentRenderer(CustomAnnotationRenderer contentRenderer) {
        this.contentRenderer = contentRenderer;
    }
}
