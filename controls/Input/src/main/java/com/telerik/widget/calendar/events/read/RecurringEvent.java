package com.telerik.widget.calendar.events.read;

import com.telerik.widget.calendar.events.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Recurring event holds all the information about a recurring rule bound to a recurring event.
 */
public class RecurringEvent extends Event {

    private static final String SUNDAY = "su";
    private static final String MONDAY = "mo";
    private static final String TUESDAY = "tu";
    private static final String WEDNESDAY = "we";
    private static final String THURSDAY = "th";
    private static final String FRIDAY = "fr";
    private static final String SATURDAY = "sa";

    private static final char TIME_SECOND = 's';
    private static final char TIME_MINUTE = 'm';
    private static final char TIME_HOUR = 'h';
    private static final char TIME_DAY = 'd';
    private static final char TIME_WEEK = 'w';

    private static final String FREQ_SECONDLY = "secondly";
    private static final String FREQ_MINUTELY = "minutely";
    private static final String FREQ_HOURLY = "hourly";
    private static final String FREQ_DAILY = "daily";
    private static final String FREQ_WEEKLY = "weekly";
    private static final String FREQ_MONTHLY = "monthly";
    private static final String FREQ_YEARLY = "yearly";

    private static final Calendar WORK_CALENDAR = Calendar.getInstance();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    private static final String UNTIL_RULE_NAME = "until";
    private static final String FREQ_RULE_NAME = "freq";
    private static final String WEEK_START_RULE_NAME = "wkst";
    private static final String BYDAY_RULE_NAME = "byday";
    private static final String INTERVAL_RULE_NAME = "interval";
    private static final String COUNT_RULE_NAME = "count";
    private static final String BYMONTH_RULE_NAME = "bymonth";
    private static final String BYSETPOS_RULE_NAME = "bysetpos";
    private static final String BYSECOND_RULE_NAME = "bysecond";
    private static final String BYMONTHDAY_RULE_NAME = "bymonthday";
    private static final String BYYEARDAY_RULE_NAME = "byyearday";
    private static final String BYWEEKNO_RULE_NAME = "byweekno";
    private static final String BYMINUTE_RULE_NAME = "byminute";
    private static final String BYHOUR_RULE_NAME = "byhour";

    private final String rrule;
    private Frequency frequency;
    private int interval = 1;
    private int byDay;
    private int[] byDayModifiers;
    private int byMonth;
    private int[] bySecond;
    private int[] byMinute;
    private int[] byHour;
    private int[] byMonthDay;
    private int[] byYearDay;
    private int[] byWeekNo;
    private int[] bySetPos;
    private int weekStartDay = Calendar.MONDAY;

    private int count;
    private long repeatUntilDate;

    /**
     * Creates a new instance of the {@link RecurringEvent} class for storing information about the recurrence rule of a recurring event.
     *
     * @param title     the title of the event.
     * @param startDate the start date of the event.
     * @param duration  duration of the event expressed as the [ISO 8601] basic format for the duration of time.
     * @param rrule     the rrule holding the recurrence information.
     */
    public RecurringEvent(String title, long startDate, String duration, String rrule) {
        this(title, startDate, calculateEndDate(startDate, duration), rrule);
    }

    /**
     * Creates a new instance of the {@link RecurringEvent} class for storing information about the recurrence rule of a recurring event.
     *
     * @param title     the title of the event.
     * @param startDate the start date of the event.
     * @param endDate   the end date of the event.
     * @param rrule     the rrule holding the recurrence information.
     */
    public RecurringEvent(String title, long startDate, long endDate, String rrule) {
        super(title, startDate, endDate);

        this.rrule = rrule;

        this.byDayModifiers = new int[7];
        buildRecurrenceRules(rrule);
    }

    private static long calculateEndDate(long startDate, String duration) {
        WORK_CALENDAR.setTimeInMillis(startDate);

        StringBuilder stringBuilder = new StringBuilder();
        int time;

        for (Character character : duration.toLowerCase().toCharArray()) {
            if (Character.isDigit(character)) {
                stringBuilder.append(character);
            } else {
                if (stringBuilder.length() == 0 || character.equals('t') || character.equals('p'))
                    continue;
                time = Integer.parseInt(stringBuilder.toString());
                switch (character) {
                    case TIME_SECOND:
                        WORK_CALENDAR.add(Calendar.SECOND, time);
                        break;
                    case TIME_MINUTE:
                        WORK_CALENDAR.add(Calendar.MINUTE, time);
                        break;
                    case TIME_HOUR:
                        WORK_CALENDAR.add(Calendar.HOUR, time);
                        break;
                    case TIME_DAY:
                        WORK_CALENDAR.add(Calendar.DAY_OF_YEAR, time);
                        break;
                    case TIME_WEEK:
                        WORK_CALENDAR.add(Calendar.WEEK_OF_YEAR, time);
                        break;
                }

                stringBuilder.setLength(0);
            }
        }

        return WORK_CALENDAR.getTimeInMillis();
    }

