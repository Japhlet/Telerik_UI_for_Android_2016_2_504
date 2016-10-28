package com.telerik.android.data;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

public class AndroidDataSourceAdapter<E> extends DataSourceAdapterBase<E> implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    AdapterView<?> adapterView;

    public AndroidDataSourceAdapter(List<E> data, Context context, AdapterView<?> adapterView) {
        super(data, context);
        this.adapterView = adapterView;
        if(this.adapterView != null) {
            this.adapterView.setOnItemClickListener(this);
            this.adapterView.setOnItemSelectedListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        this.selectionService().selectItem((DataItem<E>)this.getItem(i));
        this.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        this.selectionService().selectItem((DataItem<E>)this.getItem(i));
        this.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        this.selectionService().clearSelection();
        this.notifyDataSetChanged();
    }
}
