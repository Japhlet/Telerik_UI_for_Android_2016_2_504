package com.telerik.widget.calendar.events;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;

import com.telerik.android.common.Util;
import com.telerik.widget.calendar.CalendarDayCell;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A default renderer class which draws the events in {@link com.telerik.widget.calendar.RadCalendarView}.
 */
public class EventRenderer {

    private static final int TICKS_IN_A_DAY = 84600000;
    private static final int DEFAULT_EVENT_TEXT_SIZE_SP = 9;

    private final Paint eventPaint;
    private EventRenderMode eventRenderMode;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.EventRenderer} class.
     *
     * @param context context to be used
     */
    public EventRenderer(Context context) {

        this.eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.eventPaint.setTextSize(Util.getDimen(TypedValue.COMPLEX_UNIT_SP, DEFAULT_EVENT_TEXT_SIZE_SP));

        this.eventRenderMode = EventRenderMode.Shape_And_Text;
    }

    /**
     * Renders the events for the provided cell inside the provided canvas.
     *
     * @param canvas drawing canvas
     * @param cell   cell which contains the events that should be drawn
     */
    public void renderEvents(Canvas canvas, CalendarDayCell cell) {
        switch (this.eventRenderMode) {
            case Text:
                drawEventsInModeText(canvas, cell);
                break;
            case Shape:
                drawEventsInModeShape(canvas, cell);
                break;
            case Shape_And_Text:
                drawEventsInModeShapeAndText(canvas, cell);
                break;
        }
    }

    /**
     * Gets the current {@link com.telerik.widget.calendar.events.EventRenderMode}
     * which determines how the events should be drawn.
     *
     * @return the current event render mode
     */
    public EventRenderMode getEventRenderMode() {
        return eventRenderMode;
    }

    /**
     * Sets a new {@link com.telerik.widget.calendar.events.EventRenderMode}
     * which determines how the events will be drawn.
     *
     * @param eventRenderMode the new event render mode
     */
    public void setEventRenderMode(EventRenderMode eventRenderMode) {
        this.eventRenderMode = eventRenderMode;
    }

    /**
     * Gets the text size for drawing of events.
     *
     * @return the current text size
     */
    public float getEventTextSize() {
        return this.eventPaint.getTextSize();
    }

    /**
     * Sets a new size that will be used by this renderer to draw events.
     *
     * @param eventTextSize the new text size
     */
    public void setEventTextSize(float eventTextSize) {
        this.eventPaint.setTextSize(eventTextSize);
    }

    private void drawEventsInModeText(Canvas canvas, CalendarDayCell cell) {

        int width = cell.getWidth();
        int height = cell.getHeight();

        int padding = cell.getPaddingRight() / 4;

        Rect drawTextRect = new Rect();
        if (cell.getText() != null) {
            String text = cell.getText();
            cell.getTextPaint().getTextBounds(text, 0, text.length(), drawTextRect);
        }

        int spacingForDateVertical = width < height ? drawTextRect.height() + cell.getPaddingBottom() : 0;
        int spacingForDateHorizontal = width > height ? drawTextRect.width() + cell.getPaddingLeft() : 0;

        Rect drawSurface = new Rect(
                padding,
                (padding + spacingForDateVertical),
                (width - padding - spacingForDateHorizontal),
                (height - padding));

        if (width > height) {
            spacingForDateVertical = drawTextRect.height() + cell.getPaddingBottom();
        }

        int currentVerticalOffset = 2 * padding;

        String dummyText = "Dummy text";
        this.eventPaint.getTextBounds(dummyText, 0, dummyText.length(), drawTextRect);
        int generalEventHeight = drawTextRect.height() + padding;

        int remainingEventsCount = cell.getEvents().size();

        for (Event event : cell.getEvents()) {

            String eventTitle = event.getTitle();
            if(eventTitle == null) {
                eventTitle = "";
            }

            int color = event.getEventColor();

            this.eventPaint.setColor(color);
            this.eventPaint.getTextBounds(eventTitle, 0, eventTitle.length(), drawTextRect);

            int spaceForEvent = drawSurface.height();
            int eventsToDraw = remainingEventsCount;
            if (event.isAllDay() && width > height) {
                spaceForEvent -= spacingForDateVertical;
                eventsToDraw = 1;
            }

            if (canDrawEvent(generalEventHeight - padding, spaceForEvent, currentVerticalOffset, padding, eventsToDraw)) {
                if (event.isAllDay()) {
                    eventTitle = getClampedEventTitle(eventTitle, drawSurface.width() - 3 * padding);
                    this.drawTextWithBackground(canvas, eventTitle, cell.getLeft(), (cell.getTop() + drawSurface.bottom - generalEventHeight), padding, Color.WHITE, color, width, generalEventHeight + padding, drawTextRect);
                    drawSurface.bottom -= generalEventHeight + padding;
                } else {
                    eventTitle = getClampedEventTitle(eventTitle, drawSurface.width() - generalEventHeight - 3 * padding);
                    this.drawTextWithShape(canvas, eventTitle, (cell.getLeft() + drawSurface.left), (cell.getTop() + drawSurface.top + currentVerticalOffset), padding, color, color, 0, drawTextRect);
                    currentVerticalOffset += generalEventHeight + padding;
                }
                remainingEventsCount--;
            } else {
                this.eventPaint.setColor(Color.GRAY);
                String additionalEventsText = String.format("+%d", remainingEventsCount);
                double additionalTextLeft = drawSurface.left + padding;
                double additionalTextBottom = drawSurface.bottom - padding;
                canvas.drawText(additionalEventsText, (float) (cell.getLeft() + additionalTextLeft), (float) (cell.getTop() + additionalTextBottom), this.eventPaint);
                return;
            }
        }
    }

    private void drawEventsInModeShapeAndText(Canvas canvas, CalendarDayCell cell) {

        int width = cell.getWidth();
        int height = cell.getHeight();

        int padding = cell.getPaddingRight() / 4;
        int offset = cell.getPaddingRight();

        Rect drawTextRect = new Rect();
        if (cell.getText() != null) {
            String text = cell.getText();
            cell.getTextPaint().getTextBounds(text, 0, text.length(), drawTextRect);
        }

        int spacingForDateVertical = width < height ? drawTextRect.height() + cell.getPaddingBottom() : 0;
        int spacingForDateHorizontal = width > height ? drawTextRect.width() + cell.getPaddingLeft() : 0;

        Rect drawSurface = new Rect(
                offset,
                (offset + spacingForDateVertical),
                (width - offset - spacingForDateHorizontal),
                (height - offset));

        int currentVerticalOffset = padding;

        String dummyText = "Dummy text";
        this.eventPaint.getTextBounds(dummyText, 0, dummyText.length(), drawTextRect);
        int shapeSize = drawTextRect.height() + padding;

        int remainingEventsCount = cell.getEvents().size();

        for (Event event : cell.getEvents()) {
            int color = event.getEventColor();
            this.eventPaint.setColor(color);

            String eventTitle = event.getTitle();
            if(eventTitle == null) {
                eventTitle = "";
            }

            this.eventPaint.getTextBounds(eventTitle, 0, eventTitle.length(), drawTextRect);

            if (canDrawEvent(shapeSize, drawSurface.height(), currentVerticalOffset, padding, remainingEventsCount)) {
                if (event.isAllDay()) {
                    eventTitle = getClampedEventTitle(eventTitle, drawSurface.width() - 3 * padding);
                    this.drawTextWithBackground(canvas, eventTitle, (cell.getLeft() + drawSurface.left), (cell.getTop() + drawSurface.top + currentVerticalOffset), padding / 2, Color.WHITE, color, drawSurface.width(), shapeSize, drawTextRect);
                    currentVerticalOffset += shapeSize + padding;
                } else {
                    eventTitle = getClampedEventTitle(eventTitle, drawSurface.width() - shapeSize - 3 * padding);
                    int textColor = Color.BLACK;
                    this.drawTextWithShape(canvas, eventTitle, (cell.getLeft() + drawSurface.left), (cell.getTop() + drawSurface.top + currentVerticalOffset), padding, textColor, color, shapeSize, drawTextRect);
                    currentVerticalOffset += shapeSize + padding;
                }
                remainingEventsCount--;
            } else {
                this.eventPaint.setColor(Color.GRAY);
                String additionalEventsText = String.format("+%d", remainingEventsCount);
                int additionalTextLeft = drawSurface.left;
                int additionalTextBottom = drawSurface.bottom;
                canvas.drawText(additionalEventsText, (float) (cell.getLeft() + additionalTextLeft), (float) (cell.getTop() + additionalTextBottom), this.eventPaint);
                return;
            }
        }
    }

    private void drawEventsInModeShape(Canvas canvas, CalendarDayCell cell) {

        int width = cell.getWidth();
        boolean hasDrawnAllDayEvent = false;

        int shapeWidth = width / 5;
        int offset = (int) (cell.getPaddingRight() * .75);

        double totalSpace = cell.getHeight() - 2 * offset;

        Long dateStart = cell.getDate();

        List<Event> eventList = cell.getEvents();
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event event2) {
                return (event.getStartDate() < event2.getStartDate()) ? -1 : (event.getStartDate() > event2.getStartDate()) ? 1 : 0;
            }
        });

        int endOfLastShape = 0;
        for (Event currentEvent : eventList) {
            int color = currentEvent.getEventColor();
            this.eventPaint.setColor(color);

            if (currentEvent.isAllDay()) {
                if (hasDrawnAllDayEvent) {
                    this.eventPaint.setColor(Color.BLACK);
                }
                canvas.drawRect((float) (cell.getLeft() + (offset / 2)), (cell.getTop() + (offset / 2)), (cell.getLeft() + (width - offset / 2)), (cell.getTop() + (offset)), this.eventPaint);
                hasDrawnAllDayEvent = true;
            } else {
                float shapeRelativeStart = (currentEvent.getStartDate() - dateStart) / (float) TICKS_IN_A_DAY;
                if (shapeRelativeStart < 0) {
                    shapeRelativeStart = 0;
                }
                float shapeRelativeEnd = (currentEvent.getEndDate() - dateStart) / (float) TICKS_IN_A_DAY;
                if (shapeRelativeEnd > 1) {
                    shapeRelativeEnd = 1;
                }
                int shapeStart = (int) (shapeRelativeStart * totalSpace);
                int shapeEnd = (int) (shapeRelativeEnd * totalSpace);
                if(shapeEnd == shapeStart) {
                    shapeEnd++;
                }
                if (shapeStart > endOfLastShape) {
                    // In this case there is no conjunction with the previous event so we simply render it with its color.
                    canvas.drawRect((float) (cell.getLeft() + (offset / 2)), (float) (cell.getTop() + offset + shapeStart), (float) (cell.getLeft() + offset / 2 + shapeWidth), (float) (cell.getTop() + offset + shapeEnd), this.eventPaint);
                    endOfLastShape = shapeEnd;
                } else if (shapeEnd > endOfLastShape) {
                    // In this case the previous event ends after the current starts but before it finishes.
                    // We render the common part in Black and the rest in the event's color.
                    this.eventPaint.setColor(Color.BLACK);
                    canvas.drawRect((float) (cell.getLeft() + (offset / 2)), (float) (cell.getTop() + (offset + shapeStart)), (float) (cell.getLeft() + (offset / 2 + shapeWidth)), (float) (cell.getTop() + (offset + endOfLastShape)), this.eventPaint);
                    this.eventPaint.setColor(color);
                    canvas.drawRect((float) (cell.getLeft() + (offset / 2)), (float) (cell.getTop() + (offset + endOfLastShape)), (float) (cell.getLeft() + (offset / 2 + shapeWidth)), (float) (cell.getTop() + (offset + shapeEnd)), this.eventPaint);
                    endOfLastShape = shapeEnd;
                } else {
                    // In this case the current event ends before the previous, so we render the whole event in Black.
                    this.eventPaint.setColor(Color.BLACK);
                    canvas.drawRect((float) (cell.getLeft() + (offset / 2)), (float) (cell.getTop() + (offset + shapeStart)), (float) (cell.getLeft() + (offset / 2 + shapeWidth)), (float) (cell.getTop() + (offset + shapeEnd)), this.eventPaint);
                }
            }
        }
    }

    private void drawTextWithShape(Canvas canvas, String text, int left, int top, int padding, int foreground, int shapeColor, int shapeSize, Rect drawRect) {
        this.eventPaint.setColor(shapeColor);
        if (shapeSize > 0) {
            canvas.drawRect((float) left, (float) top, (float) (left + shapeSize), (float) (top + shapeSize), this.eventPaint);
        }
        this.eventPaint.setColor(foreground);
        canvas.drawText(text, (float) (left - drawRect.left + padding + shapeSize), (float) (top - drawRect.top + padding), this.eventPaint);
    }

    private void drawTextWithBackground(Canvas canvas, String text, int left, int top, int padding, int foreground, int background, int backgroundWidth, int shapeSize, Rect drawRect) {
        this.eventPaint.setColor(background);
        double rectRight = backgroundWidth != 0 ? backgroundWidth + left : drawRect.width() + left + 4 * padding;
        canvas.drawRect((float) left, (float) top, (float) rectRight, (float) (top + shapeSize), this.eventPaint);

        this.eventPaint.setColor(foreground);
        canvas.drawText(text, (float) (left - drawRect.left + 2 * padding), (float) (top - drawRect.top + padding), this.eventPaint);
    }

    private boolean canDrawEvent(int height, int drawSurfaceHeight, int offset, int padding, int remainingEventsCount) {
        if (remainingEventsCount > 1) {
            return offset + 2 * height + padding < drawSurfaceHeight;
        }
        return offset + height < drawSurfaceHeight;
    }

    private String getClampedEventTitle(String originalString, int availableSpace) {
        int lastVisibleChar = originalString.length();
        float textWidth = this.eventPaint.measureText(originalString, 0, lastVisibleChar);
        while (textWidth > availableSpace && lastVisibleChar > 0) {
            lastVisibleChar--;
            textWidth = this.eventPaint.measureText(originalString, 0, lastVisibleChar);
        }

        if (lastVisibleChar < originalString.length() - 1) {
            return String.format("%s...", originalString.substring(0, lastVisibleChar));
        }
        return originalString;
    }
}
