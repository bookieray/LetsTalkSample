package brainwind.letstalksample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.scwang.wave.MultiWaveHeader;

import java.util.HashMap;
import java.util.Map;

import brainwind.letstalksample.bookie_activity.BookieActivity;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDao;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.features.letstalk.LetsTalkManageEach;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.fragments.letstalk.convo_threads.ConvoThreads;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.Conversation;
import brainwind.letstalksample.fragments.letstalk.current_topic.CurrentTopic;
import brainwind.letstalksample.fragments.suggestions.FragmentSuggestionsForLetsTalk;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class MainActivity extends AppCompatActivity implements CommentListener {

    public static final String MOVE_TO_ACTIVITIES = "move_activities";
    //The rootView
    @BindView(R.id.rootview)
    RelativeLayout rootview;

    //Handle if the conversation is a standalone conversation
    // or around another Bookie activity
    boolean is_standalone=false;
    String activity_id="";
    String conversation_id="";

    //The head
    @BindView(R.id.back_but)
    FloatingActionButton back_but;
    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.waveHeader)
    MultiWaveHeader waveHeader;

    //Reference
    @BindView(R.id.media_attachment)
    ImageView media_attachment;
    @BindView(R.id.reference_title)
    TextView reference_title;
    @BindView(R.id.reference_label)
    TextView reference_label;
    @BindView(R.id.reference_source)
    TextView reference_source;
    @BindView(R.id.reference_type)
    TextView reference_type;
    @BindView(R.id.read_more_reference)
    TextView read_more_reference;
    //if it is a video
    @BindView(R.id.play_button_area)
    FrameLayout play_button_area;

    //The Content Area
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.content_pager)
    ViewPager2 content_pager;
    private PageAdapter pagerAdapter;

    //The Typing Area
    @BindView(R.id.typing_area)
    LinearLayout typing_area;
    @BindView(R.id.next_item)
    FloatingActionButton next_item;
    //@BindView(R.id.typing_comment_type)
    //Spinner typing_comment_type;
    @BindView(R.id.attach_media_button)
    ImageView attach_media_button;
    @BindView(R.id.emojicon_edit_text)
    EmojiconEditText emojicon_edit_text;
    @BindView(R.id.emoji_button)
    ImageView emoji_button;
    @BindView(R.id.send_button)
    FloatingActionButton send_button;
    EmojIconActions  emojIcon;

    //Authentication
    private FirebaseUser firebaseUser;
    private UserInfo userInfo;
    private static final int RC_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private MaterialDialog please_wait;
    private BookieActivity bookieActivity;
    private Conversation conversation;
    //The head comment
    Comment head_comment;
    private CurrentTopic current_Topic;
    //The LinearLayout that handles the news event or comment that the user
    //is commentating on
    @BindView(R.id.news_suggestion_or_comment)
    LinearLayout news_suggestion_or_comment;
    @BindView(R.id.reference_title_news)
    TextView reference_title_news;
    @BindView(R.id.reference_source_news)
    TextView reference_source_news;
    //Pending comment
    Comment pending_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        getDynamicLink();


    }

    private void getDynamicLink()
    {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.i("testDynLink","link received "+deepLink.toString());

                            Intent intent=new Intent();
                            boolean is_convo=false;
                            int set=1;
                            for(String query_name:deepLink.getQueryParameterNames())
                            {

                                Log.i("testDynLink","query_name="+query_name);
                                Log.i("testDynLink","query_value="+deepLink.getQueryParameter(query_name));

                                //OrgFields.YEAR="";

                                if(query_name.equals("convo_id"))
                                {
                                    is_convo=true;
                                    intent=new Intent(MainActivity.this, LetsTalkManageEach.class);
                                    intent.putExtra(OrgFields.CONVERSATION_ID,deepLink.getQueryParameter(query_name));

                                }
                                if(query_name.equals("stc"))
                                {
                                    Log.i("shareDayConvo",deepLink.getQueryParameter(query_name));
                                    intent.putExtra(CurrentTopicForConvo.STARTING_SIGNATURE,deepLink.getQueryParameter(query_name));
                                }
                                if(query_name.equals("is_st"))
                                {
                                    boolean is_st=Boolean.parseBoolean(deepLink.getQueryParameter(query_name));
                                    intent.putExtra(OrgFields.IS_STANDALONE_CONVO,is_st);
                                }
                                if(query_name.equals("set"))
                                {
                                    try {
                                        set=Integer.parseInt(deepLink.getQueryParameter(query_name));
                                        intent.putExtra(CurrentTopicForConvo.SET,set);
                                    }
                                    catch(Exception exception)
                                    {

                                    }
                                }
                                if(query_name.equals("nm"))
                                {
                                    boolean nm=Boolean.parseBoolean(deepLink.getQueryParameter(query_name));
                                    intent.putExtra(OrgFields.NO_MORE_PREV_COMMENTS,nm);
                                }
                                if(query_name.equals("t"))
                                {
                                    long t=Long.parseLong(deepLink.getQueryParameter(query_name));
                                    intent.putExtra(CurrentTopicForConvo.TSECONDS,t);
                                }


                            }

                            if(is_convo)
                            {
                                startActivity(intent);
                            }



                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("testDynLink", "getDynamicLink:onFailure", e);
                    }
                });

    }




    private void handleConversationAgenda()
    {

        if(getIntent().hasExtra(OrgFields.ACTIVITY_ID))
        {
            is_standalone=false;
            activity_id=getIntent().getStringExtra(OrgFields.ACTIVITY_ID);
            //gets the activity that this conversation revolves around

        }
        else
        {
            is_standalone=true;
            conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);
            //gets for now the sample conversation
            getSampleConvo();
        }



    }

    private void checkIfSignedIn()
    {

        if(firebaseUser==null)
        {

            please_wait=new MaterialDialog.Builder(this)
                    .title("Please wait")
                    .content("Please we sign you in")
                    .cancelable(false)
                    .show();

            signInGoogleComplete();


        }
        else
        {

            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {


                            UserInfoDao userInfoDao =UserInfoDatabase.getDatabase(MainActivity.this)
                                    .userInfoDao();

                            UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                            if(userInfo==null)
                            {

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent intent=new Intent(MainActivity.this, CaptureInfo1.class);
                                        startActivity(intent);

                                    }
                                });

                            }
                            else
                            {

                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        getSampleConvo();

                                    }
                                });

                            }


                        }
                    });

        }


    }

    //the traditional intent sign in
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
        startActivityForResult(signInIntent, RC_SIGN_IN);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }

        switch (requestCode)
        {

            case RC_SIGN_IN:

                //If the sign attempt is successful from the Google Account Authentication system
                if(resultCode== GoogleSignInStatusCodes.SUCCESS)
                {
                    please_wait=new MaterialDialog.Builder(this)
                            .title("Please wait signing you in")
                            .content("Signing into Bookie")
                            .negativeText("Cancel")
                            .cancelable(false)
                            .show();
                }
                else {

                }

                // The Task returned from this call is always completed, no need to attach
                // a listener.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);

                break;

        }

    }

    //methods that retrieve credentials and authenticate with Firebase
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInGoogleComplete", "signInWithCredential:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(please_wait!=null)
                                {
                                    please_wait.dismiss();
                                }
                                nextAfterSignIn();

                            } else {
                                // If sign in fails, display a message to the user.
                                if(please_wait!=null)
                                {
                                    please_wait.dismiss();
                                }
                                Log.i("signInGoogleComplete", "signInWithCredential:failure",
                                        task.getException());

                            }
                        }
                    });

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("signInGoogleComplete", "signInResult:failed code=" + e.getStatusCode()
                    +" "+e.getStatus().getStatusMessage());
            if(please_wait!=null)
            {
                please_wait.dismiss();
            }
            reTryGoogleSignIn(e.getLocalizedMessage());

        }
    }

    private void handleSignInResult(AuthCredential firebaseCredential) {
        try {


            FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("signInGoogleComplete", "signInWithCredential:success");
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(please_wait!=null)
                                {
                                    please_wait.dismiss();
                                }
                                nextAfterSignIn();

                            } else {
                                // If sign in fails, display a message to the user.
                                if(please_wait!=null)
                                {
                                    please_wait.dismiss();
                                }
                                Log.i("signInGoogleComplete", "signInWithCredential:failure",
                                        task.getException());

                            }
                        }
                    });

        } catch (Exception e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("signInGoogleComplete", "signInResult:failed errormsg=" + e.getMessage());
            if(please_wait!=null)
            {
                please_wait.dismiss();
            }
            reTryGoogleSignIn(e.getLocalizedMessage());

        }
    }

    private void reTryGoogleSignIn(String errormessage)
    {

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        new MaterialDialog.Builder(this)
                .title("Oops sorry something went wrong")
                .content(errormessage+", Try again?")
                .positiveText("Try again")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        signInGoogleComplete();

                    }
                })
                .neutralText("Cancel")
                .cancelable(false)
                .show();

    }

    //The next step after signing in
    private void nextAfterSignIn()
    {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(please_wait!=null)
        {
            please_wait.dismiss();
        }
        //check if the local user information is there


    }


    private void getSampleConvo()
    {

        //get the sample activity
        //S3aNh6Jq7ZajBiJS2jut
        //sample conversation

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(please_wait!=null)
        {
            please_wait.dismiss();
        }

        please_wait=new MaterialDialog.Builder(MainActivity.this)
                .title("Please wait")
                .content("Please wait while we download the convo")
                .cancelable(false)
                .show();

        String doc_id="";
        if(getIntent().hasExtra(OrgFields.ACTIVITY_ID))
        {
            is_standalone=false;
            activity_id=getIntent().getStringExtra(OrgFields.ACTIVITY_ID);
            doc_id=activity_id;
            //gets the activity that this conversation revolves around

        }
        else if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
        {
            is_standalone=true;
            conversation_id=getIntent().getStringExtra(OrgFields.CONVERSATION_ID);
            doc_id=conversation_id;
            //gets for now the sample conversation
        }
        else
        {
            doc_id="S3aNh6Jq7ZajBiJS2jut";
        }

        CloudWorker.getActivities()
                .document(doc_id)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(please_wait!=null)
                        {
                            please_wait.dismiss();
                        }
                        please_wait=new MaterialDialog.Builder(MainActivity.this)
                                .title("Oops, there has been an error")
                                .content("error="+e.getMessage())
                                .positiveText("Try again")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        getSampleConvo();

                                    }
                                })
                                .cancelable(false)
                                .show();

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(please_wait!=null)
                        {
                            please_wait.dismiss();
                        }

                        if(getIntent().hasExtra(OrgFields.ACTIVITY_ID))
                        {
                            activity_id=documentSnapshot.getId();
                            bookieActivity=new BookieActivity(documentSnapshot);
                        }
                        else if(getIntent().hasExtra(OrgFields.CONVERSATION_ID))
                        {
                            conversation_id=documentSnapshot.getId();
                            conversation=new Conversation(documentSnapshot);
                        }
                        else
                        {
                            conversation_id=documentSnapshot.getId();
                            conversation=new Conversation(documentSnapshot);
                        }

                        showConvo(documentSnapshot);
                        setUpViewPager(documentSnapshot);


                    }
                });

        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(MainActivity.this)
                                .userInfoDao();

                        userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                        if(userInfo!=null)
                        {



                        }
                        else
                        {

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    NoUserRecord();

                                }
                            });

                        }

                    }
                });

    }

    private void setUpViewPager(DocumentSnapshot documentSnapshot)
    {

        //Set Up ViewPager
        if(content_pager.getAdapter()==null)
        {

            pagerAdapter = new PageAdapter(this);
            content_pager.setAdapter(pagerAdapter);

            //align tabs with the view pager
            tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    content_pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }

    }

    private void NoUserRecord()
    {

        if(please_wait!=null)
        {
            please_wait.dismiss();
        }

        please_wait=new MaterialDialog.Builder(MainActivity.this)
                .title("You need to fill in your details")
                .content("We are missing your names and country, please do not delete app data or check device storage.")
                .cancelable(false)
                .positiveText("Okay")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Intent intent=new Intent(MainActivity.this,
                                CaptureInfo1.class);
                        startActivity(intent);

                    }
                })
                .show();

    }

    private void showConvo(DocumentSnapshot documentSnapshot)
    {

        if(documentSnapshot!=null)
        {

            conversation=new Conversation(documentSnapshot);
            label.setText(conversation.getActivity_title());
            if(conversation.isIs_news_reference()==false)
            {
                read_more_reference.setVisibility(View.GONE);
            }

            waveHeader.setStartColor(Color.parseColor(conversation.getOrg_main_color()));
            waveHeader.setCloseColor(Color.parseColor(conversation.getOrg_main_color()));

            //Media Thumbnail
            if(conversation.isHas_attached_media())
            {

                if(conversation.isIs_attach_media_image())
                {

                    Glide.with(MainActivity.this)
                         .load(Uri.parse(conversation.getMedia_url()))
                         .placeholder(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                         .error(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                         .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    reTryLoadingAttachImage();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                         .into(media_attachment);

                }
                else if(conversation.isIs_attach_media_video()||conversation.isIs_attach_media_audio())
                {

                    play_button_area.setVisibility(View.VISIBLE);
                    if(conversation.isIs_attach_media_video())
                    {

                        Glide.with(MainActivity.this)
                                .load(Uri.parse(conversation.getVideo_thumbnail()))
                                .placeholder(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                                .error(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                                .addListener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        reTryLoadingAttachImage();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(media_attachment);

                    }
                    else
                    {



                    }

                }

            }
            else
            {

                //load some image from the web to assist


            }

            //The News/Activity Reference
            if(conversation.isIs_news_reference())
            {
                reference_title.setText(conversation.getNews_headline());
                reference_source.setText(conversation.getNews_source().toUpperCase());
                if(conversation.isIs_news_reference())
                {
                    reference_type.setText("News");
                }
                else
                {
                    reference_type.setText("Activity");
                }
            }
            else if(conversation.isIs_activity_reference())
            {

                reference_title.setText("");
                reference_source.setText("");
                getActivity(conversation.getActivity_reference());
                reference_type.setText("Activity");

            }

            if(is_standalone)
            {
                reference_label.setText("Based on News and Media");
            }
            else
            {
                reference_label.setText("Based on Activity");
            }



        }


    }

    private void getActivity(String activity_reference)
    {

        CloudWorker.getActivities()
                .document(activity_reference)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                        reTryLoadingReferenceActivity(e);
                        
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        BookieActivity bookieActivity=new BookieActivity(documentSnapshot);
                        showReferenceActivity(bookieActivity);
                        
                    }
                });

    }

    private void reTryLoadingReferenceActivity(Exception e)
    {



    }

    private void showReferenceActivity(BookieActivity bookieActivity)
    {

        reference_title.setText(bookieActivity.getActivity_title());
        reference_source.setText(bookieActivity.getOrgname());

        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_SURVEY))
        {
            reference_type.setText("Survey");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_ANNOUNCEMENT))
        {
            reference_type.setText("Announcement");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_ARTICLE))
        {
            reference_type.setText("Article");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_EVENT))
        {
            reference_type.setText("Event");
        }

        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_HAVE_YOUR_SAY))
        {
            reference_type.setText("Have Your Say");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_INTEREST))
        {
            reference_type.setText("Interest");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_PARTNERSHIP))
        {
            reference_type.setText("Partnership");
        }
        if(bookieActivity.getActivity_type().trim().equals(OrgFields.ACTIVITY_TYPE_PROJECT))
        {
            reference_type.setText("Project");
        }

        //Show the Activity's Reference
        if(bookieActivity.isHas_attached_media())
        {

            if(bookieActivity.isIs_attach_media_image())
            {

                Glide.with(MainActivity.this)
                        .load(Uri.parse(bookieActivity.getMedia_url()))
                        .placeholder(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                        .error(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                reTryLoadingAttachImage();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(media_attachment);

            }
            else if(bookieActivity.isIs_attach_media_video()||bookieActivity.isIs_attach_media_audio())
            {

                play_button_area.setVisibility(View.VISIBLE);
                if(bookieActivity.isIs_attach_media_video())
                {

                    Glide.with(MainActivity.this)
                            .load(Uri.parse(bookieActivity.getVideo_thumbnail()))
                            .placeholder(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                            .error(MainActivity.this.getResources().getDrawable(R.drawable.image_placeholder))
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    reTryLoadingAttachImage();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(media_attachment);

                }
                else
                {



                }

            }

        }
        else
        {

            //load some image from the web to assist


        }



    }

    private void reTryLoadingAttachImage()
    {



    }

    @Override
    public void foundHeadComment(Comment head_comment)
    {

        this.head_comment=head_comment;
        if(this.current_Topic!=null)
        {
            this.current_Topic.setHead_comment(head_comment);
        }

    }

    //usually from news suggestion adapter the user
    //used to initiate a new comment or qoute or share the comment
    @Override
    public void sendCommentWithNewsReference(MediaItemWebSearch mediaItemWebSearch,Comment head_comment)
    {

        if(firebaseUser!=null&userInfo!=null)
        {


            this.head_comment=head_comment;
            //handle if the user selects to comment on the news event
            if(mediaItemWebSearch!=null)
            {
                news_suggestion_or_comment.setVisibility(View.VISIBLE);
                reference_title_news.setText(mediaItemWebSearch.getHeading());
                reference_source_news.setText(mediaItemWebSearch.getSource());


            }
            else{
                news_suggestion_or_comment.setVisibility(View.GONE);
            }


        }
        if(firebaseUser!=null)
        {

            if(head_comment!=null)
            {

            }
            else
            {

            }

        }

    }

    @Override
    public void onReply(Comment comment) {

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

    private void sendCommentQoutePrepare(Comment comment)
    {

        if(head_comment!=null)
        {

            new MaterialDialog.Builder(this)
                    .title("Just to be organized")
                    .content("A new Topic/comment or in response to the current head comment")
                    .cancelable(false)
                    .positiveText("New Topic")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            comment.setIs_new_topic(false);
                            comment.setHead_comment_id("");
                            sendCommentQoute(comment);

                        }
                    })
                    .negativeText("Response to the head comment")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            sendCommentQoute(comment);

                        }
                    })
                    .show();

        }
        else
        {

            

        }


    }

    private void sendCommentQoute(Comment comment)
    {



    }

    private class PageAdapter extends FragmentStateAdapter {
        public PageAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            if(position==0)
            {

                if(is_standalone)
                {
                    current_Topic=CurrentTopic.newInstance(conversation_id,conversation.getActivity_title());
                    return current_Topic;
                }
                else
                {
                    current_Topic=CurrentTopic.newInstance(activity_id,bookieActivity.getActivity_title()
                            ,true);
                    return current_Topic;
                }

            }
            else if(position==1)
            {
                return ConvoThreads.newInstance("",true);
            }
            else
            {
                return FragmentSuggestionsForLetsTalk.newInstance("","");
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser==null)
        {
            //signInGoogleComplete();
        }

    }

    @OnClick(R.id.title)
    void LearnMoreClicked()
    {

        if(conversation!=null)
        {
            new MaterialDialog.Builder(this)
                    .title("More about this conversation")
                    .content(conversation.getActivity_desc())
                    .cancelable(false)
                    .positiveText("Got it")
                    .show();

        }

    }
    @OnClick(R.id.play_button_area)
    void PlayVideoAudio()
    {



    }
    @OnClick(R.id.read_more_reference)
    void ReadMoreReference()
    {

        if(conversation!=null)
        {

            if(conversation.isIs_news_reference())
            {

                MaterialDialog dialog=new MaterialDialog.Builder(MainActivity.this)
                        .title("Read More")
                        .customView(R.layout.dialog_webview_readnews,false)
                        .cancelable(false)
                        .positiveText("Okay Read it")
                        .build();

                WebView webview=dialog.getCustomView().findViewById(R.id.webview);
                webview.loadUrl(conversation.getNews_sourcelink());

                dialog.show();

            }

        }

    }

    @OnClick(R.id.send_button)
    void sendComment()
    {

        if(userInfo!=null)
        {

            if(head_comment!=null)
            {
                new MaterialDialog.Builder(this)
                        .title("Just to confirm")
                        .content(Html.fromHtml("Please confirm whether this is in response to the <b>head comment " +
                                "as the topic</b> or you are bringing a <b>new topic</b>"))
                        .positiveText("In response to head comment")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                sendComToCloud(true);

                            }
                        })
                        .negativeText("Raising a new Topic")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                sendComToCloud(false);

                            }
                        })
                        .cancelable(false)
                        .show();
            }
            else
            {
                sendComToCloud(true);
            }


        }
        else
        {

            NoUserRecord();

        }

    }

    private void sendComToCloud(boolean new_topic)
    {

        Map<String,Object> values=new HashMap<String,Object>();
        String comment=emojicon_edit_text.getText().toString();
        values.put(OrgFields.COMMENT,comment);
        if(this.is_standalone)
        {
            values.put(OrgFields.IS_STANDALONE_CONVO,true);
            values.put(OrgFields.IS_ACTIVITY_BASED,false);
            values.put(OrgFields.CONVERSATION_ID,conversation_id);
            values.put(OrgFields.ACTIVITY_ID,"");
        }
        else
        {
            values.put(OrgFields.IS_STANDALONE_CONVO,false);
            values.put(OrgFields.IS_ACTIVITY_BASED,true);
            values.put(OrgFields.ACTIVITY_ID,activity_id);
            values.put(OrgFields.CONVERSATION_ID,"");
        }
        values.put(OrgFields.COMMENTATER_NAME,userInfo.firstname+" "+userInfo.lastname);
        values.put(OrgFields.USER_UID,firebaseUser.getUid());
        //values.put(OrgFields.COMMENT_TYPE,typing_comment_type.getSelectedItem().toString());
        if(new_topic)
        {
            values.put(OrgFields.COMMENTER_PROFILE_IMAGE,userInfo.profile_path);
        }
        //the date
        values.put(OrgFields.USER_CREATED_DATE, FieldValue.serverTimestamp());
        values.put(OrgFields.USER_MODIFIED_DATE, FieldValue.serverTimestamp());
        //The number of comments
        values.put(OrgFields.NUM_COMMENTS,0);
        values.put(OrgFields.IS_NEW_TOPIC,new_topic);

        CloudWorker.getLetsTalk()
                .add(values)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("send_com_cloud","e1="+e.getMessage());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        documentReference
                                .get()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.i("send_com_cloud","e2="+e.getMessage());

                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Comment head_com=new Comment(documentSnapshot);
                                        current_Topic.setHead_comment(head_com);
                                        Log.i("send_com_cloud","successful");

                                    }
                                });

                    }
                });


    }

    private void listenForDynamicLinks()
    {

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.i("lsdynic",deepLink.toString());
                            for(String key:deepLink.getQueryParameterNames())
                            {
                                Log.i("lsdynic",key);
                            }

                        }
                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...
                        // ...

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("lsdynic", "getDynamicLink:onFailure", e);
                    }
                });

    }


}