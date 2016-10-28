package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;
import android.graphics.Typeface;

import com.telerik.android.common.Function;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;

public interface ChartLabelRenderer {
    /**
     * Draws label of the given {@link DataPoint} on the given {@link Canvas}.
     *
     * @param canvas    The canvas to draw on.
     * @param dataPoint The {@link ChartNode} that is related to the label. For a series it will be a {@link DataPoint}. For an {@link com.telerik.widget.chart.visualization.common.Axis}
     *                  it will be an {@link com.telerik.widget.chart.engine.axes.AxisTickModel}.
     */
    void renderLabel(Canvas canvas, ChartNode dataPoint);

    /**
     * Gets the labels color.
     *
     * @return The labels color.
     */
    public int getLabelTextColor();

    /**
     * Sets the labels color.
     *
     * @param color The labels color.
     */
    public void setLabelTextColor(int color);

    /**
     * Gets the label size.
     *
     * @return The label size.
     */
    public float getLabelSize();

    /**
     * Sets the label size.
     *
     * @param value The label size.
     */
    public void setLabelSize(float value);

    /**
     * Gets the labels {@link android.graphics.Typeface}
     *
     * @return The labels typeface.
     */
    Typeface getLabelFont();

    /**
     * Sets the labels {@link android.graphics.Typeface}.
     *
     * @param value The new labels typeface.
     */
    void setLabelFont(Typeface value);

    /**
     * Gets the labels font style.
     *
     * @return The labels font style.
     */
    public int getLabelFontStyle();

    /**
     * Sets the labels font style.
     *
     * @param value The new labels font style.
     */
    public void setLabelFontStyle(int value);

    /**
     * Gets the label format string.
     *
     * @return The label format string.
     */
    public String getLabelFormat();

    /**
     * Sets the label format string.
     *
     * @param format The label format string. The format string is a standard Java format string.
     * For more information see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html">the java formatter help page</a>.
     */
    public void setLabelFormat(String format);

    /**
     * Gets the margin of the labels.
     *
     * @return The labels margin.
     */
    public float getLabelMargin();

    /**
     * Sets the margin of the labels.
     *
     * @param offset The new labels margin.
     */
    public void setLabelMargin(float offset);

    public Function<Object, String> getLabelValueToStringConverter();

    public void setLabelValueToStringConverter(Function<Object, String> converter);
}
