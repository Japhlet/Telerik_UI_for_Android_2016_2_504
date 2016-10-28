package com.telerik.widget.palettes;

import android.graphics.Color;

import com.telerik.android.common.Util;

import java.util.HashMap;

/**
 * A combination of colors for a given chart element.
 */
public class PaletteEntry {
    private int fill;
    private int stroke;
    private int additionalFill;
    private int additionalStroke;
    private float strokeWidth = Util.getDP(2.0f);
    private HashMap<String, String> customValuesMap;

    /**
     * Creates an instance of {@link PaletteEntry} with transparent colors.
     */
    public PaletteEntry() {
        this(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
    }

    /**
     * Creates an instance of {@link PaletteEntry} with transparent colors and the specified fill.
     */
    public PaletteEntry(int fill) {
        this(fill, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
    }

    /**
     * Creates an instance of {@link PaletteEntry} with transparent colors and the specified fill and stroke.
     */
    public PaletteEntry(int fill, int stroke) {
        this(fill, stroke, Color.TRANSPARENT, Color.TRANSPARENT);
    }

    /**
     * Creates an instance of {@link PaletteEntry} with transparent colors and the specified fill, stroke and additional fill.
     */
    public PaletteEntry(int fill, int stroke, int additionalFill) {
        this(fill, stroke, additionalFill, Color.TRANSPARENT);
    }

    /**
     * Creates an instance of {@link PaletteEntry} with the specified colors.
     */
    public PaletteEntry(int fill, int stroke, int additionalFill, int additionalStroke) {
        this(fill, stroke, additionalFill, additionalStroke, Util.getDP(2.0f));
    }

    /**
     * Creates an instance of {@link PaletteEntry} with the specified colors.
     */
    public PaletteEntry(int fill, int stroke, int additionalFill, int additionalStroke, float strokeWidth) {
        this.fill = fill;
        this.stroke = stroke;
        this.additionalFill = additionalFill;
        this.additionalStroke = additionalStroke;
        this.customValuesMap = new HashMap<>();
        this.strokeWidth = strokeWidth;
    }

    /**
     * Gets the width of the stroke.
     *
     * @return the stroke width.
     */
    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    /**
     * Sets the width of the stroke.
     *
     * @param value the stroke width.
     */
    public void setStrokeWidth(float value) {
        this.strokeWidth = value;
    }

    /**
     * Gets the fill color.
     */
    public int getFill() {
        return this.fill;
    }

    /**
     * Sets the fill color.
     */
    public void setFill(int value) {
        this.fill = value;
    }

    /**
     * Gets the stroke color.
     */
    public int getStroke() {
        return this.stroke;
    }

    /**
     * Sets the stroke color.
     */
    public void setStroke(int value) {
        this.stroke = value;
    }

    /**
     * Gets the additional fill color.
     */
    public int getAdditionalFill() {
        return this.additionalFill;
    }

    /**
     * Sets the additional fill color.
     */
    public void setAdditionalFill(int value) {
        this.additionalFill = value;
    }

    /**
     * Gets the additional stroke color.
     */
    public int getAdditionalStroke() {
        return this.additionalStroke;
    }

    /**
     * Sets the additional stroke color.
     */
    public void setAdditionalStroke(int value) {
        this.additionalStroke = value;
    }

    /**
     * Gets a value for the provided key in the current {@link PaletteEntry}.
     * This method can be used by Chart elements that do not rely on the standard {@link PaletteEntry}
     * API for retrieving values.
     *
     * @param keyName the name of the key for which to retrieve the provided value.
     * @return the value stored for the provided key. <code>null</code> if there is no value present for the key.
     */
    public String getCustomValue(String keyName) {
        return this.getCustomValue(keyName, null);
    }

    /**
     * Gets a value for the provided key in the current {@link PaletteEntry}.
     * This method can be used by Chart elements that do not rely on the standard {@link PaletteEntry}
     * API for retrieving values.
     *
     * @param keyName      the name of the key for which to retrieve the provided value.
     * @param defaultValue the default value that will be returned if no custom value is present for the provided key.
     * @return the value stored for the provided key. <code>null</code> if there is no value present for the key.
     */
    public String getCustomValue(String keyName, String defaultValue) {
        if (keyName == null) {
            throw new IllegalArgumentException("keyName cannot be null");
        }

        if (this.customValuesMap.containsKey(keyName)) {
            return this.customValuesMap.get(keyName);
        }

        return defaultValue;
    }

    /**
     * Gets a value for the provided key in the current {@link PaletteEntry}.
     * This method can be used by Chart elements that do not rely on the standard {@link PaletteEntry}
     * API for retrieving values.
     *
     * @param keyName      the name of the key for which to retrieve the provided value.
     * @param defaultValue the default value that will be returned if no custom value is present for the provided key.
     * @return the value stored for the provided key. <code>null</code> if there is no value present for the key.
     */
    public String getCustomValue(String keyName, Object defaultValue) {
        return this.getCustomValue(keyName, defaultValue.toString());
    }

    /**
     * Sets a value for the provided key in the current {@link PaletteEntry}.
     * This method can be used by Chart elements that do not rely on the standard {@link PaletteEntry}
     * API for storing values.
     *
     * @param keyName the name of the key for which to store the provided value.
     * @param value   the string value to store.
     */
    public void setCustomValue(String keyName, String value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (keyName == null) {
            throw new IllegalArgumentException("keyName cannot be null");
        }

        this.customValuesMap.put(keyName, value);
    }

    /**
     * Sets a value for the provided key in the current {@link PaletteEntry}.
     * This method can be used by Chart elements that do not rely on the standard {@link PaletteEntry}
     * API for storing values.
     *
     * @param keyName the name of the key for which to store the provided value.
     * @param value   the value to store.
     */
    public void setCustomValue(String keyName, Object value) {
        this.setCustomValue(keyName, value.toString());
    }

    /**
     * Copy constructor.
     */
    public PaletteEntry(PaletteEntry entry) {

        this();

        this.copyFields(entry);
    }

    @Override
    public PaletteEntry clone() {
        PaletteEntry result = new PaletteEntry();
        result.copyFields(this);

        return result;
    }

    private void copyFields(PaletteEntry entry) {
        this.stroke = entry.stroke;
        this.strokeWidth = entry.strokeWidth;
        this.fill = entry.fill;
        this.additionalFill = entry.additionalFill;
        this.additionalStroke = entry.additionalStroke;

        if (!entry.customValuesMap.isEmpty()) {
            for (String key : entry.customValuesMap.keySet()) {
                this.setCustomValue(key, entry.customValuesMap.get(key));
            }
        }
    }
}
