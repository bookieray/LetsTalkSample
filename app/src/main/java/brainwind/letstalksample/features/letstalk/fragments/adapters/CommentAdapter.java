package brainwind.letstalksample.features.letstalk.fragments.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;


//ResumeFromLinkCommentsAdapter
public class CommentAdapter extends TestAdapter
{


    public int last_reading_pos=0;
    public CommentAdapter(Fragment current_fragment) {
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