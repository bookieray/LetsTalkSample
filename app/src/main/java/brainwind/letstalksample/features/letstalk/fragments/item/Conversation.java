package brainwind.letstalksample.features.letstalk.fragments.item;

import com.google.firebase.firestore.DocumentSnapshot;

import brainwind.letstalksample.bookie_activity.BookieActivity;
import brainwind.letstalksample.data.database.OrgFields;

public class Conversation extends BookieActivity
{

    private int num_comments=0;

    public Conversation(DocumentSnapshot documentSnapshot) {
        super(documentSnapshot);

        if(documentSnapshot!=null)
        {

            if(documentSnapshot.contains(OrgFields.NUM_COMMENTS))
            {
                this.num_comments=documentSnapshot.getLong(OrgFields.NUM_COMMENTS).intValue();
            }

        }

    }

    public int getNum_comments() {
        return num_comments;
    }


}
