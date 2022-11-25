package brainwind.letstalksample.features.letstalk.fragments.adapters;

import static android.view.View.GONE;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>
{

    public String starting_timestamp="";
    public int starting_set_number=1;
    public boolean no_comments_in_convo=false;
    View rootView;
    Fragment current_fragment;
    public HashMap<String,Boolean> no_more_comments=new HashMap<String,Boolean>();
    public String loading_timestamp_cmid="";

    public CommentAdapter(Fragment current_fragment,View rootView) {
        this.current_fragment = current_fragment;
        this.rootView = rootView;
    }

    //tracks the comments linked to a specific timestamp comment
    public HashMap<String,ArrayList<Comment>> timestamps_comments=new HashMap<String,ArrayList<Comment>>();
    public List<Comment> commentListUnderHeadComment = new ArrayList<Comment>();
    //tracks the current loading position of the timestamp
    public int loading_timestamp_position=-1;

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==-1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list
                    , parent, false);
            return new CommentAdapter.CommentHolder(view);
        }
        else if (viewType == Comment.AGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agree_comment
                    , parent, false);
            return new CommentAdapter.CommentHolder(view);
        }
        else if (viewType == Comment.DISAGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disagree_comment
                    , parent, false);
            return new CommentAdapter.CommentHolder(view);
        }
        else if (viewType == Comment.QUESTION||viewType==Comment.ANSWER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_comment
                    , parent, false);
            return new CommentAdapter.CommentHolder(view);
        }
        else {

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.agree_comment
                            , parent, false);
            return new CommentAdapter.CommentHolder(view);
        }

    }

    public HashMap<String,Integer> timestamps_positions=new HashMap<String,Integer>();
    public HashMap<String,Integer> comment_position=new HashMap<String,Integer>();
    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position)
    {


        if(getItemViewType(position)!=-1)
        {
            Comment comment=commentListUnderHeadComment.get(position);
            if(comment.getCreatedDate()!=null)
            {
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                comment.getCreatedDate())
                        .toString();
                android.util.Log.i("shareDayConvo","sdadapt output="+output+" "+comment.getCreatedDate().getTime());
            }
            comment_position.put(comment.getComment_id(),position);
            categorizeMorningNoonAfternoonNight(holder,comment,position);
            holder.summary_area.setVisibility(View.VISIBLE);
            //decide and set the comment as a timestamp
            decideIdTimeStamp(comment,position);
            //now show the comment
            showBasicCommentDetails(holder, position);
            checkIfStartingTimestamp(holder,position);
        }


    }

    private void checkIfStartingTimestamp(CommentHolder holder, int position)
    {

        final int after_comment_pos=position;
        final Comment comment=commentListUnderHeadComment.get(position);
        if(starting_timestamp.isEmpty()==false)
        {

        }

        if(starting_timestamp.equals(comment.getTimestamp())
                &timestamps_comments.containsKey(comment.getTimestamp())
                &comment.getComment_id().isEmpty()==false)
        {


            Log.i("chckIfStartingTimestamp","starting_timestamp="+starting_timestamp
                    +" "+timestamps_comments
                    .containsKey(comment.getTimestamp())
                    +" starting_set_number="+starting_set_number);

            if(starting_set_number>1)
            {


                ArrayList<Comment> cmtds=timestamps_comments.get(comment.getTimestamp());
                int lefts=starting_set_number-cmtds.size();
                if(cmtds.size()>0)
                {
                    Comment first_comment=cmtds.get(0);
                    Comment last_comment=cmtds.get(cmtds.size()-1);
                    android.text.format.DateFormat df =
                            new android.text.format.DateFormat();
                    String outputx1=df.format("hh:mm:ss a",
                                    first_comment.getCreatedDate())
                            .toString();
                    String outputx2=df.format("hh:mm:ss a",
                                    last_comment.getCreatedDate())
                            .toString();
                    Log.i("iskha",outputx1+" "+outputx2+" "+starting_set_number);

                    if(comment.getComment_id().equals(last_comment.getComment_id()))
                    {
                        if(lefts>0)
                        {
                            holder.summary_area.setVisibility(View.VISIBLE);
                            if(lefts>10)
                            {
                                holder.summary_label.setText("Next 10/"+lefts+" comments");
                            }
                            else
                            {
                                if(lefts>1)
                                {
                                    holder.summary_label.setText("Next "+lefts+" comments");
                                }
                                else
                                {
                                    holder.summary_label.setText("Get last comment");
                                }
                            }


                            holder.summary_area.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Log.i("msudhka","next");
                                    if(started_loading_next_coments.size()>0)
                                    {
                                        if(comment_position.containsKey(loading_timestamp_cmid))
                                        {
                                            if(comment_position.containsKey(loading_timestamp_cmid))
                                            {
                                                int pos=comment_position.get(loading_timestamp_cmid);
                                                if(pos>-1&pos<commentListUnderHeadComment.size())
                                                {

                                                    Comment comment1=commentListUnderHeadComment
                                                            .get(loading_timestamp_position);
                                                    Memory memory=new Memory(holder.timestamp_area.getContext());
                                                    String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                                    if(jnm.isEmpty()==false)
                                                    {

                                                        long localtimeoffset=Long.parseLong(jnm);
                                                        long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                                        org.joda.time.LocalDateTime localDateTime
                                                                =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                                        String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment1);
                                                        Log.i("tinsja","timelabel="+timelabel);
                                                        Toast.makeText(holder.timestamp_area.getContext(),
                                                                "Busy with comments for "+timelabel, Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        started_loading_next_coments.put(comment.getTimestamp()
                                                ,comment.getComment_id());


                                        loading_timestamp_cmid=comment.getComment_id();
                                        holder.summary_area_card.setVisibility(GONE);
                                        holder.loading_next_comments.setVisibility(View.VISIBLE);
                                        getNextCommentsAfter(comment,after_comment_pos);
                                    }



                                }
                            });
                        }
                        else
                        {

                        }
                    }

                }



            }

        }

        if(started_loading_next_coments.containsKey(comment.getTimestamp()))
        {
            String chs=started_loading_next_coments.get(comment.getTimestamp());
            if(chs.equals(comment.getComment_id()))
            {
                holder.loading_next_comments.setVisibility(View.VISIBLE);
                holder.summary_area_card.setVisibility(GONE);
                holder.loading_prev_comments.setVisibility(View.GONE);


            }
        }
        if(started_loading_next_coments.containsKey(comment.getTimestamp())==false)
        {
            android.util.Log.i("slasod",comment.getComment());
            holder.loading_next_comments.setVisibility(GONE);
        }
        if(started_loading_older_coments.containsKey(comment.getTimestamp())==false)
        {
            holder.loading_prev_comments.setVisibility(GONE);
        }

    }

    private void getNextCommentsAfter(Comment after_comment, int after_comment_pos)
    {

        Log.i("getNextCommentsAfter",after_comment.getTimestamp());
        CommentListener commentListener=(CommentListener) current_fragment;
        if(commentListener!=null&timestamps_positions.containsKey(after_comment.getTimestamp()))
        {
            commentListener.ScrollTo(after_comment_pos);
            int timestamp_pos=timestamps_positions.get(after_comment.getTimestamp());
            Comment timestamp=commentListUnderHeadComment.get(timestamp_pos);
            commentListener.GetCommentsUnderTimestampAfterComment(timestamp,
                    timestamp_pos,after_comment);
        }

    }

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
    private void categorizeMorningNoonAfternoonNight(CommentHolder holder,Comment comment,int position)
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

    private void decideIdTimeStamp(Comment comment,int position)
    {

        //decide if it is a timestamp
        if(position==0)
        {
            timestamps_positions.put(comment.getTimestamp(),position);
            comment.setIs_timestamp(true);
            this.commentListUnderHeadComment.set(position,comment);
        }
        else
        {

            Comment prev_comment=this.commentListUnderHeadComment.get(position-1);
            if(comment.isOnTheSameDay(prev_comment))
            {
                comment.setIs_timestamp(false);
                this.commentListUnderHeadComment.set(position,comment);
            }
            else
            {
                comment.setIs_timestamp(true);
                this.commentListUnderHeadComment.set(position,comment);
            }



        }

    }

    public int first_timestamp_position=0;
    public int last_timestamp_position=0;//visible
    public HashMap<String,String> started_loading_older_coments =new HashMap<String,String>();
    public HashMap<String,String> started_loading_next_coments =new HashMap<String,String>();
    private void showBasicCommentDetails(CommentAdapter.CommentHolder holder, int position)
    {

        final Comment comment=commentListUnderHeadComment.get(position);
        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();
        //show or hide the summary area(number of comments under the timestamp when collapsed)
        holder.timestamp_stance.setVisibility(GONE);

        if(comment.isIs_timestamp()==false)
        {
            holder.view_older.setVisibility(GONE);
            holder.summary_area.setVisibility(GONE);
            holder.timestamp_area.setVisibility(GONE);
        }
        else
        {
            holder.animationView.setVisibility(GONE);
            holder.timestamp_area.setVisibility(View.VISIBLE);
            holder.view_older.setVisibility(GONE);
            holder.summary_area.setVisibility(GONE);

            if(timestamps_comments.containsKey(timestamp))
            {
                ArrayList<Comment> comments=timestamps_comments.get(timestamp);
                if(comments.size()>=9&no_more_comments.containsKey(timestamp)==false)
                {
                    holder.view_older.setVisibility(View.VISIBLE);
                    holder.view_older_txt.setText("View Older Comments");
                    holder.view_older
                            .startAnimation(AnimationUtils
                                    .loadAnimation(holder
                                                    .view_older
                                                    .getContext()
                                            , R.anim.pulse));

                    setUpViewOlder(holder,position);
                }
                if(comments.size()>0)
                {
                    if(comment.isIs_expanded_timestamp()==false)
                    {
                        holder.summary_area.setVisibility(View.VISIBLE);
                    }

                }

                if(no_more_comments.containsKey(timestamp))
                {
                    holder.view_older.setVisibility(GONE);
                }

            }

            if(position>0)
            {
                last_timestamp_position=position;
            }

            if(first_timestamp_position!=position)
            {
                if(first_visible>first_timestamp_position||
                        first_visible==-1&comment.getComment().isEmpty()==false
                                &first_timestamp_position<first_visible)
                {
                    holder.animationView.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.animationView.setVisibility(GONE);
                }
            }

            //show the timestamp label
            Memory memory=new Memory(holder.timestamp_area.getContext());
            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

            if(jnm.isEmpty()==false)
            {

                long localtimeoffset=Long.parseLong(jnm);
                long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;

                org.joda.time.LocalDateTime localDateTime
                        =new org.joda.time.LocalDateTime(estimatedServerTimeMs);
                android.util.Log.w("jskasd", "offset="+localtimeoffset
                        +" estimatedServerTimeMs="+estimatedServerTimeMs
                        +" "+localDateTime.getDayOfMonth()+" "+localDateTime.getMonthOfYear()
                        +" "+localDateTime.getYear()
                        +" "+String.valueOf(localtimeoffset));

                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
                Log.i("tinsja","timelabel="+timelabel+" "+localDateTime.getDayOfMonth()
                        +" "+localDateTime.getMonthOfYear()+" "+localDateTime.getYear());
                holder.timestamp_label.setText(timelabel);
                holder.timestamp_label.setVisibility(View.VISIBLE);
            }



        }

        //check if there is a comment and hide it
        //if there is no comment


        holder.loading_prev_comments.setVisibility(GONE);
        if(comment.getComment().trim().isEmpty())
        {

            holder.comment_view.setVisibility(GONE);
            Log.i("kdhjksa",comment.getTimestamp()+" "+ started_loading_older_coments.containsKey(timestamp));
            if(started_loading_older_coments.containsKey(timestamp))
            {
                holder.loading_prev_comments.setVisibility(View.VISIBLE);
                holder.summary_area.setVisibility(GONE);
            }
            else
            {

                Log.i("GetCommentsUnderTimstmp","s01 "+position);
                holder.summary_area.setVisibility(View.VISIBLE);
                holder.summary_label.setText(Html.fromHtml("<b>Tap for comments</b>"));


                holder.summary_label
                        .startAnimation(AnimationUtils
                                .loadAnimation(holder
                                                .summary_label
                                                .getContext()
                        , R.anim.pulse));

                holder.summary_area.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i("GetCommentsUnderTimstmp","s0r1");

                        if(started_loading_older_coments.containsKey(timestamp)|| started_loading_older_coments.size()>0)
                        {
                            Memory memory=new Memory(holder.timestamp_area.getContext());
                            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                            if(jnm.isEmpty()==false)
                            {

                                long localtimeoffset=Long.parseLong(jnm);
                                org.joda.time.LocalDateTime localDateTime
                                        =new org.joda.time.LocalDateTime(localtimeoffset);

                                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
                                Log.i("tinsja","timelabel="+timelabel);
                                Snackbar.make(rootView,"Busy with comments for "+timelabel,
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                        {

                            if(current_fragment!=null)
                            {

                                CommentListener commentListener=(CommentListener) current_fragment;
                                if(commentListener!=null)
                                {
                                    commentListener.GetCommentsUnderTimestamp(comment, position);
                                    holder.summary_area.setVisibility(View.GONE);
                                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                                }

                            }

                        }

                    }
                });

            }
            /*if(position==commentListUnderHeadComment.size()-1)
            {
                holder.loading_prev_comments.setVisibility(View.VISIBLE);
                if(started_loading.containsKey(timestamp)==false)
                {

                    started_loading.put(timestamp,true);
                }
            }
            else if(loading_timestamp_position==position&started_loading.containsKey(timestamp))
            {
                holder.loading_prev_comments.setVisibility(View.VISIBLE);
                if(started_loading.containsKey(timestamp)==false)
                {

                    started_loading.put(timestamp,true);
                }
                Log.i("wrserta","position="+position
                        +" day="+comment.getDay()
                        +" month="+comment.getMonth()
                        +" year="+comment.getYear());
            }

             */

        }
        else
        {
            holder.comment_view.setVisibility(View.VISIBLE);
            holder.loading_prev_comments.setVisibility(GONE);
            holder.comment.setText(comment.getComment());

            if(started_loading_older_coments.containsKey(comment.getTimestamp()))
            {
                String comment_id=started_loading_older_coments.get(comment.getTimestamp());
                if(comment_id.equals(comment.getComment_id()))
                {
                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                    holder.summary_area.setVisibility(GONE);
                }

            }
            else if(started_loading_next_coments.containsKey(comment.getTimestamp()))
            {
                String comment_id=started_loading_next_coments.get(comment.getTimestamp());
                if(comment_id.equals(comment.getComment_id()))
                {
                    holder.loading_prev_comments.setVisibility(View.GONE);
                    holder.summary_area_card.setVisibility(GONE);
                    holder.loading_next_comments.setVisibility(View.VISIBLE);;

                }
            }

        }

        //show the comment name
        if(comment.isIs_timestamp())
        {
            timestamps_positions.put(comment.getTimestamp(),position);
            if(comment.getComment().isEmpty()==false)
            {
                holder.comment_name.setText(comment.getCommentator_name());
                if(no_more_comments.containsKey(timestamp))
                {
                    holder.view_older_txt.setText("No More Comments");
                    holder.view_older_txt.setTextColor(holder.view_older.getContext()
                            .getResources().getColor(R.color.gray));
                    setUpViewOlder(holder, position);
                }
                setUpArrowHandle(holder,comment);

            }
            else
            {
                holder.expand_collape_timestamp_group.setVisibility(GONE);
            }
        }

        //show time on the comment
        if(comment.getCreatedDate()!=null)
        {
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String output=df.format("hh:mm a",
                            comment.getCreatedDate())
                    .toString();
            holder.time_sent.setText(output);
        }

        holder.sent_status.setVisibility(GONE);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null
                &comment.getCommentator_uid()!=null)
        {

            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser.getUid().trim()
                    .equals(comment.getCommentator_uid().trim()))
            {
                showMessageIconForUser(holder,position);
            }
            else
            {
                Log.i("notusida",comment.getCommentator_uid());
            }

        }

        //decide if it is a reponse or an answer
        if(comment.getComment_type()==Comment.ANSWER)
        {
            Log.i("aansw",comment.getComment());
            holder.replied_area.setVisibility(View.VISIBLE);
            holder.comment_type.setText("Answer");
            holder.comment_type.setTextColor(holder.comment_name.getContext().getResources()
                    .getColor(R.color.green));

        }


    }

    private void showMessageIconForUser(CommentHolder holder, int position)
    {

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        Comment comment=commentListUnderHeadComment.get(position);
        if(comment.getCreatedDate()!=null)
        {
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a",
                            comment.getCreatedDate())
                    .toString();
            Log.i("musghjda",output+" "+comment.getComment_id()+" "+comment.isSent());
        }
        holder.comment_name.setText("You");
        if(comment.isSent())
        {
            holder.sent_status.setImageDrawable(holder.comment_name.getContext()
                    .getResources().getDrawable(R.drawable.ic_baseline_done_24));
            holder.sent_status.setVisibility(View.VISIBLE);

            if(comment.getNumOFCommentsRead()>0)
            {
                holder.num_people_read.setVisibility(View.VISIBLE);
                holder.num_people_read.setText(NumUtils.getAbbreviatedNum(comment.getNumOFCommentsRead())+" read");
            }

        }
        else
        {
            holder.sent_status.setImageDrawable(holder.comment_name.getContext()
                    .getResources().getDrawable(R.drawable.ic_baseline_access_time_24));
            holder.sent_status.setVisibility(View.VISIBLE);
        }



    }

    private void setUpArrowHandle(CommentHolder holder, Comment comment)
    {

        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();

        if(comment.isIs_timestamp())
        {

            setUpShareConvoDay(holder,comment);
            if(timestamps_comments.containsKey(timestamp))
            {
                ArrayList<Comment> comments=timestamps_comments.get(timestamp);

                if(comments.size()>1)
                {
                    holder.expand_collape_timestamp_group.setVisibility(View.VISIBLE);
                    if(comment.isIs_expanded_timestamp())
                    {
                        holder.expand_collape_timestamp_group.setImageDrawable(holder
                                .comment_name
                                .getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
                    }
                    else
                    {
                        holder.expand_collape_timestamp_group.setImageDrawable(holder
                                .comment_name
                                .getResources().getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
                    }

                    handleOnExpandCollapseClicked(holder,comment);
                }
                else
                {
                    holder.expand_collape_timestamp_group.setVisibility(View.GONE);
                }
            }

        }




    }

    private void setUpShareConvoDay(CommentHolder holder, Comment comment)
    {

        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();
        holder.share_day_convo.setVisibility(View.VISIBLE);
        holder.share_day_convo
                .startAnimation(AnimationUtils
                        .loadAnimation(holder
                                        .share_day_convo
                                        .getContext()
                                , R.anim.pulse));

        holder.timestamp_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareDayConvo(holder,comment);


            }
        });
        holder.share_day_convo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareDayConvo(holder,comment);

            }
        });

    }

    private void shareDayConvo(CommentHolder holder, Comment timestamp_comment)
    {

        android.text.format.DateFormat df =
                new android.text.format.DateFormat();
        String output1=df.format("yyyy-MM-dd hh:mm:ss a",
                        timestamp_comment.getCreatedDate())
                .toString();
        Log.i("shareDayConvo","sharing comment day="+output1);
        CommentListener commentListener=(CommentListener) current_fragment;
        if(commentListener!=null)
        {
            commentListener.OnShareDayConvo(timestamp_comment);
        }

    }

    private void handleOnExpandCollapseClicked(CommentHolder holder, Comment comment)
    {

        holder.expand_collape_timestamp_group
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(comment.isIs_expanded_timestamp())
                        {

                        }
                        else
                        {

                        }

                    }
                });

    }

    private void setUpViewOlder(CommentHolder holder, int position)
    {

        final Comment comment=commentListUnderHeadComment.get(position);
        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();
        holder.view_older.setVisibility(View.VISIBLE);
        holder.view_older.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(no_more_comments.containsKey(timestamp))
                {
                    Snackbar.make(rootView,"No More Comments for "+holder.summary_label.getText(),
                            Toast.LENGTH_LONG).show();
                }
                else
                {

                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                    if(current_fragment!=null)
                    {

                        CommentListener commentListener=(CommentListener) current_fragment;
                        if(commentListener!=null)
                        {
                            String timestamp=comment.getYear()+"-"+comment.getMonth()+"-"
                                    +comment.getDay();
                            if(timestamps_comments.containsKey(timestamp))
                            {
                                ArrayList<Comment> comments=timestamps_comments.get(timestamp);
                                if(comments.size()>0)
                                {
                                    Comment comment1=comments.get(comments.size()-1);
                                    android.text.format.DateFormat df =
                                            new android.text.format.DateFormat();
                                    String output1=df.format("hh:mm:ss a",
                                                    comment1.getCreatedDate())
                                            .toString();
                                    String output2=df.format("hh:mm:ss a",
                                                    comment.getCreatedDate())
                                            .toString();
                                    Log.i("mudgka",output1+" "+output2);
                                    commentListener.GetCommentsUnderTimestampBeforeComment(comment,position,comment);
                                }

                            }
                            else
                            {

                            }

                        }

                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        if(this.commentListUnderHeadComment.size()==0&no_comments_in_convo==false)
        {
            return 1;
        }
        else
        {
            return this.commentListUnderHeadComment.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(this.commentListUnderHeadComment.size()<1)
        {
            return -1;
        }
        else
        {
            Comment comment = this.commentListUnderHeadComment.get(position);
            return comment.getComment_type();
        }
    }

    public int last_reading_pos=0;
    @Override
    public void onViewAttachedToWindow(@NonNull CommentHolder holder) {
        super.onViewAttachedToWindow(holder);

        RecyclerView comment_list=current_fragment.getView().findViewById(R.id.comment_list);
        LinearLayoutManager ln=(LinearLayoutManager)comment_list.getLayoutManager();
        final int position=holder.getBindingAdapterPosition();
        if(getItemViewType(position)!=-1)
        {

            last_reading_pos=ln.findLastVisibleItemPosition();
            Comment comment=commentListUnderHeadComment.get(position);
            holder.loading_prev_comments.setVisibility(GONE);
            if(started_loading_older_coments.containsKey(comment.getTimestamp()))
            {
                String comment_id=started_loading_older_coments.get(comment.getTimestamp());
                if(comment_id.equals(comment.getComment_id()))
                {
                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                    holder.summary_area.setVisibility(GONE);
                    holder.loading_next_comments.setVisibility(View.GONE);;
                }
                holder.view_older_txt
                        .startAnimation(AnimationUtils
                                .loadAnimation(holder
                                                .view_older_txt
                                                .getContext()
                                        , R.anim.pulse));

            }
            else if(started_loading_next_coments.containsKey(comment.getTimestamp()))
            {
                String comment_id=started_loading_next_coments.get(comment.getTimestamp());
                if(comment_id.equals(comment.getComment_id()))
                {
                    holder.loading_prev_comments.setVisibility(View.GONE);
                    holder.summary_area_card.setVisibility(GONE);
                    holder.loading_next_comments.setVisibility(View.VISIBLE);;
                    holder.summary_label
                            .startAnimation(AnimationUtils
                                    .loadAnimation(holder
                                                    .summary_label
                                                    .getContext()
                                            , R.anim.pulse));

                }
            }

            if(comment.getComment().trim().isEmpty()) {

                String timestamp = comment.getYear() + "-" + comment.getMonth() + "-" + comment.getDay();
                holder.comment_view.setVisibility(GONE);

                if (started_loading_older_coments.containsKey(timestamp)) {
                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                } else {

                    holder.summary_area.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Log.i("GetCommentsUnderTimstmp","s0r1");
                            if(started_loading_older_coments.containsKey(timestamp)
                                    || started_loading_older_coments.size()>0)
                            {

                                if(started_loading_older_coments.containsKey(timestamp))
                                {
                                    Memory memory=new Memory(holder.timestamp_area.getContext());
                                    String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                    if(jnm.isEmpty()==false)
                                    {

                                        long localtimeoffset=Long.parseLong(jnm);
                                        long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                        org.joda.time.LocalDateTime localDateTime
                                                =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                        String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
                                        Log.i("tinsja","timelabel="+timelabel);
                                        Snackbar.make(rootView,"Busy with comments for "+timelabel,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    if(comment_position.containsKey(loading_timestamp_cmid))
                                    {
                                        int pos=comment_position.get(loading_timestamp_cmid);
                                        if(pos>-1&pos<commentListUnderHeadComment.size())
                                        {

                                            Comment comment1=commentListUnderHeadComment.get(loading_timestamp_position);
                                            Memory memory=new Memory(holder.timestamp_area.getContext());
                                            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                            if(jnm.isEmpty()==false)
                                            {

                                                long localtimeoffset=Long.parseLong(jnm);
                                                long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                                org.joda.time.LocalDateTime localDateTime
                                                        =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment1);
                                                Log.i("tinsja","timelabel="+timelabel);
                                                Toast.makeText(holder.timestamp_area.getContext(),
                                                        "Busy with comments for "+timelabel, Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    }
                                }


                            }
                            else if(started_loading_next_coments.containsKey(timestamp)
                                    || started_loading_next_coments.size()>0)
                            {

                                if(started_loading_next_coments.containsKey(timestamp))
                                {

                                    Memory memory=new Memory(holder.timestamp_area.getContext());
                                    String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                    if(jnm.isEmpty()==false)
                                    {

                                        long localtimeoffset=Long.parseLong(jnm);
                                        long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                        org.joda.time.LocalDateTime localDateTime
                                                =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                        String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
                                        Log.i("tinsja","timelabel="+timelabel);
                                        Snackbar.make(rootView,"Busy with comments for "+timelabel,
                                                Toast.LENGTH_LONG).show();



                                    }

                                }
                                else
                                {

                                    if(comment_position.containsKey(loading_timestamp_cmid))
                                    {
                                        int pos=comment_position.get(loading_timestamp_cmid);
                                        if(pos>-1&pos<commentListUnderHeadComment.size())
                                        {

                                            Comment comment1=commentListUnderHeadComment.get(loading_timestamp_position);
                                            Memory memory=new Memory(holder.timestamp_area.getContext());
                                            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                            if(jnm.isEmpty()==false)
                                            {

                                                long localtimeoffset=Long.parseLong(jnm);
                                                long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                                org.joda.time.LocalDateTime localDateTime
                                                        =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment1);
                                                Log.i("tinsja","timelabel="+timelabel);
                                                Toast.makeText(holder.timestamp_area.getContext(),
                                                        "Busy with comments for "+timelabel, Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    }

                                }

                            }
                            else
                            {

                                Log.i("msudhka","position="+position);
                                if(current_fragment!=null)
                                {

                                    CommentListener commentListener=(CommentListener) current_fragment;
                                    if(commentListener!=null)
                                    {
                                        loading_timestamp_position=position;
                                        loading_timestamp_cmid=comment.getComment_id();
                                        commentListener.GetCommentsUnderTimestamp(comment, position);
                                        holder.summary_area.setVisibility(View.GONE);
                                        holder.loading_prev_comments.setVisibility(View.VISIBLE);
                                    }

                                }

                            }


                        }
                    });



                }

            }





        }



    }

    @Override
    public void onViewRecycled(@NonNull CommentHolder holder) {
        super.onViewRecycled(holder);

        int position=holder.getBindingAdapterPosition();
        if(position>-1&position<commentListUnderHeadComment.size())
        {
            Comment comment=commentListUnderHeadComment.get(position);
            //categorizeMorningNoonAfternoonNight(holder,comment,position);
        }

    }

    int first_visible=-1;
    int last_visible=-1;

    public class CommentHolder extends RecyclerView.ViewHolder {


        CardView timestamp_area;
        ImageView expand_collape_timestamp_group;
        RelativeLayout timestamp_area_l;
        TextView timestamp_stance;
        TextView timestamp_label;
        RelativeLayout comment_view;
        LottieAnimationView animationView;
        TextView time_sent;
        TextView num_people_read;
        ImageView share_day_convo;

        TextView part_of_day;
        TextView comment_name;
        TextView comment_type;
        TextView num_comments_view;
        TextView summary_label;
        RelativeLayout summary_area;
        CardView summary_area_card;
        EmojiconTextView comment;
        ImageView sent_status;

        RelativeLayout view_older;
        TextView view_older_txt;
        ProgressBar loading_prev_comments;
        ProgressBar loading_next_comments;

        //reply area
        LinearLayout replied_area;
        FrameLayout reply_agree_disagree_flag;
        TextView replied_comment_name;
        TextView comment_reply;
        TextView reply_comment_type;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            share_day_convo=(ImageView) itemView.findViewById(R.id.share_day_convo);
            loading_prev_comments = (ProgressBar) itemView.findViewById(R.id.loading_prev_comments);
            loading_next_comments = (ProgressBar) itemView.findViewById(R.id.loading_next_comments);
            animationView = (LottieAnimationView) itemView.findViewById(R.id.animationView);
            time_sent = (TextView) itemView.findViewById(R.id.time_sent);
            num_people_read = (TextView) itemView.findViewById(R.id.num_people_read);
            timestamp_area = (CardView) itemView.findViewById(R.id.timestamp_area);
            expand_collape_timestamp_group = (ImageView)
                    itemView.findViewById(R.id.expand_collape_timestamp_group);
            timestamp_area_l = (RelativeLayout)
                    itemView.findViewById(R.id.timestamp_area_l);
            timestamp_label=(TextView)itemView.findViewById(R.id.timestamp_label);
            timestamp_stance=(TextView)itemView.findViewById(R.id.timestamp_stance);

            summary_area_card = (CardView) itemView.findViewById(R.id.summary_area_card);
            summary_area = (RelativeLayout) itemView.findViewById(R.id.summary_area);
            comment_view = (RelativeLayout) itemView.findViewById(R.id.comment_view);
            summary_label = (TextView) itemView.findViewById(R.id.summary_label);
            part_of_day = (TextView) itemView.findViewById(R.id.part_of_day);
            comment_name = (TextView) itemView.findViewById(R.id.comment_name);
            comment_type = (TextView) itemView.findViewById(R.id.comment_type);
            num_comments_view = (TextView) itemView.findViewById(R.id.num_comments_view);
            comment = (EmojiconTextView) itemView.findViewById(R.id.comment);
            sent_status = (ImageView) itemView.findViewById(R.id.sent_status);

            view_older=(RelativeLayout) itemView.findViewById(R.id.view_older);
            view_older_txt=(TextView) itemView.findViewById(R.id.view_older_txt);

                /*
                            //reply agree
            LinearLayout replied_area_agree;
            FrameLayout agree_disagree_flag_agree;
            TextView replied_comment_name_agree;
            TextView comment_reply_agree;
            //reply disagree
            LinearLayout replied_area_disagree;
            FrameLayout agree_disagree_flag_disagree;
            TextView replied_comment_name_disagree;
            TextView comment_reply_disagree;
            //reply question
            LinearLayout replied_area_question;
            FrameLayout agree_disagree_flag_question;
            TextView replied_comment_name_question;
            TextView comment_reply_question;
                 */

            //reply area
            replied_area=(LinearLayout) itemView.findViewById(R.id.replied_area);
            reply_agree_disagree_flag=(FrameLayout)itemView.findViewById(R.id.reply_agree_disagree_flag);
            replied_comment_name=(TextView) itemView.findViewById(R.id.replied_comment_name);
            comment_reply=(TextView) itemView.findViewById(R.id.comment_reply);
            reply_comment_type=(TextView) itemView.findViewById(R.id.reply_comment_type);

        }



    }

    public void AddComment(Comment comment)
    {
        this.commentListUnderHeadComment.add(comment);
        Log.i("getCommentsFrHedComent","size on adapter="+this.commentListUnderHeadComment.size());

    }

    public void AddTimeStampComment(Comment comment)
    {

        String timestamp=comment.getDay()+"-"+comment.getMonth()
                +"-"+comment.getYear();
        if(timestamps_comments.containsKey(timestamp))
        {
            ArrayList<Comment> comments=timestamps_comments.get(timestamp);
            comments.add(comment);
            timestamps_comments.put(timestamp,comments);

        }
        else
        {

            ArrayList<Comment> comments=new ArrayList<Comment>();
            comments.add(comment);
            timestamps_comments.put(timestamp,comments);


        }


    }

    public void setNoMoreComments(HashMap<String,Boolean> no_more_comments)
    {

        if(no_more_comments!=null)
        {

            this.no_more_comments.clear();
            this.no_more_comments.putAll(no_more_comments);


        }

    }


}