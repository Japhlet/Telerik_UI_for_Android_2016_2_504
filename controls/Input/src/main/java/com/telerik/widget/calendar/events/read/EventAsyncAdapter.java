package com.telerik.widget.calendar.events.read;

import android.os.AsyncTask;

import com.telerik.widget.calendar.CalendarTools;
import com.telerik.widget.calendar.RadCalendarView;
import com.telerik.widget.calendar.events.Event;
import com.telerik.widget.calendar.events.EventAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter for handling async event loading.
 */
public abstract class EventAsyncAdapter extends EventAdapter {

    protected static final java.util.Calendar WORK_CALENDAR_ONE = java.util.Calendar.getInstance();
    protected static final java.util.Calendar WORK_CALENDAR_TWO = java.util.Calendar.getInstance();

    protected HashMap<Long, List<Event>> baseDates;
    protected LoadEventTask eventLoadTask;
    protected boolean eventLoadTaskRunning;
    protected List<Long> requestedDates;
    protected List<Event> eventsRed;

    /**
     * Creates a new instance of the {@link EventAsyncAdapter} class for handling async event loading.
     *
     * @param owner the calendar instance owning this adapter.
     */
    public EventAsyncAdapter(RadCalendarView owner) {
        this(owner, null);
    }

    /**
     * Creates a new instance of the {@link EventAsyncAdapter} class for handling async event loading.
     *
     * @param owner  the calendar instance owning this adapter.
     * @param events the local events.
     */
    public EventAsyncAdapter(RadCalendarView owner, List<Event> events) {
        super(owner, events);

        this.eventsRed = new ArrayList<Event>();
    }

    /**
     * Used to read the events from the calendar provider. Thread safe operation - the events will be populated once they are available.
     */
    public void readEventsAsync() {
        new RequestTask(new ArrayList<Event>()).execute();
    }

    /**
     * Called asynchronously to collect events data from an external source. Thread safe, update of the calendar will be performed automatically.
     *
     * @param events collection to store the events.
     */
    protected abstract void loadEventsAsync(List<Event> events);

    private class RequestTask extends AsyncTask<Void, Void, Void> {

        private final List<Event> events;

        public RequestTask(List<Event> events) {
            this.events = events;
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadEventsAsync(events);

            return null;
        }

        @Override
        protected void onPostExecute(Void events) {
            onEventsResult(this.events);
        }
    }

    @Override
    public void requestEventsForDates(final List<Long> dates, final GenericResultCallback<HashMap<Long, List<Event>>> callback) {
        this.requestedDates = dates;

        super.requestEventsForDates(dates, new GenericResultCallback<HashMap<Long, List<Event>>>() {
            @Override
            public void onResult(final HashMap<Long, List<Event>> forDatesBase) {
                baseDates = forDatesBase;
                if (eventLoadTaskRunning) {
                    eventLoadTask.cancel(true);
                } else {
                    startNewLoadTask(callback);
                }
            }
        });

        /*super.requestEventsForDates(dates, new GenericResultCallback<HashMap<Long, List<Event>>>() {
            @Override
            public void onResult(final HashMap<Long, List<Event>> forDatesBase) {
                callback.onResult(forDatesBase);
            }
        });*/
    }

    private void startNewLoadTask(final GenericResultCallback<HashMap<Long, List<Event>>> callback) {
        eventLoadTaskRunning = true;
        eventLoadTask = new LoadEventTask(new GenericResultCallback<HashMap<Long, List<Event>>>() {
            @Override
            public void onResult(HashMap<Long, List<Event>> forDates) {
                for (long date : baseDates.keySet()) {
                    if (baseDates.get(date) != null) {
                        if (forDates.get(date) == null)
                            forDates.put(date, baseDates.get(date));
                        else
                            forDates.get(date).addAll(baseDates.get(date));
                    }
                }
                callback.onResult(forDates);
                eventLoadTaskRunning = false;
            }
        },
                new Runnable() {
                    @Override
                    public void run() {
                        startNewLoadTask(callback);
                    }
                },
                eventsRed, requestedDates);

        eventLoadTask.execute();
    }

    /**
     * Called when the events are successfully red from the specified calendars.
     *
     * @param events the set of events.
     */
    protected void onEventsResult(List<Event> events) {
        this.eventsRed.clear();
        if (events != null) {
            this.eventsRed.addAll(events);
        }

        this.owner.notifyDataChanged();
    }

