package com.telerik.widget.calendar;

/**
 * Represents are range of consecutive dates.
 */
public class DateRange {
    private long start;
    private long end;

    /**
     * Creates a new range of consecutive dates.
     *
     * @param start start date
     * @param end   end date
     * @throws java.lang.IllegalArgumentException If the start date is after the end date
     */
    public DateRange(long start, long end) {
        if (start > end) {
            throw new IllegalArgumentException("start of the range should be before its end");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the start date for this date range.
     *
     * @return the start of the range
     */
    public long getStart() {
        return this.start;
    }

    /**
     * Sets a new start date for the date range.
     * If the new start date is after the end date, an exception is thrown.
     *
     * @param start the new start date for the date range
     * @throws java.lang.IllegalArgumentException If the start date is after the end date
     */
    public void setStart(long start) {
        if (this.start != start) {
            if (start > this.end) {
                throw new IllegalArgumentException("start of the range should be before its end");
            }
            this.start = start;
        }
    }

    /**
     * Returns the end date for this date range.
     *
     * @return the end of the range
     */
    public long getEnd() {
        return this.end;
    }

    /**
     * Sets a new end date for the date range.
     * If the new end date is before the start date, an exception is thrown.
     *
     * @param end the new end date for the date range
     * @throws java.lang.IllegalArgumentException If the end date is before the start date
     */
    public void setEnd(long end) {
        if (this.end != end) {
            if (this.start > end) {
                throw new IllegalArgumentException("start of the range should be before its end");
            }
            this.end = end;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        if (!(o instanceof DateRange))
            return false;

        DateRange second = (DateRange) o;
        return start == second.start &&
                end == second.end;
    }

    @Override
    public int hashCode() {
        String result = String.format("%d.%d", start, end);
        return result.hashCode();
    }
}
