package com.telerik.widget.dataform.visualization.viewers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormCheckBoxViewer extends DataFormBooleanViewer {

    public DataFormCheckBoxViewer(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, dataForm.getEditorsMainLayout(), dataForm.getEditorsHeaderLayout(), R.id.data_form_text_viewer_header, R.layout.data_form_checkbox_editor, R.id.data_form_checkbox_editor, property);

        CheckBox viewer = (CheckBox)editorView;
        viewer.setEnabled(false);
    }
}
