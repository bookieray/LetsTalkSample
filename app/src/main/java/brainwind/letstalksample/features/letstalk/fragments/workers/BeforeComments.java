package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class BeforeComments extends CommentReader
{


    public BeforeComments(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);
        setWorkerID(PAGINATION_PREV);
    }

    @Override
    public void successFullRead(QuerySnapshot queryDocumentSnapshots,String timestamp,  int workerID) {
        super.successFullRead(queryDocumentSnapshots,timestamp, workerID);

        Log.i("BeforeComments","successFullRead workerID="+workerID+" queryDocumentSnapshots.isEmpty()="+queryDocumentSnapshots.isEmpty());

        CommentCommunications commentCommunications=(CommentCommunications) getCommentWorkerFromFragment();
        if(commentCommunications!=null)
        {
            commentCommunications.onSuccessfulFetchTimestampComments(queryDocumentSnapshots,timestamp,workerID);
        }

    }

    @Override
    public void failedToGet(Exception e,String timestamp,  int workerID) {
        super.failedToGet(e,timestamp, workerID);

        Log.i("BeforeComments","failedToGet workerID="+workerID+" e="+e.getMessage());

    }


}
