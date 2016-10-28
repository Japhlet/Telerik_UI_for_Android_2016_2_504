package com.telerik.widget.dataform.visualization.editors;

import android.widget.EditText;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormMultilineTextEditor extends DataFormTextEditor {
    public DataFormMultilineTextEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, property);

        EditText coreEditor = (EditText)this.getEditorView();
        coreEditor.setSingleLine(false);
    }
}
