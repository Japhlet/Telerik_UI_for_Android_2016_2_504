package com.telerik.widget.chart.visualization.common;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;

import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.chartAreas.ChartPlotAreaModel;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.DataPointBindingEntry;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.DataBindingListener;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.visualization.behaviors.DataPointInfo;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all chart series.
 */
public abstract class ChartSeries extends ChartElementPresenter implements com.telerik.widget.chart.engine.view.ChartSeries, DataBindingListener, ChartSeriesModel.DataPointsChangedListener{

    public static final int SERIES_Z_INDEX = 1000;
    protected boolean isVisibleInLegend = true;
    private boolean showLabels;
    private Iterable itemsSource;
    private boolean clipToPlotArea = true;

    private ChartSeriesDataSource dataSource;
    private BaseLabelRenderer labelRenderer;

    private ChartSeriesModel model;
    private boolean isSelected;
    private ArrayList<PropertyChangedListener> propertyChangedListeners = new ArrayList<>();

    /**
     * Creates a new instance of the {@link ChartSeries} class.
     */
    protected ChartSeries() {
        this.dataSource = this.createDataSourceInstance();
        this.dataSource.addBoundItemPropertyChangedListener(this);
    }

    public void addPropertyChangedListener(PropertyChangedListener listener) {
        this.propertyChangedListeners.add(listener);
    }

    public void removePropertyChangedListener(PropertyChangedListener listener) {
        this.propertyChangedListeners.remove(listener);
    }

    protected void notifyPropertyChangedListeners(String propertyName, Object value) {
        for(PropertyChangedListener listener : propertyChangedListeners) {
            listener.onPropertyChanged(this, propertyName, value);
        }
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean value) {
        if (this.isSelected == value) {
            return;
        }

        this.isSelected = value;

        this.onIsSelectedChanged();
    }

    /**
     * Called when the setIsSelected() method is called with a new value.
     */
    protected void onIsSelectedChanged() {
        this.invalidatePalette();
    }

    /**
     * Sets the padding for the labels in the series.
     *
     * @param left   the left padding.
     * @param top    the top padding.
     * @param right  the right padding.
     * @param bottom the bottom padding.
     */
    public void setLabelPadding(final float left, final float top, final float right, final float bottom) {
        this.getLabelRenderer().setLabelPadding(left, top, right, bottom);
    }

    /**
     * Gets the {@link com.telerik.widget.chart.visualization.common.renderers.ChartLabelRenderer} responsible for drawing the labels.
     *
     * @return A {@link com.telerik.widget.chart.visualization.common.renderers.ChartLabelRenderer} instance.
     */
    public BaseLabelRenderer getLabelRenderer() {
        if (labelRenderer == null)
            setLabelRenderer(createLabelRenderer());

        return this.labelRenderer;
    }

    /**
     * Sets a {@link com.telerik.widget.chart.visualization.common.renderers.ChartLabelRenderer} object that will be responsible for drawing the series labels.
     *
     * @param value A the new series ChartLabelRenderer. If <code>null</code> is passed, the default renderer will be used.
     */
    public void setLabelRenderer(BaseLabelRenderer value) {
        if (this.labelRenderer == value) {
            return;
        }

        this.labelRenderer = value;
        this.labelRenderer.invalidatePalette();
        this.requestRender();
    }

    /**
     * Gets a boolean value determining whether the current {@link PointTemplateSeries}
     * instance will be visible in the Chart legend.
     *
     * @return <code>true</code> if the series is shown in the legend, otherwise <code>false</code>.
     */
    public boolean getIsVisibleInLegend() {
        return this.isVisibleInLegend;
    }

    /**
     * Sets a boolean value determining whether the current {@link PointTemplateSeries}
     * instance will be visible in the Chart legend.
     *
     * @param value <code>true</code> if the series is shown in the legend, otherwise <code>false</code>.
     */
    public void setIsVisibleInLegend(boolean value) {
        if (this.isVisibleInLegend == value) {
            return;
        }

        this.isVisibleInLegend = value;
        this.onIsVisibleInLegendChanged(value);
    }

