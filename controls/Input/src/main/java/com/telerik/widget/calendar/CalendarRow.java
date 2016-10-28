package com.telerik.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.ArrayList;

/**
 * Responsible for holding and managing a collection of {@link com.telerik.widget.calendar.CalendarCell} instances.
 */
public class CalendarRow extends CalendarElement {

    public static final int WEEK_NUMBER_CELL_INDEX = 0;

    /**
     * Holds the collection of cells managed by this row instance.
     */
    protected final ArrayList<CalendarCell> cells;

    int firstVisibleCellIndex;
    int lastVisibleCellIndex;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.CalendarRow} class.
     *
     * @param owner the current calendar that owns this row instance.
     */
    public CalendarRow(RadCalendarView owner) {
        super(owner);

        this.cells = new ArrayList<CalendarCell>();
        this.setBackgroundColor(Color.TRANSPARENT, Color.TRANSPARENT);
    }

    @Override
    protected void onAlphaChanged() {
        for (CalendarCell cell : this.cells)
            cell.setAlpha(this.alpha);
    }

    /**
     * Adds a cell to the current row.
     *
     * @param cell the cell to be added.
     */
    public void addCell(CalendarCell cell) {
        this.cells.add(cell);
        cell.setRow(this);
    }

    /**
     * Gets a cell at a given index.
     *
     * @param index the index of the cell.
     * @return the cell at this index.
     */
    public CalendarCell getCell(int index) {
        return this.cells.get(index);
    }

    /**
     * The number of cells currently added to this row instance.
     *
     * @return the number of cells.
     */
    public int cellsCount() {
        return this.cells.size();
    }

    @Override
    protected void onArrange() {
        super.onArrange();

        int visibleCellsCount = 0;

        for (int i = 0, len = this.cellsCount(); i < len; i++)
            if (this.cells.get(i).getVisibility() != ElementVisibility.Gone)
                visibleCellsCount++;

        int cellWidth = (this.getWidth() / visibleCellsCount);
        int currentHorizontalPosition = this.getLeft();
        for (int i = 0; i < cells.size(); i++) {
            CalendarElement element = cells.get(i);
            if (element.getVisibility() == ElementVisibility.Gone)
                continue;

            if (i < this.cells.size() - 1) {
                element.arrange(currentHorizontalPosition, getTop(), (currentHorizontalPosition + cellWidth), getBottom());
                currentHorizontalPosition += cellWidth;
            } else {
                element.arrange(currentHorizontalPosition, getTop(), getRight(), getBottom());
                break;
            }
        }
    }

    @Override
    public void render(Canvas canvas) {
        //super.render(canvas);

        for (CalendarCell cell : this.cells)
            if (cell.getVisibility() == ElementVisibility.Visible) {
                cell.render(canvas);
            }
    }

    @Override
    public void postRender(Canvas canvas) {
        for (CalendarCell cell : this.cells)
            if (cell.getVisibility() == ElementVisibility.Visible) {
                cell.postRender(canvas);
            }
    }

    @Override
    public void setVirtualOffsets(int offsetX, int offsetY) {
        super.setVirtualOffsets(offsetX, offsetY);

        for (CalendarCell cell : this.cells) {
            cell.setVirtualOffsets(offsetX, offsetY);
        }
    }
}
