package com.telerik.widget.dataform.engine;

import java.util.HashMap;
import java.util.IllegalFormatException;

public class MaximumLengthValidator extends PropertyValidatorBase {
    private int maximumLength = 8;

    public MaximumLengthValidator() {
        this.setNegativeMessage("The value entered must contain at most %s characters.");
    }

    public int getMaximumLength() {
        return maximumLength;
    }

    public void setMaximumLength(int maximumLength) {
        this.maximumLength = maximumLength;
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return input.toString().length() <= maximumLength;
    }

    @Override
    public String getNegativeMessage() {
        String negativeMessage = super.getNegativeMessage();
        if(negativeMessage == null) {
            return null;
        }
        try {
            return String.format(negativeMessage, Integer.toString(maximumLength));
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
            return String.format(positiveMessage, Integer.toString(maximumLength));
        } catch (IllegalFormatException ex) {
            return positiveMessage;
        }
    }

    @Override
    public void applyParams(HashMap<String, Object> params) {
        super.applyParams(params);

        for(String key : params.keySet()) {
            Object value = params.get(key);
            String keyLowerCase = key.toLowerCase();
            if(keyLowerCase.contains("length")) {
                setMaximumLength(((Number) value).intValue());
            }
        }
    }
}