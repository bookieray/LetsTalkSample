package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.Query;

import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class QueryWorker
{

    private static final String CONVO_ID = "param1";
    private static final String TITLE = "param2";
    private static final String IS_STANDALONE = "param3";
    private static final String TIMEOFFSET = "param4";


    public static com.google.firebase.firestore.Query getHeadCommentQuery(Fragment currentTopicForConvo)
    {

        if(currentTopicForConvo.getArguments()!=null)
        {

            String conversation_id = currentTopicForConvo.getArguments().getString(CONVO_ID);
            boolean is_standalone=false;
            if (currentTopicForConvo.getArguments().containsKey(IS_STANDALONE)) {
                is_standalone = currentTopicForConvo.getArguments().getBoolean(IS_STANDALONE);
            }
            com.google.firebase.firestore.Query query;
            if (is_standalone) {
                query = CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID, conversation_id)
                        .whereEqualTo(OrgFields.IS_NEW_TOPIC, true)
                        .orderBy(OrgFields.NUM_COMMENTS, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .orderBy(OrgFields.USER_CREATED_DATE, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1);
            } else {
                query = CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID, conversation_id)
                        .whereEqualTo(OrgFields.IS_NEW_TOPIC, true)
                        .orderBy(OrgFields.NUM_COMMENTS, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .orderBy(OrgFields.USER_CREATED_DATE, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(1);
            }
            return query;

        }
        else
        {
            return null;
        }

    }

    public static com.google.firebase.firestore.Query getCommentsForHeadCommentQuery(Comment timestamp_comment
            , Spinner comment_type)
    {

        com.google.firebase.firestore.Query query=null;
        if(timestamp_comment!=null)
        {
            if(comment_type!=null)
            {



            }
        }
        return query;

    }

    public static Query getCommentsForHeadCommentQuery(Comment timestamp_comment,
                                                       Spinner comment_type,
                                                       Comment afterComment)
    {

        com.google.firebase.firestore.Query query=null;
        if(timestamp_comment!=null)
        {
            if(comment_type!=null)
            {



            }
        }
        return query;


    }

}
