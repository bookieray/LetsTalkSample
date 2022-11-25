package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.CommentDate;
import brainwind.letstalksample.features.letstalk.fragments.item.LabelT;
import brainwind.letstalksample.features.letstalk.fragments.item.TMDay;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentWorker
{

    Fragment currentTopicForConvo;
    private static final String CONVO_ID = "param1";
    private static final String TITLE = "param2";
    private static final String IS_STANDALONE = "param3";
    private static final String TIMEOFFSET = "param4";
    private static final String STARTING_SIGNATURE = "param5";

    /*
    1:START OF HEAD COMMENT
     */

    RelativeLayout rootview;

    //Loading of the head comment
    RelativeLayout loading_head_comment;
    ProgressBar progress_bar_loading_head_comment;


    //After loaded the head comment
    //There is a head comment
    CardView head_comment_parent_view;
    CircleImageView head_profile_image;
    TextView head_comment_name;
    TextView comment_type;
    ExpandableTextView head_comment_textView;
    TextView num_comments_view;
    TextView status;

    //There is no head comment
    RelativeLayout sharedialog_area;
    TextView no_data_area_label_sharedialog;
    Button sharedialog_button;
    ExpandableTextView qouted_comment;

    //Area below head comment area
    RelativeLayout marker_filter_area;
    Spinner comment_filter;

    //Failed to load the head comment
    LinearLayout head_comment_error_area;
    TextView head_comment_error_label;
    Button head_comment_tryagain;
    private Spinner comment_type_spinner;
    private Comment head_comment;
    private String starting_sign="";
    private boolean load_last_timestamp;

    public ReplyCommentWorker replyCommentWorker=new ReplyCommentWorker(this);

    private void getViews(Fragment currentTopicForConvo)
    {

        this.rootview=(RelativeLayout)currentTopicForConvo.getView()
                .findViewById(R.id.rootview);
        /*
         START OF HEAD COMMENT
        */
        //Loading for the head comment
        this.progress_bar_loading_head_comment=currentTopicForConvo.getView()
                .findViewById(R.id.progress_bar_loading_head_comment);

        //The head comment views
        this.head_comment_parent_view=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment_parent_view);
        this.head_profile_image=currentTopicForConvo.getView()
                .findViewById(R.id.head_profile_image);
        this.head_comment_name=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment_name);
        this.comment_type=currentTopicForConvo.getView()
                .findViewById(R.id.comment_type);
        this.head_comment_textView=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment);
        this.num_comments_view=currentTopicForConvo.getView()
                .findViewById(R.id.num_comments_view);
        this.status=currentTopicForConvo.getView()
                .findViewById(R.id.status);

        //Area below head comment area
        //the labels with the agree, filter and disagree
        this.marker_filter_area=currentTopicForConvo.getView()
                .findViewById(R.id.marker_filter_area);
        //The Filter for the loaded comments
        this.comment_filter=currentTopicForConvo.getView()
                .findViewById(R.id.comment_filter);
        setUpFilter();

        //Failed to load the head comment
        head_comment_error_area=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment_error_area);
        head_comment_error_label=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment_error_label);
        head_comment_tryagain=currentTopicForConvo.getView()
                .findViewById(R.id.head_comment_tryagain);
        /*
         END OF HEAD COMMENT
        */


        /*
         START OF COMMENTS FOR HEAD COMMENT
        */



        /*
         END OF COMMENTS FOR HEAD COMMENT
        */
        if(comment_list==null&currentTopicForConvo!=null)
        {
            comment_list=currentTopicForConvo.getView()
                    .findViewById(R.id.comment_list);
            comment_list.setLayoutManager(new LinearLayoutManager(currentTopicForConvo.getContext()));

        }
        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicForConvo,rootview);
            checkCommentListKeyBoard();
            if(comment_list!=null)
            {
                comment_list.setAdapter(commentAdapter);
            }

        }


    }

    boolean selected_filter=false;
    public int last_select_filter_option=-1;
    private void setUpFilter()
    {

        if(this.comment_filter!=null)
        {
            this.comment_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if(selected_filter==false)
                    {
                        selected_filter=true;
                    }
                    else
                    {
                        comment_filter.setVisibility(View.GONE);
                        getTimestampsForConVoandCommentType(currentTopicForConvo,head_comment,comment_filter);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    //Loading Head Comment
    boolean fetching_head_comment=false;
    //Loading Head Comment: 1
    public void getHeadComment(Fragment currentTopicForConvo)
    {

        this.currentTopicForConvo=currentTopicForConvo;
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        if(currentTopicForConvo!=null)
                        {


                            if(currentTopicForConvo.getArguments()!=null)
                            {

                                getViews(currentTopicForConvo);
                                final String conversation_id = currentTopicForConvo.getArguments()
                                        .getString(CONVO_ID);
                                boolean is_standalone=false;
                                if (currentTopicForConvo.getArguments().containsKey(IS_STANDALONE)) {
                                    is_standalone = currentTopicForConvo.getArguments()
                                            .getBoolean(IS_STANDALONE);
                                }
                                com.google.firebase.firestore.Query query=QueryWorker
                                        .getHeadCommentQuery(currentTopicForConvo);

                                if(currentTopicForConvo!=null)
                                {

                                    currentTopicForConvo.getActivity()
                                            .runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    CommentListener commentListener=(CommentListener)
                                                            currentTopicForConvo;
                                                    if(commentListener!=null)
                                                    {
                                                        commentListener.foundHeadComment(null);
                                                    }

                                                }
                                            });

                                }
                                if(commentAdapter.commentListUnderHeadComment!=null)
                                {
                                    commentAdapter.commentListUnderHeadComment.clear();
                                }

                                if(commentAdapter!=null)
                                {
                                    currentTopicForConvo.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            commentAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }

                                query.get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                showGetHeadCommentError(e);

                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                replyCommentWorker=new ReplyCommentWorker(CommentWorker.this);
                                                replyCommentWorker.attachReplySwitch(comment_list);
                                                if(queryDocumentSnapshots.isEmpty())
                                                {
                                                    if(currentTopicForConvo!=null)
                                                    {

                                                        CommentListener commentListener=(CommentListener)
                                                                currentTopicForConvo;
                                                        if(commentListener!=null)
                                                        {
                                                            commentListener.foundHeadComment(null);
                                                        }

                                                    }
                                                    noHeadComment();
                                                }
                                                else
                                                {

                                                    showHeadComment(queryDocumentSnapshots.getDocuments().get(0));
                                                }

                                            }
                                        });

                            }

                        }

                    }
                });


    }
    //Loading Head Comment: 2
    private void showHeadComment(DocumentSnapshot documentSnapshot)
    {


        this.currentTopicForConvo.getActivity()
                .runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Comment head_comment=new Comment(documentSnapshot);
                        Log.i("hdcmts",head_comment.getComment_id()+" "+
                                currentTopicForConvo.getArguments()
                                        .getString(CONVO_ID));
                        if(currentTopicForConvo!=null)
                        {

                            CommentListener commentListener=(CommentListener) currentTopicForConvo;
                            if(commentListener!=null)
                            {
                                commentListener.foundHeadComment(head_comment);
                            }

                        }

                        head_comment_error_area=currentTopicForConvo.getView()
                                .findViewById(R.id.head_comment_error_area);
                        loading_head_comment=currentTopicForConvo.getView()
                                        .findViewById(R.id.loading_head_comment);
                        head_comment_parent_view=currentTopicForConvo.getView()
                                        .findViewById(R.id.head_comment_parent_view);
                        head_profile_image=currentTopicForConvo.getView()
                                .findViewById(R.id.head_profile_image);
                        head_comment_name=currentTopicForConvo.getView()
                                .findViewById(R.id.head_comment_name);
                        comment_type=currentTopicForConvo.getView()
                                .findViewById(R.id.comment_type);
                        head_comment_textView=currentTopicForConvo.getView()
                                .findViewById(R.id.head_comment);
                        num_comments_view=currentTopicForConvo.getView()
                                .findViewById(R.id.num_comments_view);
                        status=currentTopicForConvo.getView()
                                .findViewById(R.id.status);

                        head_comment_error_area.setVisibility(View.GONE);
                        loading_head_comment.setVisibility(View.GONE);
                        head_comment_parent_view.setVisibility(View.VISIBLE);

                        //the user's name the head comment belongs to
                        head_comment_name.setText(head_comment.getCommentator_name());
                        String firstname="";
                        if(head_comment.getCommentator_name().trim().isEmpty()==false)
                        {
                            String headCommentName=head_comment.getCommentator_name();
                            String[] names=headCommentName.split(" ");
                            if(names.length>1)
                            {
                                headCommentName="";
                                for(int i=0;i<names.length;i++)
                                {
                                    if(i==0)
                                    {
                                        firstname=names[0];
                                    }
                                    String jn=names[i];
                                    Log.i("uksidla","jn="+jn);
                                    if(jn.length()>1)
                                    {
                                        headCommentName+=" "+jn.substring(0,1).toUpperCase()
                                                +jn.substring(1,jn.length());
                                    }
                                    else
                                    {
                                        headCommentName+=" "+jn.substring(0,0).toUpperCase();
                                    }

                                }
                            }
                            headCommentName=headCommentName.trim();
                            head_comment_name.setText(headCommentName);
                            Log.i("muhgsfa","headCommentName="+headCommentName);
                        }
                        //show the user's profile image
                        Drawable placeholder = new AvatarGenerator.AvatarBuilder(currentTopicForConvo.getActivity())
                                .setLabel(head_comment.getCommentator_name())
                                .setAvatarSize(120)
                                .setTextSize(30)
                                .toCircle()
                                .setBackgroundColor(currentTopicForConvo.getActivity().
                                        getResources().getColor(R.color.purple_700))
                                .build();
                        //show the profile image
                        if (head_comment.getCommentator_profile_path().isEmpty() == false) {

                            Glide.with(currentTopicForConvo.getActivity()).clear(head_profile_image);


                            Glide.with(currentTopicForConvo.getActivity())
                                    .load(Uri.parse(head_comment.getCommentator_profile_path()))
                                    .placeholder(placeholder)
                                    .centerCrop()
                                    .error(placeholder)
                                    .into(head_profile_image);

                        }
                        else
                        {
                            Glide.with(currentTopicForConvo.getActivity())
                                    .load(placeholder)
                                    .placeholder(placeholder)
                                    .centerCrop()
                                    .error(placeholder)
                                    .into(head_profile_image);
                        }

                        status.setText("");
                        //tell the user if there is a news source
                        if(head_comment.isHasNewsReference())
                        {

                            if(head_comment.getNewsSource().trim().isEmpty()==false)
                            {
                                String[] names=head_comment.getNewsSource().trim().split(" ");
                                if(names.length>1)
                                {
                                    status.setText("Based on "+names[0]+"...");
                                }
                                else
                                {
                                    status.setText("Based on "+head_comment.getNewsSource());
                                }

                            }
                            else
                            {
                                status.setText(head_comment.getNewsSource());
                            }
                            status.setTextColor(currentTopicForConvo.getResources().getColor(R.color.green));

                        }
                        else
                        {
                            status.setText("(Tap to Research)");
                            status.setTextColor(currentTopicForConvo.getResources().getColor(R.color.red));
                        }

                        //show them the comment
                        //include the news headline for context
                        if(head_comment.isHasNewsReference())
                        {
                            if(head_comment.getNewsSource().trim().isEmpty()==false)
                            {
                                head_comment_textView.setText("Commenting on "+head_comment.getNewsHeadline()
                                        +firstname+":"
                                        +'"'+head_comment.getComment()+'"');
                            }
                            else
                            {

                            }
                        }
                        else
                        {
                            head_comment_textView.setText(head_comment.getComment());
                        }

                        //show them how many comments under this head comment
                        //the number of comments under the head comment
                        num_comments_view.setText(NumUtils.getAbbreviatedNum(head_comment.getNum_comments())
                                + " comments");

                        getTimestampsForConVoandCommentType(currentTopicForConvo,head_comment,comment_filter);


                    }
                });


    }
    //Loading Head Comment: 3
    private void noHeadComment()
    {



    }
    //Loading Head Comment: 4
    private void showGetHeadCommentError(Exception e)
    {



    }

    /*
    1:END OF THE HEAD COMMENT
     */

    /*
    2:START OF COMMENTS FOR HEAD COMMENT
     */

    //Loading of comments for the Head Comment
    //The CommentAdapter adds a element if no comments and returns a viewType -1
    //this will return @layout/loading_view.xml in the onCreateViewHolder
    //then after notifyDataSetChanged


    //After loaded comments for the Head Comment

    //There is comments for the Head Comment
    public RecyclerView comment_list;
    public CommentAdapter commentAdapter;

    //There is no comments for the Head Comment


    //Failed to load the head comment


    //Lists that the registry for the timestamps for the comments
    //this makes the sampling for each day possible and allows
    //users to skip
    public void getTimestampsForConVoandCommentType(Fragment currentTopicForConvo,
                                                    Comment head_comment,
                                                    Spinner comment_type)
    {

        currentTopicForConvo.getActivity()
                .runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        marker_filter_area.setVisibility(View.GONE);

                    }
                });

        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicForConvo,rootview);
        }

        final String conversation_id = currentTopicForConvo.getArguments().getString(CONVO_ID);
        boolean is_standalone=false;
        if (currentTopicForConvo.getArguments().containsKey(IS_STANDALONE)) {
            is_standalone = currentTopicForConvo.getArguments().getBoolean(IS_STANDALONE);
        }
        Log.i("getCommentsFrHedComent","getCommentsForHeadComment conversation_id="+conversation_id
                +" is_standalone="+is_standalone);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String qtxt=conversation_id+"_"+head_comment.getComment_id();

        if(comment_filter.getSelectedItemPosition()==1)
        {
            //Agree
            qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.AGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==2)
        {
            //Disagree
            qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.DISAGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==3)
        {
            //Question
            qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.QUESTION;
        }
        else if(comment_filter.getSelectedItemPosition()==4)
        {
            //Answers
            qtxt=conversation_id+"_"+head_comment.getComment_id()+"_"+Comment.ANSWER;
        }


        mDatabase.child(qtxt)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {

                        if(!task.isSuccessful())
                        {
                            Log.i("getCommentsFrHedComent","Not Successful "+task.getException().getMessage());
                            failedToTimestamps(task);
                        }
                        else
                        {


                            Log.i("getCommentsFrHedComent","Successful ");
                            //scan the JSON string and get the dates
                            ScanJsonTimeStamps(task,currentTopicForConvo,head_comment,comment_type);


                        }

                    }
                });




    }

    private void failedToTimestamps(Task<DataSnapshot> task)
    {

        if(last_select_filter_option>-1)
        {

            CommentListener commentListener=(CommentListener) currentTopicForConvo;
            if(commentListener!=null)
            {
                commentListener.OnErrorLoading(task.getException().getMessage(),comment_filter.getSelectedItemPosition());
            }
            comment_filter.setSelection(last_select_filter_option);
        }
        else
        {
            CommentListener commentListener=(CommentListener) currentTopicForConvo;
            if(commentListener!=null)
            {
                commentListener.OnErrorLoading(task.getException().getMessage()
                        ,comment_filter.getSelectedItemPosition());
            }
            if(comment_filter.getSelectedItemPosition()>0)
            {
                comment_filter.setSelection(0);
            }
            else
            {

                currentTopicForConvo.getActivity()
                        .runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new MaterialDialog.Builder(currentTopicForConvo.getActivity())
                                        .title("Oops there is an error")
                                        .content(task.getException().getMessage())
                                        .cancelable(false)
                                        .positiveText("Retry")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog,
                                                                @NonNull DialogAction which) {

                                                getTimestampsForConVoandCommentType(currentTopicForConvo
                                                        ,head_comment,comment_filter);

                                            }
                                        })
                                        .negativeText("Close convo")
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                currentTopicForConvo.getActivity().finish();

                                            }
                                        })
                                        .show();

                            }
                        });


            }
        }

    }

    private void listenForToday(Fragment currentTopicForConvo,
                                Comment head_comment,
                                Spinner comment_type)
    {

        Memory memory=new Memory(currentTopicForConvo.getActivity());
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

        if(jnm.isEmpty()==false)
        {

            long localtimeoffset=Long.parseLong(jnm);
            long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;

            org.joda.time.LocalDateTime localDateTime
                    =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

            Query query=null;
            String conversation_id = currentTopicForConvo.getArguments()
                    .getString(CONVO_ID);
            //return getYear()+"-"+getMonth()+"-"+getDay();
            String timestamp=localDateTime.getYear()
                    +"-"+localDateTime.getMonthOfYear()
                    +"-"+localDateTime.getDayOfMonth();

            if(head_comment!=null)
            {

                //All
                if(comment_type.getSelectedItemPosition()==0)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Agree
                if(comment_type.getSelectedItemPosition()==1)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);

                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Disagree
                if(comment_type.getSelectedItemPosition()==2)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Question
                if(comment_type.getSelectedItemPosition()==3)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Answers
                if(comment_type.getSelectedItemPosition()==4)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }



            }
            else
            {

                //All
                if(comment_type.getSelectedItemPosition()==0)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Agree
                if(comment_type.getSelectedItemPosition()==1)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Disagree
                if(comment_type.getSelectedItemPosition()==2)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Question
                if(comment_type.getSelectedItemPosition()==3)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }
                //Answers
                if(comment_type.getSelectedItemPosition()==4)
                {
                    query=CloudWorker.getLetsTalkComments()
                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                            .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                            .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                            .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                    if(timestamp_comments.containsKey(localDateTime.getYear()+"-"
                            +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth()))
                    {
                        ArrayList<Comment> cmts=timestamp_comments.get(localDateTime.getYear()+"-"
                                +localDateTime.getMonthOfYear()+"-"+localDateTime.getDayOfMonth());
                        if(cmts.size()>0)
                        {
                            Comment cmy=cmts.get(cmts.size()-1);
                            query=CloudWorker.getLetsTalkComments()
                                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                    .whereEqualTo(OrgFields.DAY,localDateTime.getDayOfMonth())
                                    .whereEqualTo(OrgFields.MONTH,localDateTime.getMonthOfYear())
                                    .whereEqualTo(OrgFields.YEAR,localDateTime.getYear())
                                    .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.ANSWER)
                                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,cmy.getCreatedDate())
                                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                        }
                    }
                }

            }

            if(query!=null)
            {

                query.addSnapshotListener(MetadataChanges.INCLUDE
                        ,new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value,
                                                @Nullable FirebaseFirestoreException error) {

                                if(value!=null)
                                {

                                    if(value.isEmpty()==false)
                                    {

                                        UserInfoDatabase.databaseWriteExecutor
                                                .execute(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                        for(int i=0;i<value.getDocuments().size();i++)
                                                        {

                                                            DocumentSnapshot documentSnapshot=value.getDocuments().get(i);
                                                            final Comment comment=new Comment(documentSnapshot);
                                                            Log.i("uyshgak","a1="+(process_comments
                                                                    .containsKey(comment.getComment_id()))
                                                                    +" a2="+(value.getMetadata().hasPendingWrites()));
                                                            if(process_comments
                                                                    .containsKey(comment.getComment_id())==false
                                                                    &value.getMetadata().hasPendingWrites())
                                                            {

                                                                Log.i("listenForToday",
                                                                        value.getMetadata().isFromCache()
                                                                                +" "+value.getMetadata().hasPendingWrites()
                                                                                +" "+comment.getComment_id());
                                                                if(value.getMetadata().hasPendingWrites())
                                                                {
                                                                    comment.setSent(false);
                                                                }
                                                                else
                                                                {
                                                                    comment.setSent(true);
                                                                }

                                                                commentAdapter.commentListUnderHeadComment
                                                                        .add(comment);
                                                                process_comments.put(comment.getComment_id(),true);
                                                                String timestamp=comment.getTimestamp();
                                                                if(timestamp_comments.containsKey(timestamp))
                                                                {

                                                                    ArrayList<Comment> comments=timestamp_comments.get(timestamp);
                                                                    comments.add(comment);
                                                                    timestamp_comments.put(timestamp,comments);
                                                                    commentAdapter.timestamps_comments.put(timestamp,comments);

                                                                    Log.i("tx"+timestamp,"1 tsize="
                                                                            +timestamp_comments.get(timestamp).size()
                                                                            +" "+comment.getTimestamp());



                                                                }
                                                                else
                                                                {
                                                                    ArrayList<Comment> comments=new ArrayList<Comment>();
                                                                    comments.add(comment);
                                                                    timestamp_comments.put(timestamp,comments);
                                                                    commentAdapter.timestamps_comments.put(timestamp,comments);
                                                                    Log.i("tx"+timestamp,"2 tsize="
                                                                            +timestamp_comments.get(timestamp).size()+"");


                                                                }

                                                                if(timestamp_comments.containsKey(comment.getTimestamp()))
                                                                {
                                                                    ArrayList<Comment> commentArrayList=timestamp_comments.get(comment.getTimestamp());
                                                                    addDayMarkers(commentArrayList.size(),
                                                                            commentAdapter
                                                                                    .commentListUnderHeadComment.size()-1
                                                                            ,true);
                                                                }
                                                                currentTopicForConvo.getActivity()
                                                                        .runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                final int last_c=commentAdapter.commentListUnderHeadComment.size()-1;
                                                                                commentAdapter.notifyItemInserted(last_c);
                                                                                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                                                                                {

                                                                                    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                                                                    if(comment.getCommentator_uid().equals(user.getUid()))
                                                                                    {
                                                                                        comment_list.scrollToPosition(last_c);
                                                                                        comment_list.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                comment_list.smoothScrollToPosition(last_c);
                                                                                            }
                                                                                        },300);
                                                                                    }

                                                                                }

                                                                            }
                                                                        });

                                                                registerToday(comment);


                                                            }
                                                            else if(value.getMetadata().hasPendingWrites())
                                                            {

                                                                if(commentAdapter.comment_position
                                                                        .containsKey(comment.getComment_id()))
                                                                {


                                                                    final int positionx=commentAdapter.comment_position
                                                                            .get(comment.getComment_id());

                                                                    Log.i("uyshgak","positionx="+positionx);

                                                                    if(positionx<commentAdapter.commentListUnderHeadComment.size())
                                                                    {
                                                                        Comment comt=commentAdapter.commentListUnderHeadComment.get(positionx);
                                                                        if(value.getMetadata().hasPendingWrites()==false&comt.isSent()==false)
                                                                        {
                                                                            comt.setSent(true);
                                                                            registerToday(comment);
                                                                            commentAdapter.commentListUnderHeadComment.set(positionx,comt);
                                                                            currentTopicForConvo.getActivity()
                                                                                    .runOnUiThread(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            commentAdapter.notifyItemChanged(positionx);

                                                                                            Log.i("dhyishd","posil="+positionx);
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


                            }
                        });


            }


        }

    }

    private void registerToday(Comment comment)
    {

        Memory memory=new Memory(currentTopicForConvo.getActivity());
        String hj=memory.getString(OrgFields.SERVER_TIME_OFFSET);
        long estimatedServerTimeMs =0;

        if(hj.isEmpty()==false)
        {
            long localtimeoffset=Long.parseLong(hj);
            estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
        }
        org.joda.time.LocalDateTime localDateTime
                =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(comment.getHead_comment_id().isEmpty()==false)
        {
            Log.i("polksd",comment.getConversation_id()
                    +"_"+comment.getHead_comment_id());
            Log.i("polksd",comment.getConversation_id()
                    +"_"+comment.getHead_comment_id()+"_"+comment.getComment_type());

            mDatabase.child(comment.getConversation_id()
                            +"_"+comment.getHead_comment_id())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .setValue(comment.getComment_id());
            mDatabase.child(comment.getConversation_id()
                            +"_"+comment.getHead_comment_id()+"_"+comment.getComment_type())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .setValue(comment.getComment_id());

        }
        else
        {

            Log.i("polksd",comment.getConversation_id()
                    +"_"+comment.getComment_id());
            Log.i("polksd",comment.getConversation_id()
                    +"_"+comment.getComment_id()+"_"+comment.getComment_type());
            mDatabase.child(comment.getConversation_id()
                            +"_"+comment.getComment_id())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .setValue(comment.getComment_id());
            mDatabase.child(comment.getConversation_id()
                            +"_"+comment.getComment_id()+"_"+comment.getComment_type())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .setValue(comment.getComment_id());

        }

    }

    //The years the conversation has been carried out
    List<Integer> years=new ArrayList<Integer>();
    //The months the conversation has been carried out and indexed by the year-month string to retrieve
    //and aggregate into lists indexed by the key
    HashMap<Integer,ArrayList<Integer>> year_months=new HashMap<Integer,ArrayList<Integer>>();
    //The days the conversation across months, years and indexed by the year-month-day
    HashMap<String,ArrayList<Integer>> year_month_days=new HashMap<String,ArrayList<Integer>>();
    //The Last Comment ID linked to combination of the day, month and year
    HashMap<String,String> year_month_day_last_comment_id=new HashMap<String,String>();
    //The list of the objects that singularly contain day, month and year
    ArrayList<TMDay> tmDays=new ArrayList<TMDay>();
    //tracks the timestamps already added to the master list of the comments
    HashMap<String, CommentDate> timestamps_dates=new HashMap<String,CommentDate>();
    boolean assigned_scroll_listener=false;
    private void ScanJsonTimeStamps(Task<DataSnapshot> task,
                                    Fragment currentTopicForConvo,
                                    Comment head_comment,
                                    Spinner comment_type)
    {

        this.process_comments.clear();
        this.head_comment= head_comment;
        this.comment_type_spinner=comment_type;
        JSONObject jsonObject= null;
        //clear the lists to allow for new indexes
        //according to the filter selection, ALL, AGREE, DISAGREE, QUESTION
        years.clear();
        year_months.clear();
        year_month_days.clear();
        year_month_day_last_comment_id.clear();
        timestamp_comments.clear();

        tmDays.clear();
        if(commentAdapter==null)
        {
            commentAdapter=new CommentAdapter(currentTopicForConvo,rootview);
        }
        if(commentAdapter!=null)
        {

            //on the adapter
            commentAdapter.commentListUnderHeadComment.clear();
            commentAdapter.timestamps_comments.clear();
            commentAdapter.markers.clear();
            commentAdapter.markers_positions.clear();
            commentAdapter.no_more_comments.clear();
            commentAdapter.started_loading_older_coments.clear();
            commentAdapter.started_loading_next_coments.clear();
            //internally
            no_more_comments.clear();
            timestamp_comments.clear();
            last_loaded_position=0;
            loading_timestamp_position=last_loaded_position;
            timestamps_dates.clear();

            commentAdapter.loading_timestamp_position=loading_timestamp_position;
            commentAdapter.last_timestamp_position=loading_timestamp_position;

        }

        try
        {

            jsonObject = new JSONObject(task.getResult().getValue().toString());
            //Iterator<String> keys = jsonObject.keys();
            JSONArray years_list=jsonObject.names();
            Log.i("getCommentsFrHedComent","years_count="+years_list.length());

            //loop through the years
            for(int i=0;i<years_list.length();i++)
            {

                //get each year
                Integer year=Integer.parseInt(years_list.getString(i));
                Log.i("getCommentsFrHedComent","year="+year);
                years.add(year);

                //find the months linked to each year
                JSONObject jsonObject1=jsonObject.getJSONObject(years_list.getString(i));
                Iterator<String> months_names = jsonObject1.keys();

                //loop through the months
                while(months_names.hasNext())
                {

                    //get each month
                    String month_name = months_names.next();
                    Integer month=Integer.parseInt(month_name);
                    Log.i("getCommentsFrHedComent","month="+month_name);
                    //get if already index a list linked to the year
                    if(year_months.containsKey(year))
                    {
                        ArrayList<Integer> months=year_months.get(year);
                        months.add(month);
                        year_months.put(year,months);
                    }
                    else
                    {
                        //create a list linked to the year
                        ArrayList<Integer> months=new ArrayList<>();
                        months.add(month);
                        year_months.put(year,months);
                    }

                    //find the days linked to the month
                    JSONObject jsonObject2=jsonObject1.getJSONObject(month_name);
                    Iterator<String> days = jsonObject2.keys();

                    while(days.hasNext())
                    {

                        String day_name = days.next();
                        Integer day=Integer.parseInt(day_name);
                        Log.i("getCommentsFrHedComent","month="+month_name+" day="+day);

                        //find the days linked to the year and month
                        if(year_month_days.containsKey(year+"-"+month))
                        {
                            ArrayList<Integer> indexed_days=year_month_days.get(year+"-"+month);
                            indexed_days.add(day);
                            year_month_days.put(year+"-"+month,indexed_days);
                        }
                        else
                        {
                            ArrayList<Integer> indexed_days=new ArrayList<Integer>();
                            indexed_days.add(day);
                            year_month_days.put(year+"-"+month,indexed_days);

                        }

                        String value=jsonObject2.getString(day_name);
                        year_month_day_last_comment_id.put(year+"-"+month+"-"+day,value);

                    }



                }

            }

            Log.i("getCommentsFrHedComent","starting to create tmDays "+years.size());
            //reorder the lists
            //sort out years
            //descending order
            Collections.sort(years);
            Collections.reverse(years);
            //reorder the months and days
            for(Integer year:years)
            {

                //find the list of months that belong to this year
                if(year_months.containsKey(year))
                {
                    //reorder the months that belong to the year
                    ArrayList<Integer> months=year_months.get(year);
                    Collections.sort(months);
                    Collections.reverse(months);
                    //reorder the days belong to each month in this year
                    for(Integer month:months)
                    {
                        //find the days belong to each month in this year
                        if(year_month_days.containsKey(year+"-"+month))
                        {

                            //sort out days in that month and year
                            //descending order
                            ArrayList<Integer> days=year_month_days.get(year+"-"+month);
                            Collections.sort(days);
                            Collections.reverse(days);
                            //loop through the days and with month and year in previous
                            HashMap<String,String> jkms=new HashMap<String,String>();
                            for(Integer day:days)
                            {

                                TMDay tmDay=new TMDay(month+"","");
                                tmDay.setYear(year);
                                tmDay.setMonth(month+"");
                                tmDay.setDay(day+"");
                                tmDay.addDay(day+"");

                                if(jkms
                                        .containsKey(tmDay.getYear()
                                                +"-"+tmDay.getMonth()
                                                +"-"+tmDay.getDay())==false)
                                {

                                    String hj=jkms
                                            .get(year+"-"+month+"-"+day);
                                    if(year_month_day_last_comment_id.containsKey(hj))
                                    {
                                        String last_comment_id=year_month_day_last_comment_id.get(hj);
                                        tmDay.setLast_comment_id(last_comment_id);
                                    }
                                    jkms.put(hj,tmDay.getLast_comment_id());
                                    tmDays.add(tmDay);

                                }


                            }

                        }
                    }

                }

            }
            //looping through the list of the objects
            // that singularly contain day, month and year
            int starting= tmDays.size()-1;
            //if there are too many days of the conversation
            //load only 100 days worth of timestamps
            //and delete the older than 100 days and then from
            if(starting>99)
            {
                starting=99;
                deleteTooOldDaysOfConvoFromRegistry();
            }
            //looping through the list of the objects
            // that singularly contain day, month and year
            //looping from the most previuous first and added to the adapter list

            Log.i("getCommentsFrHedComent","starting="+starting+" "+tmDays.size());
            for(int i=starting;i>=0;i--)
            {

                TMDay tmDay2=tmDays.get(i);
                String timestamp=tmDay2.getDay()+"-"+tmDay2.getMonth()
                        +"-"+tmDay2.getYear();
                String timestamp2=tmDay2.getYear()+"-"+tmDay2.getMonth()
                        +"-"+tmDay2.getDay();
                Log.i("timstar",timestamp2);

                if(commentAdapter.started_loading_older_coments.containsKey(timestamp2))
                {
                    commentAdapter.started_loading_older_coments.remove(timestamp2);
                }
                //is it already added to the comment list adapter to prevent duplicates
                if(timestamps_dates.containsKey(timestamp)==false)
                {

                    Comment comment=new Comment();
                    comment.setTimestamp(tmDay2);


                    commentAdapter.AddComment(comment);
                    int pl=commentAdapter.commentListUnderHeadComment.size()-1;
                    Log.i("adsxefs","adding "+comment.getTimestamp()+" "+pl);
                    commentAdapter.timestamps_positions.put(comment.getTimestamp(),pl);
                    CommentDate commentDate=new CommentDate(comment);
                    timestamps_dates.put(timestamp,commentDate);
                }

                if(i==0)
                {

                }

            }

            if(tmDays.size()>0)
            {

                LoadLastOrStartTimestamp(comment_type);
            }

            if(tmDays.size()>0)
            {
                last_select_filter_option=comment_filter.getSelectedItemPosition();
                comment_filter.setVisibility(View.VISIBLE);
            }
            else if(last_select_filter_option>-1)
            {

                String judk="No Comments that ";
                if(comment_filter.getSelectedItemPosition()==1)
                {
                    judk+="agree";
                }
                if(comment_filter.getSelectedItemPosition()==2)
                {
                    judk+="disagree";
                }
                if(comment_filter.getSelectedItemPosition()==3)
                {
                    judk+="ask questions";
                }
                if(comment_filter.getSelectedItemPosition()==4)
                {
                    judk+="answer questions";
                }

                String jidk="";
                String sjkd="";
                if(last_select_filter_option==0)
                {
                    jidk=". Go back to viewing all comments";
                }
                if(last_select_filter_option==1)
                {
                    jidk=". Go back to viewing comments that agree";
                }
                if(last_select_filter_option==2)
                {
                    jidk=". Go back to viewing comments that disagree";
                }
                if(last_select_filter_option==3)
                {
                    jidk=". Go back to viewing comments that ask questions";
                }
                if(last_select_filter_option==4)
                {
                    jidk=". Go back to viewing comments that answer questions";
                }
                if(jidk.isEmpty()==false)
                {
                    sjkd="Go Back";
                }
                new MaterialDialog.Builder(currentTopicForConvo.getActivity())
                        .title(judk)
                        .content(judk+jidk)
                        .cancelable(false)
                        .positiveText(sjkd)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                comment_filter.setSelection(last_select_filter_option);


                            }
                        })
                        .show();
            }
            else
            {


                if(comment_filter.getSelectedItemPosition()>0)
                {
                    comment_filter.setVisibility(View.VISIBLE);
                    comment_filter.setSelection(0);

                }
                else
                {

                    comment_filter.setVisibility(View.GONE);
                    commentAdapter.no_comments_in_convo=true;
                    commentAdapter.notifyDataSetChanged();
                    new MaterialDialog.Builder(currentTopicForConvo.getActivity())
                            .title("Make a comment")
                            .customView(R.layout.jushyas,true)
                            .positiveText("Okay")
                            .cancelable(false)
                            .show();

                }




            }



        }
        catch (Exception exception)
        {

        }


        if(comment_list!=null)
        {

            if(assigned_scroll_listener==false)
            {

                comment_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if(newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                        {
                            user_scrolled=true;
                        }

                        if(newState==RecyclerView.SCROLL_STATE_IDLE)
                        {

                            LinearLayoutManager linearLayoutManager=(LinearLayoutManager)
                                    comment_list.getLayoutManager();
                            int first_visible=linearLayoutManager.findFirstVisibleItemPosition();
                            int last_visible=linearLayoutManager.findLastVisibleItemPosition();

                            String msjk=commentAdapter.loading_timestamp_cmid;
                            if(msjk.isEmpty()==false)
                            {

                                if(commentAdapter.comment_position.containsKey(msjk))
                                {

                                    int pos=commentAdapter.comment_position.get(msjk);

                                    if(pos<first_visible||pos>last_visible)
                                    {
                                        if(pos>-1&pos<commentAdapter.commentListUnderHeadComment.size())
                                        {

                                            Comment comment1=commentAdapter.commentListUnderHeadComment.get(loading_timestamp_position);
                                            Memory memory=new Memory(currentTopicForConvo.getActivity());
                                            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

                                            if(jnm.isEmpty()==false)
                                            {

                                                long localtimeoffset=Long.parseLong(jnm);
                                                long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;
                                                org.joda.time.LocalDateTime localDateTime
                                                        =new org.joda.time.LocalDateTime(estimatedServerTimeMs);

                                                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment1);
                                                com.google.android.exoplayer2.util.Log.i("tinsja","timelabel="+timelabel);
                                                Toast.makeText(currentTopicForConvo.getActivity(),
                                                        "Busy with comments for "+timelabel, Toast.LENGTH_LONG).show();

                                            }

                                        }
                                    }

                                }

                            }

                            commentAdapter.last_reading_pos=last_visible;
                            //commentAdapter.thereIsTimeStampBeforeThis(first_visible,last_visible);

                            int lk=last_loaded_position-1;
                            if(user_scrolled)
                            {
                                //Log.i("wrserta","lk="+lk+" first="+first_visible+" last="+last_visible);
                            }
                            if(lk>first_visible&lk<last_visible
                                    &lk<commentAdapter.commentListUnderHeadComment.size()
                            &user_scrolled)
                            {

                                Comment comment=commentAdapter.commentListUnderHeadComment.get(lk);
                                String timestamp=comment.getYear()+"-"+comment.getMonth()
                                        +"-"+comment.getDay();

                                if(commentAdapter.started_loading_older_coments.containsKey(timestamp)==false)
                                {

                                    Log.i("wrserta","position="+lk
                                            +" day="+comment.getDay()
                                            +" month="+comment.getMonth()
                                            +" year="+comment.getYear());
                                    //loading_timestamp_position=lk;
                                    //commentAdapter.loading_timestamp_position=lk;
                                    //commentAdapter.notifyItemChanged(lk);
                                    //commentAdapter.started_loading.put(timestamp,true);
                                    //getCommentsUnderTimeStamp(comment,currentTopicForConvo,head_comment,
                                            //comment_type_spinner,lk);

                                }


                            }

                        }

                    }
                });

            }

        }

    }

    private void deleteTimeStampFromRegistry(Comment comment)
    {

        if(head_comment!=null)
        {

            DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
            final String conversation_id = currentTopicForConvo.getArguments()
                    .getString(CONVO_ID);
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id())
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.AGREES)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.DISAGREES)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.QUESTION)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();
            mDatabase.child(conversation_id
                            +"_"+head_comment.getComment_id()+"_"+Comment.ANSWER)
                    .child('"'+String.valueOf(comment.getYear())+'"')
                    .child('"'+String.valueOf(comment.getMonth())+'"')
                    .child('"'+String.valueOf(comment.getDay())+'"')
                    .removeValue();

        }

    }

    private void LoadLastOrStartTimestamp(Spinner comment_type)
    {

        //now notify the adapter
        this.currentTopicForConvo.getActivity()
                .runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(marker_filter_area==null)
                        {
                            marker_filter_area= CommentWorker.this.currentTopicForConvo.getView()
                                    .findViewById(R.id.marker_filter_area);
                            marker_filter_area.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            marker_filter_area.setVisibility(View.VISIBLE);
                        }
                        if(comment_list==null)
                        {
                            comment_list= CommentWorker.this.currentTopicForConvo.getView()
                                    .findViewById(R.id.comment_list);
                        }
                        if(comment_list.getAdapter()==null)
                        {
                            comment_list.setAdapter(commentAdapter);
                            commentAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            commentAdapter.notifyDataSetChanged();
                        }
                        Log.i("getCommentsFrHedComent","getActivity "
                                +commentAdapter.commentListUnderHeadComment.size()
                                +" "+comment_list.getVisibility()
                                +" "+commentAdapter.commentListUnderHeadComment.isEmpty());

                        if(commentAdapter.commentListUnderHeadComment.isEmpty()==false)
                        {

                            load_last_timestamp=true;

                            if(currentTopicForConvo.getArguments()!=null)
                            {

                                if(currentTopicForConvo.getArguments().containsKey(STARTING_SIGNATURE))
                                {
                                    boolean is_day_convo=true;
                                    starting_sign=currentTopicForConvo.getArguments().getString(STARTING_SIGNATURE);
                                    Log.i("shareDayConvo","from commmentWorker "+starting_sign);
                                    if(is_day_convo)
                                    {
                                        load_last_timestamp=false;
                                    }

                                }

                            }

                            Comment commnet=new Comment();
                            if(currentTopicForConvo.getArguments()!=null)
                            {
                                if(currentTopicForConvo.getArguments()
                                        .containsKey(STARTING_SIGNATURE))
                                {
                                    starting_sign=currentTopicForConvo.getArguments()
                                            .getString(STARTING_SIGNATURE);

                                }
                            }

                            Log.i("shareDayConvo","from comment worker-"+starting_sign);
                            if(starting_sign.isEmpty()==false)
                            {
                                commnet.setTimestamp(starting_sign);
                                commnet.setFromStartingSign(true);

                            }
                            else
                            {
                                commnet=commentAdapter.commentListUnderHeadComment
                                        .get(commentAdapter.commentListUnderHeadComment.size()-1);
                            }
                            boolean has_pos=commentAdapter.timestamps_positions
                                    .containsKey(commnet.getTimestamp());
                            Log.i("isuhaks","load_last_timestamp="+load_last_timestamp
                                    +" has_pos="+has_pos+" "+commnet.getTimestamp()+" "+starting_sign);

                            if(starting_sign.isEmpty()==false)
                            {
                                final int pos=commentAdapter.timestamps_positions.get(commnet.getTimestamp());
                                commentAdapter.commentListUnderHeadComment.set(pos,commnet);
                                Log.i("isuhaks","pos="+pos);
                                getCommentsUnderTimeStamp(commnet
                                        ,currentTopicForConvo
                                        ,head_comment
                                        , comment_type,
                                        pos);
                                commentAdapter.loading_timestamp_position=pos;
                                commentAdapter.last_timestamp_position=pos;
                                commentAdapter.notifyItemChanged(pos);
                                comment_list.scrollToPosition(pos);
                                comment_list.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        comment_list.smoothScrollToPosition(pos);

                                    }
                                },200);
                            }
                            else if(load_last_timestamp||
                                    has_pos==false)
                            {

                                Log.i("isuhaks","has_pos="+has_pos);

                                if(has_pos)
                                {
                                    final int pos=commentAdapter.timestamps_positions.get(commnet.getTimestamp());
                                    getCommentsUnderTimeStamp(commentAdapter
                                                    .commentListUnderHeadComment
                                                    .get(pos)
                                            ,currentTopicForConvo
                                            ,head_comment
                                            , comment_type,
                                            pos);

                                    commentAdapter.loading_timestamp_position=pos;
                                    commentAdapter.last_timestamp_position=pos;
                                    commentAdapter.notifyItemChanged(pos);
                                    comment_list.scrollToPosition(pos);
                                    comment_list.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            comment_list.smoothScrollToPosition(pos);

                                        }
                                    },200);
                                }
                                else
                                {

                                    final int last=commentAdapter.commentListUnderHeadComment.size()-1;
                                    getCommentsUnderTimeStamp(commentAdapter
                                                    .commentListUnderHeadComment
                                                    .get(last)
                                            ,currentTopicForConvo
                                            ,head_comment
                                            , comment_type,
                                            last);
                                    commentAdapter.loading_timestamp_position=last;
                                    commentAdapter.last_timestamp_position=last;
                                    commentAdapter.notifyItemChanged(last);
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
                });


    }

    HashMap<String,Boolean> process_comments=new HashMap<String, Boolean>();
    public HashMap<String,ArrayList<Comment>> timestamp_comments=new HashMap<String,ArrayList<Comment>>();
    HashMap<String,Boolean> no_more_comments=new HashMap<String,Boolean>();
    public Comment beforeComment =null;
    public Comment afterComment =null;
    public boolean started_listening=false;
    public void getCommentsUnderTimeStamp(Comment timestamp_comment,
                                          Fragment currentTopicForConvo,
                                          Comment head_comment,
                                          Spinner comment_type,int pos)
    {

        Log.i("shareDayConvo","isFrom="+timestamp_comment.isFromStartingSign());
        Log.i("kdhjksa","getCommentsUnderTimeStamp "+timestamp_comment.getTimestamp());
        loading_timestamp_position=pos;
        last_loaded_position=pos;
        commentAdapter.loading_timestamp_position=loading_timestamp_position;
        commentAdapter.last_timestamp_position=loading_timestamp_position;
        if(beforeComment!=null)
        {
            commentAdapter.started_loading_older_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());
        }
        else if(afterComment!=null)
        {
            commentAdapter.started_loading_next_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());
        }
        final String timestamp=timestamp_comment.getYear()+"-"+timestamp_comment.getMonth()+"-"
                +timestamp_comment.getDay();

        final int position=pos;
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        Query query=QueryWorker.
                                getCommentsForHeadCommentQuery(timestamp_comment,comment_type);

                        final String conversation_id = currentTopicForConvo.getArguments()
                                .getString(CONVO_ID);
                        boolean is_standalone=false;
                        if (currentTopicForConvo.getArguments().containsKey(IS_STANDALONE)) {
                            is_standalone = currentTopicForConvo.getArguments().getBoolean(IS_STANDALONE);
                        }
                        if(head_comment!=null)
                        {

                            if(comment_filter.getSelectedItemPosition()==0)
                            {
                                query= CloudWorker.getLetsTalkComments()
                                        .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                        .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                                        .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                                        .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);


                                Log.i("getCommentsUnderTmeStmp",""+timestamp_comment.isFromStartingSign()
                                        +" "+(beforeComment !=null));

                                if(afterComment!=null)
                                {
                                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                                    String output=df.format("yyyy-MM-dd hh:mm:ss a", afterComment.getCreatedDate())
                                            .toString();
                                    Log.i("afterComment",output+" "+afterComment.getComment()+" "
                                            +afterComment.getCreatedDate().getTime()+" "+head_comment.getComment_id());
                                    query= CloudWorker.getLetsTalkComments()
                                            .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                            .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                                            .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                                            .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                                            .whereGreaterThan(OrgFields.USER_CREATED_DATE, afterComment.getCreatedDate())
                                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                                }
                                else if(beforeComment !=null)
                                {

                                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                                    String output=df.format("yyyy-MM-dd hh:mm:ss a", beforeComment.getCreatedDate())
                                            .toString();
                                    Log.i("beforeComment",output);
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
                                                if(s==false)
                                                {
                                                    commentAdapter.no_more_comments.put(timestamp_comment.getTimestamp(),true);
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
                                                .whereGreaterThanOrEqualTo(OrgFields.USER_CREATED_DATE,new Date(ts))
                                                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING);
                                    }



                                }
                                else
                                {
                                    Log.i("nsodha",head_comment.getComment_id());
                                }

                            }
                            //Agree
                            else if(comment_filter.getSelectedItemPosition()==1)
                            {
                                query=CloudWorker.getLetsTalkComments()
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
                                                    commentAdapter.no_more_comments.put(timestamp_comment.getTimestamp(),true);
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
                            }
                            //Disagree
                            else if(comment_filter.getSelectedItemPosition()==2)
                            {
                                query=CloudWorker.getLetsTalkComments()
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
                                                    commentAdapter.no_more_comments.put(timestamp_comment.getTimestamp(),true);
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
                            }
                            //Question
                            else if(comment_filter.getSelectedItemPosition()==3)
                            {
                                query=CloudWorker.getLetsTalkComments()
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
                                                    commentAdapter.no_more_comments.put(timestamp_comment.getTimestamp(),true);
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
                            }
                            //Answers
                            else
                            {
                                query=CloudWorker.getLetsTalkComments()
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
                                                    commentAdapter.no_more_comments.put(timestamp_comment.getTimestamp(),true);
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

                        query.limit(10)
                                .get()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.i("getCommentsUnderTmeStmp","e="+e.getMessage());
                                        Log.i("kdhjksa",timestamp_comment.getTimestamp()+
                                                " e="+e.getMessage());
                                        Log.i(timestamp,"e="+e.getMessage());
                                        getCommentsUnderTimeStamp(timestamp_comment,currentTopicForConvo,
                                                head_comment,comment_type,pos);

                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        commentAdapter.loading_timestamp_cmid="";
                                        if(afterComment==null& queryDocumentSnapshots.isEmpty()==false)
                                        {
                                            Comment fc=new Comment(queryDocumentSnapshots
                                                    .getDocuments().get(0));
                                            Comment lc=new Comment(queryDocumentSnapshots
                                                    .getDocuments().get(queryDocumentSnapshots.getDocuments().size()-1));
                                            Log.i("ujskdjha","before next is called " +
                                                    "fc"+fc.getCreatedDate()
                                                    +" lc="+lc.getCreatedDate());
                                        }
                                        else
                                        {

                                            if(queryDocumentSnapshots.isEmpty()==false)
                                            {
                                                Comment fc=new Comment(queryDocumentSnapshots
                                                        .getDocuments().get(0));
                                                Comment lc=new Comment(queryDocumentSnapshots
                                                        .getDocuments().get(queryDocumentSnapshots.getDocuments().size()-1));
                                                Log.i("ujskdjha","after next is called " +
                                                        "fc"+fc.getCreatedDate()
                                                        +" lc="+lc.getCreatedDate());
                                            }

                                        }

                                        String timestamp=timestamp_comment.getTimestamp();
                                        Log.i("t"+timestamp,queryDocumentSnapshots.size()+"");



                                        Log.i("kdhjksa",timestamp+" succeeded "+queryDocumentSnapshots.isEmpty());
                                        Log.i("getCommentsUnderTmeStmp","queryDocumentSnapshots.isEmpty()="
                                                +queryDocumentSnapshots.isEmpty()+" "+queryDocumentSnapshots.size());
                                        if(queryDocumentSnapshots.isEmpty())
                                        {
                                            no_more_comments.put(timestamp,true);
                                            if(timestamp_comments.containsKey(timestamp)==false)
                                            {
                                                deleteTimeStampFromRegistry(timestamp_comment);
                                                commentAdapter.commentListUnderHeadComment
                                                        .remove(commentAdapter.commentListUnderHeadComment.size()-1);
                                                if(timestamp_comments.size()==0&commentAdapter.commentListUnderHeadComment.size()>0)
                                                {
                                                    LoadLastOrStartTimestamp(comment_type);
                                                }
                                            }
                                            else
                                            {
                                                commentAdapter.setNoMoreComments(no_more_comments);
                                                commentAdapter.notifyItemChanged(pos);
                                            }
                                            currentTopicForConvo.getActivity()
                                                    .runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            if(commentAdapter.started_loading_next_coments
                                                                    .containsKey(timestamp_comment.getTimestamp()))
                                                            {
                                                                commentAdapter.started_loading_next_coments
                                                                        .remove(timestamp_comment.getTimestamp());
                                                                Log.i("slasod","removing "+timestamp_comment.getTimestamp()
                                                                        +" "+timestamp_comment.getComment());
                                                            }
                                                            if(commentAdapter.started_loading_older_coments
                                                                    .containsKey(timestamp_comment.getTimestamp()))
                                                            {
                                                                commentAdapter.started_loading_older_coments
                                                                        .remove(timestamp_comment.getTimestamp());
                                                            }
                                                            if(commentAdapter.comment_position
                                                                    .containsKey(timestamp_comment.getComment_id())
                                                                    &timestamp_comment.getComment_id().isEmpty()==false)
                                                            {
                                                                int pls=commentAdapter.comment_position.get(timestamp_comment.getComment_id());
                                                                commentAdapter.notifyItemChanged(pls);
                                                            }
                                                            else
                                                            {
                                                                commentAdapter.notifyItemChanged(pos);
                                                            }


                                                        }
                                                    });

                                        }
                                        else
                                        {
                                            if(queryDocumentSnapshots.getDocuments().size()<10)
                                            {
                                                no_more_comments.put(timestamp,true);
                                                commentAdapter.no_more_comments.put(timestamp,true);
                                                Comment tms=commentAdapter
                                                        .commentListUnderHeadComment
                                                        .get(position);
                                                if(tms.getComment().trim().isEmpty()==false)
                                                {
                                                    currentTopicForConvo.getActivity()
                                                            .runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {

                                                                    commentAdapter.notifyItemChanged(position);

                                                                }
                                                            });
                                                }
                                            }
                                        }

                                        ArrayList<Comment> commentArrayList=new ArrayList<Comment>();

                                        if(timestamp_comment.getTimestamp()
                                                .equals(commentAdapter.starting_timestamp)&afterComment==null
                                                ||afterComment!=null)
                                        {
                                            for(int i=0
                                                ;i<queryDocumentSnapshots.getDocuments().size();i++)
                                            {


                                                DocumentSnapshot documentSnapshot=queryDocumentSnapshots
                                                        .getDocuments().get(i);
                                                Comment comment=new Comment(documentSnapshot);
                                                comment.setSent(true);
                                                if(process_comments.containsKey(comment.getComment_id()))
                                                {
                                                    continue;
                                                }
                                                else
                                                {
                                                    process_comments.put(comment.getComment_id(),true);
                                                }
                                                commentArrayList.add(comment);
                                                registerToday(comment);
                                                Log.i("iyusa","i="+i+" "+comment.getCreatedDate().getTime());
                                                DateFormat df =
                                                        new DateFormat();
                                                String output=df.format("hh:mm:ss a",
                                                                comment.getCreatedDate())
                                                        .toString();
                                                Log.i("huysja",comment.getComment_id()+" "+output);

                                                if(timestamp_comments.containsKey(timestamp))
                                                {

                                                    ArrayList<Comment> comments=timestamp_comments.get(timestamp);
                                                    comments.add(comment);
                                                    timestamp_comments.put(timestamp,comments);
                                                    commentAdapter.timestamps_comments.put(timestamp,comments);

                                                    Log.i("tx"+timestamp,"1 tsize="
                                                            +timestamp_comments.get(timestamp).size()
                                                            +" "+timestamp_comment.getTimestamp());



                                                }
                                                else
                                                {
                                                    ArrayList<Comment> comments=new ArrayList<Comment>();
                                                    comments.add(comment);
                                                    timestamp_comments.put(timestamp,comments);
                                                    commentAdapter.timestamps_comments.put(timestamp,comments);
                                                    Log.i("tx"+timestamp,"2 tsize="
                                                            +timestamp_comments.get(timestamp).size()+"");


                                                }

                                                Log.i("getCommentsUnderTmeStmp",comment.getComment_id()
                                                        +" "+output+" "+comment.getComment()
                                                        +" "+comment.getCreatedDate().getTime());

                                            }
                                        }
                                        else
                                        {
                                            for(int i=queryDocumentSnapshots.getDocuments().size()-1
                                                ;i>=0;i--)
                                            {


                                                DocumentSnapshot documentSnapshot=queryDocumentSnapshots
                                                        .getDocuments().get(i);
                                                Comment comment=new Comment(documentSnapshot);
                                                comment.setSent(true);
                                                if(process_comments.containsKey(comment.getComment_id()))
                                                {
                                                    continue;
                                                }
                                                else
                                                {
                                                    process_comments.put(comment.getComment_id(),true);
                                                }
                                                commentArrayList.add(comment);
                                                registerToday(comment);
                                                Log.i("iyusa","i="+i+" "+comment.getCreatedDate().getTime());
                                                DateFormat df =
                                                        new DateFormat();
                                                String output=df.format("hh:mm:ss a",
                                                                comment.getCreatedDate())
                                                        .toString();
                                                Log.i("huysja",comment.getComment_id()+" "+output);

                                                if(timestamp_comments.containsKey(timestamp))
                                                {

                                                    ArrayList<Comment> comments=timestamp_comments.get(timestamp);
                                                    comments.add(comment);
                                                    timestamp_comments.put(timestamp,comments);
                                                    commentAdapter.timestamps_comments.put(timestamp,comments);

                                                    Log.i("tx"+timestamp,"1 tsize="
                                                            +timestamp_comments.get(timestamp).size()
                                                            +" "+timestamp_comment.getTimestamp());



                                                }
                                                else
                                                {
                                                    ArrayList<Comment> comments=new ArrayList<Comment>();
                                                    comments.add(comment);
                                                    timestamp_comments.put(timestamp,comments);
                                                    commentAdapter.timestamps_comments.put(timestamp,comments);
                                                    Log.i("tx"+timestamp,"2 tsize="
                                                            +timestamp_comments.get(timestamp).size()+"");


                                                }

                                                Log.i("getCommentsUnderTmeStmp",comment.getComment_id()
                                                        +" "+output+" "+comment.getComment()
                                                        +" "+comment.getCreatedDate().getTime());

                                            }
                                        }




                                        if(timestamp_comments.containsKey(timestamp))
                                        {
                                            Log.i("tx"+timestamp,"on loope finish tsize="
                                                    +timestamp_comments.get(timestamp).size()
                                                    +" "+queryDocumentSnapshots.size());
                                        }
                                        recordReadComments(timestamp);
                                        Comment comment=commentAdapter
                                                .commentListUnderHeadComment
                                                .get(pos);
                                        if(commentAdapter.comment_position
                                                .containsKey(timestamp_comment.getComment_id())
                                                &timestamp_comment.getComment_id().isEmpty()==false)
                                        {
                                            int psl=commentAdapter.comment_position
                                                    .get(timestamp_comment.getComment_id());
                                            comment=commentAdapter
                                                    .commentListUnderHeadComment
                                                    .get(psl);
                                        }


                                        if(commentArrayList.size()>0)
                                        {
                                            Comment mk1=commentArrayList.get(0);
                                            DateFormat df =
                                                    new DateFormat();
                                            String output=df.format("hh:mm:ss a",
                                                            mk1.getCreatedDate())
                                                    .toString();
                                            Log.i("loduis",output+" pos="+pos);
                                            if(afterComment==null)
                                            {
                                                if(commentAdapter.comment_position
                                                        .containsKey(timestamp_comment.getComment_id())
                                                        &timestamp_comment.getComment_id().isEmpty()==false)
                                                {
                                                    int psl=commentAdapter.comment_position
                                                            .get(timestamp_comment.getComment_id());
                                                    commentAdapter.commentListUnderHeadComment.set(psl,mk1);
                                                }
                                                else
                                                {
                                                    commentAdapter.commentListUnderHeadComment.set(pos,mk1);
                                                }

                                            }


                                            Comment commenttx=commentAdapter
                                                    .commentListUnderHeadComment
                                                    .get(pos);
                                            if(commentAdapter.comment_position
                                                    .containsKey(timestamp_comment.getComment_id())&timestamp_comment.getComment_id().isEmpty()==false)
                                            {
                                                int psl=commentAdapter.comment_position
                                                        .get(timestamp_comment.getComment_id());
                                                commenttx=commentAdapter
                                                        .commentListUnderHeadComment
                                                        .get(psl);
                                            }
                                            String outputx=df.format("hh:mm:ss a",
                                                            commenttx.getCreatedDate())
                                                    .toString();
                                            Log.i("loduis",outputx+" pos="+pos);

                                        }

                                        if(timestamp_comments.containsKey(timestamp))
                                        {

                                            //ArrayList<Comment> commentArrayList=timestamp_comments
                                            //.get(timestamp);


                                            if(commentArrayList.size()>0)
                                            {

                                                Log.i("getCommentsUnderTmeStmp",comment.getComment_id());
                                                //commentArrayList.remove(0);
                                                int poscx=pos;
                                                int kl=pos;
                                                if(commentAdapter.comment_position
                                                        .containsKey(timestamp_comment.getComment_id())&timestamp_comment.getComment_id().isEmpty()==false)
                                                {
                                                    int psl=commentAdapter.comment_position
                                                            .get(timestamp_comment.getComment_id());
                                                    kl=psl;
                                                    poscx=psl;

                                                }

                                                if(kl+1<(commentAdapter.commentListUnderHeadComment.size()+commentArrayList.size()))
                                                {
                                                    poscx=kl+1;
                                                }
                                                final int posc=poscx;
                                                Log.i("loduis","posc="+posc);
                                                if(commentArrayList.size()>0)
                                                {
                                                    if(afterComment==null)
                                                    {
                                                        commentArrayList.remove(0);
                                                    }
                                                }

                                                final int yhkl=commentAdapter.commentListUnderHeadComment
                                                        .size()-1;

                                                if(afterComment!=null)
                                                {
                                                    if(commentAdapter.comment_position.containsKey(afterComment.getComment_id()))
                                                    {
                                                        int posl=commentAdapter.comment_position.get(afterComment.getComment_id());
                                                        if(posl+1<commentAdapter.commentListUnderHeadComment.size())
                                                        {
                                                            commentAdapter.commentListUnderHeadComment
                                                                    .addAll(posl+1,commentArrayList);
                                                        }
                                                        else
                                                        {
                                                            commentAdapter.commentListUnderHeadComment
                                                                    .addAll(commentArrayList);
                                                        }
                                                    }
                                                }
                                                else if(kl+1>=(commentAdapter.commentListUnderHeadComment.size()))
                                                {
                                                    commentAdapter.commentListUnderHeadComment
                                                            .addAll(commentArrayList);
                                                }
                                                else{
                                                    commentAdapter.commentListUnderHeadComment
                                                            .addAll(posc,commentArrayList);
                                                }

                                                final boolean jsik=commentAdapter.started_loading_older_coments
                                                        .containsKey(timestamp);

                                                final boolean jsik2=commentAdapter.started_loading_next_coments
                                                        .containsKey(timestamp)&(afterComment!=null);
                                                if(commentAdapter.started_loading_older_coments
                                                        .containsKey(timestamp))
                                                {
                                                    commentAdapter.started_loading_older_coments.remove(timestamp);
                                                }
                                                if(commentAdapter.started_loading_next_coments
                                                        .containsKey(timestamp))
                                                {
                                                    commentAdapter.started_loading_next_coments.remove(timestamp);
                                                }
                                                currentTopicForConvo.getActivity()
                                                        .runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {

                                                                if(jsik)
                                                                {

                                                                    commentAdapter.notifyItemRangeInserted(posc
                                                                            ,commentArrayList.size());

                                                                }
                                                                else if(jsik2)
                                                                {
                                                                    int posl=commentAdapter.comment_position.get(afterComment.getComment_id());
                                                                    if(posl+1<commentAdapter.commentListUnderHeadComment.size())
                                                                    {
                                                                        commentAdapter.notifyItemRangeInserted(posl+1
                                                                                ,commentArrayList.size());
                                                                    }
                                                                    else
                                                                    {
                                                                        commentAdapter.notifyItemRangeInserted(yhkl
                                                                                ,commentArrayList.size());
                                                                    }
                                                                }


                                                                addDayMarkers(commentArrayList.size(),position,
                                                                        false);
                                                                if(queryDocumentSnapshots.isEmpty()==false)
                                                                {
                                                                    int kl=pos;
                                                                    if(commentAdapter.comment_position
                                                                            .containsKey(timestamp_comment.getComment_id())&timestamp_comment.getComment_id().isEmpty()==false)
                                                                    {
                                                                        kl=commentAdapter.comment_position
                                                                                .get(timestamp_comment.getComment_id());
                                                                    }
                                                                    int hug=kl+(commentArrayList.size());
                                                                    if(hug<0)
                                                                    {
                                                                        hug=kl;
                                                                    }

                                                                    final int juhfs=hug;

                                                                    commentAdapter.notifyItemChanged(juhfs);
                                                                    Log.i("yeugdj","user_scrolled="+user_scrolled);

                                                                    if(jsik2)
                                                                    {
                                                                        int posl=commentAdapter.comment_position.get(afterComment.getComment_id());
                                                                        if(posl+1<commentAdapter.commentListUnderHeadComment.size())
                                                                        {
                                                                            comment_list.scrollToPosition(posl+1);

                                                                        }
                                                                        else
                                                                        {
                                                                            comment_list.scrollToPosition(commentAdapter
                                                                                    .commentListUnderHeadComment
                                                                                    .size()-1);
                                                                        }

                                                                    }
                                                                    else if(user_scrolled==false)
                                                                    {
                                                                        comment_list.scrollToPosition(commentAdapter.commentListUnderHeadComment.size()-1);
                                                                    }
                                                                    else
                                                                    {
                                                                        comment_list.scrollToPosition(juhfs);
                                                                    }

                                                                    comment_list.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            if(jsik2)
                                                                            {
                                                                                if(jsik2)
                                                                                {
                                                                                    int posl=commentAdapter.comment_position
                                                                                            .get(afterComment.getComment_id());
                                                                                    if(posl+1<commentAdapter.commentListUnderHeadComment.size())
                                                                                    {
                                                                                        comment_list.smoothScrollToPosition(posl+1);

                                                                                    }
                                                                                    else
                                                                                    {
                                                                                        comment_list.smoothScrollToPosition(commentAdapter
                                                                                                .commentListUnderHeadComment
                                                                                                .size()-1);
                                                                                    }
                                                                                    commentAdapter.notifyDataSetChanged();

                                                                                }
                                                                            }
                                                                            else if(user_scrolled==false)
                                                                            {
                                                                                comment_list.smoothScrollToPosition(commentAdapter.commentListUnderHeadComment.size()-1);
                                                                            }
                                                                            else
                                                                            {
                                                                                comment_list.smoothScrollToPosition(juhfs);
                                                                            }

                                                                            if(started_listening==false&timestamp_comment.isFromStartingSign()==false)
                                                                            {
                                                                                started_listening=true;
                                                                                new CountDownTimer(2000,1000) {
                                                                                    @Override
                                                                                    public void onTick(long l) {

                                                                                    }

                                                                                    @Override
                                                                                    public void onFinish() {
                                                                                        listenForToday(currentTopicForConvo,head_comment,comment_type);
                                                                                    }
                                                                                }
                                                                                        .start();
                                                                            }


                                                                            beforeComment =null;
                                                                            afterComment=null;

                                                                        }
                                                                    },100);

                                                                }

                                                            }
                                                        });



                                            }
                                            else
                                            {
                                                addDayMarkers(0,position,false);
                                                beforeComment =null;
                                                afterComment=null;
                                            }



                                        }






                                    }
                                });

                    }
                });

    }

    private void recordReadComments(String timestamp)
    {

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {

            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            String reader=firebaseUser.getUid();

            if(timestamp_comments.containsKey(timestamp))
            {

                ArrayList<Comment> comments=timestamp_comments.get(timestamp);

                if(comments.size()>0)
                {
                    DateFormat df =
                            new DateFormat();
                    String output1=df.format("hh:mm:ss a",
                                    comments.get(0).getCreatedDate())
                            .toString();
                    String output2=df.format("yyyy-MM-dd hh:mm:ss",
                                    comments.get(comments.size()-1).getCreatedDate())
                            .toString();
                    Log.i("koshyd","output1="+output1);
                    Log.i("koshyd","output2="+output2);

                    JSONArray jsonArray=new JSONArray();

                    for(Comment comment:comments)
                    {

                        if(comment.getCommentator_uid().equals(reader))
                        {
                            continue;
                        }
                        JSONObject jsonObject=new JSONObject();
                        String outputxc=df.format("yyyy-MM-dd hh:mm:ss",
                                        comment.getCreatedDate())
                                .toString();
                        try {
                            final String conversation_id = currentTopicForConvo.getArguments()
                                    .getString(CONVO_ID);
                            jsonObject.put("convo_id",conversation_id);
                            jsonObject.put("read_comment_id",comment.getComment_id());
                            jsonObject.put("day",comment.getTimestamp());
                            jsonObject.put("reader",reader);
                            jsonObject.put("writer",comment.getCommentator_uid());
                            jsonObject.put("read_comment_date",outputxc);
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {

                        }

                    }

                    if(jsonArray.length()>0)
                    {
                        Log.i("koshyd",jsonArray.toString());
                        submitReadReport(timestamp,jsonArray.toString());

                    }



                }

            }

        }

    }

    private void submitReadReport(String timestamp, String jsonArray)
    {


        String url="https://bookie.remould.co.za/comments/submitRead.php";
        byte[] encodedBytes = Base64.encode(jsonArray.getBytes(), Base64.DEFAULT);
        String data=new String(encodedBytes, StandardCharsets.UTF_8);

        RequestQueue queue = Volley.newRequestQueue(currentTopicForConvo.getActivity());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("koshyd",response.replace("<br>","\n"));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("koshyd",error.getMessage());
            }


        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("data",data);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void addDayMarkers(int size, int posc,boolean isFromListening)
    {

        HashMap<String, LabelT> markers=new HashMap<String,LabelT>();
        int startx=posc+size;
        if(startx<commentAdapter.commentListUnderHeadComment.size())
        {
            Comment comment=commentAdapter.commentListUnderHeadComment.get(startx);
            if(comment.isIs_timestamp())
            {
                startx--;
                if(startx<posc)
                {
                    posc=startx;
                }
            }
        }
        final int start=startx;
        Log.i("mjsudka","size="+size+" posc="+posc+" start="+start+" isFromListening="
                +isFromListening);
        for(int x=start;x>=0&x>=posc;x--)
        {
            final int i=x;
            currentTopicForConvo.getActivity()
                    .runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(i>-1&i<commentAdapter.commentListUnderHeadComment.size())
                            {


                                Comment comment=commentAdapter.commentListUnderHeadComment.get(i);
                                LocalDateTime localDateTime=new LocalDateTime(comment.getCreatedDate());

                                //Early Morning 5 to 8 am
                                boolean is_early_morning=(localDateTime.getHourOfDay()>=5&localDateTime.getHourOfDay()<=10);
                                ////Late morning 11 am to 12pm
                                //if(localDateTime.getHourOfDay()>=11&localDateTime.getHourOfDay()<12)
                                boolean is_late_morning=(localDateTime.getHourOfDay()>=11&localDateTime.getHourOfDay()<12);
                                //noon
                                //if(localDateTime.getHourOfDay()==12)
                                boolean is_noon=(localDateTime.getHourOfDay()==12);
                                //Early afternoon 1 to 3pm
                                //if(localDateTime.getHourOfDay()>=13&localDateTime.getHourOfDay()<=15)
                                boolean is_early_afternoon=(localDateTime.getHourOfDay()>=13&localDateTime.getHourOfDay()<=15);
                                //Late afternoon 4 to 5pm
                                //if(localDateTime.getHourOfDay()>=16&localDateTime.getHourOfDay()<17)
                                boolean is_late_afternoon=(localDateTime.getHourOfDay()>=16&localDateTime.getHourOfDay()<17);
                                //Early evening 5 to 7 pm
                                //if(localDateTime.getHourOfDay()>=17&localDateTime.getHourOfDay()<=19)
                                boolean is_early_evening=(localDateTime.getHourOfDay()>=17&localDateTime.getHourOfDay()<=19);
                                //Night 8 pm to 4 am
                                //if(localDateTime.getHourOfDay()>=20&localDateTime.getHourOfDay()<=23
                                //||localDateTime.getHourOfDay()>=1&localDateTime.getHourOfDay()<=4)
                                boolean is_night=(localDateTime.getHourOfDay()>=20&localDateTime.getHourOfDay()<=23
                                        ||localDateTime.getHourOfDay()>=1&localDateTime.getHourOfDay()<=4);

                                boolean is_a_marker=false;
                                if(i==start)
                                {
                                    is_a_marker=true;
                                }
                                else
                                {

                                    if(is_early_morning&markers.containsKey(CommentAdapter.EARLY_MORNING)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_late_morning&markers.containsKey(CommentAdapter.LATE_MORNING)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_noon&markers.containsKey(CommentAdapter.NOON)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_early_afternoon&markers.containsKey(CommentAdapter.EARLY_AFTERNOON)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_late_afternoon&markers.containsKey(CommentAdapter.LATE_AFTERNOON)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_early_evening&markers.containsKey(CommentAdapter.EARLY_EVENING)==false)
                                    {
                                        is_a_marker=true;
                                    }
                                    if(is_night&markers.containsKey(CommentAdapter.NIGHT)==false)
                                    {
                                        is_a_marker=true;
                                    }

                                }


                                if(is_a_marker)
                                {

                                    if(is_early_morning)
                                    {

                                        if(commentAdapter.markersx.containsKey(CommentAdapter.EARLY_MORNING)
                                                &isFromListening)
                                        {
                                            String remid=commentAdapter.markersx.get(CommentAdapter.EARLY_MORNING);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.EARLY_MORNING);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.EARLY_MORNING);
                                        commentAdapter.markersx.put(CommentAdapter.EARLY_MORNING,comment.getComment_id());
                                        markers.put(CommentAdapter.EARLY_MORNING,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_late_morning)
                                    {
                                        if(commentAdapter.markersx.containsKey(CommentAdapter.LATE_MORNING)
                                                &isFromListening)
                                        {
                                            String remid=commentAdapter.markersx.get(CommentAdapter.LATE_MORNING);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.LATE_MORNING);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.LATE_MORNING);
                                        commentAdapter.markersx.put(CommentAdapter.LATE_MORNING,comment.getComment_id());
                                        markers.put(CommentAdapter.LATE_MORNING,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_noon)
                                    {
                                        if(commentAdapter.markersx.containsKey(CommentAdapter.NOON)
                                                &isFromListening)
                                        {
                                            String remid=commentAdapter.markersx.get(CommentAdapter.NOON);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.NOON);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.NOON);
                                        commentAdapter.markersx.put(CommentAdapter.NOON,comment.getComment_id());
                                        markers.put(CommentAdapter.NOON,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_early_afternoon)
                                    {
                                        if(commentAdapter.markersx.containsKey(CommentAdapter.EARLY_AFTERNOON)
                                                &isFromListening)
                                        {

                                            String remid=commentAdapter.markersx.get(CommentAdapter.EARLY_AFTERNOON);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.EARLY_AFTERNOON);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.EARLY_AFTERNOON);
                                        commentAdapter.markersx.put(CommentAdapter.EARLY_AFTERNOON,comment.getComment_id());
                                        markers.put(CommentAdapter.EARLY_AFTERNOON,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_late_afternoon)
                                    {
                                        if(commentAdapter.markersx.containsKey(CommentAdapter.LATE_AFTERNOON)
                                                &isFromListening)
                                        {

                                            String remid=commentAdapter.markersx.get(CommentAdapter.LATE_AFTERNOON);
                                            Log.i("chsjkas","i="+i
                                                    +" "+commentAdapter.comment_position.containsKey(remid)
                                                    +" "+commentAdapter.markers.containsKey(remid));
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    Comment cmgs=commentAdapter.commentListUnderHeadComment.get(pos);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }

                                                }
                                            }

                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.LATE_AFTERNOON);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.LATE_AFTERNOON);
                                        commentAdapter.markersx.put(CommentAdapter.LATE_AFTERNOON,comment.getComment_id());
                                        markers.put(CommentAdapter.LATE_AFTERNOON,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_early_evening)
                                    {

                                        if(commentAdapter.markersx.containsKey(CommentAdapter.EARLY_EVENING)&isFromListening)
                                        {
                                            String remid=commentAdapter.markersx.get(CommentAdapter.EARLY_EVENING);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.EARLY_EVENING);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.EARLY_EVENING);
                                        commentAdapter.markersx.put(CommentAdapter.NIGHT,comment.getComment_id());
                                        markers.put(CommentAdapter.EARLY_EVENING,new LabelT());
                                        commentAdapter.notifyItemChanged(i);
                                    }
                                    if(is_night)
                                    {
                                        if(commentAdapter.markersx.containsKey(CommentAdapter.NIGHT)&isFromListening)
                                        {
                                            String remid=commentAdapter.markersx.get(CommentAdapter.NIGHT);
                                            if(commentAdapter.markers.containsKey(remid))
                                            {
                                                commentAdapter.markers.remove(remid);
                                                if(commentAdapter.comment_position.containsKey(remid))
                                                {
                                                    int pos=commentAdapter.comment_position.get(remid);
                                                    if(pos<commentAdapter.commentListUnderHeadComment.size())
                                                    {
                                                        commentAdapter.notifyItemChanged(pos);
                                                    }
                                                }
                                            }
                                        }
                                        commentAdapter.markers_positions.put(i,CommentAdapter.NIGHT);
                                        commentAdapter.markers.put(comment.getComment_id(),CommentAdapter.NIGHT);
                                        commentAdapter.markersx.put(CommentAdapter.NIGHT,comment.getComment_id());
                                        LabelT jk=new LabelT();
                                        jk.setPosition(i);
                                        markers.put(CommentAdapter.NIGHT,jk);
                                        commentAdapter.notifyItemChanged(i);
                                    }

                                    Log.i("mjsudka","i="+i+" "+comment.getComment()+localDateTime.getHourOfDay()
                                            +" "+comment.isIs_timestamp()
                                            +" is_night="+is_night);
                                }

                            }

                        }
                    });

        }

                        /*

        //Early Morning 5 to 8 am
            if(localDateTime.getHourOfDay()>=5&localDateTime.getHourOfDay()<=8)
            {
                //public static String EARLY_MORNING="es1";
                Log.i("kidhas","s0 position="+position);
                //public static String NIGHT="es6";
                //there is previous and next comment
                if(markers.containsKey(EARLY_MORNING)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(EARLY_MORNING,labelT);
                    markers_positions.put(position,EARLY_MORNING);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }

                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Early Morning");
                }

            }
            //Late morning 11 am to 12pm
            if(localDateTime.getHourOfDay()>=11&localDateTime.getHourOfDay()<12)
            {
                //Late morning 11 am to 12pm
                if(markers.containsKey(LATE_MORNING)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(LATE_MORNING,labelT);
                    markers_positions.put(position,LATE_MORNING);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }

                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Late Morning");
                }
            }
            //noon
            if(localDateTime.getHourOfDay()==12)
            {
                //public static String NOON="es3";
                if(markers.containsKey(NOON)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(NOON,labelT);
                    markers_positions.put(position,NOON);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }

                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Noon");
                }
            }
            //Early afternoon 1 to 3pm
            if(localDateTime.getHourOfDay()>=13&localDateTime.getHourOfDay()<=15)
            {


                //public static String EARLY_AFTERNOON="es4";
                if(markers.containsKey(EARLY_AFTERNOON)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(EARLY_AFTERNOON,labelT);
                    markers_positions.put(position,EARLY_AFTERNOON);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }

                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Early Afternoon");
                }

            }
            //Late afternoon 4 to 5pm
            if(localDateTime.getHourOfDay()>=16&localDateTime.getHourOfDay()<17)
            {
                //public static String LATE_AFTERNOON="es4";
                if(markers.containsKey(LATE_AFTERNOON)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(LATE_AFTERNOON,labelT);
                    markers_positions.put(position,LATE_AFTERNOON);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }


                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Late Afternoon");
                }
            }
            //Early evening 5 to 7 pm
            if(localDateTime.getHourOfDay()>=17&localDateTime.getHourOfDay()<=19)
            {

                if(timestamps_comments.containsKey(comment.getYear()+"-"
                        +comment.getMonth()+"-"+comment.getDay()))
                {
                    ArrayList<Comment> comments=timestamps_comments.get(comment.getYear()+"-"
                            +comment.getMonth()+"-"+comment.getDay());

                    Log.i("kolsad","comments.size="+comments.size()+"");
                    for(Comment comment1:comments)
                    {
                        LocalDateTime lk=new LocalDateTime(comment1.getCreatedDate());
                        Log.i("kolsad","min="+lk.getMinuteOfHour()
                                +" sec="+lk.getSecondOfMinute());
                    }

                }
                //public static String EARLY_EVENING="es5";
                if(markers.containsKey(EARLY_EVENING)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(EARLY_EVENING,labelT);
                    markers_positions.put(position,EARLY_EVENING);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }


                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("Early Evening");
                }
            }
            //Night 8 pm to 4 am
            if(localDateTime.getHourOfDay()>=20&localDateTime.getHourOfDay()<=23
                    ||localDateTime.getHourOfDay()>=1&localDateTime.getHourOfDay()<=4)
            {
                Log.i("kidhas","s0 position="+position);
                //public static String NIGHT="es6";
                //there is previous and next comment
                if(timestamps_comments.containsKey(comment.getYear()+"-"
                        +comment.getMonth()+"-"+comment.getDay()))
                {
                    ArrayList<Comment> comments=timestamps_comments.get(comment.getYear()+"-"
                            +comment.getMonth()+"-"+comment.getDay());

                    Log.i("kolsad","comments.size="+comments.size()+"");
                    for(Comment comment1:comments)
                    {
                        LocalDateTime lk=new LocalDateTime(comment1.getCreatedDate());
                        Log.i("kolsad","min="+lk.getMinuteOfHour()
                                +" sec="+lk.getSecondOfMinute());
                    }

                }
                if(markers.containsKey(NIGHT)==false)
                {
                    LabelT labelT=new LabelT();
                    labelT.setPosition(position);
                    labelT.setSeconds(localDateTime.getSecondOfMinute());
                    markers.put(NIGHT,labelT);
                    markers_positions.put(position,NIGHT);
                    holder.part_of_day.setVisibility(View.VISIBLE);
                }

                if(holder.part_of_day.getVisibility()==View.VISIBLE)
                {
                    holder.part_of_day.setText("At Night");
                }

            }

         */


    }

    public void getCommentsUnderTimeStamp(Comment timestamp_comment,int position)
    {

        this.getCommentsUnderTimeStamp(timestamp_comment,currentTopicForConvo,
                head_comment,
                comment_filter,position);

    }

    public int last_loaded_position=0;
    public int loading_timestamp_position=last_loaded_position;
    public boolean user_scrolled=false;
    private void checkIfAnotherTimeStampAbove(int position,Comment head_comment,Spinner comment_type)
    {

        if(position-1>=0&position-1<commentAdapter.commentListUnderHeadComment.size())
        {
            final int prev_position=position-1;
            last_loaded_position=position;
            final Comment prev_comment=commentAdapter.commentListUnderHeadComment.get(position-1);
            if(prev_comment.isIs_timestamp())
            {
                currentTopicForConvo.getActivity()
                        .runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                LinearLayoutManager ln=(LinearLayoutManager) comment_list.getLayoutManager();
                                int first=ln.findFirstVisibleItemPosition();
                                int last=ln.findLastVisibleItemPosition();

                               if(prev_position>=first&prev_position<last
                                       &prev_position!=loading_timestamp_position
                                       &prev_comment.getComment().isEmpty())
                               {

                                   loading_timestamp_position=prev_position;
                                   commentAdapter.loading_timestamp_position=loading_timestamp_position;
                                   commentAdapter.notifyItemChanged(prev_position);
                                   //getCommentsUnderTimeStamp(prev_comment,currentTopicForConvo,
                                           //head_comment,comment_type,prev_position);

                               }

                            }
                        });
            }

        }

    }

    private void deleteTooOldDaysOfConvoFromRegistry()
    {



    }

    public List<Comment> getCommentListUnderHeadComment()
    {
        return this.commentAdapter.commentListUnderHeadComment;
    }



    /*
    2:END OF COMMENTS FOR HEAD COMMENT
     */

    boolean isKeyboardShowing=false;
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
                                    if(commentAdapter.last_reading_pos>0)
                                    {

                                        //comment_list.scrollToPosition(commentAdapter.last_reading_pos);
                                        comment_list.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                //comment_list.smoothScrollToPosition(commentAdapter.last_reading_pos);
                                                Log.i("keyposa","last_reading_pos="+commentAdapter.last_reading_pos);

                                            }
                                        },1000);
                                    }
                                }
                            }

                        }
                    });


        }
        catch(Exception exception)
        {

        }
    }


}
