package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.util.Log;

import java.util.ArrayList;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class ShareConvoTimestampAdapter extends BasicCommentAdapter
{
    Fragment current_fragment;

    public ShareConvoTimestampAdapter(Fragment current_fragment) {
        this.current_fragment = current_fragment;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(getItemViewType(position)!=-1)
        {
            Comment comment=commentListUnderHeadComment.get(position);
            if(holder.share_day_convo!=null)
            {
                if(holder.share_day_convo.getVisibility()==View.VISIBLE)
                {
                    setUpShareConvoDay(holder,comment);
                }
            }
        }

    }

    public void setUpShareConvoDay(CommentHolder holder, Comment comment)
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


}
