package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipContentAdapter;
import com.telerik.widget.chart.R;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterBubbleDataPoint;
import com.telerik.widget.chart.engine.dataPoints.ScatterDataPoint;

import java.text.DateFormat;
import java.util.Calendar;

public class ChartTooltipContentAdapter implements TooltipContentAdapter {

    /**
     * States whether to apply the default styles or not.
     */
    protected boolean applyDefaultStyles = true;

    /**
     * The current context.
     */
    protected Context context;

    /**
     * The content to be visualized in the popup presenter.
     */
    protected View popupContent;

    /**
     * The ohlc content to be visualized in the popup presenter.
     */
    protected View ohlcContent;

    /**
     * The id of the resource holding the tooltip elements.
     */
    protected int tooltipContentId;

    /**
     * The text size of the value text field.
     */
    protected float valueTextSize;

    /**
     * The category size of the value text field.
     */
    protected float categoryTextSize;

    /**
     * The color of the value text.
     */
    protected int valueTextColor;

    /**
     * The color of the category text color.
     */
    protected int categoryTextColor;

    /**
     * The padding on the left of the tooltip.
     */
    protected int paddingLeft;

    /**
     * The padding on the top of the tooltip.
     */
    protected int paddingTop;

    /**
     * The padding on the right of the tooltip.
     */
    protected int paddingRight;

    /**
     * The padding on the bottom of the tooltip.
     */
    protected int paddingBottom;

    /**
     * Color for the background of the tooltip.
     */
    protected int backgroundColor;

    /**
     * Converter used for custom value to string conversions.
     */
    protected Function<Object, String> valueToStringConverter;

    /**
     * Converter used for custom category to string conversions.
     */
    protected Function<Object, String> categoryToStringConverter;

    /**
     * Creates a new instance of the {@link ChartTooltipContentAdapter} class using a default tooltip resource.
     *
     * @param context the current context.
     */
    public ChartTooltipContentAdapter(Context context) {
        this(context, R.layout.default_tooltip_content);
    }

    /**
     * Creates a new instance of the {@link ChartTooltipContentAdapter} class.
     *
     * @param context          the current context.
     * @param tooltipContentId the resource id of a custom xml defining the content of the tooltip.
     */
    public ChartTooltipContentAdapter(Context context, int tooltipContentId) {
        if (context == null) {
            throw new NullPointerException("context");
        }

        this.context = context;
        this.tooltipContentId = tooltipContentId;
    }

    /**
     * Gets the current tooltip content resource.
     *
     * @return the current resource id.
     */
    public int getTooltipContent() {
        return tooltipContentId;
    }

    /**
     * Sets the current tooltip content resource.
     *
     * @param tooltipContentId the new resource id.
     */
    public void setTooltipContent(int tooltipContentId) {
        this.tooltipContentId = tooltipContentId;
    }

