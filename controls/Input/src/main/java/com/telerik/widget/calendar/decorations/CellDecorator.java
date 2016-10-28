package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.RadCalendarView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Basic class for decorating cells. It stores the cells based on a given layer id and then renders them accordingly.
 */
public abstract class CellDecorator extends Decorator {

    /**
     * Value determining whether the decoration will be stroked or filled.
     */
    protected boolean stroked = true;

    /**
     * Scale of the indicator.
     */
    protected float scale = .80f;

    /**
     * Collection of cells sorted by their layer id that are prepared for decoration.
     */
    protected Hashtable<Integer, List<CalendarCell>> cellsForDecoration;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.decorations.CellDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public CellDecorator(RadCalendarView owner) {
        super(owner);

        this.cellsForDecoration = new Hashtable<Integer, List<CalendarCell>>();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets a value determining whether the decorator will be stroked or filled.
     *
     * @return <code>true</code> will set the decorator to be stroked, <code>false</code> will make it filled.
     */
    public boolean isStroked() {
        return stroked;
    }

    /**
     * Gets a value determining whether the decorator will be stroked or filled.
     *
     * @param stroked the new style of the decorator - <code>true</code> will set the decorator to be stroked, <code>false</code> will make it filled.
     */
    public void setStroked(boolean stroked) {
        if (this.stroked != stroked) {
            this.stroked = stroked;
            if (stroked)
                this.paint.setStyle(Paint.Style.STROKE);
            else
                this.paint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void clearDecorations() {
        for (List<CalendarCell> cellsForLayer : this.cellsForDecoration.values()) {
            cellsForLayer.clear();
        }
    }

    @Override
    public void toggleDecorationForCell(CalendarCell cell, int layerId) {
        if (!this.cellsForDecoration.containsKey(layerId)) {
            this.cellsForDecoration.put(layerId, new ArrayList<CalendarCell>());
        }

        this.cellsForDecoration.get(layerId).add(cell);
    }

    @Override
    public void renderLayer(int layerId, Canvas canvas) {
        List<CalendarCell> cellsForLayer = cellsForDecoration.get(layerId);
        if (cellsForLayer != null) {
            for (CalendarCell cell : cellsForLayer) {
                renderDecorationForCell(canvas, cell);
            }
        }
    }

    /**
     * Renders the decoration for a given cell.
     *
     * @param canvas the canvas onto which the decoration will be rendered.
     * @param cell   the cell for which a decoration will be rendered.
     */
    protected void renderDecorationForCell(Canvas canvas, CalendarCell cell) {
    }
}
