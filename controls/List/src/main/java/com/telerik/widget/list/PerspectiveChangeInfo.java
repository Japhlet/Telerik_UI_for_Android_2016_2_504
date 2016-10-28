package com.telerik.widget.list;

/**
 * Provides information about the changes applied to perspective items in DeckOfCardsLayoutManager.
 */
public class PerspectiveChangeInfo {

    public static final long DEFAULT_DURATION = 300l;

    public static final float DEFAULT_ALPHA = 1.0f;
    public static final float DEFAULT_TRANSLATION = -1.0f;

    public static final int DEFAULT_ELEVATION = -1;

    private DeckOfCardsLayoutManager owner;

    private long animationDuration = DEFAULT_DURATION;

    private float alpha = DEFAULT_ALPHA;

    private float translateStart = DEFAULT_TRANSLATION;
    private float translateTop = DEFAULT_TRANSLATION;
    private float translateEnd = DEFAULT_TRANSLATION;
    private float translateBottom = DEFAULT_TRANSLATION;

    private int elevation = DEFAULT_ELEVATION;

    public PerspectiveChangeInfo(DeckOfCardsLayoutManager owner) {
        this.owner = owner;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        this.animationDuration = animationDuration;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        this.owner.requestLayout();
    }

    public float getTranslateStart() {
        return translateStart;
    }

    public void setTranslateStart(float translateStart) {
        this.translateStart = translateStart;
        this.owner.calculateFrontViewSize();
    }

    public float getTranslateTop() {
        return translateTop;
    }

    public void setTranslateTop(float translateTop) {
        this.translateTop = translateTop;
        this.owner.calculateFrontViewSize();
    }

    public float getTranslateEnd() {
        return translateEnd;
    }

    public void setTranslateEnd(float translateEnd) {
        this.translateEnd = translateEnd;
        this.owner.calculateFrontViewSize();
    }

    public float getTranslateBottom() {
        return translateBottom;
    }

    public void setTranslateBottom(float translateBottom) {
        this.translateBottom = translateBottom;
        this.owner.calculateFrontViewSize();
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
        this.owner.requestLayout();
    }
}
