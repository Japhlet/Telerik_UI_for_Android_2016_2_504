package com.telerik.widget.dataform.visualization.editors;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public abstract class DataFormBooleanEditor extends EntityPropertyEditor implements CheckBox.OnCheckedChangeListener {
    CompoundButton coreEditor;

    public DataFormBooleanEditor(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerViewId, int editorLayoutId, int editorViewId, int validationLayoutId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerViewId, editorLayoutId, editorViewId, validationLayoutId, property);

        this.coreEditor = (CompoundButton)this.editorView;

        this.applyEntityValueToEditor(property.getValue());
        this.coreEditor.setOnCheckedChangeListener(this);
    }

    @Override
    public Object value() {
        return coreEditor.isChecked();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.onEditorValueChanged(isChecked);
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        Boolean isChecked = (Boolean)entityValue;
        if(isChecked == null) {
            isChecked = false;
        }
        this.coreEditor.setChecked(isChecked);
    }
}
