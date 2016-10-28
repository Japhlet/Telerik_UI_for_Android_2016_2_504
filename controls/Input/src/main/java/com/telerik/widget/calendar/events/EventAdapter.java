package com.telerik.widget.calendar.events;

import com.telerik.widget.calendar.CalendarTools;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.events.read.GenericResultCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Default adapter for events displayed by {@link com.telerik.widget.calendar.RadCalendarView}.
 */
public class EventAdapter {

    protected final RadCalendarView owner;
    private List<Event> events;
    protected Calendar calendar;
    private EventRenderer renderer;

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.EventAdapter} class.
     *
     * @param owner the calendar instance owning this adapter.
     */
    public EventAdapter(RadCalendarView owner) {
        this(owner, null);
    }

    /**
     * Creates a new instance of the {@link com.telerik.widget.calendar.events.EventAdapter} class.
     *
     * @param owner  the calendar instance owning this adapter.
     * @param events list of events for this adapter
     */
    public EventAdapter(RadCalendarView owner, List<Event> events) {
        if (owner == null)
            throw new NullPointerException("owner");

        this.owner = owner;
        this.calendar = owner.getCalendar();
        this.events = events;
        this.renderer = new EventRenderer(owner.getContext());
    }

    /**
     * Used to collect events information for the given collection of dates.
     *
     * @param dates    the dates for which events will be collected.
     * @param callback the callback for returning the events.
     */
    public void requestEventsForDates(List<Long> dates, GenericResultCallback<HashMap<Long, List<Event>>> callback) {
        HashMap<Long, List<Event>> result = new HashMap<Long, List<Event>>();
        for (Long date : dates) {
            result.put(date, this.getEventsForDate(date));
        }

        callback.onResult(result);
    }

    /**
     * Gets a list of events that should be displayed for the provided date.
     *
     * @param date the date of the events that we need to display
     * @return list of event for the provided date
     */
    public List<Event> getEventsForDate(long date) {
        if (this.events == null) {
            return null;
        }

        long dateStart = CalendarTools.getDateStart(date);
        calendar.setTimeInMillis(dateStart);
        calendar.add(Calendar.DATE, 1);
        long dateEnd = calendar.getTimeInMillis();

        List<Event> eventsForDate = new ArrayList<Event>();
        for (Event event : this.events) {
            if (event == null) {
                continue;
            }

            if (eventShouldBeVisible(event, dateStart, dateEnd)) {
                eventsForDate.add(event);
            }
        }

        return eventsForDate.size() > 0 ? eventsForDate : null;
    }

    /**
     * Adds an event to the collection of elements. Deprecated - use {@link #setEvents(java.util.List)} instead.
     *
     * @param event the event to be added.
     * @deprecated
     */
    public void addEvent(Event event) {
        this.events.add(event);
        this.owner.notifyDataChanged();
    }

    /**
     * Gets the list of all events handled by this adapter.
     *
     * @return list of all events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of all events handled by this adapter.
     *
     * @param events the list of all events
     */
    public void setEvents(List<Event> events) {
        this.events = events;
        this.owner.notifyDataChanged();
    }

    /**
     * Gets the current {@link com.telerik.widget.calendar.events.EventRenderer}
     * which is responsible for the drawing of the events.
     *
     * @return the current event renderer
     */
    public EventRenderer getRenderer() {
        return renderer;
    }

    /**
     * Sets an {@link com.telerik.widget.calendar.events.EventRenderer}
     * which will be responsible for the drawing of the events.
     *
     * @param renderer the new event renderer
     */
    public void setRenderer(EventRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Determines whether an event should be displayed for a given date duration.
     *
     * @param event the event to be considered.
     * @param dateStart the start of the date.
     * @param dateEnd the end of the date.
     * @return <code>true</code> if the event should be displayed, <code>false</code> otherwise.
     */
    protected boolean eventShouldBeVisible(Event event, long dateStart, long dateEnd) {
        if(dateStart < event.getEndDate() && dateEnd > event.getStartDate()) {
            return true;
        }
        if(event.getStartDate() == event.getEndDate() && event.getStartDate() == dateStart) {
            return true;
        }
        return false;
    }
}
