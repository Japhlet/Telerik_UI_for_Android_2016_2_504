package com.telerik.widget.dataform.engine;

import java.util.HashMap;

public abstract class PropertyValidatorBase implements PropertyValidator {

    private String positiveMessage = "";
    private String negativeMessage = "";

    @Override
    public void validate(Object input, String propertyName, ValidationCompletedListener callback) {
        boolean isValid = this.validateCore(input, propertyName);
        String message;
        if(isValid) {
            message = this.getPositiveMessage();
        } else {
            message = this.getNegativeMessage();
        }

        ValidationInfo info = new ValidationInfo(isValid, message, propertyName, input);
        callback.validationCompleted(info);
    }

    protected abstract boolean validateCore(Object input, String propertyName);

    @Override
    public String getPositiveMessage() {
        return positiveMessage;
    }

    @Override
    public void setPositiveMessage(String value) {
        positiveMessage = value;
    }

    @Override
    public String getNegativeMessage() {
        return negativeMessage;
    }

    @Override
    public void setNegativeMessage(String value) {
        negativeMessage = value;
    }

    public void applyParams(HashMap<String, Object> params) {
        for(String key : params.keySet()) {
            Object value = params.get(key);
            String keyLowerCase = key.toLowerCase();
            if(keyLowerCase.contains("success") || keyLowerCase.contains("positive")) {
                setPositiveMessage((String)value);
            } else if (keyLowerCase.contains("fail") || keyLowerCase.contains("error") || keyLowerCase.contains("negative")) {
                setNegativeMessage((String)value);
            }
        }
    }
}
