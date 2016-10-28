package com.telerik.widget.chart.visualization.pieChart;

/**
 * The SliceStyle class allows easier customization of the way
 * RadPieChartView visualizes its series.
 * <p/>
 * Each of the slices in the pie can be customized by one SliceStyle.
 * The customization options include:
 * <ul>
 * <li>The color of fill</li>
 * <li>The color of the stroke</li>
 * <li>The width of the stroke</li>
 * <li>The color of the arc, which is used as inner stroke in the pie slice in its arc part</li>
 * <li>The width of the arc, which is used as inner stroke in the pie slice in its arc part</li>
 * </ul>
 *
 * @see RadPieChartView
 * @see PieSeries
 */
public class SliceStyle {

    private int fillColor;
    private int strokeColor;
    private float strokeWidth;
    private int arcColor;
    private float arcWidth;

    /**
     * Gets the value of the color that will be used to fill a slice of pie.
     *
     * @return the color that will be used as fill
     */
    public int getFillColor() {
        return this.fillColor;
    }

    /**
     * Registers the color that will be used for the fill of a slice of pie.
     *
     * @param fillColor the value of the fill color
     */
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Gets the value of the color that will be used for the stroke a slice of pie.
     *
     * @return the color that will be used as stroke
     */
    public int getStrokeColor() {
        return this.strokeColor;
    }

    /**
     * Registers the color that will be used for the stroke of a slice of pie.
     *
     * @param strokeColor the value of the stroke color
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    /**
     * Gets the value of the width of the stroke that will be drawn around a slice of pie.
     * The width can be <code>0</code>, but then the stroke will not be visible.
     *
     * @return the width of the stroke line around the pie slice
     */
    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    /**
     * Registers the width of the stroke that will be drawn around a slice of pie.
     * If the width is <code>0</code>, the stroke will not be visible.
     *
     * @param strokeWidth the value of the stroke width
     */
    public void setStrokeWidth(float strokeWidth) {
        if (strokeWidth < 0)
            throw new IllegalArgumentException("strokeWidth cannot be negative");

        this.strokeWidth = strokeWidth;
    }

    /**
     * Gets the value of the color that will be used for the arc that
     * will be drawn around a slice of pie.
     * The arc is a stroke that is drawn only for the curved part of the slice.
     * If both arc and stroke are visible the arc is drawn inside the stroke.
     *
     * @return the color that will be used to draw an arc
     */
    public int getArcColor() {
        return this.arcColor;
    }

    /**
     * Registers the color that will be used for the arc that
     * will be drawn around a slice of pie.
     * The arc is a stroke that is drawn only for the curved part of the slice.
     * If both arc and stroke are visible the arc is drawn inside the stroke.
     *
     * @param arcColor the value of the arc color
     */
    public void setArcColor(int arcColor) {
        this.arcColor = arcColor;
    }

    /**
     * Gets the value of the width of the arc that will be drawn around a slice of pie.
     * The arc is a stroke that is drawn only for the curved part of the slice.
     * The value of the width can be <code>0</code>, then the arc will not be visible.
     * If both arc and stroke are visible the arc is drawn inside the stroke.
     *
     * @return the width of the stroke line around the arc of a pie slice
     */
    public float getArcWidth() {
        return this.arcWidth;
    }

    /**
     * Registers the width of the arc that will be drawn around a slice of pie.
     * The arc is a stroke that is drawn only for the curved part of the slice.
     * If the width is <code>0</code>, the arc will not be visible.
     * If both arc and stroke are visible the arc is drawn inside the stroke.
     *
     * @param arcWidth the value of the arc width
     */
    public void setArcWidth(float arcWidth) {
        if (arcWidth <= 0)
            throw new IllegalArgumentException("arcWidth cannot be negative or zero" +
                    (arcWidth == 0 ? ". If you want to hide the arc use TRANSPARENT color instead(RECOMMENDED) or set it to something really low" : ""));
        this.arcWidth = arcWidth;
    }
}
