package brainwind.letstalksample.features.letstalk;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.GeneratedIds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nex3z.notificationbadge.NotificationBadge;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.scwang.wave.MultiWaveHeader;

import org.firezenk.bubbleemitter.BubbleEmitterView;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import brainwind.letstalksample.R;
import brainwind.letstalksample.bookie_activity.BookieActivity;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.adapters.TestAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.CommentDate;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import brainwind.letstalksample.features.letstalk.fragments.item.TMDay;
import brainwind.letstalksample.utils.ImageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class TestGroup2 extends AppCompatActivity implements CommentTimeStampNavigation {

    //OPERATIONS
    public static final int CONVO=0;
    public static final int HEAD_COMMENT_TIMESTAMPS=1;
    public static final int HEAD_COMMENT_COMMENTS=2;
    public static final int HEAD_COMMENTS=3;
    public int DOWNLOAD_OP=CONVO;

    //The conversation title and toolbar
    @BindView(R.id.waveHeader)
    MultiWaveHeader waveHeader;

    @BindView(R.id.label)
    ExpandableTextView label;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.edit_activity)
    TextView edit_activity;
    String conversation_id="";
    BookieActivity bookieActivity;
    @BindView(R.id.loading_area)
    RelativeLayout loading_area;
    private CountDownTimer countDownTimer;


    private void LoadConvo()
    {

        DOWNLOAD_OP=CONVO;
        if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
        {
            conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);
        }
        else
        {
            //test dummy conversation_id
            conversation_id="S3aNh6Jq7ZajBiJS2jut";
        }


        CloudWorker.getActivities()
                .document(conversation_id)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        failedToDownload(e);

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot!=null)
                        {

                            bookieActivity=new BookieActivity(documentSnapshot);
                            getNewsSuggestions();
                            showConvo();
                            LoadHeadComments();
                            searchByImagesOnWeb();
                            searchVideosOnWeb();
                        }


                    }
                });

    }

    MaterialDialog materialDialog;
    private void failedToDownload(Exception e)
    {

        String title="";
        if(DOWNLOAD_OP==CONVO)
        {
            title="Error getting convo";
        }
        if(DOWNLOAD_OP==HEAD_COMMENT_TIMESTAMPS)
        {
            title="Error getting timestamps";
        }
        if(DOWNLOAD_OP==HEAD_COMMENTS)
        {
            title="Error getting head comments";
        }
        if(DOWNLOAD_OP==HEAD_COMMENT_COMMENTS)
        {
            title="Error getting comments for head comment";
        }
        String content=e.getMessage();

        materialDialog=new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .cancelable(false)
                .positiveText("Internet connection on browser")
                .negativeText("Try again")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(DOWNLOAD_OP==CONVO)
                        {
                            LoadConvo();
                        }
                        if(DOWNLOAD_OP==HEAD_COMMENT_TIMESTAMPS)
                        {
                            getTimestampsForHeadComment(headcomment);
                        }
                        if(DOWNLOAD_OP==HEAD_COMMENTS)
                        {
                            LoadHeadComments();
                        }
                        if(DOWNLOAD_OP==HEAD_COMMENT_COMMENTS)
                        {
                            getComments(current_timestamp_comment,current_timestamp_comment.getAdapter_position());
                        }

                    }
                })
                .neutralText("Close Convo")
                .show();

    }

    private boolean isAdmin=false;
    private void showConvo()
    {
        label.setText(bookieActivity.getActivity_title());
        if(bookieActivity.isIs_news_reference())
        {
            status.setTextColor(getResources().getColor(R.color.green));
            status.setText(bookieActivity.getNews_source());
        }
        else
        {
            status.setTextColor(getResources().getColor(R.color.red));
            status.setVisibility(View.GONE);

        }

        if(bookieActivity.getOrg_main_color().isEmpty()==false)
        {
            int backColor=Color.parseColor(bookieActivity.getOrg_main_color());
            waveHeader.setStartColor(backColor);
            waveHeader.setCloseColor(backColor);
        }

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {

            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            Log.i("showConvo","uid="+firebaseUser.getUid());
            if(firebaseUser.getUid().trim().equals(bookieActivity.getAdmin_uid()))
            {
                edit_activity.setVisibility(View.VISIBLE);
                isAdmin=true;
            }
            else
            {
                edit_activity.setVisibility(View.GONE);
            }

        }
        else
        {
            edit_activity.setVisibility(View.GONE);
        }

    }
    //When user taps on the toolbar and top card
    @OnClick({R.id.uystra,R.id.waveHeader,R.id.topPanel,R.id.toolbar,R.id.label,R.id.activity_type,R.id.edit_activity,R.id.status})
    void TopCardClicked()
    {
        if(bookieActivity!=null)
        {

            if(bookieActivity.isIs_news_reference()&isAdmin)
            {
                new MaterialDialog.Builder(this)
                        .title("Edit or News Source")
                        .content(bookieActivity.getActivity_desc()+". Do you want to edit the convo or view news source")
                        .positiveText("Edit Convo")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .negativeText("View News Source")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }
            else
            {

            }

        }
    }

    //Creating a comment
    @BindView(R.id.disable_typing_area)
    FrameLayout disable_typing_area;
    @BindView(R.id.disable_typing_area_label)
    TextView disable_typing_area_label;
    @BindView(R.id.typing_area)
    LinearLayout typing_area;

    //Viewing the comments - Head Comment
    //The head comment vaiables
    ArrayList<Comment> headcommentArrayList=new ArrayList<Comment>();
    Comment headcomment=null;
    //The head comment views
    //The head comment parent view that holds all the views to display
    @BindView(R.id.headcomment_list_label)
    TextView headcomment_list_label;
    @BindView(R.id.head_comment_convo)
    RelativeLayout head_comment_convo;
    @BindView(R.id.comment_name)
    TextView comment_name;
    @BindView(R.id.comment_type)
    TextView head_comment_type;
    @BindView(R.id.comment_view)
    RelativeLayout comment_view;
    @BindView(R.id.comment)
    ExpandableTextView comment;
    @BindView(R.id.time_sent)
    TextView time_sent;
    @BindView(R.id.num_people_read)
    TextView num_people_read;
    @BindView(R.id.hjk)
    EmojiconTextView hjk;
    //The head comments navigation views
    @BindView(R.id.prev_headcomment_area)
    FrameLayout prev_headcomment_area;
    @BindView(R.id.next_headcomment_area)
    FrameLayout next_headcomment_area;
    @BindView(R.id.np_headcommment)
    FloatingActionButton np_headcommment;
    @BindView(R.id.prev_headcommment)
    FloatingActionButton prev_headcomment;
    @BindView(R.id.badge)
    NotificationBadge badge;
    //The methods for head comments
    //viewing a head comment
    private void showHeadCommentx(Comment commentx)
    {

        loading_area.setVisibility(View.GONE);
        comment_filter.setVisibility(View.GONE);
        tabs.setVisibility(View.GONE);
        headcomment=commentx;
        //RelativeLayout head_comment_rootview=(RelativeLayout) findViewById(R.id.head_comment_rootview);
        comment_name.setText(commentx.getCommentator_name());
        comment.setText(commentx.getComment());
        time_sent.setText(commentx.getTimeStr());
        if(testAdapter==null)
        {
            initiaLizeCommentList();
        }
        if(testAdapter.getActualNumberofComments()>commentx.getNum_comments())
        {
            num_people_read.setText(NumUtils.getAbbreviatedNum(testAdapter.getActualNumberofComments())+" comments");
        }
        else
        {
            num_people_read.setText(NumUtils.getAbbreviatedNum(commentx.getNum_comments())+" comments");
        }
        num_people_read.setVisibility(View.VISIBLE);
        if(commentx.getComment_type()==Comment.AGREES)
        {
            hjk.setText("\uD83D\uDC4D");
            comment_name.setTextColor(getResources().getColor(R.color.black));
            comment_view.setBackground(getResources().getDrawable(R.drawable.right_tv_bg));
            head_comment_type.setTextColor(getResources().getColor(R.color.purple_700));
            head_comment_type.setText("Agrees with Convo Title");
            EmojiconTextView textView=comment.findViewWithTag("kosps");
            textView.setTextColor(getResources().getColor(R.color.black));
            time_sent.setTextColor(getResources().getColor(R.color.black));
            num_people_read.setTextColor(getResources().getColor(R.color.black));
            Drawable dh= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(Color.RED)
                    .build();
            badge.setBadgeBackgroundDrawable(dh);
            badge.setTextColor(getResources().getColor(R.color.white));
            np_headcommment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            np_headcommment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            prev_headcomment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.glass2)));
            prev_headcomment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));


        }
        if(commentx.getComment_type()==Comment.DISAGREES)
        {
            hjk.setText("\uD83D\uDC4E");
            comment_name.setTextColor(getResources().getColor(R.color.white));
            head_comment_type.setTextColor(getResources().getColor(R.color.white));
            comment_view.setBackground(getResources().getDrawable(R.drawable.left_tv_bg));
            EmojiconTextView textView=comment.findViewWithTag("kosps");
            textView.setTextColor(getResources().getColor(R.color.white));
            head_comment_type.setText("Disagrees with Convo Title");
            time_sent.setTextColor(getResources().getColor(R.color.white));
            num_people_read.setTextColor(getResources().getColor(R.color.white));
            Drawable dh= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(Color.WHITE)
                    .build();
            badge.setBadgeBackgroundDrawable(dh);
            badge.setTextColor(getResources().getColor(R.color.red));
            np_headcommment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            np_headcommment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));

            prev_headcomment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            prev_headcomment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));


        }
        if(commentx.getComment_type()==Comment.QUESTION)
        {
            hjk.setText("✋");
            comment_name.setTextColor(getResources().getColor(R.color.black));
            comment_view.setBackground(getResources().getDrawable(R.drawable.right_tv_bg));
            head_comment_type.setTextColor(getResources().getColor(R.color.blue));
            head_comment_type.setText("Questions the Convo title");
            EmojiconTextView textView=comment.findViewWithTag("kosps");
            textView.setTextColor(getResources().getColor(R.color.black));
            time_sent.setTextColor(getResources().getColor(R.color.black));
            num_people_read.setTextColor(getResources().getColor(R.color.black));
            Drawable dh= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(Color.RED)
                    .build();
            badge.setBadgeBackgroundDrawable(dh);
            badge.setTextColor(getResources().getColor(R.color.white));
            np_headcommment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            np_headcommment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            prev_headcomment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.glass2)));
            prev_headcomment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));


        }
        if(commentx.getComment_type()==Comment.ANSWER)
        {
            hjk.setText("✋");
            comment_view.setBackground(getResources().getDrawable(R.drawable.right_tv_bg));
            head_comment_type.setTextColor(getResources().getColor(R.color.blue));
            head_comment_type.setText("Answers the Convo title");
            EmojiconTextView textView=comment.findViewWithTag("kosps");
            textView.setTextColor(getResources().getColor(R.color.black));
            time_sent.setTextColor(getResources().getColor(R.color.black));
            num_people_read.setTextColor(getResources().getColor(R.color.black));
            Drawable dh= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(Color.RED)
                    .build();
            badge.setBadgeBackgroundDrawable(dh);
            badge.setTextColor(getResources().getColor(R.color.white));
            np_headcommment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            np_headcommment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            prev_headcomment.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.glass2)));
            prev_headcomment.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.black)));


        }


        Log.i("showHeadComment",commentx.getComment());
        typing_area.setVisibility(View.VISIBLE);
        disable_typing_area.setVisibility(View.VISIBLE);
        disable_typing_area_label.setText("Please wait");

        HeadCommentIndex();
        getTimestampsForHeadComment(commentx);
        getTimestampsForVoiceNotesForHeadComment(commentx);

    }

    @BindView(R.id.comment_list_vn)
    RecyclerView comment_list_vn;
    private void getTimestampsForVoiceNotesForHeadComment(Comment commentx)
    {

        tabs.setVisibility(View.VISIBLE);
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String qtxt=headcomment.getComment_id();

        if(comment_filter.getSelectedItemPosition()==0)
        {
            //All
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES+"_vn";
        }
        else if(comment_filter.getSelectedItemPosition()==1)
        {
            //Agree
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES+"_vn";
        }
        else if(comment_filter.getSelectedItemPosition()==2)
        {
            //Disagree
            qtxt=headcomment.getComment_id()+"_"+Comment.DISAGREES+"_vn";
        }
        else if(comment_filter.getSelectedItemPosition()==3)
        {
            //Question
            qtxt=headcomment.getComment_id()+"_"+Comment.QUESTION+"_vn";
        }
        else if(comment_filter.getSelectedItemPosition()==4)
        {
            //Answers
            qtxt=headcomment.getComment_id()+"_"+Comment.ANSWER+"_vn";
        }

        //testing
        qtxt=qtxt.replace("_vn","");

        initiaLizeCommentVCnotesList();

        mDatabase.child(qtxt)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {



                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {



                    }
                });


    }

    TestAdapter vcNotetestAdapter;
    private void initiaLizeCommentVCnotesList()
    {

        if(vcNotetestAdapter==null)
        {
            vcNotetestAdapter=new TestAdapter(this);
            //comment_list.setLayoutManager(new LinearLayoutManager(getContext()));
            comment_list_vn.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            comment_list_vn.setAdapter(vcNotetestAdapter);
        }

    }

    @BindView(R.id.comment_filter)
    Spinner comment_filter;
    @BindView(R.id.tabs)
    TabLayout tabs;
    private void getTimestampsForHeadComment(Comment commentx)
    {

        DOWNLOAD_OP=HEAD_COMMENT_TIMESTAMPS;
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String qtxt=headcomment.getComment_id();

        if(comment_filter.getSelectedItemPosition()==0)
        {
            //All
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==1)
        {
            //Agree
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==2)
        {
            //Disagree
            qtxt=headcomment.getComment_id()+"_"+Comment.DISAGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==3)
        {
            //Question
            qtxt=headcomment.getComment_id()+"_"+Comment.QUESTION;
        }
        else if(comment_filter.getSelectedItemPosition()==4)
        {
            //Answers
            qtxt=headcomment.getComment_id()+"_"+Comment.ANSWER;
        }

        Log.i("getTmstmpsFrHadCmmnt","qtxt="+qtxt);

        initiaLizeCommentList();
        
        testAdapter.is_loading=true;
        testAdapter.notifyDataSetChanged();

        mDatabase.child(qtxt)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("getTmstmpsFrHadCmmnt",e.getMessage());
                        failedToDownload(e);

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        Log.i("getTmstmpsFrHadCmmnt","onSuccess dataSnapshot.getValue() has "+(dataSnapshot.getValue()!=null));

                        if(dataSnapshot.getValue()!=null)
                        {
                            convertAndOrderJsonTimestamps(dataSnapshot);
                        }


                    }
                });


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
    public int last_select_filter_option=-1;
    HashMap<String,Boolean> process_comments=new HashMap<String, Boolean>();
    public HashMap<String,ArrayList<Comment>> timestamp_comments=new HashMap<String,ArrayList<Comment>>();
    HashMap<String,Boolean> no_more_comments=new HashMap<String,Boolean>();
    public int last_loaded_position=0;
    public int loading_timestamp_position=last_loaded_position;
    HashMap<String, ArrayList<Comment>> ident_list=new HashMap<String, ArrayList<Comment>>();
    private void convertAndOrderJsonTimestamps(DataSnapshot dataSnapshot)
    {

        String qtxt=headcomment.getComment_id();

        if(comment_filter.getSelectedItemPosition()==0)
        {
            //All
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==1)
        {
            //Agree
            qtxt=headcomment.getComment_id()+"_"+Comment.AGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==2)
        {
            //Disagree
            qtxt=headcomment.getComment_id()+"_"+Comment.DISAGREES;
        }
        else if(comment_filter.getSelectedItemPosition()==3)
        {
            //Question
            qtxt=headcomment.getComment_id()+"_"+Comment.QUESTION;
        }
        else if(comment_filter.getSelectedItemPosition()==4)
        {
            //Answers
            qtxt=headcomment.getComment_id()+"_"+Comment.ANSWER;
        }
        JSONObject jsonObject= null;
        try {

            jsonObject = new JSONObject(dataSnapshot.getValue().toString());
            //Iterator<String> keys = jsonObject.keys();
            JSONArray years_list=jsonObject.names();
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

            }
            //looping through the list of the objects
            // that singularly contain day, month and year
            //looping from the most previuous first and added to the adapter list
            for(int i=starting;i>=0;i--)
            {

                TMDay tmDay2=tmDays.get(i);
                String timestamp=tmDay2.getDay()+"-"+tmDay2.getMonth()
                        +"-"+tmDay2.getYear();
                String timestamp2=tmDay2.getYear()+"-"+tmDay2.getMonth()
                        +"-"+tmDay2.getDay();
                Log.i("timstar",timestamp2);

                if(testAdapter.started_loading_older_coments.containsKey(timestamp2))
                {
                    testAdapter.started_loading_older_coments.remove(timestamp2);
                }
                //is it already added to the comment list adapter to prevent duplicates
                if(timestamps_dates.containsKey(timestamp)==false)
                {

                    Comment comment=new Comment();
                    comment.setTimestamp(tmDay2);


                    testAdapter.commentListUnderHeadComment.add(comment);
                    int pl=testAdapter.commentListUnderHeadComment.size()-1;
                    Log.i("adsxefs","adding "+comment.getTimestamp()+" "+pl);
                    CommentDate commentDate=new CommentDate(comment);
                    timestamps_dates.put(timestamp,commentDate);
                }

                if(i==0)
                {

                }

            }

            ArrayList<Comment> timetsmaps=new ArrayList<Comment>();
            if(tmDays.size()>0)
            {
                for(int i=tmDays.size()-1;i>=0;i--)
                {
                    TMDay tmDay=tmDays.get(i);
                    Comment comment=new Comment();
                    comment.setTimestamp(tmDay);
                    timetsmaps.add(comment);

                }

                ident_list.put(qtxt,timetsmaps);

            }

            Log.i("getTmstmpsFrHadCmmnt","timetsmaps.size="+timetsmaps.size()+" "+tmDays.size());
            testAdapter.commentListUnderHeadComment.clear();
            vcNotetestAdapter.commentListUnderHeadComment.clear();
            testAdapter.commentListUnderHeadComment.addAll(timetsmaps);
            vcNotetestAdapter.commentListUnderHeadComment.addAll(timetsmaps);
            testAdapter.is_loading=false;
            testAdapter.notifyDataSetChanged();
            vcNotetestAdapter.is_loading=false;
            vcNotetestAdapter.notifyDataSetChanged();

            if(timetsmaps.size()>0)
            {
                OP=INITIAL_C;
                int timestamp_pos=testAdapter.commentListUnderHeadComment.size()-1;
                getComments(testAdapter.commentListUnderHeadComment.get(timestamp_pos),timestamp_pos);
            }



        } catch (JSONException e) {
            e.printStackTrace();

        }


    }

    //The head comment onclick events
    //variables for navigating the head comments
    int selected_headcomment_position=0;
    int num_views_headcomments=0;
    HashMap<String,Integer> lujk=new HashMap<String,Integer>();
    int last_known_pos=0;
    @OnClick(R.id.np_headcommment)
    void NextPrevHeadComment()
    {

        if(busy)
        {
            Toast.makeText(this, "Busy loading comments, please wait", Toast.LENGTH_SHORT).show();
        }
        else if(headcommentArrayList.size()>1&selected_headcomment_position+1<headcommentArrayList.size())
        {


            np_headcommment.setImageResource(R.drawable.baseline_keyboard_arrow_right_purple_700_24dp);
            selected_headcomment_position++;

            Comment commentx=headcommentArrayList.get(selected_headcomment_position);
            showHeadCommentx(commentx);
            if(selected_headcomment_position+1>=headcommentArrayList.size())
            {
                next_headcomment_area.setVisibility(View.GONE);
            }
            if(lujk.containsKey(commentx.getComment_id())==false)
            {
                if(selected_headcomment_position>last_known_pos)
                {
                    last_known_pos=selected_headcomment_position;
                    num_views_headcomments++;
                }
                int y=headcommentArrayList.size()-num_views_headcomments;
                if(y>0)
                {

                    badge.setText(NumUtils.getAbbreviatedNum(y));
                }
                else
                {
                    badge.setNumber(y);
                }
                lujk.put(commentx.getComment_id(),num_views_headcomments);
            }
            else if(badge.getTextView().getText().toString().trim().equals("0")==false)
            {
                //remove later for testing
                if(selected_headcomment_position>last_known_pos)
                {
                    last_known_pos=selected_headcomment_position;
                    num_views_headcomments++;
                }

                int y=headcommentArrayList.size()-num_views_headcomments;
                if(y>0)
                {

                    badge.setText(NumUtils.getAbbreviatedNum(y));
                }
                else
                {
                    badge.setNumber(y);
                }
                lujk.put(commentx.getComment_id(),num_views_headcomments);
            }

            if(selected_headcomment_position>=headcommentArrayList.size()-4&started_after_scroll==false&no_more_headcomments==false)
            {

                started_after_scroll=true;
                LoadHeadComments();

            }

            int how_many_comments_before=selected_headcomment_position-0;
            if(selected_headcomment_position>0)
            {
                prev_headcomment_area.setVisibility(View.VISIBLE);

            }
            else
            {
                prev_headcomment_area.setVisibility(View.GONE);
            }

            Log.i("lsjhdfks","selected_headcomment_position="+selected_headcomment_position+" how_many_comments_before="+how_many_comments_before);

        }
        else
        {
            next_headcomment_area.setVisibility(View.GONE);
        }

    }
    @OnClick(R.id.prev_headcommment)
    void PrevHeadComment()
    {


        if(selected_headcomment_position>0)
        {


            selected_headcomment_position--;
            Comment commentx=headcommentArrayList.get(selected_headcomment_position);
            showHeadCommentx(commentx);
            int how_many_comments_before=selected_headcomment_position-0;
            if(how_many_comments_before==0)
            {
                prev_headcomment_area.setVisibility(View.GONE);
                np_headcommment.setImageResource(R.drawable.baseline_keyboard_arrow_right_purple_700_24dp);
            }


        }

        if(headcommentArrayList.size()>1&selected_headcomment_position+1<headcommentArrayList.size())
        {
            next_headcomment_area.setVisibility(View.VISIBLE);
        }



    }
    void HeadCommentIndex()
    {

        int index=selected_headcomment_position+1;

        if(index<1000)
        {
            headcomment_list_label.setText(Html.fromHtml("<b><strong>"+NumUtils.getAbbreviatedNum(index)
                    +"<sup>th</sup>/"+NumUtils.getAbbreviatedNum(headcommentArrayList.size())+"</strong></b>"));
        }
        else
        {
            headcomment_list_label.setText(Html.fromHtml("<b><strong>"+NumUtils.getAbbreviatedNum(index)
                    +"/"+NumUtils.getAbbreviatedNum(headcommentArrayList.size())+"</strong></b>"));
        }
    }
    @BindView(R.id.loading_head_comments)
    ProgressBar loading_head_comments;
    //methods for loading Head Comments
    private void LoadHeadComments()
    {

        DOWNLOAD_OP=HEAD_COMMENTS;
        //add a agree item
        loading_head_comments.setVisibility(View.VISIBLE);
        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,"S3aNh6Jq7ZajBiJS2jut")
                .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                .orderBy(OrgFields.NUM_COMMENTS,Query.Direction.DESCENDING);

        if(headcommentArrayList.size()>0)
        {
            Comment lastcomment=headcommentArrayList.get(headcommentArrayList.size()-1);
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,"S3aNh6Jq7ZajBiJS2jut")
                    .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                    .whereGreaterThan(OrgFields.USER_CREATED_DATE,lastcomment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                    .orderBy(OrgFields.NUM_COMMENTS,Query.Direction.DESCENDING);
        }

        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        started_after_scroll=false;
                        Log.i("LoadHeadComments","e="+e.getMessage());
                        Toast.makeText(TestGroup2.this
                                ,"Getting Head Comments failed", Toast.LENGTH_SHORT).show();
                        Toast.makeText(TestGroup2.this
                                ,e.getMessage(), Toast.LENGTH_SHORT).show();
                        if(headcommentArrayList.size()==0)
                        {
                            failedToDownload(e);
                        }

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        loading_head_comments.setVisibility(View.GONE);
                        started_after_scroll=false;
                        Log.i("LoadHeadComments","queryDocumentSnapshots.isEmpty="+(queryDocumentSnapshots.isEmpty())
                                +" queryDocumentSnapshots.size="+queryDocumentSnapshots.size());

                        if(queryDocumentSnapshots.isEmpty()==false)
                        {

                            DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);
                            Comment commentx=new Comment(documentSnapshot);
                            headcommentArrayList.add(commentx);
                            if(headcomment==null)
                            {
                                showHeadCommentx(commentx);
                            }
                            //setUpViewPager();

                            //selected_headcomment_position=0;

                            for(int i=0;i<queryDocumentSnapshots.size();i++)
                            {
                                DocumentSnapshot documentSnapshotxx=queryDocumentSnapshots.getDocuments().get(i);
                                Comment commentxx=new Comment(documentSnapshotxx);
                                commentxx.setComment_type(Comment.DISAGREES);
                                commentxx.setComment("2");

                                Comment commentxx1=new Comment(documentSnapshotxx);
                                commentxx1.setComment_type(Comment.QUESTION);
                                commentxx1.setComment("3");

                                Comment commentxx2=new Comment(documentSnapshotxx);
                                commentxx2.setComment_type(Comment.ANSWER);
                                commentxx2.setComment("4");

                                headcommentArrayList.add(commentxx);
                                headcommentArrayList.add(commentxx1);
                                headcommentArrayList.add(commentxx2);
                            }




                        }
                        else
                        {
                            no_more_headcomments=true;
                            Log.i("LoadHeadComments","no_more_headcomments="+no_more_headcomments);

                        }

                        if(headcommentArrayList.size()>1)
                        {

                            if(num_views_headcomments==0)
                            {
                                num_views_headcomments++;
                            }
                            badge.setText(NumUtils.getAbbreviatedNum(headcommentArrayList.size()-num_views_headcomments));
                            next_headcomment_area.setVisibility(View.VISIBLE);


                        }
                        HeadCommentIndex();


                    }
                });


    }

    //Viewing the comments - Comments under the Head Comment
    //The views for viewing the comments
    @BindView(R.id.marker_filter_area)
    RelativeLayout marker_filter_area;
    @BindView(R.id.comment_list)
    RecyclerView comment_list;
    //methods for loading comments under head comment
    //variables for getting comments under head comment
    boolean busy=false;
    boolean no_more_headcomments=false;
    boolean started_after_scroll=false;
    private TestAdapter testAdapter;
    private void initiaLizeCommentList()
    {
        if(testAdapter==null)
        {
            testAdapter=new TestAdapter(this);
            //comment_list.setLayoutManager(new LinearLayoutManager(getContext()));
            comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            comment_list.setAdapter(testAdapter);
            checkCommentListKeyBoard();
        }
    }

    private final static int INITIAL_C=0;
    private final static int PREV_C=1;
    private final static int NEXT_C=2;
    private int OP=INITIAL_C;
    Comment current_timestamp_comment;
    boolean initial=true;
    private void getComments(Comment timestamp_comment,int timestamp_pos)
    {


        this.current_timestamp_comment=timestamp_comment;
        DOWNLOAD_OP=HEAD_COMMENT_COMMENTS;
        comment_filter.setVisibility(View.VISIBLE);

        busy=true;
        initiaLizeCommentList();
        if(OP==PREV_C)
        {
            testAdapter.notifyItemChanged(timestamp_comment.getAdapter_position());
        }
        if(OP==INITIAL_C||OP==PREV_C)
        {
            testAdapter.started_loading_older_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());
        }
        else if(OP==NEXT_C)
        {
            testAdapter.started_loading_next_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());
        }
        if(OP==INITIAL_C)
        {
            testAdapter.is_loading=true;
            testAdapter.notifyDataSetChanged();
        }

        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,headcomment.getComment_id())
                .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);

        if(OP==PREV_C)
        {
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.PARENT_COMMENT_ID,headcomment.getComment_id())
                    .whereEqualTo(OrgFields.DAY,timestamp_comment.getDay())
                    .whereEqualTo(OrgFields.MONTH,timestamp_comment.getMonth())
                    .whereEqualTo(OrgFields.YEAR,timestamp_comment.getYear())
                    .whereLessThan(OrgFields.USER_CREATED_DATE,timestamp_comment.getCreatedDate())
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);
        }

        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        busy=false;
                        testAdapter.is_loading=false;
                        testAdapter.notifyDataSetChanged();
                        Log.i("getComments","onFailure "+e.getMessage());
                        failedToDownload(e);

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        testAdapter.currentLoadingTimestampLabel="";
                        testAdapter.is_loading=false;
                        if(testAdapter.started_loading_older_coments.containsKey(timestamp_comment.getTimestamp()))
                        {
                            testAdapter.started_loading_older_coments.remove(timestamp_comment.getTimestamp());
                        }
                        if(testAdapter.started_loading_next_coments.containsKey(timestamp_comment.getTimestamp()))
                        {
                            testAdapter.started_loading_next_coments.remove(timestamp_comment.getTimestamp());
                        }

                        Log.i("getComments","onSuccess "+queryDocumentSnapshots.isEmpty());

                        if(queryDocumentSnapshots.isEmpty()==false)
                        {

                            disable_typing_area.setVisibility(View.GONE);
                            typing_area.setVisibility(View.VISIBLE);

                            if(OP==INITIAL_C||OP==PREV_C)
                            {


                                if(timestamp_comment.getComment().trim().isEmpty()==false)
                                {
                                    testAdapter.commentListUnderHeadComment.set(timestamp_pos+1,timestamp_comment);
                                }

                                for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++)
                                {

                                    DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(i);
                                    Comment comment=new Comment(documentSnapshot);

                                    if(i==queryDocumentSnapshots.size()-1&timestamp_pos<testAdapter.commentListUnderHeadComment.size())
                                    {
                                        if(timestamp_comment.getComment().trim().isEmpty()==false)
                                        {

                                            testAdapter.commentListUnderHeadComment.set(timestamp_pos,comment);

                                        }
                                        else
                                        {
                                            testAdapter.commentListUnderHeadComment.set(timestamp_pos,comment);
                                        }

                                    }
                                    else
                                    {
                                        if(timestamp_pos+1<testAdapter.commentListUnderHeadComment.size())
                                        {
                                            testAdapter.commentListUnderHeadComment.add(timestamp_pos+1,comment);
                                        }
                                        else
                                        {
                                            testAdapter.commentListUnderHeadComment.add(comment);
                                        }

                                    }

                                    if(i==1||i==4||i==8)
                                    {
                                        Comment comment1=new Comment();
                                        comment1.setComment_type(Comment.NEWS_AD);

                                        if(i==8)
                                        {
                                            comment1.setItAd(true);
                                        }

                                        if(timestamp_pos+1<testAdapter.commentListUnderHeadComment.size())
                                        {
                                            testAdapter.news_positions.put(timestamp_pos+1,"");
                                            testAdapter.commentListUnderHeadComment.add(timestamp_pos+1,comment1);
                                        }
                                        else
                                        {
                                            testAdapter.news_positions.put(testAdapter.commentListUnderHeadComment.size(),"");
                                            testAdapter.commentListUnderHeadComment.add(comment1);
                                        }

                                    }

                                    if(testAdapter.timestamps_comments.containsKey(comment.getTimestamp()))
                                    {

                                        ArrayList<Comment> list=testAdapter.timestamps_comments.get(comment.getTimestamp());
                                        if(list.size()>0)
                                        {
                                            if(OP==INITIAL_C||OP==NEXT_C)
                                            {
                                                list.add(comment);
                                            }
                                            else
                                            {
                                                list.add(0,comment);
                                            }
                                        }
                                        else
                                        {
                                            list.add(comment);
                                        }

                                        testAdapter.timestamps_comments.put(comment.getTimestamp(),list);

                                    }
                                    else
                                    {

                                        ArrayList<Comment> list=new ArrayList<Comment>();
                                        if(list.size()>0)
                                        {
                                            if(OP==INITIAL_C||OP==NEXT_C)
                                            {
                                                list.add(comment);
                                            }
                                            else
                                            {
                                                list.add(0,comment);
                                            }
                                        }
                                        else
                                        {
                                            list.add(comment);
                                        }
                                        testAdapter.timestamps_comments.put(comment.getTimestamp(),list);
                                    }

                                }

                                if(queryDocumentSnapshots.size()<10)
                                {
                                    testAdapter.no_more_comments_previous.put(timestamp_comment.getTimestamp(),true);
                                    testAdapter.notifyItemChanged(timestamp_comment.getAdapter_position());
                                }

                                if(initial)
                                {
                                    initial=false;
                                    testAdapter.notifyDataSetChanged();
                                    final int yu=testAdapter.getItemCount()-1;
                                    final int offset=testAdapter.getOffset();
                                    Comment mkl1=testAdapter.commentListUnderHeadComment.get(yu-offset);
                                    mkl1.setSent(false);
                                    testAdapter.commentListUnderHeadComment.set(yu-offset,mkl1);
                                    testAdapter.notifyItemChanged(yu);
                                    last_reading_pos=yu;
                                    comment_list.scrollToPosition(yu);
                                    comment_list.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.i("addCommentsX","1 yu="+yu);
                                            comment_list.smoothScrollToPosition(yu);
                                            Log.i("addCommentsX","2 yu="+yu);
                                            if(yu<testAdapter.getItemCount())
                                            {
                                                Comment mkl1=testAdapter.commentListUnderHeadComment.get(yu-offset);
                                                mkl1.setSent(true);
                                                testAdapter.commentListUnderHeadComment.set(yu-offset,mkl1);
                                                testAdapter.notifyItemChanged(yu);
                                            }
                                            busy=false;

                                        }
                                    },200);
                                }
                                else
                                {
                                    testAdapter.notifyItemChanged(timestamp_pos);
                                    if(queryDocumentSnapshots.size()>1)
                                    {

                                        int hj=timestamp_pos+(queryDocumentSnapshots.size()-1);
                                        int num=queryDocumentSnapshots.size()-1;
                                        if(testAdapter.timestamps_comments.containsKey(timestamp_comment.getTimestamp()))
                                        {
                                            //if(i==1||i==4||i==8)
                                            //news/ads 3 per every 10 comments for a timestamp
                                            int jk=0;
                                            if(queryDocumentSnapshots.size()>=2)
                                            {
                                                jk++;
                                            }
                                            if(queryDocumentSnapshots.size()>=5)
                                            {
                                                jk++;
                                            }
                                            if(queryDocumentSnapshots.size()>=8)
                                            {
                                                jk++;
                                            }

                                            num=+jk;
                                            hj=timestamp_pos+((queryDocumentSnapshots.size()-1)+jk);

                                        }
                                        testAdapter.notifyItemRangeInserted(timestamp_pos+1
                                                ,num);
                                        int rem=testAdapter.timestamps_comments.get(timestamp_comment.getTimestamp()).size()/10;
                                        Log.i("mxvsgdha","hj="+hj+" tp1="+timestamp_comment.getAdapter_position()
                                                +" tp2="+timestamp_pos
                                                +" tstmp.size="+testAdapter.timestamps_comments.get(timestamp_comment.getTimestamp()).size()
                                                +" qsize="+queryDocumentSnapshots.size()+" rem="+rem);
                                        if(hj<testAdapter.commentListUnderHeadComment.size())
                                        {
                                            Comment cmtx=testAdapter.commentListUnderHeadComment.get(hj);
                                            Log.i("mxvsgdha","hj="+hj+" isadnews="+(cmtx.getComment_type()==Comment.NEWS_AD)+" "+cmtx.getComment()+" "+cmtx.getTimeStr());

                                            comment_list.scrollToPosition(hj);
                                            last_reading_pos=hj;


                                        }

                                    }
                                }

                                skipTimestampSH();

                            }
                            else if(OP==NEXT_C)
                            {

                            }

                            if(testAdapter.getActualNumberofComments()>headcomment
                                    .getNum_comments())
                            {
                                num_people_read.setText(NumUtils.getAbbreviatedNum(testAdapter.getActualNumberofComments())+" comments");
                            }




                        }
                        else
                        {
                            if(OP==INITIAL_C||OP==PREV_C)
                            {
                                testAdapter.no_more_comments_previous.put(timestamp_comment.getTimestamp(),true);
                                testAdapter.notifyItemChanged(timestamp_pos);

                            }
                            if(OP==NEXT_C)
                            {
                                testAdapter.no_more_comments_next.put(timestamp_comment.getTimestamp(),true);
                                testAdapter.notifyItemChanged(timestamp_pos);

                            }
                        }

                    }
                });

    }

    CountDownTimer countDownTimerx;
    boolean smkl=false;
    HashMap<String,Boolean> shownship_timestamps=new HashMap<String,Boolean>();
    private void skipTimestampSH()
    {

        if(comment_list.getLayoutManager()!=null) {

            final int first = findVisiblePosition(comment_list);
            final int last = findLastVisiblePosition(comment_list);

            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {

                            Comment timestamp_commentx = null;
                            if(first>-1&first<testAdapter.commentListUnderHeadComment.size())
                            {
                                Comment cmy=testAdapter.commentListUnderHeadComment.get(first);
                                if(cmy.getComment_type()!=Comment.NEWS_AD)
                                {
                                    timestamp_commentx=cmy;
                                }
                            }
                            if(last>-1&last<testAdapter.commentListUnderHeadComment.size()&timestamp_commentx==null)
                            {
                                Comment cmy=testAdapter.commentListUnderHeadComment.get(last);
                                if(cmy.getComment_type()!=Comment.NEWS_AD)
                                {
                                    timestamp_commentx=cmy;
                                }
                            }

                            final Comment timestamp_comment=timestamp_commentx;
                            if(timestamp_comment!=null)
                            {
                                Log.i("skipTimestampSH","first="+first+" "+timestamp_comment.getAdapter_position()+" "+timestamp_comment.getTimestamp()
                                        +" "+shownship_timestamps.containsKey(timestamp_comment.getTimestamp().trim()));

                                int scrollto=0;
                                for(int uj=timestamp_comment.getAdapter_position();uj>=0;uj--)
                                {

                                    Comment comment=testAdapter.commentListUnderHeadComment.get(uj);
                                    if(comment.getTimestamp().equals(timestamp_comment.getTimestamp())==false
                                            &comment.getComment_type()!=Comment.NEWS_AD)
                                    {
                                        scrollto=uj;
                                        break;
                                    }

                                }

                                Log.i("skipTimestampSH","scrollto="+scrollto);
                                if(scrollto>0)
                                {

                                    TestGroup2.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            if(timestamp_comment!=null)
                                            {
                                                if(shownship_timestamps.containsKey(timestamp_comment.getTimestamp().trim())==false
                                                        &timestamp_comment.getAdapter_position()-1>=0)
                                                {


                                                    smkl=true;
                                                    shownship_timestamps.put(timestamp_comment.getTimestamp().trim(),true);
                                                    timestamp_skipper.setText("Skip to Previous day"+" in 4");
                                                    timestamp_skipper.setVisibility(View.VISIBLE);
                                                    countDownTimerx=new CountDownTimer(4000,1000) {
                                                        @Override
                                                        public void onTick(long l) {
                                                            long sec=l/1000;
                                                            timestamp_skipper.setText("Skip to Previous day"+" in "+sec);
                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            timestamp_skipper.setVisibility(View.GONE);
                                                            rstr(timestamp_comment);
                                                        }
                                                    };
                                                    countDownTimerx.start();
                                                    timestamp_skipper.setOnCloseIconClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {

                                                            timestamp_skipper.setVisibility(View.GONE);
                                                            rstr(timestamp_comment);

                                                        }
                                                    });
                                                    timestamp_skipper.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {


                                                            timestamp_skipper.setVisibility(View.GONE);
                                                            if(countDownTimerx!=null)
                                                            {
                                                                countDownTimerx.cancel();
                                                            }

                                                            int yu=timestamp_comment.getAdapter_position();

                                                            UserInfoDatabase.databaseWriteExecutor
                                                                    .execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            int scrollto=0;
                                                                            for(int uj=yu;uj>=0;uj--)
                                                                            {

                                                                                Comment comment=testAdapter.commentListUnderHeadComment.get(uj);
                                                                                if(comment.getTimestamp().equals(timestamp_comment.getTimestamp())==false
                                                                                        &comment.getComment_type()!=Comment.NEWS_AD)
                                                                                {
                                                                                    scrollto=uj;
                                                                                    break;
                                                                                }

                                                                            }

                                                                            final int ku=scrollto;

                                                                            TestGroup2.this.runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {

                                                                                    Log.i("rushjdad","ku="+ku);
                                                                                    comment_list.scrollToPosition(ku);
                                                                                    rstr(timestamp_comment);

                                                                                }
                                                                            });

                                                                        }
                                                                    });

                                                        }
                                                    });

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

    private void rstr(Comment current_timestamp_comment)
    {

        new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if(shownship_timestamps.containsKey(current_timestamp_comment.getTimestamp()))
                {
                    shownship_timestamps.remove(current_timestamp_comment.getTimestamp());
                }
            }
        }.start();

    }

    public int findVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] firstVisibleItems = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            int firstVisibleItem = firstVisibleItems[0];
            for (int tmp : firstVisibleItems) {
                if (firstVisibleItem > tmp) {
                    firstVisibleItem = tmp;
                }
            }
            return firstVisibleItem;
        }
        throw new IllegalArgumentException("only support LinearLayoutManager and StaggeredGridLayoutManager");
    }
    public int findLastVisiblePosition(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] firstVisibleItems = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            int firstVisibleItem = firstVisibleItems[0];
            for (int tmp : firstVisibleItems) {
                if (firstVisibleItem > tmp) {
                    firstVisibleItem = tmp;
                }
            }
            return firstVisibleItem;
        }
        throw new IllegalArgumentException("only support LinearLayoutManager and StaggeredGridLayoutManager");
    }

    AdLoader adLoader;
    private void getAds()
    {

        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        // Show the ad.
                        if(testAdapter==null)
                        {
                            initiaLizeCommentList();
                        }

                        testAdapter.addNativeAd(nativeAd);

                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build();

        adLoader.loadAds(new AdRequest.Builder().build(), 1);


    }
    private int getLastVisibleItem(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            //int lastVisibleItemPosition = ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPositions();
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastVisibleItemPositions(null);
            // get maximum element within the list
            int lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            return lastVisibleItemPosition;
        }
        return -1;
    }
    //get last item
    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    //the recyclerview resizes
    boolean isKeyboardShowing=false;
    int last_reading_pos=0;
    private void checkCommentListKeyBoard()
    {
        try {

            Log.i("chckCmmntLstKyBard",this.getPackageName());
            if(comment_list!=null)
            {

                //RecyclerView comment_list=currentTopicForConvo1.getCommentWorker().getComment_list();
                Log.i("chckCmmntLstKyBard","comment_list!=null "+(comment_list!=null));
                if(comment_list!=null)
                {
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


                                        }
                                    }
                                    else {
                                        // keyboard is closed
                                        if (isKeyboardShowing) {
                                            isKeyboardShowing = false;
                                            testAdapter.notifyDataSetChanged();
                                            Log.i("chckCmmntLstKyBard","L");
                                            if(last_reading_pos>0)
                                            {

                                                Log.i("chckCmmntLstKyBard","L last_reading_pos="+last_reading_pos);
                                                final int jk=last_reading_pos;
                                                comment_list.scrollToPosition(jk);
                                                comment_list.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        comment_list.smoothScrollToPosition(jk);
                                                        Log.i("keyposa","last_reading_pos="+jk);

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
                                last_reading_pos=getLastVisibleItem(comment_list);
                                if(current_timestamp_comment!=null)
                                {
                                    skipTimestampSH();
                                }
                            }
                            else
                            {

                                if(timestamp_skipper!=null)
                                {
                                    timestamp_skipper.setVisibility(View.GONE);
                                    if(countDownTimerx!=null)
                                    {
                                        countDownTimerx.cancel();
                                    }
                                }

                            }

                        }
                    });
                }
            }

        }
        catch(Exception exception)
        {
            Log.i("chckCmmntLstKyBard","e="+exception.getMessage());
        }
    }

    //The Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_group2);

        ButterKnife.bind(this);
        //setUpViewPager();

        marker_filter_area.setVisibility(View.VISIBLE);
        LoadConvo();
        getAds();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        bubbleEmitterView.setColorResources(R.color.glass2
                ,R.color.glass2,R.color.glass);
        countDownTimer=new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long l) {
                bubbleEmitterView.emitBubble(100);
                bubbleEmitterView.emitBubble(100);
                bubbleEmitterView.emitBubble(100);
            }

            @Override
            public void onFinish() {

                countDownTimer.start();
            }
        };

        countDownTimer.start();



    }

    @BindView(R.id.record_view)
    FrameLayout record_view;
    private GoogleSignInClient mGoogleSignInClient;
    ActivityResultLauncher<Intent> fileSelector = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i("fileSelector","started "+result.getResultCode()+" "+Activity.RESULT_OK);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        returnUri = data.getData();
                        String mimeType = getContentResolver().getType(returnUri);
                        Log.i("fileSelector","mimeType="+mimeType);

                    }
                    else
                    {

                    }
                }
            });
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
        {
            String conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);
            Log.i("onPostCreate","conversation_id="+conversation_id);
        }
        else
        {
            Log.i("onPostCreate","no convo_id");
        }

        //getUIDs();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==1)
                {
                    stopRecording();
                    StopPlayLocalAudio();
                    record_view.setVisibility(View.GONE);
                    voice_notes_area.setVisibility(View.VISIBLE);
                    comment_list.setVisibility(View.GONE);
                    typing_area.setVisibility(View.VISIBLE);
                }
                else
                {
                    voice_notes_area.setVisibility(View.GONE);
                    comment_list.setVisibility(View.VISIBLE);
                    typing_area.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initTypingAreaViews();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        //fileSelector.launch(intent);


    }


    //the traditional intent sign in
    ActivityResultLauncher<Intent> authLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.i("onActivityResult", "firebaseAuthWithGoogle:" + account.getId());
                            if(account!=null)
                            {

                                boolean nm=account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE));
                                Log.i("onPostCreate","nm="+nm);
                                if(recording_before_signin)
                                {
                                    RecordVN();
                                }
                                else
                                {
                                    deniedGoogleDriveScope();
                                }

                            }


                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.i("onActivityResult", "Google sign in failed", e);
                        }

                    }
                    else
                    {
                        deniedGoogleDriveScope();
                    }
                }
            });
    private void GoogleSignIn()
    {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.google_client_id))
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        authLauncher.launch(signInIntent);

    }

    boolean recording_before_signin=false;
    @OnClick(R.id.vn_mic)
    public void RecordVN()
    {

        typing_area.setVisibility(View.GONE);
        playing_time.setVisibility(View.INVISIBLE);
        timer.setVisibility(View.VISIBLE);
        recording_before_signin=true;
        recording=false;
        if(mRecorder!=null)
        {
            stopRecording();
        }
        StopPlayLocalAudio();
        pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_stop_circle_48));

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null)
        {

            boolean nm=account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE));
            Log.i("RecordVN","nm="+nm);

            if(nm==false)
            {
                deniedGoogleDriveScope();
            }
            else
            {
                RecordVNAudio();
            }


        }
        else {
            Log.i("RecordVN","account!=null "+(account!=null));

            // [START config_signin]
            // Configure Google Sign In
            GoogleSignIn();


        }

    }

    private MediaRecorder mRecorder;
    String fileName="";
    boolean recording=false;
    File tempFile;

    public void RecordVNAudio()
    {

        Log.i("RecordVNAudio","started");

        boolean cm=false;

        // Start the animation
        cm=true;

        if(cm)
        {

            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                        @Override public void onPermissionsChecked(MultiplePermissionsReport report)
                        {

                            /* ... */
                            if(report.areAllPermissionsGranted())
                            {

                                fileName= UUID.randomUUID().toString();
                                Log.i("RecordVNAudio",fileName+" areAllPermissionsGranted");
                                LocalDateTime localDateTime=new LocalDateTime();
                                fileName= localDateTime.millisOfDay().getAsText()+"";
                                String jkl=bookieActivity.getActivity_title();
                                jkl=jkl.replace("#","");
                                jkl=jkl.replace("%","");
                                jkl=jkl.replace("&","");
                                jkl=jkl.replace("{","");
                                jkl=jkl.replace("}","");
                                jkl=jkl.replace("\\","");
                                jkl=jkl.replace(" ","");
                                jkl=jkl.replace("$","");
                                jkl=jkl.replace(">","");
                                jkl=jkl.replace("*","");
                                jkl=jkl.replace("?","");
                                jkl=jkl.replace("/","");
                                jkl=jkl.replace("!","");
                                jkl=jkl.replace("'","");
                                jkl=jkl.replace('"'+"","");
                                jkl=jkl.replace(":","");
                                jkl=jkl.replace("@","");
                                jkl=jkl.replace("+","");
                                jkl=jkl.replace("|","");
                                jkl=jkl.replace("=","");
                                jkl=jkl.replace("’","");
                                fileName=jkl;
                                //getExternalStorageDirectory
                                fileName= jkl;

                                File dir=TestGroup2.this.getFilesDir();
                                fileName=dir.getAbsolutePath()+"/"+jkl+".3gp";
                                tempFile=new File(fileName);
                                if(mRecorder==null)
                                {
                                    // below method is used to initialize
                                    // the media recorder class
                                    mRecorder = new MediaRecorder();
                                    // below method is used to set the audio
                                    // source which we are using a mic.
                                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                    mRecorder.setOutputFile(tempFile.getAbsolutePath());
                                }

                                Log.i("RecordVNAudio","recording="+recording);
                                if(recording)
                                {

                                    if(countDownTimerl!=null)
                                    {
                                        countDownTimerl.cancel();
                                        timer.setText("");

                                    }
                                    recording=false;
                                    if(mRecorder!=null)
                                    {
                                        // below method will stop
                                        //the audio recording.
                                        mRecorder.stop();
                                        // below method will release
                                        // the media recorder class.
                                        mRecorder.release();
                                        mRecorder = null;
                                    }
                                    if(new File(tempFile.getAbsolutePath()).exists())
                                    {
                                        Log.i("RecordVNAudio","fileName.size="+new File(tempFile.getAbsolutePath()).length());
                                        PlayLocalAudio();
                                    }
                                    else
                                    {
                                        Log.i("RecordVNAudio","fileName does not exists");
                                    }

                                }
                                else
                                {


                                    record_view.setVisibility(View.VISIBLE);
                                    done.setVisibility(View.INVISIBLE);
                                    delete.setVisibility(View.INVISIBLE);
                                    slider.setVisibility(View.INVISIBLE);
                                    recording=true;
                                    startCountdownTimer();
                                    Log.i("RecordVNAudio",fileName+" areAllPermissionsGranted");
                                    if(new File(fileName).exists())
                                    {
                                        new File(fileName).delete();
                                    }

                                    try
                                    {
                                        mRecorder.prepare();
                                        mRecorder.start();
                                    }
                                    catch(Exception exception)
                                    {
                                        Log.i("RecordVNAudio", "prepare() failed "+exception.getMessage());
                                        // below method will stop
                                        //the audio recording.
                                        mRecorder = null;
                                    }

                                }

                            }
                            else
                            {

                                Log.i("RecordVNAudio","not areAllPermissionsGranted");




                            }

                        }
                        @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                                 PermissionToken token) {
                            /* ... */


                        }

                    }).check();

        }




    }


    private void stopRecording()
    {

        if(recording)
        {
            done.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            timer.setVisibility(View.VISIBLE);

            if(countDownTimerl!=null)
            {
                countDownTimerl.cancel();
                timer.setText("60");

            }
            recording=false;
            if(mRecorder!=null)
            {
                // below method will stop
                //the audio recording.
                mRecorder.stop();
                // below method will release
                // the media recorder class.
                mRecorder.release();
                mRecorder = null;
            }
            if(new File(tempFile.getAbsolutePath()).exists())
            {
                Log.i("RecordVNAudio","fileName.size="+new File(tempFile.getAbsolutePath()).length());
                PlayLocalAudio();
            }
            else
            {
                Log.i("RecordVNAudio","fileName does not exists");
            }

        }
        if(countDownTimerl!=null)
        {
            countDownTimerl.cancel();
            countDownTimerl=null;
        }
        if(timertask!=null)
        {
            timertask.cancel();
        }
        StopPlayLocalAudio();
    }

    @BindView(R.id.pause_stop)
    FloatingActionButton pause_stop;
    @BindView(R.id.done)
    FloatingActionButton done;
    @BindView(R.id.delete)
    FloatingActionButton delete;

    @OnClick(R.id.pause_stop)
    public void pauseStopRecPlay()
    {

        if(recording)
        {

            pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            if(countDownTimerl!=null)
            {
                countDownTimerl.cancel();
                timer.setText("");

            }
            recording=false;
            if(mRecorder!=null)
            {
                // below method will stop
                //the audio recording.
                mRecorder.stop();
                // below method will release
                // the media recorder class.
                mRecorder.release();
                mRecorder = null;
            }
            if(new File(tempFile.getAbsolutePath()).exists())
            {
                Log.i("RecordVNAudio","fileName.size="+new File(tempFile.getAbsolutePath()).length());
                PlayLocalAudio();
            }
            else
            {
                Log.i("RecordVNAudio","fileName does not exists");
            }

        }
        else if(mPlayer!=null)
        {

            if(mPlayer.isPlaying())
            {
                PausePlayLocalAudio();
                pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_circle_outline_24));
            }
            else{
                ResumePlayLocalAudio();
                pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            }

        }
        else if(tempFile!=null)
        {
            pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
            PlayLocalAudio();
        }


    }

    @OnClick(R.id.delete)
    void Delete()
    {

        slider.setProgress(0);
        typing_area.setVisibility(View.VISIBLE);
        record_view.setVisibility(View.INVISIBLE);
        stopRecording();
        recording=false;

    }

    CountDownTimer countDownTimerl;
    @BindView(R.id.timerx)
    TextView timer;
    private void startCountdownTimer()
    {

        Log.i("RecordVNAudio","startCountdownTimer "+(countDownTimerl!=null));
        if(countDownTimerl!=null)
        {
            countDownTimerl.cancel();
            countDownTimerl=null;
        }
        timer.setVisibility(View.VISIBLE);
        countDownTimerl=new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {

                if(timertask!=null)
                {
                    timertask.cancel();
                    timertask=null;
                }
                final long seconds_left=l/1000;
                timer.setText(seconds_left+"");
                timer.setVisibility(View.VISIBLE);
                Log.i("djfsfd","startCountdownTimer sec="+seconds_left);


            }

            @Override
            public void onFinish() {
                RecordVNAudio();
            }
        }.start();

    }

    MediaPlayer mPlayer;
    boolean wasPlaying=false;
    @BindView(R.id.slider)
    SeekBar slider;
    @BindView(R.id.playing_time)
    TextView playing_time;
    private Timer timertask;
    private void PlayLocalAudio()
    {

        if(tempFile!=null)
        {

            if(tempFile.exists())
            {

                timer.setVisibility(View.INVISIBLE);
                if(countDownTimerl!=null)
                {
                    countDownTimerl.cancel();
                    countDownTimerl=null;
                }
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(tempFile.getAbsolutePath());

                    mPlayer.prepare();
                    if(slider.getProgress()>0&slider.getProgress()<mPlayer.getDuration())
                    {
                        mPlayer.seekTo(slider.getProgress());
                    }
                    mPlayer.start();
                    wasPlaying=true;
                    pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
                    done.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                    slider.setVisibility(View.VISIBLE);
                    slider.setMax(mPlayer.getDuration());

                    slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if(mPlayer!=null)
                            {

                                long j=mPlayer.getCurrentPosition()/1000;
                                playing_time.setText(j+"");

                            }
                            else
                            {
                                int j=i/1000;
                                playing_time.setText(j+"");
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            PausePlayLocalAudio();

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            if(mPlayer!=null)
                            {
                                paused_position=seekBar.getProgress();
                                mPlayer.seekTo(seekBar.getProgress());
                                ResumePlayLocalAudio();
                            }
                        }
                    });

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            wasPlaying=false;
                            if(mPlayer!=null)
                            {
                                long j=mPlayer.getDuration()/1000;
                                timer.setText(j+"");
                                Log.i("djfsfd","onCompletion");
                                slider.setProgress(mPlayer.getCurrentPosition());
                                mPlayer.release();
                                mPlayer=null;
                                pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_play_arrow_24));
                                timertask.cancel();

                            }
                        }
                    });
                    playing_time.setVisibility(View.VISIBLE);
                    timertask=new Timer();
                    timertask.scheduleAtFixedRate(new TimerTask() {

                        @Override

                        public void run() {

                            try {

                                if(recording==false&mPlayer!=null)
                                {
                                    if(mPlayer.isPlaying())
                                    {

                                        slider.setProgress(mPlayer.getCurrentPosition());
                                        long j=mPlayer.getCurrentPosition()/1000;
                                        //playing_time.setText(j+"");
                                        Log.i("djfsfd","startCountdownTimer p="+j+" ");

                                    }
                                    else
                                    {

                                    }
                                }
                                else
                                {
                                    this.cancel();
                                }


                            } catch (Exception e) {

                            }

                        }

                    }, 0, 100);
                    slider.setVisibility(View.VISIBLE);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @BindView(R.id.voice_notes_area)
    RelativeLayout voice_notes_area;
    //done recording
    @OnClick(R.id.done)
    void DoneRec()
    {

        stopRecording();
        StopPlayLocalAudio();
        record_view.setVisibility(View.GONE);
        voice_notes_area.setVisibility(View.VISIBLE);
        tabs.selectTab(tabs.getTabAt(1));
        comment_list.setVisibility(View.GONE);

    }

    int paused_position=0;
    private void PausePlayLocalAudio()
    {

        if(mPlayer!=null)
        {
            if(mPlayer.isPlaying())
            {
                mPlayer.pause();
                paused_position=mPlayer.getCurrentPosition();
            }
        }

    }
    private void ResumePlayLocalAudio()
    {

        if(mPlayer!=null)
        {
            mPlayer.seekTo(paused_position);
            mPlayer.start();
        }

    }
    private void StopPlayLocalAudio()
    {

        if(mPlayer!=null)
        {
            if(mPlayer.isPlaying())
            {
                mPlayer.stop();
            }
            mPlayer.release();
            mPlayer=null;
            slider.setVisibility(View.INVISIBLE);
            timer.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            done.setVisibility(View.INVISIBLE);
            pause_stop.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_stop_circle_48));
            record_view.setVisibility(View.INVISIBLE);
        }


    }

    private void uploadAudio()
    {

        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        int numOfIds = 4;
                        GeneratedIds allIds = null;
                        try {
                            GoogleSignInAccount account =GoogleSignIn.getLastSignedInAccount(TestGroup2.this);
                            GoogleAccountCredential credential=GoogleAccountCredential.usingOAuth2(TestGroup2.this,
                                    Collections.singleton(DriveScopes.DRIVE_FILE));
                            credential.setSelectedAccount(account.getAccount());
                            Drive drive=new Drive.Builder(AndroidHttp
                                    .newCompatibleTransport(),new GsonFactory(),credential)
                                    .setApplicationName(getResources().getString(R.string.app_name))
                                    .build();

                            allIds = drive.files().generateIds()
                                    .setSpace("drive").setCount(numOfIds).execute();

                            List<String> generatedFileIds = allIds.getIds();

                            for(String jn:generatedFileIds)
                            {
                                Log.i("RecordVNAudio","jn="+jn);
                            }


                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                });

    }

    private void deniedGoogleDriveScope()
    {

        new MaterialDialog.Builder(this)
                .title("We need access to google drive")
                .content("We store you voice notes on your google drive " +
                        "in a Bookie/Audio/Convo Title" +
                        " in order to back up, organize and track files used on the Bookie platform for later use " +
                        "and to delete easily file related to certain organizations/groups or topics")
                .positiveText("Sign In")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        GoogleSignIn();

                    }
                })
                .negativeText("Never mind")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        recording_before_signin=false;

                    }
                })
                .cancelable(false)
                .show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
        {
            String conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);
            Log.i("onResume","conversation_id="+conversation_id);
        }
        else
        {
            Log.i("onResume","no convo_id");
        }



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        Log.i("onPostResume","last_reading_pos="+last_reading_pos);
        if(last_reading_pos>0)
        {


            final int jk=last_reading_pos;
            comment_list.scrollToPosition(jk);
            comment_list.postDelayed(new Runnable() {
                @Override
                public void run() {

                    comment_list.smoothScrollToPosition(jk);
                    Log.i("keyposa","last_reading_pos="+jk);

                }
            },1000);
        }
        else
        {

        }

        if(wasPlaying)
        {
            ResumePlayLocalAudio();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        PausePlayLocalAudio();
        if(youTubePlayer!=null)
        {
            youTubePlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopPlayLocalAudio();
        if(youTubePlayerView!=null)
        {
            youTubePlayerView.release();
        }
    }

    ArrayList<NewsFactsMedia> newsFactsMediaArrayList=new ArrayList<NewsFactsMedia>();
    private void getNewsSuggestions()
    {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.google.com/search?q="+bookieActivity.getActivity_title()+"&tbm=nws";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.i("getNewsSuggestions","onResponse");

                        UserInfoDatabase.databaseWriteExecutor
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        Document doc = Jsoup.parse(response);
                                        Elements body=doc.getElementsByTag("body");

                                        if(body!=null)
                                        {

                                            if(body.isEmpty()==false)
                                            {

                                                Element body_1=body.get(0);
                                                Elements alinks=body_1.select("a:matches(\\b(?:[a-z]){3,4}\\b)");

                                                Log.i("getNewsSuggestions","alinks.isEmpty()="
                                                        +(alinks.isEmpty()));

                                                for(int i=0;i<alinks.size();i++)
                                                {

                                                    Element a=alinks.get(i);
                                                    Elements spans=a.select("span:matches(\\b(?:[a-z]{0,10})\\b||\\b\\d{0,10})");
                                                    Elements gms=a.getElementsByTag("span");
                                                    if(a.hasParent())
                                                    {
                                                        gms=a.parent().getElementsByTag("span");
                                                    }

                                                    Elements djks=a.select("div[role=heading]");

                                                    String sourcelink=a.attr("href");
                                                    String heading="";
                                                    String source="";
                                                    String timestamp="";
                                                    if(djks.isEmpty()==false)
                                                    {
                                                        Log.i("getNewsSuggestions",djks.get(0).text());
                                                        heading=djks.get(0).text();

                                                        if(spans.isEmpty()==false)
                                                        {

                                                            for(Element span:spans)
                                                            {
                                                                String p="^\\d\\s(minutes|weeks|moments|years|days|hours|months|a minute|a week|a moment|a year|a day|a hour|a month|minute|week|moment|year|day|hour|month|minute)\\s(ago)$";
                                                                String p2="\\d\\s[A-Z]{1}[a-z]{2,8}\\s\\d{4}";
                                                                Pattern pattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                                                                Pattern pattern2 = Pattern.compile(p2, Pattern.CASE_INSENSITIVE);
                                                                Matcher matcher = pattern.matcher(span.text());
                                                                Matcher matcher2 = pattern2.matcher(span.text());
                                                                boolean matchFound = matcher.find();
                                                                boolean matchFound2 = matcher2.find();
                                                                if(matchFound||matchFound2)
                                                                {
                                                                    timestamp=span.text();
                                                                }
                                                                else
                                                                {
                                                                    source=span.text();
                                                                }

                                                            }

                                                        }
                                                    }

                                                    //decide whether to scrap the link for the complete title
                                                    if(heading.indexOf("...")>-1||source.isEmpty()==false)
                                                    {
                                                        Log.i("getNewsSuggestions","scraping "+i
                                                                +" "+heading+" "+source);
                                                        String hj=heading;
                                                        if(hj.indexOf("...")>-1)
                                                        {
                                                            hj=hj.substring(0,hj.indexOf("..."));
                                                        }

                                                    }
                                                    else
                                                    {
                                                        Log.i("getNewsSuggestions",i
                                                                +" "+heading+" "+source+" "+timestamp);
                                                    }

                                                    for(Element span:gms)
                                                    {

                                                        Pattern p = Pattern.compile("\\d\\s(?:[a-z]{3,10})\\s(ago)||(a)\\s(?:[a-z]{3,10})\\s(ago)||\\d\\s(?:[a-z]{3,10})\\s\\d{4}");
                                                        Matcher m = p.matcher(span.text());

                                                        if(span.text().trim().isEmpty()==false&m.matches()==false)
                                                        {

                                                            Log.i("jusgald_"+i,"i="+i+" text="+span.text());
                                                            source=span.text();


                                                        }
                                                    }

                                                    if(heading.isEmpty()==false&sourcelink.isEmpty()==false)
                                                    {


                                                        NewsFactsMedia newsFactsMedia=new NewsFactsMedia();
                                                        newsFactsMedia.setTitle(heading);
                                                        newsFactsMedia.setSource(source);
                                                        newsFactsMedia.setLink(sourcelink);
                                                        newsFactsMedia.setType(NewsFactsMedia.NEWS);
                                                        newsFactsMediaArrayList.add(newsFactsMedia);
                                                        Log.i("jsnajs",newsFactsMedia.getTitle()+" source="+newsFactsMedia.getSource()+" i="+i);



                                                    }



                                                }

                                                TestGroup2.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if(testAdapter==null)
                                                        {
                                                            initiaLizeCommentList();
                                                        }
                                                        final boolean ml=testAdapter.newsFactsMediaArrayList.size()>0;
                                                        Log.i("getNewsSuggestions","ml="+ml+" "+testAdapter.newsFactsMediaArrayList.size());
                                                        testAdapter.addNewsAdsList(newsFactsMediaArrayList);
                                                        if(ml==false)
                                                        {
                                                            testAdapter.notifyDataSetChanged();
                                                        }

                                                    }
                                                });


                                            }

                                        }


                                    }
                                });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("getNewsSuggestions","error="+error.getMessage());

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void startShowingNewsAlerts()
    {

        if(newsFactsMediaArrayList.size()>0)
        {

            NewsFactsMedia newsFactsMedia=newsFactsMediaArrayList.get(newsFactsMediaArrayList.size()-1);

            int offset=testAdapter.getOffset();

            if(offset==2)
            {
                testAdapter.showNewsAlerts(newsFactsMedia);
            }

        }

    }

    public String getDeviceId(Context context){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            String androidId =
                    Settings.Secure.getString((ContentResolver)context.getContentResolver(),Settings.Secure.ANDROID_ID);
            messageDigest.update(androidId.getBytes());
            byte[] arrby = messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            int n = arrby.length;
            for(int i=0; i<n; ++i){
                String oseamiya = Integer.toHexString((int)(255 & arrby[i]));
                while(oseamiya.length() < 2){
                    oseamiya = "0" + oseamiya;
                }
                sb.append(oseamiya);
            }
            String result = sb.toString();
            return result;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    void getUIDs()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(TestGroup2.this);
                    String myId = adInfo != null ? adInfo.getId() : null;

                    Log.i("UIDMY", myId);
                } catch (Exception e) {
                    Log.i("UIDMY", e.getMessage());
                    Toast toast = Toast.makeText(TestGroup2.this, "error occurred ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    @Override
    public void getCommentsForTimeStampsPrevTo(Comment first_comment_in_timestamp) {

        OP=PREV_C;
        Log.i("ljshdpajdsx",first_comment_in_timestamp.getDateTimeStr());
        current_timestamp_comment=first_comment_in_timestamp;
        getComments(first_comment_in_timestamp,first_comment_in_timestamp.getAdapter_position());

    }

    @Override
    public void getCommentsForTimeStampsNextAfter(Comment last_comment_in_timestamp) {

        OP=NEXT_C;

    }

    @Override
    public void getCommentsForTimeStampsInitial(Comment timestamp_comemnt) {

        OP=INITIAL_C;

    }

    @BindView(R.id.bubbleEmitter)
    BubbleEmitterView bubbleEmitterView;
    @BindView(R.id.timestamp_skipper)
    Chip timestamp_skipper;

    @BindView(R.id.rootView)
    RelativeLayout rootView;
    private void initTypingAreaViews()
    {

        EmojIconActions emojIcon=new EmojIconActions(this,rootView,emojicon_edit_text,emoji_button);
        emojIcon.ShowEmojIcon();


    }
    //creating a text based comment
    @BindView(R.id.emojicon_edit_text)
    EmojiconEditText emojicon_edit_text;
    @BindView(R.id.emoji_button)
    ImageView emoji_button;

    //file being sent
    String fileUrlBeingSent="";
    int current_comment_type=Comment.AGREES;
    @OnClick(R.id.send_button)
    void SendComment()
    {

        String commentText=emojicon_edit_text.getText().toString();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {

            if(commentText.trim().isEmpty()&returnUri==null&fileUrlBeingSent.isEmpty())
            {
                //it is only a text comment
                Toast.makeText(this, "Say something", Toast.LENGTH_SHORT).show();
            }
            else if(commentText.trim().isEmpty()==false&returnUri==null&fileUrlBeingSent.isEmpty())
            {

                //send the text comment
                checkIfGaveUserInfo();

            }
            else
            {

                //send a file from gallery
                if(returnUri!=null)
                {
                    checkIfGaveUserInfo();
                }
                else if(fileUrlBeingSent.trim().isEmpty()==false)
                {
                    checkIfGaveUserInfo();
                }

            }
        }
        else
        {

            new MaterialDialog.Builder(this)
                    .title("You have to sign In")
                    .positiveText("Sign In")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            Intent intent=new Intent(TestGroup2.this, SignInAccount.class);
                            intent.putExtra(OrgFields.CONVERSATION_ID,conversation_id);
                            startActivity(intent);

                        }
                    })
                    .negativeText("Nope")
                    .cancelable(false)
                    .show();

        }

    }

    UserInfo currentUserInfo=null;
    private void checkIfGaveUserInfo()
    {

        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                        final UserInfo userInfo=UserInfoDatabase.getDatabase(TestGroup2.this)
                                .userInfoDao().getUserInfo(firebaseUser.getUid());
                        currentUserInfo=userInfo;
                        if(userInfo!=null)
                        {

                            TestGroup2.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    String commentText=emojicon_edit_text.getText().toString();
                                    if(commentText.trim().isEmpty()&returnUri==null&fileUrlBeingSent.isEmpty())
                                    {
                                        //it is only a text comment
                                        Toast.makeText(TestGroup2.this, "Say something", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {

                                        AskIfNewTopicOrNewTopic();

                                    }


                                }
                            });

                        }


                    }
                });

    }

    boolean is_new_topic=true;
    private void AskIfNewTopicOrNewTopic()
    {


        if(headcomment!=null)
        {

            String content="";
            if(headcomment.getComment().length()>30)
            {
                content="responding to "+headcomment.getCommentator_name().trim()
                        +" <b><i>'"+headcomment.getComment().substring(0,29)+"...'</i></b> or <b>new Topic?</b>";
            }
            else
            {
                content="responding to "+headcomment.getCommentator_name().trim()
                        +" <b><i>'"+headcomment.getComment()+"'</i></b> or <b>new Topic?</b>";
            }

            new MaterialDialog.Builder(this)
                    .title("New Topic or Responding to head comment")
                    .content(Html.fromHtml(content))
                    .cancelable(false)
                    .positiveText("New Topic")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            is_new_topic=true;
                            AskCommentType();

                        }
                    })
                    .negativeText("Responding to "+headcomment.getCommentator_name())
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            is_new_topic=false;
                            AskCommentType();

                        }
                    })
                    .neutralText("Never mind")
                    .show();

        }
        else
        {
            AskCommentType();
        }

    }

    private void AskCommentType()
    {

        if(bookieActivity!=null)
        {

            String content="";
            if(bookieActivity.getActivity_title().length()>70)
            {
                content="how do you respond to "+" <b><i>'"+bookieActivity.getActivity_title().substring(0,29)+"...'</i></b>?";
            }
            else
            {
                content="how do you respond to "+" <b><i>'"+bookieActivity.getActivity_title()+"'</i></b>?";
            }

            new MaterialDialog.Builder(this)
                    .title("how do you respond to convo?")
                    .content(Html.fromHtml(content))
                    .items(new String[]{"Agree","Disagree","Question","Answer"})
                    .itemsColor(getResources().getColor(R.color.red))
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                            if(position==0)
                            {
                                current_comment_type=Comment.AGREES;
                            }
                            if(position==1)
                            {
                                current_comment_type=Comment.DISAGREES;
                            }
                            if(position==2)
                            {
                                current_comment_type=Comment.QUESTION;
                            }
                            if(position==3)
                            {
                                current_comment_type=Comment.ANSWER;
                            }

                            if(returnUri!=null)
                            {

                            }
                            else if(fileUrlBeingSent.trim().isEmpty()==false)
                            {

                            }
                            else
                            {
                                sendTextComment(currentUserInfo);
                            }

                        }
                    })
                    .cancelable(false)
                    .positiveText("Never mind")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {



                        }
                    })
                    .show();

        }

    }

    private void setWhoSentComment(UserInfo userInfo, Map<String, Object> comment_values)
    {

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        comment_values.put(OrgFields.UID,firebaseUser.getUid());
        comment_values.put(OrgFields.USER_FIRSTNAME,userInfo.firstname);
        comment_values.put(OrgFields.USER_LASTNAME,userInfo.lastname);
        comment_values.put(OrgFields.COMMENTATER_NAME,userInfo.firstname+" "+userInfo.lastname);
        if(userInfo.profile_path!=null)
        {
            comment_values.put(OrgFields.USER_PROFILE_PICTURE,userInfo.profile_path);
            comment_values.put(OrgFields.COMMENTER_PROFILE_IMAGE,userInfo.profile_path);
        }

    }
    private void setWhoCreatedConvo(Map<String, Object> comment_values)
    {

        if(bookieActivity!=null)
        {

            if(bookieActivity.getOrgid().trim().isEmpty()==false)
            {
                comment_values.put(OrgFields.ORGID,bookieActivity.getOrgid());
                comment_values.put(OrgFields.IS_ORG_CREATED,true);
            }
            else
            {
                comment_values.put(OrgFields.ORGID,bookieActivity.getAdmin_uid());
                comment_values.put(OrgFields.IS_ORG_CREATED,false);
            }


        }

    }


    Uri returnUri=null;
    private void setCommentDetails(String commentText,Map<String, Object> comment_values)
    {

        //send a file from gallery
        boolean audiofile=false;
        if(tempFile!=null)
        {
            audiofile=tempFile.exists();
        }

        if(audiofile)
        {

            if(current_comment_type==Comment.AGREES)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_AUDIO);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_AUDIO,Comment.AGREES});
            }
            if(current_comment_type==Comment.DISAGREES)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_AUDIO);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_AUDIO,Comment.DISAGREES});
            }
            if(current_comment_type==Comment.QUESTION)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_AUDIO);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_AUDIO,Comment.QUESTION});
            }
            if(current_comment_type==Comment.ANSWER)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_AUDIO);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_AUDIO,Comment.ANSWER});
            }

        }
        else if(returnUri!=null)
        {

            String mimeType = getContentResolver().getType(returnUri);
            boolean isimage=mimeType.trim().indexOf("image/")==0;
            boolean isvideo=mimeType.trim().indexOf("video/")==0;
            if(isimage)
            {

                if(current_comment_type==Comment.AGREES)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_IMAGE_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_IMAGE_ONLY,Comment.AGREES});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_IMAGE_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_IMAGE_TEXT_ONLY,Comment.AGREES});
                    }
                }
                if(current_comment_type==Comment.DISAGREES)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_IMAGE_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_IMAGE_ONLY,Comment.DISAGREES});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_IMAGE_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_IMAGE_TEXT_ONLY,Comment.DISAGREES});
                    }
                }
                if(current_comment_type==Comment.QUESTION)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_IMAGE_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_IMAGE_ONLY,Comment.QUESTION});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_IMAGE_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_IMAGE_TEXT_ONLY,Comment.QUESTION});
                    }
                }
                if(current_comment_type==Comment.ANSWER)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_IMAGE_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_IMAGE_ONLY,Comment.ANSWER});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_IMAGE_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_IMAGE_TEXT_ONLY,Comment.QUESTION});
                    }
                }

            }
            else if(isvideo)
            {

                if(current_comment_type==Comment.AGREES)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_VIDEO_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_VIDEO_ONLY,Comment.AGREES});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_VIDEO_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_VIDEO_TEXT_ONLY,Comment.AGREES});
                    }
                }
                if(current_comment_type==Comment.DISAGREES)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_VIDEO_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_VIDEO_ONLY,Comment.DISAGREES});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_VIDEO_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_VIDEO_TEXT_ONLY,Comment.DISAGREES});
                    }
                }
                if(current_comment_type==Comment.QUESTION)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_VIDEO_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_VIDEO_ONLY,Comment.QUESTION});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_VIDEO_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_VIDEO_TEXT_ONLY,Comment.QUESTION});
                    }
                }
                if(current_comment_type==Comment.ANSWER)
                {
                    if(commentText.trim().isEmpty())
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_VIDEO_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_VIDEO_ONLY,Comment.ANSWER});
                    }
                    else
                    {
                        comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_VIDEO_TEXT_ONLY);
                        comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_VIDEO_TEXT_ONLY,Comment.QUESTION});
                    }
                }

            }



        }
        else if(fileUrlBeingSent.trim().isEmpty()==false)
        {

        }
        else if(commentText.trim().isEmpty()==false&returnUri==null&fileUrlBeingSent.isEmpty())
        {

            //send the text comment
            if(current_comment_type==Comment.AGREES)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.AGREES_TEXT_ONLY);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.AGREES_TEXT_ONLY,Comment.AGREES});
            }
            if(current_comment_type==Comment.DISAGREES)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.DISAGREES_TEXT_ONLY);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.DISAGREES_TEXT_ONLY,Comment.DISAGREES});
            }
            if(current_comment_type==Comment.QUESTION)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.QUESTION_TEXT_ONLY);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.QUESTION_TEXT_ONLY,Comment.QUESTION});
            }
            if(current_comment_type==Comment.ANSWER)
            {
                comment_values.put(OrgFields.COMMENT_TYPE,Comment.ANSWER_TEXT_ONLY);
                comment_values.put(OrgFields.COMMENT_TYPES,new int[]{Comment.ANSWER_TEXT_ONLY,Comment.ANSWER});
            }

        }



    }
    private void sendTextComment(UserInfo userInfo)
    {

        String commentText=emojicon_edit_text.getText().toString();
        Map<String, Object> comment_values = new HashMap<>();
        setWhoSentComment(userInfo,comment_values);
        setWhoCreatedConvo(comment_values);
        setCommentDetails(commentText,comment_values);

    }

    //creating a image comment

    //creating a video comment

    Spinner mediatypes_choosing_files;
    TabLayout media_source_menu;
    private MediaItemAdapter mediaItemAdapter;
    RecyclerView media_list;
    ProgressBar progress_bar_loading_head_comment;
    BaseBottomSheet bottomSheet;
    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView youTubePlayerView;
    YouTubePlayer youTubePlayer;
    LinearLayout video_preview_menu;
    FloatingActionButton video_next_item_preview;
    FloatingActionButton video_cancel_preview;
    @OnClick(R.id.attach_media_button)
    void onAttachMedia()
    {

        emojicon_edit_text.setHint("What's your take?");
        preview_area.setVisibility(View.GONE);
        bottomSheet = new SimpleCustomBottomSheet(this);
        bottomSheet.show();

        FloatingActionButton close_button_media_dialog=(FloatingActionButton) bottomSheet.findViewById(R.id.close_button_media_dialog);
        video_preview_menu=(LinearLayout) bottomSheet.findViewById(R.id.video_preview_menu);
        video_cancel_preview=(FloatingActionButton) bottomSheet.findViewById(R.id.video_cancel_preview);
        video_next_item_preview=(FloatingActionButton) bottomSheet.findViewById(R.id.video_next_item_preview);
        close_button_media_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.dismiss();
            }
        });

        progress_bar_loading_head_comment=(ProgressBar) bottomSheet.findViewById(R.id.progress_bar_loading_head_comment);
        media_source_menu=(TabLayout) bottomSheet.findViewById(R.id.media_source_menu);
        media_source_menu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mediaItemAdapter.notifyDataSetChanged();
                if(tab.getPosition()==0)
                {
                    if(mediatypes_choosing_files.getSelectedItemPosition()==0)
                    {
                        searchByImagesOnWeb();
                    }
                    else
                    {
                        searchVideosOnWeb();
                    }
                }
                else
                {

                    if(mediatypes_choosing_files.getSelectedItemPosition()==0)
                    {

                        searchByImagesOnGoogleDrive();
                    }
                    else
                    {
                        searchVideosOnGoogleDrive();
                    }

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        media_list=(RecyclerView) bottomSheet.findViewById(R.id.media_list);
        if(mediaItemAdapter==null)
        {
            mediaItemAdapter=new MediaItemAdapter();
        }
        media_list.setLayoutManager(new LinearLayoutManager(this));
        media_list.setAdapter(mediaItemAdapter);
        mediatypes_choosing_files=(Spinner) bottomSheet.findViewById(R.id.mediatypes_choosing_files);
        mediatypes_choosing_files.setVisibility(View.INVISIBLE);
        mediatypes_choosing_files.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(media_source_menu.getSelectedTabPosition()==0)
                {
                    if(i==0)
                    {
                        searchByImagesOnWeb();
                    }
                    else
                    {
                        searchVideosOnWeb();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        TextView convo_title=(TextView) bottomSheet.findViewById(R.id.convo_title);
        convo_title.setText(bookieActivity.getActivity_title());

        youTubePlayerView = (com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView)findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayerx) {
                super.onReady(youTubePlayerx);

                youTubePlayer=youTubePlayerx;

            }

            @Override
            public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String videoId) {
                super.onVideoId(youTubePlayer, videoId);
                video_preview_menu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);

                if(state== PlayerConstants.PlayerState.PLAYING)
                {

                }

            }
        });

        video_cancel_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CancelVideoPreview();

            }
        });
        video_next_item_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nextVideoItemMediaWeb();

            }
        });
        searchByImagesOnWeb();


    }

    @BindView(R.id.cancel_preview)
    FloatingActionButton cancel_preview;
    @BindView(R.id.next_item_preview)
    FloatingActionButton next_item_preview;


    int start=0;
    int video_start=0;

    //Searching the web
    private void searchByImagesOnWeb()
    {

        boolean images=true;
        if(mediatypes_choosing_files!=null)
        {
           if(mediatypes_choosing_files.getSelectedItemPosition()==0)
           {
               images=true;
           }
           else
           {
               images=false;
           }
        }
        if(images&mediaItemImagesWebSearchList_web.size()>0)
        {

            if(mediaItemAdapter!=null)
            {
                mediaItemAdapter.use_web_source=true;
                mediaItemAdapter.notifyDataSetChanged();
                mediatypes_choosing_files.setVisibility(View.VISIBLE);
                Log.i("searchingimagesweb_","s1 "+mediaItemImagesWebSearchList_web.size());
            }

        }
        else
        {

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.cancelAll("searchingimagesweb_"+start);
            String term=bookieActivity.getActivity_title().toString().trim().toLowerCase();
            String url="https://www.google.com/search?q="+term+"&tbm=isch";
            if(start<10)
            {
                url="https://www.google.com/search?q="+term+"&tbm=isch";
            }
            else
            {
                url="https://www.google.com/search?q="+term+"&tbm=isch&start="+start;
            }
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.i("searchingimagesweb","onResponse");
                            Document doc = Jsoup.parse(response);
                            Elements body=doc.getElementsByTag("body");
                            if(body!=null)
                            {

                                if(body.isEmpty()==false)
                                {

                                    Element body_1=body.get(0);
                                    Elements images=body_1.select("img[data-src]");
                                    for(Element image : images)
                                    {

                                        if(image.hasAttr("alt")&image.hasAttr("data-src"))
                                        {

                                            String alt=image.attr("alt");
                                            if(alt.trim().isEmpty()==false)
                                            {

                                                String image_link=image.attr("data-src");
                                                String image_desc=image.attr("alt");
                                                MediaItemWebSearch mediaItemWebSearch=new MediaItemWebSearch();
                                                mediaItemWebSearch.setImage(true);
                                                mediaItemWebSearch.setHeading(image_desc);
                                                mediaItemWebSearch.setLink(image_link);
                                                Log.i("searchingimagesweb",image_link);
                                                mediaItemImagesWebSearchList_web.add(mediaItemWebSearch);


                                            }

                                        }

                                    }

                                    if(mediaItemAdapter!=null)
                                    {
                                        mediaItemAdapter.use_web_source=true;
                                        mediaItemAdapter.notifyDataSetChanged();
                                        if(mediatypes_choosing_files!=null)
                                        {
                                            mediatypes_choosing_files.setVisibility(View.VISIBLE);
                                        }


                                    }



                                }

                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.i("searchingimagesweb","VolleyError error="+error.getMessage());


                }
            });
            stringRequest.setTag("searchingimagesweb_"+start);
            queue.add(stringRequest);

        }

    }

    private static final String PAGING = "PAGING";
    private void searchVideosOnWeb()
    {

        boolean videos=true;
        if(mediatypes_choosing_files!=null)
        {
            if(mediatypes_choosing_files.getSelectedItemPosition()==1)
            {
                videos=true;
            }
            else
            {
                videos=false;
            }
        }

        if(videos&mediaItemVideosWebSearchList_web.size()>0)
        {

            if(mediaItemAdapter!=null)
            {
                mediaItemAdapter.use_web_source=true;
                mediaItemAdapter.notifyDataSetChanged();
                mediatypes_choosing_files.setVisibility(View.VISIBLE);
                Log.i("searchingvideosweb_","s1 "+mediaItemVideosWebSearchList_web.size()
                        +" "+mediaItemAdapter.getItemCount());
            }

        }
        else
        {

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.cancelAll("searchingvideosweb_"+video_start);
            queue.cancelAll(PAGING);

            String term=bookieActivity.getActivity_title().toString().trim().toLowerCase();
            // Instantiate the RequestQueue.
            String url="https://www.google.com/search?q=youtube "+term+"&tbm=vid&start="+video_start;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the first 500 characters of the response string.
                            //Log.i("lxksfa","Response is: " + response.substring(0, 500));
                            Log.i("searchingvideosweb_","got the response");
                            Document doc = Jsoup.parse(response);
                            Elements body=doc.getElementsByTag("body");

                            if(body!=null)
                            {
                                if(body.isEmpty()==false)
                                {

                                    Log.i("searchingvideosweb_","body is not null ");
                                    Element body_1=body.get(0);

                                    Elements links = body_1.getElementsByTag("a");

                                    //Log.i("lxksfa","body="+body.html());

                                    Log.i("searchingvideosweb_","videos isEmpty="
                                            +links.isEmpty()+" "+links.size());

                                    loadLinks(links,false);





                                }

                            }





                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.i("searchingvideosweb_",error.getMessage());

                }
            });
            stringRequest.setTag("searchingvideosweb_"+video_start);
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        }



    }
    private void loadLinks(Elements links,boolean loadOnPaginate)
    {

        boolean found=false;
        for (Element link : links)
        {

            String linkHref = link.attr("href");
            String videoId= ImageUtil.getVideoId(linkHref);
            boolean has_video_id=false;
            if(videoId!=null)
            {
                if(videoId.isEmpty()==false)
                {
                    has_video_id=true;
                }
            }
            String linkText = link.text();

            Elements imgs=link.getElementsByTag("img");

            if(imgs.isEmpty()==false&link.hasAttr("aria-label")
                    &has_video_id&ImageUtil.isYoutubeUrl(linkHref))
            {
                String heading=link.attr("aria-label");
                Element thumbnail=imgs.get(0);
                if(thumbnail.hasAttr("src"))
                {
                    String srcdata=thumbnail.attr("src");
                    Log.i("lxksfa","linkHref="+linkHref+" "+heading+" "+srcdata);
                    MediaItemWebSearch mediaItemWebSearch=new MediaItemWebSearch();
                    mediaItemWebSearch.setHeading(heading);
                    mediaItemWebSearch.setLink(linkHref);
                    mediaItemWebSearch.setBase64Image(ImageUtil.getBase64String(srcdata));
                    mediaItemWebSearch.setVideo(true);

                    mediaItemVideosWebSearchList_web.add(mediaItemWebSearch);
                    found=true;

                }
                else
                {
                    Log.i("lxksfa","linkHref="+linkHref+" "+heading);
                }



            }

        }

        if(found)
        {
            if(mediaItemAdapter!=null)
            {
                mediaItemAdapter.use_web_source=true;
                mediaItemAdapter.notifyDataSetChanged();
            }
        }


    }

    //Searching Google Drive
    private void searchVideosOnGoogleDrive() {
    }

    private void searchByImagesOnGoogleDrive()
    {
    }

    @BindView(R.id.preview_area)
    FrameLayout preview_area;
    @BindView(R.id.preview_thumbnail)
    ImageView preview_thumbnail;



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
            View view= LayoutInflater.from(context).inflate(
                    R.layout.view_simple_custom_bottom_sheet,
                    this,
                    false
            );

            return view;
        }

    }

    private boolean notified_user=false;
    private HashMap<Integer,String> failed_jobs=new HashMap<Integer,String>();
    List<MediaItemWebSearch> mediaItemImagesWebSearchList_web=new ArrayList<MediaItemWebSearch>();
    List<MediaItemWebSearch> mediaItemVideosWebSearchList_web=new ArrayList<MediaItemWebSearch>();
    class MediaItemAdapter extends RecyclerView.Adapter<MediaItemAdapter.Holder>
    {

        public static final int IMAGE=0;
        public static final int VIDEO=1;
        public boolean use_web_source=true;
        @NonNull
        @Override
        public MediaItemAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if(viewType==VIDEO)
            {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_media_item_websearch_video
                                ,parent,false);
                return new Holder(view);
            }
            else
            {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_media_item_websearch
                                ,parent,false);
                return new Holder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull MediaItemAdapter.Holder holder, int position) {

            final int pos=position;
            if(use_web_source&mediatypes_choosing_files!=null)
            {
                if(mediatypes_choosing_files.getSelectedItemPosition()==0)
                {
                    //image
                    MediaItemWebSearch mediaItemWebImage=mediaItemImagesWebSearchList_web.get(pos);
                    String image_link=mediaItemWebImage.getLink();
                    String image_heading=mediaItemWebImage.getHeading();

                    holder.heading.setText(image_heading);
                    if(position==selectedIndexImagesWeb)
                    {
                        holder.heading.setText(Html.fromHtml("<b>"+holder.heading.getText().toString()+"</b>"));
                    }
                    loadImg(image_link,holder.thumbnail_view,pos);

                    holder.rootview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            preViewImage(pos);
                            bottomSheet.dismiss();

                        }
                    });

                }
                else
                {

                    //Video
                    MediaItemWebSearch mediaItemWebVideo=mediaItemVideosWebSearchList_web.get(pos);
                    String image_link=mediaItemWebVideo.getThumnail_url();
                    String video_heading=mediaItemWebVideo.getHeading();

                    holder.heading.setText(video_heading);

                    int yu=video_heading.indexOf("Play on YouTube.");
                    if(yu-1>0)
                    {
                        String jkls=video_heading.substring(0,yu);
                        holder.heading.setText(jkls);
                    }

                    if(position==selectedVideoMedia)
                    {
                        holder.heading.setText(Html.fromHtml("<b>"+holder.heading.getText().toString()+"</b>"));
                    }
                    String videoId=ImageUtil.getVideoId(mediaItemWebVideo.getLink());
                    if(videoId!=null)
                    {
                        //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                        loadVidImg(videoId,holder.thumbnail_view,position);

                    }

                    Log.i("searchingvideosweb_",mediaItemWebVideo.getHeading());

                    holder.rootview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String videoId=ImageUtil.getVideoId(mediaItemWebVideo.getLink());
                            if(videoId!=null)
                            {
                                //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                                preViewVideo(0,videoId);

                            }


                        }
                    });

                }
            }

        }

        @Override
        public int getItemViewType(int position) {
            if(use_web_source&mediatypes_choosing_files!=null)
            {
                if(mediatypes_choosing_files.getSelectedItemPosition()==0)
                {
                    //image
                    return IMAGE;
                }
                else
                {
                    //Video
                    return VIDEO;

                }
            }
            else
            {
                return IMAGE;
            }
        }

        @Override
        public int getItemCount() {
            if(use_web_source&mediatypes_choosing_files!=null)
            {
                if(mediatypes_choosing_files.getSelectedItemPosition()==0)
                {
                    return mediaItemImagesWebSearchList_web.size();
                }
                else
                {
                    return mediaItemVideosWebSearchList_web.size();
                }
            }
            else
            {
                return 0;
            }
        }

        public void loadImg(String image_link,CircleImageView imageView,Integer position)
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            Glide.with(TestGroup2.this)
                    .load(Uri.parse(image_link))
                    .centerCrop()
                    .apply(options)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
                                                    boolean isFirstResource) {
                            failed_jobs.put(position,image_link);
                            if(notified_user==false)
                            {
                                notified_user=true;
                                new MaterialDialog.Builder(TestGroup2.this)
                                        .title("We will try loading images that failed in every 5 seconds")
                                        .content("We will retry loading failed images every 5 seconds")
                                        .positiveText("Okay")
                                        .show();

                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if(failed_jobs.containsKey(position))
                            {
                                failed_jobs.remove(position);
                            }
                            return false;
                        }
                    })
                    .into(imageView);

        }

        public void loadImg(String image_link,ImageView imageView,Integer position)
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();
            Glide.with(TestGroup2.this)
                    .load(Uri.parse(image_link))
                    .centerCrop()
                    .apply(options)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            failed_jobs.put(position,image_link);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if(failed_jobs.containsKey(position))
                            {
                                failed_jobs.remove(position);
                            }
                            return false;
                        }
                    })
                    .into(imageView);

        }

        public void loadVidImg(String videoId, ImageView imageView, Integer position)
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            if(videoId!=null)
            {
                //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                if(videoId.isEmpty()==false)
                {

                    Glide.with(TestGroup2.this)
                            .load(Uri.parse("https://img.youtube.com/vi/"+videoId+"/0.jpg"))
                            .centerCrop()
                            .apply(options)
                            .into(imageView);
                    Log.i("gysha","videoID="+videoId);
                }

            }

        }

        public class Holder extends RecyclerView.ViewHolder {

            LinearLayout rootview;
            ImageView thumbnail_view;
            TextView heading;

            public Holder(@NonNull View itemView) {
                super(itemView);

                rootview=(LinearLayout) itemView.findViewById(R.id.rootview);
                thumbnail_view=(ImageView) itemView.findViewById(R.id.thumbnail_view);
                heading=(TextView) itemView.findViewById(R.id.heading);

            }


        }

    }

    int selectedVideoMedia=-1;
    private void preViewVideo(int i, String videoId)
    {


        if(i<mediaItemVideosWebSearchList_web.size()&i>-1)
        {

            selectedVideoMedia=i;
            MediaItemWebSearch mediaItemWebSearch=mediaItemVideosWebSearchList_web.get(i);
            youTubePlayerView.setVisibility(View.VISIBLE);
            video_preview_menu.setVisibility(View.VISIBLE);
            mediaItemAdapter.notifyDataSetChanged();

            if(youTubePlayer!=null)
            {

                youTubePlayer.loadVideo(videoId,0);

            }

        }

    }

    void nextVideoItemMediaWeb()
    {

        if(media_source_menu.getSelectedTabPosition()==0)
        {
            if(mediatypes_choosing_files.getSelectedItemPosition()==0)
            {
                //image


            }
            else
            {

                //Video
                selectedVideoMedia++;
                if(selectedVideoMedia>=mediaItemVideosWebSearchList_web.size())
                {
                    selectedVideoMedia=0;
                }
                MediaItemWebSearch mediaItemWebSearch=mediaItemVideosWebSearchList_web.get(selectedVideoMedia);
                String videoId=ImageUtil.getVideoId(mediaItemWebSearch.getLink());
                if(videoId!=null)
                {
                    //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                    preViewVideo(selectedVideoMedia,videoId);

                }



            }
        }


    }

    void CancelVideoPreview()
    {

        if(youTubePlayer!=null)
        {
            youTubePlayer.pause();
            video_preview_menu.setVisibility(View.GONE);
            youTubePlayerView.setVisibility(View.GONE);
        }

    }

    private void hideKeyBoard()
    {

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null)
        {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    int selectedIndexImagesWeb=-1;
    private void preViewImage(int index)
    {

        selectedIndexImagesWeb=index;
        if(index<mediaItemImagesWebSearchList_web.size()&index>-1)
        {


            preview_area.setVisibility(View.VISIBLE);
            MediaItemWebSearch mediaItemWebSearch=mediaItemImagesWebSearchList_web.get(index);
            //emojicon_edit_text.setHint(mediaItemWebSearch.getHeading());
            Toast.makeText(this, mediaItemWebSearch.getHeading(), Toast.LENGTH_SHORT).show();
            Log.i("sdfdsdfsd"," h="+preview_area.getMeasuredHeight()+" w="+preview_area.getMeasuredWidth());
            Glide.with(TestGroup2.this)
                    .load(Uri.parse(mediaItemWebSearch.getLink()))
                    .override(preview_area.getMeasuredWidth(),preview_area.getMeasuredHeight())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.i("sdfdsdfsd","e"+e.getMessage());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.i("sdfdsdfsd","onResourceReady");
                            return false;
                        }
                    })
                    .into(preview_thumbnail);

        }

    }

    @OnClick(R.id.cancel_preview)
    void existPreviewImage()
    {
        preview_area.setVisibility(View.GONE);
        emojicon_edit_text.setHint("What's your take?");
    }
    @OnClick(R.id.next_item_preview)
    void nextItemMediaWeb()
    {

        if(media_source_menu.getSelectedTabPosition()==0)
        {
            if(mediatypes_choosing_files.getSelectedItemPosition()==0)
            {
                //image
                selectedIndexImagesWeb++;
                if(selectedIndexImagesWeb>=mediaItemImagesWebSearchList_web.size())
                {
                    selectedIndexImagesWeb=0;
                }
                preViewImage(selectedIndexImagesWeb);

            }
            else
            {

                //Video



            }
        }


    }

}