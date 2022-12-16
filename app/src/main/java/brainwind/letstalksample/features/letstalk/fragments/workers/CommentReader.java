package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.TMDay;

public class CommentReader extends QueryWorker{

    private boolean finishedFirstRequest=false;
    CurrentTopicForConvo currentTopicForConvo;
    public CommentReader(String conversation_id, Fragment currentTopicForConvof) {
        super(conversation_id, currentTopicForConvof);
        this.currentTopicForConvo=(CurrentTopicForConvo) currentTopicForConvof;
    }

    @Override
    public void setCurrentTopicForConvo(Fragment currentTopicForConvo) {
        super.setCurrentTopicForConvo(currentTopicForConvo);
        this.currentTopicForConvo=(CurrentTopicForConvo) currentTopicForConvo;
    }

    public HashMap<String, ArrayList<Comment>> Timestamp_commenttype_commentArrayList = new HashMap<String, ArrayList<Comment>>();

    public boolean hasCommentsForTimestampCommentType(String timestamp,int comment_type)
    {

        String key=timestamp+"_"+comment_type;
        return Timestamp_commenttype_commentArrayList.containsKey(key);
    }
    //either used for after the last date from a previous set or before a date from a previous set
    public boolean hasCommentsForTimestampCommentType(String timestamp, int comment_type, Date afterdate)
    {

        String key=timestamp+"_"+comment_type+"_"+afterdate.getTime();
        return Timestamp_commenttype_commentArrayList.containsKey(key);
    }

    public void getCommentsForTimestampCommentType(Comment headcomment,String timestamp,int comment_type) throws Exception {

        //using the conversation id, head comment comment id, year, month, day and comment type
        TMDay tmDay=new TMDay(timestamp);
        int month=-1;
        int day=-1;
        try
        {
            month=Integer.parseInt(tmDay.getMonth());
        }
        catch(Exception exception)
        {

        }
        try
        {
            day=Integer.parseInt(tmDay.getDay());
        }
        catch(Exception exception)
        {

        }

        if(month==-1||day==-1)
        {
            throw new Exception("Month or Day is invalid, "+timestamp+" CommentReader.getCommentsForTimestampCommentType(Comment headcomment,String timestamp,int comment_type)");
        }

        setHead_comment(headcomment);
        Comment timestamp_comment=new Comment();
        timestamp_comment.setTimestamp(tmDay);

        Query query=null;
        /* from the spinner
            <item>All</item>
            <item>Agree</item>
            <item>Disagree</item>
            <item>Question</item>
            <item>Answers</item>
         */
        if(comment_type==0)
        {
            query=getQueryForAllCommentTypeWithHeadComment(timestamp_comment);
        }
        if(comment_type==1)
        {
            query=getQueryForAgreeWithHeadComment(timestamp_comment);
        }
        if(comment_type==2)
        {
            query=getQueryForDisagreeWithHeadComment(timestamp_comment);
        }
        if(comment_type==3)
        {
            query=getQueryForQuestionWithHeadComment(timestamp_comment);
        }
        if(comment_type==4)
        {
            query=getQueryForQuestionWithHeadComment(timestamp_comment);
        }

        runQuery(query,timestamp);

    }
    public void getCommentsForTimestampCommentTypeBefore(Comment headcomment,String timestamp,int comment_type,Comment beforedatecomment) throws Exception {

        //using the conversation id, head comment comment id, year, month, day and comment type
        TMDay tmDay=new TMDay(timestamp);
        int month=-1;
        int day=-1;
        try
        {
            month=Integer.parseInt(tmDay.getMonth());
        }
        catch(Exception exception)
        {

        }
        try
        {
            day=Integer.parseInt(tmDay.getDay());
        }
        catch(Exception exception)
        {

        }

        if(month==-1||day==-1)
        {
            throw new Exception("Month or Day is invalid, "+timestamp+" CommentReader.getCommentsForTimestampCommentType(Comment headcomment,String timestamp,int comment_type)");
        }

        beforeComment=beforedatecomment;
        setBeforeComment(beforedatecomment);
        setHead_comment(headcomment);
        Comment timestamp_comment=new Comment();
        timestamp_comment.setTimestamp(tmDay);

        Query query=null;
        /* from the spinner
            <item>All</item>
            <item>Agree</item>
            <item>Disagree</item>
            <item>Question</item>
            <item>Answers</item>
         */
        if(comment_type==0)
        {
            query=getQueryForAllCommentTypeWithHeadComment(timestamp_comment);
        }
        if(comment_type==1)
        {
            query=getQueryForAgreeWithHeadComment(timestamp_comment);
        }
        if(comment_type==2)
        {
            query=getQueryForDisagreeWithHeadComment(timestamp_comment);
        }
        if(comment_type==3)
        {
            query=getQueryForQuestionWithHeadComment(timestamp_comment);
        }
        if(comment_type==4)
        {
            query=getQueryForQuestionWithHeadComment(timestamp_comment);
        }

        runQuery(query,timestamp);

    }
    public void getCommentsForTimestampCommentTypeAfter(Comment headcomment,String timestamp,int comment_type,Comment afterdatecomment) throws Exception {

        try
        {

            //using the conversation id, head comment comment id, year, month, day and comment type
            TMDay tmDay=new TMDay(timestamp);
            int month=-1;
            int day=-1;
            try
            {
                month=Integer.parseInt(tmDay.getMonth());
            }
            catch(Exception exception)
            {

            }
            try
            {
                day=Integer.parseInt(tmDay.getDay());
            }
            catch(Exception exception)
            {

            }

            if(month==-1||day==-1)
            {
                throw new Exception("Month or Day is invalid, "+timestamp+" CommentReader.getCommentsForTimestampCommentType(Comment headcomment,String timestamp,int comment_type)");
            }

            afterdatecomment=afterdatecomment;
            setAfterComment(afterdatecomment);
            setHead_comment(headcomment);
            Comment timestamp_comment=new Comment();
            timestamp_comment.setTimestamp(tmDay);

            Query query=null;
            /* from the spinner
                <item>All</item>
                <item>Agree</item>
                <item>Disagree</item>
                <item>Question</item>
                <item>Answers</item>
            */
            if(comment_type==0)
            {
                query=getQueryForAllCommentTypeWithHeadComment(timestamp_comment);
            }
            if(comment_type==1)
            {
                query=getQueryForAgreeWithHeadComment(timestamp_comment);
            }
            if(comment_type==2)
            {
                query=getQueryForDisagreeWithHeadComment(timestamp_comment);
            }
            if(comment_type==3)
            {
                query=getQueryForQuestionWithHeadComment(timestamp_comment);
            }
            if(comment_type==4)
            {
                query=getQueryForQuestionWithHeadComment(timestamp_comment);
            }

            runQuery(query,timestamp);

        }
        catch(Exception exception)
        {
            failedToGet(exception,timestamp,getWorkerID());
        }

    }

