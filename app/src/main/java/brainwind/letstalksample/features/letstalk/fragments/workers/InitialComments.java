package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class InitialComments extends CommentReader
{


    public InitialComments(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);
        setWorkerID(INITIAL);
    }


    @Override
    public void failedToGet(Exception e,String timestamp,  int workerID) {
        super.failedToGet(e,timestamp, workerID);

        if(getCommentWorkerFromFragment()!=null)
        {
            CommentCommunications commentCommunications=(CommentCommunications) getCommentWorkerFromFragment();
            if(commentCommunications!=null)
            {
                commentCommunications.onFailureToFetchTimestampComments(getHead_comment(),e.getMessage(),timestamp,workerID);
            }
        }
        else
        {
            Log.i("onshjj","failedToGet getCommentWorkerFromFragment()==null");
        }

    }

    @Override
    public void successFullRead(QuerySnapshot queryDocumentSnapshots,String timestamp, int workerID) {
        super.successFullRead(queryDocumentSnapshots,timestamp, workerID);

        if(getCommentWorkerFromFragment()!=null)
        {
            CommentCommunications commentCommunications=(CommentCommunications) getCommentWorkerFromFragment();
            if(commentCommunications!=null)
            {
                commentCommunications.onSuccessfulFetchTimestampComments(queryDocumentSnapshots,timestamp,workerID);
            }
        }
        else
        {
            Log.i("onshjj","successFullRead getCommentWorkerFromFragment()==null");
        }

    }


}
