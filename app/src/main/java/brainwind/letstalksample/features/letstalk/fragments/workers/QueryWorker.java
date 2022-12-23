package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.Query;

import org.joda.time.LocalDateTime;

import java.util.Date;

import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;

public class QueryWorker
{

    private static final String CONVO_ID = "param1";
    private static final String TITLE = "param2";
    private static final String IS_STANDALONE = "param3";
    private static final String TIMEOFFSET = "param4";

    CommentAdapter commentAdapter;
    String conversation_id="";

    public QueryWorker(String conversation_id,Fragment currentTopicForConvo) {
        this.conversation_id = conversation_id;
        this.commentAdapter=null;
    }

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

    public static com.google.firebase.firestore.Query getHeadCommentsQuery(Fragment currentTopicForConvo)
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
                        .limit(10);
            } else {
                query = CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID, conversation_id)
                        .whereEqualTo(OrgFields.IS_NEW_TOPIC, true)
                        .orderBy(OrgFields.NUM_COMMENTS, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .orderBy(OrgFields.USER_CREATED_DATE, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(10);
            }
            return query;

        }
        else
        {
            return null;
        }

    }

    public static com.google.firebase.firestore.Query getHeadCommentsQuery(Fragment currentTopicForConvo,Comment last_comment)
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
                        .whereGreaterThan(OrgFields.USER_CREATED_DATE, last_comment.getCreatedDate())
                        .orderBy(OrgFields.NUM_COMMENTS, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .orderBy(OrgFields.USER_CREATED_DATE, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(10);
            } else {
                query = CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID, conversation_id)
                        .whereEqualTo(OrgFields.IS_NEW_TOPIC, true)
                        .whereGreaterThan(OrgFields.USER_CREATED_DATE, last_comment.getCreatedDate())
                        .orderBy(OrgFields.NUM_COMMENTS, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .orderBy(OrgFields.USER_CREATED_DATE, com.google.firebase.firestore.Query.Direction.DESCENDING)
                        .limit(10);
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

    Comment afterComment;
    Comment beforeComment;

    public Comment getAfterComment() {
        return afterComment;
    }

    public void setAfterComment(Comment afterComment) {
        this.afterComment = afterComment;
    }

    public Comment getBeforeComment() {
        return beforeComment;
    }

    public void setBeforeComment(Comment beforeComment) {
        this.beforeComment = beforeComment;
    }

    Comment head_comment;

    public Comment getHead_comment() {
        return head_comment;
    }

    public void setHead_comment(Comment head_comment) {
        this.head_comment = head_comment;
    }

    Fragment currentTopicForConvo;

    public Fragment getCurrentTopicForConvo() {
        return currentTopicForConvo;
    }

    public void setCurrentTopicForConvo(Fragment currentTopicForConvo) {
        this.currentTopicForConvo = currentTopicForConvo;
    }

    public Query getQuery(Comment timestamp_comment, Spinner comment_filter)
    {


        Query query=QueryWorker.
                getCommentsForHeadCommentQuery(timestamp_comment,comment_filter);
        boolean is_standalone=false;
        if (currentTopicForConvo.getArguments().containsKey(IS_STANDALONE)) {
            is_standalone = currentTopicForConvo.getArguments().getBoolean(IS_STANDALONE);
        }
        if(head_comment!=null)
        {

            if(comment_filter.getSelectedItemPosition()==0)
            {
                query=getQueryForAllCommentTypeWithHeadComment(timestamp_comment);

            }
            //Agree
            else if(comment_filter.getSelectedItemPosition()==1)
            {
                query=getQueryForAgreeWithHeadComment(timestamp_comment);
            }
            //Disagree
            else if(comment_filter.getSelectedItemPosition()==2)
            {
                query=getQueryForDisagreeWithHeadComment(timestamp_comment);
            }
            //Question
            else if(comment_filter.getSelectedItemPosition()==3)
            {
                query=getQueryForQuestionWithHeadComment(timestamp_comment);
            }
            //Answers
            else
            {
                query=getQueryForAnswerWithHeadComment(timestamp_comment);
            }

        }
        else
        {

            if(comment_filter.getSelectedItemPosition()==0)
            {
                query=CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                if(beforeComment !=null)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                            .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                }
            }
            //Agree
            else if(comment_filter.getSelectedItemPosition()==1)
            {
                query=CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                if(beforeComment !=null)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                            .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                }
            }
            //Disagree
            else if(comment_filter.getSelectedItemPosition()==2)
            {
                query=CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                if(beforeComment !=null)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                            .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                }
            }
            //Question
            else if(comment_filter.getSelectedItemPosition()==3)
            {
                query=CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                if(beforeComment !=null)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                            .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                }
            }
            //Answers
            else
            {
                query=CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                if(beforeComment !=null)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                            .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
                }
            }


        }

        return query;

    }

    public Query getQueryForAllCommentTypeWithHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);

        Log.i("beforeCommentx","b="+(beforeComment !=null));
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",afterComment.getDateStr()+" "+afterComment.getComment()+" "
                    +afterComment.getCreatedDate().getTime()+" "+head_comment.getComment_id());
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereGreaterThan("sjkkdhja",afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        else if(beforeComment !=null)
        {

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", beforeComment.getCreatedDate())
                    .toString();
            Log.i("beforeCommentx",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();


            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);

                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        Log.i("getCommentsUnderTmeStmp","s="+s+" "+set
                                +" "+commentAdapter.no_more_comments_next.containsKey(timestamp_comment.getTimestamp()));
                        if(s)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("isFromStartingSign",output+" lm="+lm.getTime()+" ts="+ts+" "+timestamp_comment.getComment());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        else
        {
            Log.i("nsodha",head_comment.getComment_id());
        }

        return query;

    }
    public Query getQueryForAgreeWithHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }

        return query;

    }
    public Query getQueryForDisagreeWithHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }

        return query;

    }
    public Query getQueryForQuestionWithHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        return query;
    }
    public Query getQueryForAnswerWithHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        return query;
    }

    public Query getQueryForAllCommentTypeForNewHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);

        Log.i("beforeCommentx","b="+(beforeComment !=null));
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",afterComment.getDateStr()+" "+afterComment.getComment()+" "
                    +afterComment.getCreatedDate().getTime()+" "+head_comment.getComment_id());
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereGreaterThan("sjkkdhja",afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        else if(beforeComment !=null)
        {

            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", beforeComment.getCreatedDate())
                    .toString();
            Log.i("beforeCommentx",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();


            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);

                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        Log.i("getCommentsUnderTmeStmp","s="+s+" "+set
                                +" "+commentAdapter.no_more_comments_next.containsKey(timestamp_comment.getTimestamp()));
                        if(s)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("isFromStartingSign",output+" lm="+lm.getTime()+" ts="+ts+" "+timestamp_comment.getComment());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        else
        {
            Log.i("nsodha",head_comment.getComment_id());
        }

        return query;

    }
    public Query getQueryForAgreeForNewHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }

        return query;

    }
    public Query getQueryForDisagreeForNewHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }

        return query;

    }
    public Query getQueryForQuestionForNewHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        return query;
    }
    public Query getQueryForAnswerForNewHeadComment(Comment timestamp_comment)
    {

        if(commentAdapter==null)
        {
            this.commentAdapter=((CurrentTopicForConvo)currentTopicForConvo).getCommentAdapter();
        }
        Query query=CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        if(afterComment!=null)
        {
            android.text.format.DateFormat df = new android.text.format.DateFormat();
            String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                    .toString();
            Log.i("afterComment",output);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(beforeComment !=null)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .whereLessThan(OrgFields.USER_CREATED_DATE, beforeComment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }
        else if(timestamp_comment.isFromStartingSign())
        {

            commentAdapter.starting_timestamp=timestamp_comment.getTimestamp();

            if(currentTopicForConvo.getArguments()!=null)
            {

                if(currentTopicForConvo.getArguments()
                        .containsKey(CurrentTopicForConvo.SET))
                {
                    int set=currentTopicForConvo.getArguments()
                            .getInt(CurrentTopicForConvo.SET);
                    commentAdapter.starting_set_number=set;
                    if(currentTopicForConvo.getArguments().containsKey(OrgFields.NO_MORE_PREV_COMMENTS))
                    {
                        boolean s=currentTopicForConvo.getArguments().getBoolean(OrgFields.NO_MORE_PREV_COMMENTS);
                        if(s==false)
                        {
                            commentAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                        }
                    }
                }

            }



            Date lm=new LocalDateTime()
                    .withYear(timestamp_comment.getYear())
                    .withMonthOfYear(timestamp_comment.getMonth())
                    .withDayOfMonth(timestamp_comment.getDay())
                    .withHourOfDay(timestamp_comment.getHour())
                    .withMinuteOfHour(timestamp_comment.getMin())
                    .withSecondOfMinute(timestamp_comment.getSec()).toDate();

            if(currentTopicForConvo.getArguments().containsKey(CurrentTopicForConvo.TSECONDS))
            {
                long ts=currentTopicForConvo.getArguments().getLong(CurrentTopicForConvo.TSECONDS);
                android.text.format.DateFormat df =
                        new android.text.format.DateFormat();
                String output=df.format("yyyy-MM-dd hh:mm:ss a",
                                lm)
                        .toString();
                Log.i("shareDayConvo","sd1 output="+output+" "+lm.getTime());

                Log.i("getCommentsUnderTmeStmp",output
                        +" conversation_id="+conversation_id
                        +" h="+head_comment.getComment_id()+" "+lm.getTime());

                query= CloudWorker.getLetsTalkComments()
                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                        .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                        .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
            }



        }
        return query;
    }



}
