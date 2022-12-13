package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class ExpandCollapseCommentsAdapter extends PaginateNextCommentsAdapter
{


    public ExpandCollapseCommentsAdapter(Fragment current_fragment) {
        super(current_fragment);

    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(getItemViewType(position)!=-1)
        {
            Comment comment = commentListUnderHeadComment.get(position);
            if(comment.isIs_timestamp())
            {
                if(timestamps_comments.containsKey(comment.getTimestamp()))
                {
                    ArrayList<Comment> tims=timestamps_comments.get(comment.getTimestamp());
                    if(tims.size()>=2)
                    {
                        holder.expand_collape_timestamp_group.setVisibility(View.VISIBLE);
                        setUpExpandCollapse(holder,position);

                    }
                }
                else
                {
                    holder.expand_collape_timestamp_group.setVisibility(View.GONE);
                }
            }

        }

    }

    public void setUpExpandCollapse(CommentHolder holder, int position)
    {
        final Comment comment=commentListUnderHeadComment.get(position);

        if(comment.isIs_expanded_timestamp())
        {
            holder.summary_area.setVisibility(View.GONE);
            if(started_loading_next_coments.containsKey(comment.getTimestamp()))
            {
                holder.summary_area.setVisibility(View.VISIBLE);
                holder.loading_next_comments.setVisibility(View.VISIBLE);
                holder.summary_area_card.setVisibility(View.GONE);
            }
            else
            {
                holder.loading_next_comments.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.summary_area.setVisibility(View.VISIBLE);
            if(started_loading_next_coments.containsKey(comment.getTimestamp()))
            {
                holder.loading_next_comments.setVisibility(View.VISIBLE);
            }
            else
            {

                holder.loading_next_comments.setVisibility(View.GONE);
            }

            setUpOnExpandCollapseClicked(holder,position);
        }



    }

    private void setUpOnExpandCollapseClicked(CommentHolder holder, int position)
    {

        final Comment comment=commentListUnderHeadComment.get(position);
        ArrayList<Comment> tcmsd=timestamps_comments.get(comment.getTimestamp());
        int kl=tcmsd.size()-1;
        if(kl>0)
        {
            holder.summary_area.setVisibility(View.VISIBLE);
            holder.summary_area_card.setVisibility(View.VISIBLE);
            holder.summary_label.setText(NumUtils.getAbbreviatedNum(tcmsd.size())+" More");
        }
        else
        {
            holder.summary_area.setVisibility(View.GONE);
        }

        if(started_loading_older_coments.size()>0||started_loading_next_coments.size()>0)
        {
            holder.summary_area.setVisibility(View.GONE);
        }

        holder.summary_area_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ExpandCollapse(comment,position);

            }
        });

        holder.expand_collape_timestamp_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ExpandCollapse(comment,position);

            }
        });

    }

    public void ExpandCollapse(Comment comment,int position)
    {

        ArrayList<Comment> tcmsd=timestamps_comments.get(comment.getTimestamp());
        tcmsd.remove(0);

        if(comment.isIs_expanded_timestamp())
        {
            int endPos=tcmsd.size();
            commentListUnderHeadComment.removeAll(tcmsd);
            notifyItemRangeRemoved(position+1,endPos);
            comment.setIs_expanded_timestamp(false);
            commentListUnderHeadComment.set(position,comment);
        }
        else
        {
            int endPos=tcmsd.size();
            if(position+1<commentListUnderHeadComment.size())
            {
                commentListUnderHeadComment.addAll(position+1,tcmsd);
                notifyItemRangeInserted(position+1,endPos);
            }
            else
            {
                commentListUnderHeadComment.addAll(tcmsd);
                if(position+1<commentListUnderHeadComment.size())
                {
                    notifyItemRangeInserted(position+1,endPos);
                }

            }



        }

    }


}
