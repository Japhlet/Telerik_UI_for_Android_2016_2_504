package com.telerik.widget.chart.engine.axes.categorical;

import com.telerik.android.common.DateTimeExtensions;
import com.telerik.widget.chart.engine.axes.AxisTickModel;
import com.telerik.widget.chart.engine.axes.AxisUpdateContext;
import com.telerik.widget.chart.engine.axes.common.DateTimeComponent;
import com.telerik.widget.chart.engine.axes.common.DateTimeHelper;
import com.telerik.widget.chart.engine.dataPoints.DataPoint;
import com.telerik.widget.chart.engine.elementTree.ChartAreaInvalidateFlags;
import com.telerik.widget.chart.engine.propertyStore.PropertyKeys;
import com.telerik.widget.chart.engine.propertyStore.ValueExtractor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeCategoricalAxisModel extends CategoricalAxisModel implements Comparator<AxisCategory> {

    static final int DATE_TIME_COMPONENT_PROPERTY_KEY = PropertyKeys.register(DateTimeCategoricalAxisModel.class, "DateTimeComponent", ChartAreaInvalidateFlags.ALL);
    static final int DATE_FORMAT_PROPERTY_KEY = PropertyKeys.register(DateTimeCategoricalAxisModel.class, "DateFormat", ChartAreaInvalidateFlags.INVALIDATE_AXES);

    public static DateFormat defaultDateFormat = new SimpleDateFormat();
    private boolean validCategories = false;

    public DateTimeComponent getDateTimeComponent() {
        return this.getTypedValue(DATE_TIME_COMPONENT_PROPERTY_KEY, DateTimeComponent.TIME_IN_MILLIS);
    }

    public void setDateTimeComponent(DateTimeComponent value) {
        this.setValue(DATE_TIME_COMPONENT_PROPERTY_KEY, value);
    }

    public DateFormat getDateFormat() {
        return this.getTypedValue(DATE_FORMAT_PROPERTY_KEY, defaultDateFormat);
    }

    public void setDateFormat(DateFormat value) {
        this.setValue(DATE_FORMAT_PROPERTY_KEY, value);
    }

    @Override
    Object getCategoryKey(DataPoint point, Object value) {
        ValueExtractor<Calendar> date = new ValueExtractor<Calendar>();
        if (!DateTimeHelper.tryGetDateTime(value, date)) {
            this.validCategories = false;
            return super.getCategoryKey(point, value);
        }

        Calendar calendar = date.value;
        switch (this.getDateTimeComponent()) {
            case YEAR:
                return calendar.get(Calendar.YEAR);
            case QUARTER:
                return DateTimeExtensions.getQuarterOfYear(calendar);
            case MONTH:
                return calendar.get(Calendar.MONTH);
            case WEEK:
                return calendar.get(Calendar.WEEK_OF_MONTH);
            case HOUR:
                return calendar.get(Calendar.HOUR_OF_DAY);
            case MINUTE:
                return calendar.get(Calendar.MINUTE);
            case SECOND:
                return calendar.get(Calendar.SECOND);
            case MILLISECOND:
                return calendar.get(Calendar.MILLISECOND);
            case DATE:
                return String.format("%s_%s_%s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            case TIME_OF_DAY:
                return String.format("%s_%s_%s_%s", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
            case DAY:
                return calendar.get(Calendar.DAY_OF_MONTH);
            case DAY_OF_WEEK:
                return calendar.get(Calendar.DAY_OF_WEEK);
            case DAY_OF_YEAR:
                return calendar.get(Calendar.DAY_OF_YEAR);
            case TIME_IN_MILLIS:
                return calendar.getTimeInMillis();
            default:
                throw new IllegalArgumentException("Unrecognized DATE component has been specified for grouping the DateTimeCategoricalAxis.");
        }
    }

    @Override
    protected void updateCore(AxisUpdateContext context) {
        // reset the validation flag
        this.validCategories = true;

        super.updateCore(context);

        if (this.validCategories) {
            // sort the categories so that they are arranged in a chronological order
            Collections.sort(this.categories, this);
        }
    }

    @Override
    protected Object getLabelContentCore(AxisTickModel tick) {
        if (tick.virtualIndex() >= this.categories.size()) {
            return null;
        }

        Object keySource = this.categories.get(tick.virtualIndex()).keySource;
        if (keySource instanceof Calendar) {

            Calendar calendar = (Calendar) keySource;

            DateFormat format = this.getDateFormat();
            if (format == null) {
                format = defaultDateFormat;
            }

            return format.format(calendar.getTime());
        }

        if ( keySource instanceof String) {

            String[] formats = new String[]{
            "dd/MM/yyyy",
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mmZ",
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"};

             for (String format : formats) {
                 try {
                     SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                     sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                     Date date = sdf.parse(keySource.toString());

                     if (this.getDateFormat() == null) {
                         return DateFormat.getDateInstance().format(date);
                     }

                     return this.getDateFormat().format(date);
                 } catch (java.text.ParseException ex) {
                 }
             }
        }

        return super.getLabelContentCore(tick);
    }

    @Override
    public int compare(AxisCategory axisCategory, AxisCategory axisCategory2) {
        Calendar keySource = (Calendar) axisCategory.keySource;
        Calendar keySource2 = (Calendar) axisCategory2.keySource;

        return keySource.compareTo(keySource2);
    }
}
