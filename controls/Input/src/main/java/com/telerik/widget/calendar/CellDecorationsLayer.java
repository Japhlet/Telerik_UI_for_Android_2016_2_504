package com.telerik.widget.calendar;

import com.telerik.widget.calendar.decorations.SegmentDecorator;

/**
 * Represents a layer which draws decoration lines over the lines
 * drawn by {@link com.telerik.widget.calendar.GridLinesLayer}. Deprecated: use {@link com.telerik.widget.calendar.decorations.SegmentDecorator} instead.
 */
public class CellDecorationsLayer extends SegmentDecorator {

    /**
     * Creates an instance of the {@link CellDecorationsLayer} class.
     *
     * @param owner the current calendar instance owning the element.
     */
    public CellDecorationsLayer(RadCalendarView owner) {
        super(owner);
    }

    /**
     * Used for adding a decoration for a calendar cell. Adding decoration on an already decorated cell
     * will disable the previous decoration and will not apply the current. Deprecated: use {@link #toggleDecorationForCell(com.telerik.widget.calendar.CalendarCell)} instead.
     *
     * @param cell the cell to be decorated.
     * @deprecated
     */
    public void addDecorationForCell(CalendarCell cell) {
        addDecorationForCell(0, cell);
    }

    /**
     * Used for adding a decoration for a calendar cell on a specified layer. Adding decoration on an already decorated cell
     * will disable the previous decoration and will not apply the current. Deprecated: use {@link #toggleDecorationForCell(CalendarCell, int)} instead.
     *
     * @param layerId the id of the layer that will store the decoration.
     * @param cell    the cell to be decorated.
     * @deprecated
     */
    public void addDecorationForCell(int layerId, CalendarCell cell) {
        toggleDecorationForCell(cell.getLeft(), cell.getTop(),
                cell.getRight(), cell.getBottom(), layerId);
    }


    /**
     * Adds a decoration for a cell using the provided borders. Deprecated: use {@link #toggleDecorationForCell(int, int, int, int)} instead.
     *
     * @param left   the left border.
     * @param top    the top border.
     * @param right  the right border.
     * @param bottom the bottom border.
     * @deprecated
     */
    public void addDecorationForCell(int left, int top, int right, int bottom) {
        this.toggleDecorationForCell(left, top, right, bottom, 0);
    }

    /**
     * Adds a decoration for a cell using the provided borders and an id for the layer to be used when storing the decoration.
     * The layers do matter in the modes involving overlapping of fragments, where the decorations will be called for rendering along with their
     * corresponding fragment by its fragment id, which will be the layer of the decoration. Deprecated: use {@link #toggleDecorationForCell(int, int, int, int, int)}
     *
     * @param layerId the id of the layer that will store the decoration.
     * @param left    the left border.
     * @param top     the top border.
     * @param right   the right border.
     * @param bottom  the bottom border.
     * @deprecated
     */
    public void addDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        this.toggleDecorationForCell(left, top, right, bottom, layerId);
    }

    /**
     * Removes a decoration for a cell using the provided borders and an id for the layer to be used when storing the decoration.
     * The layers do matter in the modes involving overlapping of fragments, where the decorations will be called for rendering along with their
     * corresponding fragment by its fragment id, which will be the layer of the decoration. Deprecated: use {@link #toggleDecorationForCell(int, int, int, int, int)}
     *
     * @param layerId the id of the layer that will store the decoration.
     * @param left    the left border.
     * @param top     the top border.
     * @param right   the right border.
     * @param bottom  the bottom border.
     * @deprecated
     */
    public void removeDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        this.toggleDecorationForCell(left, top, right, bottom, layerId);
    }
}
