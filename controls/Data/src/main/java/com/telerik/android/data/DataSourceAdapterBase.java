package com.telerik.android.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DataSourceAdapterBase<E> extends BaseAdapter implements DataChangedListener<E>, SelectionChangeListener<DataItem<E>>, CurrentItemChangedListener<E> {
    RadDataSource<E> dataSource = new RadDataSource<E>();
    SelectionService<DataItem<E>> selectionService = new SelectionService<DataItem<E>>();
    CurrencyService<E> currencyService;
    ViewType viewType;
    Context context;

    public DataSourceAdapterBase(List<E> data, Context context) {
        this.context = context;
        this.currencyService = new CurrencyService<E>(data);
        this.dataSource.setSource(data);
        this.dataSource.addDataChangeListener(this);
        this.selectionService.addSelectionChangeListener(this);
    }

    public RadDataSource<E> dataSource() {
        return this.dataSource;
    }

    public SelectionService<DataItem<E>> selectionService() {
        return this.selectionService;
    }

    @Override
    public int getCount() {
        return this.getDataSourceView().size();
    }

    @Override
    public Object getItem(int position) {
        return this.getDataSourceView().get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.getDataSourceView().get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataItem<E> item = this.getDataSourceView().get(position);
        if(selectionService.isItemSelected(item)) {
            return createSelectedView(item);
        }

        if(currencyService.isCurrent(item.entity())) {
            return createCurrentView(item);
        }

        return createNormalView(item);
    }

    public ViewType getViewType() {
        return this.viewType;
    }

    public void setViewType(ViewType value) {
        if(this.viewType == value) {
            return;
        }

        this.viewType = value;
        this.notifyDataSetInvalidated();
    }

    private List<DataItem<E>> getDataSourceView() {
        if(this.viewType == ViewType.FLAT) {
            return this.dataSource.flatView();
        }

        return this.dataSource.view();
    }

    private View createSelectedView(DataItem<E> item) {
        View result = this.createNormalView(item);
        result.setBackgroundColor(Color.YELLOW);
        return result;
    }

    private View createNormalView(DataItem<E> item) {
        TextView textView = new TextView(this.context);
        if(item.getItems().isEmpty()) {
            textView.setText(item.entity().toString());
        } else {
            textView.setText(item.groupKey().toString());
        }

        return textView;
    }

    protected View createCurrentView(DataItem<E> item) {
        return this.createSelectedView(item);
    }

    @Override
    public void dataChanged(DataChangeInfo<E> info) {
        this.notifyDataSetInvalidated();
    }

    @Override
    public void selectionChanged(SelectionChangeInfo<DataItem<E>> info) {
        this.notifyDataSetChanged();
    }

    @Override
    public void currentItemChanged(CurrentItemChangedInfo<E> changeInfo) {
        this.notifyDataSetChanged();
    }

    public enum ViewType {
        FLAT,
        HIERARCHY
    }
}

