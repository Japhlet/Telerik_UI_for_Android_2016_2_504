package com.telerik.widget.dataform.visualization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataFormValidatorParams {
    double DEFAULT_MIN = Double.MIN_VALUE;
    double DEFAULT_MAX = Double.MAX_VALUE;
    int DEFAULT_LENGTH = -1;
    String NULL = "This is Null";

    double min() default DEFAULT_MIN;
    double max() default DEFAULT_MAX;
    int length() default DEFAULT_LENGTH;
    /**
     * @deprecated Replaced by {@link #length()}
     */
    @Deprecated int minimumLength() default DEFAULT_LENGTH;
    String positiveMessage() default NULL;
    String negativeMessage() default NULL;
}