package com.telerik.widget.chart.visualization.behaviors;

public interface PanZoomListener {
    void onPan(double panX, double panY);
    void onZoom(double zoomX, double zoomY);
}
