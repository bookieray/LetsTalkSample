package brainwind.letstalksample.features.letstalk.fragments.listeners;

import java.util.ArrayList;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public interface CommentsListener
{
    //finding the head comment
    void foundHeadComment(Comment headcomment);
    void noHeadCommentFound();
    void errorFindingHeadComment(String errorMessage);
    //finding comments
    void foundCommentsForHeadComment(ArrayList<Comment> comments);
    void errorFindingCommentsForHeadComment();

}
