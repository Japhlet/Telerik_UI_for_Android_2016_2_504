package com.telerik.widget.chart.visualization.cartesianChart.series.pointrenderers;

import android.graphics.Paint;

import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.OhlcSeriesBase;
import com.telerik.widget.palettes.PaletteEntry;

import java.util.HashMap;

public abstract class OhlcPointRendererBase extends ChartDataPointRendererBase<OhlcSeriesBase> {

    protected Paint upStrokePaint = new Paint();
    protected Paint strokePaint;
    private HashMap<DataPoint, PaletteEntry> pointColors = new HashMap<DataPoint, PaletteEntry>();

    public OhlcPointRendererBase(OhlcSeriesBase series) {
        super(series);
    }

    public HashMap<DataPoint, PaletteEntry> pointColors() {
        return this.pointColors;
    }

    public Paint upStrokePaint() {
        return this.upStrokePaint;
    }
}
