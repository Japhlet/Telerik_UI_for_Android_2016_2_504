package com.telerik.widget.dataform.visualization.annotations;

import com.telerik.widget.dataform.engine.EmptyValidator;
import com.telerik.widget.dataform.engine.PropertyValidator;


public @interface DataFormValidator {
    Class<? extends PropertyValidator> type() default EmptyValidator.class;
    DataFormValidatorParams params() default @DataFormValidatorParams;
}