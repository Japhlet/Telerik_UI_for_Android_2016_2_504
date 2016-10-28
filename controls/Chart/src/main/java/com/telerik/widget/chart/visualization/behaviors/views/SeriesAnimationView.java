package com.telerik.widget.chart.visualization.behaviors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.telerik.widget.chart.visualization.common.ChartSeries;

public class SeriesAnimationView extends View {
    private ChartSeries series;
    private int activeAnimationCount;

    public SeriesAnimationView(Context context, ChartSeries series) {
        super(context);
        this.series = series;
    }

    public void incrementAnimationCount() {
        activeAnimationCount++;
    }

    public void decrementAnimationCount() {
        activeAnimationCount--;
    }

    public int getActiveAnimationCount() {
        return activeAnimationCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(this.series == null) {
            return;
        }

        this.series.render(canvas);
        this.series.postRender(canvas);
    }

    public ChartSeries getSeries() {
        return series;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(w, h);
    }
}
