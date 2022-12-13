package brainwind.letstalksample.features.letstalk.fragments.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ResumeFromLinkCommentsAdapter extends DayMarkerCommentsAdapter
{

    public String starting_timestamp="";
    public int starting_set_number=0;

    public ResumeFromLinkCommentsAdapter(Fragment current_fragment) {
        super(current_fragment);


    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if(starting_timestamp.isEmpty())
        {



        }

    }



}
