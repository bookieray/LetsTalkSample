package brainwind.letstalksample.features.letstalk.fragments.adapters;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;


//ResumeFromLinkCommentsAdapter
public class CommentAdapter extends ResumeFromLinkCommentsAdapter
{


    public int last_reading_pos=0;
    public CommentAdapter(Fragment fragment) {
        super(fragment);
        //super(current_fragment);
    }

    private boolean hasComments=false;
    public void setHasComments(boolean b)
    {
        this.hasComments=b;
    }

    public boolean isHasComments() {
        return hasComments;
    }

}