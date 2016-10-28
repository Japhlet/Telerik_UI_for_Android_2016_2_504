package com.telerik.widget.dataform.visualization.editors;

import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormSwitchEditor extends DataFormBooleanEditor {

    public DataFormSwitchEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_switch_editor,
                R.id.data_form_switch_editor,
                dataForm.getEditorsValidationLayout(), property);

        SwitchCompat switchCompat = (SwitchCompat)this.editorView;
        switchCompat.setOnCheckedChangeListener(this);
        if(property.getHeader() != null &&
                dataForm.getEditorsMainLayout() == R.layout.data_form_editor_layout_1) {
            switchCompat.setText(property.getHeader());
            getHeaderView().setVisibility(View.GONE);
        }

        if(property.getValue() != null) {
            switchCompat.setChecked((Boolean) property.getValue());
        }
    }
}
