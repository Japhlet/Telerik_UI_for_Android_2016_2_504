package com.telerik.widget.chart.visualization.pieChart;

import android.graphics.RectF;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;
import com.telerik.widget.chart.engine.dataPoints.PieDataPoint;

class DoughnutSegment extends PieSegment {

    DoughnutSegment(DoughnutSeries series) {
        super(series);
    }

    @Override
    void updatePaths(PieDataPoint point, PieUpdateContext context) {

        if (point.sweepAngle() == 0) {
            this.fillPath.reset();
            this.strokePath.reset();
            this.arcPath.reset();
            return;
        }

        RadPoint centerPoint = context.center;

        if (point.getRelativeOffsetFromCenter() > 0) {

            double offsetInPixels = (int) (context.radius * point.getRelativeOffsetFromCenter());
            double middleAngle = point.startAngle() + (point.sweepAngle() / 2);

            centerPoint = context.getCenterWithOffset(offsetInPixels, middleAngle);
        }

        DoughnutUpdateContext doughnutContext = (DoughnutUpdateContext) context;

        double radius = context.radius;
        double innerRadius = doughnutContext.innerRadiusFactor * context.radius;

        double strokeWidth = this.strokePaint.getStrokeWidth();
        double strokePadding = strokeWidth / 2;
        double arcWidth = this.arcPaint.getStrokeWidth();
        double arcPadding = strokeWidth + arcWidth / 2;

        double sweepAngle = point.sweepAngle();
        double startAngle = point.startAngle();

        double left = centerPoint.getX() - radius;
        double top = centerPoint.getY() - radius;
        double right = left + context.diameter;
        double bottom = top + context.diameter;

        double radiusDifference = radius - innerRadius;
        double innerDiameter = 2 * innerRadius;

        RectF fillOval = new RectF((float) left, (float) top, (float) right, (float) bottom);
        RectF strokeOval = new RectF((float) (left + strokePadding), (float) (top + strokePadding), (float) (right - strokePadding), (float) (bottom - strokePadding));
        RectF arcOval = new RectF((float) (left + arcPadding), (float) (top + arcPadding), (float) (right - arcPadding), (float) (bottom - arcPadding));

        RectF innerOval = new RectF(
                (float) (left + radiusDifference + strokePadding),
                (float) (top + radiusDifference + strokePadding),
                (float) (left + radiusDifference + innerDiameter - strokePadding),
                (float) (top + radiusDifference + innerDiameter - strokePadding));

        double sliceOffset = sweepAngle == 360 ? 0 : (float) this.series.getSliceOffset();

        double offsetSweepAngle = this.getAngleWithOffset(sweepAngle, (sliceOffset + strokeWidth), (float) context.radius);
        double angleDifference = sweepAngle - offsetSweepAngle;
        double offsetStartAngle = startAngle + angleDifference / 2;

        double innerOffsetSweepAngle = this.getAngleWithOffset(offsetSweepAngle, (sliceOffset + strokeWidth) / 2, innerRadius);
        double innerAngleDifference = offsetSweepAngle - innerOffsetSweepAngle;
        double innerOffsetStartAngle = offsetStartAngle + innerAngleDifference / 2;

        RadPoint arcPointFill = RadMath.getArcPoint(innerOffsetStartAngle + innerOffsetSweepAngle, centerPoint, innerRadius - strokePadding);
        RadPoint arcPointStroke = RadMath.getArcPoint(innerOffsetStartAngle + innerOffsetSweepAngle, centerPoint, innerRadius - strokeWidth);
        RadPoint segmentEndPoint = RadMath.getArcPoint(offsetStartAngle, centerPoint, context.radius);

        this.fillPath.reset();
        this.fillPath.addArc(fillOval, (float) offsetStartAngle, (float) offsetSweepAngle);
        this.fillPath.lineTo((float) arcPointFill.getX(), (float) arcPointFill.getY());
        this.fillPath.addArc(innerOval, (float) (innerOffsetStartAngle + innerOffsetSweepAngle), (float) -innerOffsetSweepAngle);
        this.fillPath.lineTo((float) segmentEndPoint.getX(), (float) segmentEndPoint.getY());

        this.strokePath.reset();
        this.strokePath.addArc(strokeOval, (float) offsetStartAngle, (float) offsetSweepAngle);
        this.strokePath.lineTo((float) arcPointStroke.getX(), (float) arcPointStroke.getY());
        this.strokePath.addArc(innerOval, (float) (innerOffsetStartAngle + innerOffsetSweepAngle), (float) -innerOffsetSweepAngle);
        this.strokePath.lineTo((float) segmentEndPoint.getX(), (float) segmentEndPoint.getY());

        this.arcPath.reset();
        this.arcPath.addArc(arcOval, (float) offsetStartAngle, (float) offsetSweepAngle);

        this.center = RadMath.getArcPoint(offsetStartAngle + offsetSweepAngle / 2, context.center, (doughnutContext.radius + innerRadius) / 2.0);
    }
}