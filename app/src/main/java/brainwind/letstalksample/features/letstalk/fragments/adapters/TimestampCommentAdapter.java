package brainwind.letstalksample.features.letstalk.fragments.adapters;

import static android.view.View.GONE;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class TimestampCommentAdapter extends RecyclerView.Adapter<TimestampCommentAdapter.CommentHolder>
{

    public HashMap<String,ArrayList<Comment>> timestamps_comments=new HashMap<String,ArrayList<Comment>>();

    public List<Comment> commentListUnderHeadComment = new ArrayList<Comment>();

    @Override
    public int getItemViewType(int position) {
        if(position>=this.commentListUnderHeadComment.size())
        {
            return -1;
        }
        else
        {
            Comment comment = this.commentListUnderHeadComment.get(position);
            return comment.getComment_type();
        }
    }

    @NonNull
    @Override
    public TimestampCommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==-1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list
                    , parent, false);
            return new CommentHolder(view);
        }
        else if (viewType == Comment.AGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agree_comment
                    , parent, false);
            return new CommentHolder(view);
        }
        else if (viewType == Comment.DISAGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disagree_comment
                    , parent, false);
            return new CommentHolder(view);
        }
        else if (viewType == Comment.QUESTION||viewType==Comment.ANSWER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_comment
                    , parent, false);
            return new CommentHolder(view);
        }
        else {

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.agree_comment
                            , parent, false);
            return new CommentHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TimestampCommentAdapter.CommentHolder holder, int position) {


        if(getItemViewType(position)!=-1)
        {
            Comment comment=commentListUnderHeadComment.get(position);
            decideIfTimeStamp(comment,position);
            holder.view_older.setVisibility(View.GONE);
            holder.summary_area.setVisibility(GONE);
            if(comment.isIs_timestamp())
            {
                holder.timestamp_area.setVisibility(View.VISIBLE);
                holder.animationView.setVisibility(GONE);
                holder.timestamp_area.setVisibility(View.VISIBLE);
                holder.view_older.setVisibility(GONE);
                holder.summary_area.setVisibility(GONE);
                //show the timestamp label
                String timestamp_label=getTimestamp(comment,holder.timestamp_area.getContext());
                holder.timestamp_label.setText(timestamp_label);
                holder.timestamp_label.setVisibility(View.VISIBLE);

            }
            else
            {
                holder.timestamp_area.setVisibility(GONE);
            }

            if(comment.getComment().trim().isEmpty())
            {
                holder.comment_view.setVisibility(GONE);
            }

        }

    }

    public String getTimestamp(Comment comment,Context context)
    {

        Memory memory=new Memory(context);
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
            return timelabel;
        }
        else
        {
            return "";
        }

    }

    private void decideIfTimeStamp(Comment comment,int position)
    {

        //decide if it is a timestamp
        if(position==0)
        {
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

    @Override
    public int getItemCount() {
        return commentListUnderHeadComment.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder
    {

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





}
