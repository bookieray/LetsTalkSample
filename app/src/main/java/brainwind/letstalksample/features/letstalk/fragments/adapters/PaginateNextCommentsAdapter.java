package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.util.Log;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class PaginateNextCommentsAdapter extends PaginatePreviousCommentsAdapter
{

    public HashMap<String,Boolean> no_more_comments_next=new HashMap<String,Boolean>();
    public HashMap<String,String> started_loading_next_coments =new HashMap<String,String>();

    public PaginateNextCommentsAdapter(Fragment current_fragment) {
        super(current_fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(getItemViewType(position)!=-1) {

            Comment comment = commentListUnderHeadComment.get(position);
            if(checkIfLastCommentUnderTimestamp(position))
            {

                if(no_more_comments_next.containsKey(comment.getTimestamp()))
                {

                    Log.i("txr"+comment.getTimestamp(),"s1");
                    holder.summary_area.setVisibility(View.GONE);
                    holder.loading_next_comments.setVisibility(View.GONE);

                }
                else if(timestamps_comments.containsKey(comment.getTimestamp()))
                {
                    Log.i("txr"+comment.getTimestamp(),"s2");
                    holder.summary_area.setVisibility(View.GONE);
                }
                else
                {

                    holder.summary_area.setVisibility(View.GONE);
                    if(started_loading_older_coments.containsKey(comment.getTimestamp()))
                    {
                        Log.i("txr"+comment.getTimestamp(),"s3");
                        holder.summary_area.setVisibility(View.GONE);
                    }
                    else if(started_loading_next_coments.containsKey(comment.getTimestamp()))
                    {
                        Log.i("txr"+comment.getTimestamp(),"s4");
                        holder.loading_next_comments.setVisibility(View.VISIBLE);
                        holder.summary_area_card.setVisibility(View.GONE);
                        holder.summary_area.setVisibility(View.VISIBLE);
                    }
                    else
                    {

                        if(timestamps_comments.containsKey(comment.getTimestamp())==false)
                        {
                            Log.i("txr"+comment.getTimestamp(),"s5");
                            holder.summary_label.setText("Tap for comments");
                            holder.summary_area.setVisibility(View.VISIBLE);
                            holder.summary_area_card.setVisibility(View.VISIBLE);
                            holder.loading_next_comments.setVisibility(View.GONE);
                            holder.loading_prev_comments.setVisibility(View.GONE);
                            setUpOnNextClicked(holder,position);
                        }
                        else
                        {
                            Log.i("txr"+comment.getTimestamp(),"s6");
                            holder.summary_area.setVisibility(View.GONE);
                        }

                    }



                }

            }
            else
            {

                Log.i("txr"+comment.getTimestamp(),"s7");
                holder.summary_area.setVisibility(View.GONE);


            }




        }

    }

    public boolean checkIfLastCommentUnderTimestamp(int position)
    {

        if(position+1<commentListUnderHeadComment.size())
        {
            Comment current_comment=commentListUnderHeadComment.get(position);
            Comment next_comment=commentListUnderHeadComment.get(position+1);
            if(current_comment.getTimestamp().equals(next_comment.getTimestamp()))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(position==commentListUnderHeadComment.size()-1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    private void setUpOnNextClicked(CommentHolder holder, int position)
    {

        Log.i("setUpOnNextClicked","position="+position);
        final Comment comment=commentListUnderHeadComment.get(position);
        holder.summary_label
                .startAnimation(AnimationUtils
                        .loadAnimation(holder
                                        .summary_label
                                        .getContext()
                                , R.anim.pulse));

        holder.summary_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String timestamp=comment.getTimestamp();
                if(no_more_comments_previous.containsKey(timestamp))
                {
                    Toast.makeText(current_fragment.getActivity()
                            ,"No More Comments for "+holder.summary_label.getText(),
                            Toast.LENGTH_LONG).show();
                }
                else if(started_loading_older_coments.size()>0||started_loading_next_coments.size()>0)
                {
                    Toast.makeText(current_fragment.getActivity()
                            ,"Loading comments for "+currentLoadingTimestampLabel,
                            Toast.LENGTH_LONG).show();
                }
                else
                {

                    if(current_fragment!=null)
                    {

                        CommentListener commentListener=(CommentListener) current_fragment;
                        if(commentListener!=null)
                        {
                            currentLoadingTimestampLabel=getTimestamp(comment,holder.timestamp_area.getContext());
                            commentListener.GetCommentsUnderTimestamp(comment, position);
                            holder.loading_next_comments.setVisibility(View.VISIBLE);
                            holder.summary_area_card.setVisibility(View.GONE);
                            started_loading_next_coments.put(comment.getTimestamp(),currentLoadingTimestampLabel);

                        }

                    }

                }


            }
        });

    }




}