    /**
     * Used to determine whether the event should recur on the passed date.
     *
     * @param event the event to be evaluated.
     * @param date  the date on which the event is evaluated.
     * @return <code>true</code> if the event must recur, <code>false</code> otherwise.
     */
    protected boolean eventShouldRecur(RecurringEvent event, long date) {
        long eventDateStart = CalendarTools.getDateStart(event.getStartDate());
        if (date < eventDateStart ||
                (event.repeatUntilDate() != 0 && date > CalendarTools.getDateStart(event.repeatUntilDate()))) {
            return false;
        }

        WORK_CALENDAR_ONE.setTimeInMillis(eventDateStart);
        WORK_CALENDAR_TWO.setTimeInMillis(date);

        if (Math.abs(WORK_CALENDAR_ONE.get(java.util.Calendar.YEAR) - WORK_CALENDAR_TWO.get(java.util.Calendar.YEAR)) > 3) {
            return false;
        }

        if (event.isByDay()) {
            if (!event.byDay(WORK_CALENDAR_TWO.get(java.util.Calendar.DAY_OF_WEEK))) {
                return false;
            }
        }

        if (event.isModified()) {
            return handleModifiedRecurrence(event);
        } else {
            return handleUnmodifiedRecurrence(event);
        }
    }

    private boolean handleModifiedRecurrence(RecurringEvent event) {
        return false;
    }

    private boolean handleUnmodifiedRecurrence(RecurringEvent event) {
        int incrementValue;
        switch (event.frequency()) {
            case Daily:
                incrementValue = java.util.Calendar.DAY_OF_YEAR;
                break;
            case Weekly:
                incrementValue = java.util.Calendar.WEEK_OF_YEAR;
                break;
            case Monthly:
                incrementValue = java.util.Calendar.MONTH;
                break;
            case Yearly:
                incrementValue = java.util.Calendar.YEAR;
                break;
            default:
                return false;
        }

        int count = 0;
        while (true) {
            if (WORK_CALENDAR_ONE.get(java.util.Calendar.YEAR) == WORK_CALENDAR_TWO.get(java.util.Calendar.YEAR) &&
                    WORK_CALENDAR_ONE.get(java.util.Calendar.DAY_OF_YEAR) == WORK_CALENDAR_TWO.get(java.util.Calendar.DAY_OF_YEAR)) {
                return true;
            }

            WORK_CALENDAR_ONE.add(incrementValue, event.interval());

            if (WORK_CALENDAR_ONE.getTimeInMillis() > WORK_CALENDAR_TWO.getTimeInMillis())
                return false;

            if (event.isByDay() && !event.byDay(WORK_CALENDAR_ONE.get(java.util.Calendar.DAY_OF_WEEK)))
                continue;

            if ((event.count() > 0 && ++count >= event.count()))
                return false;
        }
    }

    protected class LoadEventTask extends AsyncTask<Void, Void, HashMap<Long, List<Event>>> {

        private final GenericResultCallback<HashMap<Long, List<Event>>> callback;
        private final List<Event> eventsRed;
        private final Runnable cancelCallback;
        private final List<Long> dates;
        private java.util.Calendar calendar = java.util.Calendar.getInstance();

        /**
         * Creates a new instance of the {@link LoadEventTask} used for async loading of events.
         *
         * @param callback the callback to be executed when the loading of events is done.
         */
        public LoadEventTask(GenericResultCallback<HashMap<Long, List<Event>>> callback, Runnable onCanceledCallback, List<Event> sourceEvents, List<Long> dates) {
            this.callback = callback;
            this.eventsRed = sourceEvents;
            this.cancelCallback = onCanceledCallback;
            this.dates = dates;
        }

        @Override
        protected HashMap<Long, List<Event>> doInBackground(Void... params) {
            HashMap<Long, List<Event>> eventsForDates = new HashMap<Long, List<Event>>();

            for (int i = 0; i < dates.size(); i++) {
                if (isCancelled())
                    break;

                eventsForDates.put(dates.get(i), getEventsForDate(dates.get(i), this.eventsRed));
            }

            return eventsForDates;
        }

        private List<Event> getEventsForDate(long date, List<Event> eventsRead) {
            List<Event> events = new ArrayList<Event>();

            if (eventsRead == null)
                return events;

            long dateStart = CalendarTools.getDateStart(date);
            this.calendar.setTimeInMillis(dateStart);
            this.calendar.add(java.util.Calendar.DATE, 1);
            long dateEnd = this.calendar.getTimeInMillis();

            for (int i = 0; i < eventsRead.size(); i++) {
                if (isCancelled())
                    break;

                Event event = eventsRead.get(i);
                if (event == null) {
                    continue;
                }

                if (eventShouldBeVisible(event, dateStart, dateEnd)) {
                    events.add(event);
                } else if (event instanceof RecurringEvent) {
                    if (eventShouldRecur((RecurringEvent) event, dateStart)) {
                        events.add(event);
                    }
                }
            }

            return events;
        }

        @Override
        protected void onPostExecute(HashMap<Long, List<Event>> result) {
            this.callback.onResult(result);
        }

        @Override
        protected void onCancelled(HashMap<Long, List<Event>> result) {
            cancelCallback.run();
        }
    }
}
