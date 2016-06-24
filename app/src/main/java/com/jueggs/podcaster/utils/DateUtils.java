package com.jueggs.podcaster.utils;

import android.content.Context;
import com.jueggs.podcaster.App;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.data.PodcastContract;
import com.jueggs.utils.DateTimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.jueggs.podcaster.data.PodcastContract.*;
import static com.jueggs.utils.DateTimeUtils.*;

public class DateUtils
{
    public static final String TIME_PATTERN = "h:mm a";
    public static final String FULLDATE_PATTERN_EN = "MM-dd-yy h:mm a";
    public static final String FULLDATE_PATTERN_DE = "dd.MM.yy h:mm a";

    public static String createDateString(Context context, String dateString)
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        DateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN);
        DateFormat fullDateFormat = new SimpleDateFormat(App.LANGUAGE.equals(LANG_EN) ? FULLDATE_PATTERN_EN : FULLDATE_PATTERN_DE);

        try
        {
            Date date = dateFormat.parse(dateString);
            String timeString = timeFormat.format(date);

            if (isToday(date))
                return String.format("%s %s", context.getString(R.string.today), timeString);
            else if (isYesterday(date))
                return String.format("%s %s", context.getString(R.string.yesterday), timeString);
            else
                return fullDateFormat.format(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
