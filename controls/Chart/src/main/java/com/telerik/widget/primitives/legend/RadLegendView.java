package com.telerik.widget.primitives.legend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.Util;
import com.telerik.android.common.licensing.LicensingProvider;
import com.telerik.widget.chart.R;

/**
 * A view that displays a legend for a {@link LegendInfoProvider}.
 */
public class RadLegendView extends FrameLayout implements CollectionChangeListener {

    ListView listView;
    Context context;
    LegendInfoProvider legendProvider;
    LegendItemListAdapter adapter;
    int itemViewLayout = R.layout.legend_item_view;

    /**
     * Creates an instance of {@link RadLegendView}.
     *
     * @param context The app context.
     */
    public RadLegendView(Context context) {
        this(context, null, 0, R.layout.radlegendcontrol);
    }

    /**
     * Creates an instance of {@link RadLegendView}.
     *
     * @param context The app context.
     * @param attrs   The attribute set if the view was created in XML.
     */
    public RadLegendView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, R.layout.radlegendcontrol);
    }

    /**
     * Creates an instance of {@link RadLegendView}.
     *
     * @param context  The app context.
     * @param attrs    The attribute set if the view was created in XML.
     * @param defStyle The default style resource identifier.
     * @param layout   The layout resource identifier with which to create the {@link RadLegendView}.
     */
    public RadLegendView(Context context, AttributeSet attrs, int defStyle, int layout) {
        super(context, attrs, defStyle);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout, this);

        this.listView = Util.getLayoutPart(this, R.id.legendListView, ListView.class);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LicensingProvider.verify(this.getContext());
    }


    /**
     * Gets the layout that contains the {@link RadLegendItemView} objects.
     */
    public int getItemViewLayout() {
        return this.itemViewLayout;
    }

    /**
     * Sets the layout that contains the {@link RadLegendItemView} objects.
     */
    public void setItemViewLayout(int value) {
        if (this.itemViewLayout == value) {
            return;
        }

        this.itemViewLayout = value;
    }

    /**
     * Gets the {@link LegendInfoProvider}.
     */
    public LegendInfoProvider getLegendProvider() {
        return this.legendProvider;
    }

    /**
     * Sets the {@link LegendInfoProvider}.
     */
    public void setLegendProvider(LegendInfoProvider value) {
        if (this.legendProvider == value) {
            return;
        }

        if (this.legendProvider != null) {
            this.legendProvider.getLegendInfos().removeCollectionChangeListener(this);
        }

        this.legendProvider = value;

        if (this.legendProvider != null) {
            this.legendProvider.getLegendInfos().addCollectionChangeListener(this);
            this.adapter = new LegendItemListAdapter(this.context, this.legendProvider.getLegendInfos(), this);
            this.listView.setAdapter(this.adapter);
        }
    }

    @Override
    public void collectionChanged(CollectionChangedEvent info) {
        this.adapter.notifyDataSetChanged();
    }
}
