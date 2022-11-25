package brainwind.letstalksample.data.database;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

public class CloudWorker
{


    public static DocumentReference getUserDocument()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection(OrgCollections.USERS)
                .document(firebaseUser.getUid());
    }

    public static DocumentReference getUserDocumentForUser(String uid)
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection(OrgCollections.USERS)
                .document(uid);
    }

    public static CollectionReference getUserActivities()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("activities");
    }

    public static CollectionReference getUserInterests()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("interests");
    }

    public static CollectionReference getOrgInterests()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("org_interests");
    }

    public static CollectionReference getUserOrganizations()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("organizations");
    }

    public static CollectionReference getLetsTalk()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("letstalk");
    }

    public static CollectionReference getLetsTalkComments()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection(OrgCollections.LETSTALK_COMMENTS);
    }

    public static CollectionReference getUserOrganizationInterests()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("organizations_interests");
    }
    public static CollectionReference getUserHaveYourSays()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("have_your_says");
    }
    public static CollectionReference getUserProjects()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("projects");
    }
    public static CollectionReference getUserEvents()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("events");
    }
    public static CollectionReference getUserPartnerships()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("partnerships");
    }

    public static CollectionReference getUserMemberships()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("memberships");
    }

    public static CollectionReference getActivities()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        return db.collection("activities");

    }


    public static FirebaseFirestore getDatabase()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db;

    }

}
