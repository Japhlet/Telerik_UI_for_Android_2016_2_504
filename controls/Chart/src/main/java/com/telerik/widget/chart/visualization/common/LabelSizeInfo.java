package com.telerik.widget.chart.visualization.common;

import android.text.StaticLayout;

import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadSize;

/**
 * Instances of this class hold information about the size of a single label on the {@link Axis}.
 */
public class LabelSizeInfo {
    public StaticLayout textLayout;
    public RadSize size = RadSize.getEmpty();
    public RadSize untransformedSize = RadSize.getEmpty();
    public RadPoint transformOffset = RadPoint.getEmpty();
}