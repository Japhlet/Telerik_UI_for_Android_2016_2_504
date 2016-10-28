package com.telerik.widget.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajekov on 4/29/2015.
 */
class EventsInlineManager extends EventsManager {

    private int currentExplosionHeight;
    private int originalExplodedCellTop;
    private int originalExplodedCellBottom;
    private List<CalendarElement> elementsShiftingUp = new ArrayList<CalendarElement>();
    private List<CalendarElement> elementsShiftingDown = new ArrayList<CalendarElement>();
    private int explosionTopBorder;
    private int explosionBottomBorder;

    EventsInlineManager(RadCalendarView owner) {
        super(owner);
    }

    @Override
    int showEvents(CalendarDayCell calendarDayCell) {
        this.currentExpandedCell = calendarDayCell;

        int cellTop = calendarDayCell.virtualTop();

        this.originalExplodedCellTop = calendarDayCell.virtualTop();
        this.originalExplodedCellBottom = calendarDayCell.virtualBottom();
        this.explosionTopBorder = this.owner.scrollManager.currentFragment.rows().get(0).virtualTop();
        this.explosionBottomBorder = this.owner.scrollManager.currentFragment.rows().get(this.owner.scrollManager.currentFragment.rows().size() - 1).virtualTop();

        if (owner.scrollMode != ScrollMode.Overlap && this.owner.scrollMode != ScrollMode.Stack) {
            loadRowsForExplosion(cellTop, this.owner.scrollManager.previousFragment.rows());
        }

        loadRowsForExplosion(cellTop, this.owner.scrollManager.currentFragment.rows());

        if (this.owner.scrollMode != ScrollMode.Overlap && this.owner.scrollMode != ScrollMode.Stack) {
            loadRowsForExplosion(cellTop, this.owner.scrollManager.nextFragment.rows());
        }

        this.owner.scrollManager.markFragmentsDirty();

        prepareEventsInfo(calendarDayCell.getEvents());
        if(getAdapter() != null) {
            listView.setAdapter(getAdapter());
        } else {
            listView.setAdapter(new EventInlineAdapter(owner.getContext(), R.layout.inline_event, eventInfos));
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(owner.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
        listView.measure(widthMeasureSpec, View.MeasureSpec.UNSPECIFIED);
        listView.setBackgroundColor(owner.getAdapter().getInlineEventsBackgroundColor());

        this.currentExplosionHeight = Math.min(owner.initialRowHeight * 4, listView.getMeasuredHeight() * eventInfos.length);

        if (owner.scrollMode == ScrollMode.Stack) {
            this.owner.scrollManager.previousFragment.visibility = ElementVisibility.Invisible;
            this.owner.scrollManager.previousFragment.dirty = true;
        }

        return currentExplosionHeight;
    }

    int hideEvents() {
        this.owner.scrollManager.markFragmentsDirty();
        this.owner.removeView(listView);

        return currentExplosionHeight;
    }

    private void loadRowsForExplosion(int cellTop, List<CalendarRow> elements) {
        int rowTop;
        for (CalendarElement row : elements) {
            rowTop = row.virtualTop();

            if (rowTop <= cellTop) {
                elementsShiftingUp.add(row);
            } else if (rowTop > cellTop) {
                elementsShiftingDown.add(row);
            }
        }
    }

    void showByAmount(int animationStep) {
        int stepUp = animationStep >> 1;
        int stepDown = stepUp;
        int extra;

        if ((animationStep & 1) == 1) {
            ++stepDown;
        }

        if (originalExplodedCellBottom > this.owner.scrollManager.getBottom() - (owner.initialRowHeight << 1)) {
            stepUp += stepDown;
            stepDown = 0;
        } else {
            if (elementsShiftingDown.get(0).virtualTop() + stepDown > explosionBottomBorder) {
                extra = (elementsShiftingDown.get(0).virtualTop() + stepDown) - explosionBottomBorder;
                stepDown -= extra;
                stepUp += extra;
            }

            if (elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualTop() - stepUp < explosionTopBorder) {
                extra = (explosionTopBorder) - (elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualTop() - stepUp);
                stepUp -= extra;
                stepDown += extra;
            }
        }

        if (stepDown != 0) {
            for (CalendarElement element : elementsShiftingDown) {
                element.arrange(element.getLeft(), element.getTop() + stepDown, element.getRight(), element.getBottom() + stepDown);
            }
        }

        if (stepUp != 0) {
            for (CalendarElement element : elementsShiftingUp) {
                element.arrange(element.getLeft(), element.getTop() - stepUp, element.getRight(), element.getBottom() - stepUp);
            }
        }

        this.owner.scrollManager.markFragmentsDirty();
    }

    void hideByAmount(int animationStep) {
        int stepUp = Math.abs(animationStep) >> 1;
        int stepDown = stepUp;

        if ((animationStep & 1) == 1) {
            ++stepUp;
        }

        if (elementsShiftingDown.size() > 0) {
            if (elementsShiftingDown.get(0).virtualTop() - stepUp < originalExplodedCellBottom) {
                int extra = originalExplodedCellBottom - (elementsShiftingDown.get(0).virtualTop() - stepUp);
                stepUp -= extra;
                stepDown += extra;
            }
        } else {
            stepDown += stepUp;
            stepUp = 0;
        }

        if (elementsShiftingUp.size() > 0 && elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualTop() + stepDown > originalExplodedCellTop) {
            int extra = elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualTop() + stepDown - originalExplodedCellTop;
            stepDown -= extra;
            stepUp += extra;
        }

        if (stepDown > 0) {
            for (CalendarElement element : elementsShiftingUp) {
                element.arrange(element.getLeft(), element.getTop() + stepDown, element.getRight(), element.getBottom() + stepDown);
            }
        }

        if (stepUp > 0) {
            for (CalendarElement element : elementsShiftingDown) {
                element.arrange(element.getLeft(), element.getTop() - stepUp, element.getRight(), element.getBottom() - stepUp);
            }
        }

        this.owner.scrollManager.markFragmentsDirty();
    }

    void onHided() {
        this.currentExpandedCell = null;
        this.elementsShiftingDown.clear();
        this.elementsShiftingUp.clear();

        if (owner.scrollMode == ScrollMode.Stack) {
            this.owner.scrollManager.previousFragment.visibility = ElementVisibility.Visible;
            this.owner.scrollManager.previousFragment.dirty = true;
        }

        this.owner.invalidateArrange();
        this.owner.scrollManager.markFragmentsDirty();
        this.owner.invalidate();
    }

    void onShown() {
        this.owner.addView(listView);
        listView.layout(owner.scrollManager.getLeft(), elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualBottom() + owner.datesHolder.getTop(), owner.scrollManager.getRight(), elementsShiftingUp.get(elementsShiftingUp.size() - 1).virtualBottom() + currentExplosionHeight + owner.datesHolder.getTop());
        Animation animation = AnimationUtils.loadAnimation(owner.getContext(), android.R.anim.fade_in);
        animation.setDuration(200);
        listView.startAnimation(animation);
    }

    protected class EventInlineAdapter extends ArrayAdapter<EventInfo> {

        private LayoutInflater layoutInflater;

        public EventInlineAdapter(Context context, int resource, EventInfo[] objects) {
            super(context, resource, objects);
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.inline_event, parent, false);
                textView = (TextView) convertView.findViewById(R.id.inline_event_title);
                textView.setTextSize(owner.getAdapter().style.inlineEventTitleTextSize);

                textView = (TextView) convertView.findViewById(R.id.inline_event_start);
                textView.setTextSize(owner.getAdapter().style.inlineEventTimeStartTextSize);
                textView.setTextColor(owner.getAdapter().style.inlineEventTimeStartTextColor);

                textView = (TextView) convertView.findViewById(R.id.inline_event_end);
                textView.setTextSize(owner.getAdapter().style.inlineEventTimeEndTextSize);
                textView.setTextColor(owner.getAdapter().style.inlineEventTimeEndTextColor);
            }

            EventInfo eventInfo = getItem(position);
            textView = (TextView) convertView.findViewById(R.id.inline_event_title);
            textView.setTextColor(eventInfo.color);
            textView.setText(eventInfo.title);

            if (eventInfo.allDay) {
                textView = (TextView) convertView.findViewById(R.id.inline_event_start);
                textView.setVisibility(View.GONE);

                textView = (TextView) convertView.findViewById(R.id.inline_event_end);
                textView.setVisibility(View.GONE);
            } else {
                textView = (TextView) convertView.findViewById(R.id.inline_event_start);
                textView.setVisibility(View.VISIBLE);
                textView.setText(eventInfo.startTimeFormatted);

                textView = (TextView) convertView.findViewById(R.id.inline_event_end);
                textView.setVisibility(View.VISIBLE);
                textView.setText(eventInfo.endTimeFormatted);
            }

            return convertView;
        }
    }
}
