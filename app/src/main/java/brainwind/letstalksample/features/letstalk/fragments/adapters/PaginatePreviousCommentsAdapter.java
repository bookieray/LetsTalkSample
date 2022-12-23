package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class PaginatePreviousCommentsAdapter extends ShareConvoTimestampAdapter
{

    public HashMap<String,Boolean> no_more_comments_previous=new HashMap<String,Boolean>();
    public HashMap<String,String> started_loading_older_coments =new HashMap<String,String>();
    public String currentLoadingTimestampLabel="";
    public PaginatePreviousCommentsAdapter(Fragment current_fragment) {
        super(current_fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(getItemViewType(position)!=-1)
        {
            Comment comment=commentListUnderHeadComment.get(position);
            holder.loading_prev_comments.setVisibility(View.GONE);
            if(comment.isIs_timestamp())
            {
                if(no_more_comments_previous.containsKey(comment.getTimestamp()))
                {
                    holder.view_older.setVisibility(View.GONE);
                    holder.loading_prev_comments.setVisibility(View.GONE);
                    Log.i("fhjsgfdka","a "+position);
                }
                else
                {

                    if(started_loading_older_coments.containsKey(comment.getTimestamp()))
                    {
                        holder.loading_prev_comments.setVisibility(View.VISIBLE);
                        holder.view_older.setVisibility(View.GONE);
                        Log.i("fhjsgfdka","b "+position);
                    }
                    else {

                        if(timestamps_comments.containsKey(comment.getTimestamp()))
                        {

                            if(timestamps_comments.get(comment.getTimestamp()).size()>=9)
                            {
                                holder.view_older.setVisibility(View.VISIBLE);
                                Log.i("fhjsgfdka","c "+position);
                                setUpViewOlder(holder,position);
                            }
                            else
                            {
                                holder.view_older.setVisibility(View.GONE);
                                Log.i("fhjsgfdka","d "+position);
                            }
                            holder.loading_prev_comments.setVisibility(View.GONE);

                        }
                        else
                        {
                            Log.i("fhjsgfdka","e "+position);
                            holder.view_older.setVisibility(View.GONE);
                            holder.loading_prev_comments.setVisibility(View.GONE);
                        }
                    }

                }
            }
            else
            {
                holder.view_older.setVisibility(View.GONE);
                holder.loading_prev_comments.setVisibility(View.GONE);
            }
        }

    }

    public void setUpViewOlder(CommentHolder holder, int position)
    {

        final Comment comment=commentListUnderHeadComment.get(position);
        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();
        holder.view_older_txt
                .startAnimation(AnimationUtils
                        .loadAnimation(holder
                                        .view_older_txt
                                        .getContext()
                                , R.anim.pulse));
        holder.view_older.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(no_more_comments_previous.containsKey(timestamp))
                {
                    Toast.makeText(current_fragment.getActivity()
                            ,"No More Comments for "+holder.summary_label.getText(),
                            Toast.LENGTH_LONG).show();
                }
                else if(started_loading_older_coments.size()>0||currentLoadingTimestampLabel.isEmpty()==false)
                {

                    Toast.makeText(current_fragment.getActivity()
                            ,"Loading comments for "+currentLoadingTimestampLabel,
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
                            Log.i("mudgka","c="+timestamps_comments.containsKey(timestamp));
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
                                    currentLoadingTimestampLabel=getTimestamp(comment,holder.timestamp_area.getContext());
                                    commentListener.GetCommentsUnderTimestampBeforeComment(comment,position,comment);
                                    started_loading_older_coments.put(comment.getTimestamp(),currentLoadingTimestampLabel);
                                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                                    holder.view_older.setVisibility(View.GONE);

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

    public void SignalLoading(Comment timestamp_comment)
    {
        started_loading_older_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());

    }

    public void SignalLoadingLastComment()
    {

        Comment timestamp_comment=commentListUnderHeadComment.get(commentListUnderHeadComment.size()-1);
        SignalLoading(timestamp_comment);

    }


}
