package brainwind.letstalksample.features.letstalk.fragments.workers;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public interface CommentCommunications
{

    void TryLoadHeadComment();
    void ShowMoreHeadComments();
    void ChangeHeadComment(Comment headcomment);
    void onFailureToFetchTimestamps(Comment headcomment,String errorMessage);
    void onFailureToFetchTimestampComments(Comment headcomment,String errorMessage,String timestamp,int workerID);
    void onSuccessfulFetchTimestampComments(QuerySnapshot queryDocumentSnapshots,String timestamp, int workerID);
    void TimeStampsForHeadComment(Comment headcomment, ArrayList<Comment> timestampList);

}
