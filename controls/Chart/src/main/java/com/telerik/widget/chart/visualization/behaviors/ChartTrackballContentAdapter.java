package com.telerik.widget.chart.visualization.behaviors;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.R;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.engine.dataPoints.RangeDataPoint;
import com.telerik.widget.chart.engine.view.ChartElementPresenter;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.RangeBarSeries;
import com.telerik.widget.chart.visualization.common.PointTemplateSeries;

public class ChartTrackballContentAdapter extends ChartTooltipContentAdapter {

    /**
     * Creates a new instance of the {@link ChartTrackballContentAdapter} class.
     *
     * @param context The app context.
     */
    public ChartTrackballContentAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(Object[] targets) {
        if (targets == null || targets.length == 0)
            return null;


        this.popupContent = popupContent();
        LinearLayout pointsListLayout = Util.getLayoutPart(this.popupContent, R.id.trackball_points_list, LinearLayout.class);
        pointsListLayout.removeAllViews();

        TextView categoryText = Util.getLayoutPart(this.popupContent, R.id.chart_trackball_category, TextView.class);

        this.updateCategoryText(categoryText, (DataPoint) targets[0]);

        for (Object dataPoint : targets) {
            View dataPointView = this.getViewForDataPoint((DataPoint) dataPoint);
            if (dataPointView == null) {
                continue;
            }

            pointsListLayout.addView(dataPointView);
        }

        return this.popupContent;
    }

    @Override
    protected View popupContent() {
        View result = Util.createViewFromXML(R.layout.default_trackball_content, ViewGroup.class, this.context);
        if (this.applyDefaultStyles) {
            result.setBackgroundColor(this.backgroundColor);
            result.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

            TextView categoryText = Util.getLayoutPart(result, R.id.chart_trackball_category, TextView.class);
            categoryText.setTextColor(this.categoryTextColor);
            categoryText.setTextSize(this.categoryTextSize);
        }

        return result;
    }

    /**
     * Gets a view for representing a given data point based on the type of the data point.
     *
     * @param dataPoint passed data point.
     * @return view representing the passed data point.
     */
    protected View getViewForDataPoint(DataPoint dataPoint) {
        ChartElementPresenter parent = dataPoint.getParent().getPresenter();
        if (parent instanceof RangeBarSeries) {
            return this.createRangeDataPointView((RangeDataPoint) dataPoint);
        }

        if (dataPoint instanceof CategoricalDataPoint) {
            return this.createCategoricalDataPointView((CategoricalDataPoint) dataPoint);
        }

        if (dataPoint instanceof OhlcDataPoint) {
            return this.createOhlcDataPointView((OhlcDataPoint) dataPoint);
        }

        return null;
    }

    /**
     * Creates a view for representing a range data point.
     *
     * @param point the data point.
     * @return the representing view.
     */
    protected View createRangeDataPointView(RangeDataPoint point) {
        ViewGroup result = Util.createViewFromXML(R.layout.range_trackball_item_content, ViewGroup.class, this.context);
        PointTemplateSeries series = (PointTemplateSeries) point.getParent().getPresenter();
        int textColor = series.getLegendFillColor();

        TextView textView = (TextView) result.findViewById(R.id.highText);
        textView.setText("High: " + extractValue(point.getHigh()));
        textView.setTextColor(textColor);
        if (this.applyDefaultStyles) {
            textView.setTextSize(this.valueTextSize);
        }

        textView = (TextView) result.findViewById(R.id.lowText);
        textView.setText("Low: " + extractValue(point.getLow()));
        textView.setTextColor(textColor);
        if (this.applyDefaultStyles) {
            textView.setTextSize(this.valueTextSize);
        }

        return result;
    }

    /**
     * Creates a view for representing an ohlc data point.
     *
     * @param point the data point.
     * @return the representing view.
     */
    protected View createOhlcDataPointView(OhlcDataPoint point) {
        ViewGroup result = Util.createViewFromXML(R.layout.ohlc_trackball_item_content, ViewGroup.class, this.context);

        TextView textView = (TextView) result.findViewById(R.id.openText);
        textView.setText("Open: " + extractValue(point.getOpen()));
        if (this.applyDefaultStyles) {
            textView.setTextColor(this.valueTextColor);
            textView.setTextSize(this.valueTextSize);
        }

        textView = (TextView) result.findViewById(R.id.highText);
        textView.setText("High: " + extractValue(point.getHigh()));
        if (this.applyDefaultStyles) {
            textView.setTextColor(this.valueTextColor);
            textView.setTextSize(this.valueTextSize);
        }

        textView = (TextView) result.findViewById(R.id.lowText);
        textView.setText("Low: " + extractValue(point.getLow()));
        if (this.applyDefaultStyles) {
            textView.setTextColor(this.valueTextColor);
            textView.setTextSize(this.valueTextSize);
        }

        textView = (TextView) result.findViewById(R.id.closeText);
        textView.setText("Close: " + extractValue(point.getClose()));
        if (this.applyDefaultStyles) {
            textView.setTextColor(this.valueTextColor);
            textView.setTextSize(this.valueTextSize);
        }

        return result;
    }

    /**
     * Creates a view for representing a categorical data point.
     *
     * @param point the data point.
     * @return the representing view.
     */
    protected View createCategoricalDataPointView(CategoricalDataPoint point) {
        TextView valueText = Util.createViewFromXML(R.layout.default_trackball_item_content, TextView.class, this.context);
        if (this.applyDefaultStyles) {
            valueText.setTextSize(this.valueTextSize);
        }

        if (point.isEmpty) {
            valueText.setText("No data.");
        } else {
            valueText.setText(extractValue(point.getValue()));
        }

        PointTemplateSeries series = (PointTemplateSeries) point.getParent().getPresenter();
        valueText.setTextColor(series.getLegendFillColor());

        return valueText;
    }

    protected void updateCategoryText(TextView textView, DataPoint point) {
        if (point instanceof CategoricalDataPointBase) {
            textView.setText(extractCategory(((CategoricalDataPointBase) point).getCategory()));
        }
    }
}
