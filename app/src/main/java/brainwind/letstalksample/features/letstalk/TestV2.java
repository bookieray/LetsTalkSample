package brainwind.letstalksample.features.letstalk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.features.letstalk.fragments.adapters.TestAdapter;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestV2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestV2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TestAdapter testAdapter;

    public TestV2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestV2.
     */
    // TODO: Rename and change types and number of parameters
    public static TestV2 newInstance(String param1, String param2) {
        TestV2 fragment = new TestV2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_v2, container, false);
    }

    @BindView(R.id.comment_list)
    RecyclerView comment_list;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
        testAdapter=new TestAdapter(getActivity());
        //comment_list.setLayoutManager(new LinearLayoutManager(getContext()));
        comment_list.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        comment_list.setAdapter(testAdapter);

        getComments();

    }

    private void getComments()
    {

        Query query= CloudWorker.getLetsTalkComments()
                .whereEqualTo(OrgFields.CONVERSATION_ID,"S3aNh6Jq7ZajBiJS2jut")
                .whereEqualTo(OrgFields.PARENT_COMMENT_ID,"mLpMm8yelTVTQaPjz75A")
                .whereEqualTo(OrgFields.DAY,19)
                .whereEqualTo(OrgFields.MONTH,12)
                .whereEqualTo(OrgFields.YEAR,2022)
                .orderBy(OrgFields.USER_CREATED_DATE, Query.Direction.DESCENDING);

        query.limit(10).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("getComments","onFailure "+e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Log.i("getComments","onSuccess "+queryDocumentSnapshots.isEmpty());
                        if(queryDocumentSnapshots.isEmpty()==false)
                        {

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

                                    new CountDownTimer(3000,1000) {
                                        @Override
                                        public void onTick(long l) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            Log.i("addCommentsX","2 yu="+yu);
                                            Comment mkl1=testAdapter.commentListUnderHeadComment.get(yu);
                                            mkl1.setSent(true);
                                            testAdapter.commentListUnderHeadComment.set(yu,mkl1);
                                            testAdapter.notifyItemChanged(yu);
                                        }
                                    }.start();

                                }
                            },200);

                        }
                        else
                        {

                        }

                    }
                });

    }



}