package com.telerik.widget.chart.visualization.common.renderers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;

import com.telerik.android.common.Function;
import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.common.ChartSeries;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Base class for all chart series renderer classes.
 */
public abstract class BaseLabelRenderer extends PropertyManager implements ChartLabelRenderer {

    public static int LABEL_FILL_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BaseLabelRenderer renderer = (BaseLabelRenderer)sender;

            int color = (int)propertyValue;
            if (renderer.labelFillColor == color)
                return;

            renderer.labelFillColor = color;
            renderer.getLabelFillPaint(0).setColor(color);

        }
    });

    public static int LABEL_STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BaseLabelRenderer renderer = (BaseLabelRenderer)sender;

            int color = (int)propertyValue;
            if (renderer.labelStrokeColor == color)
                return;

            renderer.labelStrokeColor = color;
            renderer.labelStrokePaint.setColor(color);
        }
    });

    public static int LABEL_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            BaseLabelRenderer renderer = (BaseLabelRenderer)sender;
            int color = (int)propertyValue;

            if (renderer.labelTextColor == color)
                return;

            renderer.labelTextColor = color;
            renderer.labelTextPaint.setColor(color);
        }
    });

    /**
     * The name of the palette family that will be associated when applying palettes to the current renderer instance.
     */
    protected static final String PALETTE_FAMILY = "SeriesLabels";

    /**
     * The string format that will be used in rendering the text of the labels.
     */
    protected String labelFormat = "%s";

    /**
     * The distance between the label and the data point associated with it.
     */
    protected float labelMargin;

    /**
     * The color of the label text.
     */
    protected int labelTextColor = Color.WHITE;

    /**
     * The color of the label background fill.
     */
    protected int labelFillColor;

    /**
     * The color of the label background stroke.
     */
    protected int labelStrokeColor;

    /**
     * The width of the label background width.
     */
    protected float labelStrokeWidth;

    /**
     * The distance between the label text and the top part of the label background.
     */
    protected float labelPaddingTop;

    /**
     * The distance between the label text and the bottom part of the label background.
     */
    protected float labelPaddingBottom;

    /**
     * The distance between the label text and the left part of the label background.
     */
    protected float labelPaddingLeft;

    /**
     * The distance between the label text and the right part of the label background.
     */
    protected float labelPaddingRight;

    /**
     * The font style to be used when rendering the label text.
     */
    protected int fontStyle;

    /**
     * The type face to be used when rendering the label text.
     */
    protected Typeface labelTypeface;

    /**
     * The paint to be used when rendering the label text.
     */
    protected Paint labelTextPaint;

    /**
     * The paint to be used when rendering the label background fill.
     */
    protected Paint labelFillPaint;

    /**
     * The paint to be used when rendering the label background stroke.
     */
    protected Paint labelStrokePaint;

    /**
     * The converter to be used when turning a label data point to text.
     */
    protected Function<Object, String> labelToStringConverter;

    /**
     * The chart series that own the current renderer instance.
     */
    protected final ChartSeries owner;

    /**
     * Creates a new instance of the {@link BaseLabelRenderer} class used for rendering series labels.
     *
     * @param owner the chart series that will be owning the current renderer instance.
     */
    public BaseLabelRenderer(ChartSeries owner) {
        this.owner = owner;

        this.labelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        this.labelTextPaint.setTextSize(Util.getDimen(TypedValue.COMPLEX_UNIT_SP, 13));
        this.labelTextPaint.setColor(this.labelTextColor);

        this.labelMargin = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 7);
        this.labelStrokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1);

        float labelPadding = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 5);

        this.labelPaddingTop = labelPadding;
        this.labelPaddingBottom = labelPadding;
        this.labelPaddingLeft = labelPadding;
        this.labelPaddingRight = labelPadding;

        initLabelStrokePaint();
        initLabelFillPaint();
    }

    @Override
    public int getLabelTextColor() {
        return (int)this.getValue(LABEL_COLOR_PROPERTY_KEY);
    }

    @Override
    public void setLabelTextColor(int color) {
        this.setValue(LABEL_COLOR_PROPERTY_KEY, color);
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

        if (value == null)
            throw new NullPointerException("value");

        if (this.labelTypeface != null && this.labelTypeface.equals(value)) {
            return;
        }

        this.labelTypeface = value;
        this.labelTextPaint.setTypeface(value);
    }

    /**
     * Sets the padding for the labels.
     *
     * @param left   the left padding.
     * @param top    the top padding.
     * @param right  the right padding.
     * @param bottom the bottom padding.
     */
    public void setLabelPadding(float left, float top, float right, float bottom) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0)
            throw new IllegalArgumentException("all padding values must be greater or equal to zero");

        this.labelPaddingLeft = left;
        this.labelPaddingTop = top;
        this.labelPaddingRight = right;
        this.labelPaddingBottom = bottom;
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
        this.labelTextPaint.setTypeface(Typeface.create(this.labelTypeface, this.fontStyle));
    }

    @Override
    public String getLabelFormat() {
        return this.labelFormat;
    }

    @Override
    public void setLabelFormat(String format) {
        if (format == null)
            throw new NullPointerException("format");

        this.labelFormat = format;
    }

    @Override
    public float getLabelMargin() {
        return this.labelMargin;
    }

    @Override
    public void setLabelMargin(float offset) {
        this.labelMargin = offset;
    }

    @Override
    public float getLabelSize() {
        return labelTextPaint.getTextSize();
    }

    @Override
    public void setLabelSize(float value) {
        if (value < 0)
            throw new IllegalArgumentException("value cannot be negative");

        this.labelTextPaint.setTextSize(value);
    }

    /**
     * Gets the label fill color.
     *
     * @return the current fill color.
     */
    public int getLabelFillColor() {
        return (int)this.getValue(LABEL_FILL_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the label fill color.
     *
     * @param color the new fill color.
     */
    public void setLabelFillColor(int color) {
        this.setValue(LABEL_FILL_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the color of the label stroke.
     *
     * @return the current label stroke color.
     */
    public int getLabelStrokeColor() {
        return (int)this.getValue(LABEL_STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the color of the label stroke.
     *
     * @param color the new stroke color.
     */
    public void setLabelStrokeColor(int color) {
        this.setValue(LABEL_STROKE_COLOR_PROPERTY_KEY, color);
    }

    @Override
    public Function<Object, String> getLabelValueToStringConverter() {
        return this.labelToStringConverter;
    }

    @Override
    public void setLabelValueToStringConverter(Function<Object, String> converter) {
        if (converter == null)
            throw new NullPointerException("converter");
        this.labelToStringConverter = converter;
    }

    /**
     * Applies the passed palette to the current label renderer instance.
     *
     * @param palette the palette to be applied.
     */
    public void applyPalette(ChartPalette palette) {
        PaletteEntry entry = null;
        if (palette != null) {
            entry = palette.getEntry(PALETTE_FAMILY, owner.getCollectionIndex());
        }

        if (entry != null) {
            this.setValue(LABEL_FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getFill());
            this.setValue(LABEL_STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());

            String labelColor = entry.getCustomValue("TextColor");
            if (labelColor != null) {
                this.setValue(LABEL_COLOR_PROPERTY_KEY, PALETTE_VALUE, Color.parseColor(labelColor));
            }
        }
    }

    /**
     * Used to force apply the current palette.
     */
    public void invalidatePalette() {
        applyPalette(this.owner.getPalette());
    }

    @Override
    public void renderLabel(Canvas canvas, ChartNode dataPoint) {
        DataPoint point = (DataPoint) dataPoint;
        String[] labelTextLines = getLabelText(point).split("\n");
        int longest = 0, current = 0;
        for (int i = 0, len = labelTextLines.length; i < len; i++)
            if (labelTextLines[i].length() > longest) {
                longest = labelTextLines[i].length();
                current = i;
            }

        Rect textSize = getTextBounds(labelTextLines[current], this.labelTextPaint);
        if (labelTextLines.length > 1)
            textSize.bottom += textSize.height() * labelTextLines.length;

        RadPoint textPosition = calculateLabelPoint(point, textSize);
        double x = textPosition.getX();
        double y = textPosition.getY();

        // Makes sure that the labels don't get clipped if the chart is not zoomed.
        RadRect parentSlot = dataPoint.getParent().getLayoutSlot();
        boolean isChartZoomedHorizontally = isChartZoomedHorizontally();
        boolean isChartZoomedVertically = isChartZoomedVertically();
        if (!isChartZoomedHorizontally) {
            x = preventClippingLeft((float) x, parentSlot);
            x = preventClippingRight((float) x, parentSlot, textSize);
        }

        if (!isChartZoomedVertically) {
            y = preventClippingTop((float) y, parentSlot, textSize);
            y = preventClippingBottom((float) y, parentSlot);
        }

        textPosition = new RadPoint(x, y);

        Path path = new Path();
        Rect labelBackground = getLabelBackgroundBounds(textPosition, textSize);
        RadRect slot = point.getLayoutSlot();
        prepareLabel(path, labelBackground, slot);
        path.close();

        drawLabelBackground(canvas, path, dataPoint.index());

        // Prevent too large padding from causing non consistent behaviours.
        if (!isChartZoomedHorizontally && labelBackground.left < parentSlot.getX() || labelBackground.left > parentSlot.getRight()) {
            x = slot.getX() - (textSize.width() / 2.0);
        }

        if (!isChartZoomedVertically && labelBackground.top < parentSlot.getY() || labelBackground.top > parentSlot.getBottom()) {
            y = slot.getY() - (textSize.height());
        }

        float lineHeight = textSize.height() / labelTextLines.length;
        for (int i = 0, len = labelTextLines.length; i < len; i++)
            drawLabelText(canvas, labelTextLines[i], (float) x, (float) (y - ((len - i - 1) * lineHeight)));
    }

    /**
     * Determines whether the current chart instance is zoomed horizontally or not.
     *
     * @return <code>true</code> if the chart is being zoomed, <code>false</code> otherwise.
     */
    protected final boolean isChartZoomedHorizontally() {
        RadSize zoom = this.owner.getChart().getZoom();

        return zoom.getWidth() != 1.0;
    }

    /**
     * Determines whether the current chart instance is zoomed vertically or not.
     *
     * @return <code>true</code> if the chart is being zoomed, <code>false</code> otherwise.
     */
    protected final boolean isChartZoomedVertically() {
        RadSize zoom = this.owner.getChart().getZoom();

        return zoom.getHeight() != 1.0;
    }

    /**
     * Prepares an empty path to take the shape that will be used as background of the label. The path is moved to the top left corner of the
     * passed label boundaries and it follows the shape of the rectangle. Inheritors might override this behaviour by adding additional points that will
     * form pointers or other desired shapes and effects.
     *
     * @param path          the path to be filled with points defining the shape of the label background.
     * @param labelBounds   the boundaries of the label background which the path will follow exactly if not altered by inheritors.
     * @param dataPointSlot the data point slot.
     */
    protected void prepareLabel(Path path, Rect labelBounds, RadRect dataPointSlot) {
        path.moveTo(labelBounds.left, labelBounds.top);
        path.lineTo(labelBounds.right, labelBounds.top);
        path.lineTo(labelBounds.right, labelBounds.bottom);
        path.lineTo(labelBounds.left, labelBounds.bottom);
        path.lineTo(labelBounds.left, labelBounds.top);
    }

    /**
     * Draws the background of the label using a predefined path of points and a data point index.
     *
     * @param canvas         the canvas on which the background will be drawn.
     * @param path           the path holding the shape of the label background.
     * @param dataPointIndex the index of the data point that the current label is associated with.
     */
    protected void drawLabelBackground(Canvas canvas, Path path, int dataPointIndex) {
        canvas.drawPath(path, getLabelFillPaint(dataPointIndex));
        canvas.drawPath(path, this.labelStrokePaint);
    }

    /**
     * Draws the text value of the text using a predefined text value and position.
     *
     * @param canvas        the canvas on which the label text will be drawn.
     * @param labelText     the text value to be drawn.
     * @param textPositionX the x position for the drawn text.
     * @param textPositionY the y position of the drawn text.
     */
    protected void drawLabelText(Canvas canvas, String labelText, float textPositionX, float textPositionY) {
        canvas.drawText(labelText, textPositionX, textPositionY, this.labelTextPaint);
    }

    /**
     * Calculates the location of the label for the provided {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} instance
     * according to the font settings currently defined on the renderer and the visual settings
     * of the associated {@link com.telerik.widget.chart.visualization.common.ChartSeries}.
     *
     * @param point    the {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} instance for which to calculate the label position.
     * @param textSize text boundaries for the label.
     * @return an instance of the {@link RadPoint} class representing the label position.
     */
    protected RadPoint calculateLabelPoint(DataPoint point, Rect textSize) {
        return null;
    }

    /**
     * Used to extract the value to be printed as text for the label using a given data point.
     *
     * @param dataPoint data point from which the value will be extracted.
     * @return the extracted value.
     */
    protected String getLabelText(DataPoint dataPoint) {
        return null;
    }

    /**
     * Used to obtain the appropriate fill paint for the selected data point.
     *
     * @param dataPointIndex the data point according to which the paint is obtained.
     * @return the appropriate paint.
     */
    protected Paint getLabelFillPaint(int dataPointIndex) {
        return this.labelFillPaint;
    }

    /**
     * This method calculates the size of a text before rendering it to the canvas with higher precision.
     *
     * @param text  the text to be measured.
     * @param paint the paint that will be used to render the text.
     * @return the calculated size.
     */
    protected Rect getTextBounds(final String text, final Paint paint) {
        final Rect textSize = new Rect();
        paint.getTextBounds(Util.generateDummyText(text), 0, text.length(), textSize);

        return textSize;
    }

    /**
     * Used to calculate the position and dimensions of the label background according to the text position.
     *
     * @param calculatedPosition the position of the label text.
     * @param textBounds         the size of the text.
     * @return the calculated rectangle that will hold the label background.
     */
    protected Rect getLabelBackgroundBounds(final RadPoint calculatedPosition, final Rect textBounds) {
        return new Rect(
                (int) (calculatedPosition.getX() - (this.labelPaddingLeft + this.labelStrokeWidth)),
                (int) (calculatedPosition.getY() - (textBounds.height() + this.labelPaddingTop + this.labelStrokeWidth)),
                (int) (calculatedPosition.getX() + (textBounds.width() + this.labelPaddingRight + this.labelStrokeWidth)),
                (int) (calculatedPosition.getY() + this.labelPaddingBottom + this.labelStrokeWidth)
        );
    }

    /**
     * The offset that occurs on the left of the label text's rendering position.
     *
     * @return the left offset.
     */
    protected float offsetLeft() {
        return this.labelPaddingLeft + this.labelStrokeWidth;
    }

    /**
     * The offset that occurs on the top of the label text's rendering position.
     *
     * @param textBounds the size of the text.
     * @return the top offset.
     */
    protected float offsetTop(Rect textBounds) {
        return textBounds.height() + this.labelPaddingTop + this.labelStrokeWidth;
    }

    /**
     * The offset that occurs on the right of the label text's rendering position.
     *
     * @param textBounds the size of the text.
     * @return the right offset.
     */
    protected float offsetRight(Rect textBounds) {
        return textBounds.width() + this.labelPaddingRight + this.labelStrokeWidth;
    }

    /**
     * The offset that occurs on the bottom of the label text's rendering position.
     *
     * @return the bottom offset.
     */
    protected float offsetBottom() {
        return this.labelPaddingBottom + this.labelStrokeWidth;
    }

    /**
     * This method adjusts the suggested x coordinate if needed such as when adding the appropriate offset on the left side,
     * generated from factors such as but not limited to padding and label stroke, the label fits the provided layout slot.
     *
     * @param x          the suggested x coordinate.
     * @param parentSlot the provided layout slot.
     * @return the adjusted x coordinate.
     */
    protected double preventClippingLeft(double x, RadRect parentSlot) {
        double offset = offsetLeft();
        double finalX = x;
        if (x - offset < parentSlot.getX())
            finalX = (parentSlot.getX() + offset);

        return finalX;
    }

    /**
     * This method adjusts the suggested y coordinate if needed such as when adding the appropriate offset on the top side,
     * generated from factors such as but not limited to text height, padding and label stroke, the label fits the provided layout slot.
     *
     * @param y          the suggested y coordinate.
     * @param parentSlot the provided layout slot.
     * @return the adjusted y coordinate.
     */
    protected double preventClippingTop(double y, RadRect parentSlot, Rect textBounds) {
        double finalY = y;
        double offset = offsetTop(textBounds);
        if (y - offset < parentSlot.getY())
            finalY = (parentSlot.getY() + offset);

        return finalY;
    }

    /**
     * This method adjusts the suggested x coordinate if needed such as when adding the appropriate offset on the right side,
     * generated from factors such as but not limited to text width, padding and label stroke, the label fits the provided layout slot.
     *
     * @param x          the suggested x coordinate.
     * @param parentSlot the provided layout slot.
     * @return the adjusted x coordinate.
     */
    protected double preventClippingRight(double x, RadRect parentSlot, Rect textBounds) {
        double offset = offsetRight(textBounds);
        double finalX = x;
        if (x + offset > parentSlot.getRight())
            finalX = (parentSlot.getRight() - offset);

        return finalX;
    }

    /**
     * This method adjusts the suggested y coordinate if needed such as when adding the appropriate offset on the bottom side,
     * generated from factors such as but not limited to padding and label stroke, the label fits the provided layout slot.
     *
     * @param y          the suggested y coordinate.
     * @param parentSlot the provided layout slot.
     * @return the adjusted y coordinate.
     */
    protected double preventClippingBottom(double y, RadRect parentSlot) {
        double finalY = y;
        double offset = offsetBottom();
        if (y + offset > parentSlot.getBottom())
            finalY = (parentSlot.getBottom() - offset);

        return finalY;
    }

    private void initLabelFillPaint() {
        this.labelFillPaint = new Paint();
        this.labelFillPaint.setStyle(Paint.Style.FILL);
    }

    private void initLabelStrokePaint() {
        this.labelStrokePaint = new Paint();
        this.labelStrokePaint.setStyle(Paint.Style.STROKE);
        this.labelStrokePaint.setStrokeWidth(this.labelStrokeWidth);
    }
}
