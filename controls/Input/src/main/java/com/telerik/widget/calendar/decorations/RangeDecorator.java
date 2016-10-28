package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.CalendarSelectionMode;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.ScrollMode;

import java.util.List;

/**
 * Basic class for rendering range of cells.
 */
public abstract class RangeDecorator extends CellDecorator {

    private boolean shapeOpened;
    private int currentCellIndex;

    /**
     * The first date to be decorated.
     */
    protected long firstDateForDecoration;

    /**
     * The last date to be decorated.
     */
    protected long lastDateForDecoration;

    /**
     * Scale of the shape.
     */
    protected float shapeScale = .95f;

    /**
     * Color for the shape of the decoration.
     */
    protected int shapeColor = Color.parseColor("#009688");

    /**
     * Value holding the bonds of the shape.
     */
    protected RectF shapeBounds = new RectF();

    /**
     * The size of the shape. Gets updated on {@link #renderLayer(int, android.graphics.Canvas)} using the current {@link #shapeScale}
     */
    protected int shapeSize;

    /**
     * The size of the indicator. Gets updated on {@link #renderLayer(int, android.graphics.Canvas)}
     */
    protected int indicatorSize;
    private int clipLeftBorder;
    private int clipRightBorder;

    /**
     * Creates a new instance of the {@link RangeDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public RangeDecorator(RadCalendarView owner) {
        super(owner);
        if (owner.getSelectionMode() != CalendarSelectionMode.Range)
            throw new RuntimeException("Range decorators only work with range selection mode! Please make sure that your RadCalendarView instance is currently in range selection mode.");

        setStroked(false);
        resetMinMaxDates();
    }

    /**
     * Gets the color of the shape.
     *
     * @return the current color of the shape.
     */
    public int getShapeColor() {
        return shapeColor;
    }

    /**
     * Sets the color of the shape.
     *
     * @param shapeColor the new color of the shape.
     */
    public void setShapeColor(int shapeColor) {
        if (this.shapeColor != shapeColor) {
            this.shapeColor = shapeColor;
            this.owner.invalidate();
        }
    }

    /**
     * Gets the shapeScale of the decorator, where 0 will have no decorator and 1 will have the decorator as big as the cell it decorates.
     *
     * @return the current decorator shapeScale.
     */
    public float getShapeScale() {
        return shapeScale;
    }

    /**
     * Sets the shapeScale of the decorator, where 0 will have no decorator and 1 will have the decorator as big as the cell it decorates.
     *
     * @param shapeScale the new decorator shapeScale.
     */
    public void setShapeScale(float shapeScale) {
        if (shapeScale < 0 || shapeScale > 1)
            throw new IllegalArgumentException("shapeScale must be between 0 and 1 inclusive");

        if (this.shapeScale != shapeScale) {
            this.shapeScale = shapeScale;
            this.owner.invalidate();
        }
    }

    @Override
    public void toggleDecorationForCell(CalendarCell cell, int layerId) {
        super.toggleDecorationForCell(cell, layerId);

        if (cell.getDate() > lastDateForDecoration)
            lastDateForDecoration = cell.getDate();

        if (cell.getDate() < firstDateForDecoration)
            firstDateForDecoration = cell.getDate();
    }

    List<CalendarCell> lastRenderedLayer;

