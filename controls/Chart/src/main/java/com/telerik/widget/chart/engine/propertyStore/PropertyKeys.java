package com.telerik.widget.chart.engine.propertyStore;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Property key registrar.
 */
public final class PropertyKeys {

    private static int counter = 0;
    private static Map<Type, PropertyLookup> properties = new HashMap<Type, PropertyLookup>(32);
    private static SparseIntArray propertyFlags = new SparseIntArray(32);

    private PropertyKeys() {
    }

    /**
     * Registers a property of the given type with the given name.
     *
     * @param type         The property type.
     * @param propertyName The property name.
     * @return Returns the property key.
     */
    public static int register(Type type, String propertyName) {
        PropertyLookup lookup = findProperties(type, false);
        if (lookup == null) {
            lookup = new PropertyLookup();
            properties.put(type, lookup);
        }

        int key = counter++;
        lookup.namesByKey.put(key, propertyName);
        lookup.keysByName.put(propertyName, key);

        return key;
    }

    /**
     * Registers a property of the given type with the given name and {@link ChartAreaInvalidateFlags}.
     *
     * @param type         The property type.
     * @param propertyName The property name.
     * @param flags        The invalidate flags.
     * @return Returns the property key.
     */
    public static int register(Type type, String propertyName, int flags) {
        int key = register(type, propertyName);
        propertyFlags.put(key, flags);

        return key;
    }

    /**
     * Gets the {@link ChartAreaInvalidateFlags} for the given property.
     *
     * @param key The property key.
     */
    public static int getPropertyFlags(int key) {
        return propertyFlags.get(key, ChartAreaInvalidateFlags.NONE);
    }

    /**
     * Gets the property name given the property key.
     *
     * @param type The property type.
     * @param key  The property key.
     */
    public static String getNameByKey(Type type, int key) {
        Type searchType = type;
        PropertyLookup props = findProperties(searchType, true);
        String name = "";
        // traverse all the properties down in the type hierarchy
        while (props != null) {
            name = props.namesByKey.get(key, "");
            if (!name.equals("")) {
                return name;
            }

            searchType = searchType.getClass().getSuperclass();
            props = findProperties(searchType, true);
        }

        return name;
    }

    private static PropertyLookup findProperties(Type type, boolean lookUpBase) {
        if (!lookUpBase) {
            return properties.get(type);
        }

        Type currentType = type;
        Type baseType = PropertyBagObject.class;

        while (currentType != null && currentType != baseType) {
            if (properties.containsKey(currentType)) {
                return properties.get(currentType);
            }

            currentType = currentType.getClass().getSuperclass();
        }

        return null;
    }
}

class PropertyLookup {
    public SparseArray<String> namesByKey = new SparseArray<String>(8);
    public Map<String, Integer> keysByName = new HashMap<String, Integer>(8);
}

