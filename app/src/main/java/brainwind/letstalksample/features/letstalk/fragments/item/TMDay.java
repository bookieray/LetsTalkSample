package brainwind.letstalksample.features.letstalk.fragments.item;

import android.content.Context;

import com.google.firebase.database.Query;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;

public class TMDay
{


    long estimatedServerTimeMs=0;
    private String month="";
    private String day="";
    private String last_comment_id="";
    private Integer year=new LocalDateTime(estimatedServerTimeMs).getYear();
    private ArrayList<String> days=new ArrayList<String>();
    private HashMap<String,String> cmids=new HashMap<String,String>();

    public TMDay(String month, String day) {
        this.month = month;
        this.day = day;
    }

    public TMDay(Context context) {

        Memory memory=new Memory(context);
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

        if(jnm.isEmpty()==false)
        {

            estimatedServerTimeMs=Long.parseLong(jnm);
            year=new LocalDateTime(estimatedServerTimeMs).getYear();

        }

    }


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getLast_comment_id() {
        return last_comment_id;
    }

    public void setLast_comment_id(String last_comment_id) {
        this.last_comment_id = last_comment_id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public void addDay(String day) {
        this.days.add(day);
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public HashMap<String, String> getCmids() {
        return cmids;
    }


    public void addLastCommentID(String s, String hj) {

        this.cmids.put(s,hj);

    }
}

