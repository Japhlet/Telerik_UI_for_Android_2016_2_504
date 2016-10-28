package com.telerik.widget.dataform.visualization.editors;

import android.widget.AutoCompleteTextView;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormAutoCompleteEditor extends DataFormTextEditor {
    protected AutoCompleteTextView autoComplete;

    public DataFormAutoCompleteEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_autocomplete_editor,
                R.id.data_form_autocomplete_editor,
                dataForm.getEditorsValidationLayout(), property);

        autoComplete = (AutoCompleteTextView)this.editorView;
    }

    public AutoCompleteTextView getAutoCompleteView() {
        return autoComplete;
    }
}
