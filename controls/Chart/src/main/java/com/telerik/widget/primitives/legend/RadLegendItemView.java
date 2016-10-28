package com.telerik.widget.primitives.legend;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telerik.android.common.Util;
import com.telerik.widget.chart.R;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class is the view that visualizes a {@link LegendItem} object.
 */
public class RadLegendItemView extends LinearLayout implements PropertyChangeListener {

    LegendItem legendItem;
    TextView titleView;
    View iconView;

    /**
     * Initializes a new instance of the {@link RadLegendItemView}
     *
     * @param context context for the legend item view.
     */
    public RadLegendItemView(Context context) {
        this(context, R.layout.legend_item_view);
    }

    /**
     * Initializes a new instance of the {@link RadLegendItemView}
     *
     * @param context context for the legend item view.
     * @param layout  index of the layout to be inflated.
     */
    public RadLegendItemView(Context context, int layout) {
        super(context);
        this.setOrientation(LinearLayout.HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout, this);
        this.titleView = Util.getLayoutPart(this, R.id.legendItemTitleView, TextView.class);
        this.iconView = Util.getLayoutPart(this, R.id.legendItemIconView, View.class);
    }

    /**
     * Sets the {@link LegendItem} object to be visualized.
     *
     * @param value The legend item.
     */
    public void setLegendItem(LegendItem value) {
        if (value == this.legendItem) {
            return;
        }

        if (this.legendItem != null) {
            this.legendItem.setPropertyChangeListener(null);
        }

        this.legendItem = value;

        if (this.legendItem != null) {
            this.legendItem.setPropertyChangeListener(this);
            this.titleView.setText(value.getTitle());

            // Using deprecated method to keep API 9 supported.
            iconView.setBackgroundDrawable(generateLegendIcon((int) getResources().getDimension(R.dimen.legend_stroke_width),
                    value.getStrokeColor(), value.getFillColor()));
        } else {
            this.titleView.setText("");
            this.iconView.setBackgroundColor(0);
        }
    }

    /**
     * Gets the current legend item for this instance.
     *
     * @return the current legend item.
     */
    public LegendItem getLegendItem() {
        return this.legendItem;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("title")) {
            this.titleView.setText(this.legendItem.getTitle());
        } else if (event.getPropertyName().equals("fillColor")) {
            this.iconView.setBackgroundColor(this.legendItem.getFillColor());
        }
    }

    private Drawable generateLegendIcon(final int strokeWidth, final int strokeColor, final int fillColor) {
        final GradientDrawable icon = new GradientDrawable();
        icon.setStroke(strokeWidth, strokeColor);
        icon.setColor(fillColor);

        return icon;
    }
}
