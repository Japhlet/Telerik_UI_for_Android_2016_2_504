package com.telerik.widget.dataform.visualization.viewers;

import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

public class DataFormTextViewer extends EntityPropertyViewer {
    public DataFormTextViewer(RadDataForm dataForm, EntityProperty property) {
        super(dataForm, dataForm.getEditorsMainLayout(), dataForm.getEditorsHeaderLayout(), R.id.data_form_text_viewer_header, R.layout.data_form_text_viewer, R.id.data_form_text_viewer, property);

        if(property.getHeader() != null) {
            ((TextView) headerView).setText(property.getHeader());
        }
        ((TextView)editorView).setText(Util.toString(property().getValue()));
    }
}
