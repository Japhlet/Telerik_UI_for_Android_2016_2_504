package com.telerik.widget.dataform.engine;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class represent an arbitrary business object with its collection of getters and setters. This
 * class exposes methods for invoking the corresponding getters and setters thus allowing the modification
 * of the values of the associated object by the data form control.
 */
public class EntityBase extends EntityCore {
    protected Object sourceObject;
    protected ArrayList<EntityProperty> properties;
    protected HashMap<String, Method> reflectionCache;

    /**
     * Creates an instance of the {@link Entity} class
     * with the provided source object.
     *
     * @param source     the source object.
     */
    public EntityBase(Object source) {
        this.sourceObject = source;
        this.properties = new ArrayList<>();
        this.reflectionCache = new HashMap<>();

        this.initializePropertyMap();
    }

    /**
     * Sets the given property with the provided value.
     *
     * @param target an instance of {@link EntityProperty} class representing the property to set.
     * @param value  the value to set.
     */
    public void setProperty(EntityProperty target, Object value) {
        Method setMethod = this.reflectionCache.get("set" + target.name());
        try {
            setMethod.invoke(this.sourceObject, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the value of the provided property.
     *
     * @param source an instance of the {@link EntityProperty} class representing the property to get.
     * @return the value of the property.
     */
    public Object getProperty(EntityProperty source) {
        Method getMethod = this.reflectionCache.get("get" + source.name());
        try {
            return getMethod.invoke(this.sourceObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns the {@link EntityProperty} instances
     * representing the editable fields of the associated source object.
     *
     * @return an {@link Iterable} implementation containing the editable properties.
     */
    public Iterable<EntityProperty> properties() {
        return this.properties;
    }

    public Object getSourceObject() {
        return sourceObject;
    }

    protected void initializePropertyMap() {
        LinkedHashMap<String, Class> coreProperties = this.resolveCoreProperties(this.sourceObject.getClass());

        for (String coreProperty : coreProperties.keySet()) {
            EntityProperty property = this.createProperty(coreProperty, coreProperties.get(coreProperty));
            this.properties.add(property);
        }
    }

    public EntityProperty createProperty(String coreProperty, Object propertyType) {
        return new EntityPropertyBase(coreProperty, (Class)propertyType, this);
    }

    protected LinkedHashMap<String, Class> resolveCoreProperties(Class entityType) {
        Method[] methods = entityType.getDeclaredMethods();

        LinkedHashMap<String, Class> result = new LinkedHashMap<>();

        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                if (method.getParameterTypes().length != 0) {
                    // We do not want getters with parameters.
                    continue;
                }
                String actualName = methodName.substring(3);
                this.reflectionCache.put(methodName, method);
                if (!result.containsKey(actualName)) {
                    result.put(actualName, method.getReturnType());
                }
            }

            if (methodName.startsWith("set")) {
                if (method.getParameterTypes().length != 1) {
                    // We do not want setters with parameter count different than 1
                    continue;
                }
                String actualName = methodName.substring(3);
                this.reflectionCache.put(methodName, method);
                if (!result.containsKey(actualName)) {
                    result.put(actualName, method.getParameterTypes()[0]);
                }
            }
        }
        return result;
    }
}
