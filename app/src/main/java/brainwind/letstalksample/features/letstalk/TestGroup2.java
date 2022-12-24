package brainwind.letstalksample.features.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.nex3z.notificationbadge.NotificationBadge;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.AllTopicsForConvo;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.NewsMedia;
import brainwind.letstalksample.features.letstalk.fragments.adapters.TestAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class TestGroup2 extends AppCompatActivity {


    //The conversation title and toolbar
    @BindView(R.id.label)
    ExpandableTextView label;

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
    @BindView(R.id.badge_prev)
    NotificationBadge badge_prev;
    @BindView(R.id.next_headcomment_area)
    FrameLayout next_headcomment_area;
    @BindView(R.id.headcomment_index)
    NotificationBadge headcomment_index;
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


        headcomment=commentx;
        //RelativeLayout head_comment_rootview=(RelativeLayout) findViewById(R.id.head_comment_rootview);
        comment_name.setText(commentx.getCommentator_name());
        comment.setText(commentx.getComment());
        time_sent.setText(commentx.getTimeStr());
        num_people_read.setText(NumUtils.getAbbreviatedNum(commentx.getNum_comments())+" comments");
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

            Drawable dh2= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();
            headcomment_index.setBadgeBackgroundDrawable(dh2);
            headcomment_index.setTextColor(getResources().getColor(R.color.white));
            headcomment_index.setText(NumUtils.getAbbreviatedNum(selected_headcomment_position+1));


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

            Drawable dh2= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.white))
                    .build();
            headcomment_index.setBadgeBackgroundDrawable(dh2);
            headcomment_index.setTextColor(getResources().getColor(R.color.purple_700));
            headcomment_index.setText(NumUtils.getAbbreviatedNum(selected_headcomment_position+1));

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

            Drawable dh2= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();
            headcomment_index.setBadgeBackgroundDrawable(dh2);
            headcomment_index.setTextColor(getResources().getColor(R.color.white));
            headcomment_index.setText(NumUtils.getAbbreviatedNum(selected_headcomment_position+1));

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

            Drawable dh2= new AvatarGenerator.AvatarBuilder(TestGroup2.this)
                    .setLabel("")
                    .setTextSize(0)
                    .setAvatarSize(80)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();
            headcomment_index.setBadgeBackgroundDrawable(dh2);
            headcomment_index.setTextColor(getResources().getColor(R.color.white));
            headcomment_index.setText(NumUtils.getAbbreviatedNum(selected_headcomment_position+1));

        }


        Log.i("showHeadComment",commentx.getComment());
        typing_area.setVisibility(View.VISIBLE);
        disable_typing_area.setVisibility(View.VISIBLE);
        disable_typing_area_label.setText("Please wait");


        getComments(commentx);


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
                badge_prev.setNumber(how_many_comments_before);

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
            badge_prev.setNumber(how_many_comments_before);
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
    //methods for loading Head Comments
    private void LoadHeadComments()
    {

        //add a agree item

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

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

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
    private void getComments(Comment headcommment)
    {

        busy=true;
        if(testAdapter==null)
        {
            testAdapter=new TestAdapter();
            //comment_list.setLayoutManager(new LinearLayoutManager(getContext()));
            comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            comment_list.setAdapter(testAdapter);
        }
        testAdapter.is_loading=true;
        testAdapter.commentListUnderHeadComment.clear();
        testAdapter.notifyDataSetChanged();
        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,"S3aNh6Jq7ZajBiJS2jut")
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,headcommment.getComment_id())
                .whereEqualTo(OrgFields.DAY,19)
                .whereEqualTo(OrgFields.MONTH,12)
                .whereEqualTo(OrgFields.YEAR,2022)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);

        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        busy=false;
                        testAdapter.is_loading=false;
                        testAdapter.notifyDataSetChanged();
                        Log.i("getComments","onFailure "+e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        testAdapter.is_loading=false;
                        Log.i("getComments","onSuccess "+queryDocumentSnapshots.isEmpty());

                        if(queryDocumentSnapshots.isEmpty()==false)
                        {

                            disable_typing_area.setVisibility(View.GONE);
                            typing_area.setVisibility(View.VISIBLE);
                            for(int i=0;i<queryDocumentSnapshots.getDocuments().size();i++)
                            {

                                DocumentSnapshot documentSnapshot=queryDocumentSnapshots.getDocuments().get(i);
                                Comment comment=new Comment(documentSnapshot);
                                testAdapter.commentListUnderHeadComment.add(comment);
                            }

                            testAdapter.notifyDataSetChanged();
                            final int yu=testAdapter.commentListUnderHeadComment.size()-1;
                            Comment mkl1=testAdapter.commentListUnderHeadComment.get(yu);
                            mkl1.setSent(false);
                            testAdapter.commentListUnderHeadComment.set(yu,mkl1);
                            testAdapter.notifyItemChanged(yu);
                            comment_list.scrollToPosition(yu);
                            comment_list.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("addCommentsX","1 yu="+yu);
                                    comment_list.smoothScrollToPosition(yu);
                                    Log.i("addCommentsX","2 yu="+yu);
                                    if(yu<testAdapter.commentListUnderHeadComment.size())
                                    {
                                        Comment mkl1=testAdapter.commentListUnderHeadComment.get(yu);
                                        mkl1.setSent(true);
                                        testAdapter.commentListUnderHeadComment.set(yu,mkl1);
                                        testAdapter.notifyItemChanged(yu);
                                    }
                                    busy=false;

                                }
                            },200);

                        }
                        else
                        {

                        }

                    }
                });

    }

    //The Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_group2);

        ButterKnife.bind(this);
        //setUpViewPager();

        marker_filter_area.setVisibility(View.VISIBLE);

        LoadHeadComments();

        label.setText("Kevin Samuels’ death result of hypertension");

    }




}