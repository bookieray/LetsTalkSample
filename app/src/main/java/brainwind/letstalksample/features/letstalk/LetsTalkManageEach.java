package brainwind.letstalksample.features.letstalk;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.scwang.wave.MultiWaveHeader;

import org.joda.time.LocalDateTime;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import brainwind.letstalksample.CaptureInfo1;
import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.LoadingListener;
import brainwind.letstalksample.MainActivity;
import brainwind.letstalksample.R;
import brainwind.letstalksample.bookie_activity.BookieActivity;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NetworkUtils;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.fragments.AllTopicsForConvo;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.NewsMedia;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.Conversation;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentCreator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class LetsTalkManageEach extends AppCompatActivity
        implements CommentListener, LoadingListener {

    private static int OPERATION=0;
    private static final int GETTING_TIME_OFFSET=1;

    /*
    Start of Getting Conversation
    L1:https://todoist.com/showTask?id=6202994199&sync_id=6202994199
    Documentation: C:\Users\HP\Desktop\Bookie\Lets Talk Coding Plan.doc
     */

    //the rootview
    @BindView(R.id.rootview)
    RelativeLayout rootview;


    //the head of the activity
    //https://todoist.com/showTask?id=6202780089&sync_id=6202780089
    //2 MultiWaveHeader:  waveHeader
    @BindView(R.id.waveHeader)
    MultiWaveHeader waveHeader;
    //3 CardView:  topPanel
    @BindView(R.id.topPanel)
    CardView topPanel;
    //4 FloatingActionButton:  back_but
    @BindView(R.id.back_but)
    FloatingActionButton back_but;
    //5 Toolbar:  toolbar
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //6 ExpandableTextView:  label
    @BindView(R.id.label)
    ExpandableTextView label;
    //7 TextView:  status
    @BindView(R.id.status)
    TextView status;
    //8 TextView to indicate the activity type it is based on
    @BindView(R.id.activity_type)
    TextView activity_type;

    //Variables needed for the conversation
    //either an activity id which could be either be "have_your_say","event"
    // "project","partnership","article"
    //"announcement","org_interest","survey" (OrgFields.)
    //or activity id of activity type "lets_talk" (OrgFields.ACTIVITY_TYPE_LETS_TALK)
    String conversation_id="";
    //the conversation title
    String title="";
    //boolean to indicate whether the conversation is a standalone
    //or based on an activity
    //if it is based on an activity
    boolean is_standalone=true;

    //News source details
    boolean has_news_reference=false;
    String news_sourcelink="";
    String news_headline="";
    String news_source="";
    String orgmaincolor="";
    private FirebaseAuth mAuth;

    //The Activity being created, do not perform long threaded operations here
    //no network requests or updated the views since there are still being created
    //this will block the OnCreate thread since it has
    //https://developer.android.com/guide/components/activities/activity-lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_talk_manage_each);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        ButterKnife.bind(this);
        Memory memory=new Memory(this);
        if(memory.getString(OrgFields.SERVER_TIME_OFFSET).isEmpty()==false
                &memory.getString(OrgFields.SERVER_TIME_OFFSET)!="-1")
        {
            estimatedServerTimeMs=Long.parseLong(memory.getString(OrgFields.SERVER_TIME_OFFSET));
        }
        else
        {
            getServerTimeOffset();
        }
        Log.i("and_life_cycle","onCreate estimatedServerTimeMs="
                +estimatedServerTimeMs);





    }

    public Intent getIntentFromActivity()
    {
        return getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("and_life_cycle","onStart");
        if(toolbar==null)
        {
            ButterKnife.bind(this);

            setSupportActionBar(toolbar);
            setTitle("");

            setUpTextToSpeech();
            EmojIconActions emojIcon=new EmojIconActions(this,rootview,emojicon_edit_text,emoji_button);
            emojIcon.ShowEmojIcon();

        }
    }

    @BindView(R.id.emoji_button)
    ImageView emoji_button;
    //once
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i("and_life_cycle","onPostCreate");
        monitorConnect();
        getServerTimeOffset();
        L0();

    }



    boolean isKeyboardShowing=false;
    int last_reading_pos=0;
    RecyclerView comment_list;
    private void checkCommentListKeyBoard(Fragment currentTopicForConvo)
    {
        try {

            Log.i("chckCmmntLstKyBard",this.getPackageName()+" "+(currentTopicForConvo!=null));
            if(currentTopicForConvo!=null)
            {
                CurrentTopicForConvo currentTopicForConvo1=(CurrentTopicForConvo)currentTopicForConvo;
                RecyclerView comment_list=currentTopicForConvo1.getView().findViewById(R.id.comment_list);
                this.comment_list=comment_list;
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
                                            LinearLayoutManager ln=(LinearLayoutManager)comment_list.getLayoutManager();


                                        }
                                    }
                                    else {
                                        // keyboard is closed
                                        if (isKeyboardShowing) {
                                            isKeyboardShowing = false;
                                            currentTopicForConvo1.getCommentWorker().getCommentAdapter().notifyDataSetChanged();
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
            }

        }
        catch(Exception exception)
        {
            Log.i("chckCmmntLstKyBard","e="+exception.getMessage());
        }
    }

    //Start of Getting Conversation
    @OnClick(R.id.back_but)
    void CloseConvo()
    {
        finish();
    }

    //https://todoist.com/showTask?id=6202994199&sync_id=6202994199
    //L0:Get intent data for the conversation, id
    // , standalone, headline and news reference link
    // , title and source.
    private void L0()
    {

        loading.setVisibility(View.VISIBLE);

        Intent intent=getIntent();
        boolean has_intent_data=getIntent().hasExtra(OrgFields.CONVERSATION_ID)
                &getIntent().hasExtra(OrgFields.IS_STANDALONE_CONVO)
                &getIntent().hasExtra(OrgFields.TITLE)
                &getIntent().hasExtra(OrgFields.ORG_MAIN_COLOR)
                &getIntent().hasExtra(OrgFields.ORG_NAME)
                &getIntent().hasExtra(OrgFields.ORGID)
                &getIntent().hasExtra(OrgFields.UID)
                &getIntent().hasExtra(OrgFields.ADMIN_NAME)
                &getIntent().hasExtra(OrgFields.ADMIN_PROFILE_PATH)
                &getIntent().hasExtra(OrgFields.ACTIVITY_TYPE);


        if(has_intent_data)
        {
            showFromIntent(intent);
        }
        else
        {
            downloadConvo();
        }

    }

    private void showFromIntent(Intent intent)
    {

        //Set the background of the organization to the activity
        String orgmaincolor=getIntent()
                .getStringExtra(OrgFields.ORG_MAIN_COLOR);
        int orgbackcolor= Color.parseColor(orgmaincolor);
        waveHeader.setStartColor(orgbackcolor);
        waveHeader.setCloseColor(orgbackcolor);
        //got the conversation id
        conversation_id=intent
                .getStringExtra(OrgFields.CONVERSATION_ID);
        //boolean to indicate whether the conversation is a standalone
        //or based on an activity
        is_standalone=intent
                .getBooleanExtra(OrgFields.IS_STANDALONE_CONVO,true);

        /* start of handling the details in the toppanel cardview
         */
        if(is_standalone)
        {
            activity_type.setText("LETS TALK");
        }
        else
        {

            if(intent.hasExtra(OrgFields.ACTIVITY_TYPE))
            {
                String activity_type_txt=intent.getStringExtra(OrgFields.ACTIVITY_TYPE);
                activity_type.setText(activity_type_txt.toLowerCase().replace("_"," "));
            }

        }

        //Title
        title=intent.getStringExtra(OrgFields.TITLE);
        label.setText(title);
        /*end of handling the details in the toppanel cardview
         */

        //Activity type
        String activity_type_txt=intent.getStringExtra(OrgFields.ACTIVITY_TYPE);
        activity_type.setText(activity_type_txt);

            /*
            News Source for taping on the toppanel cardview to view news source
            https://todoist.com/showTask?id=6202985731&sync_id=6202985731
             */
        if(intent.hasExtra(OrgFields.NEWS_SOURCELINK)
                &intent.hasExtra(OrgFields.NEWS_HEADLINE)
                &intent.hasExtra(OrgFields.NEWS_SOURCE))
        {
            has_news_reference=true;
            //News Source Link
            news_sourcelink=intent.getStringExtra(OrgFields.NEWS_SOURCELINK);
            //News Headline
            news_headline=intent.getStringExtra(OrgFields.NEWS_HEADLINE);
            //News Source
            news_source=intent.getStringExtra(OrgFields.NEWS_SOURCE);
        }
        else {
            has_news_reference=false;
        }

        //Organization background color
        this.orgmaincolor=intent.getStringExtra(OrgFields.ORG_MAIN_COLOR);
        //Set Up ViewPager and tabs
        setUpViewPager();


        if(has_news_reference)
        {
            //Has news reference (green)
            status.setText("Has Credible News event");
            status.setTextColor(getResources().getColor(R.color.green));
        }
        else
        {
            //No news reference (red)
            status.setText("No news source");
            status.setTextColor(getResources().getColor(R.color.red));
        }



    }

    private boolean ny1=false;
    private void notifyUserOfNewsReference()
    {


        if(ny1==false)
        {
            new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(status,"Convo based on this (Tap to read)"
                                    ,"News:"+news_headline)
                    )
                    .listener(new TapTargetSequence.Listener() {
                        // This listener will tell us when interesting(tm) events happen in regards
                        // to the sequence
                        @Override
                        public void onSequenceFinish() {
                            // Yay
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                            // Perform action for the current target
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Boo
                        }
                    }).start();
            ny1=true;
        }

    }

    @BindView(R.id.loading)
    RelativeLayout loading;
    @BindView(R.id.loading_label)
    TextView loading_label;
    @BindView(R.id.loading_desc)
    TextView loading_desc;
    @BindView(R.id.progress_bar_loading)
    ProgressBar progress_bar_loading;
    CountDownTimer retry_timer;
    //L1 – get Convo if there is no intent data
    //variables for loading
    boolean loading_in_progress=false;
    boolean loading_failed=false;
    boolean loading_successfully=false;
    private int num_attempts=0;

    //the task to firestore
    Task<DocumentSnapshot> task;
    //the results
    //either a conversation
    Conversation conversation;
    //or a bookie activity
    BookieActivity bookieActivity;

    long estimatedServerTimeMs=0;
    boolean timeoffset_running=false;
    private void getServerTimeOffset() {


        timeoffset_running=true;
        Memory memory=new Memory(LetsTalkManageEach.this);
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);
        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        please_wait=new MaterialDialog.Builder(LetsTalkManageEach.this)
                .title("We fetching the time offset from server")
                .content("We need to retrieve the online time to make everything work in order")
                .cancelable(false)
                .show();
        boolean sta=false;
        if(jnm.isEmpty()==false)
        {

            long localtimeoffset=Long.parseLong(jnm);
            if(localtimeoffset==-1)
            {
                sta=true;
            }
            else
            {

                sta=false;

            }

        }
        else
        {
            sta=true;
        }

        Log.i("ResetTimeOffset","sta="+sta+" set_up_vp="+set_up_vp);

        if(sta)
        {
            OPERATION=GETTING_TIME_OFFSET;
            DatabaseReference offsetRef =
                    FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
            offsetRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    timeoffset_running=false;
                    long offset = snapshot.getValue(Long.class);
                    estimatedServerTimeMs = System.currentTimeMillis() + offset;
                    LocalDateTime localDateTime=new LocalDateTime(estimatedServerTimeMs);

                    Log.w("jskasd", "offset="+offset
                            +" estimatedServerTimeMs="+estimatedServerTimeMs
                            +" "+localDateTime.getDayOfMonth()+" "+localDateTime.getMonthOfYear()
                            +" "+localDateTime.getYear()
                            +" "+String.valueOf(offset));

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    Memory memory=new Memory(LetsTalkManageEach.this);
                    memory.Save(OrgFields.SERVER_TIME_OFFSET,String.valueOf(offset));

                    if(set_up_vp)
                    {
                        setUpViewPager();
                        set_up_vp=false;
                    }

                    startResetWorker();




                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("jskasd", "Listener was cancelled");

                    timeoffset_running=false;
                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    please_wait=new MaterialDialog.Builder(LetsTalkManageEach.this)
                            .title("We could not connect to server")
                            .content("There has been an error "+error.getMessage())
                            .cancelable(false)
                            .positiveText("Try again")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    getServerTimeOffset();

                                }
                            })
                            .negativeText("Never Mind")
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    finish();

                                }
                            })
                            .show();

                    startResetWorker();


                }



            });
        }
        else if(set_up_vp)
        {
            if(please_wait!=null)
            {
                please_wait.dismiss();
            }
            setUpViewPager();
            set_up_vp=false;
            startResetWorker();

        }
        else
        {
            if(please_wait!=null)
            {
                please_wait.dismiss();
            }

            startResetWorker();

        }


    }

    //gets the convo, if no intent data
    boolean download_convo_running=false;
    @OnClick(R.id.retry_button_loading)
    void downloadConvo()
    {

        download_convo_running=true;
        missing_convo.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        num_attempts++;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                //if there was a count down timer when attempting to retry
                if(retry_timer!=null)
                {
                    retry_timer.cancel();
                }
                if(NetworkUtils.isConnectedNetwork(LetsTalkManageEach.this)==false)
                {
                    loading_in_progress=false;
                    loading_failed=true;
                    L21();
                }
                //Change 2 text to reflect what is loading and change text color to black.
                progress_bar_loading.setVisibility(View.VISIBLE);
                loading_label.setVisibility(View.VISIBLE);
                loading_label.setTextColor(Color.BLACK);
                loading_desc.setTextColor(Color.BLACK);
                loading_label.setText("Please wait");
                loading_desc.setText("We are downloading your conversation");

                //to ensure tracking a request to firestore in progress successfully check the task
                //completion to verify
                if(task!=null)
                {
                    if(task.isComplete())
                    {
                        loading_in_progress=false;
                        if(conversation==null&bookieActivity==null)
                        {
                            loading_failed=true;
                        }
                        else
                        {
                            loading_failed=false;
                        }
                    }
                    else
                    {
                        loading_in_progress=true;

                    }
                }

                //a boolean to indicate to the user a query is in progress via snackbar.
                if(loading_in_progress==false)
                {

                    //cancel any previous attempts to firestore to prevent many network requests
                    //running congruently, simultaneously
                    if(task!=null)
                    {
                        if(task.isComplete()==false)
                        {

                        }
                    }
                    loading_in_progress=true;
                    /*
                    Testing sample data if the conversation id and title is empty
                    set up sample data that corresponds to the cloud
                    REMOVE THIS WHEN YOU PREPARE FOR RELEASE
                     */
                    if(conversation_id.isEmpty())
                    {
                        conversation_id="S3aNh6Jq7ZajBiJS2jut";
                        title="Kevin Samuels’ death result of hypertension ";
                        is_standalone=true;
                        has_news_reference=true;

                    }

                    //if there is no conversation id
                    if(conversation_id.trim().isEmpty())
                    {
                        new MaterialDialog.Builder(LetsTalkManageEach.this)
                                .title("Oops")
                                .content("Something is wrong, please check how you were referred here")
                                .positiveText("Okay")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog,
                                                        @NonNull DialogAction which) {

                                        finish();

                                    }
                                })
                                .cancelable(false)
                                .show();
                    }
                    //https://todoist.com/showTask?id=6202784890&sync_id=6202784890
                    //Show 1: RelativeLayout: loading
                    loading.setVisibility(View.VISIBLE);
                    //Change 2 text to reflect what is loading and change text color to black.
                    loading_label.setVisibility(View.VISIBLE);
                    loading_label.setTextColor(Color.BLACK);
                    loading_desc.setTextColor(Color.BLACK);
                    loading_label.setText("Please wait");
                    if(title.trim().isEmpty()==false)
                    {
                        loading_desc.setText(Html.fromHtml("downloading convo for '<i>"+title+"</i>'"));
                    }
                    else
                    {
                        loading_desc.setText("downloading convo for "+conversation_id);
                    }

                    //when there is no internet connection to a network
                    //notify the user
                    if(NetworkUtils.isConnectedNetwork(LetsTalkManageEach.this)==false)
                    {

                        FailedToFetch("Error loading conversation",
                                "<b>You are not connected to a network</b>");


                    }
                    //only perform the query when there is a connection to internet network
                    else
                    {


                        UserInfoDatabase.databaseWriteExecutor
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        //get the conversation from cloud
                                        DocumentReference query;
                                        query=CloudWorker
                                                .getActivities()
                                                .document(conversation_id);
                                        task=query.get();

                                        task.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        download_convo_running=false;
                                                        Log.i("get_convo","onFailure");
                                                        LetsTalkManageEach.this
                                                                .runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                        if(please_wait!=null)
                                                                        {
                                                                            please_wait.dismiss();
                                                                        }
                                                                        FailedToFetch("Error contacting our server",
                                                                                e.getMessage());

                                                                    }
                                                                });

                                                    }
                                                })
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        download_convo_running=false;

                                                        LetsTalkManageEach.this
                                                                .runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                        if(please_wait!=null)
                                                                        {
                                                                            please_wait.dismiss();
                                                                        }
                                                                        OnSuccessfulFetch(documentSnapshot);

                                                                    }
                                                                });



                                                    }
                                                });

                                    }
                                });
                        //simultaneously a get request to test the connection even if the user is connected to a network
                        //https://todoist.com/showTask?id=6202786009&sync_id=6202786009
                        /* Volley Request using get request to check internet connectivity with a connected network.
                        “google.com”
                         */
                        L21();

                    }

                }
                //indicate to the user a query is in progress via snackbar
                else
                {
                    Snackbar.make(rootview,
                            "We currently attempting to get your convo",
                            Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void FailedToFetch(String heading,String desc)
    {

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        final String descf=desc;
        //loading failed because there is no network to perform it
        loading_in_progress=false;
        loading_failed=true;

        //OnFailure
        //Show 1: RelativeLayout: loading
        loading.setVisibility(View.VISIBLE);
        //Change 2:(TextView:loading_label) text color  to red and update with error message,
        // check internet connectivity and notify user via snackbar.
        loading_desc.setTextColor(Color.RED);
        loading_label.setTextColor(Color.RED);
        loading_label.setText(heading);
        loading_desc.setText(Html.fromHtml(desc));
        //3:Button:retry_button_loading when clicked will retry L2()
        // so there must be a Boolean to indicate L2 failed.
        // So if this Boolean is true then recall L2()
        // and a boolean to indicate to the user a query is in progress via snackbar.
        loading_in_progress=false;
        loading_failed=true;
        //Hide 4:ProgressBar: progress_bar_loading
        progress_bar_loading.setVisibility(View.GONE);
        //Countdown timer 30 seconds at finish call L2().
        Intent intent=getIntent();
        boolean has_intent_data=intent.hasExtra(OrgFields.CONVERSATION_ID)
                &intent.hasExtra(OrgFields.IS_STANDALONE_CONVO)
                &intent.hasExtra(OrgFields.TITLE)
                &getIntent().hasExtra(OrgFields.ORG_MAIN_COLOR)
                &getIntent().hasExtra(OrgFields.ACTIVITY_TYPE);

        if(has_intent_data)
        {
            loading.setVisibility(View.GONE);
            missing_convo.setVisibility(View.GONE);
            showFromIntent(intent);
        }
        else
        {
            retry_timer= new CountDownTimer(20000,1000) {
                @Override
                public void onTick(long l) {
                    long secs=l/1000;
                    loading_desc.setText(Html.fromHtml(descf+
                            " retrying in "+secs+"s</b>"));
                }

                @Override
                public void onFinish() {
                    downloadConvo();
                }
            }.start();
        }

    }

    private void OnSuccessfulFetch(DocumentSnapshot documentSnapshot)
    {

        loading_successfully=true;
        Log.i("get_convo","onSuccess");
        loading.setVisibility(View.GONE);

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        if(documentSnapshot!=null)
        {
            if(documentSnapshot.exists())
            {

                //get first item (only 1) in data set and
                // parse item to either Bookie Activity
                // or Conversation and update views in L3.
                //update toppanel cardview
                bookieActivity=new BookieActivity(documentSnapshot);
                conversation_id=bookieActivity.getActivity_id();
                is_standalone=bookieActivity.isIs_activity_reference();
                if(is_standalone)
                {
                    conversation=new Conversation(documentSnapshot);
                    conversation_id=conversation.getActivity_id();
                }

                L32();


            }
            else
            {
                L31();
            }
        }
        else
        {
            L31();
        }



    }

    private void startResetWorker()
    {

        Log.i("ResetTimeOffset","startResetWorker called");
        Constraints constraints = new Constraints.Builder()
                .build();

        PeriodicWorkRequest resetRequest =
                new PeriodicWorkRequest.Builder(ResetTimeOffset.class,
                        6, TimeUnit.HOURS)
                        // Constraints
                        .setConstraints(constraints)
                        .addTag("reset_timeoffset")
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "reset_timeoffset",
                ExistingPeriodicWorkPolicy.KEEP,
                resetRequest);


    }

    /*

1.	FrameLayout: uystra
2.	MultiWaveHeader:  waveHeader
3.	CardView:  topPanel
4.	FloatingActionButton:  back_but
5.	Toolbar:  toolbar
6.	ExpandableTextView:  label
 */
    //OnData
    private void L32()
    {

        if(bookieActivity!=null||conversation!=null)
        {
            //Update L3:2 with the org main color parsed as a color integer.
            String orgbackcolor=bookieActivity.getOrg_main_color();
            int orgcolor=Color.parseColor(orgbackcolor);
            waveHeader.setStartColor(orgcolor);
            waveHeader.setCloseColor(orgcolor);
            //Update L3:6 with the bookie activity title or conversation title.
            label.setText(bookieActivity.getActivity_title());
            /*
            3.	Check if there is a news reference and update L3: status with either:
                •	No news reference (red)
                •	Has news reference (green)
             */
            if(bookieActivity.isIs_news_reference())
            {
                //Has news reference (green)
                status.setText("Has Credible News event");
                status.setTextColor(getResources().getColor(R.color.green));
            }
            else
            {
                //No news reference (red)
                status.setText("No news source");
                status.setTextColor(getResources().getColor(R.color.red));
            }

            //reveal the activity the activity is based on
            if(bookieActivity.getActivity_type()!=OrgFields.ACTIVITY_TYPE_LETS_TALK)
            {
                activity_type.setText(bookieActivity.getActivity_type().toUpperCase().replace("_"," "));
            }

            has_news_reference=bookieActivity.isIs_news_reference();
            //update the variables
            is_standalone=bookieActivity.isIs_activity_reference();
            has_news_reference=bookieActivity.isIs_news_reference();
            conversation_id=bookieActivity.getActivity_id();
            title=bookieActivity.getActivity_title();
            if(has_news_reference)
            {
                news_sourcelink=bookieActivity.getNews_sourcelink();
                news_headline=bookieActivity.getNews_headline();
                news_source=bookieActivity.getNews_source();
                orgmaincolor=bookieActivity.getOrg_main_color();

                //the user has to know about the news reference


            }

            //Set Up ViewPager and tabs
            setUpViewPager();

        }
        else
        {

            Intent intent=getIntent();
            boolean has_intent_data=intent.hasExtra(OrgFields.CONVERSATION_ID)
                    &intent.hasExtra(OrgFields.IS_STANDALONE_CONVO)
                    &intent.hasExtra(OrgFields.TITLE)
                    &getIntent().hasExtra(OrgFields.ORG_MAIN_COLOR)
                    &getIntent().hasExtra(OrgFields.ACTIVITY_TYPE);

            if(has_intent_data)
            {
                showFromIntent(intent);
                //Set Up ViewPager and tabs
                setUpViewPager();
            }

        }


    }

    /*Volley Request using get request to check internet connectivity with a connected network.
        “google.com”
         */
    private void L21()
    {

        monitorNetworkChanges();

    }

    private ConnectivityManager.NetworkCallback networkCallback =
            new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);


            if(loading_successfully==false)
            {
                downloadConvo();
                Log.i("get_convo","onAvailable, started a request loading_failed="+loading_failed+" "+num_attempts
                        +" loading_successfully="+loading_successfully);
            }
            else
            {
                Log.i("get_convo","onAvailable, did not start a request loading_failed="+loading_failed+" "+num_attempts
                        +" loading_successfully="+loading_successfully);
                loading.setVisibility(View.GONE);
            }
            notifyBackOnline();
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);

            notifyOffline();
            if(loading_failed==false&loading_successfully==false)
            {
                downloadConvo();
                Log.i("get_convo","onAvailable, started a request loading_failed="+loading_failed
                        +" "+num_attempts+" loading_successfully="+loading_successfully);
            }
            else
            {
                Log.i("get_convo","onAvailable, did not start a request loading_failed="+loading_failed
                        +" "+num_attempts+" loading_successfully="+loading_successfully);
            }

        }

        @Override
        public void onCapabilitiesChanged(@NonNull Network network,
                                          @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            final boolean unmetered = networkCapabilities
                    .hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED);



        }

    };

    private void monitorNetworkChanges()
    {

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            connectivityManager = (ConnectivityManager) this.getSystemService(ConnectivityManager.class);
        }
        else
        {
            connectivityManager=(ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback);




    }

    private void notifyOffline()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                disable_typing_area.setVisibility(View.VISIBLE);
                if(emojicon_edit_text.isFocused())
                {
                    tellUserMsg("You are currently offline");

                }
                else
                {
                    Snackbar.make(rootview,
                            "You are currently offline",
                            Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    @BindView(R.id.disable_typing_area)
    FrameLayout disable_typing_area;

    @OnClick({R.id.disable_typing_area,R.id.disable_typing_area_label})
    void TellUserOffline()
    {
        notifyOffline();
    }

    private void notifyBackOnline() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                disable_typing_area.setVisibility(View.GONE);
                if(num_attempts>1)
                {
                    if(emojicon_edit_text.isFocused())
                    {
                        //tell the user using text to speech
                        tellUserMsg("You are back online");
                    }
                    else
                    {
                        Snackbar.make(rootview,
                                "You are back online",
                                Snackbar.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    tellUserMsg("You are back online");
                }

            }
        });
    }

    TextToSpeech t1;
    boolean t1_initiated=false;
    private void setUpTextToSpeech()
    {

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1_initiated=true;
                }
            }
        });



    }
    private void tellUserMsg(String toSpeak)
    {

        if(t1!=null)
        {

            if(t1_initiated)
            {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else
                {
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }

            }

        }


    }
    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }



    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.content_pager)
    ViewPager2 content_pager;
    Fragment current_Topic_for_convo;

    public static final int LOADING_COMMENTS=0;

    @Override
    public void onLoadFinish(int operation)
    {

        if(operation==LOADING_COMMENTS)
        {

            if(has_news_reference)
            {
                Log.i("msghhdi","has_news_reference="+has_news_reference
                        +" ny1="+ny1);
                //the user has to know about the news reference
                notifyUserOfNewsReference();

            }



        }

    }

    //Page Adapter for content_pager
    private class PageAdapter extends FragmentStateAdapter {
        public PageAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {

            if(getIntent().hasExtra(OrgFields.IS_STANDALONE_CONVO))
            {
                is_standalone=getIntent().getBooleanExtra(OrgFields.IS_STANDALONE_CONVO,false);
            }
            if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
            {
                conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);

            }

            if(position==0)
            {

                if(is_standalone&conversation!=null)
                {
                    if(getIntent().hasExtra(CurrentTopicForConvo.STARTING_SIGNATURE))
                    {
                        String starting_sign=getIntent()
                                .getStringExtra(CurrentTopicForConvo.STARTING_SIGNATURE);
                        if(getIntent().hasExtra(CurrentTopicForConvo.SET))
                        {

                            int set=getIntent().getIntExtra(CurrentTopicForConvo.SET,1);
                            if(getIntent().hasExtra(OrgFields.NO_MORE_PREV_COMMENTS))
                            {
                                boolean no_more=getIntent().getBooleanExtra(OrgFields.NO_MORE_PREV_COMMENTS
                                        ,false);
                                if(getIntent().hasExtra(CurrentTopicForConvo.TSECONDS))
                                {
                                    long tseconds=getIntent().getLongExtra(CurrentTopicForConvo.TSECONDS
                                            ,0);
                                    current_Topic_for_convo= CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    conversation.getActivity_title(),
                                                    estimatedServerTimeMs,
                                                    starting_sign,set,
                                                    no_more,tseconds);
                                }
                                else
                                {
                                    current_Topic_for_convo= CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    conversation.getActivity_title(),
                                                    estimatedServerTimeMs,starting_sign,set,no_more);
                                }

                            }
                            else
                            {

                                if(getIntent().hasExtra(CurrentTopicForConvo.TSECONDS))
                                {
                                    long tseconds=getIntent().getLongExtra(CurrentTopicForConvo.TSECONDS
                                            ,0);
                                    current_Topic_for_convo= CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    conversation.getActivity_title(),
                                                    estimatedServerTimeMs,starting_sign,
                                                    set,tseconds);
                                }
                                else
                                {
                                    current_Topic_for_convo= CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    conversation.getActivity_title(),
                                                    estimatedServerTimeMs,starting_sign,set);
                                }

                            }

                        }
                        else
                        {
                            if(getIntent().hasExtra(CurrentTopicForConvo.TSECONDS))
                            {
                                long tseconds=getIntent().getLongExtra(CurrentTopicForConvo.TSECONDS
                                        ,0);
                                current_Topic_for_convo= CurrentTopicForConvo
                                        .newInstance(conversation_id,
                                                conversation.getActivity_title(),estimatedServerTimeMs
                                                ,starting_sign,tseconds);
                            }
                            else
                            {
                                current_Topic_for_convo= CurrentTopicForConvo
                                        .newInstance(conversation_id,
                                                conversation.getActivity_title(),estimatedServerTimeMs
                                                ,starting_sign);
                            }
                        }
                    }
                    else
                    {
                        current_Topic_for_convo= CurrentTopicForConvo
                                .newInstance(conversation_id,
                                        conversation.getActivity_title(),estimatedServerTimeMs);
                    }

                    return current_Topic_for_convo;
                }
                else
                {

                    if(getIntent().hasExtra(CurrentTopicForConvo.STARTING_SIGNATURE)) {
                        String starting_sign = getIntent()
                                .getStringExtra(CurrentTopicForConvo.STARTING_SIGNATURE);
                        if(getIntent().hasExtra(CurrentTopicForConvo.SET))
                        {
                            int set=getIntent().getIntExtra(CurrentTopicForConvo.SET,1);

                            if(getIntent().hasExtra(OrgFields.NO_MORE_PREV_COMMENTS))
                            {

                                boolean no_more=getIntent().getBooleanExtra(OrgFields.NO_MORE_PREV_COMMENTS
                                        ,false);
                                if(getIntent().hasExtra(CurrentTopicForConvo.TSECONDS)) {
                                    long tseconds = getIntent().getLongExtra(CurrentTopicForConvo.TSECONDS
                                            , 0);
                                    current_Topic_for_convo=CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    bookieActivity.getActivity_title()
                                                    ,true,estimatedServerTimeMs,
                                                    starting_sign,set,no_more,tseconds);
                                }
                                else
                                {
                                    current_Topic_for_convo=CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    bookieActivity.getActivity_title()
                                                    ,true,estimatedServerTimeMs,starting_sign,set,no_more);
                                }

                            }
                            else
                            {
                                if(getIntent().hasExtra(CurrentTopicForConvo.TSECONDS)) {
                                    long tseconds = getIntent().getLongExtra(CurrentTopicForConvo.TSECONDS
                                            , 0);
                                    current_Topic_for_convo=CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    bookieActivity.getActivity_title()
                                                    ,true,estimatedServerTimeMs,
                                                    starting_sign,set,tseconds);
                                }
                                else
                                {
                                    current_Topic_for_convo=CurrentTopicForConvo
                                            .newInstance(conversation_id,
                                                    bookieActivity.getActivity_title()
                                                    ,true,estimatedServerTimeMs,starting_sign,set);
                                }

                            }
                        }
                        else
                        {
                            current_Topic_for_convo=CurrentTopicForConvo
                                    .newInstance(conversation_id,
                                            bookieActivity.getActivity_title()
                                            ,true,estimatedServerTimeMs,starting_sign);
                        }

                    }
                    else
                    {
                        current_Topic_for_convo=CurrentTopicForConvo
                                .newInstance(conversation_id,
                                        bookieActivity.getActivity_title()
                                        ,true,estimatedServerTimeMs);
                    }

                    return current_Topic_for_convo;
                }

            }
            else if(position==1)
            {
                return AllTopicsForConvo
                        .newInstance(conversation_id
                                ,is_standalone);
            }
            else
            {
                return NewsMedia
                        .newInstance(conversation_id
                        ,title);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }



    }
    PageAdapter pageAdapter;
    boolean set_up_vp=false;
    private void setUpViewPager() {

        Memory memory = new Memory(LetsTalkManageEach.this);
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);
        long localtimeoffset = -1;
        if(jnm.isEmpty()==false)
        {
            localtimeoffset=Long.parseLong(jnm);
        }
        LocalDateTime localDateTime=new LocalDateTime(localtimeoffset);
        Log.i("L33","localtimeoffset="+localtimeoffset+" "+localDateTime.getMonthOfYear());
        if (jnm.isEmpty()||localtimeoffset==-1) {
            if (please_wait != null) {
                please_wait.dismiss();
            }
            please_wait = new MaterialDialog.Builder(LetsTalkManageEach.this)
                    .title("We fetching the time offset from server")
                    .content("We need to retrieve the online time to make everything work in order")
                    .cancelable(false)
                    .show();
            getServerTimeOffset();
            set_up_vp=true;
        }
        else
        {


            if(content_pager.getAdapter()==null)
            {

                pageAdapter = new PageAdapter(this);
                content_pager.setAdapter(pageAdapter);
                //align tabs with the view pager
                tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        content_pager.setCurrentItem(tab.getPosition());
                        if(tab.getPosition()>0)
                        {
                            typing_area.setVisibility(View.GONE);
                        }
                        else
                        {
                            if(head_comment!=null)
                            {
                                typing_area.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                typing_area.setVisibility(View.GONE);
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

                content_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);

                        tabs.selectTab(tabs.getTabAt(position));
                        if(position>0)
                        {
                            typing_area.setVisibility(View.GONE);
                        }
                        else
                        {
                            if(head_comment!=null)
                            {
                                typing_area.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                typing_area.setVisibility(View.GONE);
                            }
                        }

                    }
                });

                //testing warning dialog
                //remove when tested or preparing for release
                //has_news_reference=false;
                if(has_news_reference==false)
                {
                    new MaterialDialog.Builder(this)
                            .title("Be carreful")
                            .content("This convo has no attached news event it is based on, " +
                                    "please switch to the news/media tab to view some credible news events to compare")
                            .cancelable(false)
                            .positiveText("Switch to news/media")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    //switch to news/media
                                    tabs.selectTab(tabs.getTabAt(2));

                                }
                            })
                            .negativeText("Later")
                            .show();
                }



            }

        }


    }

    //OnNoData
    @BindView(R.id.missing_data)
    RelativeLayout missing_convo;
    @BindView(R.id.missing_data_heading)
    TextView missing_data_heading;
    @BindView(R.id.missing_data_description)
    TextView missing_data_description;
    @BindView(R.id.missing_data_button)
    Button missing_convo_button;
    private void L31()
    {
        loading.setVisibility(View.GONE);
        missing_convo.setVisibility(View.VISIBLE);
        missing_data_heading.setText("Conversation not found");
        missing_data_description.setText("Conversation is either missing or deleted for non-compliance reasons.");
        missing_convo_button.setText("VIEW OTHER CONVOS");
    }
    @OnClick(R.id.missing_data_button)
    void MissingConvo()
    {
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.MOVE_TO_ACTIVITIES,true);
        startActivity(intent);
    }

    //when user taps on the topPanel
    //
    @OnClick({R.id.topPanel,R.id.label,R.id.activity_type,R.id.status})
    void UserTopPanel()
    {
        if(has_news_reference&conversation_id.isEmpty()==false)
        {
            String content=news_headline;
            if(news_source.isEmpty()==false)
            {
                content=news_source+":"+news_headline;
            }
            new MaterialDialog.Builder(this)
                    .title("Based on this news reference")
                    .content(content)
                    .cancelable(false)
                    .positiveText("Go to news site (Popup in App)")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            showNewsSite();

                        }
                    })
                    .negativeText("Okay Got it")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                        }
                    })
                    .show();
        }
        else
        {
            Log.i("UserTopPanel","has_news_reference="+has_news_reference
                    +" conversation_id.isEmpty()="+conversation_id.isEmpty());
        }
    }

    WebView webView;
    private void showNewsSite()
    {

        MaterialDialog dialog1=new MaterialDialog.Builder(LetsTalkManageEach.this)
                .title(news_headline)
                .customView(R.layout.dialog_webview_readnews,
                        false)
                .cancelable(false)
                .positiveText("Okay read it")
                .negativeText("Reload Page")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {

                        showNewsSite();

                    }
                })
                .build();

        webView = (WebView) dialog1.getCustomView()
                .findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(news_sourcelink);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);

        dialog1.show();

    }

    /*
    End of Getting Conversation
    L1:https://todoist.com/showTask?id=6202994199&sync_id=6202994199
    Documentation: C:\Users\HP\Desktop\Bookie\Lets Talk Coding Plan.doc
     */

    //End of Getting Conversation

    //Start of Sending Comment

    CommentCreator commentCreator;
    //L5.1 Send comment to server – Typing Area
    @BindView(R.id.emojicon_edit_text)
    EmojiconEditText emojicon_edit_text;
    FirebaseUser firebaseUser;
    @OnClick(R.id.send_button)
    void startSendComment()
    {

        commentCreator=new CommentCreator(this,
                current_Topic_for_convo,bookieActivity);
        commentCreator.setHead_comment(head_comment);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(emojicon_edit_text.getText().toString().isEmpty()==false)
        {
            //15.1.1:Check if the user is signed in – if false then 15.1.2
            if(firebaseUser!=null)
            {
                //check if there is any internet connection
                if(NetworkUtils.isConnectedNetwork(this))
                {
                    Snackbar.make(rootview,"No internet Connection"
                            ,Snackbar.LENGTH_SHORT).show();
                    //send comment
                    commentCreator.setHead_comment(head_comment);
                    commentCreator.setReplying_comment(replying_comment);
                    commentCreator.startSendComment();

                }
                else
                {
                    //send comment
                    commentCreator.setHead_comment(head_comment);
                    commentCreator.setReplying_comment(replying_comment);
                    commentCreator.startSendComment();


                }
            }
            else
            {
                SignInViaGoogle();
            }
        }
        else
        {

            Snackbar.make(rootview,"Oops you said nothing",Snackbar.LENGTH_SHORT).show();

        }

    }

    /*
    Dialog to decide whether the comment is a “new topic” or
    “under the current topic” then execute L5.3.1
     */
    Comment head_comment;
    Comment replying_comment;
    @BindView(R.id.typing_area)
    LinearLayout typing_area;
    @Override
    public void foundHeadComment(Comment head_comment) {

        this.head_comment=head_comment;
        if(this.head_comment!=null)
        {
            Log.i("foundHeadComment","from Activity "+head_comment.getComment_id());
            typing_area.setVisibility(View.VISIBLE);
        }
        else
        {
            typing_area.setVisibility(View.GONE);
        }

    }

    private void SignInViaGoogle()
    {

        signInGoogleComplete();

    }

    MaterialDialog please_wait;
    private static final int RC_SIGN_IN = 101;
    GoogleSignInClient  mGoogleSignInClient;
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
                            firebaseAuthWithGoogle(account.getIdToken());

                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.i("onActivityResult", "Google sign in failed", e);
                        }

                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken)
    {


        mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("onActivityResult", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent=new Intent(LetsTalkManageEach.this,
                                    CaptureInfo1.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("onActivityResult", "signInWithCredential:failure", task.getException());

                        }
                    }
                });

    }

    private void signInGoogleComplete()
    {

        please_wait=new MaterialDialog.Builder(this)
                .title("Please wait")
                .content("Please wait while we connect to Google SignIn")
                .cancelable(false)
                .show();

        please_wait.show();
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestIdToken(getString(R.string.google_client_id))
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null)
        {
            mGoogleSignInClient.signOut();
        }

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        authLauncher.launch(signInIntent);


    }

    //End of Sending Comment

    @Override
    public void onBackPressed() {
        if(webView!=null)
        {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //start worker to check internet connection every 10 minutes if there is no internet connection
        //or to notify the user there is an internet connection and the last viewed convo
        //use the Memory class to record the last convo visited.


    }

    @Override
    public void sendCommentWithNewsReference(MediaItemWebSearch mediaItemWebSearch,
                                             Comment head_comment) {

    }

    @Override
    public void onReply(Comment comment) {
        replying_comment=comment;
        Log.i("onReply",replying_comment.getComment_id()
                +" "+replying_comment.getCommentator_name());
        if(comment.getComment().isEmpty()==false)
        {
            showQuotedMessage();
        }
        else
        {

            Memory memory=new Memory(LetsTalkManageEach.this);
            String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

            if(jnm.isEmpty()==false)
            {

                long localtimeoffset=Long.parseLong(jnm);
                org.joda.time.LocalDateTime localDateTime
                        =new org.joda.time.LocalDateTime(localtimeoffset);

                String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
                com.google.android.exoplayer2.util.Log.i("tinsja","timelabel="+timelabel);
                Toast.makeText(LetsTalkManageEach.this,"Tap for comments for "+timelabel
                        ,Toast.LENGTH_LONG).show();
            }


        }
    }

    @BindView(R.id.replied_area)
    LinearLayout replied_area;
    @BindView(R.id.agree_disagree_flag)
    FrameLayout agree_disagree_flag;
    @BindView(R.id.comment_type_commentating)
    TextView comment_type_commentating;
    @BindView(R.id.replied_comment_name)
    TextView replied_comment_name;
    @BindView(R.id.qouted_comment)
    ExpandableTextView qouted_comment;
    private void showQuotedMessage()
    {

        replied_area.setVisibility(View.VISIBLE);

        //shake animation
        replied_area.startAnimation(AnimationUtils.loadAnimation(LetsTalkManageEach.this
                , R.anim.shake));

        emojicon_edit_text.requestFocus();

        if(replying_comment.getComment_type()==Comment.AGREES)
        {
            agree_disagree_flag.setBackgroundColor(getResources().getColor(R.color.purple_700));
            comment_type_commentating.setTextColor(getResources().getColor(R.color.purple_700));
            comment_type_commentating.setText("Agrees");
        }
        if(replying_comment.getComment_type()==Comment.DISAGREES)
        {
            agree_disagree_flag.setBackgroundColor(getResources().getColor(R.color.red));
            comment_type_commentating.setTextColor(getResources().getColor(R.color.red));
            comment_type_commentating.setText("Disagrees");
        }
        if(replying_comment.getComment_type()==Comment.QUESTION)
        {
            agree_disagree_flag.setBackgroundColor(getResources().getColor(R.color.blue));
            comment_type_commentating.setTextColor(getResources().getColor(R.color.blue));
            comment_type_commentating.setText("Question");
        }

        replied_comment_name.setText("Replying to "+replying_comment.getCommentator_name());
        qouted_comment.setText(replying_comment.getComment());


    }
    @OnClick(R.id.cancel_button_reply)
    void CancelReply()
    {
        replied_area.setVisibility(View.GONE);
        replying_comment=null;
    }

    @Override
    public void onImageComment(NewsFactsMedia newsFactsMedia) {

    }

    @Override
    public void onVideoComment(NewsFactsMedia newsFactsMedia) {

    }

    @Override
    public void GetCommentsUnderTimestamp(Comment comment, int position) {

    }

    @Override
    public void GetCommentsUnderTimestampBeforeComment(Comment timestamp_comment,
                                                       int position,
                                                       Comment after_comment)
    {



    }

    @Override
    public void GetCommentsUnderTimestampAfterComment(Comment timestamp_comment,
                                                      int position, Comment after_comment) {

    }

    @Override
    public void OnShareDayConvo(Comment start_comment) {

    }

    @Override
    public void OnCancelReply() {
        CancelReply();

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

    }

    @Override
    public void LeaveConvo() {
        this.finish();
    }

    @Override
    public void messageUpdated() {
        emojicon_edit_text.setText("");
    }

    @Override
    public void onFragmentCreated(Fragment currentTopicForConvo) {

        CurrentTopicForConvo currentTopicForConvo1=(CurrentTopicForConvo) currentTopicForConvo;
        if(currentTopicForConvo1!=null)
        {
            checkCommentListKeyBoard(currentTopicForConvo);
        }
        else
        {
            Log.i("chckCmmntLstKyBard","currentTopicForConvo1==null");
        }

    }


    private void monitorConnect()
    {


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.google.com";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.i("monitorConnect","response ");
                        if(timeoffset_running==false)
                        {
                            Log.i("monitorConnect","timeoffset_running="+timeoffset_running);
                            getServerTimeOffset();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("monitorConnect","error "+OPERATION);
                if(OPERATION==GETTING_TIME_OFFSET)
                {
                    tellUserNoConnectionTimeOffset();
                }

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    private void tellUserNoConnectionTimeOffset()
    {

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        if(NetworkUtils.isConnectedNetwork(LetsTalkManageEach.this))
        {
            please_wait=new MaterialDialog.Builder(LetsTalkManageEach.this)
                    .title("Error connecting")
                    .content("Please check your data balance or check network connection")
                    .positiveText("Try again")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            getServerTimeOffset();

                        }
                    })
                    .negativeText("Try in Browser")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.google.com"));
                            startActivity(browserIntent);

                        }
                    })
                    .cancelable(false)
                    .show();
        }
        else
        {
            please_wait=new MaterialDialog.Builder(LetsTalkManageEach.this)
                    .title("No Connection")
                    .content("Please switch on data or connect to a wifi")
                    .positiveText("Try again")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            getServerTimeOffset();

                        }
                    })
                    .negativeText("Go to Settings")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                        }
                    })
                    .cancelable(false)
                    .show();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(last_reading_pos>0&comment_list!=null)
        {

            final int jk=last_reading_pos;
            Log.i("hdusaga","xlast_reading_pos="+jk);
            comment_list.scrollToPosition(last_reading_pos);
            comment_list.postDelayed(new Runnable() {
                @Override
                public void run() {

                    comment_list.smoothScrollToPosition(jk);
                    Log.i("hdusaga","xlast_reading_pos="+jk);


                }
            },1000);
        }

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
            View view= LayoutInflater.from(context).inflate(
                    R.layout.view_simple_custom_bottom_sheet,
                    this,
                    false
            );

            return view;
        }

    }



}