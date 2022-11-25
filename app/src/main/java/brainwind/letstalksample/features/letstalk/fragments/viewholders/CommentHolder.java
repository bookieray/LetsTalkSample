package brainwind.letstalksample.features.letstalk.fragments.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import brainwind.letstalksample.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentHolder extends RecyclerView.ViewHolder {

    public ExpandableTextView comment;
    public TextView comment_name;
    public TextView comment_type;
    public CircleImageView head_profile_image;

    public CommentHolder(@NonNull View itemView) {
        super(itemView);

        comment=(ExpandableTextView) itemView.findViewById(R.id.comment);
        comment_name=(TextView) itemView.findViewById(R.id.comment_name);
        comment_type=(TextView) itemView.findViewById(R.id.comment_type);
        head_profile_image=(CircleImageView) itemView.findViewById(R.id.head_profile_image);


    }


}
