package com.telerik.widget.dataform.visualization.editors;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormToggleButtonEditor extends DataFormBooleanEditor {
    public DataFormToggleButtonEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_toggle_button_editor,
                R.id.data_form_toggle_editor,
                dataForm.getEditorsValidationLayout(), property);
    }
}