    /**
     * Called when the setIsVisibleInLegend() method is called.
     *
     * @param value The new isVisibleInLegend value.
     */
    protected void onIsVisibleInLegendChanged(boolean value) {
    }

    /**
     * Determines if the series contains the provided point.
     *
     * @param touchLocation The point to test.
     * @return True if the series contains the point argument and false otherwise.
     */
    public boolean hitTest(PointF touchLocation) {
        return this.hitTestDataPoint(touchLocation) != null;
    }

    /**
     * Determines if the provided point argument is on a data point.
     *
     * @param touchLocation The point to test.
     * @return The data point that contains the provided point.
     */
    public DataPoint hitTestDataPoint(PointF touchLocation) {
        DataPointCollection<DataPoint> points = this.model().dataPoints();
        for (DataPoint point : points) {
            if (this.hitTestDataPoint(touchLocation, point)) {
                return point;
            }
        }

        return null;
    }

    protected boolean hitTestDataPoint(PointF touchLocation, DataPoint point) {
        RectF rect = Util.convertToRectF(point.getLayoutSlot());
        return rect.contains(touchLocation.x, touchLocation.y);
    }

    /**
     * Gets the format in which the labels' text will be rendered.
     *
     * @return the current format.
     */
    public String getLabelFormat() {
        return getLabelRenderer().getLabelFormat();
    }

    /**
     * Sets the format in which the labels' text will be rendered.
     *
     * @param value the new format.
     */
    public void setLabelFormat(String value) {
        getLabelRenderer().setLabelFormat(value);
        this.requestRender();
    }

    /**
     * Gets the converter used for converting the value of a label to text value.
     *
     * @return the current label value converter.
     */
    public Function<Object, String> getLabelValueToStringConverter() {
        return getLabelRenderer().getLabelValueToStringConverter();
    }

    /**
     * Sets the converter used to convert label value to string value.
     *
     * @param converter the new converter.
     */
    public void setLabelValueToStringConverter(Function<Object, String> converter) {
        getLabelRenderer().setLabelValueToStringConverter(converter);
        this.requestRender();
    }

    /**
     * Gets the label font.
     *
     * @return the current label font.
     */
    public Typeface getLabelFont() {
        return getLabelRenderer().getLabelFont();
    }

    /**
     * Sets the label font.
     *
     * @param value the new label font.
     */
    public void setLabelFont(Typeface value) {
        getLabelRenderer().setLabelFont(value);
        this.requestRender();
    }

    /**
     * Gets the current label font style.
     *
     * @return the current label font style.
     */
    public int getLabelFontStyle() {
        return getLabelRenderer().getLabelFontStyle();
    }

    /**
     * Sets the current label font style.
     *
     * @param value the new label font style.
     */
    public void setLabelFontStyle(int value) {
        getLabelRenderer().setLabelFontStyle(value);
        this.requestRender();
    }

    /**
     * Gets the color of the label text.
     *
     * @return the current text color.
     */
    public int getLabelTextColor() {
        return getLabelRenderer().getLabelTextColor();
    }

    /**
     * Sets the color of the label text.
     *
     * @param value the new color.
     */
    public void setLabelTextColor(int value) {
        getLabelRenderer().setLabelTextColor(value);
        this.requestRender();
    }

    /**
     * Gets the label fill color.
     *
     * @return the current fill color.
     */
    public int getLabelFillColor() {
        return getLabelRenderer().getLabelFillColor();
    }

    /**
     * Sets the current label fill color.
     *
     * @param color the new color.
     */
    public void setLabelFillColor(int color) {
        getLabelRenderer().setLabelFillColor(color);
        this.requestRender();
    }

