package com.telerik.widget.dataform.engine;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.IllegalFormatException;

public class RangeValidator extends PropertyValidatorBase {
    private double max;
    private double min;

    public RangeValidator() {
        this(0, 100);
    }

    public RangeValidator(double min, double max) {
        if(min >= max) {
            throw new IllegalArgumentException("Min cannot be equal to or greater than max.");
        }

        this.min = min;
        this.max = max;

        this.setNegativeMessage("Value must be within the [%.2f, %.2f] range.");
    }

    public double getMax() {
        return max;
    }

    public void setMax(double value) {
        max = value;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double value) {
        min = value;
    }

    @Override
    public String getNegativeMessage() {
        String negativeMessage = super.getNegativeMessage();
        if(negativeMessage == null) {
            return null;
        }
        try {
            return String.format(negativeMessage, min, max);
        } catch (IllegalFormatException ex) {
            return negativeMessage;
        }
    }

    @Override
    public String getPositiveMessage() {
        String positiveMessage = super.getPositiveMessage();
        if(positiveMessage == null) {
            return null;
        }
        try {
            return String.format(positiveMessage, min, max);
        } catch (IllegalFormatException ex) {
            return positiveMessage;
        }
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        if(input == null || input == JSONObject.NULL) {
            return false;
        }
        Number value = (Number)input;

        if(Double.compare(value.doubleValue(), min) < 0) {
            return false;
        } else if(Double.compare(value.doubleValue(), max) > 0) {
            return false;
        }

        return true;
    }

    @Override
    public void applyParams(HashMap<String, Object> params) {
        super.applyParams(params);

        for(String key : params.keySet()) {
            Object value = params.get(key);
            String keyLowerCase = key.toLowerCase();
            if(keyLowerCase.contains("min")) {
                setMin(((Number) value).doubleValue());
            } else if(keyLowerCase.contains("max")) {
                setMax(((Number) value).doubleValue());
            }
        }
    }
}
