package com.telerik.widget.chart.visualization.pieChart;

import com.telerik.android.common.math.RadMath;
import com.telerik.android.common.math.RadPoint;

class PieUpdateContext {

    RadPoint center;
    double radius;
    double diameter;
    double startAngle;

    RadPoint getCenterWithOffset(double offsetInPixels, double angle) {
        if (offsetInPixels == 0) {
            return this.center;
        }

        return RadMath.getArcPoint(angle, this.center, offsetInPixels);
    }
}