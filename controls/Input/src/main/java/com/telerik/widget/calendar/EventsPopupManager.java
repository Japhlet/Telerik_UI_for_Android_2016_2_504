package com.telerik.widget.calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.telerik.android.common.Util;

/**
 * Created by ajekov on 4/29/2015.
 */
class EventsPopupManager extends EventsManager {
    private FrameLayout popLayout;
    private PopupWindow popupWindow;

    EventsPopupManager(RadCalendarView owner) {
        super(owner);
        int oneDp = (int) Util.getDimen(TypedValue.COMPLEX_UNIT_DIP, 1) * 5;

        this.popLayout = new FrameLayout(owner.getContext());
        popLayout.setBackgroundDrawable(owner.getResources().getDrawable(R.drawable.events_popup_bg));
        this.popLayout.setPadding(oneDp, oneDp, oneDp, oneDp);
        this.popLayout.addView(listView);
        this.popupWindow = new PopupWindow(popLayout);
    }

    int showEvents(CalendarDayCell calendarDayCell) {
        if (currentExpandedCell == calendarDayCell) {
            hideEvents();
            return 0;
        }

        prepareEventsInfo(calendarDayCell.getEvents());

        if(getAdapter() != null) {
            listView.setAdapter(getAdapter());
        } else {
            listView.setAdapter(new EventPopupAdapter(owner.getContext(), R.layout.popup_event, eventInfos));
        }

        listView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        listView.setBackgroundColor(owner.getAdapter().getPopupEventsWindowBackgroundColor());

        if (!popupWindow.isShowing())
            popupWindow.showAtLocation(this.owner, Gravity.NO_GRAVITY, 0, 0);

        popupWindow.update(calendarDayCell.virtualLeft(), calendarDayCell.virtualBottom() + (calendarDayCell.getWidth() >> 2) + owner.datesHolder.getTop(), getWidestView(listView.getAdapter()) + popLayout.getPaddingLeft() + popLayout.getPaddingRight(), (int) (((((listView.getMeasuredHeight() * 1.01)) * Math.min(4, eventInfos.length))) + popLayout.getPaddingTop() + popLayout.getPaddingBottom()));

        this.currentExpandedCell = calendarDayCell;

        return 0;
    }

    int hideEvents() {
        popupWindow.dismiss();
        currentExpandedCell = null;

        return 0;
    }

    public class EventPopupAdapter extends ArrayAdapter<EventInfo> {

        private LayoutInflater layoutInflater;

        public EventPopupAdapter(Context context, int resource, EventInfo[] objects) {
            super(context, resource, objects);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.popup_event, parent, false);
                textView = (TextView) convertView.findViewById(R.id.popup_event_title);
                textView.setTypeface(null, Typeface.BOLD);
                textView.setTextSize(owner.getAdapter().style.popupEventTitleTextSize);
                textView = (TextView) convertView.findViewById(R.id.popup_event_time);
                textView.setTextSize(owner.getAdapter().style.popupEventTimeTextSize);
            }

            EventInfo eventInfo = getItem(position);
            textView = (TextView) convertView.findViewById(R.id.popup_event_title);
            textView.setTextColor(eventInfo.color);
            textView.setText(eventInfo.title);

            textView = (TextView) convertView.findViewById(R.id.popup_event_time);

            if (eventInfo.allDay) {
                textView.setText("");
            } else {
                textView.setText(String.format("%s - %s", eventInfo.startTimeFormatted, eventInfo.endTimeFormatted));
                textView.setTextColor(eventInfo.color);
            }

            return convertView;
        }
    }

    private int getWidestView(Adapter adapter) {
        int maxWidth = 0;
        View view = null;
        FrameLayout fakeLayout = new FrameLayout(owner.getContext());
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            view = adapter.getView(i, view, fakeLayout);
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int width = view.getMeasuredWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        return (int) (maxWidth * 1.05);
    }
}
