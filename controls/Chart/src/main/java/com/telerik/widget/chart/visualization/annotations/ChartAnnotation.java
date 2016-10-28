package com.telerik.widget.chart.visualization.annotations;

import android.graphics.Canvas;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModelWithAxes;
import com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.visualization.common.ChartElementPresenter;
import com.telerik.widget.chart.visualization.common.ChartLayoutContext;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;

/**
 * This is the base class for all classes that are used to show annotations on a Chart.
 * This class is abstract and you should not use it directly in your application.
 */
public abstract class ChartAnnotation extends ChartElementPresenter {

    public static final int ANNOTATION_Z_INDEX = 0;
    private boolean clipToPlotArea = true;

    /**
     * Creates an instance of the {@link ChartAnnotation} class with a specified application context.
     */
    public ChartAnnotation() {
    }

    private void update() {
        if (this.getModel().isUpdated()) {
            this.updatePresenters();
        }
    }

    /**
     * Gets a boolean value determining whether the annotation
     * will be visually clipped to fit the plot area.
     *
     * @return <code>true</code> if the annotation will be clipped, otherwise <code>false</code>.
     */
    public boolean getClipToPlotArea() {
        return this.clipToPlotArea;
    }

    /**
     * Sets a boolean value determining whether the annotation will be visually clipped
     * to fit the plot area.
     *
     * @param value <code>true</code> if the annotation will be clipped, otherwise <code>false</code>.
     */
    public void setClipToPlotArea(boolean value) {
        this.clipToPlotArea = value;
    }

    /**
     * Gets an instance of the {@link ChartAnnotationModel} class representing the Chart engine model
     * associated with this annotation.
     *
     * @return the {@link com.telerik.widget.chart.engine.decorations.annotations.ChartAnnotationModel} instance.
     */
    public abstract ChartAnnotationModel getModel();

    /**
     * Updates the presenters.
     */
    protected void updatePresenters() {
    }

    @Override
    protected ChartElement getElement() {
        return this.getModel();
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        super.updateUICore(context);

        this.update();
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);

        if (this.getClipToPlotArea()) {
            canvas.save(Canvas.CLIP_SAVE_FLAG);
            canvas.clipRect(Util.convertToRectF(this.getChart().getPlotAreaClip()));
        }

        this.drawCore(canvas);

        if (this.getClipToPlotArea()) {
            canvas.restore();
        }
    }

    protected abstract void drawCore(Canvas canvas);

    @Override
    protected int getDefaultZIndex() {
        return ChartAnnotation.ANNOTATION_Z_INDEX + this.getModel().index();
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        ((ChartAreaModelWithAxes) this.getChart().chartAreaModel()).addAnnotation(this.getModel());
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        ((ChartAreaModelWithAxes) oldChart.chartAreaModel()).removeAnnotation(this.getModel());
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        PaletteEntry entry = palette.getEntry(this.getPaletteFamilyCore(), this.getModel().collectionIndex());
        if (entry == null) {
            return;
        }

        this.processPaletteEntry(entry);
    }

    protected abstract void processPaletteEntry(PaletteEntry entry);
}
