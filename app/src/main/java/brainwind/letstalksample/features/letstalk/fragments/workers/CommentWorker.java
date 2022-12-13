package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
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

    }

    @Override
    public void onFailureToFetchTimestampComments(Comment headcomment,String errorMessage,int workerID)
    {

        Log.i("onshjj","onFailureToFetchTimestampComments workerID="+workerID+" "+errorMessage);

    }

    @Override
    public void onSuccessfulFetchTimestampComments(QuerySnapshot queryDocumentSnapshots, int workerID)
    {

        Log.i("onshjj","onSuccessfulFetchTimestampComments workerID="+workerID);
        if(queryDocumentSnapshots.isEmpty()==false)
        {

            DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);
            Comment comment=new Comment(documentSnapshot);
            final String timestamp=comment.getTimestamp();

            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {

                            int timestamp_pos=-1;
                            int last_pos_comment_in_timestamp=-1;
                            for(int i=0;i<commentAdapter.commentListUnderHeadComment.size();i++)
                            {

                                Comment comment1=commentAdapter.commentListUnderHeadComment.get(i);

                                if(comment1.getTimestamp().equals(timestamp)&comment1.isIs_timestamp())
                                {
                                    Log.i("sjdugas","i="+i);
                                    if(workerID==CommentReader.INITIAL)
                                    {
                                        timestamp_pos=i;
                                        addComments(queryDocumentSnapshots,timestamp_pos,last_pos_comment_in_timestamp,workerID);
                                        break;
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

    }

    private void addComments(QuerySnapshot queryDocumentSnapshots, int positionOfTimeStamp,
                             int last_pos_comment_in_timestamp,int workerID)
    {

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
        Log.i("msudjaps",queryDocumentSnapshots.getDocuments().size()+" documents");
        if(workerID==CommentReader.INITIAL)
        {

            DocumentSnapshot documentSnapshotx=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.getDocuments().size()-1);
            Comment commentx=new Comment(documentSnapshotx);
            commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,commentx);

            for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++)
            {

                if(i==queryDocumentSnapshots.getDocuments().size()-1)
                {
                    continue;
                }
                DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(i);
                Comment comment=new Comment(documentSnapshot);
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
                    commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,comment);
                    Log.i("msudjaps","1 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                            +" "+comment.getDateStr()+" yj="+yj+" "+positionOfTimeStamp);
                }
                else if(start_add<commentAdapter.commentListUnderHeadComment.size())
                {
                    if(i==queryDocumentSnapshots.getDocuments().size()-1)
                    {
                        commentAdapter.commentListUnderHeadComment.set(positionOfTimeStamp,comment);
                        Log.i("msudjaps","2 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                                +" "+comment.getDateStr()+" yj="+yj+" "+positionOfTimeStamp);
                    }
                    else
                    {
                        commentAdapter.commentListUnderHeadComment.add(start_add,comment);
                        Log.i("msudjaps","3 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                                +" "+comment.getDateStr()+" yj="+yj+" "+positionOfTimeStamp);
                    }
                }
                else
                {
                    commentAdapter.commentListUnderHeadComment.add(comment);
                    Log.i("msudjaps","4 i="+i+" day="+comment.getDay()+" month="+comment.getMonth()+" year="+comment.getYear()
                            +" "+comment.getDateStr()+" yj="+yj+" "+positionOfTimeStamp);
                }

            }


            getCurrentTopicForConvo().getActivity()
                    .runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            commentAdapter.notifyDataSetChanged();
                            if(comment_list==null)
                            {
                                comment_list=getRootView().findViewById(R.id.comment_list);
                            }
                            if(yj<commentAdapter.commentListUnderHeadComment.size())
                            {
                                comment_list.scrollToPosition(yj);
                                comment_list.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        comment_list.smoothScrollToPosition(yj);
                                    }
                                },200);
                            }

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
        Log.i("showHeadComment","showHeadComment");
        Log.i("CommentWorker","showHeadComment "+(currentTopicFragment!=null)+" "+(getCurrentTopicFragment()!=null));
        if(convoTimeStamps==null)
        {
            convoTimeStamps=new ConvoTimeStamps(conversation_id,currentTopicFragment);
            convoTimeStamps.setHead_comment(headcomment);
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

            commentAdapter.is_loading=true;
            commentAdapter.notifyDataSetChanged();

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


}