    /**
     * Gets the corresponding day modifier for the given day. Valid values are {@link java.util.Calendar#SUNDAY}, {@link java.util.Calendar#MONDAY} etc. Default value is 0.
     *
     * @param day the day for which to check in the modifiers.
     * @return the modifier for this day.
     */
    public int byDayModifier(int day) {
        return this.byDayModifiers[day - 1];
    }

    /**
     * The collection of seconds for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of seconds.
     */
    public int[] bySecond() {
        return this.bySecond;
    }

    /**
     * The collection of minutes for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of minutes.
     */
    public int[] byMinute() {
        return this.byMinute;
    }

    /**
     * The collection of hours for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of hours.
     */
    public int[] byHour() {
        return this.byHour;
    }

    /**
     * The collection of month days for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of month days.
     */
    public int[] byMonthDay() {
        return this.byMonthDay;
    }

    /**
     * The collection of year days for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of year days.
     */
    public int[] byYearDay() {
        return this.byYearDay;
    }

    /**
     * The collection of week numbers for the recurrence rule. Default value is <code>null</code>.
     *
     * @return the current collection of week numbers.
     */
    public int[] byWeekNo() {
        return this.byWeekNo;
    }

    /**
     * Gets the recurrence rule used to build the current event instance tokens.
     *
     * @return the current recurrence rule.
     */
    public String rrule() {
        return this.rrule;
    }

    /**
     * Used to check if a given day is present in the by day set. The day values are considered regarding the constants of the Calendar class:
     * {@link java.util.Calendar#SUNDAY}, {@link java.util.Calendar#MONDAY} etc.
     *
     * @param day the day to be checked.
     * @return <code>true</code> if the day is present in the set, <code>false</code> otherwise.
     */
    public boolean byDay(int day) {
        return (this.byDay & (1 << day)) > 0;
    }

    /**
     * Used to check if a given month is present in the by month set. The day values are considered regarding the constants of the Calendar class:
     * {@link java.util.Calendar#JANUARY}, {@link java.util.Calendar#FEBRUARY} etc.
     *
     * @param month the month to be checked.
     * @return <code>true</code> if the month is present in the set, <code>false</code> otherwise.
     */
    public boolean byMonth(int month) {
        return (this.byMonth & (1 << month)) > 0;
    }

    /**
     * Gets the repeat until date if present. Default value is 0.
     *
     * @return the current repeat until date.
     */
    public long repeatUntilDate() {
        return this.repeatUntilDate;
    }

    /**
     * Gets the count used in repeating the event. Default value is 0.
     *
     * @return the current count.
     */
    public int count() {
        return this.count;
    }

    /**
     * Gets the start of the week for this event. Default value is {@link java.util.Calendar#MONDAY}
     *
     * @return the current week start.
     */
    public int weekStartDay() {
        return this.weekStartDay;
    }

    /**
     * Gets the interval in which the recurrence occurs. Default value is 1.
     *
     * @return the current interval.
     */
    public int interval() {
        return this.interval;
    }

    /**
     * Gets a value stating whether the rule has defined bySecond values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isBySecond() {
        return this.bySecond != null;
    }

    /**
     * Gets a value stating whether the rule has defined byMinute values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isByMinute() {
        return this.bySecond != null;
    }

    /**
     * Gets a value stating whether the rule has defined byHour values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isByHour() {
        return this.bySecond != null;
    }

    /**
     * Gets a value stating whether the rule has defined byMonthDay values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isByMonthDay() {
        return this.byMonthDay != null;
    }

    /**
     * Gets a value stating whether the rule has defined byYearDay values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isByYearDay() {
        return this.byYearDay != null;
    }

    /**
     * Gets a value stating whether the rule has defined byWeekNo values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isByWeekNo() {
        return this.byWeekNo != null;
    }

    /**
     * Gets a value stating whether the rule has defined bySetPos values.
     *
     * @return <code>true</code> if there are defined values, <code>false</code> otherwise.
     */
    public boolean isBySetPos() {
        return this.bySetPos != null;
    }

    /**
     * Gets a value determining whether the rule is stated as byday rule.
     *
     * @return <code>true</code> if the rule is by day, <code>false</code> otherwise.
     */
    public boolean isByDay() {
        return this.byDay > 0;
    }

