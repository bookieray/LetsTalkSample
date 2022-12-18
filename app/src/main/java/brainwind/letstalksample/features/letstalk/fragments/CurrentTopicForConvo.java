package brainwind.letstalksample.features.letstalk.fragments;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.skyfishjy.library.RippleBackground;

import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.LoadingListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.SwipeControllerActions;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.LetsTalkManageEach;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.CommentDate;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import brainwind.letstalksample.features.letstalk.fragments.workers.AfterComments;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentCommunications;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentReader;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentWorker;
import brainwind.letstalksample.fragments.letstalk.current_topic.CurrentTopic;
import brainwind.letstalksample.utils.AndroidUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CurrentTopicForConvo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CurrentTopicForConvo extends Fragment implements CommentListener {

    public static final String SET = "set";
    public static final String TSECONDS = "ts";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String CONVO_ID = "param1";
    public static final String TITLE = "param2";
    public static final String IS_STANDALONE = "param3";
    public static final String TIMEOFFSET = "param4";
    public static final String STARTING_SIGNATURE = "param5";
    public static final String HEAD_COMMENT_ID = "param6";

    //CommentWorker
    CommentWorker commentWorker;
    public Comment reply_comment;


    public CurrentTopicForConvo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param conversation_id Parameter 1.
     * @param activity_title  Parameter 2.
     * @return A new instance of fragment CurrentTopicForConvo.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       boolean is_standalone,
                                       long estimatedServerTimeMs) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       boolean is_standalone,
                                       long estimatedServerTimeMs,String starting_sign) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        fragment.setArguments(args);
        return fragment;
    }
    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       boolean is_standalone,
                                       long estimatedServerTimeMs,String starting_sign,int set) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        fragment.setArguments(args);
        return fragment;
    }



    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       boolean is_standalone,
                                       long estimatedServerTimeMs,String starting_sign,int set,
                                       boolean no_more_prev_for_starting_comment) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putBoolean(OrgFields.NO_MORE_PREV_COMMENTS
                , no_more_prev_for_starting_comment);
        fragment.setArguments(args);
        return fragment;
    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       boolean is_standalone,
                                       long estimatedServerTimeMs,String starting_sign,int set,
                                       boolean no_more_prev_for_starting_comment,long tseconds) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putLong(TSECONDS, tseconds);
        args.putBoolean(OrgFields.NO_MORE_PREV_COMMENTS
                , no_more_prev_for_starting_comment);
        fragment.setArguments(args);
        return fragment;
    }



    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign,long tseconds) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putLong(TSECONDS, tseconds);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign,int set) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign,int set,long tseconds) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putLong(TSECONDS, tseconds);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign,int set,boolean s) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putBoolean(OrgFields.NO_MORE_PREV_COMMENTS, s);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id,
                                       String activity_title,
                                       long estimatedServerTimeMs,String starting_sign,
                                       int set,boolean s,long tseconds ) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putBoolean(OrgFields.NO_MORE_PREV_COMMENTS, s);
        args.putLong(TSECONDS, tseconds);
        fragment.setArguments(args);
        return fragment;

    }

    public static Fragment newInstance(String conversation_id, String activity_title,
                                       boolean is_standalone, long estimatedServerTimeMs,
                                       String starting_sign, int set,
                                       long tseconds) {
        CurrentTopicForConvo fragment = new CurrentTopicForConvo();
        Bundle args = new Bundle();
        args.putString(CONVO_ID, conversation_id);
        args.putString(TITLE, activity_title);
        args.putLong(TIMEOFFSET, estimatedServerTimeMs);
        args.putString(STARTING_SIGNATURE, starting_sign);
        args.putInt(SET, set);
        args.putBoolean(IS_STANDALONE, is_standalone);
        args.putLong(TSECONDS, tseconds);
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @BindView(R.id.view_more_headtopics)
    FloatingActionButton view_more_headtopics;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_topic_for_convo,
                container, false);
        ButterKnife.bind(this, view);
        //getHeadComment();
        //setUpScroll();
        //attachReplySwitch();
        //checkCommentListKeyBoard();
        //setUpFilter();
        if(comment_list!=null)
        {
            setUpCommentList();
        }
        return view;
    }

    private FloatingActionButton close_button_media_dialog;
    private SimpleCustomBottomSheet bottomSheet;
    @OnClick(R.id.view_more_headtopics)
    void ViewMoreTopics()
    {

        bottomSheet = new SimpleCustomBottomSheet(getActivity());
        bottomSheet.show();
        close_button_media_dialog=(FloatingActionButton)bottomSheet
                .findViewById(R.id.close_button_media_dialog);
        close_button_media_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheet.dismiss();

            }
        });

    }


    class SimpleCustomBottomSheet extends BaseBottomSheet {

        public SimpleCustomBottomSheet(@NonNull Activity hostActivity) {
            this(hostActivity, new Config.Builder(hostActivity).build());
        }

        public SimpleCustomBottomSheet(@NonNull Activity hostActivity, @NonNull BaseConfig config) {
            super(hostActivity, config);
        }

        @NonNull
        @Override
        public final View onCreateSheetContentView(@NonNull Context context) {
            View view=LayoutInflater.from(context).inflate(
                    R.layout.more_topics,
                    this,
                    false
            );

            return view;
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getHeadComment();
        CommentListener commentListener=(CommentListener) getActivity();
        if(commentListener!=null)
        {
            commentListener.onFragmentCreated(this);
        }
    }

    public CommentAdapter getCommentAdapter()
    {
        if(commentWorker==null)
        {
            Log.i("dhuasgs","commentWorker==null");
            String conversation_id = this.getArguments()
                    .getString(CONVO_ID);
            commentWorker=new CommentWorker(conversation_id,this);
        }
        Log.i("dhuasgs",""+(commentWorker!=null)+" "+(commentWorker.getCommentAdapter()!=null));
        return commentWorker.getCommentAdapter();
    }

    private void getHeadComment()
    {

        if(commentWorker==null)
        {
            String conversation_id = this.getArguments()
                    .getString(CONVO_ID);
            String title = this.getArguments()
                    .getString(TITLE);
            boolean is_standalone = this.getArguments()
                    .getBoolean(IS_STANDALONE);
            commentWorker=new CommentWorker(conversation_id,CurrentTopicForConvo.this);
            commentWorker.setTitle(title);
            commentWorker.setIs_standalone(is_standalone);
            if(commentWorker.getRootView()!=null)
            {
                Log.i("ydughas","1 rootview is not empty");

                if(getActivity().getIntent().hasExtra(HEAD_COMMENT_ID))
                {
                    String headcomment_id=getActivity().getIntent().getStringExtra(HEAD_COMMENT_ID);
                    commentWorker.getHeadComment(headcomment_id);
                }
                else
                {
                    commentWorker.getHeadComment();
                }

            }
            else
            {
                Log.i("ydughas","2 rootview is empty");
            }

        }
        else
        {
            if(commentWorker.getRootView()!=null)
            {
                Log.i("ydughas","3 rootview is not empty");
            }
            else
            {
                Log.i("ydughas","4 rootview is empty");
            }
        }

        if(commentWorker!=null)
        {
            //commentWorker.getHeadComment(this);
        }


    }

    @Override
    public void foundHeadComment(Comment head_comment) {

        CommentListener commentListener=(CommentListener) getActivity();
        if(commentListener!=null)
        {

            commentListener.foundHeadComment(head_comment);

        }

    }

    @Override
    public void sendCommentWithNewsReference(MediaItemWebSearch mediaItemWebSearch, Comment head_comment) {

    }

    @Override
    public void onReply(Comment comment) {
        reply_comment=comment;
    }

    @Override
    public void onImageComment(NewsFactsMedia newsFactsMedia) {

    }

    @Override
    public void onVideoComment(NewsFactsMedia newsFactsMedia) {

    }

    @Override
    public void GetCommentsUnderTimestamp(Comment comment,int position) {

        Log.i("GetCommentsUnderTimstmp","s1");
        if(comment!=null)
        {
            Log.i("GetCommentsUnderTimstmp",comment.getDateStr());
            //commentWorker.getCommentsUnderTimeStamp(comment,position);
            int timestamp_pos=-1;
            for(int i=0;i<commentWorker.getCommentAdapter().commentListUnderHeadComment.size();i++)
            {

                Comment comment1=commentWorker.getCommentAdapter().commentListUnderHeadComment.get(i);
                if(comment1.getTimestamp().equals(comment.getTimestamp())&comment1.isIs_timestamp()&timestamp_pos==-1)
                {
                    timestamp_pos=i;
                    break;
                }

            }
            commentWorker.getCommentsAfterComment(comment,timestamp_pos);

        }

    }

    @Override
    public void GetCommentsUnderTimestampBeforeComment(Comment timestamp_comment,
                                                       int position,
                                                       Comment before_comment)
    {

        final String timestamp=timestamp_comment.getYear()+"-"+timestamp_comment.getMonth()
                +"-"+timestamp_comment.getDay();
        Log.i(timestamp,"b1");
        if(timestamp_comment!=null)
        {
            Log.i(timestamp,timestamp_comment.getDay()
                    +" "+timestamp_comment.getMonth()
                    +" "+timestamp_comment.getYear());
            //commentWorker.beforeComment=before_comment;
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String output=df.format("hh:mm:ss a",
                            before_comment.getCreatedDate())
                    .toString();
            Log.i("getNxtTimeStp",output);
            commentWorker.getCommentsBeforeComment(before_comment,position);
            //commentWorker.getCommentsUnderTimeStamp(timestamp_comment,position);
        }

    }

    @Override
    public void GetCommentsUnderTimestampAfterComment(Comment timestamp_comment,
                                                      int position,
                                                      Comment after_comment) {
        final String timestamp=timestamp_comment.getYear()+"-"+timestamp_comment.getMonth()
                +"-"+timestamp_comment.getDay();
        Log.i(timestamp,"a1");
        if(timestamp_comment!=null)
        {
            Log.i(timestamp,timestamp_comment.getDay()
                    +" "+timestamp_comment.getMonth()
                    +" "+timestamp_comment.getYear());
            //commentWorker.afterComment=after_comment;
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String output=df.format("hh:mm:ss a",
                            after_comment.getCreatedDate())
                    .toString();
            Log.i("getCommentsUnderTmeStmp",output);
            //commentWorker.getCommentsUnderTimeStamp(after_comment,position);
        }

    }

    @Override
    public void OnShareDayConvo(Comment start_comment)
    {

        LocalDateTime localDateTime=new LocalDateTime(start_comment.getCreatedDate());
        String signStart=start_comment.getYear()
                +"."+start_comment.getMonth()
                +"."+start_comment.getDay()
                +"."+localDateTime.getHourOfDay()
                +"."+localDateTime.getMinuteOfHour()
                +"."+localDateTime.getSecondOfMinute();
        Log.i("isFromStartingSign","signStart="+signStart+" "+start_comment.getCreatedDate().getTime()+" "+(commentWorker.getHead_comment()!=null));
        if(getArguments()!=null)
        {

            String title="";
            String convo_id="";
            boolean is_standalone=true;
            if(getArguments().containsKey(CONVO_ID))
            {
                convo_id=getArguments().getString(CONVO_ID);
            }
            if(getArguments().containsKey(TITLE))
            {
                title=getArguments().getString(TITLE);
            }
            if(getArguments().containsKey(IS_STANDALONE))
            {
                is_standalone=getArguments().getBoolean(IS_STANDALONE);
            }

            int recent_set_number=1;
            if(commentWorker.getCommentAdapter().timestamps_comments.containsKey(start_comment.getTimestamp()))
            {
                recent_set_number=commentWorker.getCommentAdapter().timestamps_comments.get(start_comment.getTimestamp()).size();
            }
            boolean more_previous=true;
            if(commentWorker.getCommentAdapter().no_more_comments_next.containsKey(start_comment.getTimestamp()))
            {
                more_previous=false;
            }
            android.text.format.DateFormat df =
                    new android.text.format.DateFormat();
            String outputx1=df.format("hh:mm:ss a",
                            start_comment.getCreatedDate())
                    .toString();
            Log.i("yusgajs",start_comment.getComment()+" "+start_comment.getDay()
                    +" "+start_comment.getMonth()
                    +" "+start_comment.getYear()
                    +" "+outputx1);

            Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks
                    .getInstance()
                    .createDynamicLink()
                    .setLink(Uri.parse("https://www.bookie.remould.co.za/?"
                            +"convo_id="+convo_id+
                            "&is_st="+is_standalone
                            +"&stc="+signStart
                            +"&set="+recent_set_number
                            +"&nm="+more_previous
                            +"&ht="+commentWorker.getHead_comment().getComment_id()
                            +"&t="+start_comment.getCreatedDate().getTime()))
                    .setDomainUriPrefix("https://remould.page.link")
                    // Open links with this app on Android
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .buildShortDynamicLink()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
                                // Short link created
                                Uri shortLink = task.getResult().getShortLink();
                                Uri flowchartLink = task.getResult().getPreviewLink();

                                Log.i("msujash","shortLink="+shortLink);
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                Memory memory=new Memory(getActivity());
                                String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);
                                String timelabel="";

                                String title=getArguments().getString(TITLE);
                                if(jnm.isEmpty()==false)
                                {

                                    long localtimeoffset=Long.parseLong(jnm);
                                    org.joda.time.LocalDateTime localDateTime
                                            =new org.joda.time.LocalDateTime(localtimeoffset);
                                    timelabel= TimeUtilities.getTimeLabel(localDateTime,start_comment);

                                }
                                if(start_comment.getComment().length()>50)
                                {
                                    share.putExtra(Intent.EXTRA_TEXT, timelabel+"'s Convo"+"\n"
                                            +""+title+"\n"
                                            +start_comment.getCommentator_name()
                                            +":'"+start_comment.getComment().substring(0,49)
                                            +"...' "+shortLink);
                                }
                                else
                                {
                                    share.putExtra(Intent.EXTRA_TEXT, timelabel+"'s Convo"+"\n"
                                            +""+title+"\n"
                                            +start_comment.getCommentator_name()
                                            +":'"+start_comment.getComment()
                                            +"...' "+shortLink);
                                }
                                startActivity(Intent.createChooser(share, "Share "+timelabel+"'s Convo"));

                            } else {
                                // Error
                                // ...
                                Log.i("msujash",task.getException().getMessage());
                            }
                        }
                    });





        }


    }

    @Override
    public void OnCancelReply() {
        //commentWorker.replyCommentWorker=null;
        reply_comment=null;

    }

    @Override
    public void OnNewMessage(Comment comment, int position) {

    }

    @Override
    public void OnErrorLoading(String errorString,int selection) {



    }

    @Override
    public void OnLoadingFinished() {

    }

    @Override
    public void ScrollTo(int position) {
        /*if(commentWorker.comment_list!=null)
        {
            commentWorker.comment_list.scrollToPosition(position);
            commentWorker.comment_list.postDelayed(new Runnable() {
                @Override
                public void run() {
                    commentWorker.comment_list.smoothScrollToPosition(position);
                }
            },200);
        }

         */
    }

    @Override
    public void LeaveConvo() {

    }

    @Override
    public void messageUpdated() {
        CommentListener commentListener=(CommentListener) getActivity();
        if(commentListener!=null)
        {
            commentListener.OnCancelReply();
        }
    }

    @Override
    public void onFragmentCreated(Fragment currentTopicForConvo) {

    }


    @Override
    public void onResume() {
        super.onResume();


        /*if(this.commentWorker.head_comment==null&commentWorker.is_loading_headcomment==false)
        {
            Log.i("onResume","fragment resumed started getting head");
            getHeadComment();
        }
        else
        {
            Log.i("onResume","fragment resumed not getting head");
        }

         */

        if(commentWorker!=null)
        {
            commentWorker.onResume();
        }

    }

    public CommentWorker getCommentWorker() {
        if(commentWorker==null)
        {
            String conversation_id = this.getArguments()
                    .getString(CONVO_ID);
            commentWorker=new CommentWorker(conversation_id,this);
        }
        return commentWorker;
    }

    public void setCommentWorker(CommentWorker commentWorker) {
        this.commentWorker = commentWorker;
    }

    class MessageSwipeController extends ItemTouchHelper.Callback {

        private Drawable imageDrawable;
        private Drawable shareRound;
        private RecyclerView.ViewHolder currentItemViewHolder=null;
        private View mView;
        private float dX=0f;
        private float replyButtonProgress=0;
        private long lastReplyButtonAnimationTime=0;
        private boolean swipeBack=false;
        private boolean isVibrate=false;
        private boolean startTracking=false;
        private SwipeControllerActions swipeControllerActions;
        private Context context;

        MessageSwipeController(Context context, SwipeControllerActions swipeControllerActions)
        {
            this.context=context;
            this.swipeControllerActions=swipeControllerActions;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            //mView = viewHolder.itemView
            mView=viewHolder.itemView;
            imageDrawable=getContext().getDrawable(R.drawable.ic_reply_black_24dp);
            imageDrawable.setBounds(0, 0, 100, 100);
            shareRound=getContext().getDrawable(R.drawable.ic_round_shape);



            return ItemTouchHelper.Callback.makeMovementFlags(ACTION_STATE_IDLE, RIGHT);
        }

        /*
         override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /*
            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                if (swipeBack) {
                    swipeBack = false
                    return 0
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection)
            }
         */
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (swipeBack)
            {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);

        }

        /*
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ACTION_STATE_SWIPE) {
                    setTouchListener(recyclerView, viewHolder)
                }

                if (mView.translationX < convertTodp(130) || dX < this.dX) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    this.dX = dX
                    startTracking = true
                }
                currentItemViewHolder = viewHolder
                drawReplyButton(c)
            }
         */

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState == ACTION_STATE_SWIPE)
            {
                setTouchListener(recyclerView, viewHolder);
            }
            if (mView.getTranslationX() < convertTodp(130) || dX < this.dX)
            {
                super.onChildDraw(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive);
                this.dX = dX;
                startTracking = true;
            }
            currentItemViewHolder = viewHolder;
            drawReplyButton(c);

        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchListener(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder)
        {

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    swipeBack = motionEvent.getAction() == MotionEvent.ACTION_CANCEL
                            || motionEvent.getAction() == MotionEvent.ACTION_UP;
                    if (swipeBack)
                    {
                        if (Math.abs(mView.getTranslationX()) >= convertTodp(100))
                        {
                            swipeControllerActions.showReplyUI(viewHolder.getBindingAdapterPosition());
                        }
                    }
                    return false;
                }
            });

        }

        private void drawReplyButton(Canvas canvas)
        {

            if (currentItemViewHolder == null) {
                return;
            }

            //val translationX = mView.translationX
            float translationX=mView.getTranslationX();
            //val newTime = System.currentTimeMillis()
            long newTime = System.currentTimeMillis();
            //val dt = Math.min(17, newTime - lastReplyButtonAnimationTime)
            long dt=Math.min(17,newTime-lastReplyButtonAnimationTime);
            //lastReplyButtonAnimationTime = newTime
            lastReplyButtonAnimationTime = newTime;
            //val showing = translationX >= convertTodp(30)
            boolean showing = translationX >= convertTodp(30);
            if (showing)
            {
                if (replyButtonProgress < 1.0f)
                {
                    replyButtonProgress += dt / 180.0f;
                    if (replyButtonProgress > 1.0f)
                    {
                        replyButtonProgress = 1.0f;
                    }
                    else
                    {
                        mView.invalidate();
                    }
                }
            }
            else if (translationX <= 0.0f)
            {

                replyButtonProgress = 0f;
                startTracking = false;
                isVibrate = false;

            }
            else
            {

                if (replyButtonProgress > 0.0f)
                {
                    replyButtonProgress -= dt / 180.0f;
                    if (replyButtonProgress < 0.1f)
                    {
                        replyButtonProgress = 0f;

                    }
                    else
                    {
                        mView.invalidate();
                    }
                }

            }

            //val alpha: Int
            int alpha;
            //val scale: Float
            float scale;
            if (showing)
            {
                if (replyButtonProgress <= 0.8f)
                {
                    scale=1.2f * (replyButtonProgress / 0.8f);
                }
                else
                {
                    scale=1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f);
                }
                alpha = (int)Math.min(255f, 255 * (replyButtonProgress / 0.8f));

            }
            else
            {
                scale = replyButtonProgress;
                alpha = (int)Math.min(255f, 255 * replyButtonProgress);
            }

            shareRound.setAlpha(alpha);
            imageDrawable.setAlpha(alpha);
            if (startTracking)
            {
                if (!isVibrate && mView.getTranslationX() >= convertTodp(100))
                {
                    mView.performHapticFeedback(
                            HapticFeedbackConstants.KEYBOARD_TAP,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    isVibrate = true;
                }
            }

            /*
            val x: Int = if (mView.translationX > convertTodp(130)) {
                convertTodp(130) / 2
            } else {
                (mView.translationX / 2).toInt()
            }
             */
            int x;
            if (mView.getTranslationX() > convertTodp(130))
            {
                x=convertTodp(130) / 2;
            }
            else
            {
                x=(int)(mView.getTranslationX() / 2);
            }
            //val y = (mView.top + mView.measuredHeight / 2).toFloat()
            float y=Float.valueOf(mView.getTop() + mView.getMeasuredHeight() / 2);
            //shareRound.colorFilter =
            //            PorterDuffColorFilter(ContextCompat.getColor(context,
            //            R.color.colorE),
            //            PorterDuff.Mode.MULTIPLY)
            shareRound.setColorFilter(new PorterDuffColorFilter(getActivity().getResources().getColor(R.color.colorE)
                    , PorterDuff.Mode.MULTIPLY));
            shareRound.setBounds(
                    (int)(x - convertTodp(18) * scale),
                    (int)(y - convertTodp(18) * scale),
                    (int)(x + convertTodp(50) * scale),
                    (int)(y + convertTodp(50) * scale)
            );
            shareRound.draw(canvas);
            imageDrawable.setBounds(
                    (int)(x - convertTodp(12) * scale),
                    (int)(y - convertTodp(11) * scale),
                    (int)(x + convertTodp(40) * scale),
                    (int)(y + convertTodp(38) * scale)
            );
            imageDrawable.draw(canvas);
            shareRound.setAlpha(255);
            imageDrawable.setAlpha(255);

        }
        /*
            private fun convertTodp(pixel: Int): Int {
                return AndroidUtils.dp(pixel.toFloat(), context)
            }
         */
        private int convertTodp(int pixel)
        {
            return AndroidUtils.dp(Float.valueOf(pixel), context);
        }


    }


    private int quotedMessagePos;
    MessageSwipeController messageSwipeController=new MessageSwipeController(getActivity(),
            new SwipeControllerActions() {
                @Override
                public void showReplyUI(int position) {
                    quotedMessagePos = position;
                    Comment comment=commentWorker.getCommentAdapter().commentListUnderHeadComment.get(position);
                    showQuotedMessage(comment);
                }
            });

    private void showQuotedMessage(Comment comment)
    {

        CommentListener commentListener=(CommentListener) getActivity();
        if(commentListener!=null)
        {
            commentListener.onReply(comment);
        }

    }

    @BindView(R.id.comment_list)
    RecyclerView comment_list;
    private ItemTouchHelper itemTouchHelper;
    private void setUpCommentList()
    {

        itemTouchHelper=new ItemTouchHelper(messageSwipeController);
        itemTouchHelper.attachToRecyclerView(comment_list);

    }


}