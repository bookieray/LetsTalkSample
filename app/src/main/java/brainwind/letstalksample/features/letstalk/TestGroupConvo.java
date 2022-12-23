package brainwind.letstalksample.features.letstalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

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

public class TestGroupConvo extends AppCompatActivity {

    @BindView(R.id.comment_list)
    RecyclerView comment_list;
    TestAdapter testAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_group_convo);
        ButterKnife.bind(this);
        testAdapter=new TestAdapter();
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(testAdapter);

        Intent intent=new Intent(this,TestGroup2.class);
        startActivity(intent);
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
                                    Log.i("addComments","1 yu="+yu);
                                    comment_list.smoothScrollToPosition(yu);

                                    new CountDownTimer(3000,1000) {
                                        @Override
                                        public void onTick(long l) {

                                        }

                                        @Override
                                        public void onFinish() {
                                            Log.i("addComments","2 yu="+yu);
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