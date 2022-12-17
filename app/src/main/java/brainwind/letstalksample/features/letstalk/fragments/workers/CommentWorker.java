package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.datatransport.runtime.dagger.multibindings.ElementsIntoSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.workers.headcomments.HeadCommentWorker;

public class CommentWorker extends CurrentCommentsView implements CommentCommunications
{

    public static final String CONVO_ID = "param1";
    public static final String TITLE = "param2";
    public static final String IS_STANDALONE = "param3";
    public static final String TIMEOFFSET = "param4";
    public static final String STARTING_SIGNATURE = "param5";

    CommentAdapter commentAdapter;
    Fragment currentTopicFragment;
    String conversation_id="";
    private int last;



    public CommentWorker(String conversation_id,Fragment currentTopicFragment) {
        super(conversation_id,currentTopicFragment);
        this.currentTopicFragment=currentTopicFragment;
        setCurrentTopicForConvo(currentTopicFragment);
        Log.i("CommentWorker","Constructor "+(currentTopicFragment!=null)+" "+(getCurrentTopicFragment()!=null));
        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicFragment);
            checkCommentListKeyBoard();
        }
        this.conversation_id=conversation_id;
    }

    public Fragment getCurrentTopicFragment() {
        return currentTopicFragment;
    }

    public void setCurrentTopicFragment(Fragment currentTopicFragment) {
        this.currentTopicFragment = currentTopicFragment;
    }

    public CommentAdapter getCommentAdapter() {
        return commentAdapter;
    }

    public void setCommentAdapter(CommentAdapter commentAdapter) {
        this.commentAdapter = commentAdapter;
    }

    public String getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    @Override
    public void TryLoadHeadComment()
    {
        getHeadComment();
        Log.i("TryLoadHeadComment","started");
    }

    HeadCommentWorker headCommentWorker=null;
    @Override
    public void ShowMoreHeadComments()
    {

        if(headCommentWorker==null)
        {
            headCommentWorker=new HeadCommentWorker(CommentWorker.this);
        }

        headCommentWorker.LoadMoreHeadComments();

    }

    @Override
    public void ChangeHeadComment(Comment headcomment)
    {

        this.showHeadComment(headcomment,this);
    }

    @Override
    public void onFailureToFetchTimestamps(Comment headcomment, String errorMessage)
    {

        Log.i("fexcshgak",errorMessage);
        new MaterialDialog.Builder(getCurrentTopicFragment().getActivity())
                .title("Oops")
                .content("We could not connect to get timestamps, "+errorMessage)
                .positiveText("Try Again")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getHeadComment();
                    }
                })
                .negativeText("Change head comment")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        ShowMoreHeadComments();

                    }
                })
                .show();
    }

    @Override
    public void onFailureToFetchTimestampComments(Comment headcomment,String errorMessage,String timestamp,int workerID)
    {

        Log.i("FalreTFthTmestmpCmmnts","onFailureToFetchTimestampComments workerID="+workerID+" "+errorMessage);
        commentAdapter.no_more_comments_next.clear();
        commentAdapter.no_more_comments_previous.clear();
        if(comment_list.isComputingLayout()==false)
        {
            commentAdapter.notifyDataSetChanged();
        }
        else
        {
            comment_list.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            commentAdapter.notifyDataSetChanged();
                        }
                    });
        }
        new MaterialDialog.Builder(getCurrentTopicFragment().getActivity())
                .title("Oops")
                .content("Could not get messages for "+headcomment.getDateStr()+" "+errorMessage)
                .positiveText("Try again")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(workerID==CommentReader.INITIAL)
                        {
                            try {
                                initialComments.getCommentsForTimestampCommentType(headcomment,timestamp,comment_filter.getSelectedItemPosition());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(workerID==CommentReader.PAGINATION_PREV)
                        {
                            try {
                                int timestamp_pos=-1;
                                for(int i=0;i<commentAdapter.commentListUnderHeadComment.size();i++)
                                {

                                    Comment comment1=commentAdapter.commentListUnderHeadComment.get(i);
                                    if(comment1.getTimestamp().equals(timestamp)&comment1.isIs_timestamp()&timestamp_pos==-1)
                                    {
                                        timestamp_pos=i;
                                        break;
                                    }

                                }
                                getCommentsBeforeComment(headcomment,timestamp_pos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(workerID==CommentReader.PAGINATION_NEXT)
                        {
                            try {
                                int timestamp_pos=-1;
                                for(int i=0;i<commentAdapter.commentListUnderHeadComment.size();i++)
                                {

                                    Comment comment1=commentAdapter.commentListUnderHeadComment.get(i);
                                    if(comment1.getTimestamp().equals(timestamp)&comment1.isIs_timestamp()&timestamp_pos==-1)
                                    {
                                        timestamp_pos=i;
                                        break;
                                    }

                                }
                                getCommentsAfterComment(getAfterComment(),timestamp_pos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                })
                .negativeText("Never mind")
                .show();

    }

    AfterComments afterComments;
    public void getCommentsAfterComment(Comment afterComment, int timestamp_pos)
    {

        boolean nsma=commentAdapter.timestamps_comments.containsKey(afterComment.getTimestamp());
        Log.i("getCommentsAfterComment",afterComment.getDateTimeStr()+" nsma="+nsma);

        if(commentAdapter.timestamps_comments.containsKey(afterComment.getTimestamp())==false)
        {

            if(initialComments==null)
            {
                initialComments=new InitialComments(conversation_id,currentTopicFragment);

            }


            try {
                initialComments.setCurrentTopicForConvo(currentTopicForConvo);
                initialComments.getCommentsForTimestampCommentType(getHead_comment(),afterComment.getTimestamp()
                        ,comment_filter.getSelectedItemPosition());
            } catch (Exception e) {
                Log.i("getCommentsAfterComment",e.getMessage());
                e.printStackTrace();
            }

        }
        else
        {
            if(afterComments==null)
            {
                afterComments=new AfterComments(getConversation_id(),currentTopicForConvo);
            }

            afterComments.setCurrentTopicForConvo(currentTopicForConvo);
            afterComments.setAfterComment(afterComment);
            try {
                afterComments.getCommentsForTimestampCommentTypeAfter(getHead_comment(),afterComment.getTimestamp(),comment_filter.getSelectedItemPosition(),afterComment);
            } catch (Exception e) {
                Log.i("getCommentsAfterComment","commmentAdapter="+(commentAdapter!=null));
                Log.i("getCommentsAfterComment",e.getMessage());
                e.printStackTrace();

            }
        }

    }

    @Override
    public void onSuccessfulFetchTimestampComments(QuerySnapshot queryDocumentSnapshots,String timestampx, int workerID)
    {

        commentAdapter.no_more_comments_previous.clear();
        commentAdapter.no_more_comments_next.clear();
        commentAdapter.is_loading=false;
        Log.i("ScsfulFtchTmestmpCments","onSuccessfulFetchTimestampComments workerID="+workerID+" "+queryDocumentSnapshots.isEmpty()+" "+timestampx);
        if(queryDocumentSnapshots.isEmpty()==false)
        {

            if(queryDocumentSnapshots.getDocuments().size()<10)
            {
                if(workerID==CommentReader.PAGINATION_PREV)
                {
                    commentAdapter.no_more_comments_previous.put(timestampx,true);
                    commentAdapter.notifyDataSetChanged();
                }
                if(workerID==CommentReader.PAGINATION_NEXT||workerID==CommentReader.INITIAL
                        ||workerID==CommentReader.LINK_RESUME)
                {
                    commentAdapter.no_more_comments_next.put(timestampx,true);
                    commentAdapter.notifyDataSetChanged();
                }
            }
            DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);
            Comment comment=new Comment(documentSnapshot);
            final String timestamp=timestampx;
            Log.i("ScsfulFtchTmestmpCments",timestamp);

            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {

                            int timestamp_pos=-1;
                            int last_pos_comment_in_timestamp=-1;

                            for(int i=0;i<commentAdapter.commentListUnderHeadComment.size();i++)
                            {

                                Comment comment1=commentAdapter.commentListUnderHeadComment.get(i);
                                Comment comment=new Comment(documentSnapshot);
                                if(commentAdapter.started_loading_older_coments.containsKey(comment.getTimestamp()))
                                {
                                    commentAdapter.started_loading_older_coments.remove(comment.getTimestamp());
                                }
                                if(commentAdapter.started_loading_next_coments.containsKey(comment.getTimestamp()))
                                {
                                    commentAdapter.started_loading_next_coments.remove(comment.getTimestamp());
                                }

                                if(workerID==CommentReader.PAGINATION_PREV&comment1.getTimestamp().equals(timestamp)&comment1.isIs_timestamp())
                                {
                                    Log.i("ossaxj","t="+timestamp+" t2="+comment1.getTimestamp()+" "+comment1.isIs_timestamp()
                                            +" "+comment1.getTimestamp().equals(timestamp)+" i="+i);
                                }
                                if(comment1.getTimestamp().equals(timestamp)&comment1.isIs_timestamp()&timestamp_pos==-1)
                                {
                                    Log.i("sjdugas","i="+i);
                                    if(workerID==CommentReader.INITIAL||workerID==CommentReader.PAGINATION_PREV)
                                    {
                                        timestamp_pos=i;
                                        if(workerID==CommentReader.INITIAL)
                                        {
                                            addComments(queryDocumentSnapshots,timestamp_pos,last_pos_comment_in_timestamp,workerID);
                                            break;
                                        }

                                    }

                                }
                                else if(comment1.getTimestamp().equals(timestamp)&timestamp_pos
                                >-1)
                                {
                                    last_pos_comment_in_timestamp=i;
                                }
                                else if(timestamp_pos
                                        >-1)
                                {
                                    break;
                                }

                            }

                            if(workerID==CommentReader.PAGINATION_NEXT
                                    ||workerID==CommentReader.PAGINATION_PREV
                                    ||workerID==CommentReader.LINK_RESUME
                                    )
                            {
                                addComments(queryDocumentSnapshots,timestamp_pos,last_pos_comment_in_timestamp,workerID);
                            }


                        }
                    });

        }
        else
        {

            commentAdapter.is_loading=false;
            commentAdapter.notifyDataSetChanged();
            Toast.makeText(getCurrentTopicForConvo().getActivity(), "No comments under this head comment", Toast.LENGTH_SHORT).show();



        }

    }

    private void addComments(QuerySnapshot queryDocumentSnapshots, int positionOfTimeStamp,
                             int last_pos_comment_in_timestamp,int workerID)
    {

        commentAdapter.is_loading=false;
        int start_add=0;
        if(positionOfTimeStamp+1<commentAdapter.commentListUnderHeadComment.size()&positionOfTimeStamp>-1)
        {
            //Initial load add after the timestamp
            if(workerID==CommentReader.INITIAL||workerID==CommentReader.PAGINATION_PREV)
            {
                start_add=positionOfTimeStamp+1;
            }
            if(workerID==CommentReader.PAGINATION_NEXT)
            {
                if(last_pos_comment_in_timestamp+1<commentAdapter.commentListUnderHeadComment.size())
                {
                    start_add=last_pos_comment_in_timestamp;
                }
                else
                {
                    start_add=commentAdapter.commentListUnderHeadComment.size();
                }
            }
        }
        else
        {
            //then add at the end of the list
            start_add=commentAdapter.commentListUnderHeadComment.size();
        }

        final int yj=start_add;
        Log.i("msudjaps",queryDocumentSnapshots.getDocuments().size()+" documents workerID="+workerID+" "+positionOfTimeStamp);
        if(workerID==CommentReader.INITIAL||workerID==CommentReader.PAGINATION_PREV)
        {

            DocumentSnapshot documentSnapshotx=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.getDocuments().size()-1);
            Comment commentx=new Comment(documentSnapshotx);
            Comment commentxs=commentAdapter.commentListUnderHeadComment.get(positionOfTimeStamp);
            if(commentxs.getComment().isEmpty())
            {
                Log.i("ydusxefas","timestamp is isEmpty "+positionOfTimeStamp+" last="+commentx.getComment());
            }
            else
            {
                Log.i("ydusxefas","timestamp is not isEmpty "+positionOfTimeStamp+" "+commentxs.getComment()+" last="+commentx.getComment());
                if(positionOfTimeStamp+1<commentAdapter.commentListUnderHeadComment.size())
                {
                    commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,commentx);
                    commentAdapter.commentListUnderHeadComment.add(positionOfTimeStamp+1,commentxs);
                }
            }
            commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,commentx);

            for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++)
            {

                DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(i);
                Comment comment=new Comment(documentSnapshot);
                Log.i("losjdhka",comment.getComment()+" "+comment.getTimeStr()+" "+queryDocumentSnapshots.getDocuments().size());
                Log.i("mdiahdla",comment.getTimestamp()
                        +" "+commentAdapter.started_loading_older_coments.containsKey(comment.getTimestamp())
                        +" "+commentAdapter.started_loading_next_coments.containsKey(comment.getTimestamp()));

                if(commentAdapter.started_loading_older_coments.containsKey(comment.getTimestamp()))
                {
                    commentAdapter.started_loading_older_coments.remove(comment.getTimestamp());
                }
                if(commentAdapter.started_loading_next_coments.containsKey(comment.getTimestamp()))
                {
                    commentAdapter.started_loading_next_coments.remove(comment.getTimestamp());
                }
                if(i==queryDocumentSnapshots.getDocuments().size()-1)
                {
                    continue;
                }

                if(commentAdapter.timestamps_comments.containsKey(comment.getTimestamp()))
                {
                    ArrayList<Comment> ystd=commentAdapter.timestamps_comments.get(comment.getTimestamp());
                    ystd.add(comment);
                    commentAdapter.timestamps_comments.put(comment.getTimestamp(),ystd);
                }
                else
                {
                    ArrayList<Comment> ystd=new ArrayList<Comment>();
                    ystd.add(comment);
                    commentAdapter.timestamps_comments.put(comment.getTimestamp(),ystd);
                }


                if(i==queryDocumentSnapshots.getDocuments().size()-1)
                {
                    commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,comment);
                    Log.i("msudjaps","1 i="+i+comment.getTimestamp()+" yj="+yj+" "+positionOfTimeStamp);
                }
                else if(start_add<commentAdapter.commentListUnderHeadComment.size())
                {
                    if(i==queryDocumentSnapshots.getDocuments().size()-1)
                    {
                        commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,comment);
                        Log.i("msudjaps","2 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                                +" "+comment.getDateTimeStr()+" yj="+yj+" "+positionOfTimeStamp);
                    }
                    else
                    {
                        commentAdapter.commentListUnderHeadComment.add(start_add,comment);
                        Log.i("msudjaps","3 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                                +" "+comment.getDateTimeStr()+" yj="+yj+" "+positionOfTimeStamp);
                    }
                }
                else
                {
                    commentAdapter.commentListUnderHeadComment.add(comment);
                    Log.i("msudjaps","4 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                            +" "+comment.getDateTimeStr()+" yj="+yj+" "+positionOfTimeStamp);
                }

            }


            getCurrentTopicForConvo().getActivity()
                    .runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(marker_filter_area==null)
                            {
                                marker_filter_area=getRootView().findViewById(R.id.marker_filter_area);
                            }
                            marker_filter_area.setVisibility(View.VISIBLE);
                            commentAdapter.currentLoadingTimestampLabel="";
                            commentAdapter.notifyDataSetChanged();
                            if(comment_list==null)
                            {
                                comment_list=getRootView().findViewById(R.id.comment_list);
                            }
                            boolean ms=false;
                            if(initialComments!=null)
                            {
                                if(initialComments.isScrolled()==false&workerID==CommentReader.INITIAL)
                                {
                                    ms=true;
                                }
                            }

                            if(yj<commentAdapter.commentListUnderHeadComment.size()&ms)
                            {
                                comment_list.scrollToPosition(yj);
                                comment_list.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        comment_list.smoothScrollToPosition(yj);
                                        initialComments.setScrolled(true);

                                    }
                                },200);

                            }

                            Log.i("mysgad","1");
                            listenForTodaysComments();

                        }
                    });

        }



    }

    InitialComments initialComments;
    RecyclerView comment_list;
    @Override
    public void TimeStampsForHeadComment(Comment headcomment, ArrayList<Comment> timestampList) {

        Log.i("TimeStampsForHdComment",headcomment.getConversation_id()+" "+headcomment.getComment_id());
        Log.i("TimeStampsForHdComment","timestampList.isEmpty()="+timestampList.isEmpty());
        commentAdapter.commentListUnderHeadComment.clear();
        commentAdapter.no_more_comments_next.clear();
        commentAdapter.no_more_comments_previous.clear();
        if(timestampList.isEmpty()==false)
        {

            if(comment_filter!=null)
            {
                comment_filter.setVisibility(View.VISIBLE);
            }
            if(marker_filter_area!=null)
            {
                marker_filter_area.setVisibility(View.VISIBLE);
            }
            if(initialComments==null)
            {
                initialComments=new InitialComments(conversation_id,currentTopicFragment);

            }

            if(commentAdapter==null)
            {
                commentAdapter=new CommentAdapter(getCurrentTopicFragment());
            }

            commentAdapter.commentListUnderHeadComment.clear();
            commentAdapter.commentListUnderHeadComment.addAll(timestampList);

            if(comment_list==null)
            {
                comment_list=getRootView().findViewById(R.id.comment_list);
                comment_list.setLayoutManager(new LinearLayoutManager(getCurrentTopicForConvo().getActivity()));
                comment_list.setAdapter(commentAdapter);
            }

            commentAdapter.SignalLoadingLastComment();
            commentAdapter.notifyDataSetChanged();
            last=commentAdapter.commentListUnderHeadComment.size()-1;
            comment_list.scrollToPosition(last);
            Log.i("sjdugas","last="+last);
            comment_list.postDelayed(new Runnable() {
                @Override
                public void run() {

                    comment_list.smoothScrollToPosition(commentAdapter.commentListUnderHeadComment.size()-1);
                    Comment timestamp_comment=commentAdapter.commentListUnderHeadComment.get(
                            commentAdapter.commentListUnderHeadComment.size()-1);
                    try {
                        if(comment_filter==null)
                        {
                            comment_filter=getRootView().findViewById(R.id.comment_filter);
                        }
                        initialComments.setCurrentTopicForConvo(currentTopicForConvo);
                        initialComments.getCommentsForTimestampCommentType(getHead_comment(),timestamp_comment.getTimestamp()
                                ,comment_filter.getSelectedItemPosition());
                    } catch (Exception e) {
                        Log.i("onshjj","e="+e.getMessage());
                    }

                }
            },200);
        }
        else
        {

            if(comment_filter!=null)
            {
                comment_filter.setVisibility(View.GONE);
            }
            if(marker_filter_area!=null)
            {
                marker_filter_area.setVisibility(View.GONE);
            }
            commentAdapter.is_loading=false;
            commentAdapter.notifyDataSetChanged();
            Toast.makeText(getCurrentTopicForConvo().getActivity(), "No comments under this head comment", Toast.LENGTH_SHORT).show();
        }



    }

    public void getHeadComment()
    {

        LoadingHeadCommentView();
        Query query=getHeadCommentQuery(currentTopicForConvo);

        query.limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        ShowHeadCommentError(e.getMessage(),CommentWorker.this);

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(queryDocumentSnapshots.isEmpty()==false)
                        {
                            DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);
                            Comment headcomment=new Comment(documentSnapshot);
                            showHeadComment(headcomment,CommentWorker.this);
                        }
                        else
                        {
                            NoHeadCommentView(CommentWorker.this);
                        }

                    }
                });

    }


    ConvoTimeStamps convoTimeStamps;
    @Override
    public void showHeadComment(Comment headcomment, CommentWorker commentWorkerinstance) {
        super.showHeadComment(headcomment, commentWorkerinstance);
        head_comment=headcomment;
        Log.i("showHeadComment","showHeadComment "+headcomment.getComment_id());
        Log.i("CommentWorker","showHeadComment "+(currentTopicFragment!=null)+" "+(getCurrentTopicFragment()!=null));
        if(listener!=null)
        {
            Log.i("mysgad","2");
            //listener.remove();
        }
        if(convoTimeStamps==null)
        {
            convoTimeStamps=new ConvoTimeStamps(conversation_id,currentTopicFragment);
        }
        if(initialComments!=null)
        {
            initialComments.setScrolled(false);
        }

        if(getCurrentTopicForConvo().getActivity()!=null)
        {
            CommentListener commentListener=(CommentListener) getCurrentTopicForConvo().getActivity();
            if(commentListener!=null)
            {
                commentListener.foundHeadComment(headcomment);
            }
        }

        convoTimeStamps.setHead_comment(headcomment);
        commentAdapter.no_more_comments_previous.clear();
        commentAdapter.no_more_comments_next.clear();
        commentAdapter.commentListUnderHeadComment.clear();
        commentAdapter.timestamps_comments.clear();
        commentAdapter.started_loading_older_coments.clear();
        commentAdapter.started_loading_next_coments.clear();
        commentAdapter.timestamps_comments.clear();
        commentAdapter.is_loading=true;
        commentAdapter.notifyDataSetChanged();
        if(comment_filter!=null)
        {
            comment_filter.setVisibility(View.GONE);
        }
        if(marker_filter_area!=null)
        {
            marker_filter_area.setVisibility(View.GONE);
        }
        convoTimeStamps.getTimestampsForHeadComment(CommentWorker.this);
        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicFragment);
        }
        if(comment_list!=null)
        {
            if(comment_list.getLayoutManager()==null)
            {
                comment_list.setLayoutManager(new LinearLayoutManager(getCurrentTopicForConvo().getActivity()));
                comment_list.setAdapter(commentAdapter);
            }
        }
        else
        {
            comment_list=getRootView().findViewById(R.id.comment_list);
            comment_list.setLayoutManager(new LinearLayoutManager(getCurrentTopicForConvo().getActivity()));
            comment_list.setAdapter(commentAdapter);
        }

        Log.i("showHeadComment",convoTimeStamps.getConversation_id()+" ");

    }


    public void onResume()
    {

        Log.i("conResume","onResume");
        if(initialComments!=null)
        {
            Log.i("conResume","onResume last="+last);
            if(last>0)
            {
                comment_list.scrollToPosition(last);
                comment_list.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        comment_list.smoothScrollToPosition(last);

                    }
                },200);

            }
        }

    }


    private BeforeComments beforeComments;
    public void getCommentsBeforeComment(Comment comment, int position)
    {

        if(beforeComments==null)
        {
            if(comment_filter==null)
            {
                comment_filter=getRootView().findViewById(R.id.comment_filter);
            }

            beforeComments=new BeforeComments(conversation_id,currentTopicFragment);
            beforeComments.setBeforeComment(comment);
            beforeComments.beforeComment=comment;
            beforeComments.setCurrentTopicForConvo(currentTopicForConvo);




        }

        try {
            beforeComments.getCommentsForTimestampCommentTypeBefore(getHead_comment(),comment.getTimestamp()
                    ,comment_filter.getSelectedItemPosition(),comment);
        } catch (Exception e) {

        }

    }


    public void getHeadComment(String headcomment_id)
    {

        CloudWorker.getLetsTalkComments()
                .document(headcomment_id)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        ShowHeadCommentError(e.getMessage(),CommentWorker.this);

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot!=null)
                        {
                            Comment comment=new Comment(documentSnapshot);
                            setHead_comment(comment);
                            showHeadComment(comment,CommentWorker.this);
                        }

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {



                    }
                });

    }


    public void listenForTodaysComments()
    {

        Memory memory=new Memory(currentTopicForConvo.getActivity());
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);
        if(jnm.isEmpty()==false)
        {

            long localtimeoffset=Long.parseLong(jnm);
            long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;

            org.joda.time.LocalDateTime localDateTime
                    =new org.joda.time.LocalDateTime(estimatedServerTimeMs);


            String timestamp=localDateTime.getYear()+"-"+localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth();
            Log.i("listenForTodaysComments",timestamp);

            if(commentAdapter.commentListUnderHeadComment.size()>0&commentAdapter.timestamps_comments.containsKey(timestamp))
            {
                int l=commentAdapter.commentListUnderHeadComment.size()-1;
                listenForTodaysCommentsAfterLastComment(commentAdapter.commentListUnderHeadComment.get(l),localDateTime);
            }
            else if(commentAdapter.commentListUnderHeadComment.size()>0)
            {
                int l=commentAdapter.commentListUnderHeadComment.size()-1;
                Comment comment=commentAdapter.commentListUnderHeadComment.get(l);

                if(comment.getTimestamp().equals(timestamp))
                {
                    listenForTodaysCommentsAfterLastComment(comment,localDateTime);
                }
                else
                {
                    listenForTodaysCommentsForDate(localDateTime);
                }

            }
            else
            {
                listenForTodaysCommentsForDate(localDateTime);
            }

        }

    }

    private void listenForTodaysCommentsAfterLastComment(Comment comment, LocalDateTime localDateTime)
    {

        /*
                <item>All</item>
                <item>Agree</item>
                <item>Disagree</item>
                <item>Question</item>
                <item>Answers</item>
         */
        Query query=null;
        //<item>All</item>
        if(comment_filter.getSelectedItemPosition()==0)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Agree</item>
        if(comment_filter.getSelectedItemPosition()==1)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Disagree</item>
        if(comment_filter.getSelectedItemPosition()==2)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Question</item>
        if(comment_filter.getSelectedItemPosition()==3)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Answers</item>
        if(comment_filter.getSelectedItemPosition()==4)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }

        listenToQuery(query);

    }

    private void listenForTodaysCommentsForDate(LocalDateTime localDateTime)
    {

        /*
                <item>All</item>
                <item>Agree</item>
                <item>Disagree</item>
                <item>Question</item>
                <item>Answers</item>
         */
        Query query=null;
        //<item>All</item>
        if(comment_filter.getSelectedItemPosition()==0)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Agree</item>
        if(comment_filter.getSelectedItemPosition()==1)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Disagree</item>
        if(comment_filter.getSelectedItemPosition()==2)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Question</item>
        if(comment_filter.getSelectedItemPosition()==3)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }
        //<item>Answers</item>
        if(comment_filter.getSelectedItemPosition()==4)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
        }

        listenToQuery(query);


    }

    private ListenerRegistration listener;
    HashMap<String,Boolean> rec_isfromcache=new HashMap<String, Boolean>();
    private void listenToQuery(Query query)
    {

        if(query!=null)
        {

            Log.i("listenToQuery","started");
            if(listener!=null)
            {
                //listener.remove();
            }
            listener=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    Log.i("listenToQuery","onEvent");
                    if(value!=null)
                    {

                        if(value.isEmpty()==false)
                        {
                            for(int i=0;i<value.getDocuments().size();i++)
                            {

                                DocumentSnapshot documentSnapshot=value.getDocuments().get(i);
                                final Comment comment=new Comment(documentSnapshot);
                                Log.i("listenToQuery",comment.getComment()+" "+documentSnapshot.getMetadata().isFromCache());
                                if(documentSnapshot.getMetadata().isFromCache())
                                {
                                    comment.setSent(false);
                                }
                                else
                                {
                                    comment.setSent(true);
                                }

                                if(rec_isfromcache.containsKey(comment.getComment_id())==false)
                                {

                                    rec_isfromcache.put(comment.getComment_id(),comment.isSent());
                                    final int mp=commentAdapter.commentListUnderHeadComment.size()-1;
                                    commentAdapter.commentListUnderHeadComment.add(comment);
                                    commentAdapter.notifyItemInserted(mp);
                                    if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                                    {

                                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(comment.getCommentator_uid()))
                                        {
                                            if(i==value.getDocuments().size()-1)
                                            {
                                                comment_list.scrollToPosition(commentAdapter.commentListUnderHeadComment.size()-1);
                                                comment_list.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                       comment_list.smoothScrollToPosition(commentAdapter.commentListUnderHeadComment.size()-1);
                                                    }
                                                },200);



                                            }

                                            CommentListener commentListener=(CommentListener) getCurrentTopicFragment().getActivity();
                                            if(commentListener!=null)
                                            {
                                                commentListener.messageUpdated();
                                            }

                                        }

                                    }

                                }
                                else
                                {

                                    boolean isSent=rec_isfromcache.get(comment.getComment_id());

                                    if(isSent==false&comment.isSent())
                                    {

                                        UserInfoDatabase.databaseWriteExecutor
                                                .execute(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        for(int i=0;i<commentAdapter.commentListUnderHeadComment.size();i++)
                                                        {

                                                            Comment comment1=commentAdapter.commentListUnderHeadComment.get(i);
                                                            if(comment1.getComment_id().equals(comment.getComment_id()))
                                                            {

                                                                final int yk=i;
                                                                commentAdapter.commentListUnderHeadComment.set(i,comment);
                                                                getCurrentTopicFragment().getActivity()
                                                                        .runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                commentAdapter.notifyItemChanged(yk);

                                                                            }
                                                                        });
                                                                break;

                                                            }

                                                        }

                                                    }
                                                });

                                    }

                                }



                            }
                        }

                    }

                }



            });

        }

    }

    boolean isKeyboardShowing=false;
    int last_reading_pos=0;
    private void checkCommentListKeyBoard()
    {
        try {

            comment_list.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {

                            Rect r = new Rect();
                            comment_list.getWindowVisibleDisplayFrame(r);
                            int screenHeight = comment_list.getRootView().getHeight();

                            // r.bottom is the position above soft keypad or device button.
                            // if keypad is shown, the r.bottom is smaller than that before.
                            int keypadHeight = screenHeight - r.bottom;

                            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                                // keyboard is opened
                                if (!isKeyboardShowing) {
                                    isKeyboardShowing = true;
                                    LinearLayoutManager ln=(LinearLayoutManager)comment_list.getLayoutManager();


                                }
                            }
                            else {
                                // keyboard is closed
                                if (isKeyboardShowing) {
                                    isKeyboardShowing = false;
                                    commentAdapter.notifyDataSetChanged();
                                    if(last_reading_pos>0)
                                    {

                                        final int lp=last_reading_pos;
                                        comment_list.scrollToPosition(lp);
                                        comment_list.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                comment_list.smoothScrollToPosition(lp);
                                                Log.i("keyposa","last_reading_pos="+lp);

                                            }
                                        },1000);
                                    }
                                }
                            }

                        }
                    });

            comment_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if(newState==RecyclerView.SCROLL_STATE_IDLE)
                    {
                        LinearLayoutManager lm=(LinearLayoutManager)comment_list.getLayoutManager();
                        last_reading_pos=lm.findLastVisibleItemPosition();
                    }

                }
            });

        }
        catch(Exception exception)
        {

        }
    }

    public RecyclerView getComment_list() {
        if(comment_list==null)
        {
            if(getRootView()!=null)
            {
                comment_list=(RecyclerView) getRootView().findViewById(R.id.comment_list);
            }
        }
        return comment_list;
    }
}