    public static final int INITIAL=0;
    public static final int PAGINATION_NEXT=1;
    public static final int PAGINATION_PREV=2;
    public static final int LINK_RESUME=3;

    private int id=INITIAL;
    public void setWorkerID(int id)
    {
        this.id=id;
    }
    public int getWorkerID()
    {
        return this.id;
    }

    public void runQuery(Query query,String timestamp)
    {

        Log.i("onshjj","runQuery "+timestamp);
        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("onshjj","onFailure "+timestamp);
                        failedToGet(e,timestamp,getWorkerID());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        finishedFirstRequest=true;
                        Log.i("onshjj","onSuccess "+queryDocumentSnapshots.isEmpty()+" "+timestamp+" "+queryDocumentSnapshots.getMetadata().isFromCache());
                        successFullRead(queryDocumentSnapshots,timestamp,getWorkerID());

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Log.i("onshjj","isSuccessful="+task.isSuccessful());
                        if(task.isSuccessful())
                        {

                        }
                        else
                        {
                            Log.i("onshjj","e="+task.getException().getMessage());
                        }

                    }
                });

    }

    public boolean isFinishedFirstRequest() {
        return finishedFirstRequest;
    }

    public void successFullRead(QuerySnapshot queryDocumentSnapshots,String timestamp, int workerID)
    {
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

    public void failedToGet(Exception e,String timestamp, int workerID)
    {

        Log.i("failedToGet","e="+e.getMessage());
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
            Log.i("failedToGet","failedToGet getCommentWorkerFromFragment()==null");
        }

    }

    public CommentWorker getCommentWorkerFromFragment()
    {

        if(this.currentTopicForConvo!=null)
        {
            return this.currentTopicForConvo.getCommentWorker();
        }
        else
        {
            return null;
        }
    }


}
