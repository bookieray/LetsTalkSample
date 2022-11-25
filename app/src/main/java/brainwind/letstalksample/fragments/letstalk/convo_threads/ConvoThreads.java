package brainwind.letstalksample.fragments.letstalk.convo_threads;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This will list the main conversation threads linked to this conversation
 */
public class ConvoThreads extends Fragment {

    @BindView(R.id.convo_threads)
    RecyclerView convo_threads;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONVERSATION_ID = "con_id";
    private static final String IS_STANDALONE = "is_standalone";


    // TODO: Rename and change types of parameters
    private String conversation_id;
    private boolean is_standalone=true;
    private ThreadAdapter threadadapter;
    private boolean finished_fecth_head_comment=true;

    public ConvoThreads() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param conversation_id Parameter 1.
     * @return A new instance of fragment ConvoThreads.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvoThreads newInstance(String conversation_id,boolean is_standalone) {
        ConvoThreads fragment = new ConvoThreads();
        Bundle args = new Bundle();
        args.putString(CONVERSATION_ID, conversation_id);
        args.putBoolean(IS_STANDALONE, is_standalone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            conversation_id = getArguments().getString(CONVERSATION_ID);
            is_standalone = getArguments().getBoolean(IS_STANDALONE);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_convo_threads, container, false);
        ButterKnife.bind(this,view);
        getConVosThreads();
        convo_threads.setLayoutManager(new LinearLayoutManager(getActivity()));
        threadadapter=new ThreadAdapter();
        convo_threads.setAdapter(threadadapter);
        return view;
    }

    private void getConVosThreads()
    {

        Query query=null;
        if(this.is_standalone)
        {
            query= CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.CONVERSATION_ID,conversation_id)
                    .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                    .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                    .limit(10);
        }
        else
        {
            query=CloudWorker.getLetsTalkComments()
                    .whereEqualTo(OrgFields.ACTIVITY_ID,conversation_id)
                    .whereEqualTo(OrgFields.IS_NEW_TOPIC,true)
                    .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING)
                    .orderBy(OrgFields.NUM_COMMENTS, Query.Direction.DESCENDING)
                    .limit(10);
        }

        query.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.i("get_head_coms","e="+e.getMessage());

                        finished_fecth_head_comment=false;


                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(finished_fecth_head_comment)
                        {
                            Log.i("get_head_coms","successful isEmpty="+queryDocumentSnapshots.isEmpty());
                            if(queryDocumentSnapshots.isEmpty())
                            {
                                Log.i("get_head_coms","conversation_id="+conversation_id
                                        +" is_standalone="+is_standalone);
                            }
                            else
                            {

                                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                                {

                                    Comment comment=new Comment(documentSnapshot);
                                    threads.add(comment);

                                }

                                threadadapter.notifyDataSetChanged();

                            }
                        }


                    }
                });

    }

    List<Comment> threads=new ArrayList<Comment>();
    class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadHolder>
    {


        @NonNull
        @Override
        public ThreadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.head_comment_view,parent,false);
            return new ThreadHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ThreadHolder holder, int position) {

            final Comment comment=threads.get(position);

            holder.head_comment_view.setVisibility(View.VISIBLE);
            holder.head_comment_name.setText(comment.getCommentator_name());
            if(comment.getComment_type()==Comment.QOUTE)
            {
                holder.comment_type.setText("Qouted");
                if(comment.getNewsSource().isEmpty()==false)
                {
                    holder.comment_type.setText("Qouted from "+comment.getNewsSource());
                }
            }
            if(comment.getComment_type()==Comment.AGREES)
            {
                holder.comment_type.setText("Agrees");
            }
            if(comment.getComment_type()==Comment.DISAGREES)
            {
                holder.comment_type.setText("Disagrees");
            }
            if(comment.getComment_type()==Comment.QUESTION)
            {
                holder.comment_type.setText("Question");
            }

            if(comment.getComment_type()==Comment.QOUTE)
            {

                holder.head_comment.setText(Html.fromHtml(
                        '"'+comment.getComment()+'"'));

            }
            else
            {
                holder.head_comment.setText(comment.getComment());
            }


            holder.num_comments_view.setText(comment.getNum_comments()+" comments");

            Glide.with(getActivity()).clear(holder.head_profile_image);
            Drawable placeholder= new AvatarGenerator.AvatarBuilder(getActivity())
                    .setLabel(comment.getCommentator_name())
                    .setAvatarSize(120)
                    .setTextSize(30)
                    .toCircle()
                    .setBackgroundColor(getResources().getColor(R.color.purple_700))
                    .build();

            Glide.with(getActivity())
                    .load(comment.getCommentator_profile_path())
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(holder.head_profile_image);

            holder.rootview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CommentListener commentListener=(CommentListener) getActivity();
                    if(commentListener!=null)
                    {
                        commentListener.foundHeadComment(comment);
                    }

                }
            });

            holder.head_comment.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {

                    CommentListener commentListener=(CommentListener) getActivity();
                    if(commentListener!=null)
                    {
                        commentListener.foundHeadComment(comment);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return threads.size();
        }

        public class ThreadHolder extends RecyclerView.ViewHolder {

            CardView rootview;
            LinearLayout head_comment_view;
            CircleImageView head_profile_image;
            TextView head_comment_name;
            TextView comment_type;
            TextView num_comments_view;
            ExpandableTextView head_comment;

            public ThreadHolder(@NonNull View itemView) {
                super(itemView);

                rootview=(CardView) itemView.findViewById(R.id.head_comment_parent_view);
                head_comment_view=(LinearLayout) itemView.findViewById(R.id.head_comment_view);
                head_profile_image=(CircleImageView) itemView.findViewById(R.id.head_profile_image);
                head_comment_name=(TextView) itemView.findViewById(R.id.head_comment_name);
                comment_type=(TextView) itemView.findViewById(R.id.comment_type);
                num_comments_view=(TextView) itemView.findViewById(R.id.num_comments_view);
                head_comment=(ExpandableTextView) itemView.findViewById(R.id.head_comment);

            }


        }


    }

    @Override
    public void onResume() {
        super.onResume();

        threadadapter.notifyDataSetChanged();

    }


}