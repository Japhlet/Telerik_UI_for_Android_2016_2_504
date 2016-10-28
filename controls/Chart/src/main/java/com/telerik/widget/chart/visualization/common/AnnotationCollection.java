package com.telerik.widget.chart.visualization.common;

import com.telerik.widget.chart.visualization.annotations.ChartAnnotation;

public class AnnotationCollection<T extends ChartAnnotation> extends PresenterCollection<T> {

    /**
     * Creates an instance of the {@link AnnotationCollection} class with a specified {@link RadChartViewBase} as its owner.
     *
     * @param control the owning {@link RadChartViewBase}.
     */
    public AnnotationCollection(RadChartViewBase control) {
        super(control);
    }
}
