package com.telerik.widget.primitives.legend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * An {@link ArrayAdapter} used in {@link RadLegendView}.
 */
public class LegendItemListAdapter extends ArrayAdapter<LegendItem> {

    private Context context;
    private RadLegendView legendControl;

    /**
     * Creates an instance of {@link LegendItemListAdapter}.
     *
     * @param context       The app context.
     * @param items         The legend items to visualize.
     * @param legendControl The {@link RadLegendView} associated with this adapter.
     */
    public LegendItemListAdapter(Context context, List<LegendItem> items, RadLegendView legendControl) {
        super(context, 0, items);

        if (legendControl == null) {
            throw new InvalidParameterException("legendControl cannot be null");
        }

        this.context = context;
        this.legendControl = legendControl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RadLegendItemView view;
        if (convertView != null) {
            view = (RadLegendItemView) convertView;
        } else {
            view = new RadLegendItemView(this.context, legendControl.getItemViewLayout());
        }

        LegendItem item = this.getItem(position);
        view.setLegendItem(item);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEnabled(int position) {
        // Disables selection.
        return false;
    }
}
