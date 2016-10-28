package com.telerik.widget.dataform.engine;

import org.json.JSONObject;

public class JsonEntityProperty extends EntityPropertyCore {

    public JsonEntityProperty(String name, Class type, Entity owner) {
        super(name, type, owner);

        loadHeader();
    }

    private void loadHeader() {
        if(this.getHeader() == null) {
            this.setHeader(headerFormat(name()));
        }
    }

    /**
     * Sets a value that, in case of successful validation, will be persisted on the source object's
     * property when {@code EntityProperty.commit()} is called.
     *
     * @param value the value candidate to set.
     */
    @Override
    public void setValueCandidate(Object value) {
        if(value == null) {
            value = JSONObject.NULL;
        }

        super.setValueCandidate(value);
    }

    /**
     * Gets the value currently persisted on the source object.
     *
     * @return the value;
     */
    @Override
    public Object getValue() {
        Object value = super.getValue();

        if(value == JSONObject.NULL) {
            value = null;
        }

        return value;
    }

    @Override
    protected String headerFormat(String name) {
        String header = super.headerFormat(name);
        return capitalized(header);
    }

    private String capitalized(String original) {
        if(original == null || original.isEmpty()) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}