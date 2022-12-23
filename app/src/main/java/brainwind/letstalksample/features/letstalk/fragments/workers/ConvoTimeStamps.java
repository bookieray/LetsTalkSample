package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.CommentDate;
import brainwind.letstalksample.features.letstalk.fragments.item.TMDay;

public class ConvoTimeStamps extends CommentWorker
{

    RecyclerView commment_list;
    CommentAdapter commentAdapter=null;
    public ConvoTimeStamps(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);
        this.currentTopicForConvo=currentTopicFragment;
        this.setCurrentTopicFragment(currentTopicFragment);
        setConversation_id(conversation_id);
        Log.i("ConvoTimeStamps","Constructor "+(currentTopicFragment!=null)+" "+(getCurrentTopicFragment()!=null));
        //PrepareViews();
        commentAdapter=new CommentAdapter(currentTopicFragment);
    }

    @Override
    public void PrepareViews() {
        super.PrepareViews();

        /*commment_list=(RecyclerView) getRootView().findViewById(R.id.comment_list);
        if(comment_filter==null)
        {
            comment_filter=(Spinner) getRootView().findViewById(R.id.comment_filter);
        }

        Log.i("ConvoTimeStamps","PrepareViews "+(this.currentTopicFragment!=null)+" "+(this.getCurrentTopicFragment()!=null));
        if(commment_list!=null)
        {
            Log.i("showHeadComment","commment_list!=null");
            if(commment_list.getLayoutManager()==null)
            {
                commment_list.setLayoutManager(new LinearLayoutManager(this.getCurrentTopicFragment().getActivity()));
            }

            commment_list.setAdapter(commentAdapter);


        }
        else
        {
            Log.i("showHeadComment","commment_list==null");
        }
        */

    }

    HashMap<String, ArrayList<Comment>> ident_list=new HashMap<String, ArrayList<Comment>>();
    public void getTimestampsForHeadComment(CommentWorker commentWorker)
    {


        if(commment_list==null)
        {
            commment_list=(RecyclerView) getRootView().findViewById(R.id.comment_list);
        }
        if(marker_filter_area==null)
        {
            marker_filter_area=(RelativeLayout) getRootView().findViewById(R.id.marker_filter_area);
        }
        if(comment_filter==null)
        {
            comment_filter=(Spinner) getRootView().findViewById(R.id.comment_filter);
        }
        if(commment_list.getLayoutManager()==null)
        {
            commment_list.setLayoutManager(new LinearLayoutManager(this.getCurrentTopicFragment().getActivity()));
        }


        Comment headcomment=getHead_comment();
        Log.i("ksldass","headcomment!=null "+(headcomment!=null));
        if(headcomment!=null)
        {

            final String conversation_id = getCurrentTopicFragment().getArguments().getString(CONVO_ID);
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String qtxt=conversation_id+"_"+head_comment.getComment_id();
            if(comment_filter.getSelectedItemPosition()==0)
            {
                //All
                qtxt=head_comment.getComment_id()+"_"+Comment.AGREES;
            }
            else if(comment_filter.getSelectedItemPosition()==1)
            {
                //Agree
                qtxt=head_comment.getComment_id()+"_"+Comment.AGREES;
            }
            else if(comment_filter.getSelectedItemPosition()==2)
            {
                //Disagree
                qtxt=head_comment.getComment_id()+"_"+Comment.DISAGREES;
            }
            else if(comment_filter.getSelectedItemPosition()==3)
            {
                //Question
                qtxt=head_comment.getComment_id()+"_"+Comment.QUESTION;
            }
            else if(comment_filter.getSelectedItemPosition()==4)
            {
                //Answers
                qtxt=head_comment.getComment_id()+"_"+Comment.ANSWER;
            }


            if(ident_list.containsKey(qtxt))
            {
                ArrayList<Comment> timestamps=ident_list.get(qtxt);
                CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                if(commentCommunications!=null)
                {
                    commentCommunications.TimeStampsForHeadComment(getHead_comment(),timestamps);
                }
            }
            else
            {
                //marker_filter_area.setVisibility(View.VISIBLE);
                //comment_filter.setVisibility(View.VISIBLE);
                //comment_filter.setEnabled(false);
                Log.i("ksldass",(getCurrentTopicFragment()!=null)+" "+(currentTopicFragment!=null));
                Log.i("getTmestmpsFrHeadCmment","qtxt="+qtxt);
                mDatabase.child(qtxt)
                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {

                                Log.i("getTmestmpsFrHeadCmment","isSuccessful="+(task.isSuccessful())+" "+(task.getResult().getValue()!=null));
                                if(!task.isSuccessful())
                                {

                                    CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                                    if(commentCommunications!=null)
                                    {
                                        commentCommunications.onFailureToFetchTimestamps(getHead_comment(),task.getException().getMessage());
                                    }

                                }
                                else if(task.getResult().getValue()==null)
                                {
                                    CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                                    if(commentCommunications!=null)
                                    {
                                        ArrayList<Comment> timetsmaps=new ArrayList<Comment>();
                                        commentCommunications.TimeStampsForHeadComment(getHead_comment(),timetsmaps);
                                    }
                                }
                                else
                                {

                                    String qtxt=conversation_id+"_"+head_comment.getComment_id();
                                    if(comment_filter.getSelectedItemPosition()==0)
                                    {
                                        //All
                                        qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.AGREES;
                                    }
                                    else if(comment_filter.getSelectedItemPosition()==1)
                                    {
                                        //Agree
                                        qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.AGREES;
                                    }
                                    else if(comment_filter.getSelectedItemPosition()==2)
                                    {
                                        //Disagree
                                        qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.DISAGREES;
                                    }
                                    else if(comment_filter.getSelectedItemPosition()==3)
                                    {
                                        //Question
                                        qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.QUESTION;
                                    }
                                    else if(comment_filter.getSelectedItemPosition()==4)
                                    {
                                        //Answers
                                        qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.ANSWER;
                                    }
                                    ScanJsonTimeStamps(commentWorker,qtxt,task,currentTopicForConvo,head_comment,comment_filter);

                                }

                            }
                        });

                Log.i("ksldass",qtxt);
            }



        }


    }

    //The years the conversation has been carried out
    List<Integer> years=new ArrayList<Integer>();
    //The months the conversation has been carried out and indexed by the year-month string to retrieve
    //and aggregate into lists indexed by the key
    HashMap<Integer,ArrayList<Integer>> year_months=new HashMap<Integer,ArrayList<Integer>>();
    //The days the conversation across months, years and indexed by the year-month-day
    HashMap<String,ArrayList<Integer>> year_month_days=new HashMap<String,ArrayList<Integer>>();
    //The Last Comment ID linked to combination of the day, month and year
    HashMap<String,String> year_month_day_last_comment_id=new HashMap<String,String>();
    //The list of the objects that singularly contain day, month and year
    ArrayList<TMDay> tmDays=new ArrayList<TMDay>();
    //tracks the timestamps already added to the master list of the comments
    HashMap<String, CommentDate> timestamps_dates=new HashMap<String,CommentDate>();
    boolean assigned_scroll_listener=false;
    public int last_select_filter_option=-1;
    HashMap<String,Boolean> process_comments=new HashMap<String, Boolean>();
    public HashMap<String,ArrayList<Comment>> timestamp_comments=new HashMap<String,ArrayList<Comment>>();
    HashMap<String,Boolean> no_more_comments=new HashMap<String,Boolean>();
    public int last_loaded_position=0;
    public int loading_timestamp_position=last_loaded_position;

    private void ScanJsonTimeStamps(CommentWorker commentWorker,String qtxt,Task<DataSnapshot> task,
                                    Fragment currentTopicForConvo,
                                    Comment head_comment,
                                    Spinner comment_type)
    {


        this.process_comments.clear();
        this.head_comment= head_comment;
        this.comment_type_spinner=comment_type;
        JSONObject jsonObject= null;
        //clear the lists to allow for new indexes
        //according to the filter selection, ALL, AGREE, DISAGREE, QUESTION
        years.clear();
        year_months.clear();
        year_month_days.clear();
        year_month_day_last_comment_id.clear();
        timestamp_comments.clear();

        tmDays.clear();
        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicForConvo);
        }
        if(commentAdapter!=null)
        {

            //on the adapter
            commentAdapter.commentListUnderHeadComment.clear();
            commentAdapter.timestamps_comments.clear();
            commentAdapter.markers.clear();
            commentAdapter.markers_positions.clear();
            commentAdapter.no_more_comments_next.clear();
            commentAdapter.no_more_comments_previous.clear();
            commentAdapter.started_loading_older_coments.clear();
            commentAdapter.started_loading_next_coments.clear();
            //internally
            no_more_comments.clear();
            timestamp_comments.clear();
            last_loaded_position=0;
            loading_timestamp_position=last_loaded_position;
            timestamps_dates.clear();



        }

        try
        {

            if(task.getResult().getValue()!=null)
            {
                jsonObject = new JSONObject(task.getResult().getValue().toString());
                //Iterator<String> keys = jsonObject.keys();
                JSONArray years_list=jsonObject.names();
                Log.i("getCommentsFrHedComent","years_count="+years_list.length());

                //loop through the years
                for(int i=0;i<years_list.length();i++)
                {

                    //get each year
                    Integer year=Integer.parseInt(years_list.getString(i));
                    Log.i("getCommentsFrHedComent","year="+year);
                    years.add(year);

                    //find the months linked to each year
                    JSONObject jsonObject1=jsonObject.getJSONObject(years_list.getString(i));
                    Iterator<String> months_names = jsonObject1.keys();

                    //loop through the months
                    while(months_names.hasNext())
                    {

                        //get each month
                        String month_name = months_names.next();
                        Integer month=Integer.parseInt(month_name);
                        Log.i("getCommentsFrHedComent","month="+month_name);
                        //get if already index a list linked to the year
                        if(year_months.containsKey(year))
                        {
                            ArrayList<Integer> months=year_months.get(year);
                            months.add(month);
                            year_months.put(year,months);
                        }
                        else
                        {
                            //create a list linked to the year
                            ArrayList<Integer> months=new ArrayList<>();
                            months.add(month);
                            year_months.put(year,months);
                        }

                        //find the days linked to the month
                        JSONObject jsonObject2=jsonObject1.getJSONObject(month_name);
                        Iterator<String> days = jsonObject2.keys();

                        while(days.hasNext())
                        {

                            String day_name = days.next();
                            Integer day=Integer.parseInt(day_name);
                            Log.i("getCommentsFrHedComent","month="+month_name+" day="+day);

                            //find the days linked to the year and month
                            if(year_month_days.containsKey(year+"-"+month))
                            {
                                ArrayList<Integer> indexed_days=year_month_days.get(year+"-"+month);
                                indexed_days.add(day);
                                year_month_days.put(year+"-"+month,indexed_days);
                            }
                            else
                            {
                                ArrayList<Integer> indexed_days=new ArrayList<Integer>();
                                indexed_days.add(day);
                                year_month_days.put(year+"-"+month,indexed_days);

                            }

                            String value=jsonObject2.getString(day_name);
                            year_month_day_last_comment_id.put(year+"-"+month+"-"+day,value);

                        }



                    }

                }

                Log.i("getCommentsFrHedComent","starting to create tmDays "+years.size());
                //reorder the lists
                //sort out years
                //descending order
                Collections.sort(years);
                Collections.reverse(years);
                //reorder the months and days
                for(Integer year:years)
                {

                    //find the list of months that belong to this year
                    if(year_months.containsKey(year))
                    {
                        //reorder the months that belong to the year
                        ArrayList<Integer> months=year_months.get(year);
                        Collections.sort(months);
                        Collections.reverse(months);
                        //reorder the days belong to each month in this year
                        for(Integer month:months)
                        {
                            //find the days belong to each month in this year
                            if(year_month_days.containsKey(year+"-"+month))
                            {

                                //sort out days in that month and year
                                //descending order
                                ArrayList<Integer> days=year_month_days.get(year+"-"+month);
                                Collections.sort(days);
                                Collections.reverse(days);
                                //loop through the days and with month and year in previous
                                HashMap<String,String> jkms=new HashMap<String,String>();
                                for(Integer day:days)
                                {

                                    TMDay tmDay=new TMDay(month+"","");
                                    tmDay.setYear(year);
                                    tmDay.setMonth(month+"");
                                    tmDay.setDay(day+"");
                                    tmDay.addDay(day+"");

                                    if(jkms
                                            .containsKey(tmDay.getYear()
                                                    +"-"+tmDay.getMonth()
                                                    +"-"+tmDay.getDay())==false)
                                    {

                                        String hj=jkms
                                                .get(year+"-"+month+"-"+day);
                                        if(year_month_day_last_comment_id.containsKey(hj))
                                        {
                                            String last_comment_id=year_month_day_last_comment_id.get(hj);
                                            tmDay.setLast_comment_id(last_comment_id);
                                        }
                                        jkms.put(hj,tmDay.getLast_comment_id());
                                        tmDays.add(tmDay);

                                    }


                                }

                            }
                        }

                    }

                }
                //looping through the list of the objects
                // that singularly contain day, month and year
                int starting= tmDays.size()-1;
                //if there are too many days of the conversation
                //load only 100 days worth of timestamps
                //and delete the older than 100 days and then from
                if(starting>99)
                {
                    starting=99;
                    deleteTooOldDaysOfConvoFromRegistry();
                }
                //looping through the list of the objects
                // that singularly contain day, month and year
                //looping from the most previuous first and added to the adapter list

                Log.i("getCommentsFrHedComent","starting="+starting+" "+tmDays.size());
                for(int i=starting;i>=0;i--)
                {

                    TMDay tmDay2=tmDays.get(i);
                    String timestamp=tmDay2.getDay()+"-"+tmDay2.getMonth()
                            +"-"+tmDay2.getYear();
                    String timestamp2=tmDay2.getYear()+"-"+tmDay2.getMonth()
                            +"-"+tmDay2.getDay();
                    Log.i("timstar",timestamp2);

                    if(commentAdapter.started_loading_older_coments.containsKey(timestamp2))
                    {
                        commentAdapter.started_loading_older_coments.remove(timestamp2);
                    }
                    //is it already added to the comment list adapter to prevent duplicates
                    if(timestamps_dates.containsKey(timestamp)==false)
                    {

                        Comment comment=new Comment();
                        comment.setTimestamp(tmDay2);


                        commentAdapter.commentListUnderHeadComment.add(comment);
                        int pl=commentAdapter.commentListUnderHeadComment.size()-1;
                        Log.i("adsxefs","adding "+comment.getTimestamp()+" "+pl);
                        CommentDate commentDate=new CommentDate(comment);
                        timestamps_dates.put(timestamp,commentDate);
                    }

                    if(i==0)
                    {

                    }

                }

                ArrayList<Comment> timetsmaps=new ArrayList<Comment>();
                if(tmDays.size()>0)
                {
                    for(int i=tmDays.size()-1;i>=0;i--)
                    {
                        TMDay tmDay=tmDays.get(i);
                        Comment comment=new Comment();
                        comment.setTimestamp(tmDay);
                        timetsmaps.add(comment);

                    }

                    ident_list.put(qtxt,timetsmaps);

                }

                CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                if(commentCommunications!=null)
                {
                    commentCommunications.TimeStampsForHeadComment(getHead_comment(),timetsmaps);
                }
            }
            else
            {
                CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                if(commentCommunications!=null)
                {
                    ArrayList<Comment> timetsmaps=new ArrayList<Comment>();
                    commentCommunications.TimeStampsForHeadComment(getHead_comment(),timetsmaps);
                }
            }


        }
        catch (Exception exception)
        {
            Log.i("getCommentsFrHedComent","e="+exception.getMessage());
            exception.printStackTrace();
        }



    }

    private void deleteTimeStampFromRegistry(Comment comment)
    {

        if(head_comment!=null)
        {

            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
            final String conversation_id = currentTopicForConvo.getArguments()
                    .getString(CONVO_ID);
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.AGREES)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.DISAGREES)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.QUESTION)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.ANSWER)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();

        }

    }

    private void LoadLastOrStartTimestamp(Spinner comment_type)
    {



    }

    private void deleteTooOldDaysOfConvoFromRegistry()
    {



    }





}
