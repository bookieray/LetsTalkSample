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


    boolean scrolled=false;
    public InitialComments(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);
        setWorkerID(INITIAL);
    }


    @Override
    public void failedToGet(Exception e,String timestamp,  int workerID) {
        super.failedToGet(e,timestamp, workerID);


    }

    @Override
    public void successFullRead(QuerySnapshot queryDocumentSnapshots,String timestamp, int workerID) {
        super.successFullRead(queryDocumentSnapshots,timestamp, workerID);


    }

    public boolean isScrolled() {
        return scrolled;
    }

    public void setScrolled(boolean scrolled) {
        this.scrolled = scrolled;
    }

}
