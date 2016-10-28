package com.telerik.widget.chart.visualization.cartesianChart.indicators;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.widget.chart.engine.axes.AxisType;
import com.telerik.widget.chart.engine.dataPoints.CategoricalDataPointBase;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.financial.MacdIndicatorDataSource;
import com.telerik.widget.chart.engine.series.CategoricalSeriesModel;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.PointSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.CategoricalSeriesLabelRenderer;
import com.telerik.widget.chart.visualization.common.Axis;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

public class MacdIndicator extends ShortLongPeriodIndicatorBase {

    private LineRenderer signalRenderer;
    private CategoricalSeriesModel signalModel;
    private int signalPeriod;
    private Paint signalPaint;

    /**
     * Creates a new instance of the {@link MacdIndicator} class.
     */
    public MacdIndicator() {
        this.signalModel = new PointSeriesModel();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);

        this.signalRenderer.render(canvas);
    }

    /// <summary>
    /// Gets or sets the indicator signal period.
    /// </summary>
    /// <value>The signal period.</value>
    public int getSignalPeriod() {
        return this.signalPeriod;
    }

    public void setSignalPeriod(int period) {
        if (this.signalPeriod == period)
            return;

        this.signalPeriod = period;
        onSignalPeriodChanged(period);
    }

    /// <summary>
    /// Gets or setst the <see cref="Brush"/> instance that defines the stroke of the line.
    /// </summary>
    public Paint getSignalPaint() {
        return this.signalPaint;
    }

    public void setSignalPaint(Paint paint) {
        if (paint == null)
            throw new NullPointerException("paint");

        if (this.signalPaint == paint)
            return;

        this.signalPaint = paint;
        onSignalPaintChanged(paint);
    }

    @Override
    public LineRenderer createRenderer() {
        this.signalRenderer = new LineRenderer();
        this.signalRenderer.setModel(this.signalModel);
        return this.signalRenderer;
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new CategoricalSeriesLabelRenderer(this);
    }

    public ChartSeriesModel signalModel() {
        return this.signalModel;
    }

    /// <summary>
    /// Gets the collection of data points associated with the signal line.
    /// </summary>
    protected DataPointCollection<CategoricalDataPointBase> signalDataPoints() {
        return this.signalModel.dataPoints();
    }

    /// <summary>
    /// Returns a <see cref="T:System.String" /> that represents the current <see cref="T:System.Object" />.
    /// </summary>
    @Override
    public String toString() {
        return String.format("Moving Average Convergence Divergence (%s, %s, %s)", this.getLongPeriod(), this.getShortPeriod(), this.getSignalPeriod());
    }

    /*/// <summary>
    /// When overridden in a derived class, is invoked whenever application code or internal processes (such as a rebuilding layout pass) call <see cref="M:System.Windows.Controls.Control.ApplyTemplate" />. In simplest terms, this means the method is called just before a UI element displays in an application. For more information, see Remarks.
    /// </summary>
    public override

    void OnApplyTemplate() {
        base.OnApplyTemplate();

        this.renderSurface.Children.Add(this.getRenderer().strokeShape.DisconnectIfChildOfAnotherCanvas());
    }*/

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        this.getRenderer().layoutContext = context;
        this.getRenderer().prepare();
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new MacdIndicatorDataSource(this.model());
    }

    /// <summary>
    /// Occurs when one of the axes of the owning <see cref="RadCartesianChart"/> has been changed.
    /// </summary>
    /// <param name="oldAxis">The old axis.</param>
    /// <param name="newAxis">The new axis.</param>
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

    /// <summary>
    /// Occurs when the presenter has been successfully detached from its owning <see cref="RadChartBase"/> instance.
    /// </summary>
    /// <param name="oldChart"></param>
    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        this.signalModel.setPresenter(null);
        oldChart.chartAreaModel().getSeries().remove(this.signalModel);
    }

    @Override
    protected void onModelAttached() {
        super.onModelAttached();

        this.signalModel.attachAxis(this.model.getFirstAxis(), AxisType.FIRST);
        this.signalModel.attachAxis(this.model.getSecondAxis(), AxisType.SECOND);


        this.signalModel.setPresenter(this);
        this.getChart().chartAreaModel().getSeries().add(this.signalModel);
    }

    public int getStrokeColor() {
        return this.getRenderer().getStrokeColor();
    }

    public void setStrokeColor(int value) {
        this.getRenderer().setStrokeColor(value);
    }

    public float getStrokeThickness() {
        return this.getRenderer().getStrokeThickness();
    }

    public void setStrokeThickness(float value) {
        this.getRenderer().setStrokeThickness(value);
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        if (palette == null)
            return;

        super.applyPaletteCore(palette);

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), this.getChart().chartAreaModel().getSeries().indexOf(this.signalModel));
        if (entry == null)
            return;

        this.getRenderer().setValue(LineRenderer.STROKE_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());
        this.getRenderer().setValue(LineRenderer.STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
    }

    private void onSignalPaintChanged(Paint newPaint) {
        if (this.getRenderer() != null) {
            this.getRenderer().setStrokeThickness(newPaint.getStrokeWidth());
            this.getRenderer().setStrokeColor(newPaint.getColor());
        }

        if (this.isPaletteApplied) {
            this.updatePalette(true);
        }
    }

    private void onSignalPeriodChanged(int newPeriod) {
        ((MacdIndicatorDataSource) dataSource()).setSignalPeriod(newPeriod);
    }
}
