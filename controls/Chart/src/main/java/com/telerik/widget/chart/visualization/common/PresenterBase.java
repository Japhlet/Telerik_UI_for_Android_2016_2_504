package com.telerik.widget.chart.visualization.common;

import android.graphics.Canvas;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.telerik.android.common.PropertyManager;
import com.telerik.android.common.math.RadSize;
import com.telerik.widget.chart.engine.elementTree.ChartNode;
import com.telerik.widget.chart.engine.view.ChartElementPresenter;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteChangedListener;

import java.util.HashMap;

/**
 * Represents a {@link FrameLayout} base for displaying widget elements.
 *
 * @see ChartElementPresenter
 */
public abstract class PresenterBase extends PropertyManager implements ChartElementPresenter, PaletteChangedListener {
    protected boolean isVisible = true;

    /**
     * Instance of the {@link ChartLayoutContext} that holds information about the chart layout.
     *
     * @see ChartLayoutContext
     */
    protected ChartLayoutContext lastLayoutContext = new ChartLayoutContext();

    protected boolean isPaletteApplied = false;
    private boolean isLoaded = false;
    private boolean canApplyPalette = true;
    private String paletteFamily;

    /**
     * Creates an instance of the {@link PresenterBase} class using context and no attributes.
     */
    protected PresenterBase() {
    }

    public boolean isPaletteApplied() {
        return this.isPaletteApplied;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    public void setVisible(boolean visible, boolean requestRender) {
        if (this.isVisible == visible)
            return;

        this.isVisible = visible;

        if(requestRender) {
            requestRender();
        }
    }

    public abstract void requestRender();

    /**
     * Used to render the current presenter instance onto the passed canvas object.
     *
     * @param canvas the canvas onto which the presenter is to be rendered.
     */
    public void render(Canvas canvas) {
    }

    /**
     * Used as second layer for rendering the current presenter instance onto the passed canvas object.
     *
     * @param canvas the canvas onto which the presenter is to be rendered.
     */
    public void postRender(Canvas canvas) {
    }

    /**
     * Returns <code>true</code> if the current presenter was loaded and <code>false</code> otherwise.
     *
     * @return the loaded state of the presenter.
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public void refreshNode(ChartNode node) {
        this.refreshNodeCore(node);
    }

    @Override
    public RadSize measureContent(ChartNode owner, Object content) {
        RadSize result;

        if (content == null) {
            return RadSize.getEmpty();
        } else {
            result = this.measureNodeOverride(owner, content);
        }

        return result;
    }

    /**
     * Returns the palette family of this {@link PresenterBase}.
     * The family is used to resolve the right palette resources for this presenter when a {@link ChartPalette} is applied
     * to this presenter.
     *
     * @return a string representing the palette family.
     */
    @Deprecated()
    public String paletteFamily() {
        return getPaletteFamilyCore();
    }

    /**
     * Gets the default palette family if paletteFamily is null.
     */
    protected abstract String defaultPaletteFamily();

    /**
     * Returns the palette family of this {@link PresenterBase}.
     * The family is used to resolve the right palette resources for this presenter when a {@link ChartPalette} is applied
     * to this presenter.
     *
     * @return Returns the value of getPaletteFamily() if it is not null and the value of defaultPaletteFamily() otherwise.
     */
    public String getPaletteFamilyCore() {
        if (this.paletteFamily != null) {
            return this.paletteFamily;
        }

        return this.defaultPaletteFamily();
    }

    /**
     * Returns the palette family of this {@link PresenterBase}.
     * The family is used to resolve the right palette resources for this presenter when a {@link ChartPalette} is applied
     * to this presenter.
     *
     * @return a string representing the palette family.
     */
    public String getPaletteFamily() {
        return this.paletteFamily;
    }

    public void setPaletteFamily(String value) {
        if (TextUtils.equals(this.paletteFamily, value)) {
            return;
        }

        this.paletteFamily = value;
        this.invalidatePalette();
    }

    /**
     * Marks the presenter as loaded.
     */
    protected void onLoaded() {
        this.isLoaded = true;
    }

    /**
     * Marks the presenter as not loaded.
     */
    protected void onUnloaded() {
        this.isLoaded = false;
    }

    /**
     * Core entry point for calculating the size of a node's content.
     *
     * @param node    the node to be measured.
     * @param content the content of the chart node
     * @return the measurement result.
     * @see RadSize
     * @see ChartNode
     */
    protected RadSize measureNodeOverride(ChartNode node, Object content) {
        return RadSize.getEmpty();
    }

    /**
     * Performs the core logic that invalidates the visual representation of the specified logical node.
     *
     * @param node the node which core to be refreshed.
     */
    protected void refreshNodeCore(ChartNode node) {
    }

    /**
     * Triggers an update of the UI after zooming and panning.
     *
     * @param context context holding the flags about the trigger cause.
     */
    protected void updateUI(ChartLayoutContext context) {
        this.updateUICore(context);
        this.lastLayoutContext = context;
        this.onUIUpdated();
    }

    /**
     * Gets a value that determines if the chart palette should be applied to this presenter.
     * Since palette settings override user settings when applied, users can disable the palette application
     * so that their settings remain intact.
     */
    public boolean getCanApplyPalette() {
        return this.canApplyPalette;
    }

    /**
     * Sets a value that determines if the chart palette should be applied to this presenter.
     * Since palette settings override user settings when applied, users can disable the palette application
     * so that their settings remain intact.
     */
    public void setCanApplyPalette(boolean value) {
        if (value == this.canApplyPalette) {
            return;
        }

        this.canApplyPalette = value;
        this.invalidatePalette();
    }

    /**
     * Triggered after updating the UI.
     */
    protected void onUIUpdated() {

    }

    protected void applyPaletteCore(ChartPalette palette) {
    }

    /**
     * Updates all of the chart elements presented by this instance.
     *
     * @param context instance holding the chart elements.
     */
    protected void updateUICore(ChartLayoutContext context) {
        this.requestRender();
    }
}
