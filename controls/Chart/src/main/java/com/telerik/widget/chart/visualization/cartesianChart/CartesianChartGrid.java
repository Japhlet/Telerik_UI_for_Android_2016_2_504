package com.telerik.widget.chart.visualization.cartesianChart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.telerik.android.common.CollectionChangeListener;
import com.telerik.android.common.CollectionChangedEvent;
import com.telerik.android.common.ObservableCollection;
import com.telerik.android.common.DependencyPropertyChangedListener;
import com.telerik.android.common.Util;
import com.telerik.android.common.math.RadPoint;
import com.telerik.android.common.math.RadRect;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.TickPosition;
import com.telerik.widget.chart.engine.chartAreas.ChartAreaModelWithAxes;
import com.telerik.widget.chart.engine.decorations.CartesianChartGridModel;
import com.telerik.widget.chart.engine.decorations.GridLine;
import com.telerik.widget.chart.engine.decorations.GridStripe;
import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.visualization.common.Axis;
import com.telerik.widget.chart.visualization.common.ChartElementPresenter;
import com.telerik.widget.chart.visualization.common.RadChartViewBase;
import com.telerik.widget.palettes.ChartPalette;
import com.telerik.widget.palettes.PaletteEntry;
import com.telerik.widget.palettes.PaletteEntryCollection;

import java.util.ArrayList;

/**
 * Represents a decoration over the plot area of {@link RadCartesianChartView}. Adds major and minor lines, connected to each Major and Minor tick of each axis.
 */
public class CartesianChartGrid extends ChartElementPresenter {

