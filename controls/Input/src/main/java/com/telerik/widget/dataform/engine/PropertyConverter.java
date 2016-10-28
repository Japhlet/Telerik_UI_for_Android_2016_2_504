package com.telerik.widget.dataform.engine;

public interface PropertyConverter {
    Object convertTo(Object source);
    Object convertFrom(Object source);
}
