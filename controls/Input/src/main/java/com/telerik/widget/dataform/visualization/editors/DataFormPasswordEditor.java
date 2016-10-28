package com.telerik.widget.dataform.visualization.editors;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormPasswordEditor extends DataFormTextEditor {
    public DataFormPasswordEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, property);

        EditText coreEditor = (EditText)this.getEditorView();
        coreEditor.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        coreEditor.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
}
