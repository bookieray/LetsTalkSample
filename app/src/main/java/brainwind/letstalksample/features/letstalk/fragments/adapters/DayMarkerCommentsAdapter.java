package brainwind.letstalksample.features.letstalk.fragments.adapters;

import static android.view.View.GONE;

import android.view.View;

import androidx.fragment.app.Fragment;

import java.util.HashMap;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class DayMarkerCommentsAdapter extends BookmarkCommentsAdapter
{

    //Morning is the period from sunrise to noon.5 am to 12 pm (noon)
    //Early Morning 5 to 8 am
    public static String EARLY_MORNING="es1";
    //Late morning 11 am to 12pm
    public static String LATE_MORNING="es2";
    public static String NOON="es3";
    //Afternoon 12 pm to 5 pm
    //Early afternoon   1 to 3pm
    public static String EARLY_AFTERNOON="es4";
    //Late afternoon    4 to 5pm
    public static String LATE_AFTERNOON="es5";
    //Evening     5 pm to 9 pm
    //Early evening   5 to 7 pm
    public static String EARLY_EVENING="es6";
    //Night 9 pm to 4 am
    public static String NIGHT="es7";

    public HashMap<String, String> markers=new HashMap<String,String>();
    public HashMap<String, String> markersx=new HashMap<String,String>();
    public HashMap<Integer, String> markers_positions=new HashMap<Integer,String>();

    public DayMarkerCommentsAdapter(Fragment current_fragment) {
        super(current_fragment);
    }

    /*public void categorizeMorningNoonAfternoonNight(CommentAdapter.CommentHolder holder, Comment comment, int position)
    {

        org.joda.time.LocalDateTime localDateTime=
                new org.joda.time.LocalDateTime(comment.getCreatedDate());


        if(comment.getComment().trim().isEmpty())
        {
            holder.part_of_day.setVisibility(GONE);
        }
        else
        {

            if(markers.containsKey(comment.getComment_id()))
            {
                holder.part_of_day.setVisibility(View.VISIBLE);
                String jkl=markers.get(comment.getComment_id());
                if(jkl.equals(EARLY_MORNING))
                {
                    holder.part_of_day.setText("Early Morning");
                }
                if(jkl.equals(LATE_MORNING))
                {
                    holder.part_of_day.setText("Late Morning");
                }
                if(jkl.equals(NOON))
                {
                    holder.part_of_day.setText("Noon");
                }
                if(jkl.equals(EARLY_AFTERNOON))
                {
                    holder.part_of_day.setText("Early Afternoon");
                }
                if(jkl.equals(LATE_AFTERNOON))
                {
                    holder.part_of_day.setText("Late Afternoon");
                }
                if(jkl.equals(EARLY_EVENING))
                {
                    holder.part_of_day.setText("Early Evening");
                }
                if(jkl.equals(NIGHT))
                {
                    holder.part_of_day.setText("At Night");
                }

            }
            else
            {
                holder.part_of_day.setVisibility(View.GONE);
            }

        }


    }

     */




}
