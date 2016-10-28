package com.telerik.widget.dataform.visualization.editors;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.R;
import com.telerik.widget.dataform.engine.EntityProperty;
import com.telerik.widget.dataform.visualization.RadDataForm;
import com.telerik.widget.dataform.visualization.core.EntityPropertyEditor;

public class DataFormListViewEditor extends EntityPropertyEditor implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
    protected ListView listView;
    protected Object currentValue;

    public DataFormListViewEditor(RadDataForm dataForm, EntityProperty property) {
        super(dataForm,
                dataForm.getEditorsMainLayout(),
                dataForm.getEditorsHeaderLayout(),
                R.id.data_form_text_viewer_header,
                R.layout.data_form_list_editor,
                R.id.data_form_list_editor,
                dataForm.getEditorsValidationLayout(), property);

        listView = (ListView)editorView;
        listView.setOnItemSelectedListener(this);
        listView.setOnItemClickListener(this);

        Object[] values;
        if(property.type().isEnum()) {
            values = property.type().getEnumConstants();
        } else if(property.getEnumConstants() != null) {
            values = property.getEnumConstants();
        } else {
            values = new Object[0];
        }

        listView.setAdapter(new ArrayAdapter<>(dataForm.getContext(), R.layout.data_form_list_editor_item, values));
        forceListViewToStretch(listView);
        currentValue = property.getValue();
    }

    public void forceListViewToStretch(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if(params.height < 0) {
            params.height = 0;
        }
        listView.setLayoutParams(params);
    }

    public int getItemPosition(Object item) {
        ListAdapter adapter = getAdapter();
        if(adapter == null) {
            return -1;
        }

        for(int i = 0; i < adapter.getCount(); ++i) {
            if(Util.Equals(item, adapter.getItem(i))) {
                return i;
            }
        }

        return -1;
    }

    public void setAdapter(ListAdapter adapter) {
        listView.setAdapter(adapter);
        forceListViewToStretch(listView);
    }

    public ListAdapter getAdapter() {
        return listView.getAdapter();
    }

    @Override
    public Object value() {
        return currentValue;
    }

    @Override
    protected void applyEntityValueToEditor(Object entityValue) {
        currentValue = entityValue;
        int position = ((ArrayAdapter)listView.getAdapter()).getPosition(entityValue);
        if(position < 0) {
            return;
        }
        listView.performItemClick(
                listView.getAdapter().getView(position, null, null),
                position,
                listView.getAdapter().getItemId(position));
    }

    protected void persistItem(int position) {
        if(getAdapter() == null) {
            return;
        }

        if(position >= 0 && position < getAdapter().getCount()) {
            currentValue = getAdapter().getItem(position);
            onEditorValueChanged(currentValue);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        persistItem(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        onEditorValueChanged(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        persistItem(position);
    }
}
