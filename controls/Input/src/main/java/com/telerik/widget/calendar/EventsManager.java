package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.telerik.widget.calendar.events.Event;

import java.util.Calendar;
import java.util.List;

public abstract class EventsManager {

    CalendarDayCell currentExpandedCell;

    protected final RadCalendarView owner;

    protected Calendar calendar = Calendar.getInstance();
    protected EventInfo[] eventInfos;
    protected EventsListView listView;
    private ArrayAdapter<EventInfo> adapter;

    public class EventsListView extends ListView {

        public EventsListView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.clipRect(0, 0, getWidth(), getHeight());
            super.onDraw(canvas);
        }
    }

    public void setAdapter(ArrayAdapter<EventInfo> adapter) {
        this.adapter = adapter;
        this.owner.hideEvents(null);
    }

    public ArrayAdapter<EventInfo> getAdapter() {
        return this.adapter;
    }

    EventsManager(RadCalendarView owner) {
        this.owner = owner;
        this.listView = new EventsListView(owner.getContext());
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        this.listView.setOnItemLongClickListener(listener);
    }

    abstract int showEvents(CalendarDayCell calendarDayCell);

    abstract int hideEvents();

    protected void prepareEventsInfo(List<Event> events) {
        this.eventInfos = new EventInfo[events.size()];

        if(getAdapter() != null) {
            getAdapter().clear();
        }

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            EventInfo eventInfo = new EventInfo();
            eventInfo.color = event.getEventColor();
            eventInfo.title = event.getTitle();
            eventInfo.originalEvent = event;
            calendar.setTimeInMillis(event.getStartDate());
            eventInfo.startTime = event.getStartDate();
            eventInfo.startTimeFormatted = getCalendarHour();
            calendar.setTimeInMillis(event.getEndDate());
            eventInfo.endTime = event.getEndDate();
            eventInfo.endTimeFormatted = getCalendarHour();
            eventInfo.allDay = event.isAllDay();

            eventInfos[i] = eventInfo;

            if(getAdapter() != null) {
                getAdapter().add(eventInfo);
            }
        }
    }

    private String getCalendarHour() {
        return String.format("%d:%02d %s", calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.AM_PM) == 1 ? "PM" : "AM");
    }

    void showByAmount(int amount) {
    }

    void onShown() {
    }

    void hideByAmount(int amount) {
    }

    void onHided() {
    }

    public class EventInfo {
        int color;
        boolean allDay;

        Event originalEvent;

        String title;
        String startTimeFormatted;
        String endTimeFormatted;

        long startTime;
        long endTime;

        public String title() {
            return title;
        }

        public Event originalEvent() {
            return originalEvent;
        }

        public String startTimeFormatted() {
            return startTimeFormatted;
        }

        public String endTimeFormatted() {
            return endTimeFormatted;
        }

        public long startTime() {
            return startTime;
        }

        public long endTime() {
            return endTime;
        }

        @Override
        public String toString() {
            return title();
        }
    }
}
