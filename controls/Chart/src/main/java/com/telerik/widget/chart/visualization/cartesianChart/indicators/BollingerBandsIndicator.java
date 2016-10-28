package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.graphics.Canvas;
import android.graphics.Color;

import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.BollingerBandsIndicatorDataSource;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PointSeriesModel;
import com.telerik.widget.chart.visualization.common.Axis;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.ArrayList;
import java.util.List;

public class BollingerBandsIndicator extends ValuePeriodIndicatorBase {
    /**
     * The number of standard deviations used to calculate the indicator values.
     */
    protected int standardDeviations;

    /**
     * The renderer responsible for rendering the lower band.
     */
    protected LineRenderer lowerBandRenderer;

    /**
     * Current collection of chart elements.
     */
    protected List<ChartElement> elements;

    /**
     * Model holding info about the lower band.
     */
    protected CategoricalSeriesModel lowerBandModel;

    /**
     * Creates a new instance of the {@link ValuePeriodIndicatorBase} class.
     */
    public BollingerBandsIndicator() {
        this.lowerBandModel = new PointSeriesModel();

        this.lowerBandRenderer = this.createRenderer();
        this.lowerBandRenderer.setModel(this.lowerBandModel);
        this.elements = new ArrayList<ChartElement>();
        this.elements.add(this.model());
        this.elements.add(this.lowerBandModel());
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);

        this.lowerBandRenderer.render(canvas);
    }

    /**
     * Gets the number of standard deviations used to calculate the indicator values.
     *
     * @return the number of standard deviations.
     */
    public int getStandardDeviations() {
        return this.standardDeviations;
    }

    /**
     * Sets the number of standard deviations used to calculate the indicator values.
     *
     * @param standardDeviations the new number of standard deviations.
     */
    public void setStandardDeviations(int standardDeviations) {
        if (this.standardDeviations == standardDeviations)
            return;

        this.standardDeviations = standardDeviations;
        onStandardDeviationsChanged(standardDeviations);
    }

    /**
     * Gets the lower band stroke color.
     *
     * @return the current lower band stroke color.
     */
    public int getLowerBandStrokeColor() {
        return (int)this.lowerBandRenderer.getValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the lower band stroke color.
     *
     * @param color the new lower band stroke color.
     */
    public void setLowerBandStrokeColor(int color) {
        this.lowerBandRenderer.setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, color);
    }

    /**
     * Gets the lower band stroke width.
     *
     * @return the current lower band stroke width.
     */
    public float getLowerBandStrokeWidth() {
        return (float)this.lowerBandRenderer.getValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the lower band stroke color.
     *
     * @param strokeWidth the new lower band stroke color.
     */
    public void setLowerBandStrokeWidth(float strokeWidth) {
        this.lowerBandRenderer.setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, strokeWidth);
    }

    /**
     * The model holding information about the lower band.
     *
     * @return the current lower band model.
     */
    public ChartSeriesModel lowerBandModel() {
        return this.lowerBandModel;
    }

    @Override
    public String toString() {
        return String.format("Bollinger Bands (%s, %s)", this.period, this.standardDeviations);
    }

    /**
     * The collection of data points for the lower band.
     *
     * @return the current collection of lower band data points.
     */
    protected DataPointCollection<CategoricalDataPoint> lowerBandDataPoints() {
        return this.lowerBandModel().dataPoints();
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new BollingerBandsIndicatorDataSource(this.model());
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        if (palette == null)
            return;

        super.applyPaletteCore(palette);

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), getChart().chartAreaModel().getSeries().indexOf(this.model()));
        if (entry == null)
            return;

        this.lowerBandRenderer.setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        this.lowerBandRenderer.setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        this.lowerBandRenderer.layoutContext = context;
        this.lowerBandRenderer.prepare();
    }

    @Override
    protected void onChartAxisChanged(Axis oldAxis, Axis newAxis) {
        super.onChartAxisChanged(oldAxis, newAxis);

        if (oldAxis != null)
            this.model.detachAxis(oldAxis.getModel());

        if (newAxis != null)
            this.model.attachAxis(newAxis.getModel(), newAxis.getAxisType());
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        for (ChartElement element : this.elements)
            element.setPresenter(this);
    }

    @Override
    protected void onModelAttached() {
        super.onModelAttached();

        this.lowerBandModel.attachAxis(this.model.getFirstAxis(), AxisType.FIRST);
        this.lowerBandModel.attachAxis(this.model.getSecondAxis(), AxisType.SECOND);
        this.getChart().getChartArea().getSeries().add(this.lowerBandModel);
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        for (ChartElement element : this.elements) {
            element.setPresenter(null);
        }

        oldChart.chartAreaModel().getSeries().remove(this.lowerBandModel);
    }

    private void onStandardDeviationsChanged(int newValue) {
        ((BollingerBandsIndicatorDataSource) this.dataSource()).setStandardDeviations(newValue);
    }
}
