package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

        runQuery(query);

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

        runQuery(query);

    }
    public void getCommentsForTimestampCommentTypeAfter(Comment headcomment,String timestamp,int comment_type,Comment afterdatecomment) throws Exception {

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

        runQuery(query);

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

    public void runQuery(Query query)
    {

        Log.i("onshjj","runQuery");
        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("onshjj","onFailure");
                        failedToGet(e,getWorkerID());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        finishedFirstRequest=true;
                        Log.i("onshjj","onSuccess");
                        successFullRead(queryDocumentSnapshots,getWorkerID());

                    }
                });

    }

    public boolean isFinishedFirstRequest() {
        return finishedFirstRequest;
    }

    public void successFullRead(QuerySnapshot queryDocumentSnapshots, int workerID)
    {

    }

    public void failedToGet(Exception e, int workerID)
    {



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
