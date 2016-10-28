package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;
import android.util.TypedValue;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Function;
import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.dataPoints.DataPointCollection;
import com.telerik.widget.chart.engine.dataPoints.OhlcDataPoint;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.databinding.GenericDataPointBinding;
import com.telerik.widget.chart.engine.databinding.datasources.ChartSeriesDataSource;
import com.telerik.widget.chart.engine.databinding.datasources.OhlcSeriesDataSource;
import com.telerik.widget.chart.engine.series.ChartSeriesModel;
import com.telerik.widget.chart.engine.series.OhlcSeriesModel;
import com.telerik.widget.chart.visualization.cartesianChart.series.CartesianSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.OhlcPointRendererBase;
import com.telerik.widget.chart.visualization.common.renderers.BaseLabelRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * A base class for chart series that plot financial indicators using High, Low, Open, Close values.
 */
public abstract class OhlcSeriesBase extends CartesianSeries {

    public static final int STROKE_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            OhlcSeriesBase series = (OhlcSeriesBase)sender;
            series.onStrokeChanged(series.getStrokeColor());
        }
    });

    public static final int STROKE_WIDTH_PROPERTY_KEY = registerProperty(Util.getDP(2), new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            OhlcSeriesBase series = (OhlcSeriesBase)sender;

            series.onStrokeWidthChanged(series.getStrokeWidth());
        }
    });

    private DataPointBinding highBinding;
    private DataPointBinding lowBinding;
    private DataPointBinding openBinding;
    private DataPointBinding closeBinding;
    private DataPointBinding categoryBinding;

    private int strokeColor = Color.RED;
    private float strokeWidth;

    /**
     * The renderer responsible for drawing the ohlc data points.
     */
    protected OhlcPointRendererBase renderer;

    /**
     * Creates a new instance of the {@link OhlcSeriesBase} class.
     */
    public OhlcSeriesBase() {
        this.renderer = (OhlcPointRendererBase) this.getDataPointRenderer();
        this.strokeWidth = Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 2.0f);
        this.renderer.upStrokePaint().setStrokeWidth(this.strokeWidth);
    }

    protected void onStrokeChanged(int strokeColor) {
        this.strokeColor = strokeColor;
        this.renderer.upStrokePaint().setColor(strokeColor);

        this.requestRender();
    }

    protected void onStrokeWidthChanged(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.renderer.upStrokePaint().setStrokeWidth(strokeWidth);

        this.requestRender();
    }

    @Override
    protected BaseLabelRenderer createLabelRenderer() {
        return new OhlcSeriesLabelRenderer(this);
    }

    @Override
    public int getLegendFillColor() {
        int modelIndex = this.model().collectionIndex();
        ChartPalette palette = this.getPalette();
        if (palette == null || modelIndex == -1) {
            return this.strokeColor;
        }

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), modelIndex);
        if(entry == null) {
            return Color.RED;
        }

        return entry.getFill();
    }

    @Override
    public int getLegendStrokeColor() {
        return this.getLegendFillColor();
    }

    /**
     * Gets the current stroke color.
     *
     * @return the current stroke color.
     */
    public int getStrokeColor() {
        return (int)this.getValue(STROKE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the current stroke color.
     *
     * @param strokeColor the new stroke color.
     */
    public void setStrokeColor(int strokeColor) {
        this.setValue(STROKE_COLOR_PROPERTY_KEY, strokeColor);
    }

    /**
     * Gets the current stroke width.
     *
     * @return the current stroke width.
     */
    public float getStrokeWidth() {
        return (float)this.getValue(STROKE_WIDTH_PROPERTY_KEY);
    }

    /**
     * Sets the current stroke width.
     *
     * @param strokeWidth the new stroke width.
     */
    public void setStrokeWidth(float strokeWidth) {
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, strokeWidth);
    }

    @Override
    protected ChartSeriesModel createModel() {
        return new OhlcSeriesModel();
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        PaletteEntry entry = this.getDefaultEntry();
        this.setValue(STROKE_COLOR_PROPERTY_KEY, PALETTE_VALUE, entry.getStroke());
        this.setValue(STROKE_WIDTH_PROPERTY_KEY, PALETTE_VALUE, entry.getStrokeWidth());

        this.renderer.pointColors().clear();
        super.applyPaletteCore(palette);
    }

    @Override
    public void setData(Iterable data) {
        super.setData(data);
        this.invalidatePalette();
    }

    @Override
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
        this.renderer.pointColors().put(point, entry);
    }

    /**
     * Gets the collection of data points associated with the series.
     */
    public DataPointCollection<OhlcDataPoint> dataPoints() {
        return this.model().dataPoints();
    }

    /**
     * Gets the binding that will be used to fill the High member of the contained data points.
     */
    public DataPointBinding getHighBinding() {
        return this.highBinding;
    }

    /**
     * Sets the binding that will be used to fill the High member of the contained data points.
     *
     * @param highBinding The new high binding.
     */
    public void setHighBinding(DataPointBinding highBinding) {
        if (this.highBinding == highBinding) {
            return;
        }

        if (highBinding == null)
            throw new NullPointerException("highBinding");

        this.highBinding = highBinding;
        this.onHighBindingChanged(highBinding);
    }

    /**
     * Sets the binding that will be used to fill the High member of the contained data points.
     *
     * @param valueSelector The new high value selector.
     */
    public <T, U> void setHighBinding(Function<T, U> valueSelector) {
        this.setHighBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to fill the Low member of the contained data points.
     */
    public DataPointBinding getLowBinding() {
        return this.lowBinding;
    }

    /**
     * Sets the binding that will be used to fill the Low member of the contained data points.
     *
     * @param lowBinding The new low binding.
     */
    public void setLowBinding(DataPointBinding lowBinding) {
        if (this.lowBinding == lowBinding) {
            return;
        }

        if (lowBinding == null)
            throw new NullPointerException("lowBinding");

        this.lowBinding = lowBinding;
        this.onLowBindingChanged(lowBinding);
    }

    /**
     * Sets the binding that will be used to fill the Low member of the contained data points.
     *
     * @param valueSelector The new low value selector.
     */
    public <T, U> void setLowBinding(Function<T, U> valueSelector) {
        this.setLowBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to fill the Open member of the contained data points.
     */
    public DataPointBinding getOpenBinding() {
        return this.openBinding;
    }

    /**
     * Sets the binding that will be used to fill the Open member of the contained data points.
     *
     * @param openBinding The new open binding.
     */
    public void setOpenBinding(DataPointBinding openBinding) {
        if (this.openBinding == openBinding) {
            return;
        }

        if (openBinding == null)
            throw new NullPointerException("openBinding");

        this.openBinding = openBinding;
        this.onOpenBindingChanged(openBinding);
    }

    /**
     * Sets the binding that will be used to fill the Open member of the contained data points.
     *
     * @param valueSelector The new open value selector.
     */
    public <T, U> void setOpenBinding(Function<T, U> valueSelector) {
        this.setOpenBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to fill the Close member of the contained data points.
     */
    public DataPointBinding getCloseBinding() {
        return this.closeBinding;
    }

    /**
     * Sets the binding that will be used to fill the Close member of the contained data points.
     *
     * @param closeBinding The new close binding.
     */
    public void setCloseBinding(DataPointBinding closeBinding) {
        if (this.closeBinding == closeBinding) {
            return;
        }

        if (closeBinding == null)
            throw new NullPointerException("closeBinding");

        this.closeBinding = closeBinding;
        this.onCloseBindingChanged(closeBinding);
    }

    /**
     * Sets the binding that will be used to fill the Close member of the contained data points.
     *
     * @param valueSelector The new close value selector.
     */
    public <T, U> void setCloseBinding(Function<T, U> valueSelector) {
        this.setCloseBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    /**
     * Gets the binding that will be used to fill the category of the contained data points.
     */
    public DataPointBinding getCategoryBinding() {
        return this.categoryBinding;
    }

    /**
     * Sets the binding that will be used to fill the category of the contained data points.
     *
     * @param categoryBinding The new category binding.
     */
    public void setCategoryBinding(DataPointBinding categoryBinding) {
        if (this.categoryBinding == categoryBinding) {
            return;
        }

        if (categoryBinding == null)
            throw new NullPointerException("categoryBinding");

        this.categoryBinding = categoryBinding;
        this.onCategoryBindingChanged(categoryBinding);
    }

    /**
     * Sets the binding that will be used to fill the category of the contained data points.
     *
     * @param valueSelector The new category selector.
     */
    public <T, U> void setCategoryBinding(Function<T, U> valueSelector) {
        this.setCategoryBinding(new GenericDataPointBinding<T, U>(valueSelector));
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.OHLC_FAMILY;
    }

    @Override
    protected void initDataBinding() {
        OhlcSeriesDataSource source = (OhlcSeriesDataSource) this.dataSource();
        if (source == null) {
            return;
        }

        source.setHighBinding(this.highBinding);
        source.setLowBinding(this.lowBinding);
        source.setOpenBinding(this.openBinding);
        source.setCloseBinding(this.closeBinding);
        source.setCategoryBinding(this.categoryBinding);

        super.initDataBinding();
    }

    @Override
    protected ChartSeriesDataSource createDataSourceInstance() {
        return new OhlcSeriesDataSource(this.model());
    }

    private void onCategoryBindingChanged(DataPointBinding newBinding) {
        ((OhlcSeriesDataSource) this.dataSource()).setCategoryBinding(newBinding);
    }

    private void onHighBindingChanged(DataPointBinding newBinding) {
        ((OhlcSeriesDataSource) this.dataSource()).setHighBinding(newBinding);
    }

    private void onLowBindingChanged(DataPointBinding newBinding) {
        ((OhlcSeriesDataSource) this.dataSource()).setLowBinding(newBinding);
    }

    private void onOpenBindingChanged(DataPointBinding newBinding) {
        ((OhlcSeriesDataSource) this.dataSource()).setOpenBinding(newBinding);
    }

    private void onCloseBindingChanged(DataPointBinding newBinding) {
        ((OhlcSeriesDataSource) this.dataSource()).setCloseBinding(newBinding);
    }
}
