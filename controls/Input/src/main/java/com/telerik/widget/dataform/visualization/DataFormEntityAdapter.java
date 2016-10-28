package com.telerik.widget.dataform.visualization;

import android.content.Context;

import com.telerik.android.common.Function;
import com.telerik.widget.dataform.engine.Entity;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;
import com.telerik.widget.dataform.visualization.registries.EditorRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This adapter is used by RadDataForm to create editors for the object being edited.
 */
public class DataFormEntityAdapter {
    private EditorRegistry editorRegistry;
    private ArrayList<EntityProperty> properties;
    private Function<EntityProperty, EntityPropertyEditor> editorProvider;
    private Function<EntityProperty, EntityPropertyViewer> viewerProvider;

    /**
     * Creates an instance of {@link DataFormEntityAdapter} with the provided arguments.
     *
     * @param dataForm the data form associated with this adapter.
     */
    public DataFormEntityAdapter(RadDataForm dataForm) {
        this.properties = new ArrayList<>();

        this.editorRegistry = new EditorRegistry(dataForm);
    }

    /**
     * Sets an editor provider callback.
     */
    public void setEditorProvider(Function<EntityProperty, EntityPropertyEditor> provider) {
        this.editorProvider = provider;
    }

    /**
     * Sets a viewer provider callback.
     */
    public void setViewerProvider(Function<EntityProperty, EntityPropertyViewer> provider) {
        this.viewerProvider = provider;
    }

    /**
     * Gets the editor registry.
     */
    public EditorRegistry getEditorRegistry() {
        return editorRegistry;
    }

    /**
     * Gets a list of editors for a given entity.
     */
    public Iterable<EntityPropertyViewer> getEditorsForEntity(Entity entity) {
        generateProperties(entity);

        ArrayList<EntityPropertyViewer> result = new ArrayList<>();
        for (EntityProperty editableProperty : properties) {
            EntityPropertyViewer editor = editableProperty.getReadOnly() ? this.createViewer(editableProperty) : this.createEditor(editableProperty);
            if(editor != null) {
                HashMap<String, Object> editorParams = editableProperty.getEditorParams();
                if(editorParams != null && editorParams.size() > 0) {
                    editor.applyParams(editorParams);
                }
                result.add(editor);
            }
        }

        sortViewers(result);
        return result;
    }

    /**
     * Gets a list of viewers for a given entity.
     */
    public Iterable<EntityPropertyViewer> getViewersForEntity(Entity entity) {
        generateProperties(entity);

        ArrayList<EntityPropertyViewer> result = new ArrayList<>();
        for (EntityProperty editableProperty : properties) {
            EntityPropertyViewer viewer = this.createViewer(editableProperty);
            if(viewer != null) {
                result.add(viewer);
            }
        }

        sortViewers(result);
        return result;
    }

    private void sortViewers(List<EntityPropertyViewer> viewers) {
        Collections.sort(viewers, new Comparator<EntityPropertyViewer>() {
            @Override
            public int compare(EntityPropertyViewer lhs, EntityPropertyViewer rhs) {
                Integer l = lhs.property().getPosition();
                return l.compareTo(rhs.property().getPosition());
            }
        });
    }

    private void generateProperties(Entity entity) {
        this.properties.clear();

        for (EntityProperty property : entity.properties()) {
            if (property.getSkip()) {
                continue;
            }

            this.properties.add(property);
        }
    }

    private EntityPropertyViewer createViewer(EntityProperty property) {
        EntityPropertyViewer viewer = editorRegistry.createViewerFromMetadata(property);
        if(viewer != null) {
            return viewer;
        }

        if(viewerProvider != null) {
            viewer = viewerProvider.apply(property);
            if(viewer != null) {
                return viewer;
            }
        }

        viewer = editorRegistry.resolveViewerForProperty(property);
        if(viewer != null) {
            return viewer;
        }

        viewer = editorRegistry.resolveViewerForType(property);
        if(viewer != null) {
            return viewer;
        }

        return null;
    }

    private EntityPropertyEditor createEditor(EntityProperty property) {
        EntityPropertyEditor editor = editorRegistry.createEditorFromMetadata(property);
        if(editor != null) {
            return editor;
        }

        if(this.editorProvider != null) {
            editor = this.editorProvider.apply(property);
            if(editor != null) {
                return editor;
            }
        }

        editor = editorRegistry.resolveEditorForProperty(property);
        if(editor != null) {
            return editor;
        }

        editor = editorRegistry.resolveEditorForType(property);
        if(editor != null) {
            return editor;
        }

        return null;
    }
}
