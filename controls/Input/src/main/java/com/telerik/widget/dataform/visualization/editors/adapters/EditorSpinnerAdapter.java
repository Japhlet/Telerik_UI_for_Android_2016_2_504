package com.telerik.widget.dataform.visualization.editors.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.telerik.widget.calendar.R;
import com.telerik.widget.calendar.RadCalendarView;

import java.util.ArrayList;

public class EditorSpinnerAdapter extends ArrayAdapter {
    private Object[] source;
    private int itemLayout;

    public EditorSpinnerAdapter(Context context, int resource, Object[] items) {
        super(context, resource);
        this.source = items;
        this.itemLayout = resource;
   }

    public EditorSpinnerAdapter(Context context, Object[] items) {
        this(context, R.layout.data_form_spinner_item, items);
    }

    public void setItems(Object[] items) {
        this.source = items;
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if(this.source == null) {
            return null;
        }

        return this.source[position];
    }

    @Override
    public int getPosition(Object item) {
        if(this.source == null) {
            return -1;
        }

        for(int i = 0; i < source.length; ++i) {
            Object obj = source[i];

            if(obj.equals(item)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getCount() {
        if(this.source == null) {
            return 0;
        }

        return this.source.length;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(this.source == null) {
            return null;
        }

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        ((TextView) convertView).setText(this.getItem(position).toString());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(this.source == null) {
            return null;
        }

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(this.itemLayout, parent, false);
        }

        ((TextView) convertView).setText(this.getItem(position).toString());

        return convertView;
    }
}
