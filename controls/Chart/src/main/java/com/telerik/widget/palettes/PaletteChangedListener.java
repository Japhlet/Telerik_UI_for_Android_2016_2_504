package com.telerik.widget.palettes;

/**
 * Interface for classes able to listen for palette change.
 * A palette calls its palette changed listeners when a palette entry has been added, removed or replaced.
 */
public interface PaletteChangedListener {

    /**
     * A palette will call this method when it has changed,
     * so that the listener can update itself.
     */
    void onPaletteUpdated(ChartPalette source);
}