    /**
     * Gets a value determining whether the rule is stated as byMonth rule.
     *
     * @return <code>true</code> if the rule is by month, <code>false</code> otherwise.
     */
    public boolean isByMonth() {
        return this.byMonth > 0;
    }

    /**
     * Gets a value determining whether the rule is being modified using additional nth ocurrence rule as in -2MO or in BYSETPOS=-1.
     *
     * @return <code>true</code> if the rrule is modified, <code>false</code> otherwise.
     */
    public boolean isModified() {
        return isByDayModified() || isBySetPos();
    }

    /**
     * Gets a value determining whether the BYDAY rule is modified such as in -1MO.
     *
     * @return <code>true</code> if the BYDAY has been modified, <code>false</code> otherwise.
     */
    public boolean isByDayModified() {
        return (this.byDay & 1) > 0; // using the free bit to store whether there are additional modifiers.
    }

    /**
     * Gets the frequency at which the rule recurs.
     *
     * @return the current frequency.
     */
    public Frequency frequency() {
        return this.frequency;
    }

    /**
     * Gets the BYSETPOS values for the recurrance. Default value is <code>null</code>
     *
     * @return the current BYSETPOS values.
     */
    public int[] bySetPos() {
        return bySetPos;
    }

    /**
     * Used to build the recurrence data using the passed rrule string.
     *
     * @param rrule the string containing the recurrence rules.
     */
    protected void buildRecurrenceRules(String rrule) {
        if (rrule == null)
            return;

        String[] ruleTokens = extractTokens(rrule);

        for (String token : ruleTokens) {
            if (token.startsWith(FREQ_RULE_NAME)) {
                extractFrequency(token);
            } else if (token.startsWith(UNTIL_RULE_NAME)) {
                extractUntilDate(token);
            } else if (token.startsWith(WEEK_START_RULE_NAME)) {
                extractWeekStart(token);
            } else if (token.startsWith(BYDAY_RULE_NAME)) {
                extractByDay(token);
            } else if (token.startsWith(INTERVAL_RULE_NAME)) {
                extractInterval(token);
            } else if (token.startsWith(COUNT_RULE_NAME)) {
                extractCount(token);
            } else if (token.startsWith(BYMONTH_RULE_NAME)) {
                extractByMonth(token);
            } else if (token.startsWith(BYSETPOS_RULE_NAME)) {
                extractBySetPos(token);
            } else if (token.startsWith(BYSECOND_RULE_NAME)) {
                extractBySecond(token);
            } else if (token.startsWith(BYMINUTE_RULE_NAME)) {
                extractByMinute(token);
            } else if (token.startsWith(BYHOUR_RULE_NAME)) {
                extractByHour(token);
            } else if (token.startsWith(BYMONTHDAY_RULE_NAME)) {
                extractByMonthDay(token);
            } else if (token.startsWith(BYYEARDAY_RULE_NAME)) {
                extractByYearDay(token);
            } else if (token.startsWith(BYWEEKNO_RULE_NAME)) {
                extractByWeekNo(token);
            }
        }
    }

