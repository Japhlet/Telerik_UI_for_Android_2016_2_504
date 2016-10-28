package com.telerik.widget.dataform.engine;

import java.util.HashMap;
import java.util.IllegalFormatException;

public class MinimumLengthValidator extends PropertyValidatorBase {
    private int minimumLength = 8;

    public MinimumLengthValidator() {
        this.setNegativeMessage("The value entered must contain at least %s characters.");
    }

    public int getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(int minimumLength) {
        this.minimumLength = minimumLength;
    }

    @Override
    protected boolean validateCore(Object input, String propertyName) {
        return input.toString().length() >= minimumLength;
    }

    @Override
    public String getNegativeMessage() {
        String negativeMessage = super.getNegativeMessage();
        if(negativeMessage == null) {
            return null;
        }
        try {
            return String.format(negativeMessage, Integer.toString(minimumLength));
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
            return String.format(positiveMessage, Integer.toString(minimumLength));
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
                setMinimumLength(((Number) value).intValue());
            }
        }
    }
}
