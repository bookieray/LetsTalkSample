package brainwind.letstalksample.data.utilities;

import android.util.Log;

import org.joda.time.LocalDateTime;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class TimeUtilities
{

    public static String getTimeLabel(LocalDateTime today, Comment comment)
    {

        Log.i("getTimeLabel",
                " comment.year="+comment.getYear()
                +" comment.month="+comment.getMonth()
                +" comment.day="+comment.getDay()
        );

        //perform a check to correct the comment
        if(new LocalDateTime(comment.getCreatedDate()).getMonthOfYear()
                !=comment.getMonth())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getMonthOfYear());
        }
        if(new LocalDateTime(comment.getCreatedDate()).getYear()
                !=comment.getYear())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getYear());
        }
        if(new LocalDateTime(comment.getCreatedDate()).getDayOfMonth()
                !=comment.getDay())
        {
            LocalDateTime localDateTime
                    =new LocalDateTime(comment.getCreatedDate());
            comment.setMonth(localDateTime.getDayOfMonth());
        }
        Log.i("getTimeLabel","today.month="+today.getMonthOfYear()
                        +" comment.month="+comment.getMonth()
                +" comment.day="+comment.getDay()
                +" today.day="+today.getDayOfMonth()
                );
        String timeLabel="";

        //testing if it is the same day
        if(today.getDayOfMonth()==comment.getDay()
                &today.getMonthOfYear()==comment.getMonth()
                &today.getYear()==comment.getYear())
        {
            timeLabel="Today";
        }
        //testing if it is the same year and same month
        else if(today.getMonthOfYear()==comment.getMonth()
                &today.getYear()==comment.getYear()&today.getDayOfMonth()-comment.getDay()==1
        )
        {

            LocalDateTime localDateTime=new LocalDateTime(comment.getCreatedDate());
            timeLabel="Yesterday "+localDateTime.dayOfWeek().getAsShortText();
        }
        //testing if it is the same year and same month
        else if(today.getMonthOfYear()==comment.getMonth()
                &today.getYear()==comment.getYear()&today.getDayOfMonth()-comment.getDay()>1
        )
        {


            int num_of_days=today.getDayOfMonth()-comment.getDay();
            int comment_week=new LocalDateTime()
                    .withDayOfMonth(comment.getDay())
                    .withMonthOfYear(comment.getMonth())
                    .withYear(comment.getYear()).getWeekOfWeekyear();
            LocalDateTime localDateTime=new LocalDateTime()
                    .withYear(comment.getYear())
                    .withMonthOfYear(comment.getMonth())
                    .withDayOfMonth(comment.getDay());
            int todays_week=today.getWeekOfWeekyear();
            int num_weeks=todays_week-comment_week;
            if(num_weeks>1)
            {
                timeLabel="This month "+localDateTime.dayOfWeek().getAsShortText()
                        +" "+localDateTime.getDayOfMonth();
            }
            else
            {
                timeLabel="This month "+num_of_days+" days ago";
            }

        }
        else
        {

            LocalDateTime localDateTime=new LocalDateTime(comment.getCreatedDate());
            Log.i("ydhjal","day="+localDateTime.getDayOfMonth()
                    +" month="+localDateTime.getMonthOfYear()+" year="+localDateTime.getYear());

            timeLabel=localDateTime.monthOfYear().getAsShortText()
                    +" "+localDateTime.getDayOfMonth()
                    +" "+localDateTime.getYear();
        }

        return timeLabel;

    }


}
