package com.telerik.widget.chart.visualization.common;

import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.palettes.ChartPalette;

/**
 * This class is a base for all visual elements that can be part of a chart. This class is
 * abstract and should not be used in your application.
 */
public abstract class ChartElementPresenter extends PresenterBase {
    protected RadChartViewBase chart;
    private int userZIndex = -1;
    private int collectionIndex = -1;

    /**
     * Creates an instance of the {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter}
     * class with a specified activity context.
     */
    protected ChartElementPresenter() {
    }

    /**
     * Gets the Z-index of this series. Useful when adjusting the appearance of multiple series.
     *
     * @return the Z-index.
     */
    public int getZIndex() {
        if (this.userZIndex != -1) {
            return this.userZIndex;
        } else {
            return this.getDefaultZIndex();
        }
    }

    /**
     * Gets the {@link ChartPalette} instance that defines the appearance of the chart.
     *
     * @return the current chart palette.
     */
    public ChartPalette getPalette() {
        if (this.chart == null) {
            return null;
        }

        return this.chart.getPalette();
    }

    protected void processPaletteChanged() {
        this.updatePalette(true);
        this.requestRender();
    }

    private void applyPalette() {
        if (this.isPaletteApplied || !this.getCanApplyPalette()) {
            return;
        }

        ChartPalette palette = this.getPalette();

        if (palette != null) {
            this.applyPaletteCore(palette);
        }
        this.isPaletteApplied = true;
    }

    /**
     * Triggered after invalidating the palette.&nbsp;Used to reapply the palette and change the
     * current invalidation schedule of the palette.
     */
    public void onPaletteInvalidated() {
        this.applyPalette();
        this.processPaletteChanged();
    }

    @Override
    public void onPaletteUpdated(ChartPalette source) {
        if (!this.isLoaded()) {
            return;
        }

        this.processPaletteChanged();
    }

    /**
     * Invalidates the current {@link ChartPalette} instance
     * applied to this {@link PresenterBase}
     * and triggers the apply-palette logic.
     */
    public void invalidatePalette() {
        this.isPaletteApplied = false;
        this.onPaletteInvalidated();
    }

    /**
     * Triggers the palette to be applied.
     *
     * @param force if forced the palette will be applied without considering validations.
     */
    public void updatePalette(boolean force) {
        if (force) {
            this.isPaletteApplied = false;
        }

        this.applyPalette();
    }

    @Override
    public void requestRender() {
        if (this.chart != null) {
            this.chart.requestRender();
        }
    }

    public void requestLayout() {
        if (this.chart != null) {
            this.chart.requestInvalidateArrange();
        }
    }

    /**
     * Sets the Z-index of this series. Useful when adjusting the appearance of multiple series.
     *
     * @param value the Z-index.
     */
    public void setZIndex(int value) {
        this.userZIndex = value;

        if (this.chart == null) {
            return;
        }

        this.chart.sortPresenters();
    }

    void setCollectionIndex(int index) {
        this.collectionIndex = index;
    }

    @Override
    public int getCollectionIndex() {
        return this.collectionIndex;
    }

    /**
     * Gets the owning {@link RadChartViewBase} instance
     * in which this visual element resides.
     *
     * @return the owning {@link RadChartViewBase} instance.
     */
    public RadChartViewBase getChart() {
        return this.chart;
    }

    /**
     * Gets the {@link ChartElement} instance associated with this {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter}.
     *
     * @return the {@link com.telerik.widget.chart.engine.elementTree.ChartElement} instance.
     */
    protected abstract ChartElement getElement();

    /**
     * Gets the default Z-index for this {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter}.
     *
     * @return the default Z-index.
     */
    protected abstract int getDefaultZIndex();

    /**
     * Attaches the {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter} instance
     * to an owning {@link RadChartViewBase} instance.
     *
     * @param chart the owner to attach to.
     */
    public void attach(RadChartViewBase chart) {
        this.chart = chart;

        ChartElement element = this.getElement();
        if (element != null) {
            element.setPresenter(this);
        }
        this.onAttached();

        //This method was previously called in the updateUI logic. Now we want to apply
        //the palette when the series are added to the chart.
        this.updatePalette(true);
    }

    /**
     * Detaches the {@link com.telerik.widget.chart.visualization.common.ChartElementPresenter} instance
     * from the owner.
     */
    public void detach() {
        RadChartViewBase oldChart = this.chart;
        this.chart = null;
        this.getElement().setPresenter(null);
        this.onDetached(oldChart);
    }

    @Override
    public boolean getCanApplyPalette() {
        return this.chart != null && super.getCanApplyPalette();
    }

    /**
     * Called when attached to an owning {@link RadChartViewBase} instance.
     */
    protected void onAttached() {
    }

    /**
     * Called when detached from the owning {@link RadChartViewBase} instance.
     *
     * @param oldChart the previous owner.
     */
    protected void onDetached(RadChartViewBase oldChart) {
    }
}
