package com.telerik.widget.dataform.engine;

public interface PropertyValidator {
    void validate(Object input, String propertyName, ValidationCompletedListener callback);
    String getPositiveMessage();
    void setPositiveMessage(String value);
    String getNegativeMessage();
    void setNegativeMessage(String value);
}
