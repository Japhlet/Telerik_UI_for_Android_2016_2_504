package com.telerik.widget.feedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by ginev on 15/05/2014.
 */
public class JSONHelper {


    public static JSONArray toJSONArray(ArrayList<?> items) {
        JSONArray result = new JSONArray();

        for (Object item : items) {
            result.put(toJSONObject(item));
        }

        return result;
    }

    public static JSONObject toJSONObject(Object source) {
        JSONObject result = new JSONObject();

        Method[] objectMethods = source.getClass().getDeclaredMethods();

        for (int i = 0; i < objectMethods.length; i++) {
            Method m = objectMethods[i];
            if (m.getName().contains("get")) {
                Object value = null;
                try {
                    value = m.invoke(source);
                } catch (Exception e) {

                }
                if (value == null){
                    continue;
                }

                String key = m.getName().replace("get", "");
                try {

                    if (value.getClass().getPackage().getName().contains("com.telerik.widget.feedback")) {
                        try {
                            JSONObject typedObject = toJSONObject(value);
                            result.put(key, typedObject);
                        } catch (Exception e) {

                        }
                    } else  {
                        result.put(key, value);
                    }
                } catch (JSONException e) {

                }
            }
        }

        return result;
    }

    public static void init(Object target, JSONObject source) {
        Method[] thisMethods = target.getClass().getDeclaredMethods();
        try {
            for (int i = 0; i < thisMethods.length; i++) {
                Method m = thisMethods[i];
                if (m.getName().contains("set")) {
                    String key = m.getName().replace("set", "");
                    try {
                        Object value = source.get(key);
                        if (value instanceof JSONObject) {
                            try {
                                JSONObject typedObject = (JSONObject) value;
                                Class imageClass = Class.forName("com.telerik.widget.feedback." + key);
                                Constructor<?> ctor = imageClass.getConstructor(JSONObject.class);
                                m.invoke(target, ctor.newInstance(typedObject));
                            } catch (Exception e) {

                            }
                        } else {
                            m.invoke(target, source.getString(key));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        } catch (Exception e) {

        }
    }
}
