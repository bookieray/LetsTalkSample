package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class BasicCommentAdapter extends TimestampCommentAdapter
{

    public boolean is_loading=true;
    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(getItemViewType(position)!=-1)
        {
            Comment comment=commentListUnderHeadComment.get(position);
            holder.timestamp_stance.setVisibility(View.GONE);
            holder.summary_area.setVisibility(View.GONE);
            if(comment.getComment().isEmpty()==false)
            {

                holder.comment_view.setVisibility(View.VISIBLE);
                //The comment
                holder.comment.setText(comment.getComment());

                //The commentator's name
                boolean isYou=false;
                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                {
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getCommentator_uid()))
                    {
                        isYou=true;
                    }
                }
                if(isYou)
                {
                    holder.comment_name.setText("You");
                }
                else
                {
                    holder.comment_name.setText(comment.getCommentator_name());
                    String firstname="";
                    if(comment.getCommentator_name().trim().isEmpty()==false)
                    {
                        String headCommentName=comment.getCommentator_name();
                        String[] names=headCommentName.split(" ");
                        if(names.length>1)
                        {
                            headCommentName="";
                            for(int i=0;i<names.length;i++)
                            {
                                if(i==0)
                                {
                                    firstname=names[0];
                                }
                                String jn=names[i];
                                android.util.Log.i("uksidla","jn="+jn);
                                if(jn.length()>1)
                                {
                                    headCommentName+=" "+jn.substring(0,1).toUpperCase()
                                            +jn.substring(1,jn.length());
                                }
                                else
                                {
                                    headCommentName+=" "+jn.substring(0,0).toUpperCase();
                                }

                            }
                        }
                        headCommentName=headCommentName.trim();
                        holder.comment_name.setText(headCommentName);
                        android.util.Log.i("muhgsfa","headCommentName="+headCommentName);
                    }
                }

                //Comment Type
                if(comment.getComment_type()==Comment.AGREES)
                {
                    holder.comment_type.setText("Agrees with Head Comment");
                    holder.comment_type.setTextColor(holder.comment_type.getContext().getResources().getColor(R.color.purple_700));
                }
                if(comment.getComment_type()==Comment.DISAGREES)
                {
                    holder.comment_type.setText("Disagrees with Head Comment");
                    holder.comment_type.setTextColor(holder.comment_type.getContext().getResources().getColor(R.color.white));
                }
                if(comment.getComment_type()==Comment.QUESTION)
                {
                    holder.comment_type.setText("Questions the Head Comment");
                    holder.comment_type.setTextColor(holder.comment_type.getContext().getResources().getColor(R.color.blue));
                }
                if(comment.getComment_type()==Comment.ANSWER)
                {
                    holder.comment_type.setText("Answers the Head comment");
                    holder.comment_type.setTextColor(holder.comment_type.getContext().getResources().getColor(R.color.blue));
                }

                //The time the comment was sent
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

            if(comment.isSent())
            {
                Glide.with(holder.comment_view.getContext())
                        .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_done_24))
                        .into(holder.sent_status);

                if(comment.getNumOFCommentsRead()>0)
                {
                    Glide.with(holder.comment_view.getContext())
                            .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_done_all_24))
                            .into(holder.sent_status);
                }


            }
            else
            {
                Glide.with(holder.comment_view.getContext())
                        .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_access_time_24))
                        .into(holder.sent_status);
            }

        }

    }

    @Override
    public int getItemCount() {
        if(is_loading)
        {
            return commentListUnderHeadComment.size()+1;
        }
        else
        {
            return commentListUnderHeadComment.size();
        }
    }

}
