package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentCommentsView extends QueryWorker implements LoadingResults {

    RelativeLayout rootview;
    Fragment currentTopicFragment;

    //head comment views
    //The parent view
    public CardView head_comment_parent_view;
    //loading
    public RelativeLayout loading_head_comment;
    public ProgressBar progress_bar_loading_head_comment;
    //After loaded the head comment
    //There is a head comment
    public LinearLayout head_comment_view;
    public CircleImageView head_profile_image;
    public TextView head_comment_name;
    public TextView head_comment_type;
    public ExpandableTextView head_comment_textView;
    public ImageButton expand_collapse;
    public TextView num_comments_view;
    public TextView status;
    public FloatingActionButton view_more_headtopics;

    //There is no head comment
    public RelativeLayout sharedialog_area;
    public TextView no_data_area_label_sharedialog;
    public Button sharedialog_button;
    public ExpandableTextView qouted_comment;

    //Area below head comment area
    public RelativeLayout marker_filter_area;
    public Spinner comment_filter;

    //Failed to load the head comment
    public LinearLayout head_comment_error_area;
    public TextView head_comment_error_label;
    public Button head_comment_tryagain;
    public Spinner comment_type_spinner;
    String conversation_id = "";

    public CurrentCommentsView(String conversation_id, Fragment currentTopicFragment) {
        super(conversation_id, currentTopicFragment);
        this.currentTopicFragment = currentTopicFragment;
        setCurrentTopicForConvo(currentTopicFragment);
        this.conversation_id = conversation_id;
        PrepareViews();
    }


    @Override
    public void ProcessResults() {

    }

    @Override
    public void NoMoreResults() {

    }

    @Override
    public void ShowComments() {

    }

    @Override
    public void PrepareViews() {

        if (this.currentTopicFragment != null) {
            if (this.currentTopicFragment.getView() != null) {
                this.rootview = currentTopicFragment.getView().findViewById(R.id.rootview);
                if (this.rootview != null) {
                    setHeadCommentViews();
                }
            }
        }


    }

    private void setHeadCommentViews() {

        //head comment views
        //loading
        this.loading_head_comment = this.rootview
                .findViewById(R.id.loading_head_comment);
        this.progress_bar_loading_head_comment = this.rootview
                .findViewById(R.id.progress_bar_loading_head_comment);
        //After loaded the head comment
        //There is a head comment
        this.head_comment_parent_view = this.rootview
                .findViewById(R.id.head_comment_parent_view);
        this.head_comment_view = this.rootview
                .findViewById(R.id.head_comment_view);
        this.head_profile_image = this.rootview
                .findViewById(R.id.head_profile_image);
        this.head_comment_name = this.rootview
                .findViewById(R.id.head_comment_name);
        this.head_comment_type = this.rootview
                .findViewById(R.id.comment_type);
        this.head_comment_textView = this.rootview
                .findViewById(R.id.head_comment);
        this.expand_collapse=this.head_comment_textView.findViewById(com.ms.square.android.expandabletextview.R.id.expand_collapse);
        this.num_comments_view = this.rootview
                .findViewById(R.id.num_comments_view);
        this.status = this.rootview
                .findViewById(R.id.status);
        this.view_more_headtopics=this.rootview.findViewById(R.id.view_more_headtopics);

        //There is no head comment
        sharedialog_area = this.rootview
                .findViewById(R.id.sharedialog_area);
        no_data_area_label_sharedialog = this.rootview
                .findViewById(R.id.no_data_area_label_sharedialog);
        sharedialog_button = this.rootview
                .findViewById(R.id.sharedialog_button);
        qouted_comment = this.rootview
                .findViewById(R.id.qouted_comment);

        //Failed to load the head comment
        head_comment_error_area = this.rootview
                .findViewById(R.id.head_comment_error_area);
        head_comment_error_label = this.rootview
                .findViewById(R.id.head_comment_error_label);
        head_comment_tryagain = this.rootview
                .findViewById(R.id.head_comment_tryagain);


    }

    public View getRootView() {
        return this.rootview;
    }

    public void NoHeadCommentView(CommentWorker commentWorker) {

        sharedialog_area.setVisibility(View.VISIBLE);
        no_data_area_label_sharedialog.setText("There are no comments in this convo yet, make a comment or share convo");
        head_comment_parent_view.setVisibility(View.GONE);
        sharedialog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("NoHeadComment", "started");
                listenForHeadCommentWhileNoHeadComment(commentWorker);
                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks
                        .getInstance()
                        .createDynamicLink()
                        .setLink(Uri.parse("https://www.bookie.remould.co.za/?"
                                + "convo_id="+conversation_id
                                +"&is_st="+is_standalone))
                        .setDomainUriPrefix("https://remould.page.link")
                        // Open links with this app on Android
                        .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                        .buildShortDynamicLink()
                        .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                                if (task.isSuccessful()) {

                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    if(title.isEmpty()==false)
                                    {
                                        share.putExtra(Intent.EXTRA_TEXT, "Talk about this convo, '"+title+"' "+shortLink);
                                    }
                                    else
                                    {
                                        share.putExtra(Intent.EXTRA_TEXT, "Talk about this convo, "+shortLink);
                                    }

                                    Log.i("NoHeadComment", "shortLink=" + shortLink);
                                    sharedialog_area.getContext().startActivity(Intent.createChooser(share, "Share Convo"));

                                } else {

                                    Log.i("NoHeadComment", task.getException().getMessage());

                                }

                            }
                        });

            }
        });

    }

    public void LoadingHeadCommentView() {

        sharedialog_area.setVisibility(View.GONE);
        head_comment_parent_view.setVisibility(View.VISIBLE);
        //show the loading area
        progress_bar_loading_head_comment.setVisibility(View.VISIBLE);
        loading_head_comment.setVisibility(View.VISIBLE);
        head_comment_view.setVisibility(View.INVISIBLE);
        head_comment_error_area.setVisibility(View.INVISIBLE);

    }

    public void ShowHeadCommentError(String errorMessage,CommentWorker commentWorkerinstance)
    {

        sharedialog_area.setVisibility(View.GONE);
        head_comment_parent_view.setVisibility(View.VISIBLE);
        //show the loading area
        head_comment_view.setVisibility(View.INVISIBLE);

        progress_bar_loading_head_comment.setVisibility(View.INVISIBLE);
        loading_head_comment.setVisibility(View.VISIBLE);
        head_comment_error_area.setVisibility(View.VISIBLE);
        head_comment_error_label.setText(errorMessage);

        head_comment_tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommentCommunications commentCommunications=(CommentCommunications) commentWorkerinstance;
                if(commentCommunications!=null)
                {
                    commentCommunications.TryLoadHeadComment();
                }

            }
        });

    }

    String title="";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    boolean is_standalone=true;

    public boolean isIs_standalone() {
        return is_standalone;
    }

    public void setIs_standalone(boolean is_standalone) {
        this.is_standalone = is_standalone;
    }

    boolean shownHeadCommentTools=false;
    public void showHeadComment(Comment headcomment,CommentWorker commentWorkerinstance)
    {

        Log.i("showHeadComment","started");
        setHead_comment(headcomment);
        sharedialog_area.setVisibility(View.GONE);
        loading_head_comment.setVisibility(View.GONE);
        head_comment_view.setVisibility(View.VISIBLE);
        head_comment_name.setText(headcomment.getCommentator_name());
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
            head_comment_name.setText(headCommentName.trim());
            Log.i("muhgsfa","headCommentName="+headCommentName);
        }

        head_comment_textView.setText(Html.fromHtml(headcomment.getComment()));
        if(headcomment.isHasNewsReference())
        {
            status.setText("Based on source:"+headcomment.getNewsSource());
            status.setTextColor(getRootView().getResources().getColor(R.color.green));
            head_comment_textView.setText(headcomment.getComment());
        }
        else
        {
            status.setText("No source");
            status.setTextColor(getRootView().getResources().getColor(R.color.red));

        }

        num_comments_view.setText(NumUtils.getAbbreviatedNum(headcomment.getNum_comments())+" comments");
        Glide.with(head_profile_image.getContext()).clear(head_profile_image);
        //show the user's profile image
        Drawable placeholder = new AvatarGenerator.AvatarBuilder(currentTopicForConvo.getActivity())
                .setLabel(head_comment.getCommentator_name())
                .setAvatarSize(120)
                .setTextSize(30)
                .toCircle()
                .setBackgroundColor(currentTopicForConvo.getActivity().
                        getResources().getColor(R.color.purple_700))
                .build();

        if(headcomment.getCommentator_profile_path()!=null)
        {
            Glide.with(head_profile_image.getContext())
                    .load(Uri.parse(headcomment.getCommentator_profile_path()))
                    .placeholder(placeholder)
                    .centerCrop()
                    .error(placeholder)
                    .into(head_profile_image);
        }
        else
        {
            Glide.with(head_profile_image.getContext())
                    .load(placeholder)
                    .placeholder(placeholder)
                    .centerCrop()
                    .error(placeholder)
                    .into(head_profile_image);
        }
        //head_profile_image.setVisibility(View.GONE);
        if(headcomment.getComment_type()==Comment.AGREES)
        {
            head_comment_type.setText("Agrees with Convo Title");
            head_comment_type.setTextColor(getRootView().getResources().getColor(R.color.purple_700));
        }
        if(headcomment.getComment_type()==Comment.DISAGREES)
        {
            head_comment_type.setText("Disagrees with Convo Title");
            head_comment_type.setTextColor(getRootView().getResources().getColor(R.color.red));
        }
        if(headcomment.getComment_type()==Comment.QUESTION)
        {
            head_comment_type.setText("Questions the Convo Title");
            head_comment_type.setTextColor(getRootView().getResources().getColor(R.color.blue));
        }
        if(headcomment.getComment_type()==Comment.ANSWER)
        {
            head_comment_type.setText("Answers the Convo Title");
            head_comment_type.setTextColor(getRootView().getResources().getColor(R.color.blue));
        }

        view_more_headtopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommentCommunications commentCommunications=(CommentCommunications) commentWorkerinstance;
                if(commentCommunications!=null)
                {
                    commentCommunications.ShowMoreHeadComments();
                }

            }
        });

        if(shownHeadCommentTools==false)
        {
            showHeadCommentTools();
        }

    }

    private void showHeadCommentTools()
    {

        if(expand_collapse!=null)
        {



            head_comment_textView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {



                            if(shownHeadCommentTools==false)
                            {
                                Log.i("showHeadCommentTools","if(expand_collapse!=null) "+expand_collapse.isShown()+" "+expand_collapse.getVisibility());
                                if(expand_collapse.isShown())
                                {

                                    new TapTargetSequence(currentTopicFragment.getActivity())
                                            .targets(
                                                    TapTarget.forView(expand_collapse, "Read More", "To read more of the current head comment, tap"),
                                                    TapTarget.forView(view_more_headtopics,"View More Head Comments","View more head comments/topics that have comments under them"))
                                            .listener(new TapTargetSequence.Listener() {
                                                @Override
                                                public void onSequenceFinish() {
                                                    shownHeadCommentTools=true;
                                                }

                                                @Override
                                                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                                                }

                                                @Override
                                                public void onSequenceCanceled(TapTarget lastTarget) {

                                                }
                                            }).start();
                                }
                                else
                                {
                                    new TapTargetSequence(currentTopicFragment.getActivity())
                                            .targets(
                                                    TapTarget.forView(view_more_headtopics,"View More Head Comments","View more head comments/topics that have comments under them"))
                                            .listener(new TapTargetSequence.Listener() {
                                                @Override
                                                public void onSequenceFinish() {
                                                    shownHeadCommentTools=true;
                                                }

                                                @Override
                                                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                                                }

                                                @Override
                                                public void onSequenceCanceled(TapTarget lastTarget) {

                                                }
                                            }).start();
                                }
                            }
                            shownHeadCommentTools=true;

                        }
                    });

        }
        else
        {
            Log.i("showHeadCommentTools","if(expand_collapse==null)");
        }

    }

    private void listenForHeadCommentWhileNoHeadComment(CommentWorker commentWorker)
    {

        Query query=getHeadCommentQuery(currentTopicForConvo);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value.isEmpty()==false)
                {

                    DocumentSnapshot documentSnapshot =value.getDocuments().get(0);
                    Comment comment=new Comment(documentSnapshot);
                    showHeadComment(comment,commentWorker);

                }

            }
        });


    }



}
