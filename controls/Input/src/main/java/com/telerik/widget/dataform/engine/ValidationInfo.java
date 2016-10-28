package com.telerik.widget.dataform.engine;

public class ValidationInfo {
    private String message;
    private Object editorValue;
    private String propertyName;
    private boolean isValid;

    public ValidationInfo(boolean isValid, String message, String propertyName, Object editorValue) {
        this.propertyName = propertyName;
        this.isValid = isValid;
        this.message = message;
        this.editorValue = editorValue;
    }

    public String message() {
        return message;
    }

    public Object editorValue() {
        return editorValue;
    }

    public boolean isValid(){
        return this.isValid;
    }

    public String propertyName() {
        return propertyName;
    }
}
