package com.telerik.widget.calendar.decorations;

import android.graphics.Canvas;
import android.util.SparseArray;

import com.telerik.widget.calendar.CalendarCell;
import com.telerik.widget.calendar.CalendarDisplayMode;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.ScrollMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to decorate multiple cells and render segments for each one according to their positions.
 */
public class SegmentDecorator extends Decorator {

    private final SparseArray<List<LineSegment>> categorizedLineSegments;
    private final List<LineSegment> decorationSegments;

    /**
     * The half of the stroke width.
     */
    protected int halfStrokeWidth;

    /**
     * Creates a new instance of the {@link SegmentDecorator} class.
     *
     * @param owner the calendar instance owning this decorator.
     */
    public SegmentDecorator(RadCalendarView owner) {
        super(owner);

        this.categorizedLineSegments = new SparseArray<List<LineSegment>>();
        this.decorationSegments = new ArrayList<LineSegment>();
    }

    @Override
    public void setStrokeWidth(float value) {
        super.setStrokeWidth(value);

        this.halfStrokeWidth = (int) (value / 2);
    }

    @Override
    public void clearDecorations() {
        this.decorationSegments.clear();
        for (int i = 0, len = this.categorizedLineSegments.size(); i < len; i++) {
            this.categorizedLineSegments.valueAt(i).clear();
        }
    }

    @Override
    public void renderLayer(int layerId, Canvas canvas) {
        List<LineSegment> segmentsForLayer = this.categorizedLineSegments.get(layerId);

        if (segmentsForLayer == null)
            return;

        for (LineSegment segment : segmentsForLayer)
            canvas.drawLine(segment.startX, segment.startY, segment.endX, segment.endY, this.paint);
    }

    @Override
    public void toggleDecorationForCell(CalendarCell cell, int layerId) {
        toggleDecorationForCell(cell.virtualLeft(), cell.virtualTop(), cell.virtualRight(), cell.virtualBottom(), layerId);
    }

    /**
     * Used to toggle the decoration of a specified cell instance by using its border coordinates. If the cell is already being marked for decoration, the previous decoration
     * will be removed and the new one will not be added. If currently there is no decoration for the cell, a new decoration will be prepared for the time of invalidation.
     *
     * @param left   the left coordinate of the cell.
     * @param top    the top coordinate of the cell.
     * @param right  the right coordinate of the cell.
     * @param bottom the bottom coordinate of the cell.
     */
    public void toggleDecorationForCell(int left, int top, int right, int bottom) {
        toggleDecorationForCell(left, top, right, bottom, 0);
    }

    /**
     * Used to toggle the decoration of a specified cell instance by using its border coordinates. If the cell is already being marked for decoration, the previous decoration
     * will be removed and the new one will not be added. If currently there is no decoration for the cell, a new decoration will be prepared for the time of invalidation.
     * The decoration will be stored on the specified layer by using the provided layer id (usually the id of the fragment holding the cell instance).
     *
     * @param left    the left coordinate of the cell.
     * @param top     the top coordinate of the cell.
     * @param right   the right coordinate of the cell.
     * @param bottom  the bottom coordinate of the cell.
     * @param layerId the id of the layer that will store the decoration.
     */
    public void toggleDecorationForCell(int left, int top, int right, int bottom, int layerId) {
        this.changeDecorationForCell(layerId, left, top, right, bottom);
    }

    private void changeDecorationForCell(int layerId, int left, int top, int right, int bottom) {
        int id = owner.getScrollMode() == ScrollMode.Stack ? layerId : 0;
        int strokeWidth = (int)getStrokeWidth();
        int halfStroke = strokeWidth / 2;

        if(owner.getDisplayMode() == CalendarDisplayMode.Week) {
            bottom -= 1;
        }

        LineSegment leftBorder = new LineSegment(left, top - halfStroke, left, bottom + halfStroke, id);
        LineSegment topBorder = new LineSegment(left, top, right, top, id);
        LineSegment rightBorder = new LineSegment(right, top - halfStroke, right, bottom + halfStroke, id);
        LineSegment bottomBorder = new LineSegment(left, bottom, right, bottom, id);

        handleSegmentDecorationChange(layerId, leftBorder);
        handleSegmentDecorationChange(layerId, topBorder);
        handleSegmentDecorationChange(layerId, rightBorder);
        handleSegmentDecorationChange(layerId, bottomBorder);
    }

    private void handleSegmentDecorationChange(int id, LineSegment segment) {
        List<LineSegment> lineSegmentsForId = this.categorizedLineSegments.get(id);
        if (lineSegmentsForId == null) {
            lineSegmentsForId = new ArrayList<LineSegment>();
            this.categorizedLineSegments.put(id, lineSegmentsForId);
        }

        if (this.decorationSegments.contains(segment)) {
            this.decorationSegments.remove(segment);
            if ((this.owner.getScrollMode() != ScrollMode.Overlap && this.owner.getScrollMode() != ScrollMode.Stack))
                removeSegmentFromAllLayers(segment);
            else {
                removeSegmentForLayer(segment, id);
            }
        } else {
            this.decorationSegments.add(segment);
            lineSegmentsForId.add(segment);
        }
    }

    private void removeSegmentForLayer(LineSegment segment, int id) {
        List<LineSegment> layer = this.categorizedLineSegments.get(id);
        if (layer != null)
            layer.remove(segment);
    }

    private void removeSegmentFromAllLayers(LineSegment segment) {
        for (int i = 0, len = this.categorizedLineSegments.size(); i < len; i++) {
            this.categorizedLineSegments.valueAt(i).remove(segment);
        }
    }

    private class LineSegment {

        private final int startX;
        private final int endX;
        private final int startY;
        private final int endY;
        private final int id;

        LineSegment(int startX, int startY, int endX, int endY, int id) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null)
                return false;
            if (o == this)
                return true;
            if (!(o instanceof LineSegment))
                return false;

            LineSegment second = (LineSegment) o;
            return id == second.id &&
                    startX == second.startX &&
                    startY == second.startY &&
                    endX == second.endX &&
                    endY == second.endY;
        }

        @Override
        public int hashCode() {
            String result = String.format("%d.%d.%d.%d.%d", startX, startY, endX, endY, id);
            return result.hashCode();
        }
    }
}
