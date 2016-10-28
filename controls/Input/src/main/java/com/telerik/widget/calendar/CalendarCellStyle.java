package com.telerik.widget.calendar;

import android.graphics.Typeface;
import android.util.TypedValue;

public abstract class CalendarCellStyle {
    private Float borderWidth;
    private Integer borderColor;

    private Integer backgroundColor;

    private Integer textColor;
    private Float textSize;
    private Integer fontStyle;
    private String fontName;

    public Integer getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Integer borderColor) {
        this.borderColor = borderColor;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(Float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
    }

    public Float getTextSize() {
        return textSize;
    }

    public void setTextSize(Float textSize) {
        this.textSize = textSize;
    }

    public Integer getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(Integer fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
