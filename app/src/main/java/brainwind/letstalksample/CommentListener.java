package brainwind.letstalksample;

import androidx.fragment.app.Fragment;

import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;

public interface CommentListener
{

    void foundHeadComment(Comment head_comment);
    void sendCommentWithNewsReference(MediaItemWebSearch mediaItemWebSearch,Comment head_comment);
    void onReply(Comment comment);
    void onImageComment(NewsFactsMedia newsFactsMedia);
    void onVideoComment(NewsFactsMedia newsFactsMedia);
    void GetCommentsUnderTimestamp(Comment comment, int position);
    void GetCommentsUnderTimestampBeforeComment(Comment timestamp_comment
            , int position, Comment before_comment);
    void GetCommentsUnderTimestampAfterComment(Comment timestamp_comment
            , int position, Comment after_comment);
    void OnShareDayConvo(Comment start_comment);
    void OnCancelReply();
    void OnNewMessage(Comment comment,int position);
    void OnErrorLoading(String errorString,int selection);
    void OnLoadingFinished();
    void ScrollTo(int position);
    void LeaveConvo();

    void messageUpdated();

    void onFragmentCreated(Fragment currentTopicForConvo);

}
