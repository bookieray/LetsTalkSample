package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.bookie_activity.BookieActivity;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDao;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NetworkUtils;
import brainwind.letstalksample.features.letstalk.LetsTalkManageEach;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.Conversation;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class CommentCreator
{

    Activity activity;
    BookieActivity bookieActivity;
    Fragment current_topic_frag;
    Comment head_comment;
    Comment replying_comment;

    public CommentCreator(Activity activity,Fragment current_topic_frag,
                          BookieActivity bookieActivity) {
        this.activity = activity;
        this.bookieActivity=bookieActivity;
        this.current_topic_frag=current_topic_frag;
    }

    public Comment getHead_comment() {
        return head_comment;
    }

    public void setHead_comment(Comment head_comment) {
        this.head_comment = head_comment;
    }

    public Comment getReplying_comment() {
        return replying_comment;
    }

    public void setReplying_comment(Comment replying_comment) {
        this.replying_comment = replying_comment;
    }

    /*
        Dialog to decide whether the comment is a:
        • Agree
        • Disagree
        • Question
        If it is a reply then notify that this is in question to that comment
        and not head comment.
        Hence a boolean field to track
        if the current pending comment is replying to another comment.
    */
    private void AskIfAgreeDisagreeQuestion(boolean under_head_comment)
    {

        String hdf="Where do you stand?";
        String njk="";
        if(replying_comment!=null)
        {
            hdf="What is your comment to "+replying_comment.getCommentator_name();
            if(replying_comment.getComment().length()>=15)
            {
                njk='"'+replying_comment.getComment().substring(0,14)+"..."+'"';
            }
            else
            {
                njk='"'+replying_comment.getComment()+'"';
            }
        }
        else if(under_head_comment&head_comment!=null)
        {
            hdf="What is your comment to "+head_comment.getCommentator_name();
            if(head_comment.getComment().length()>=15)
            {
                njk='"'+head_comment.getComment().substring(0,14)+"..."+'"';
            }
            else
            {
                njk='"'+head_comment.getComment()+'"';
            }
        }
        else
        {
            hdf="What is your comment to "+bookieActivity.getOrgname();
            njk='"'+bookieActivity.getActivity_title()+'"';
        }

        if(replying_comment!=null)
        {
            if(replying_comment.getComment_type()== Comment.QUESTION)
            {
                MaterialDialog js=new MaterialDialog.Builder(this.activity)
                        .title(hdf)
                        .content(njk)
                        .contentColor(this.activity.getResources().getColor(R.color.black))
                        .positiveText("Answer")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.ANSWER);

                            }
                        })
                        .negativeText("Question")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.QUESTION);

                            }
                        })
                        .build();

                js.show();
            }
            else
            {

                MaterialDialog js=new MaterialDialog.Builder(this.activity)
                        .title(hdf)
                        .content(njk)
                        .contentColor(this.activity.getResources().getColor(R.color.black))
                        .positiveText("Agree")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.AGREES);

                            }
                        })
                        .negativeText("Disagree")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.DISAGREES);

                            }
                        })
                        .neutralText("Question")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.QUESTION);

                            }
                        })
                        .build();

                js.show();

            }
        }
        else
        {

            if(under_head_comment&head_comment!=null)
            {
                if(head_comment.getComment_type()==Comment.QUESTION)
                {
                    MaterialDialog js=new MaterialDialog.Builder(this.activity)
                            .title(hdf)
                            .content(njk)
                            .contentColor(this.activity.getResources().getColor(R.color.black))
                            .positiveText("Answer")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    submitCommentToCloud(under_head_comment,Comment.ANSWER);

                                }
                            })
                            .negativeText("Question")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    submitCommentToCloud(under_head_comment,Comment.QUESTION);

                                }
                            })
                            .build();

                    js.show();
                }
                else
                {
                    MaterialDialog js=new MaterialDialog.Builder(this.activity)
                            .title(hdf)
                            .content(njk)
                            .contentColor(this.activity.getResources().getColor(R.color.black))
                            .positiveText("Agree")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    submitCommentToCloud(under_head_comment,Comment.AGREES);

                                }
                            })
                            .negativeText("Disagree")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    submitCommentToCloud(under_head_comment,Comment.DISAGREES);

                                }
                            })
                            .neutralText("Question")
                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    submitCommentToCloud(under_head_comment,Comment.QUESTION);

                                }
                            })
                            .build();

                    js.show();
                }
            }
            else
            {

                MaterialDialog js=new MaterialDialog.Builder(this.activity)
                        .title(hdf)
                        .content(njk)
                        .contentColor(this.activity.getResources().getColor(R.color.black))
                        .positiveText("Agree")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.AGREES);

                            }
                        })
                        .negativeText("Disagree")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.DISAGREES);

                            }
                        })
                        .neutralText("Question")
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                submitCommentToCloud(under_head_comment,Comment.QUESTION);

                            }
                        })
                        .build();

                js.show();

            }

        }

        Snackbar.make(this.activity.findViewById(R.id.rootview)
                ,"Touch on the black area to cancel",Snackbar.LENGTH_SHORT).show();


    }

    //Send Under Current Comment
    private void SendCommentUnderHeadComment()
    {
        //ask them if they agree or disagree with the head comment
        AskIfAgreeDisagreeQuestion(true);
    }

    //sends a comment
    private void SendComment()
    {

        if(replying_comment!=null)
        {
            AskIfAgreeDisagreeQuestion(true);
        }
        else if(head_comment!=null)
        {
            String njk="";
            if(head_comment.getComment().length()>=5)
            {
                njk='"'+head_comment.getComment().substring(0,4)+"..."+'"';
            }
            else
            {
                njk='"'+head_comment.getComment()+'"';
            }
            new MaterialDialog.Builder(this.activity)
                    .title("Keeping the convo organized")
                    .content("Are you responding to "
                            +head_comment.getCommentator_name()+"'s comment"
                            +" "+njk)
                    .contentColor(this.activity.getResources().getColor(R.color.black))
                    .positiveText("Yes")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            //Send Under Current Comment
                            SendCommentUnderHeadComment();

                        }
                    })
                    .negativeText("Starting your own topic")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            /*
                            Dialog to decide whether the comment is a:
                            • Agree
                            • Disagree
                            • Question
                            If it is a reply then notify that this is in question to that comment
                            and not head comment.
                            Hence a boolean field to track
                            if the current pending comment is replying to another comment.
                             */
                            AskIfAgreeDisagreeQuestion(false);

                        }
                    })
                    .neutralText("Never mind")
                    .show();
        }
        else
        {

            //send comment as 'a new topic'
            AskIfAgreeDisagreeQuestion(false);

        }

    }

    public void startSendComment()
    {

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        EmojiconEditText emojicon_edit_text=(EmojiconEditText)
                activity.findViewById(R.id.emojicon_edit_text);
        if(emojicon_edit_text.getText().toString().isEmpty()==false)
        {
            //15.1.1:Check if the user is signed in – if false then 15.1.2
            if(firebaseUser!=null)
            {
                //check if there is any internet connection
                if(NetworkUtils.isConnectedNetwork(activity))
                {
                    Snackbar.make(activity.findViewById(R.id.rootview),
                            "No internet Connection"
                            ,Snackbar.LENGTH_SHORT).show();
                    //send comment
                    SendComment();

                }
                else
                {
                    //send comment
                    SendComment();


                }
            }
            else
            {

            }
        }
        else
        {



        }

    }

    private void submitCommentToCloud(boolean under_head_comment,int comment_type)
    {

        //after a successful submission of the comment
        //if it is reply comment operation set the replying_comment=null
        //If it is a reply comment
        //OrgFields.IS_RESPONSE_COMMENT
        /*
        • IN_RESPONSE_AGREE
        • IN_RESPONSE_DISAGREE
        • IN_RESPONSE_QUESTION
         */
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(activity)
                                .userInfoDao();
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        final UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(userInfo!=null)
                                {

                                    Map<String,Object> values=new HashMap<String,Object>();

                                    boolean has_intent_data=activity.getIntent().hasExtra(OrgFields.CONVERSATION_ID)
                                            &activity.getIntent().hasExtra(OrgFields.IS_STANDALONE_CONVO)
                                            &activity.getIntent().hasExtra(OrgFields.TITLE)
                                            &activity.getIntent().hasExtra(OrgFields.ORG_MAIN_COLOR)
                                            &activity.getIntent().hasExtra(OrgFields.ORG_NAME)
                                            &activity.getIntent().hasExtra(OrgFields.ORGID)
                                            &activity.getIntent().hasExtra(OrgFields.UID)
                                            &activity.getIntent().hasExtra(OrgFields.ADMIN_NAME)
                                            &activity.getIntent().hasExtra(OrgFields.ADMIN_PROFILE_PATH)
                                            &activity.getIntent().hasExtra(OrgFields.ACTIVITY_TYPE);
                                    Memory memory=new Memory(activity);
                                    String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                    if(jnm.isEmpty()==false)
                                    {

                                        long localtimeoffset=Long.parseLong(jnm);
                                        long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                        org.joda.time.LocalDateTime localDateTime
                                                =new org.joda.time.LocalDateTime(estimatedServerTimeMs);
                                        values.put(OrgFields.DAY,localDateTime.getDayOfMonth());
                                        values.put(OrgFields.MONTH,localDateTime.getMonthOfYear());
                                        values.put(OrgFields.YEAR,localDateTime.getYear());

                                    }
                                    //commentator's details who is talking
                                    values.put(OrgFields.USER_FIRSTNAME,userInfo.firstname);
                                    values.put(OrgFields.USER_LASTNAME,userInfo.lastname);
                                    values.put(OrgFields.UID,firebaseUser.getUid());
                                    values.put(OrgFields.USER_PROFILE_PICTURE,userInfo.profile_path);
                                    values.put(OrgFields.USER_CELLPHONE,userInfo.cellphone);
                                    //the comment
                                    if(comment_type==Comment.AGREES)
                                    {

                                        //Gets tangled up with the comments in the query
                                        //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                                        values.put(OrgFields.COMMENT_TYPE,Comment.AGREES);
                                    }
                                    if(comment_type==Comment.DISAGREES)
                                    {
                                        //Gets tangled up with the comments in the query
                                        //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                                        values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES);
                                    }
                                    if(comment_type==Comment.QUESTION)
                                    {
                                        //Gets tangled up with the comments in the query
                                        //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                                        values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION);
                                    }
                                    if(comment_type==Comment.ANSWER)
                                    {
                                        //Gets tangled up with the comments in the query
                                        //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                                        values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER);
                                    }

                                    ImageView emoji_button=(ImageView)
                                            activity.findViewById(R.id.emoji_button);
                                    EmojiconEditText emojicon_edit_text=(EmojiconEditText)
                                            activity.findViewById(R.id.emojicon_edit_text);
                                    values.put(OrgFields.COMMENT,emojicon_edit_text.getText().toString());

                                    //when they said it
                                    //orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING)
                                    values.put(OrgFields.USER_CREATED_DATE, FieldValue.serverTimestamp());
                                    values.put(OrgFields.USER_MODIFIED_DATE, FieldValue.serverTimestamp());
                                    values.put(OrgFields.IS_NEW_TOPIC,false);
                                    //who they are talking to
                                    if(replying_comment!=null)
                                    {
                                        values.put(OrgFields.IS_RESPONSE_COMMENT,true);
                                        values.put(OrgFields.REPLIED_COMMENT_ID,replying_comment.getComment_id());
                                        values.put(OrgFields.REPLIED_COMMENT_TYPE,replying_comment.getComment_type());
                                        values.put(OrgFields.REPLIED_COMMENT,replying_comment.getComment());
                                        values.put(OrgFields.REPLIED_COMMENTATOR_NAME,replying_comment.getCommentator_name());
                                        if(replying_comment.getHead_comment_id().isEmpty()==false)
                                        {
                                            //gets tangled in the query whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                            values.put(OrgFields.PARENT_COMMENT_ID,replying_comment.getHead_comment_id());
                                            //whereEqualTo(OrgFields.IS_NEW_TOPIC,false)

                                        }

                                        if(replying_comment.getComment_type()==Comment.DISAGREES)
                                        {
                                            if(comment_type==Comment.AGREES)
                                            {
                                                values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES);
                                            }
                                            if(comment_type==Comment.DISAGREES)
                                            {
                                                values.put(OrgFields.COMMENT_TYPE,Comment.AGREES);
                                            }
                                            if(comment_type==Comment.QUESTION)
                                            {
                                                values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION);
                                            }
                                        }
                                        else
                                        {
                                            values.put(OrgFields.COMMENT_TYPE,comment_type);
                                            if(replying_comment.getComment_type()==Comment.QUESTION)
                                            {
                                                values.put(OrgFields.IN_RESPONSE_QUESTION,true);
                                            }
                                        }


                                        if(replying_comment.getComment_type()==Comment.AGREES)
                                        {
                                            values.put(OrgFields.IN_RESPONSE_AGREE,true);
                                            //Gets tangled up with the comments in the query
                                            //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)

                                        }
                                        if(replying_comment.getComment_type()==Comment.DISAGREES)
                                        {
                                            values.put(OrgFields.IN_RESPONSE_DISAGREE,true);
                                            //Gets tangled up with the comments in the query
                                            //whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)

                                        }


                                    }
                                    else
                                    {
                                        values.put(OrgFields.IS_RESPONSE_COMMENT,false);
                                        if(under_head_comment&head_comment!=null)
                                        {
                                            //whereEqualTo(OrgFields.IS_NEW_TOPIC,false)
                                            values.put(OrgFields.IS_NEW_TOPIC,false);
                                            values.put(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id());

                                        }
                                        else
                                        {
                                            values.put(OrgFields.IS_NEW_TOPIC,true);

                                        }
                                        values.put(OrgFields.IN_RESPONSE_AGREE,false);



                                    }

                                    //orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                                    values.put(OrgFields.NUM_COMMENTS,0);
                                    values.put(OrgFields.CONVERSATION_ID,bookieActivity.getActivity_id());
                                    values.put(OrgFields.ACTIVITY_ID,bookieActivity.getActivity_id());
                                    values.put(OrgFields.IS_STANDALONE_CONVO,bookieActivity.isIs_standalone());
                                    values.put(OrgFields.IS_ACTIVITY_BASED,(bookieActivity.isIs_standalone()==false));
                                    values.put(OrgFields.IS_ACTIVITY_REFERENCE,(bookieActivity.isIs_standalone()==false));

                                    //the conversation this comment belongs to
                                    if(bookieActivity!=null)
                                    {
                                        values.put(OrgFields.ACTIVITY_TYPE,bookieActivity.getActivity_type());
                                    }
                                    else if(has_intent_data)
                                    {
                                        String activity_type_txt=activity.getIntent()
                                                .getStringExtra(OrgFields.ACTIVITY_TYPE);
                                        values.put(OrgFields.ACTIVITY_TYPE,activity_type_txt);
                                    }
                                    if(bookieActivity.isIs_standalone()==false)
                                    {
                                        values.put(OrgFields.ACTIVITY_REFERENCE,
                                                bookieActivity.getActivity_reference());
                                    }

                                    //the organization this conversation belongs to
                                    values.put(OrgFields.ORGID,bookieActivity.getOrgid());
                                    values.put(OrgFields.ORG_NAME,bookieActivity.getOrgname());
                                    values.put(OrgFields.ORG_MAIN_COLOR,bookieActivity.getOrg_main_color());


                                    CloudWorker.getLetsTalkComments()
                                            .add(values)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Log.i("submitCommentToCloud","onFailure "+e.getMessage());

                                                }
                                            })
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {

                                                    Memory memory=new Memory(activity);
                                                    String hj=memory.getString(OrgFields.SERVER_TIME_OFFSET);
                                                    long estimatedServerTimeMs =0;

                                                    if(hj.isEmpty()==false)
                                                    {
                                                        long localtimeoffset=Long.parseLong(hj);
                                                        estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                                    }
                                                    org.joda.time.LocalDateTime localDateTime
                                                            =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                                    Log.i("submitCommentToCloud","onSuccess "+bookieActivity.getActivity_id()
                                                            +" estimatedServerTimeMs="+estimatedServerTimeMs
                                                            +" day_of_month="+localDateTime.getDayOfMonth()
                                                            +" h="+(head_comment!=null)+" h2="+under_head_comment
                                                            +" "+bookieActivity.getActivity_id());

                                                    DatabaseReference mDatabase;
                                                    mDatabase = FirebaseDatabase.getInstance().getReference();

                                                    if(replying_comment!=null)
                                                    {

                                                        if(replying_comment.getHead_comment_id().isEmpty()==false)
                                                        {

                                                            mDatabase.child(bookieActivity.getActivity_id()
                                                                            +"_"+replying_comment.getHead_comment_id())
                                                                    .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                    .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                    .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                    .setValue(documentReference.getId());
                                                            mDatabase.child(bookieActivity.getActivity_id()
                                                                            +"_"+replying_comment.getHead_comment_id()+"_"+comment_type)
                                                                    .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                    .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                    .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                    .setValue(documentReference.getId());
                                                            incrementCommentsFor(replying_comment.getComment_id());

                                                        }

                                                        replying_comment=null;
                                                        CommentListener commentListener=(CommentListener) activity;
                                                        CommentListener commentListener2=(CommentListener) current_topic_frag;
                                                        if(commentListener!=null)
                                                        {
                                                            commentListener.OnCancelReply();
                                                        }
                                                        if(commentListener2!=null)
                                                        {
                                                            commentListener2.OnCancelReply();
                                                        }


                                                    }
                                                    else if(under_head_comment&head_comment!=null)
                                                    {
                                                        mDatabase.child(bookieActivity.getActivity_id()+"_"+head_comment.getComment_id())
                                                                .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                .setValue(documentReference.getId())
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Log.i("submitCommentToCloud","realtime database write succeeded");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.i("submitCommentToCloud","realtime database write failed "+e.getMessage());
                                                                    }
                                                                });
                                                        mDatabase.child(bookieActivity.getActivity_id()+"_"+head_comment.getComment_id()+"_"+comment_type)
                                                                .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                .setValue(documentReference.getId());
                                                        incrementCommentsFor(head_comment.getComment_id());
                                                    }
                                                    else
                                                    {

                                                        mDatabase.child(bookieActivity.getActivity_id()+"_"+documentReference.getId())
                                                                .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                .setValue(documentReference.getId());
                                                        mDatabase.child(bookieActivity.getActivity_id()+"_"+documentReference.getId()+"_"+comment_type)
                                                                .child('"'+String.valueOf(localDateTime.getYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getMonthOfYear())+'"')
                                                                .child('"'+String.valueOf(localDateTime.getDayOfMonth())+'"')
                                                                .setValue(documentReference.getId());
                                                        incrementCommentsFor(documentReference.getId());

                                                    }





                                                }
                                            });

                                }

                            }
                        });


                    }
                });

    }

    private void incrementCommentsFor(String id)
    {

        Map<String,Object> values=new HashMap<String,Object>();
        values.put(OrgFields.NUM_COMMENTS,FieldValue.increment(1));

        CloudWorker.getLetsTalkComments()
                .document(id)
                .set(values, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("incrementCommentsFor","e="+e.getMessage());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.i("incrementCommentsFor",id+" incremented");

                    }
                });

    }


}
