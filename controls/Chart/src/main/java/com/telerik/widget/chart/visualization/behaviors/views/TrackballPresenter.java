package com.telerik.widget.chart.visualization.behaviors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.telerik.android.common.math.RadRect;
import com.telerik.android.primitives.widget.tooltip.contracts.TooltipAdapter;
import com.telerik.android.primitives.widget.tooltip.views.TooltipPresenterBase;
import com.telerik.widget.chart.R;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.view.ChartElementPresenter;
import com.telerik.widget.chart.visualization.behaviors.ChartTrackBallBehavior;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.BarSeries;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.OhlcSeriesBase;
import com.telerik.widget.chart.visualization.cartesianChart.series.categorical.RangeBarSeries;
import com.telerik.widget.chart.visualization.common.PointTemplateSeries;

public class TrackballPresenter extends TooltipPresenterBase {

    private Paint linePaint = new Paint();
    private Paint intersectionPointPaint = new Paint();
    private float indicatorRadius = 0;

    public TrackballPresenter(Context context, TooltipAdapter owner) {
        this(context, owner, R.layout.trackball_container);
    }

    public TrackballPresenter(Context context, TooltipAdapter owner, int popupLayout) {
        super(context, owner, popupLayout);

        this.linePaint.setColor(Color.BLACK);

        this.indicatorRadius = this.getResources().getDimension(R.dimen.trackball_indicator_radius);
    }

    public Paint getLinePaint() {
        return this.linePaint;
    }

    public void setLinePaint(Paint value) {
        if(value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }
        this.linePaint = value;
    }

    public Paint getIntersectionPointPaint() {
        return this.intersectionPointPaint;
    }

    public float getIndicatorRadius() {
        return this.indicatorRadius;
    }

    public void setIndicatorRadius(float value) {
        this.indicatorRadius = value;
    }

    public void setIntersectionPointPaint(Paint value) {
        if(value == null) {
            throw new IllegalArgumentException("value cannot be null.");
        }

        this.intersectionPointPaint = value;
    }

    @Override
    protected void openCore(Point dataPointLocation) {
        if (!((ChartTrackBallBehavior) this.tooltipAdapter).getShowTrackInfo()) {
            return;
        }

        super.openCore(dataPointLocation);
    }

    @Override
    protected void onDrawCore(Canvas canvas) {
        if (this.isOpen()) {
            this.drawLine(canvas);
            this.drawIntersectionPoints(canvas);
        }

        super.onDrawCore(canvas);
    }

    protected void drawIntersectionPoints(Canvas canvas) {
        ChartTrackBallBehavior owner = (ChartTrackBallBehavior) this.tooltipAdapter;
        if (!owner.getShowIntersectionPoints()) {
            return;
        }

        for (DataPoint point : owner.getRelatedDataPoints()) {
            ChartElementPresenter parentPresenter = point.getParent().getPresenter();
            if (parentPresenter instanceof BarSeries ||
                    parentPresenter instanceof RangeBarSeries ||
                    parentPresenter instanceof OhlcSeriesBase ||
                    point.isEmpty) {
                continue;
            }

            Point center = point.getCenter();

            PointTemplateSeries series = (PointTemplateSeries)point.getParent().getPresenter();

            if(intersectionPointPaint.getColor() == 0) {
                intersectionPointPaint.setColor(series.getLegendFillColor());
            }
            canvas.drawCircle(center.x, center.y, this.indicatorRadius, this.intersectionPointPaint);
        }
    }

    @Override
    protected boolean shouldPreventPointOverlap() {
        return false;
    }

    protected void drawLine(Canvas canvas) {
        RadRect clipRect = this.tooltipAdapter.getPlotAreaClip();

        if (this.tooltipAdapter.alignTooltipVertically()) {
            float x = this.targetPoint.x;
            canvas.drawLine(x, (float)clipRect.getY(), x, (float)clipRect.getBottom(), this.linePaint);
        } else {
            float y = this.targetPoint.y;
            canvas.drawLine((float)clipRect.getX(), y, (float)clipRect.getRight(), y, this.linePaint);
        }
    }

    @Override
    protected RadRect calculateTooltipBounds(Point location) {
        int w = this.tooltipContentContainer.getMeasuredWidth();
        int h = this.tooltipContentContainer.getMeasuredHeight();

        if (this.tooltipAdapter.alignTooltipVertically()) {
            int left = location.x - (w / 2);
            return new RadRect(left, this.tooltipAdapter.availableLayoutSlot().top - this.tooltipContentContainer.getPaddingTop(), w, h);
        }

        int top = location.y - (h / 2);
        return new RadRect(this.tooltipAdapter.availableLayoutSlot().right - w, top, w, h);
    }
}
