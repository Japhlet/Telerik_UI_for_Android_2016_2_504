package com.telerik.widget.dataform.visualization.registries;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;
import com.telerik.widget.dataform.visualization.editors.DataFormCheckBoxEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormDecimalEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormIntegerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormSpinnerEditor;
import com.telerik.widget.dataform.visualization.editors.DataFormTextEditor;
import com.telerik.widget.dataform.visualization.viewers.DataFormCheckBoxViewer;
import com.telerik.widget.dataform.visualization.viewers.DataFormTextViewer;

import java.util.HashMap;

/**
 * This class serves as a registry for editors for a single {@link com.telerik.widget.dataform.visualization.RadDataForm}
 * instance.
 */
public class EditorRegistry {

    private HashMap<Class, Class> typeEditorMap;
    private HashMap<String, Class> propertyEditorMap;

    private HashMap<Class, Class> typeViewerMap;
    private HashMap<String, Class> propertyViewerMap;

    private RadDataForm dataForm;

    public EditorRegistry(RadDataForm dataForm){
        this.typeEditorMap = new HashMap<>();
        this.propertyEditorMap = new HashMap<>();

        this.typeViewerMap = new HashMap<>();
        this.propertyViewerMap = new HashMap<>();

        this.dataForm = dataForm;

        this.resetEditorTypeRegistry();
        this.resetViewerTypeRegistry();
    }

    public EntityPropertyEditor createEditorFromMetadata(EntityProperty property) {
        Class type = property.getEditorType();
        if(type == EntityPropertyEditor.class || type == null) {
            return null;
        }

        try {
            return property.getEditorType().getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
        } catch (Exception ex) {
            throw new Error(String.format("The data form editor for property %s must have a constructor that accepts a Context and an EntityProperty.", property.name()), ex);
        }
    }

    public EntityPropertyViewer createViewerFromMetadata(EntityProperty property) {
        Class type = property.getViewerType();
        if(type == EntityPropertyViewer.class || type == null) {
            return null;
        }

        try {
            return property.getViewerType().getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
        } catch (Exception ex) {
            throw new Error(String.format("The data form viewer for property %s must have a constructor that accepts a Context and an EntityProperty.", property.name()), ex);
        }
    }

    private void resetViewerTypeRegistry() {
        typeViewerMap.clear();

        Class[] integers = new Class[] { Integer.class, int.class, Long.class, long.class };
        addViewerForTypes(DataFormTextViewer.class, integers);
        addViewerForTypes(DataFormTextViewer.class, new Class[]{String.class});

        Class[] floats = new Class[] { Float.class, Double.class, float.class, double.class };
        addViewerForTypes(DataFormTextViewer.class, floats);

        Class[] booleans = new Class[] { boolean.class, Boolean.class };
        addViewerForTypes(DataFormCheckBoxViewer.class, booleans);

        addViewerForTypes(DataFormTextViewer.class, new Class[]{Enum.class});
    }

    public void resetEditorTypeRegistry() {
        typeEditorMap.clear();

        Class[] integers = new Class[] { Integer.class, int.class, Long.class, long.class };
        addEditorForTypes(DataFormIntegerEditor.class, integers);
        addEditorForTypes(DataFormTextEditor.class, new Class[]{String.class});

        Class[] floats = new Class[] { Float.class, Double.class, float.class, double.class };
        addEditorForTypes(DataFormDecimalEditor.class, floats);

        Class[] booleans = new Class[] { boolean.class, Boolean.class };
        addEditorForTypes(DataFormCheckBoxEditor.class, booleans);

        addEditorForTypes(DataFormSpinnerEditor.class, new Class[]{Enum.class});
    }

    public void addViewerForTypes(Class editorType, Class[] propertyTypes) {
        for(Class cls : propertyTypes) {
            typeViewerMap.put(cls, editorType);
        }
    }

    public void addEditorForTypes(Class editorType, Class[] propertyTypes) {
        for(Class cls : propertyTypes) {
            typeEditorMap.put(cls, editorType);
        }
    }

    public void addViewForProperty(Class viewerType, String propertyName) {
        propertyViewerMap.put(propertyName, viewerType);
    }

    public void addEditorForProperty(Class editorType, String propertyName) {
        propertyEditorMap.put(propertyName, editorType);
    }

    public EntityPropertyEditor resolveEditorForType(EntityProperty property){
        Class propertyType = property.type();

        Class editorType = getViewerType(typeEditorMap, propertyType);
        if (editorType != null){
            try {
                return (EntityPropertyEditor)editorType.getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
            } catch (Exception ex) {
                throw new Error(String.format("The data form editor for property %s must have a constructor that accepts a Context and an EntityProperty.", property.name()), ex);
            }
        }
        return null;
    }

    public EntityPropertyEditor resolveEditorForProperty(EntityProperty property){
        if (this.propertyEditorMap.containsKey(property.name())){
            try {
                return (EntityPropertyEditor)this.propertyEditorMap.get(property).getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
            } catch (Exception ex) {
                throw new Error("The data form editor for this property must have a constructor that accepts a Context and an EntityProperty.", ex);
            }
        }

        return null;
    }

    public EntityPropertyViewer resolveViewerForType(EntityProperty property){
        Class propertyType = property.type();

        Class viewerType = getViewerType(typeViewerMap, propertyType);

        if (viewerType != null){
            try {
                return (EntityPropertyViewer)viewerType.getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
            } catch (Exception ex) {
                throw new Error("The data form viewer for this property must have a public constructor that accepts a Context and an EntityProperty.", ex);
            }
        }
        return null;
    }

    public EntityPropertyViewer resolveViewerForProperty(EntityProperty property){
        if (this.propertyViewerMap.containsKey(property.name())){
            try {
            return (EntityPropertyViewer)this.propertyViewerMap.get(property).getConstructor(RadDataForm.class, EntityProperty.class).newInstance(dataForm, property);
            } catch (Exception ex) {
                throw new Error("The data form viewer for this property must have a public constructor that accepts a Context and an EntityProperty.", ex);
            }
        }
        return null;
    }

    private Class getViewerType(HashMap<Class, Class> map, Class type) {
        for(Class key : map.keySet()) {
            if(key.isAssignableFrom(type)) {
                return map.get(key);
            }
        }

        return null;
    }
}
