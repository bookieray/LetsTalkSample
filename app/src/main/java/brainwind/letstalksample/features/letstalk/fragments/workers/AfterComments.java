package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class AfterComments extends CommentReader
{


    public AfterComments(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id,currentTopicFragment);
    }

    @Override
    public void successFullRead(QuerySnapshot queryDocumentSnapshots,String timestamp, int workerID) {
        super.successFullRead(queryDocumentSnapshots,timestamp, workerID);
    }

    @Override
    public void failedToGet(Exception e,String timestamp,  int workerID) {
        super.failedToGet(e,timestamp, workerID);
    }


}
