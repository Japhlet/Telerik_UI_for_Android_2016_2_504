package com.telerik.widget.dataform.visualization.editors;

import android.view.View;
import android.widget.CheckBox;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormCheckBoxEditor extends DataFormBooleanEditor {
    public DataFormCheckBoxEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_checkbox_editor,
                R.id.data_form_checkbox_editor,
                dataForm.getEditorsValidationLayout(), property);

        CheckBox checkBox = (CheckBox)this.editorView;
        checkBox.setOnCheckedChangeListener(this);
        if(property.getHeader() != null &&
                dataForm.getEditorsMainLayout() == R.layout.data_form_editor_layout_1) {
            checkBox.setText(property.getHeader());
            getHeaderView().setVisibility(View.GONE);
        }
        if(property.getValue() != null) {
            checkBox.setChecked((Boolean) property.getValue());
        }
    }
}
