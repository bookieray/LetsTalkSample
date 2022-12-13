package brainwind.letstalksample.features.letstalk.fragments.workers.headcomments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.CaseMap;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurivanets.bottomsheets.BaseBottomSheet;
import com.arthurivanets.bottomsheets.BottomSheet;
import com.arthurivanets.bottomsheets.config.BaseConfig;
import com.arthurivanets.bottomsheets.config.Config;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.utilities.NumUtils;
import brainwind.letstalksample.features.letstalk.fragments.CurrentTopicForConvo;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentCommunications;
import brainwind.letstalksample.features.letstalk.fragments.workers.CommentWorker;
import brainwind.letstalksample.features.letstalk.fragments.workers.QueryWorker;
import de.hdodenhof.circleimageview.CircleImageView;

public class HeadCommentWorker extends QueryWorker
{

    CommentWorker commentWorker;
    CurrentTopicForConvo currentTopicForConvo;

    public HeadCommentWorker(CommentWorker commentWorker)
    {
        super(commentWorker.getConversation_id(),commentWorker.getCurrentTopicFragment());
        this.commentWorker=commentWorker;
        this.currentTopicForConvo=(CurrentTopicForConvo) commentWorker.getCurrentTopicFragment();
    }

    HeadCommentAdapter headCommentAdapter;
    FloatingActionButton close_button_media_dialog;
    SimpleCustomBottomSheet bottomSheet;
    ArrayList<Comment> headcommentsList=new ArrayList<Comment>();
    Comment afterComment=null;
    private RecyclerView media_list;
    private TextView convo_title;
    boolean setsc=false;
    HashMap<String,Comment> processed_comments=new HashMap<String,Comment>();
    public void LoadMoreHeadComments()
    {


        bottomSheet = new SimpleCustomBottomSheet(commentWorker.getCurrentTopicFragment().getActivity());
        bottomSheet.show();

        close_button_media_dialog=(FloatingActionButton)bottomSheet.getRootView().findViewById(R.id.close_button_media_dialog);
        convo_title=(TextView)bottomSheet.getRootView().findViewById(R.id.convo_title);
        String title=commentWorker.getTitle();
        convo_title.setText(title);
        media_list=(RecyclerView) bottomSheet.getRootView().findViewById(R.id.media_list);
        headCommentAdapter=new HeadCommentAdapter();
        media_list.setLayoutManager(new LinearLayoutManager(currentTopicForConvo.getActivity()));
        media_list.setAdapter(headCommentAdapter);
        close_button_media_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.dismiss();
            }
        });

        Query query=getHeadCommentsQuery(this.currentTopicForConvo);
        if(afterComment!=null)
        {
            query=getHeadCommentsQuery(this.currentTopicForConvo,afterComment);
        }
        query.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("LoadMoreHeadComments","e="+e.getMessage());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Log.i("LoadMoreHeadComments","successful isEmpty="+queryDocumentSnapshots.isEmpty());
                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            Comment comment=new Comment(documentSnapshot);
                            if(processed_comments.containsKey(comment.getComment_id())==false)
                            {
                                headcommentsList.add(comment);
                                processed_comments.put(comment.getComment_id(),comment);
                            }
                            else
                            {
                                continue;
                            }

                        }


                        if(queryDocumentSnapshots.getDocuments().size()<10)
                        {
                            headCommentAdapter.no_more=true;
                            headCommentAdapter.notifyDataSetChanged();
                        }
                        else
                        {

                            headCommentAdapter.notifyDataSetChanged();

                        }

                        if(afterComment==null)
                        {
                            tellHowToExit();
                        }

                        if(setsc==false&queryDocumentSnapshots.isEmpty()==false)
                        {

                            setUpScrollListener();
                            setsc=true;
                        }
                        afterComment=null;

                    }
                });


    }

    boolean shownjk=false;
    private void tellHowToExit()
    {

        if(shownjk==false)
        {

            shownjk=true;
            new TapTargetSequence(currentTopicForConvo.getActivity())
                    .targets(
                            TapTarget.forView(close_button_media_dialog, "To Close head comments", "To close the head comments dialog to get back to convo"))
                    .listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {

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

    private void setUpScrollListener()
    {

        media_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager linearLayout=(LinearLayoutManager) media_list.getLayoutManager();
                int last=linearLayout.findLastVisibleItemPosition();
                if(newState==RecyclerView.SCROLL_STATE_IDLE&headCommentAdapter.no_more==false
                        &last==headcommentsList.size()-1)
                {

                    if(headcommentsList.size()-2>=0&headcommentsList.size()>=10)
                    {
                        afterComment=headcommentsList.get(headcommentsList.size()-2);
                        LoadMoreHeadComments();
                    }

                }

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
            View view= LayoutInflater.from(context).inflate(
                    R.layout.view_simple_custom_bottom_sheet,
                    this,
                    false
            );

            return view;
        }

    }

    class HeadCommentAdapter extends RecyclerView.Adapter<HeadCommentHolder>
    {

        public boolean no_more=false;
        @NonNull
        @Override
        public HeadCommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType!=-1)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.head_comment_list_item,parent,false);
                return new HeadCommentHolder(view);
            }
            else
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list,parent,false);
                return new HeadCommentHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull HeadCommentHolder holder, int position) {

            if(getItemViewType(position)!=-1)
            {
                final Comment comment=headcommentsList.get(position);
                holder.head_comment_textView.setText(comment.getComment());
                String firstname="";
                if(comment.getCommentator_name().trim().isEmpty()==false)
                {
                    String headCommentName=comment.getCommentator_name();
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
                            android.util.Log.i("uksidla","jn="+jn);
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
                    holder.head_comment_name.setText(headCommentName);
                    android.util.Log.i("muhgsfa","headCommentName="+headCommentName);
                }

                if(comment.isHasNewsReference())
                {
                    holder.status.setText("Based on source:"+comment.getNewsSource());
                    holder.status.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.green));
                    holder.head_comment_textView.setText(comment.getComment());
                }
                else
                {
                    holder.status.setText("No source");
                    holder.status.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.red));

                }

                Glide.with(holder.head_profile_image.getContext()).clear(holder.head_profile_image);
                //show the user's profile image
                Drawable placeholder = new AvatarGenerator.AvatarBuilder(currentTopicForConvo.getActivity())
                        .setLabel(comment.getCommentator_name())
                        .setAvatarSize(120)
                        .setTextSize(30)
                        .toCircle()
                        .setBackgroundColor(currentTopicForConvo.getActivity().
                                getResources().getColor(R.color.purple_700))
                        .build();

                if(comment.getCommentator_profile_path()!=null)
                {
                    Glide.with(holder.head_profile_image.getContext())
                            .load(Uri.parse(comment.getCommentator_profile_path()))
                            .placeholder(placeholder)
                            .centerCrop()
                            .error(placeholder)
                            .into(holder.head_profile_image);
                }
                else
                {
                    Glide.with(holder.head_profile_image.getContext())
                            .load(placeholder)
                            .placeholder(placeholder)
                            .centerCrop()
                            .error(placeholder)
                            .into(holder.head_profile_image);
                }

                if(comment.getComment_type()==Comment.AGREES)
                {
                    holder.head_comment_type.setText("Agrees with Convo Title");
                    holder.head_comment_type.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.purple_700));
                }
                if(comment.getComment_type()==Comment.DISAGREES)
                {
                    holder.head_comment_type.setText("Disagrees with Convo Title");
                    holder.head_comment_type.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.red));
                }
                if(comment.getComment_type()==Comment.QUESTION)
                {
                    holder.head_comment_type.setText("Questions the Convo Title");
                    holder.head_comment_type.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.blue));
                }
                if(comment.getComment_type()==Comment.ANSWER)
                {
                    holder.head_comment_type.setText("Answers the Convo Title");
                    holder.head_comment_type.setTextColor(holder.head_comment_textView.getContext().getResources().getColor(R.color.blue));
                }

                holder.num_comments_view.setText(NumUtils.getAbbreviatedNum(comment.getNum_comments())+" comments");

                holder.select_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i("sddahssda","1 tapped");
                        CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                        if(commentCommunications!=null)
                        {
                            commentCommunications.ChangeHeadComment(comment);
                            bottomSheet.dismiss();
                        }

                    }
                });
                holder.head_comment_parent_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Log.i("sddahssda","1 tapped");
                        CommentCommunications commentCommunications=(CommentCommunications) commentWorker;
                        if(commentCommunications!=null)
                        {
                            commentCommunications.ChangeHeadComment(comment);
                            bottomSheet.dismiss();
                        }

                    }
                });



            }
            else
            {
                holder.loading_desc.setText("Search for more comments");

            }

        }

        @Override
        public int getItemCount() {
            if(no_more==false)
            {
                return headcommentsList.size()+1;
            }
            else
            {
                return headcommentsList.size();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position<headcommentsList.size())
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
    }

    private class HeadCommentHolder extends RecyclerView.ViewHolder {


        public MaterialCardView head_comment_parent_view;
        //loading
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
        public MaterialButton select_button;

        public TextView loading_desc;
        public ProgressBar progress_bar_loading;

        public HeadCommentHolder(@NonNull View itemView) {
            super(itemView);


            //head comment views
            //loading
            this.progress_bar_loading_head_comment = itemView
                    .findViewById(R.id.progress_bar_loading_head_comment);
            //After loaded the head comment
            //There is a head comment
            this.head_comment_parent_view = itemView
                    .findViewById(R.id.head_comment_parent_view);
            this.head_comment_view = itemView
                    .findViewById(R.id.head_comment_view);
            this.head_profile_image = itemView
                    .findViewById(R.id.head_profile_image);
            this.head_comment_name = itemView
                    .findViewById(R.id.head_comment_name);
            this.head_comment_type = itemView
                    .findViewById(R.id.comment_type);
            this.head_comment_textView = itemView
                    .findViewById(R.id.head_comment);
            if(this.head_comment_textView!=null)
            {
                this.expand_collapse=this.head_comment_textView.findViewById(com.ms.square.android.expandabletextview.R.id.expand_collapse);
            }
            this.num_comments_view = itemView
                    .findViewById(R.id.num_comments_view);
            this.status = itemView
                    .findViewById(R.id.status);
            this.view_more_headtopics=itemView.findViewById(R.id.view_more_headtopics);
            this.loading_desc=itemView.findViewById(R.id.loading_desc);
            this.progress_bar_loading=itemView.findViewById(R.id.progress_bar_loading);

            if(head_comment_view!=null)
            {
                head_comment_view.setVisibility(View.VISIBLE);
            }
            this.select_button=itemView.findViewById(R.id.select_button);

        }


    }


}
