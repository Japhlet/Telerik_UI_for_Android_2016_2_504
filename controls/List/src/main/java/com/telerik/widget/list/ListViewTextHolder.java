package com.telerik.widget.list;

import android.view.View;
import android.widget.TextView;

/**
 * Represents a simple ListViewHolder that contains one {@link android.widget.TextView}.
 */
public class ListViewTextHolder extends ListViewHolder {

    /**
     * Represent the textView contained in this instance.
     */
    public TextView textView;

    /**
     * Creates a new instance of the ListViewTextHolder for the provided itemView
     * which should contain a text view with id <code>R.id.groupHeaderText</code>.
     */
    public ListViewTextHolder(View itemView) {
        this(itemView, R.id.groupHeaderText);
    }

    /**
     * Creates a new instance of the ListViewTextHolder for the provided itemView
     * which should contain a text view with id that is the value of textViewResourceId.
     */
    public ListViewTextHolder(View itemView, int textViewResourceId) {
        super(itemView);

        textView = (TextView)itemView.findViewById(textViewResourceId);
    }
}
