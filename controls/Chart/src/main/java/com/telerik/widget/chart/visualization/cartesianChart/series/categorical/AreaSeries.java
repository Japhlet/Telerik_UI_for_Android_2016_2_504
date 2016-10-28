package com.telerik.widget.chart.visualization.cartesianChart.series.categorical;

import android.graphics.Color;
import android.graphics.PointF;

import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.widget.chart.engine.databinding.DataPointBinding;
import com.telerik.widget.chart.engine.series.combination.ChartSeriesCombineMode;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.FilledSeries;
import com.telerik.widget.chart.visualization.common.renderers.AreaRendererBase;
import com.telerik.widget.chart.visualization.common.renderers.LineRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * Represents a chart series that are visualized like an area figure in the cartesian space.
 */
public class AreaSeries extends CategoricalStrokedSeries implements FilledSeries {

    public static final int FILL_COLOR_PROPERTY_KEY = registerProperty(Color.RED, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            AreaSeries series = (AreaSeries)sender;

            int value = (int)propertyValue;
            series.areaRenderer.setValue(AreaRendererBase.FILL_COLOR_PROPERTY_KEY, propertyType, value);
            series.legendItem.setFillColor(series.getFillColor());
            series.requestRender();
        }
    });

    private AreaRendererBase areaRenderer;

    /**
     * Initializes a new instance of the {@link AreaSeries} class.
     */
    public AreaSeries() {
        this(null, null, null);
    }

    public AreaSeries(DataPointBinding valueBinding, DataPointBinding categoryBinding, Iterable data) {
        super(valueBinding, categoryBinding, data);

        this.areaRenderer = (AreaRendererBase) this.getRenderer();
    }

    /**
     * Sets the area fill color.
     *
     * @param value The new fill color.
     */
    public void setFillColor(int value) {
        this.setValue(FILL_COLOR_PROPERTY_KEY, value);
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.AREA_FAMILY;
    }

    @Override
    public int getFillColor() {
        return (int)this.getValue(FILL_COLOR_PROPERTY_KEY);
    }

    @Override
    public int getLegendFillColor() {
        return this.getFillColor();
    }

    public boolean hitTest(PointF touchLocation) {
        return this.areaRenderer.hitTest(touchLocation);
    }

    @Override
    protected LineRenderer createRenderer() {
        return new AreaRendererBase();
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);
        if (this.model.getCombineMode() == ChartSeriesCombineMode.STACK ||
                this.model.getCombineMode() == ChartSeriesCombineMode.STACK_100) {
            // pass our reference to next stacked series
            this.getChart().stackedSeriesContext().setPreviousStackedArea(areaRenderer.topSurfacePoints);
        }
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        PaletteEntry defaultEntry = this.getDefaultEntry();
        if (defaultEntry != null) {
            this.setValue(FILL_COLOR_PROPERTY_KEY, PALETTE_VALUE, defaultEntry.getFill());
        }
        super.applyPaletteCore(palette);
    }
}
