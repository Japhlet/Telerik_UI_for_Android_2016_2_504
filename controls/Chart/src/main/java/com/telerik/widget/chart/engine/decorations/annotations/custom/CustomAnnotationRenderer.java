package com.telerik.widget.chart.engine.decorations.annotations.custom;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.android.common.math.RadRect;
import com.telerik.android.common.math.RadSize;

/**
 * This interface is used in the RadChartView to draw annotations on the chart area.
 */
public interface CustomAnnotationRenderer {

    RadSize measureContent(Object content);

    void render(Object content, RadRect layoutSlot, Canvas canvas, Paint paint);
}
