package com.telerik.widget.dataform.visualization.viewers;

import android.widget.CompoundButton;

import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyViewer;

public abstract class DataFormBooleanViewer extends EntityPropertyViewer {
    public DataFormBooleanViewer(RadDataForm dataForm, int layoutId, int headerLayoutId, int headerId, int viewerLayoutId, int viewerId, EntityProperty property) {
        super(dataForm, layoutId, headerLayoutId, headerId, viewerLayoutId, viewerId, property);

        ((CompoundButton)this.editorView).setChecked((Boolean) property.getValue());
    }
}