    /**
     * Gets the current background color.
     *
     * @return the current background color.
     */
    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * Sets the current background color.
     *
     * @param color the new background color.
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    /**
     * Sets the current padding.
     *
     * @param left   the left padding.
     * @param top    the top padding.
     * @param right  the right padding.
     * @param bottom the bottom padding.
     */
    public void setPadding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
    }

    /**
     * Sets the current padding.
     *
     * @param padding The new padding.
     */
    public void setPadding(int padding) {
        this.paddingLeft = padding;
        this.paddingTop = padding;
        this.paddingRight = padding;
        this.paddingBottom = padding;
    }

    /**
     * Gets the current category text color.
     *
     * @return the current category color.
     */
    public int getCategoryTextColor() {
        return this.categoryTextColor;
    }

    /**
     * Sets the category text color.
     *
     * @param color the new category color.
     */
    public void setCategoryTextColor(int color) {
        this.categoryTextColor = color;
    }

    /**
     * Gets the current value text color.
     *
     * @return the current value text color.
     */
    public int getValueTextColor() {
        return this.valueTextColor;
    }

    /**
     * Sets the value text color.
     *
     * @param color the new value text color.
     */
    public void setValueTextColor(int color) {
        this.valueTextColor = color;
    }

    /**
     * Gets the current category text size.
     *
     * @return the current category text size.
     */
    public float getCategoryTextSize() {
        return this.categoryTextSize;
    }

    /**
     * Sets the category text size.
     *
     * @param size the new category size.
     */
    public void setCategoryTextSize(float size) {
        this.categoryTextSize = size;
    }

    /**
     * Gets the current value text size.
     *
     * @return the current value text size.
     */
    public float getValueTextSize() {
        return this.valueTextSize;
    }

    /**
     * Sets the value text size.
     *
     * @param size the new value size.
     */
    public void setValueTextSize(float size) {
        this.valueTextSize = size;
    }

    @Override
    public boolean getIsApplyDefaultStyles() {
        return this.applyDefaultStyles;
    }

    @Override
    public void setApplyDefaultStyles(boolean apply) {
        this.applyDefaultStyles = apply;
        this.popupContent = null;
        this.ohlcContent = null;
    }

    @Override
    public Function<Object, String> getValueToStringConverter() {
        return this.valueToStringConverter;
    }

    @Override
    public void setValueToStringConverter(Function<Object, String> converter) {
        this.valueToStringConverter = converter;
    }

    @Override
    public Function<Object, String> getCategoryToStringConverter() {
        return this.categoryToStringConverter;
    }

    @Override
    public void setCategoryToStringConverter(Function<Object, String> converter) {
        this.categoryToStringConverter = converter;
    }

    @Override
    public View getView(Object[] targets) {
        if (targets == null || targets.length == 0)
            return null;

        DataPoint dataPoint = (DataPoint) targets[0];
        if (dataPoint instanceof OhlcDataPoint) {

            OhlcDataPoint ohlcPoint = (OhlcDataPoint) dataPoint;
            return this.getOhlcContent(ohlcPoint);
        } else if (dataPoint instanceof ScatterDataPoint) {
            if (dataPoint instanceof ScatterBubbleDataPoint) {
                return this.getScatterBubbleContent((ScatterBubbleDataPoint) dataPoint);
            }
            return this.getScatterContent((ScatterDataPoint) dataPoint);
        }

        this.popupContent = this.popupContent();
        TextView valueText = Util.getLayoutPart(this.popupContent, R.id.chart_tooltip_value, TextView.class);
        TextView categoryText = Util.getLayoutPart(this.popupContent, R.id.chart_tooltip_category, TextView.class);

        if (dataPoint instanceof CategoricalDataPoint) {
            this.initCategoricalPointView(valueText, categoryText, (CategoricalDataPoint) dataPoint);
        } else if (dataPoint instanceof PieDataPoint) {
            this.initPiePointView(valueText, (PieDataPoint) dataPoint);
            categoryText.setVisibility(View.GONE);
        } else if (dataPoint instanceof RangeDataPoint) {
            this.initRangePointView(valueText, categoryText, (RangeDataPoint) dataPoint);
        }

        return this.popupContent;
    }

    protected void initCategoricalPointView(TextView valueText, TextView categoryText, CategoricalDataPoint dataPoint) {
        valueText.setText(extractValue(dataPoint.getValue()));
        categoryText.setText(extractCategory(dataPoint.getCategory()));
    }

    protected void initPiePointView(TextView valueText, PieDataPoint dataPoint) {
        valueText.setText(extractValue(dataPoint.getValue()));
    }

    protected void initRangePointView(TextView valueText, TextView categoryText, RangeDataPoint dataPoint) {
        valueText.setText(String.format("%s - %s", extractValue(dataPoint.getLow()), extractValue(dataPoint.getHigh())));
        categoryText.setText(extractCategory(dataPoint.getCategory()));
    }

    private View getScatterBubbleContent(ScatterBubbleDataPoint scatterPoint) {
        View result = this.scatterBubbleContent();

        TextView textView = Util.getLayoutPart(result, R.id.xText, TextView.class);
        textView.setText("X: " + extractValue(scatterPoint.getXValue()));

        textView = Util.getLayoutPart(result, R.id.yText, TextView.class);
        textView.setText("Y: " + extractValue(scatterPoint.getYValue()));

        textView = Util.getLayoutPart(result, R.id.areaText, TextView.class);
        textView.setText("Area: " + extractValue(scatterPoint.getSize()));

        return result;
    }

    protected View scatterBubbleContent() {
        View scatterContent = Util.createViewFromXML(R.layout.default_tooltip_scatter_bubble_content, ViewGroup.class, this.context);
        scatterContent.setBackgroundColor(this.backgroundColor);
        scatterContent.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

        TextView textView = Util.getLayoutPart(scatterContent, R.id.xText, TextView.class);
        textView.setTextColor(this.valueTextColor);

        textView = Util.getLayoutPart(scatterContent, R.id.yText, TextView.class);
        textView.setTextColor(this.valueTextColor);

        textView = Util.getLayoutPart(scatterContent, R.id.areaText, TextView.class);
        textView.setTextColor(this.valueTextColor);

        return scatterContent;
    }

    /**
     * Used to extract the value out of the passed data object.
     *
     * @param data object holding the data.
     * @return the extracted data.
     */
    protected String extractValue(Object data) {
        if (this.valueToStringConverter != null)
            return this.valueToStringConverter.apply(data);

        return String.valueOf(data);
    }

    /**
     * Used to extract the category out of the passed data object.
     *
     * @param data object holding the data.
     * @return the extracted data.
     */
    protected String extractCategory(Object data) {
        if (this.categoryToStringConverter != null)
            return this.categoryToStringConverter.apply(data);

        if (data instanceof Calendar) {
            Calendar calendar = (Calendar) data;
            return DateFormat.getDateInstance().format(calendar.getTimeInMillis());
        }

        return String.valueOf(data);
    }

    /**
     * Gets the cached popup content view.
     *
     * @return the empty cached popup view.
     */
    protected View popupContent() {
        View result = Util.createViewFromXML(this.tooltipContentId, ViewGroup.class, this.context);
        if (this.applyDefaultStyles) {
            result.setBackgroundColor(this.backgroundColor);
            result.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

            TextView valueText = Util.getLayoutPart(result, R.id.chart_tooltip_value, TextView.class);
            TextView categoryText = Util.getLayoutPart(result, R.id.chart_tooltip_category, TextView.class);

            valueText.setTextSize(this.valueTextSize);
            valueText.setTextColor(this.valueTextColor);

            categoryText.setTextSize(this.categoryTextSize);
            categoryText.setTextColor(this.categoryTextColor);
        }

        return result;
    }

    /**
     * Gets the ohlc content view representing the passed ohlc data point.
     *
     * @param ohlcPoint the passed ohlc data point.
     * @return the view representing the passed point.
     */
    protected View getOhlcContent(OhlcDataPoint ohlcPoint) {
        View ohlcContent = ohlcContent();

        TextView textView = Util.getLayoutPart(ohlcContent, R.id.highText, TextView.class);
        textView.setText("High: " + extractValue(ohlcPoint.getHigh()));

        textView = Util.getLayoutPart(ohlcContent, R.id.openText, TextView.class);
        textView.setText("Open: " + extractValue(ohlcPoint.getOpen()));

        textView = Util.getLayoutPart(ohlcContent, R.id.closeText, TextView.class);
        textView.setText("Close: " + extractValue(ohlcPoint.getClose()));

        textView = Util.getLayoutPart(ohlcContent, R.id.lowText, TextView.class);
        textView.setText("Low: " + extractValue(ohlcPoint.getLow()));

        textView = Util.getLayoutPart(ohlcContent, R.id.chart_tooltip_category, TextView.class);
        textView.setText(extractCategory(ohlcPoint.getCategory()));

        return ohlcContent;
    }

    protected View getScatterContent(ScatterDataPoint scatterPoint) {
        View result = this.scatterContent();

        TextView textView = Util.getLayoutPart(result, R.id.xText, TextView.class);
        textView.setText("X: " + extractValue(scatterPoint.getXValue()));

        textView = Util.getLayoutPart(result, R.id.yText, TextView.class);
        textView.setText("Y: " + extractValue(scatterPoint.getYValue()));

        return result;
    }

    protected View scatterContent() {
        View scatterContent = Util.createViewFromXML(R.layout.default_tooltip_scatter_content, ViewGroup.class, this.context);
        scatterContent.setBackgroundColor(this.backgroundColor);
        scatterContent.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

        TextView textView = Util.getLayoutPart(scatterContent, R.id.xText, TextView.class);
        textView.setTextColor(this.valueTextColor);

        textView = Util.getLayoutPart(scatterContent, R.id.yText, TextView.class);
        textView.setTextColor(this.valueTextColor);

        return scatterContent;
    }

    /**
     * Gets the cached empty ohlc view.
     *
     * @return the cached ohlc view.
     */
    protected View ohlcContent() {
        this.ohlcContent = Util.createViewFromXML(R.layout.default_tooltip_ohlc_content, ViewGroup.class, this.context);
        if (this.applyDefaultStyles) {
            this.ohlcContent.setBackgroundColor(this.backgroundColor);
            this.ohlcContent.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

            TextView textView = Util.getLayoutPart(this.ohlcContent, R.id.highText, TextView.class);
            textView.setTextColor(this.valueTextColor);

            textView = Util.getLayoutPart(this.ohlcContent, R.id.openText, TextView.class);
            textView.setTextColor(this.valueTextColor);

            textView = Util.getLayoutPart(this.ohlcContent, R.id.closeText, TextView.class);
            textView.setTextColor(this.valueTextColor);

            textView = Util.getLayoutPart(this.ohlcContent, R.id.lowText, TextView.class);
            textView.setTextColor(this.valueTextColor);

            textView = Util.getLayoutPart(this.ohlcContent, R.id.chart_tooltip_category, TextView.class);
            textView.setTextColor(this.categoryTextColor);
        }

        return this.ohlcContent;
    }
}
