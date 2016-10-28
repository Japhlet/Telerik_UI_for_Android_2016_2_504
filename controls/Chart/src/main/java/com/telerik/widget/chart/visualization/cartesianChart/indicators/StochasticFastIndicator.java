package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.graphics.Canvas;
import android.graphics.Color;

import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.StochasticFastIndicatorDataSource;
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

public class StochasticFastIndicator extends HighLowCloseIndicatorBase {

    /**
     * The main period of the indicator.
     */
    protected int mainPeriod;

    /**
     * The signal period of the indicator.
     */
    protected int signalPeriod;

    /**
     * Renderer responsible for rendering the signal stroke.
     */
    protected LineRenderer signalRenderer;

    /**
     * Model holding information about the signal.
     */
    protected CategoricalSeriesModel signalModel;

    /**
     * Creates a new instance of the {@link StochasticFastIndicator} class.
     */
    public StochasticFastIndicator() {
        this.signalModel = new PointSeriesModel();
    }

    /**
     * Gets the main or fast period of the indicator.
     *
     * @return the current main period.
     */
    public int getMainPeriod() {
        return this.mainPeriod;
    }

    /**
     * Sets the main or fast period of the indicator.
     *
     * @param value the new main period.
     */
    public void setMainPeriod(int value) {
        if (this.mainPeriod == value)
            return;

        this.mainPeriod = value;
        onMainPeriodChanged(value);
    }

    /**
     * Gets the signal or slow period of the indicator.
     *
     * @return the current signal period.
     */
    public int getSignalPeriod() {

        return this.signalPeriod;
    }

    /**
     * Sets the signal or slow period of the indicator.
     *
     * @param value the new signal period.
     */
    public void setSignalPeriod(int value) {
        if (this.signalPeriod == value)
            return;

        this.signalPeriod = value;
        onSignalPeriodChanged(value);
    }

    /**
     * Gets the signal stroke color.
     *
     * @return the current signal stroke color.
     */
    public int getSignalStrokeColor() {
        return this.signalRenderer.getStrokeColor();
    }

    /**
     * Sets the signal stroke color.
     *
     * @param color the new signal stroke color.
     */
    public void setSignalStroke(int color) {
        this.signalRenderer.setStrokeColor(color);
    }

    /**
     * Gets the signal stroke width.
     *
     * @return the current signal stroke width.
     */
    public float getSignalStrokeWidth() {
        return this.signalRenderer.getStrokeThickness();
    }

    /**
     * Sets the signal stroke width.
     *
     * @param width the new signal stroke width.
     */
    public void setSignalStrokeWidth(float width) {
        this.signalRenderer.setStrokeThickness(width);
    }

    /**
     * Returns the model holding the information about the signal.
     *
     * @return current signal model.
     */
    public ChartSeriesModel signalModel() {
        return this.signalModel;
    }

    @Override
    public LineRenderer createRenderer() {
        this.signalRenderer = new LineRenderer();
        this.signalRenderer.setModel(this.signalModel);
        return this.signalRenderer;
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        if (palette == null)
            return;

        super.applyPaletteCore(palette);

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), getChart().chartAreaModel().getSeries().indexOf(this.signalModel));
        if (entry == null)
            return;

        this.getRenderer().setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        this.getRenderer().setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);

        this.getRenderer().render(canvas);
    }

    @Override
    public String toString() {
        return String.format("Stochastic Fast (%s, %s)", this.mainPeriod, this.signalPeriod);
    }

    /**
     * Gets the collection of data points associated with the signal line.
     *
     * @return the current signal data points.
     */
    protected DataPointCollection<CategoricalDataPointBase> signalDataPoints() {
        return this.signalModel.dataPoints();
    }

    /**
     * Returns a collection holding two models.
     *
     * @return a new collection holding two models.
     */
    protected List<ChartElement> getElements() {
        ArrayList<ChartElement> elements = new ArrayList<ChartElement>();
        elements.add(this.model());
        elements.add(this.signalModel());
        return elements;
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        this.getRenderer().layoutContext = context;
        this.getRenderer().prepare();
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new StochasticFastIndicatorDataSource(this.model());
    }

    @Override
    protected void onChartAxisChanged(Axis oldAxis, Axis newAxis) {
        super.onChartAxisChanged(oldAxis, newAxis);

        if (oldAxis != null) {
            this.signalModel.detachAxis(oldAxis.getModel());
        }
        if (newAxis != null) {
            this.signalModel.attachAxis(newAxis.getModel(), newAxis.getAxisType());
        }
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        for (ChartElement element : this.getElements()) {
            element.setPresenter(this);
        }
    }

    @Override
    protected void onModelAttached() {
        super.onModelAttached();

        this.signalModel.attachAxis(this.model.getFirstAxis(), AxisType.FIRST);
        this.signalModel.attachAxis(this.model.getSecondAxis(), AxisType.SECOND);
        this.getChart().chartAreaModel().getSeries().add(this.signalModel);
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        for (ChartElement element : this.getElements()) {
            element.setPresenter(null);
        }

        oldChart.chartAreaModel().getSeries().remove(this.signalModel);
    }

    private void onMainPeriodChanged(int newValue) {
        ((StochasticIndicatorDataSourceBase) this.dataSource()).setMainPeriod(newValue);
    }

    private void onSignalPeriodChanged(int newValue) {
        ((StochasticIndicatorDataSourceBase) this.dataSource()).setSignalPeriod(newValue);
    }
}
