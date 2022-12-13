package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class LoadFromLink extends CommentWorker implements LoadingResults
{


    public LoadFromLink(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);

    }

}