    /**
     * Gets tje label stroke color.
     *
     * @return the current label stroke color.
     */
    public int getLabelStrokeColor() {
        return getLabelRenderer().getLabelStrokeColor();
    }

    /**
     * Sets the current label stroke color.
     *
     * @param color the new color.
     */
    public void setLabelStrokeColor(int color) {
        getLabelRenderer().setLabelStrokeColor(color);
        this.requestRender();
    }

    /**
     * Gets the label text size.
     *
     * @return the current label text size.
     */
    public float getLabelSize() {
        return getLabelRenderer().getLabelSize();
    }

    /**
     * Sets the label text size.
     *
     * @param value the new label text size.
     */
    public void setLabelSize(float value) {
        if (value <= 0) {
            throw new IllegalArgumentException("The label size cannot be a negative value or zero.");
        }

        getLabelRenderer().setLabelSize(value);
        this.requestRender();
    }

    /**
     * Gets the label margin.
     *
     * @return the current label margin.
     */
    public float getLabelMargin() {
        return getLabelRenderer().getLabelMargin();
    }

    /**
     * Sets the current label margin.
     *
     * @param value the new label margin.
     */
    public void setLabelMargin(float value) {
        getLabelRenderer().setLabelMargin(value);
        this.requestRender();
    }

    /**
     * Gets a value that determines whether the series labels should be shown.
     *
     * @return <code>true</code> it the series labels should be shown and<code>false</code>
     * otherwise.
     */
    public boolean getShowLabels() {
        return this.showLabels;
    }

    /**
     * Sets the value that determines whether the series labels should be shown.
     *
     * @param value <code>true</code> it the series labels should be shown and
     *              <code>false</code> otherwise.
     */
    public void setShowLabels(boolean value) {
        this.showLabels = value;
        this.requestRender();
    }

    /**
     * Gets the items source of the series.
     *
     * @return The items source of the series.
     */
    public Iterable getData() {
        return this.itemsSource;
    }

    /**
     * Sets the items source of the series.
     *
     * @param value The new items source of the series.
     */
    public void setData(Iterable value) {
        this.itemsSource = value;
        this.initDataBinding();

        this.notifyPropertyChangedListeners("Data", value);
    }

    /**
     * Gets a value that determines if the chart will clip its children to fit inside the plot area.
     *
     * @return <code>true</code> to clip children to the plot area or <code>false</code> otherwise.
     */
    public boolean getClipToPlotArea() {
        return this.clipToPlotArea;
    }

    /**
     * Sets a value that determines if the chart will clip its children to fit inside the plot area.
     *
     * @param value <code>true</code> to clip children to the plot area or <code>false</code> otherwise.
     */
    public void setClipToPlotArea(boolean value) {
        this.clipToPlotArea = value;
        this.requestRender();
    }

    /**
     * Gets the plot area size.
     *
     * @return The plot area size.
     * @see RadSize
     */
    public RadSize getPlotAreaSize() {
        if (this.chart == null) {
            return RadSize.getEmpty();
        }

        ChartPlotAreaModel plotArea = this.chart.chartAreaModel().getPlotArea();
        return new RadSize(plotArea.getLayoutSlot().getWidth(), plotArea.getLayoutSlot().getHeight());
    }

    @Override
    public void onDataBindingComplete() {
        if (this.chart == null) {
            return;
        }

        this.chart.resetBehaviors();

        this.requestLayout();
    }

    @Override
    public void onBoundItemPropertyChanged(DataPointBindingEntry entry, PropertyChangeEvent event) {
    }

    @Override
    public void onDataPointIsSelectedChanged(DataPoint point) {
        this.onDataPointSelectionChanged(point);
    }

