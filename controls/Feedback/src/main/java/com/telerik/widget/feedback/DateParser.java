package com.telerik.widget.feedback;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ginev on 26/05/2014.
 */
public class DateParser {

    public static String getDateFromJSONString(Context context, String string){
        SimpleDateFormat format = new SimpleDateFormat(context.getResources().getString(R.string.date_time_format_string));
        String result = new String();
        try {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date parsedDate = format.parse(string);
            SimpleDateFormat printFormat = new SimpleDateFormat(context.getResources().getString(R.string.date_time_label_format_string));
            printFormat.setTimeZone(TimeZone.getDefault());
            return printFormat.format(parsedDate);
        } catch (Exception e) {
            Log.e("Feedback", "Error parsing comment date.", e);
        }
        return result;
    }
}
