package com.telerik.widget.dataform.visualization.editors;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;

public class DataFormSegmentedEditor extends DataFormRadioGroupEditor {
    public DataFormSegmentedEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_segmented_editor,
                R.id.data_form_radio_group,
                dataForm.getEditorsValidationLayout(), property);
    }

    @Override
    protected RadioButton createButton(Context context) {

        RadioButton button = super.createButton(context);

        button.setBackgroundDrawable(this.rootLayout().getResources().getDrawable(R.drawable.data_form_segment_item));
        button.setButtonDrawable(null);
        int fiveDp = (int)Util.getDP(5);
        button.setPadding(0, fiveDp, 0, fiveDp);
        button.setGravity(Gravity.CENTER);

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT, 1);
        params.gravity = Gravity.CENTER;
        button.setLayoutParams(params);

        return button;
    }
}
