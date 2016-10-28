package com.telerik.widget.dataform.visualization.metadata;

import com.telerik.widget.dataform.engine.EntityProperty;

import java.util.HashMap;

public class MetadataProvider {
    private HashMap<String, Object> propertyMetadataMap;

    public MetadataProvider(){
        this.propertyMetadataMap = new HashMap<String, Object>();
    }

    /**
     * Registers the provided property metadata for the specified entity property.
     * @param property an instance of the {@link EntityProperty} class representing the property to register
     *                 the metadata for.
     */
    public void addMetadataForProperty(EntityProperty property, Object metadata) {
        this.propertyMetadataMap.put(property.name(), metadata);
    }


    /**
     * Resolves the metadata registered for the specified entity property. Returns {@code null}
     * if no metadata is available.
     * @param property an instance of the {@link EntityProperty} class representing the
     *                 property to resolve the metadata for.
     */
    public Object resolveMetadataForProperty(EntityProperty property) {
        return this.propertyMetadataMap.get(property.name());
    }
}
