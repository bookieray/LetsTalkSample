package brainwind.letstalksample.fragments.letstalk.current_topic;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hbb20.CountryCodePicker;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import brainwind.letstalksample.CaptureInfo1;
import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.SwipeControllerActions;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDao;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.MediaItemWebSearch;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import brainwind.letstalksample.utils.AndroidUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * This fragment will track the current's conversation's comments
 */
public class CurrentTopic extends Fragment {

    // The Conversation
    // these parameters will signal the conversation ID
    private static final String ACTIVITY_ID = "activity_id";
    private static final String CONVERSATION_ID = "con_id";
    private static final String CONVO_TITLE = "con_title";
    //the id for the conversation
    //or the id for the activity
    private String activity_id="";
    private String conversation_id="";
    //booleans
    private boolean is_activity_based=false;
    private boolean is_standalone=false;

    //The Head Comment View
    @BindView(R.id.head_comment_view)
    LinearLayout head_comment_view;
    @BindView(R.id.head_profile_image)
    CircleImageView head_profile_image;
    @BindView(R.id.head_comment_name)
    TextView head_comment_name;
    @BindView(R.id.head_comment)
    ExpandableTextView head_comment_textView;
    @BindView(R.id.comment_type)
    TextView comment_type;

    @BindView(R.id.num_comments_view)
    TextView num_comments_view;


    //The commentary
    @BindView(R.id.comment_list)
    RecyclerView comment_list;
    CommentAdapter commentAdapter;
    //The head comment
    Comment head_comment;

    //The suggestions
    MediaItemAdapter mediaItemAdapter;
    List<MediaItemWebSearch> mediaItemWebSearchList=new ArrayList<MediaItemWebSearch>();
    private String convo_title="";

    private boolean sendNewsEventAfterCommentCreate=false;
    private FirebaseFirestore db;
    private MaterialDialog please_wait;
    //The firebase User
    FirebaseUser firebaseUser;


    private boolean finished_fecth_head_comment=true;
    private boolean is_sharing=false;
    private MediaItemWebSearch shared_news_event;
    private boolean is_qoute=false;
    private List<Comment> comment_listx=new ArrayList<Comment>();
    private Comment previous_comment;
    private TopicSuggestion topicSuggestion;
    private boolean finished_fecth_prevcomment=false;

    ItemTouchHelper itemTouchHelper;

    //boolean to signal which task failed
    boolean failed_to_load_head_comment=false;
    boolean failed_to_load_comments=false;
    private Comment last_comment_date;
    private boolean comment_fetch_inprogress=false;

    public CurrentTopic() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param conversation_id Parameter 1.
     * @return A new instance of fragment CurrentTopic.
     */
    // If this is standalone conversation
    @NonNull
    public static CurrentTopic newInstance(String conversation_id,String title) {
        CurrentTopic fragment = new CurrentTopic();
        Bundle args = new Bundle();
        args.putString(CONVERSATION_ID, conversation_id);
        args.putString(CONVO_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    public static CurrentTopic newInstance(String activity_id,String title,
                                           boolean is_activity_based) {
        CurrentTopic fragment = new CurrentTopic();
        Bundle args = new Bundle();
        args.putString(ACTIVITY_ID, activity_id);
        args.putString(CONVO_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (getArguments() != null) {
            if(getArguments().containsKey(CONVERSATION_ID))
            {
                conversation_id = getArguments().getString(CONVERSATION_ID);
            }
            else if(getArguments().containsKey(ACTIVITY_ID))
            {
                activity_id = getArguments().getString(ACTIVITY_ID);
            }
            convo_title=getArguments().getString(CONVO_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_current_topic, container, false);
        ButterKnife.bind(this,view);
        if(this.conversation_id.isEmpty())
        {
            is_standalone=false;
            if(this.activity_id.isEmpty()==false)
            {
                this.is_activity_based=true;
            }
            else
            {
                this.is_activity_based=false;
            }
        }
        else
        {
            is_standalone=true;
        }
        setUpCommentList();
        return view;
    }

    private void setUpCommentList()
    {

        comment_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentAdapter=new CommentAdapter();
        comment_list.setAdapter(commentAdapter);
        itemTouchHelper=new ItemTouchHelper(messageSwipeController);
        itemTouchHelper.attachToRecyclerView(comment_list);

    }

    int current_selection=0;
    @BindView(R.id.comment_filter)
    Spinner comment_filter;
    boolean isfiltering=false;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);

        head_comment_textView.setText("Kevin Samuels should be remembered for speaking the truth and ksjkjh skdkhs skhndkshd skhnfksdhk skdbnskbf sdkjbskbndf sdbksbf skdfks skjfbkshndfksb skfbskbdfks skfbksbf skbdfksbns mfbskbf");

        setUpCommentList();

        mediaItemAdapter=new MediaItemAdapter();
        //set the comment


        getHeadComment();
        comment_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.i("onItemSelected","comment_fetch_inprogress="+comment_fetch_inprogress+" "+current_selection);
                if(comment_fetch_inprogress&i==current_selection)
                {
                    Toast.makeText(getContext(),"Give me a minute",Toast.LENGTH_LONG).show();
                }
                else
                {
                    isfiltering=true;
                    getCommentsUnderHeadComment();
                    current_selection=i;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


    }
    
    private void alertNewComments(int size)
    {


        final Snackbar snackbar=Snackbar.make(rootview, "New recent comments",
                Snackbar.LENGTH_LONG).setAction("See them Now",
                null);
        snackbar.show();

        CountDownTimer start = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                long secs=l/1000;
                snackbar.setText("New recent comments "+secs+"");
                snackbar.show();
            }

            @Override
            public void onFinish() {

                for(int i=1;i<size;i++)
                {
                    commentAdapter.notifyItemInserted(0);
                }

            }
        }.start();


    }

    private void failedToGetHeadComment(Exception e)
    {



    }

    private void testComment(Comment comment)
    {

        Comment comment1=new Comment(comment);
        comment1.setComment("Hypertension usually affects people of that age");
        comment1.setComment_type(Comment.AGREES);
        commentListUnderHeadComment.add(comment1);
        commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);

        Comment comment2=new Comment(comment);
        comment2.setComment("He seemed very healthy so i don't believe it");
        comment2.setComment_type(Comment.DISAGREES);
        commentListUnderHeadComment.add(comment2);
        commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);

