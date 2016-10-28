package com.telerik.widget.palettes;

import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.ObservableCollection;
import com.telerik.widget.chart.visualization.common.PresenterBase;

/**
 * This class represents a collection of colors that can be applied to RadChartView.
 */
public class ChartPalette implements CollectionChangeListener {

    public final static String PIE_FAMILY = "Pie";
    public final static String AREA_FAMILY = "Area";
    public final static String BAR_FAMILY = "Bar";
    public final static String LINE_FAMILY = "Line";
    public final static String POINT_FAMILY = "Point";
    public final static String OHLC_FAMILY = "Ohlc";
    public final static String HORIZONTAL_AXIS_FAMILY = "HorizontalAxis";
    public final static String VERTICAL_AXIS_FAMILY = "VerticalAxis";
    public final static String CARTESIAN_GRID_LINE_ANNOTATION = "CartesianGridLineAnnotation";
    public final static String CARTESIAN_CUSTOM_ANNOTATION = "CartesianCustomAnnotation";
    public final static String CARTESIAN_PLOT_BAND_ANNOTATION = "CartesianPlotBandAnnotation";
    public final static String CARTESIAN_CHART_GRID = "CartesianChartGrid";
    public final static String CARTESIAN_CHART_GRID_STRIPES = "CartesianChartGridStripes";
    public final static String CARTESIAN_STROKED_ANNOTATION = "CartesianStrokedAnnotation";

    private ObservableCollection<PaletteEntryCollection> seriesEntries;
    private ObservableCollection<PaletteEntry> globalEntries;
    boolean isPredefined = false;

    /**
     * Initializes a new instance of the {@link com.telerik.widget.palettes.ChartPalette} class.
     */
    public ChartPalette() {
        this.globalEntries = new ObservableCollection<PaletteEntry>();
        this.globalEntries.addCollectionChangeListener(this);

        this.seriesEntries = new ObservableCollection<PaletteEntryCollection>();
        this.seriesEntries.addCollectionChangeListener(this);
    }

    /**
     * Gets a value indicating whether the specified palette is predefined and may not be modified by the user.
     *
     * @return A value that indicates if the palette is predefined or not.
     */
    public boolean isPredefined() {
        return this.isPredefined;
    }

    /**
     * Gets the collection that stores entries not related to any particular series.
     */
    public ObservableCollection<PaletteEntry> globalEntries() {
        return this.globalEntries;
    }

    /**
     * Gets the collection with all the per-series definitions registered with the palette.
     */
    public ObservableCollection<PaletteEntryCollection> seriesEntries() {
        return this.seriesEntries;
    }

    /**
     * Gets a palette entry for the given series and index.
     *
     * @param series The series from which to get the palette family.
     * @param index  The palette index.
     */
    public PaletteEntry getEntry(PresenterBase series, int index) {
        if (series == null) {
            throw new IllegalArgumentException("series cannot be null");
        }

        return this.getEntry(series.getPaletteFamilyCore(), index);
    }

    /**
     * Gets a palette entry for the given series and index.
     *
     * @param series The series from which to get the palette family.
     */
    public PaletteEntry getEntry(PresenterBase series) {
        return this.getEntry(series, 0);
    }

    /**
     * Gets a palette entry for the given family and index.
     *
     * @param family The palette family.
     */
    public PaletteEntry getEntry(String family) {
        return this.getEntry(family, 0);
    }

    /**
     * Gets a palette entry for the given family and index.
     *
     * @param family The palette family.
     * @param index  The palette index.
     */
    public PaletteEntry getEntry(String family, int index) {
        for (PaletteEntryCollection collection : this.seriesEntries) {
            if (collection.getFamily().equals(family)) {
                if (collection.size() > 0) {
                    return collection.get(index % collection.size());
                }
                break;
            }
        }

        if (this.globalEntries.size() > 0) {
            return this.globalEntries.get(index % this.globalEntries.size());
        }

        return null;
    }

    /**
     * Retrieves a collection of {@link com.telerik.widget.palettes.PaletteEntry} instances
     * which are targeting elements belonging to the provided family.
     *
     * @param family the family for which to retrieve the palette entries.
     * @return an instance of the {@link com.telerik.widget.palettes.PaletteEntryCollection} class
     * representing the collection of resolved entries.
     */
    public PaletteEntryCollection entriesForFamily(String family) {
        for (PaletteEntryCollection collection : this.seriesEntries) {
            if (collection.getFamily().equals(family)) {
                return collection;
            }
        }

        return null;
    }

    @Override
    public void collectionChanged(CollectionChangedEvent info) {
        if (this.isPredefined) {
            throw new UnsupportedOperationException("Cannot modify a predefined ChartPalette.");
        }
    }

    /**
     * Copy constructor.
     */
    public ChartPalette(ChartPalette palette) {

        this();

        for (PaletteEntry entry : palette.globalEntries) {
            this.globalEntries.add(new PaletteEntry(entry));
        }

        for (PaletteEntryCollection collection : palette.seriesEntries) {
            this.seriesEntries.add(new PaletteEntryCollection(collection));
        }
    }

    @Override
    public ChartPalette clone() {
        ChartPalette result = new ChartPalette();
        for (PaletteEntry entry : this.globalEntries) {
            result.globalEntries.add(entry.clone());
        }

        for (PaletteEntryCollection collection : this.seriesEntries) {
            result.seriesEntries.add(collection.clone());
        }

        return result;
    }
}