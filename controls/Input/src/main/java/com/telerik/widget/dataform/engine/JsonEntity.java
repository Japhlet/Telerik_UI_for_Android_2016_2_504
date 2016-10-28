package com.telerik.widget.dataform.engine;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonEntity extends EntityCore {
    private ArrayList<EntityProperty> properties;
    private JSONObject json;

    public JsonEntity(JSONObject json) {
        this.json = json;
        this.properties = this.createProperties();
    }

    @Override
    public Object getProperty(EntityProperty source) {
        try {
            return json.get(source.name());
        } catch (JSONException e) {
            Log.e("DataForm", String.format("Error getting value of property %s.", source.name()), e);
        }
        return null;
    }

    @Override
    public void setProperty(EntityProperty target, Object value) {
        try {
            json.put(target.name(), value);
        } catch (JSONException e) {
            Log.e("DataForm", String.format("Error setting value %s to property %s.", String.valueOf(value), target.name()), e);
        }
    }

    private ArrayList<EntityProperty> createProperties() {
        ArrayList<EntityProperty> result = new ArrayList<>();

        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            Class type = resolvePropertyType(key, json);
            JsonEntityProperty property = new JsonEntityProperty(key, type, this);
            result.add(property);
        }

        return result;
    }

    private Class resolvePropertyType(String propertyName, JSONObject json) {
        try {
            Object value = json.get(propertyName);
            if(value == JSONObject.NULL) {
                return Object.class;
            }
            return value.getClass();
        } catch (JSONException ex) {
            return Object.class;
        }
    }

    @Override
    public Object getSourceObject() {
        return this.json;
    }

    @Override
    public Iterable<EntityProperty> properties() {
        return this.properties;
    }
}