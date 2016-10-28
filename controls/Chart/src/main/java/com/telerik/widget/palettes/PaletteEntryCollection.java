package com.telerik.widget.palettes;

import com.telerik.android.common.CollectionChangeAction;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.ObservableCollection;

/**
 * An {@link com.telerik.android.common.ObservableCollection} that contains {@link com.telerik.widget.palettes.PaletteEntry} objects.
 */
public class PaletteEntryCollection extends ObservableCollection<PaletteEntry> {

    private String seriesFamily;

    /**
     * Creates an instance of the {@link PaletteEntryCollection} class
     * with a specified {@link ChartPalette} owner.
     */
    public PaletteEntryCollection() {
    }


    /**
     * Gets the family of {@link com.telerik.widget.chart.visualization.common.ChartSeries} targeted by this collection.
     */
    public String getFamily() {
        return this.seriesFamily;
    }

    /**
     * Sets the family of {@link com.telerik.widget.chart.visualization.common.ChartSeries} targeted by this collection.
     */
    public void setFamily(String value) {
        if (this.seriesFamily != null && this.seriesFamily.equals(value)) {
            return;
        }

        this.seriesFamily = value;
        this.notifyListeners(new CollectionChangedEvent<PaletteEntry>(this, CollectionChangeAction.RESET));
    }

    /**
     * Copy constructor.
     */
    public PaletteEntryCollection(PaletteEntryCollection collection) {

        for (PaletteEntry entry : collection) {
            this.add(new PaletteEntry(entry));
        }

        this.setFamily(collection.getFamily());
    }

    @Override
    public PaletteEntryCollection clone() {
        PaletteEntryCollection result = new PaletteEntryCollection();

        for (PaletteEntry entry : this) {
            result.add(entry.clone());
        }

        result.setFamily(this.getFamily());

        return result;
    }
}