    /**
     * Finds the closest {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} to the given location.
     *
     * @param location The location.
     * @return The closest {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} to the given location.
     */
    public DataPointInfo findClosestPoint(Point location) {
        double minDistance = Double.POSITIVE_INFINITY;
        DataPointInfo closestDataPoint = null;

        for (Object dataPointObj : this.model().visibleDataPoints()) {
            DataPoint dataPoint = (DataPoint) dataPointObj;
            if (dataPoint.isEmpty) {
                continue;
            }

            double distance = this.getDistanceToPoint(dataPoint.getCenter(), location);

            if (distance < minDistance) {
                minDistance = distance;
                if (closestDataPoint == null) {
                    closestDataPoint = new DataPointInfo();
                }

                closestDataPoint.setDataPoint(dataPoint);
                closestDataPoint.setSeriesModel(this.model());
                closestDataPoint.setDistanceToTouchLocation(distance);
            }
        }

        return closestDataPoint;
    }

    /**
     * Gets the series model for this instance.
     *
     * @return The {@link ChartSeriesModel}.
     */
    public ChartSeriesModel model() {
        if (this.model == null) {
            this.model = this.createModel();
            this.model.setDataPointsChangedListener(this);
        }

        return this.model;
    }

    public void onPointAdded(int index, DataPoint point) {
    }

    public void onPointRemoved(int index, DataPoint point) {
    }

    /**
     * Returns the distance between a data point and a tap location.
     *
     * @param dataPointLocation the data point.
     * @param tapLocation       the tap location.
     * @return the distance.
     */
    public double getDistanceToPoint(Point dataPointLocation, Point tapLocation) {
        return RadMath.getPointDistance(dataPointLocation.x, tapLocation.x, dataPointLocation.y, tapLocation.y);
    }

    /**
     * Inheritors should override this method and create their specific {@link ChartSeriesDataSource}.
     *
     * @return A specific {@link ChartSeriesDataSource}
     */
    protected abstract ChartSeriesDataSource createDataSourceInstance();

    /**
     * Gets the {@link ChartSeriesDataSource} for this series.
     *
     * @return The {@link ChartSeriesDataSource} for this series.
     */
    public ChartSeriesDataSource dataSource() {
        return this.dataSource;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        RectF clip = Util.convertToRectF(this.lastLayoutContext.clipRect());
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(clip);

        this.drawCore(canvas);
        canvas.restore();
    }

    @Override
    public void postRender(Canvas canvas) {
        super.postRender(canvas);

        RectF clip = Util.convertToRectF(this.lastLayoutContext.clipRect());
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(clip);

        if (this.getShowLabels()) {
            this.drawLabels(canvas);
        }
        canvas.restore();
    }

    protected void drawCore(Canvas canvas) {
    }

    /**
     * Creates the default label renderer for the current series if the user didn't set a customized implementation.
     *
     * @return the default label renderer for the series.
     */
    protected abstract BaseLabelRenderer createLabelRenderer();

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        getLabelRenderer().applyPalette(palette);
    }

    @Override
    protected ChartElement getElement() {
        return this.model();
    }

    @Override
    protected int getDefaultZIndex() {
        return ChartSeries.SERIES_Z_INDEX + this.model().index();
    }

    /**
     * Initializes the data binding logic of the series.
     */
    protected void initDataBinding() {
        if (this.itemsSource != null) {
            this.dataSource.setItemsSource(this.itemsSource);
        }
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        this.chart.chartAreaModel().getSeries().add(this.model());
        onModelAttached();
    }

    protected void onModelAttached() {
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);
        oldChart.chartAreaModel().getSeries().remove(this.model());
    }

    /**
     * Inheritors should override this method to return their specific type of {@link ChartSeriesModel}.
     *
     * @return The series model for this series type.
     */
    protected abstract ChartSeriesModel createModel();

    private void drawLabels(final Canvas canvas) {
        List<DataPoint> points = this.model().visibleDataPoints();
        for (DataPoint point : points) {
            if (point.isEmpty) {
                continue;
            }

            getLabelRenderer().renderLabel(canvas, point);
        }
    }

    protected void onDataPointSelectionChanged(DataPoint point) {
        this.invalidatePalette();
    }


}
