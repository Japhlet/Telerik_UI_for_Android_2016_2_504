package com.telerik.widget.palettes;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;

import com.telerik.android.common.Util;

import org.xmlpull.v1.XmlPullParser;

/**
 * A class that contains pre-defined palettes for RadChartView.
 */
public final class ChartPalettes {

    private static ChartPalette lightPalette;
    private static final Object lockInstance = new Object();
    private static ChartPalette darkPalette;
    private static ChartPalette darkSelectedPalette;
    private static ChartPalette lightSelectedPalette;
    private static boolean appResourcesEnsured;
    private static Context context = null;

    private ChartPalettes() {
    }

    @Deprecated
    public static Context context(){
        return context;
    }

    /**
     * @deprecated instead of initializing, directly get the palettes
     * with context - {@link #light(Context)} or {@link #dark(Context)}.
     */
    @Deprecated
    public static void init(Context c) {
        if (context != null) {
            return;
        }
        context = c;
    }

    /**
     * Creates a new {@link ChartPalette} instance by using an existing
     * {@link com.telerik.widget.palettes.PaletteEntryCollection} with predefined entries.
     *
     * @param entries the {@link PaletteEntryCollection} instance containing
     *                the entries for the new palette.
     * @return the new {@link ChartPalette} instance containing the provided
     * entries.
     */
    public static ChartPalette generatePalette(PaletteEntryCollection entries) {
        ChartPalette palette = new ChartPalette();
        palette.seriesEntries().add(entries);
        return palette;
    }

    /**
     * @deprecated use {@link #light(Context)} instead.
     */
    @Deprecated
    public static ChartPalette light() {
        if (lightPalette == null) {
            synchronized (lockInstance) {
                if (lightPalette == null) {
                    lightPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_default_light);
                    lightPalette.isPredefined = true;
                }
            }
        }

        return lightPalette;
    }

    /**
     * Gets a {@link ChartPalette} with distinctive entries per series Family. Colors are light and soft.
     */
    public static ChartPalette light(Context context) {
        if(lightPalette == null) {
            synchronized (lockInstance) {
                if (lightPalette == null) {
                    lightPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_default_light);
                    lightPalette.isPredefined = true;
                }
            }
        }

        return lightPalette;
    }

    /**
     * @deprecated use {@link #dark(Context)} instead.
     */
    @Deprecated
    public static ChartPalette dark() {
        if (darkPalette == null) {
            synchronized (lockInstance) {
                if (darkPalette == null) {
                    darkPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_default_dark);
                    darkPalette.isPredefined = true;
                }
            }
        }

        return darkPalette;
    }

    /**
     * Gets a {@link ChartPalette} with distinctive entries per series Family. Colors are contrasting with each other.
     */
    public static ChartPalette dark(Context context) {
        if(darkPalette == null) {
            synchronized (lockInstance) {
                if (darkPalette == null) {
                    darkPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_default_dark);
                    darkPalette.isPredefined = true;
                }
            }
        }

        return darkPalette;
    }

    private static ChartPalette loadPalette(Context c, int paletteId) throws Error {
        if (!appResourcesEnsured) {
            appResourcesEnsured = true;
        }

        if (c == null) {
            throw new IllegalStateException("ChartPalettes is not initialized. Call init() first with a valid Context instance.");
        }

        XmlResourceParser parser = c.getResources().getXml(paletteId);
        ChartPalette result = new ChartPalette();
        try {
            parser.next();
            int eventType = parser.getEventType();
            PaletteEntry currentEntry;
            PaletteEntryCollection seriesEntries = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if (tagName.compareTo("PaletteEntryCollection") == 0) {
                        seriesEntries = new PaletteEntryCollection();
                        int attributeCount = parser.getAttributeCount();
                        for (int i = 0; i < attributeCount; i++) {
                            String attributeName = parser.getAttributeName(i);
                            if (attributeName.compareTo("Family") == 0) {
                                seriesEntries.setFamily(parser.getAttributeValue(i));
                            }
                        }
                    } else if (tagName.compareTo("PaletteEntry") == 0) {

                        int count = parser.getAttributeCount();
                        currentEntry = new PaletteEntry();

                        seriesEntries.add(currentEntry);

                        for (int i = 0; i < count; i++) {

                            String attributeName = parser.getAttributeName(i);
                            String attributeValue = parser.getAttributeValue(i);

                            if (attributeName.compareTo("Fill") == 0) {
                                currentEntry.setFill(Color.parseColor(attributeValue));
                            } else if (attributeName.compareTo("Stroke") == 0) {
                                currentEntry.setStroke(Color.parseColor(attributeValue));
                            } else if (attributeName.compareTo("AdditionalFill") == 0) {
                                currentEntry.setAdditionalFill(Color.parseColor(attributeValue));
                            } else if (attributeName.compareTo("AdditionalStroke") == 0) {
                                currentEntry.setAdditionalStroke(Color.parseColor(attributeValue));
                            } else if (attributeName.compareTo("StrokeWidth") == 0) {
                                currentEntry.setStrokeWidth(Util.getDP(Float.parseFloat(attributeValue)));
                            } else {
                                currentEntry.setCustomValue(attributeName, attributeValue);
                            }
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = parser.getName();
                    if (tagName.compareTo("PaletteEntryCollection") == 0) {
                        if (seriesEntries != null) {
                            result.seriesEntries().add(seriesEntries);
                        }
                    }
                }
                eventType = parser.next();
            }
            parser.close();

        } catch (Exception ex) {
            throw new Error("Exception occurred while loading the chart palette: " + ex.getMessage());
        }

        return result;
    }

    /**
     * @deprecated use {@link #darkSelected(Context)} instead.
     */
    @Deprecated
    public static ChartPalette darkSelected() {
        if (darkSelectedPalette == null) {
            synchronized (lockInstance) {
                if (darkSelectedPalette == null) {
                    darkSelectedPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_dark_selected);
                    darkSelectedPalette.isPredefined = true;
                }
            }
        }

        return darkSelectedPalette;
    }

    /**
     * Gets a {@link ChartPalette} which is the "Selected" version of the dark palette.
     */
    public static ChartPalette darkSelected(Context context) {
        if (darkSelectedPalette == null) {
            synchronized (lockInstance) {
                if (darkSelectedPalette == null) {
                    darkSelectedPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_dark_selected);
                    darkSelectedPalette.isPredefined = true;
                }
            }
        }

        return darkSelectedPalette;
    }

    /**
     * @deprecated use {@link #lightSelected(Context)} instead.
     */
    @Deprecated
    public static ChartPalette lightSelected() {
        if (lightSelectedPalette == null) {
            synchronized (lockInstance) {
                if (lightSelectedPalette == null) {
                    lightSelectedPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_light_selected);
                    lightSelectedPalette.isPredefined = true;
                }
            }
        }

        return lightSelectedPalette;
    }

    /**
     * Gets a {@link ChartPalette} which is the "Selected" version of the light palette.
     */
    public static ChartPalette lightSelected(Context context) {
        if (lightSelectedPalette == null) {
            synchronized (lockInstance) {
                if (lightSelectedPalette == null) {
                    lightSelectedPalette = loadPalette(context, com.telerik.widget.chart.R.xml.chart_palettes_light_selected);
                    lightSelectedPalette.isPredefined = true;
                }
            }
        }

        return lightSelectedPalette;
    }
}