        Comment comment3=new Comment(comment);
        comment3.setComment("i don't believe it");
        comment3.setComment_type(Comment.DISAGREES);
        commentListUnderHeadComment.add(comment3);
        commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);

        Comment comment4=new Comment(comment);
        comment4.setComment("Hypertension can do that, I had a cousin about 60 died because of that, can happen");
        comment4.setComment_type(Comment.AGREES);
        commentListUnderHeadComment.add(comment4);
        commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);

        Comment comment5=new Comment(comment);
        comment5.setComment("What is a Hypertension?");
        comment5.setComment_type(Comment.QUESTION);
        commentListUnderHeadComment.add(comment5);
        commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);


    }

    private void getNewsSuggestion()
    {

        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        String xterm="";
                        if(head_comment!=null)
                        {
                            xterm=head_comment.getComment();
                            xterm=xterm.replace(head_comment.getNewsSource(),"");
                        }
                        else
                        {
                            xterm=convo_title;
                        }
                        final String term=xterm;
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getActivity()).userInfoDao();
                        UserInfo userInfo=null;
                        if(firebaseUser!=null)
                        {
                            userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());
                        }
                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        CountryCodePicker countryCodePicker=new CountryCodePicker(getActivity());
                        countryCodePicker.setAutoDetectedCountry(true);
                        if(userInfo!=null)
                        {
                            countryCodePicker.setCountryForPhoneCode(userInfo.country_code);
                        }
                        //News
                        String url = "https://www.google.com/search?q="+countryCodePicker.getSelectedCountryName()
                                +" "+term+"&tbm=nws";

                        Log.i("getNewsSuggestion","initiate");

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        Log.i("getNewsSuggestion","onResponse "
                                                +countryCodePicker.getSelectedCountryName()
                                                +" "+term);
                                        Document doc = Jsoup.parse(response);
                                        Elements body=doc.getElementsByTag("body");

                                        Log.i("getNewsSuggestion","body="+body.isEmpty());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {



                                            }
                                        });

                                        Elements headline_links=body.select("a:matches(([a-zA-Z(.+)]+\\s?\\b){4,})");
                                        Log.i("getNewsSuggestion","divs.isEmpty()="+headline_links.isEmpty()
                                                +" "+headline_links.size());

                                        for(int i=0;i<headline_links.size();i++)
                                        {

                                            Element head_element=headline_links.get(i);
                                            String headline=head_element.text();
                                            String source_link=head_element.attr("href");
                                            String source_name="";
                                            String timestamp="";

                                            boolean valid_gcard=false;
                                            if(head_element.hasParent())
                                            {
                                                if(head_element.parent().hasParent())
                                                {
                                                    if(head_element.parent().parent().hasParent())
                                                    {
                                                        valid_gcard=true;
                                                    }
                                                }
                                            }

                                            Log.i("getNewsSuggestion","valid_gcard="+valid_gcard);

                                            if(valid_gcard)
                                            {

                                                boolean nm=head_element.parent().parent().parent().tagName().trim().equals("g-card");

                                                Log.i("getNewsSuggestion","nm="+nm);
                                                if(nm)
                                                {


                                                    Log.i("getNewsSuggestion"," "+ (head_element.tagName())+" nm="+nm);
                                                    Log.i("getNewsSuggestion","headline="+headline);
                                                    Log.i("getNewsSuggestion","source_link="+source_link);
                                                    Elements spans=head_element.select("span:matches(([a-zA-Z(.+)]+\\s?\\b){1,6})");

                                                    Log.i("getNewsSuggestion","spans.isEmpty()="+spans.isEmpty());
                                                    if(spans.isEmpty())
                                                    {
                                                        Log.i("getNewsSuggestion","span empty i="+i);
                                                    }
                                                    //Determine the source name and timestamp
                                                    for(Element span : spans)
                                                    {
                                                        //test it is not a time stamp
                                                        Pattern p1 = Pattern.compile("^[0-9]{1,2}\\s\\D{3,7}\\s\\D{4,7}");
                                                        Pattern p2 = Pattern.compile("^[0-9]{1,2}\\s[A-Za-z]{1,9}\\s\\d{4,7}");
                                                        Pattern p3 = Pattern.compile("^[0-9]{1,2}\\s[A-Za-z]{1,9}\\sago");
                                                        Matcher m1 = p1.matcher(span.text());
                                                        Matcher m2 = p2.matcher(span.text());
                                                        Matcher m3 = p3.matcher(span.text());
                                                        boolean b = m1.matches()||m2.matches()||m3.matches();
                                                        if(b==false&source_name.isEmpty()&span.text().isEmpty()==false)
                                                        {
                                                            source_name=span.text();
                                                            Log.i("getNewsSuggestion","spans.text="+span.text()+"(this is the source)");
                                                        }
                                                        else if(b&span.text().isEmpty()==false)
                                                        {
                                                            timestamp=span.text();
                                                            Log.i("getNewsSuggestion","spans.text="+span.text()+"(this is the time stamp)");
                                                        }
                                                        else
                                                        {
                                                            Log.i("getNewsSuggestion",span.text()+" i="+i);

                                                        }



                                                    }


                                                    if(source_name.trim().isEmpty()==false)
                                                    {
                                                        MediaItemWebSearch mediaItemWebSearch=new MediaItemWebSearch();
                                                        mediaItemWebSearch.setSource(source_name);
                                                        mediaItemWebSearch.setHeading(headline);
                                                        mediaItemWebSearch.setLink(source_link);
                                                        mediaItemWebSearch.setNews(true);
                                                        mediaItemWebSearch.setTimeStamp(timestamp);

                                                        Suggestion suggestion=new Suggestion();
                                                        suggestion.setComment(false);
                                                        suggestion.setTitle(mediaItemWebSearch.getSource());
                                                        suggestion.setLabel("News");
                                                        suggestion.setDescription(mediaItemWebSearch.getHeading());

                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {


                                                                content_sugg_list.add(suggestion);
                                                                topicSuggestion.notifyItemInserted(content_sugg_list.size()-1);


                                                            }
                                                        });
                                                        break;
                                                    }
                                                    //Log.i("divs"," "+head_element.parent().outerHtml());
                                                }
                                                else
                                                {
                                                    Log.i("getNewsSuggestion","nm=false "+head_element.outerHtml());
                                                }


                                            }
                                            //handle the google valid traffic validation

                                        }



                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("getNewsSuggestion","onErrorResponse "+error.getMessage());



                            }
                        });



                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    }
                });

    }

    private void getPreviousHeadComment(DocumentSnapshot documentSnapshot)
    {



    }

    private void showPrevComment(Comment previous_comment)
    {



    }


    public void setHead_comment(Comment head_comment) {
        this.head_comment = head_comment;
        if(head_comment!=null)
        {


            this.head_comment_view.setVisibility(View.VISIBLE);
            head_comment_name.setText(head_comment.getCommentator_name());
            if(head_comment.getComment_type()==Comment.QOUTE)
            {
                comment_type.setText("Qouted");
                if(head_comment.getNewsSource().isEmpty()==false)
                {
                    comment_type.setText("Qouted from "+head_comment.getNewsSource());
                }
            }
            if(head_comment.getComment_type()==Comment.AGREES)
            {
                comment_type.setText("Agrees");
            }
            if(head_comment.getComment_type()==Comment.DISAGREES)
            {
                comment_type.setText("Disagrees");
            }
            if(head_comment.getComment_type()==Comment.QUESTION)
            {
                comment_type.setText("Question");
            }

            if(head_comment.getComment_type()==Comment.QOUTE)
            {

                head_comment_textView.setText(Html.fromHtml(
                        '"'+head_comment.getComment()+'"'));

            }
            else
            {
                head_comment_textView.setText(head_comment.getComment());
            }


            num_comments_view.setText(head_comment.getNum_comments()+" comments");

            Glide.with(getActivity()).clear(head_profile_image);
            Drawable placeholder= new AvatarGenerator.AvatarBuilder(getActivity())
                    .setLabel(head_comment.getCommentator_name())
                    .setAvatarSize(120)
                    .setTextSize(30)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();

            Glide.with(getActivity())
                    .load(head_comment.getCommentator_profile_path())
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(head_profile_image);

            commentListUnderHeadComment.clear();
            getCommentsUnderHeadComment();

        }
        else
        {
            commentListUnderHeadComment.clear();
            commentAdapter.notifyDataSetChanged();
            loadingComments();
        }
    }


    private void loadingComments()
    {



    }

    private void getHeadComment()
    {


        Query query=null;
        if(this.is_standalone)
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                    .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                    .limit(1);
        }
        else
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.ACTIVITY_ID,activity_id)
                    .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                    .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                    .limit(1);
        }

        commentListUnderHeadComment.clear();
        query.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("get_head_com","e="+e.getMessage());
                        head_comment_view.setVisibility(View.GONE);
                        failed_to_load_head_comment=true;
                        finished_fecth_head_comment=false;
                        failedToGetHeadComment(e);
                        startCountDownToNextRetry();


                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(finished_fecth_head_comment)
                        {
                            Log.i("get_head_com","successful isEmpty="+queryDocumentSnapshots.isEmpty());
                            if(queryDocumentSnapshots.isEmpty())
                            {
                                Log.i("get_head_com","conversation_id="+conversation_id
                                        +" is_standalone="+is_standalone);
                            }
                            CommentListener commentListener=(CommentListener) getActivity();
                            if(commentListener!=null)
                            {

                                if(queryDocumentSnapshots.isEmpty()==false)
                                {

                                    Comment comment=new Comment(queryDocumentSnapshots.getDocuments().get(0));
                                    commentListener.foundHeadComment(comment);
                                    //suggestions.setVisibility(View.GONE);
                                    setHead_comment(comment);
                                    head_comment=new Comment(queryDocumentSnapshots.getDocuments().get(0));
                                    Log.i("get_head_com","head comment_id="
                                            +head_comment.getComment_id());

                                    //Simultaneously get the content suggestions for another topic
                                    DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(0);
                                    if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
                                    {
                                        getPreviousHeadComment(documentSnapshot);
                                    }

                                    //getNewsSuggestion();
                                    //testComment(comment);


                                }
                                else
                                {
                                    noHeadComment();
                                    //getNewsSuggestion();
                                }

                            }
                            else
                            {
                                noHeadComment();
                                //getNewsSuggestion();
                            }
                        }


                    }
                });



    }

    HashMap<String,Integer> comments_already_notified_of=new HashMap<String,Integer>();
    List<Date> usdates=new ArrayList<Date>();
    List<Comment> agree_comment_list=new ArrayList<Comment>();
    List<Comment> disagree_comment_list=new ArrayList<Comment>();
    List<Comment> question_comment_list=new ArrayList<Comment>();
    private void getCommentsUnderHeadComment()
    {

        comment_fetch_inprogress=true;

        if(head_comment!=null)
        {

            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {

                            Log.i("getComsUnderHeComment","from getCommentsUnderHeadComment head_comment_id="
                                    +head_comment.getComment_id());
                            Query query=null;
                            if(comment_filter.getSelectedItemPosition()==0)
                            {
                                query=CloudWorker.getLetsTalkComments()
                                        .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                        .whereEqualTo(OrgFields.IS_NEW_TOPIC,false)
                                        .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING)
                                        .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                                        .limit(10);
                            }
                            else if(comment_filter.getSelectedItemPosition()==1)
                            {

                                if(agree_comment_list.size()<10)
                                {
                                    query=CloudWorker.getLetsTalkComments()
                                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                            .whereEqualTo(OrgFields.IS_NEW_TOPIC,false)
                                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.AGREES)
                                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING)
                                            .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                                            .limit(10);
                                }


                            }
                            else if(comment_filter.getSelectedItemPosition()==2)
                            {

                                if(disagree_comment_list.size()<10)
                                {
                                    query=CloudWorker.getLetsTalkComments()
                                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                            .whereEqualTo(OrgFields.IS_NEW_TOPIC,false)
                                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.DISAGREES)
                                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING)
                                            .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                                            .limit(10);
                                }

                            }
                            else if(comment_filter.getSelectedItemPosition()==3)
                            {
                                if(question_comment_list.size()<10)
                                {
                                    query=CloudWorker.getLetsTalkComments()
                                            .whereEqualTo(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id())
                                            .whereEqualTo(OrgFields.IS_NEW_TOPIC,false)
                                            .whereEqualTo(OrgFields.COMMENT_TYPE,Comment.QUESTION)
                                            .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.ASCENDING)
                                            .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                                            .limit(10);
                                }
                            }

                            //https://firebase.google.com/docs/firestore/query-data/listen?hl=en&authuser=0#events-local-changes
                            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException error) {

                                    comment_fetch_inprogress=false;

                                    if (error != null) {

                                        Log.i("listenForNewComments", "Listen failed.", error);
                                        Log.i("getComsUnderHeComment", "Listen failed.", error);
                                        failed_to_load_head_comment=true;
                                        startCountDownToNextRetry();
                                        return;

                                    }
                                    else
                                    {
                                        Log.i("listenForNewComments", "No error");
                                        Log.i("getComsUnderHeComment", "No error "+value.size()+" ");

                                        Log.i("getComsUnderHeComment","successful "
                                                +value.isEmpty()+" isFromCache="+value.getMetadata().isFromCache());


                                        if(isfiltering)
                                        {
                                            comments_already_notified_of.clear();
                                            commentListUnderHeadComment.clear();
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    commentAdapter.notifyDataSetChanged();

                                                }
                                            });

                                            if(comment_filter.getSelectedItemPosition()==1)
                                            {
                                                commentListUnderHeadComment.addAll(agree_comment_list);
                                            }
                                            else if(comment_filter.getSelectedItemPosition()==2)
                                            {
                                                commentListUnderHeadComment.addAll(disagree_comment_list);
                                            }
                                            else if(comment_filter.getSelectedItemPosition()==2)
                                            {
                                                commentListUnderHeadComment.addAll(question_comment_list);
                                            }

                                        }
                                        if(value.isEmpty()==false)
                                        {

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    sharedialog_area.setVisibility(View.GONE);
                                                }
                                            });

                                            for(int i=0;i<value.size();i++)
                                            {

                                                DocumentSnapshot documentSnapshot=value
                                                        .getDocuments().get(i);
                                                Comment comment=new Comment(documentSnapshot);

                                                if(comments_already_notified_of
                                                        .containsKey(comment.getComment_id())==false)
                                                {

                                                    commentListUnderHeadComment.add(comment);
                                                    usdates.add(comment.getCreatedDate());
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            if(commentAdapter.getItemCount()>0&isfiltering==false)
                                                            {
                                                                commentAdapter.notifyItemInserted(commentListUnderHeadComment.size()-1);
                                                            }

                                                        }
                                                    });

                                                    comments_already_notified_of.put(comment.getComment_id(),
                                                            commentListUnderHeadComment.size()-1);

                                                    if(comment.getComment_type()==Comment.AGREES)
                                                    {
                                                        agree_comment_list.add(comment);
                                                    }
                                                    if(comment.getComment_type()==Comment.DISAGREES)
                                                    {
                                                        disagree_comment_list.add(comment);
                                                    }
                                                    if(comment.getComment_type()==Comment.QUESTION)
                                                    {
                                                        question_comment_list.add(comment);
                                                    }


                                                }
                                                else
                                                {

                                                    //refresh it, just in case the comment
                                                    int position=comments_already_notified_of
                                                            .get(comment.getComment_id());
                                                    if(commentListUnderHeadComment.size()>=position+1)
                                                    {
                                                        commentListUnderHeadComment.set(position,comment);
                                                    }
                                                    else
                                                    {
                                                        commentListUnderHeadComment.add(comment);
                                                    }


                                                }




                                            }

                                            if(commentAdapter.getItemCount()==0)
                                            {
                                                last_comment_date=commentListUnderHeadComment
                                                        .get(commentListUnderHeadComment.size()-1);
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        commentAdapter.notifyDataSetChanged();

                                                    }
                                                });
                                                failed_to_load_comments=false;
                                                isfiltering=false;
                                            }
                                            //comments_already_notified_of.clear();
                                            //listenForNewComments();

                                        }
                                        else
                                        {
                                            NoCommentsUnderHeadComment();
                                        }

                                    }

                                }
                            });

                        }
                    });

        }

    }

    private void showSuggestiveNoComment()
    {

        //check for another comment


    }

    private void noHeadComment()
    {

        loadSuggestions(convo_title);

    }

    public Comment getHead_comment() {
        return head_comment;
    }



    void loadSuggestions(String term)
    {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {



            }
        });

        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getActivity()).userInfoDao();
                        UserInfo userInfo=null;
                        if(firebaseUser!=null)
                        {
                            userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());
                        }
                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        CountryCodePicker countryCodePicker=new CountryCodePicker(getActivity());
                        countryCodePicker.setAutoDetectedCountry(true);
                        if(userInfo!=null)
                        {
                            countryCodePicker.setCountryForPhoneCode(userInfo.country_code);
                        }
                        //News
                        String url = "https://www.google.com/search?q="+countryCodePicker.getSelectedCountryName()
                                +" "+term+"&tbm=nws";

                        Log.i("ladsug","");
                        mediaItemWebSearchList.clear();
                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        Log.i("ladsug","onResponse "
                                                +countryCodePicker.getSelectedCountryName()+" "+term);
                                        Document doc = Jsoup.parse(response);
                                        Elements body=doc.getElementsByTag("body");

                                        Log.i("ladsug","body="+body.isEmpty());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {



                                            }
                                        });

                                        Elements headline_links=body.select("a:matches(([a-zA-Z(.+)]+\\s?\\b){4,})");
                                        Log.i("divs","divs.isEmpty()="+headline_links.isEmpty()
                                                +" "+headline_links.size());

                                        for(int i=0;i<headline_links.size();i++)
                                        {

                                            Element head_element=headline_links.get(i);
                                            String headline=head_element.text();
                                            String source_link=head_element.attr("href");
                                            String source_name="";
                                            String timestamp="";

                                            boolean valid_gcard=false;
                                            if(head_element.hasParent())
                                            {
                                                if(head_element.parent().hasParent())
                                                {
                                                    if(head_element.parent().parent().hasParent())
                                                    {
                                                        valid_gcard=true;
                                                    }
                                                }
                                            }
                                            if(valid_gcard)
                                            {

                                                boolean nm=head_element.parent().parent().parent().tagName().trim().equals("g-card");

                                                if(nm)
                                                {


                                                    Log.i("divs"," "+ (head_element.tagName())+" nm="+nm);
                                                    Log.i("divs","headline="+headline);
                                                    Log.i("divs","source_link="+source_link);
                                                    Elements spans=head_element.select("span:matches(([a-zA-Z(.+)]+\\s?\\b){1,6})");

                                                    Log.i("divs","spans.isEmpty()="+spans.isEmpty());
                                                    if(spans.isEmpty())
                                                    {
                                                        Log.i("kujshas","span empty i="+i);
                                                    }
                                                    //Determine the source name and timestamp
                                                    for(Element span : spans)
                                                    {
                                                        //test it is not a time stamp
                                                        Pattern p1 = Pattern.compile("^[0-9]{1,2}\\s\\D{3,7}\\s\\D{4,7}");
                                                        Pattern p2 = Pattern.compile("^[0-9]{1,2}\\s[A-Za-z]{1,9}\\s\\d{4,7}");
                                                        Pattern p3 = Pattern.compile("^[0-9]{1,2}\\s[A-Za-z]{1,9}\\sago");
                                                        Matcher m1 = p1.matcher(span.text());
                                                        Matcher m2 = p2.matcher(span.text());
                                                        Matcher m3 = p3.matcher(span.text());
                                                        boolean b = m1.matches()||m2.matches()||m3.matches();
                                                        if(b==false&source_name.isEmpty()&span.text().isEmpty()==false)
                                                        {
                                                            source_name=span.text();
                                                            Log.i("divs","spans.text="+span.text()+"(this is the source)");
                                                        }
                                                        else if(b&span.text().isEmpty()==false)
                                                        {
                                                            timestamp=span.text();
                                                            Log.i("divs","spans.text="+span.text()+"(this is the time stamp)");
                                                        }
                                                        else
                                                        {
                                                            Log.i("kujshas",span.text()+" i="+i);

                                                        }



                                                    }


                                                    if(source_name.trim().isEmpty()==false)
                                                    {
                                                        MediaItemWebSearch mediaItemWebSearch=new MediaItemWebSearch();
                                                        mediaItemWebSearch.setSource(source_name);
                                                        mediaItemWebSearch.setHeading(headline);
                                                        mediaItemWebSearch.setLink(source_link);
                                                        mediaItemWebSearch.setNews(true);
                                                        mediaItemWebSearch.setTimeStamp(timestamp);
                                                        mediaItemWebSearchList.add(mediaItemWebSearch);
                                                        //check if the headline is clipped and then web crawl the website
                                                        //source link
                                                        boolean heading_clipped=false;
                                                        if(mediaItemWebSearch.getHeading().indexOf("...")>-1)
                                                        {
                                                            heading_clipped=true;
                                                        }

                                                        startHeadingAndImageFetch(heading_clipped);

                                                    }
                                                    //Log.i("divs"," "+head_element.parent().outerHtml());
                                                }


                                            }
                                            //handle the google valid traffic validation

                                        }

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                mediaItemAdapter.notifyDataSetChanged();

                                            }
                                        });


                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("ladsug","onErrorResponse");



                            }
                        });



                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);

                    }
                });



    }

    private void startHeadingAndImageFetch(boolean heading_clipped)
    {

        

    }


    @BindView(R.id.rootview)
    FrameLayout rootview;
    private void showNewCommentAlert()
    {

        Snackbar.make(rootview, "There is a new comment", Snackbar.LENGTH_LONG).setAction("Scroll to it",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        comment_list.scrollToPosition(0);

                    }
                }).show();

    }

    private void incrementNumComments(String head_comment_id)
    {

        Map<String,Object> values=new HashMap<String,Object>();
        values.put(OrgFields.NUM_COMMENTS,FieldValue.increment(1));

        CloudWorker.getLetsTalkComments()
                .document(head_comment_id)
                .set(values, SetOptions.merge())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("incrementNumComments","failed error="+e.getMessage());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.i("incrementNumComments","successful");

                    }
                });
    }


    NewsFactsMedia newsFactsMedia;
    public void setOnImageShare(NewsFactsMedia newsFactsMedia)
    {

        this.newsFactsMedia=newsFactsMedia;



    }

    class MediaItemAdapter extends RecyclerView.Adapter<Holder>
    {


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.news_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {

            final MediaItemWebSearch mediaItemWebSearch=mediaItemWebSearchList.get(position);

            holder.news_headline.setText(mediaItemWebSearch.getHeading());
            holder.news_source.setText(mediaItemWebSearch.getSource());
            holder.news_time.setText(mediaItemWebSearch.getTimestamp());

            holder.action_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    if(firebaseUser!=null)
                    {

                        UserInfoDatabase.databaseWriteExecutor
                                .execute(new Runnable() {
                                    @Override
                                    public void run() {


                                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getContext())
                                                        .userInfoDao();
                                        UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                                        if(userInfo!=null)
                                        {

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    /*
                                                    Since in this fragment from the news suggestion adapter
                                                    the user can qoute or share directly from this fragment
                                                    otherwise a comment on the news suggestion is done the parent activity
                                                     */
                                                    new MaterialDialog.Builder(getActivity())
                                                            .title("Quote or comment")
                                                            .content("Do you want to quote the news event exactly as is or comment on it?")
                                                            .positiveText("Quote")
                                                            .onPositive(new MaterialDialog.SingleButtonCallback()
                                                            {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                                    @NonNull DialogAction which) {

                                                                    /*
                                                                    If you were qouting then there would be no need for the typing area hence no need to access
                                                                    an instance of the Activity to populate the view for the thing the user is commentating on.
                                                                     */
                                                                    if(head_comment!=null)
                                                                    {

                                                                        new MaterialDialog.Builder(getActivity())
                                                                                .title("Just to be organized")
                                                                                .content("Under the current head comment/topic or new topic")
                                                                                .positiveText("Under current head topic")
                                                                                .negativeText("New Topic")
                                                                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                                    @Override
                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                                        sendQouteUnderHeadComment(mediaItemWebSearch);
                                                                                    }
                                                                                })
                                                                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                                    @Override
                                                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                                        sendQoute(mediaItemWebSearch);

                                                                                    }
                                                                                })
                                                                                .cancelable(false)
                                                                                .show();

                                                                    }
                                                                    else
                                                                    {

                                                                        sendQoute(mediaItemWebSearch);

                                                                    }

                                                                }
                                                            })
                                                            .negativeText("Comment")
                                                            .onNegative(new MaterialDialog.SingleButtonCallback()
                                                            {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                                    @NonNull DialogAction which)
                                                                {

                                                                    if(head_comment!=null)
                                                                    {

                                                                        CommentListener commentListener=(CommentListener) getActivity();
                                                                        if(commentListener!=null)
                                                                        {
                                                                            commentListener.sendCommentWithNewsReference(mediaItemWebSearch,head_comment);
                                                                        }

                                                                    }
                                                                    else
                                                                    {

                                                                        CommentListener commentListener=(CommentListener) getActivity();
                                                                        if(commentListener!=null)
                                                                        {
                                                                            commentListener.sendCommentWithNewsReference(mediaItemWebSearch,null);
                                                                        }

                                                                    }

                                                                }
                                                            })
                                                            .neutralText("Share")
                                                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                                    @NonNull DialogAction which) {

                                                                    sendCommentThenShare(mediaItemWebSearch);

                                                                }
                                                            })
                                                            .cancelable(false)
                                                            .show();

                                                }
                                            });

                                        }
                                        else
                                        {

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    new MaterialDialog.Builder(getActivity())
                                                            .title("We missing your names")
                                                            .content("We need your names, there missing locally " +
                                                                    "so we need to re-capture so people know who is talking. " +
                                                                    "(Please do not wipe out app cache and data)")
                                                            .cancelable(false)
                                                            .positiveText("Okay")
                                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                                    @NonNull DialogAction which) {

                                                                    Intent intent=new Intent(getActivity(),CaptureInfo1.class);
                                                                    startActivity(intent);

                                                                }
                                                            })
                                                            .negativeText("Cancel")
                                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                                    @NonNull DialogAction which) {



                                                                }
                                                            })
                                                            .show();

                                                }
                                            });

                                        }




                                    }
                                });

                    }
                    else
                    {

                        new MaterialDialog.Builder(getContext())
                                .title("Oops, we need an introduction")
                                .content("We will send to our sign in page and " +
                                        "capture your name, so we know who is speaking")
                                .cancelable(false)
                                .positiveText("Okay")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        Intent intent=new Intent(getActivity(), SignInAccount.class);
                                        startActivity(intent);

                                    }
                                })
                                .negativeText("Cancel")
                                .show();

                    }


                }
            });

            if(mediaItemWebSearch.getHeading().indexOf("...")>-1)
            {
                
            }
            else
            {

            }


        }

        @Override
        public int getItemCount() {
            return mediaItemWebSearchList.size();
        }
    }

    private void sendQoute(MediaItemWebSearch mediaItemWebSearch)
    {

        is_qoute=true;
        shared_news_event=mediaItemWebSearch;
        /*
        If you were qouting then there would be no need for the typing area hence no need to access
        an instance of the Activity to populate the view for the thing the user is commentating on.
         */
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getActivity())
                                .userInfoDao();
                        UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());
                        if(userInfo!=null)
                        {

                            submitDataCloud(userInfo,false);

                        }

                    }
                });

    }

    private void sendQouteUnderHeadComment(MediaItemWebSearch mediaItemWebSearch)
    {

        is_qoute=true;
        shared_news_event=mediaItemWebSearch;
        /*
        If you were qouting then there would be no need for the typing area hence no need to access
        an instance of the Activity to populate the view for the thing the user is commentating on.
         */
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getActivity())
                                .userInfoDao();
                        UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());
                        if(userInfo!=null)
                        {

                            submitDataCloud(userInfo,true);

                        }

                    }
                });


    }

    private void sendCommentThenShare(MediaItemWebSearch mediaItemWebSearch)
    {

        shared_news_event=mediaItemWebSearch;
        is_sharing=true;
        please_wait=new MaterialDialog.Builder(getContext())
                .title("Please wait")
                .content("We busy sorting things out")
                .cancelable(false)
                .show();

        //decide whether there is a head comment or not
        boolean there_is_head_comment=head_comment!=null;
        if(there_is_head_comment)
        {


            if(please_wait!=null)
            {

                please_wait.dismiss();
                please_wait=new MaterialDialog.Builder(getContext())
                        .title("Just to be organized")
                        .content("Will this be under the current head comment/topic or a new head comment or topic")
                        .cancelable(false)
                        .positiveText("Under current head comment")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                //signal that at the end of comment creation
                                //to sent the news event to the sharing platform
                                //using shareComment(MediaItemWebSearch mediaItemWebSearch)
                                sendNewsEventAfterCommentCreate=true;
                                sendCommentUnderHeadComment(mediaItemWebSearch);

                            }
                        })
                        .negativeText("New Topic")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                //signal that at the end of comment creation
                                //to sent the news event to the sharing platform
                                //using shareComment(MediaItemWebSearch mediaItemWebSearch)
                                sendNewsEventAfterCommentCreate=true;
                                createHeadComment(mediaItemWebSearch);

                            }
                        })
                        .show();

            }


        }
        else
        {


            //signal that at the end of comment creation
            //to sent the news event to the sharing platform
            //using shareComment(MediaItemWebSearch mediaItemWebSearch)
            sendNewsEventAfterCommentCreate=true;
            createHeadComment(mediaItemWebSearch);

        }

    }

    //this must show their please wait dialogs since before this a menu is shown to the user
    private void createHeadComment(MediaItemWebSearch mediaItemWebSearch)
    {

        //remember after this the share dialog
        //using the shareComment(MediaItemWebSearch mediaItemWebSearch)
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(please_wait!=null)
                {

                    please_wait=new MaterialDialog.Builder(getActivity())
                            .title("we creating a new topic for this news")
                            .content("we creating a new topic as this news event")
                            .cancelable(false)
                            .show();
                }

            }
        });

        //The person commenting
        //we need these details from the local database
        UserInfoDatabase.databaseWriteExecutor
                        .execute(new Runnable() {
                            @Override
                            public void run() {

                                Map<String, Object> values = new HashMap<>();

                                UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getContext())
                                        .userInfoDao();

                                UserInfo userInfo= userInfoDao.getUserInfo(firebaseUser.getUid());

                                if(userInfo!=null)
                                {

                                    submitDataCloud(userInfo,true);

                                }


                            }
                        });


    }

    private void sendCommentUnderHeadComment(MediaItemWebSearch mediaItemWebSearch)
    {

        //remember after this the share dialog
        //using the shareComment(MediaItemWebSearch mediaItemWebSearch)


        //The person commenting
        //we need these details from the local database
        UserInfoDatabase.databaseWriteExecutor
                .execute(new Runnable() {
                    @Override
                    public void run() {

                        Map<String, Object> values = new HashMap<>();

                        UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getContext())
                                .userInfoDao();

                        UserInfo userInfo= userInfoDao.getUserInfo(firebaseUser.getUid());

                        if(userInfo!=null)
                        {

                            submitDataCloud(userInfo,false);

                        }


                    }
                });


    }

    private void submitDataCloud(UserInfo userinfo,boolean under_head_comment)
    {

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        final Map<String,Object> values=new HashMap<String,Object>();
        //the person commentating
        values.put(OrgFields.USER_UID,firebaseUser.getUid());
        values.put(OrgFields.COMMENTATER_NAME,userinfo.firstname+" "+userinfo.lastname);
        values.put(OrgFields.USER_COUNTRY_CODE,userinfo.country_code);
        if(userinfo.profile_path!=null)
        {
            values.put(OrgFields.COMMENTER_PROFILE_IMAGE,userinfo.profile_path);
        }
        /*
        Since in this fragment from the news suggestion adapter
        the user can qoute or share directly from this fragment
        otherwise a comment on the news suggestion is done the parent activity
         */
        boolean cont=is_sharing||is_qoute;
        //whether it is under a head comment or topic
        if(under_head_comment&head_comment==null)
        {
            values.put(OrgFields.IS_NEW_TOPIC,false);
            values.put(OrgFields.PARENT_COMMENT_ID,head_comment.getComment_id());
        }
        else
        {
            values.put(OrgFields.IS_NEW_TOPIC,true);
        }
        if(is_sharing)
        {
            if(shared_news_event!=null)
            {
                values.put(OrgFields.COMMENT,shared_news_event.getSource()+":"+shared_news_event.getHeading());
                values.put(OrgFields.COMMENT_TYPE,Comment.QOUTE);
                values.put(OrgFields.IS_SHARING,true);
                values.put(OrgFields.HAS_NEWS_REFERENCE,true);
                values.put(OrgFields.NEWS_SOURCE,shared_news_event.getSource());
                values.put(OrgFields.NEWS_SOURCELINK,shared_news_event.getLink());
                values.put(OrgFields.NEWS_HEADLINE,shared_news_event.getHeading());

            }
        }
        else if(is_qoute)
        {

            values.put(OrgFields.COMMENT_TYPE,Comment.QOUTE);
            if(shared_news_event!=null)
            {
                values.put(OrgFields.COMMENT,shared_news_event.getSource()+":"+shared_news_event.getHeading());
                values.put(OrgFields.COMMENT_TYPE,Comment.QOUTE);
                values.put(OrgFields.IS_SHARING,false);
                values.put(OrgFields.HAS_NEWS_REFERENCE,true);
                values.put(OrgFields.NEWS_SOURCE,shared_news_event.getSource());
                values.put(OrgFields.NEWS_SOURCELINK,shared_news_event.getLink());
                values.put(OrgFields.NEWS_HEADLINE,shared_news_event.getHeading());

            }


        }
        else
        {
            /*
            Since in this fragment from the news suggestion adapter
            the user can qoute or share directly from this fragment
            otherwise a comment on the news suggestion is done the parent activity
             */
        }

        //the timestamp for the comment
        values.put(OrgFields.USER_CREATED_DATE,FieldValue.serverTimestamp());
        values.put(OrgFields.USER_MODIFIED_DATE,FieldValue.serverTimestamp());
        values.put(OrgFields.CREATED_MONTH,new DateTime().getMonthOfYear());

        //comment belong to which conversation
        values.put(OrgFields.CONVERSATION_ID,conversation_id);
        values.put(OrgFields.IS_STANDALONE_CONVO,is_standalone);
        values.put(OrgFields.IS_ACTIVITY_BASED,is_activity_based);
        if(is_activity_based)
        {
            values.put(OrgFields.ACTIVITY_ID,activity_id);
        }

        values.put(OrgFields.NUM_COMMENTS,0);

        if(cont)
        {
            CloudWorker.getLetsTalkComments()
                    .add(values)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            new MaterialDialog.Builder(getActivity())
                                    .title("Sorry we could not send comment")
                                    .content("error "+e.getMessage()+" Try again")
                                    .cancelable(false)
                                    .positiveText("Try again")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog,
                                                            @NonNull DialogAction which) {

                                            submitDataCloud(userinfo,under_head_comment);

                                        }
                                    })
                                    .negativeText("Cancel")
                                    .show();

                            Log.i("submitDataCloud","e="+e.getMessage());

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {


                            Log.i("submitDataCloud","successful "+documentReference.getId()
                                    +" is_sharing="+is_sharing+" is_qoute="+is_qoute);
                            if(is_sharing)
                            {

                                shareComment(shared_news_event, documentReference.getId(),conversation_id);
                                incrementNumComms(userinfo);
                                //update the comment adapter
                                if(head_comment!=null&under_head_comment)
                                {


                                }
                                else
                                {
                                    Comment comment=new Comment(values);
                                    setHead_comment(comment);
                                    //then this becomes the head comment
                                    CommentListener commentListener=(CommentListener) getActivity();
                                    if(commentListener!=null)
                                    {
                                        commentListener.foundHeadComment(comment);
                                    }
                                }



                            }
                            else if(is_qoute)
                            {


                                incrementNumComms(userinfo);
                                //update the comment adapter
                                if(head_comment!=null&under_head_comment)
                                {
                                    Comment comment=new Comment(values);

                                }
                                else
                                {
                                    Comment comment=new Comment(values);
                                    setHead_comment(comment);
                                    //then this becomes the head comment
                                    CommentListener commentListener=(CommentListener) getActivity();
                                    if(commentListener!=null)
                                    {
                                        commentListener.foundHeadComment(comment);
                                    }
                                }

                            }

                        }
                    });

        }


    }

    private void incrementNumComms(UserInfo userInfo)
    {

        Log.i("incrementNumComms","conversation_id.isEmpty()="+conversation_id.isEmpty());
        //the conversation ID or activity ID
        if(conversation_id.isEmpty()==false)
        {

            Map<String, Object> values = new HashMap<>();
            values.put(OrgFields.NUM_COMMENTS, FieldValue.increment(1));
            values.put(userInfo.country_code+"_"+OrgFields.NUM_COMMENTS,
                    FieldValue.increment(1));
            values.put(OrgFields.COMMENT_TYPE+"_"+OrgFields.NUM_COMMENTS,
                    FieldValue.increment(1));

            CloudWorker.getActivities()
                    .document(conversation_id)
                    .set(values, SetOptions.merge())
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.i("incrementNumComms","e="+e.getMessage());

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            Log.i("incrementNumComms","successful");

                        }
                    });


        }


    }

    private void shareComment(MediaItemWebSearch mediaItemWebSearch,String comment_ID,String conversation_id)
    {

        Memory memory=new Memory(getActivity());
        String primary_domain=memory.getString(getResources().getString(R.string.primary_domain));
        String secondary_domain=memory.getString(getResources().getString(R.string.secondary_domain));

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setAndroidParameters(new DynamicLink
                        .AndroidParameters
                        .Builder(getActivity().getApplicationContext().getPackageName())
                        .build())
                .setLink(Uri.parse(primary_domain+"/index.php?convo_id="+conversation_id+"&comment_id="+comment_ID))
                .setDomainUriPrefix("https://remould.page.link/")
                .buildShortDynamicLink()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created

                            final Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            Log.i("testDynLink","shortLink="+shortLink);

                            UserInfoDatabase.databaseWriteExecutor
                                    .execute(new Runnable() {
                                        @Override
                                        public void run() {

                                            UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(getActivity())
                                                    .userInfoDao();

                                            UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                                            if(userInfo!=null)
                                            {

                                                Intent share = new Intent(Intent.ACTION_SEND);
                                                share.setType("text/plain");
                                                //create dynamic link to this content

                                                if(mediaItemWebSearch.getSource().isEmpty()==false)
                                                {
                                                    share.putExtra(Intent.EXTRA_TEXT, userInfo.firstname+" "+userInfo.lastname
                                                            +mediaItemWebSearch.getSource()
                                                            +":"+mediaItemWebSearch.getHeading()+". "+shortLink);
                                                }
                                                else
                                                {
                                                    share.putExtra(Intent.EXTRA_TEXT, userInfo.firstname+" "+userInfo.lastname+
                                                            mediaItemWebSearch.getHeading()+". "+shortLink);
                                                }
                                                startActivity(Intent.createChooser(share, "Share this with someone"));
                                                sendNewsEventAfterCommentCreate=false;

                                            }


                                        }
                                    });




                        } else {
                            // Error
                            // ...
                        }
                    }
                });



    }

    class Holder extends RecyclerView.ViewHolder {

        public TextView news_source;
        public TextView news_headline;
        public TextView news_time;
        public TextView action_button;

        public Holder(@NonNull View itemView) {
            super(itemView);

            news_source=(TextView) itemView.findViewById(R.id.news_source);
            news_headline=(TextView) itemView.findViewById(R.id.news_headline);
            news_time=(TextView) itemView.findViewById(R.id.news_time);
            action_button=(TextView) itemView.findViewById(R.id.action_button);

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("current_topic","onStart");
        //mediaItemAdapter.notifyDataSetChanged();


    }



    List<Suggestion> content_sugg_list=new ArrayList<Suggestion>();

    class TopicSuggestion extends RecyclerView.Adapter<TopicSuggestion.TopicHolder>
    {


        @NonNull
        @Override
        public TopicHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.another_topic
                    ,parent,false);
            return new TopicHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TopicHolder holder, int position)
        {

            Suggestion suggestion=content_sugg_list.get(position);
            holder.another_topic_suggestion.setText(suggestion.getLabel());
            holder.title.setText(suggestion.getTitle());
            holder.description.setText(suggestion.getDescription());
            holder.rootview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                }
            });

            if(suggestion.isComment)
            {
                holder.action_button.setText("Comment");
            }
            else
            {
                holder.action_button.setText("Talk about this");
                holder.another_topic_suggestion.setText(suggestion.getLabel()+" Suggestion");
            }

        }

        @Override
        public int getItemCount() {
            return content_sugg_list.size();
        }

        public class TopicHolder extends RecyclerView.ViewHolder {
            CardView rootview;
            TextView another_topic_suggestion;
            TextView title;
            TextView description;
            TextView action_button;
            public TopicHolder(@NonNull View itemView) {
                super(itemView);

                rootview=(CardView) itemView.findViewById(R.id.rootview);
                another_topic_suggestion=(TextView) itemView.findViewById(R.id.another_topic_suggestion);
                title=(TextView) itemView.findViewById(R.id.title);
                description=(TextView) itemView.findViewById(R.id.description);
                action_button=(TextView) itemView.findViewById(R.id.action_button);

            }
        }

    }

    class Suggestion
    {

        private boolean isComment=false;
        private String commentID="";
        private String title="";
        private String description="";
        private String label="";

        public boolean isComment() {
            return isComment;
        }

        public void setComment(boolean comment) {
            isComment = comment;
        }

        public String getCommentID() {
            return commentID;
        }

        public void setCommentID(String commentID) {
            this.commentID = commentID;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    List<Comment> commentListUnderHeadComment=new ArrayList<Comment>();
    //comment adapter
    class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder>
    {


        @NonNull
        @Override
        public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if(viewType==Comment.AGREES)
            {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.agree_comment
                        ,parent,false);
                return new CommentHolder(view);
            }
            else if(viewType==Comment.DISAGREES)
            {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.disagree_comment
                        ,parent,false);
                return new CommentHolder(view);
            }
            else if(viewType==Comment.QUESTION)
            {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.question_comment
                        ,parent,false);
                return new CommentHolder(view);
            }
            else
            {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.agree_comment
                        ,parent,false);
                return new CommentHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull CommentHolder holder, int position) {

            Comment comment=commentListUnderHeadComment.get(position);

            /*Glide.with(getActivity()).clear(holder.head_profile_image);
            Drawable placeholder= new AvatarGenerator.AvatarBuilder(getActivity())
                    .setLabel(comment.getCommentator_name())
                    .setAvatarSize(120)
                    .setTextSize(30)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();

            Glide.with(getActivity())
                    .load(placeholder)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(holder.head_profile_image);

             */

            if(holder.comment_name!=null)
            {
                holder.comment_name.setText(comment.getCommentator_name());
                if(comment.getComment_type()==Comment.AGREES)
                {
                    holder.comment_type.setText("Agrees");
                    holder.comment_type.setTextColor(getResources().getColor(R.color.purple_700));
                }
                if(comment.getComment_type()==Comment.DISAGREES)
                {
                    holder.comment_type.setText("Disagrees");
                    holder.comment_type.setTextColor(getResources().getColor(R.color.red));
                }
                if(comment.getComment_type()==Comment.QUESTION)
                {
                    holder.comment_type.setText("Question");
                    holder.comment_type.setTextColor(getResources().getColor(R.color.grey));
                }
                if(comment.getComment_type()==Comment.QOUTE)
                {
                    holder.comment_type.setText("Qouted");
                    holder.comment_type.setTextColor(getResources().getColor(R.color.black));
                    if(comment.getNewsSource().isEmpty()==false)
                    {
                        holder.comment_type.setText("Qouted from "+comment.getNewsSource());
                    }
                }


                holder.comment.setText(comment.getComment());

            }
            else if(holder.comment!=null)
            {
                holder.comment.setText("Commenting");
            }

        }

        @Override
        public int getItemViewType(int position) {
            Comment comment=commentListUnderHeadComment.get(position);
            return comment.getComment_type();
        }

        @Override
        public int getItemCount() {
            return commentListUnderHeadComment.size();
        }

        public class CommentHolder extends RecyclerView.ViewHolder {


            TextView comment_name;
            TextView comment_type;
            TextView num_comments_view;
            EmojiconTextView comment;

            public CommentHolder(@NonNull View itemView) {
                super(itemView);


                comment_name=(TextView) itemView.findViewById(R.id.comment_name);
                comment_type=(TextView) itemView.findViewById(R.id.comment_type);
                num_comments_view=(TextView) itemView.findViewById(R.id.num_comments_view);
                comment=(EmojiconTextView) itemView.findViewById(R.id.comment);

            }


        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0
            , RIGHT|ItemTouchHelper.LEFT) {

        /*
            private lateinit var imageDrawable: Drawable
    private lateinit var shareRound: Drawable

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private lateinit var mView: View
    private var dX = 0f

    private var replyButtonProgress: Float = 0.toFloat()
    private var lastReplyButtonAnimationTime: Long = 0
    private var swipeBack = false
    private var isVibrate = false
    private var startTracking = false
         */
        private Drawable imageDrawable;


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            Comment comment=commentListUnderHeadComment.get(viewHolder.getBindingAdapterPosition());
            CommentListener commentListener=(CommentListener) getActivity();

            if(commentListener!=null)
            {

                commentListener.onReply(comment);

            }

        }


        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return super.convertToAbsoluteDirection(flags, layoutDirection);

        }


    };

    private int quotedMessagePos;
    MessageSwipeController messageSwipeController=new MessageSwipeController(getActivity(),
            new SwipeControllerActions() {
        @Override
        public void showReplyUI(int position) {
            quotedMessagePos = position;
            Comment comment=commentListUnderHeadComment.get(position);
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

    @Override
    public void onResume() {
        super.onResume();

        commentAdapter.notifyDataSetChanged();
        if(hj!=null&failed_to_load_head_comment
                ||hj!=null&failed_to_load_comments)
        {
            hj.cancel();
            reTryLoad();
        }
        else if(failed_to_load_head_comment||failed_to_load_comments)
        {
            reTryLoad();
        }

    }


    public void newComment(Comment comment,Map<String,Object> values)
    {

        Log.i("newComment","started");
        /*https://firebase.google.com/docs/firestore/query-data/
                        listen?hl=en&authuser=0#events-local-changes
                         */

        Log.i("newComment","s2");


        CloudWorker.getLetsTalkComments()
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

                        /*https://firebase.google.com/docs/firestore/query-data/
                        listen?hl=en&authuser=0#events-local-changes
                         */
                        //increment the number of comments
                        if(comment.isIs_new_topic()==false)
                        {
                            if(comment.getHead_comment_id().isEmpty()==false)
                            {
                                incrementNumComments(comment.getHead_comment_id());
                            }
                        }

                    }
                });

    }

    CountDownTimer hj;
    private void startCountDownToNextRetry() {

        Snackbar.make(rootview, "There was an an error loading, retry in "+30+" secs",
                Snackbar.LENGTH_LONG).setAction("Retry Now",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        reTryLoad();

                    }
                }).show();

        hj=new CountDownTimer(1000,30000) {
            @Override
            public void onTick(long l) {
                long sec=l/1000;
                Toast.makeText(getContext(),sec+" secs",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {

                reTryLoad();

            }

        }.start();

    }

    private void reTryLoad() {

        if(head_comment==null&failed_to_load_head_comment)
        {
            getHeadComment();
        }
        else if(failed_to_load_comments)
        {
            getCommentsUnderHeadComment();
        }

    }

    @BindView(R.id.sharedialog_area)
    RelativeLayout sharedialog_area;
    @BindView(R.id.no_data_area_label_sharedialog)
    TextView no_data_area_label_sharedialog;
    //sharedialog
    @OnClick(R.id.sharedialog_button)
    void ShareConVo()
    {



    }

    //no comments under head comment
    void NoCommentsUnderHeadComment()
    {

        sharedialog_area.setVisibility(View.VISIBLE);

    }

    //which means no topics
    void NoHeadComment()
    {



    }


}