    @Override
    public void renderLayer(int layerId, Canvas canvas) {
        List<CalendarCell> layerCells = cellsForDecoration.get(layerId);

        boolean safeRendering = this.owner.getScrollMode() == ScrollMode.Overlap || this.owner.getScrollMode() == ScrollMode.Stack;
        boolean shapeWasOpened = shapeOpened;

        if (layerCells == null || layerCells.size() == 0) {
            return;
        }

        CalendarCell cellLeft = layerCells.get(0);
        this.shapeSize = ((int) (Math.min(cellLeft.getWidth(), cellLeft.getHeight()) * this.shapeScale)) >> 1;
        this.indicatorSize = ((int) (Math.min(cellLeft.getWidth(), cellLeft.getHeight()) * this.scale)) >> 1;

        if (layerCells.size() == 1) {
            if (cellLeft.getDate() == lastDateForDecoration) {
                renderIndicatorForCell(canvas, cellLeft);
                if (!stroked) {
                    renderCell(canvas, cellLeft);
                }
            }

            if (cellLeft.getDate() == firstDateForDecoration && cellLeft.getDate() == lastDateForDecoration)
                return;
        }

        this.currentCellIndex = 0;

        while (this.currentCellIndex < layerCells.size()) {
            cellLeft = layerCells.get(this.currentCellIndex++);

            if (!this.shapeOpened) {

                openShape(
                        cellLeft.getDate() > this.firstDateForDecoration ? cellLeft.virtualLeft() - this.shapeSize : cellLeft.virtualLeft() + (cellLeft.getWidth() >> 1) - this.shapeSize,
                        cellLeft.virtualTop() + (cellLeft.getHeight() >> 1) - this.shapeSize,
                        cellLeft.virtualBottom() - (cellLeft.getHeight() >> 1) + this.shapeSize,
                        cellLeft.virtualLeft()
                );
            }

            while (this.currentCellIndex < layerCells.size()) {
                if (layerCells.get(currentCellIndex).virtualTop() != cellLeft.virtualTop()) {
                    closeShape(layerCells.get(currentCellIndex - 1).virtualRight() + this.shapeSize, layerCells.get(currentCellIndex - 1).virtualRight());
                    callShapeRender(canvas);
                    break;
                }

                currentCellIndex++;
            }
        }

        CalendarCell cellRight = layerCells.get(layerCells.size() - 1);
        if (cellLeft.virtualTop() != cellRight.virtualTop()) {
            openShape(
                    cellRight.virtualLeft() - this.shapeSize,
                    cellRight.virtualTop() + (cellRight.getHeight() >> 1) - this.shapeSize,
                    cellRight.virtualBottom() - (cellRight.getHeight() >> 1) + this.shapeSize,
                    cellRight.virtualLeft()
            );
        }

        if ((this.shapeOpened && safeRendering) ||
                cellRight.isLastCellInRow() ||
                (cellRight.getDate() == lastDateForDecoration)) {

            closeShape(
                    cellRight.getDate() < this.lastDateForDecoration ?
                            cellRight.virtualRight() + this.shapeSize :
                            cellRight.virtualLeft() + (cellRight.getWidth() >> 1) + this.shapeSize,
                    cellRight.virtualRight());
        }

        if (!this.shapeOpened) {
            callShapeRender(canvas);
        }

        if (cellRight.getDate() == this.lastDateForDecoration)
            renderIndicatorForCell(canvas, cellRight);

        if (!stroked) {
            for (CalendarCell cell : layerCells) {
                renderCell(canvas, cell);
            }
        }

        if (shapeWasOpened) {
            for (CalendarCell cell : lastRenderedLayer) {
                renderCell(canvas, cell);
            }
        }

        lastRenderedLayer = layerCells;
    }

    /**
     * Used to open the shape at specific coordinates.
     *
     * @param left   the left coordinate of the shape.
     * @param top    the top coordinate of the shape.
     * @param bottom the bottom coordinate of the shape.
     */
    protected void openShape(int left, int top, int bottom, int clipLeftBorder) {
        this.clipLeftBorder = clipLeftBorder;
        this.shapeBounds.left = left;
        this.shapeBounds.top = top;
        this.shapeBounds.bottom = bottom;

        this.shapeOpened = true;
    }

    /**
     * Used the close the decorator shape on the specified coordinate.
     *
     * @param right the right coordinate of the shape.
     */
    protected void closeShape(int right, int clipRightBorder) {
        this.clipRightBorder = clipRightBorder;
        this.shapeBounds.right = right;

        this.shapeOpened = false;
    }

    /**
     * Used to draw a single cell in the range.
     *
     * @param canvas the canvas onto which the cell will be rendered.
     * @param cell   the cell which will be rendered.
     */
    protected void renderCell(Canvas canvas, CalendarCell cell) {
        canvas.drawText(cell.getText(), cell.textPositionX() + cell.getVirtualOffsetX(), cell.textPositionY() + cell.getVirtualOffsetY(), cell.getTextPaint());
    }

    /**
     * Used to draw the shape of the range.
     *
     * @param canvas the canvas onto which the shape will be rendered.
     */
    protected abstract void renderShape(Canvas canvas, RectF shapeBounds);

    /**
     * Used to draw the indicator of the range.
     *
     * @param canvas  the canvas onto which the indicator will be rendered.
     * @param centerX the x position of the indicator.
     * @param centerY the y position of the indicator.
     */
    protected abstract void renderIndicator(Canvas canvas, int centerX, int centerY);

    @Override
    public void clearDecorations() {
        super.clearDecorations();
        resetMinMaxDates();
    }

    private void renderIndicatorForCell(Canvas canvas, CalendarCell cell) {
        renderIndicator(canvas, cell.virtualLeft() + (cell.getWidth() >> 1), cell.virtualTop() + (cell.getHeight() >> 1));
    }

    private void resetMinMaxDates() {
        this.firstDateForDecoration = Long.MAX_VALUE;
        this.lastDateForDecoration = Long.MIN_VALUE;
    }

    private void callShapeRender(Canvas canvas) {
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(this.clipLeftBorder - (strokeWidth / 2), 0, this.clipRightBorder + (strokeWidth / 2), shapeBounds.bottom + (strokeWidth / 2));
        renderShape(canvas, this.shapeBounds);
        canvas.restore();
    }
}
