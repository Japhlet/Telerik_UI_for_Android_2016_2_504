package com.telerik.widget.chart.visualization.behaviors.views;

import android.content.Context;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;

public class SeriesAnimationViewport extends ViewGroup {
    RadChartViewBase chart;

    public SeriesAnimationViewport(Context context) {
        super(context);

        this.setClipChildren(true);
    }

    public void setChart(RadChartViewBase value) {
        this.chart = value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(chart == null) {
            return;
        }

        View parent = (View)this.getParent();
        int w = parent.getMeasuredWidth();
        int h = parent.getMeasuredHeight();


        for(int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            child.measure(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(chart == null) {
            return;
        }

        RectF plotArea = Util.convertToRectF(chart.getPlotAreaClip());
        int x = Math.round(plotArea.left);
        int y = Math.round(plotArea.top);

        // Twice the offset because the animation view needs to be in AnimationBehavior coordinates.
        left -= x * 2;
        top -= y * 2;

        for(int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            child.layout(left, top, left + chart.getMeasuredWidth(), top + chart.getMeasuredHeight());
        }
    }
}
