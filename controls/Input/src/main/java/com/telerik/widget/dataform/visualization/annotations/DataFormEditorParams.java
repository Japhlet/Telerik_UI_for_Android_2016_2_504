package com.telerik.widget.dataform.visualization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataFormEditorParams {
    double DEFAULT_MIN = Double.MIN_VALUE;
    double DEFAULT_MAX = Double.MAX_VALUE;
    double DEFAULT_STEP = 0;

    double min() default DEFAULT_MIN;
    double max() default DEFAULT_MAX;
    double step() default DEFAULT_STEP;
}