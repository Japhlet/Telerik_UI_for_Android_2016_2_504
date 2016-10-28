package com.telerik.widget.dataform.visualization.editors;

import android.text.InputType;
import android.widget.EditText;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormEmailEditor extends DataFormTextEditor {
    public DataFormEmailEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, property);

        EditText coreEditor = (EditText)this.getEditorView();
        coreEditor.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }
}