    public static final int MAJOR_LINES_THICKNESS_PROPERTY_KEY = registerProperty(2.0f, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;
            float lineThickness = (Float)propertyValue;
            if (lineThickness <= 0)
                throw new IllegalArgumentException("lineThickness cannot be negative or zero");

            grid.linePaint.setStrokeWidth(lineThickness);
        }
    });

    public static final int MAJOR_LINES_VISIBILITY_PROPERTY_KEY = registerProperty(GridLineVisibility.NONE, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;
            int value = (int)propertyValue;
            if (grid.majorLinesVisibilityCache == value) {
                return;
            }

            grid.majorLinesVisibilityCache = value;
            grid.onMajorLinesVisibilityChanged();
        }
    });

    public static final int STRIP_LINES_VISIBILITY_PROPERTY_KEY = registerProperty(GridLineVisibility.NONE, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            int value = (int)propertyValue;
            if (grid.stripLinesVisibility == value) {
                return;
            }

            grid.stripLinesVisibility = value;
            grid.requestRender();
        }
    });

    public static final int LINE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            grid.linePaint.setColor((int) propertyValue);
        }
    });

    public static final int VERTICAL_LINE_COLOR_PROPERTY_KEY = registerProperty(Color.BLACK, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            grid.verticalLinePaint.setColor((int)propertyValue);
        }
    });

    public static final int MAJOR_X_LINE_DASH_ARRAY_PROPERTY_KEY = registerProperty(null, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            float[] value = (float[])propertyValue;
            if (grid.majorXLines.dashArray == value) {
                return;
            }

            grid.majorXLines.dashArray = value;
            grid.onMajorXLineDashArrayChanged(value);
        }
    });

    public static final int MAJOR_Y_LINE_DASH_ARRAY_PROPERTY_KEY = registerProperty(null, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;
            float[] value = (float[])propertyValue;
            if (grid.majorYLines.dashArray == value) {
                return;
            }

            grid.majorYLines.dashArray = value;
            grid.onMajorYLineDashArrayChanged(value);
        }
    });

    public static final int MAJOR_X_LINE_RENDER_MODE_PROPERTY_KEY = registerProperty(GridLineRenderMode.ALL, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            int value = (int)propertyValue;
            if (value == grid.majorXLines.renderMode) {
                return;
            }

            grid.majorXLines.renderMode = value;
            grid.requestRender();
        }
    });

    public static final int MAJOR_Y_LINE_RENDER_MODE_PROPERTY_KEY = registerProperty(GridLineRenderMode.ALL, new DependencyPropertyChangedListener() {
        @Override
        public void onPropertyChanged(Object sender, int propertyType, Object propertyValue) {
            CartesianChartGrid grid = (CartesianChartGrid)sender;

            int value = (int)propertyValue;
            if (value == grid.majorYLines.renderMode) {
                return;
            }

            grid.majorYLines.renderMode = value;
            grid.requestRender();
        }
    });

    private static final String MAJOR_LINES_VISIBILITY_KEY = "MajorLinesVisibility";
    private static final String MAJOR_LINES_THICKNESS_KEY = "MajorLinesThickness";
    private static final String MAJOR_X_LINE_DASH_ARRAY_KEY = "MajorXLineDashArray";
    private static final String MAJOR_Y_LINE_DASH_ARRAY_KEY = "MajorYLineDashArray";
    private static final String MAJOR_X_LINE_RENDER_MODE_KEY = "MajorXLineRenderMode";
    private static final String MAJOR_Y_LINE_RENDER_MODE_KEY = "MajorYLineRenderMode";
    private static final String STRIP_LINES_VISIBILITY_KEY = "StripLinesVisibility";

    public CartesianChartGridModel grid;
    private Paint emptyPaint = new Paint();
    private Paint emptyPaintSecondary = new Paint();
    private Paint linePaint = new Paint();
    private Paint verticalLinePaint = new Paint();

    private int majorLinesVisibilityCache;
    private int stripLinesVisibility;

    private GridLinesInfo majorXLines;
    private GridLinesInfo majorYLines;

    private ObservableCollection<Paint> xStripeBrushes;
    private ObservableCollection<Paint> yStripeBrushes;

    /**
     * Initializes a new instance of the {@link CartesianChartGrid} class.
     */
    public CartesianChartGrid() {
        this.grid = new CartesianChartGridModel();

        emptyPaint.setColor(Color.TRANSPARENT);
        emptyPaintSecondary.setColor(0xffe3e3e3);

        this.majorXLines = new GridLinesInfo(this);
        this.majorYLines = new GridLinesInfo(this);

        this.majorXLines.lines = this.grid.xLines;
        this.majorYLines.lines = this.grid.yLines;

        this.xStripeBrushes = new ObservableCollection<Paint>();
        this.xStripeBrushes.addCollectionChangeListener(new CollectionChangeListener<Paint>() {
            @Override
            public void collectionChanged(CollectionChangedEvent info) {
                onXStripeStylesChanged();
            }
        });

        this.yStripeBrushes = new ObservableCollection<Paint>();
        this.yStripeBrushes.addCollectionChangeListener(new CollectionChangeListener<Paint>() {
            @Override
            public void collectionChanged(CollectionChangedEvent info) {
                onYStripeStylesChanged();
            }
        });

        this.linePaint.setStyle(Paint.Style.STROKE);
        this.linePaint.setColor(Color.GRAY);
        this.linePaint.setStrokeWidth(1);

        this.verticalLinePaint.setStyle(Paint.Style.STROKE);
        this.verticalLinePaint.setColor(Color.GRAY);
        this.verticalLinePaint.setStrokeWidth(1);

        this.majorXLines.linePaint = this.verticalLinePaint;
        this.majorYLines.linePaint = this.linePaint;

        this.setMajorLinesVisibility(GridLineVisibility.Y);
    }

    public void setHorizontalAxis(Axis axis) {
        this.grid.setPrimaryAxis(axis.getModel());
    }

    public void setVerticalAxis(Axis axis) {
        this.grid.setSecondaryAxis(axis.getModel());
    }

    /**
     * Gets the color of the grid lines.
     *
     * @return The color of the grid lines.
     */
    public int getLineColor() {
        return (int)this.getValue(LINE_COLOR_PROPERTY_KEY);
    }

    /**
     * Sets the line color of the grid lines.
     *
     * @param color The new line color.
     */
    public void setLineColor(int color) {
        this.setValue(LINE_COLOR_PROPERTY_KEY, color);
    }

    public int getVerticalLineColor() {
        return (int)getValue(VERTICAL_LINE_COLOR_PROPERTY_KEY);
    }

    public void setVerticalLineColor(int value) {
        this.setValue(VERTICAL_LINE_COLOR_PROPERTY_KEY, value);
    }

    /**
     * Gets the visibility of the major grid lines.
     *
     * @return The visibility of major grid lines.
     * @see GridLineVisibility
     */
    public int getMajorLinesVisibility() {
        return (int)this.getValue(MAJOR_LINES_VISIBILITY_PROPERTY_KEY);
    }

    /**
     * Sets the visibility of the major grid lines.
     *
     * @param value The new visibility.
     * @see GridLineVisibility
     */
    public void setMajorLinesVisibility(int value) {
        this.setValue(MAJOR_LINES_VISIBILITY_PROPERTY_KEY, value);
    }

    /**
     * Gets or sets the dash array used to define each major X line.
     *
     * @return A float array that defines the line dash pattern.
     */
    public float[] getMajorXLineDashArray() {
        return (float[])this.getValue(MAJOR_X_LINE_DASH_ARRAY_PROPERTY_KEY);
    }

    /**
     * Sets the dash pattern for the major X lines.
     *
     * @param value A float array that represents the line dash pattern.
     */
    public void setMajorXLineDashArray(float[] value) {
        this.setValue(MAJOR_X_LINE_DASH_ARRAY_PROPERTY_KEY, value);
    }

    /**
     * Gets or sets the dash array used to define each major Y line.
     *
     * @return A float array that defines the line dash pattern.
     */
    public float[] getMajorYLineDashArray() {
        return (float[])this.getValue(MAJOR_Y_LINE_DASH_ARRAY_PROPERTY_KEY);
    }

    /**
     * Sets the dash pattern for the major Y lines.
     *
     * @param value A float array that represents the line dash pattern.
     */
    public void setMajorYLineDashArray(float[] value) {
        this.setValue(MAJOR_Y_LINE_DASH_ARRAY_PROPERTY_KEY, value);
    }

    /**
     * Gets the render mode of the major X lines.
     *
     * @return The X lines {@link GridLineRenderMode}.
     * @see GridLineRenderMode
     */
    public int getMajorXLinesRenderMode() {
        return (int)this.getValue(MAJOR_X_LINE_RENDER_MODE_PROPERTY_KEY);
    }

    /**
     * Sets the render mode of the major X lines.
     *
     * @param value The X lines {@link GridLineRenderMode}.
     * @see GridLineRenderMode
     */
    public void setMajorXLinesRenderMode(int value) {
        this.setValue(MAJOR_X_LINE_RENDER_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets the render mode of the major Y lines.
     *
     * @return The Y lines {@link GridLineRenderMode}.
     * @see GridLineRenderMode
     */
    public int getMajorYLinesRenderMode() {
        return (int)this.getValue(MAJOR_Y_LINE_RENDER_MODE_PROPERTY_KEY);
    }

    /**
     * Sets the render mode of the major Y lines.
     *
     * @param value The Y lines {@link GridLineRenderMode}.
     * @see GridLineRenderMode
     */
    public void setMajorYLinesRenderMode(int value) {
        this.setValue(MAJOR_Y_LINE_RENDER_MODE_PROPERTY_KEY, value);
    }

    /**
     * Gets the {@link GridLineVisibility} of the grid stripes.
     *
     * @return The {@link GridLineVisibility} of the grid stripes.
     */
    public int getStripLinesVisibility() {
        return (int)this.getValue(STRIP_LINES_VISIBILITY_PROPERTY_KEY);
    }

    /**
     * Sets the {@link GridLineVisibility} of the grid stripes.
     *
     * @param value The new visibility.
     */
    public void setStripLinesVisibility(int value) {
        this.setValue(STRIP_LINES_VISIBILITY_PROPERTY_KEY, value);
    }

    /**
     * Gets the {@link ObservableCollection} of brushes used to display x-axis stripes.
     *
     * @return The collection of brushes used to display x-axis stripes.
     */
    public ObservableCollection<Paint> getXStripeBrushes() {
        return this.xStripeBrushes;
    }

    /**
     * Gets the {@link ObservableCollection} of brushes used to display y-axis stripes.
     *
     * @return The collection of brushes used to display y-axis stripes.
     */
    public ObservableCollection<Paint> getYStripeBrushes() {
        return this.yStripeBrushes;
    }

    /**
     * Gets the line thickness for the major lines.
     *
     * @return the current line thickness for the major lines.
     */
    public float getLineThickness() {
        return (Float)this.getValue(MAJOR_LINES_THICKNESS_PROPERTY_KEY);
    }

    /**
     * Sets the line thickness for the major lines.
     *
     * @param lineThickness new line thickness.
     */
    public void setLineThickness(float lineThickness) {
        this.setValue(MAJOR_LINES_THICKNESS_PROPERTY_KEY, lineThickness);
    }

    /**
     * Gets the line thickness for the vertical lines.
     *
     * @return the current line thickness for the vertical lines.
     */
    public float getVerticalLineThickness() {
        return this.verticalLinePaint.getStrokeWidth();
    }

    /**
     * Sets the line thickness for the vertical lines.
     *
     * @param lineThickness new vertical line thickness.
     */
    public void setVerticalLineThickness(float lineThickness) {
        if (lineThickness <= 0)
            throw new IllegalArgumentException("lineThickness cannot be negative or zero");

        this.verticalLinePaint.setStrokeWidth(lineThickness);
    }

    @Override
    protected String defaultPaletteFamily() {
        return ChartPalette.CARTESIAN_CHART_GRID;
    }

    /**
     * Gets the grid model.
     *
     * @return The grid model.
     */
    protected ChartElement getElement() {
        return this.grid;
    }

    /**
     * Gets the Z Index of the grid relative to the children inside the chart view's render surface.
     *
     * @return The Z Index of the grid relative to the children inside the chart view's render surface.
     */
    protected int getDefaultZIndex() {
        return 0;
    }

    @Override
    protected void applyPaletteCore(ChartPalette palette) {
        super.applyPaletteCore(palette);

        PaletteEntryCollection gridStripLinesPalette = palette.entriesForFamily(ChartPalette.CARTESIAN_CHART_GRID_STRIPES);
        if (gridStripLinesPalette != null) {
            for (PaletteEntry entry : gridStripLinesPalette) {
                Paint paint = new Paint();
                paint.setColor(entry.getStroke());

                this.xStripeBrushes.add(paint);
                this.yStripeBrushes.add(paint);
            }
        }

        PaletteEntry gridLinesPalette = palette.getEntry(this.getPaletteFamilyCore(), 0);
        if (gridLinesPalette == null) {
            return;
        }

        this.setValue(LINE_COLOR_PROPERTY_KEY, PALETTE_VALUE, gridLinesPalette.getStroke());
        this.setValue(VERTICAL_LINE_COLOR_PROPERTY_KEY, PALETTE_VALUE, gridLinesPalette.getStroke());

        String stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_LINES_THICKNESS_KEY);
        if (stringValue != null) {
            float majorLinesThickness = Float.parseFloat(stringValue);
            this.setValue(MAJOR_LINES_THICKNESS_PROPERTY_KEY, PALETTE_VALUE, majorLinesThickness);
        }

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_LINES_VISIBILITY_KEY);
        if (stringValue != null)
            this.setValue(MAJOR_LINES_VISIBILITY_PROPERTY_KEY, PALETTE_VALUE, GridLineVisibility.valueOf(stringValue));

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.STRIP_LINES_VISIBILITY_KEY);
        if (stringValue != null)
            this.setValue(STRIP_LINES_VISIBILITY_PROPERTY_KEY, PALETTE_VALUE, GridLineVisibility.valueOf(stringValue));

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_X_LINE_DASH_ARRAY_KEY);
        if (stringValue != null) {
            String[] dashArrayValues = stringValue.split(",");
            float[] values = new float[dashArrayValues.length];
            for (int i = 0; i < dashArrayValues.length; i++) {
                values[i] = Float.parseFloat(dashArrayValues[i]);
            }

            this.setValue(MAJOR_X_LINE_DASH_ARRAY_PROPERTY_KEY, PALETTE_VALUE, values);
        }

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_Y_LINE_DASH_ARRAY_KEY);
        if (stringValue != null) {
            String[] dashArrayValues = stringValue.split(",");
            float[] values = new float[dashArrayValues.length];
            for (int i = 0; i < dashArrayValues.length; i++) {
                values[i] = Float.parseFloat(dashArrayValues[i]);
            }

            this.setValue(MAJOR_Y_LINE_DASH_ARRAY_PROPERTY_KEY, PALETTE_VALUE, values);
        }

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_X_LINE_RENDER_MODE_KEY);
        if (stringValue != null)
            this.setValue(MAJOR_X_LINE_RENDER_MODE_PROPERTY_KEY, PALETTE_VALUE, GridLineRenderMode.valueOf(stringValue));

        stringValue = gridLinesPalette.getCustomValue(CartesianChartGrid.MAJOR_Y_LINE_RENDER_MODE_KEY);
        if (stringValue != null)
            this.setValue(MAJOR_Y_LINE_RENDER_MODE_PROPERTY_KEY, PALETTE_VALUE, GridLineRenderMode.valueOf(stringValue));
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        this.applyClip(canvas);
        this.updateVisuals(canvas);
        canvas.restore();
    }

    @Override
    protected void onAttached() {
        super.onAttached();

        ((ChartAreaModelWithAxes) this.getChart().getChartArea()).setGrid(this.grid);
    }

    @Override
    protected void onDetached(RadChartViewBase oldChart) {
        super.onDetached(oldChart);

        ((ChartAreaModelWithAxes) oldChart.getChartArea()).setGrid(null);
    }

    private void onMajorXLineDashArrayChanged(float[] newValue) {
        this.updateGridLineDashArray(this.majorXLines, newValue);
    }

    private void onMajorYLineDashArrayChanged(float[] newValue) {
        this.updateGridLineDashArray(this.majorYLines, newValue);
    }

    private void onMajorLinesVisibilityChanged() {
        this.majorXLines.visible = (this.majorLinesVisibilityCache & GridLineVisibility.X) == GridLineVisibility.X;
        this.majorYLines.visible = (this.majorLinesVisibilityCache & GridLineVisibility.Y) == GridLineVisibility.Y;

        this.requestRender();
    }

    private void updateGridLineDashArray(GridLinesInfo gridLinesInfo, float[] dashArray) {
        if (gridLinesInfo != null) {
            gridLinesInfo.dashArray = dashArray;
            this.requestRender();
        }
    }

    private void applyClip(Canvas canvas) {
        final RadRect currentClip = this.lastLayoutContext.clipRect();
        canvas.clipRect(
                (int) currentClip.getX(),
                (int) currentClip.getY(),
                (int) currentClip.getRight(),
                (int) currentClip.getBottom()
        );
    }

    private void updateVisuals(Canvas canvas) {
        this.updateXStripes(canvas);
        this.updateYStripes(canvas);

        this.majorXLines.drawLines(canvas);
        this.majorYLines.drawLines(canvas);
    }

    private Paint getStripeColor(ArrayList<Paint> colors, int brushIndex) {
        brushIndex = colors.size() > 0 ? brushIndex % colors.size() : brushIndex;
        return brushIndex < colors.size() ? colors.get(brushIndex) : ((brushIndex & 1) != 1) ? this.emptyPaint : this.emptyPaintSecondary;
    }

    private void updateXStripes(Canvas canvas) {
        if ((this.stripLinesVisibility & GridLineVisibility.X) == GridLineVisibility.X) {
            for (GridStripe stripe : this.grid.xStripes) {
                Paint color = this.getStripeColor(this.xStripeBrushes, stripe.startTick.virtualIndex());
                RadRect stripeRect = stripe.fillRect;
                canvas.drawRect(Util.convertToRectF(stripeRect), color);
            }
        }
    }

    private void updateYStripes(Canvas canvas) {
        if ((this.stripLinesVisibility & GridLineVisibility.Y) == GridLineVisibility.Y) {
            for (GridStripe stripe : this.grid.yStripes) {
                Paint color = this.getStripeColor(this.yStripeBrushes, stripe.startTick.virtualIndex());
                RadRect stripeRect = stripe.fillRect;
                canvas.drawRect(Util.convertToRectF(stripeRect), color);
            }
        }
    }

    private void onXStripeStylesChanged() {
        this.requestRender();
    }

    private void onYStripeStylesChanged() {
        this.requestRender();
    }

    private class GridLinesInfo {
        public float[] dashArray;
        public boolean visible;
        public int renderMode = GridLineRenderMode.ALL;
        public ArrayList<GridLine> lines;
        public CartesianChartGrid owner;
        public Paint linePaint;

        public GridLinesInfo(CartesianChartGrid owner) {
            this.owner = owner;
        }

        public void drawLines(Canvas canvas) {
            if (!this.visible) {
                return;
            }

            for (GridLine line : this.lines) {

                if (!this.shouldDrawLine(line)) {
                    continue;
                }

                RadPoint p1 = line.point1;
                RadPoint p2 = line.point2;

                if (this.dashArray != null) {
                    linePaint.setPathEffect(new DashPathEffect(this.dashArray, 0));
                } else {
                    linePaint.setPathEffect(null);
                }

                Path path = new Path();
                path.moveTo((float) p1.getX(), (float) p1.getY());
                path.lineTo((float) p2.getX(), (float) p2.getY());

                canvas.drawPath(path, linePaint);
            }
        }

        private boolean shouldDrawLine(GridLine line) {
            AxisTickModel tick = line.axisTickModel;
            if (tick.position() == TickPosition.FIRST && (this.renderMode & GridLineRenderMode.FIRST) != GridLineRenderMode.FIRST) {
                return false;
            }

            if (tick.position() == TickPosition.INNER && (this.renderMode & GridLineRenderMode.INNER) != GridLineRenderMode.INNER) {
                return false;
            }

            if (tick.position() == TickPosition.LAST && (this.renderMode & GridLineRenderMode.LAST) != GridLineRenderMode.LAST) {
                return false;
            }

            return true;
        }
    }
}