    /**
     * Used to extract the byHour from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByHour(String token) {
        String[] hours = extractTokenValue(token).split(",");
        this.byHour = new int[hours.length];
        fillIntArray(hours, this.byHour);
    }

    /**
     * Used to extract the byMinute from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByMinute(String token) {
        String[] minutes = extractTokenValue(token).split(",");
        this.byMinute = new int[minutes.length];
        fillIntArray(minutes, this.byMinute);
    }

    /**
     * Used to extract the byWeekNo from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByWeekNo(String token) {
        String[] weeks = extractTokenValue(token).split(",");
        this.byWeekNo = new int[weeks.length];
        fillIntArray(weeks, this.byWeekNo);
    }

    /**
     * Used to extract the byYearDay from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByYearDay(String token) {
        String[] days = extractTokenValue(token).split(",");
        this.byYearDay = new int[days.length];
        fillIntArray(days, this.byYearDay);
    }

    /**
     * Used to extract the byMonthDay from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByMonthDay(String token) {
        String[] months = extractTokenValue(token).split(",");
        this.byMonthDay = new int[months.length];
        fillIntArray(months, this.byMonthDay);
    }

    /**
     * Used to extract the bySecond from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractBySecond(String token) {
        String[] seconds = extractTokenValue(token).split(",");
        this.bySecond = new int[seconds.length];
        fillIntArray(seconds, this.bySecond);
    }

    /**
     * Used to extract the bySetPos from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractBySetPos(String token) {
        String[] positions = extractTokenValue(token).split(",");
        this.bySetPos = new int[positions.length];
        fillIntArray(positions, this.bySetPos);
    }

    /**
     * Used to extract the byMonth from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByMonth(String token) {
        String months = extractTokenValue(token);
        for (String month : months.split(",")) {
            this.byMonth |= 1 << Integer.parseInt(month);
        }
    }

    /**
     * Used to extract the count from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractCount(String token) {
        this.count = Integer.parseInt(extractTokenValue(token));
    }

    /**
     * Used to extract the interval from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractInterval(String token) {
        this.interval = Integer.parseInt(extractTokenValue(token));
    }

    /**
     * Used to extract the byDay from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractByDay(String token) {
        String[] days = extractTokenValue(token).split(",");
        int day = 0;
        for (String dayTokens : days) {
            if (dayTokens.contains(SUNDAY)) {
                day = Calendar.SUNDAY;
            } else if (dayTokens.contains(MONDAY)) {
                day = Calendar.MONDAY;
            } else if (dayTokens.contains(TUESDAY)) {
                day = Calendar.TUESDAY;
            } else if (dayTokens.contains(WEDNESDAY)) {
                day = Calendar.WEDNESDAY;
            } else if (dayTokens.contains(THURSDAY)) {
                day = Calendar.THURSDAY;
            } else if (dayTokens.contains(FRIDAY)) {
                day = Calendar.FRIDAY;
            } else if (dayTokens.contains(SATURDAY)) {
                day = Calendar.SATURDAY;
            }

            this.byDay |= 1 << day;

            if (dayTokens.length() > 2) {
                int index = dayTokens.length() - 1;
                {
                    for (int i = index; i > -1; i--) {
                        if (Character.isDigit(dayTokens.charAt(i))) {
                            index = i + 1;
                            break;
                        }
                    }

                    this.byDayModifiers[day - 1] = Integer.parseInt(dayTokens.substring(0, index));
                    this.byDay |= 1; // using the free bit to store whether the byDay has been additionally modified.
                }
            }
        }
    }

    /**
     * Used to extract the week start from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractWeekStart(String token) {
        String value = extractTokenValue(token);
        if (value.equals(SUNDAY)) {
            this.weekStartDay = Calendar.SUNDAY;

        } else if (value.equals(MONDAY)) {
            this.weekStartDay = Calendar.MONDAY;

        } else if (value.equals(TUESDAY)) {
            this.weekStartDay = Calendar.TUESDAY;

        } else if (value.equals(WEDNESDAY)) {
            this.weekStartDay = Calendar.WEDNESDAY;

        } else if (value.equals(THURSDAY)) {
            this.weekStartDay = Calendar.THURSDAY;

        } else if (value.equals(FRIDAY)) {
            this.weekStartDay = Calendar.FRIDAY;

        } else if (value.equals(SATURDAY)) {
            this.weekStartDay = Calendar.SATURDAY;
        }
    }

    /**
     * Used to extract the until date from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractUntilDate(String token) {
        try {
            this.repeatUntilDate = DATE_FORMAT.parse(extractTokenValue(token)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to extract the frequency from a rrule token.
     *
     * @param token the token from which the value will be extracted.
     */
    protected void extractFrequency(String token) {
        String freq = extractTokenValue(token);
        if (freq.equals(FREQ_SECONDLY)) {
            this.frequency = Frequency.Secondly;
        } else if (freq.equals(FREQ_MINUTELY)) {
            this.frequency = Frequency.Minutely;
        } else if (freq.equals(FREQ_HOURLY)) {
            this.frequency = Frequency.Hourly;
        } else if (freq.equals(FREQ_DAILY)) {
            this.frequency = Frequency.Daily;
        } else if (freq.equals(FREQ_WEEKLY)) {
            this.frequency = Frequency.Weekly;
        } else if (freq.equals(FREQ_MONTHLY)) {
            this.frequency = Frequency.Monthly;
        } else if (freq.equals(FREQ_YEARLY)) {
            this.frequency = Frequency.Yearly;
        }
    }

    /**
     * Extracts the tokens of a recurrence rule.
     *
     * @param rule the recurrence rule.
     * @return the tokens of the recurrence rule.
     */
    protected String[] extractTokens(String rule) {
        return rule.toLowerCase().split(";");
    }

    /**
     * Used to extract the value of a token, or the content after the '=' sign.
     *
     * @param token the token from which the value will be extracted.
     * @return the value of the token.
     */
    protected String extractTokenValue(String token) {
        return token.substring(token.indexOf("=") + 1);
    }

    private void fillIntArray(String[] values, int[] emptyArray) {
        for (int i = 0, len = values.length; i < len; i++)
            emptyArray[i] = Integer.parseInt(values[i]);
    }
}
