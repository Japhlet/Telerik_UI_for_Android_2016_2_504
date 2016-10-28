package com.telerik.widget.dataform.engine;

public class EmptyValidator extends PropertyValidatorBase {
    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return true;
    }
}
