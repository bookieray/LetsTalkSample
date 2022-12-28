package brainwind.letstalksample.features.letstalk;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public interface CommentTimeStampNavigation
{

    void getCommentsForTimeStampsPrevTo(Comment first_comment_in_timestamp);
    void getCommentsForTimeStampsNextAfter(Comment last_comment_in_timestamp);
    void getCommentsForTimeStampsInitial(Comment timestamp_comemnt);


}
