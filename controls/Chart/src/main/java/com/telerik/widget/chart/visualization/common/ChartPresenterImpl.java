package com.telerik.widget.chart.visualization.common;

import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ChartNode;

import java.util.List;

public class ChartPresenterImpl extends ChartElementPresenter {

    public ChartPresenterImpl(RadChartViewBase chart) {
        this.chart = chart;
    }

    @Override
    public int getCollectionIndex() {
        return -1;
    }

    @Override
    protected ChartElement getElement() {
        return null;
    }

    @Override
    protected int getDefaultZIndex() {
        return -1;
    }

    @Override
    protected void onUIUpdated() {
        this.requestRender();
    }

    @Override
    protected void processPaletteChanged() {
        super.processPaletteChanged();

        List<? extends ChartElementPresenter> presenters = this.chart.presenters();
        for (ChartElementPresenter presenter : presenters) {
            if (presenter.getCanApplyPalette()) {
                presenter.processPaletteChanged();
            }
        }
    }

    @Override
    protected String defaultPaletteFamily() {
        return "";
    }

    @Override
    protected void refreshNodeCore(ChartNode node) {
        this.chart.requestInvalidateArrange();
    }

    @Override
    protected void updateUICore(ChartLayoutContext context) {
        List<? extends ChartElementPresenter> presenters = this.chart.presenters();
        for (ChartElementPresenter presenter : presenters) {
            presenter.updateUI(context);
        }

        this.chart.stackedSeriesContext().clear();
    }
}
