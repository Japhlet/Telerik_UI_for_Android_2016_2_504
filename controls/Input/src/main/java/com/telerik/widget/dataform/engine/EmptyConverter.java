package com.telerik.widget.dataform.engine;

public class EmptyConverter implements PropertyConverter {
    @Override
    public Object convertTo(Object source) {
        return source;
    }

    @Override
    public Object convertFrom(Object source) {
        return source;
    }
}
