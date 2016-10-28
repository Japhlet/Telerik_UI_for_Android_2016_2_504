package com.telerik.widget.dataform.engine;

import com.telerik.android.common.Procedure2;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class EntityPropertyEditorDependency implements EntityPropertyEditorChangeListener {
    private Procedure2<RadDataForm, EntityPropertyEditor> dependencyChanged;
    private RadDataForm dataForm;
    private String[] dependencies;
    EntityPropertyEditor dependentEditor;
    private String dependentEditorName;

    public EntityPropertyEditorDependency(RadDataForm dataForm, String dependentEditorName, Procedure2<RadDataForm, EntityPropertyEditor> dependencyChanged, String... dependencies) {
        this.dependentEditorName = dependentEditorName;
        this.dependencyChanged = dependencyChanged;
        this.dataForm = dataForm;
        this.dependencies = dependencies;
    }

    public String editorName() {
        return dependentEditorName;
    }

    public void load() {
        dependentEditor = (EntityPropertyEditor)this.dataForm.getExistingEditorForProperty(dependentEditorName);
        for(String dependency : dependencies) {
            EntityPropertyEditor editor = (EntityPropertyEditor)dataForm.getExistingEditorForProperty(dependency);
            if(editor == null) {
                throw new IllegalArgumentException(String.format("The dependency between %s and %s cannot be established because an editor for %s has not been created.", dependentEditorName, dependency, dependency));
            }
            editor.addEditorChangeListener(this);
        }
    }

    public void unload() {
        for(String dependency : dependencies) {
            EntityPropertyEditor editor = (EntityPropertyEditor)dataForm.getExistingEditorForProperty(dependency);
            editor.removeEditorChangeListener(this);
        }
    }

    public void update() {
        dependencyChanged.apply(dataForm, dependentEditor);
    }

    @Override
    public void onEditorChanged(EntityPropertyEditor dependency) {
        update();
    }
}
