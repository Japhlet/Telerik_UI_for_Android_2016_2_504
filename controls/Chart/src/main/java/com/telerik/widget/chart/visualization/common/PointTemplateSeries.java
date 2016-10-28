package com.telerik.widget.chart.visualization.common;

import android.graphics.Canvas;

import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers.ChartDataPointRenderer;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;
import com.telerik.widget.primitives.legend.LegendItem;

/**
 * Represents a base class for all Chart Series that support customization of how data points
 * are visualised. This class is abstract and should not be used directly in your application.
 */
public abstract class PointTemplateSeries extends ChartSeries {
    private ChartDataPointRenderer defaultPointRenderer;

    protected final LegendItem legendItem = new LegendItem();
    private ChartDataPointRenderer customPointRenderer;

    /**
     * Creates an instance of the {@link PointTemplateSeries} class with a specified context.
     */
    protected PointTemplateSeries() {
        this.initFields();

        this.updateLegendItem();
        this.defaultPointRenderer = this.createDataPointRenderer();
    }

    protected void initFields() {
    }

    /**
     * Gets the {@link ChartDataPointRenderer} that will render the data points for this series.
     */
    public ChartDataPointRenderer getDataPointRenderer() {
        if (this.customPointRenderer == null)
            this.customPointRenderer = createDataPointRenderer();

        return this.customPointRenderer;
    }

    /**
     * Sets the {@link ChartDataPointRenderer} that will render the data points for this series.
     */
    public void setDataPointRenderer(ChartDataPointRenderer renderer) {
        if (this.customPointRenderer == renderer) {
            return;
        }

        this.customPointRenderer = renderer;
        this.requestRender();
    }

    @Override
    protected void drawCore(Canvas canvas) {
        super.drawCore(canvas);

        ChartDataPointRenderer renderer = this.customPointRenderer;

        if (renderer == null) {
            renderer = this.defaultPointRenderer;
        }

        if (renderer == null) {
            return;
        }

        for (Object point : this.model().visibleDataPoints()) {
            renderer.renderPoint(canvas, (DataPoint) point);
        }
    }

    protected ChartDataPointRenderer createDataPointRenderer() {
        return null;
    }

    /**
     * Gets the title which will be displayed in the Chart legend for this series.
     *
     * @return a string representing the title.
     */
    public String getLegendTitle() {
        return this.legendItem.getTitle();
    }

    /**
     * Sets the title which will be displayed in the Chart legend for this series.
     *
     * @param value a string representing the title.
     */
    public void setLegendTitle(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Legend title cannot be null.");
        }

        if (value.equals(this.legendItem.getTitle())) {
            return;
        }

        this.legendItem.setTitle(value);
    }

    @Override
    protected void onIsVisibleInLegendChanged(boolean newValue) {
        RadChartViewBase chart = this.getChart();

        if (chart != null) {
            if (newValue) {
                chart.getLegendInfos().add(this.legendItem);
            } else {
                chart.getLegendInfos().remove(this.legendItem);
            }
        }
    }

    private void updateLegendItem() {
        this.legendItem.setTitle(this.getLegendTitle());
        this.legendItem.setFillColor(this.getLegendFillColor());
        this.legendItem.setStrokeColor(this.getLegendStrokeColor());
    }

    /**
     * Returns the color used to depict this series in the Chart legend.
     *
     * @return an integer representing the legend color.
     */
    public abstract int getLegendFillColor();

    /**
     * Returns the stroke color used to depict this series in the Chart legend.
     *
     * @return an integer representing the legend stroke color.
     */
    public abstract int getLegendStrokeColor();

    @Override
    protected void onUIUpdated() {
        super.onUIUpdated();

        this.updateLegendItem();
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        if (this.getIsVisibleInLegend()) {
            this.getChart().getLegendInfos().add(this.legendItem);
        }
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        if (oldChart != null) {
            oldChart.getLegendInfos().remove(this.legendItem);
        }
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);
        this.setPaletteToVisuals(palette);
    }

    @Override
    public void onPointAdded(int index, DataPoint point) {
        super.onPointAdded(index, point);

        if(this.getCanApplyPalette()) {
            this.applyPaletteToDefaultVisual(point, this.getDefaultEntry());
        }
    }

    @Override
    public void onPointRemoved(int index, DataPoint point) {
        super.onPointRemoved(index, point);

        if(this.getCanApplyPalette()) {
            this.clearPaletteFromDefaultVisual(point);
        }
    }

    private void setPaletteToVisuals(ChartPalette palette) {
        if (this.chart == null) {
            return;
        }

        if (this.getIsSelected() && this.chart.getSelectionPalette() != null) {
            palette = this.chart.getSelectionPalette();
        }

        PaletteEntry defaultEntry = this.getDefaultEntry(palette);
        PaletteEntry selectionEntry = this.getSelectionEntry(this.chart.getSelectionPalette());

        if (defaultEntry != null) {
            this.applyPaletteToDefaultVisual(null, defaultEntry);
        }

        for (Object objPoint : this.model().dataPoints()) {
            DataPoint point = (DataPoint) objPoint;
            PaletteEntry entry = defaultEntry;

            if (point.getIsSelected() && selectionEntry != null) {
                entry = selectionEntry;
            }

            if (entry != null) {
                this.applyPaletteToDefaultVisual(point, entry);
            } else {
                this.clearPaletteFromDefaultVisual(point);
            }
        }
    }

    protected PaletteEntry getDefaultEntry() {
        return this.getDefaultEntry(this.chart.getPalette());
    }

    private PaletteEntry getDefaultEntry(ChartPalette palette) {
        if (palette != null) {
            getLabelRenderer().applyPalette(palette);
            return palette.getEntry(this.getPaletteFamilyCore(), this.getCollectionIndex());
        }

        return null;
    }

    private PaletteEntry getSelectionEntry(ChartPalette selectionPalette) {
        if (selectionPalette != null) {
            return selectionPalette.getEntry(this.getPaletteFamilyCore(), this.model().collectionIndex());
        }

        return null;
    }

    /**
     * Called when the specified palette entry has to be applied to the visual element
     * representing the provided {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} instance.
     *
     * @param point the point for which the palette entry will be applied.
     * @param entry the {@link com.telerik.widget.palettes.PaletteEntry} instance being applied.
     */
    protected void applyPaletteToDefaultVisual(DataPoint point, PaletteEntry entry) {
    }

    /**
     * Called when the specified palette entry has to be cleared from the visual element
     * representing the provided {@link com.telerik.widget.chart.engine.dataPoints.DataPoint} instance.
     *
     * @param point the point for which the palette entry should be cleared.
     */
    protected void clearPaletteFromDefaultVisual(DataPoint point) {
    }

    @Override
    protected void processPaletteChanged() {
        super.processPaletteChanged();

        this.setPaletteToVisuals(this.getPalette());
    }

    @Override
    protected RadSize measureNodeOverride(ChartNode node, Object content) {
        if (node instanceof DataPoint) {
            return this.measureDataPoint((DataPoint) node);
        }
        return super.measureNodeOverride(node, content);
    }

    private RadSize measureDataPoint(DataPoint point) {

        if (!point.desiredSize.equals(RadSize.getInvalid())) {
            return point.desiredSize;
        }

        return RadSize.getEmpty();
    }
}
