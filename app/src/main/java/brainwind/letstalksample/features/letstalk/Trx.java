package brainwind.letstalksample.features.letstalk;

import com.google.firebase.firestore.Query;

public interface Trx
{

    void OndisableTypingArea(String message);
    void OnenableTypingArea(String message);

    void listenForQuery(Query query);

}
