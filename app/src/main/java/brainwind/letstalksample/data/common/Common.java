package brainwind.letstalksample.data.common;

import static brainwind.letstalksample.data.checks.CheckVars.CAPTURED_USER_PROFILE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avatarfirst.avatargenlib.AvatarGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import brainwind.letstalksample.CaptureInfo1;
import brainwind.letstalksample.MainActivity;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.org.Org;
import brainwind.letstalksample.data.database.user.activity.UserActivity;
import brainwind.letstalksample.data.database.user.activity.UserActivityDao;
import brainwind.letstalksample.data.database.user.activity.UserActivityDatabase;
import brainwind.letstalksample.data.database.user.activity.UserActivityTypes;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.memory.Memory;
import de.hdodenhof.circleimageview.CircleImageView;


public class Common
{

    public static final int GETTING_ORG=0;
    public static final int GETTING_ANNOUNCEMENT=1;
    public static final int GETTING_ARTICLE=2;
    public static final int GETTING_EVENT=3;
    public static final int GETTING_HAVE_YOUR_SAY=4;
    public static final int GETTING_LETS_TALK=5;
    public static final int GETTING_ORG_INTEREST=6;
    public static final int GETTING_PARTNERSHIP=7;
    public static final int GETTING_PROJECT=8;
    public static final int GETTING_SURVEY = 9;


    public static void IfUserNotSignedIn(Activity activity)
    {

        //If the user has not signed in
        //leads to  SignInActivity if not signed in
        //or closes the activity if user opts to not sign in
        new MaterialDialog.Builder(activity)
                .title("Pleas Sign In First")
                .content("Please Sign In first in order to create an organization")
                .positiveText("Sign In")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Intent intent=new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);

                    }
                })
                .negativeText("Nope")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        activity.finish();

                    }
                })
                .cancelable(false)
                .show();


    }

    public static void CheckIfCaptureNames(Activity activity)
    {


        Memory memory=new Memory(activity);
        Log.i("kisdjla",memory.getBoolean(CAPTURED_USER_PROFILE)+"");

        if(memory.getBoolean(CAPTURED_USER_PROFILE)==false)
        {
            new MaterialDialog.Builder(activity)
                    .title("Sorry we need your names")
                    .content("We need to capture your names before you create an organization")
                    .positiveText("Okay")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            UserActivityDatabase.databaseWriteExecutor.execute(new Runnable() {
                                @Override
                                public void run() {

                                    UserActivityDao userActivityDao=UserActivityDatabase
                                            .getDatabase(activity).userActivityDao();

                                    UserActivity jk=userActivityDao.getLastUserActivityOfType(UserActivityTypes.CREATE_ORG);

                                    if(jk==null)
                                    {

                                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                                        UserActivity userActivity=new UserActivity();
                                        userActivity.actvity_type= UserActivityTypes.CREATE_ORG;
                                        userActivity.title="Creating an Organization but had to finish account set up";
                                        userActivity.uid=firebaseUser.getUid();
                                        userActivityDao.insertAll(userActivity);

                                    }

                                    Intent intent=new Intent(activity,
                                            CaptureInfo1.class);
                                    activity.startActivity(intent);

                                }
                            });


                        }
                    })
                    .cancelable(false)
                    .show();
        }


    }

    public static void DecodeOrg(Org org, DocumentSnapshot documentSnapshot)
    {

        if(org==null)
        {
            org=new Org();
        }
        if(org!=null&documentSnapshot!=null)
        {

            org.orgid=documentSnapshot.getId();
            if(documentSnapshot.contains(OrgFields.ORG_NAME))
            {
                org.OrgName=documentSnapshot.getString(OrgFields.ORG_NAME);
            }
            if(documentSnapshot.contains(OrgFields.ORG_DESC))
            {
                org.OrgDesc=documentSnapshot.getString(OrgFields.ORG_DESC);
            }
            if(documentSnapshot.contains(OrgFields.ORG_CATEGORY))
            {
                org.OrgCategory=documentSnapshot.getString(OrgFields.ORG_CATEGORY);
            }
            if(documentSnapshot.contains(OrgFields.ORG_SCOPE))
            {
                org.OrgScope=documentSnapshot.getString(OrgFields.ORG_SCOPE);
            }
            if(documentSnapshot.contains(OrgFields.ORG_COUNTRY_CODE))
            {
                org.OrgCountryCode=documentSnapshot.getString(OrgFields.ORG_COUNTRY_CODE);
            }
            if(documentSnapshot.contains(OrgFields.ORG_COUNTRY))
            {
                org.OrgCountry=documentSnapshot.getString(OrgFields.ORG_COUNTRY);
            }
            if(documentSnapshot.contains(OrgFields.ORG_MAIN_COLOR))
            {
                org.OrgMainColor=documentSnapshot.getString(OrgFields.ORG_MAIN_COLOR);
            }
            if(documentSnapshot.contains(OrgFields.ORG_LOCAL_PATH))
            {
                org.LocalLogoPath=documentSnapshot.getString(OrgFields.ORG_LOCAL_PATH);
            }
            if(documentSnapshot.contains(OrgFields.ORG_ONLINE_PATH))
            {
                org.OnlineLogoPath=documentSnapshot.getString(OrgFields.ORG_ONLINE_PATH);
            }
            if(documentSnapshot.contains(OrgFields.ORG_HAS_OTHER_PARTS_IN_OTHER_AREAS))
            {
                org.HasOtherPartsInOtherAreas=documentSnapshot.getBoolean(OrgFields.ORG_HAS_OTHER_PARTS_IN_OTHER_AREAS);
            }
            if(documentSnapshot.contains(OrgFields.USE_EXTERNAL_PROFILE_IMAGE))
            {
                org.user_external_profile_image=documentSnapshot.getBoolean(OrgFields.USE_EXTERNAL_PROFILE_IMAGE);
            }

        }

    }

    public static void DecodeUser(Activity activity,UserInfo userInfo,DocumentSnapshot documentSnapshot)
    {


        if(documentSnapshot.contains(OrgFields.USER_FIRSTNAME))
        {
            userInfo.firstname=documentSnapshot.getString(OrgFields.USER_FIRSTNAME);
        }
        if(documentSnapshot.contains(OrgFields.USER_LASTNAME))
        {
            userInfo.lastname=documentSnapshot.getString(OrgFields.USER_LASTNAME);
        }
        if(documentSnapshot.contains(OrgFields.USER_PROFILE_PICTURE_LOCAL))
        {
            userInfo.profile_local_path=documentSnapshot.getString(OrgFields.USER_PROFILE_PICTURE_LOCAL);
        }
        if(documentSnapshot.contains(OrgFields.USER_PROFILE_PICTURE))
        {
            userInfo.profile_path=documentSnapshot.getString(OrgFields.USER_PROFILE_PICTURE);
        }

        if(documentSnapshot.contains(OrgFields.USER_COUNTRY_CODE))
        {
            userInfo.country_code=Integer.parseInt(documentSnapshot.getString(OrgFields.USER_COUNTRY_CODE));
        }
        if(documentSnapshot.contains(OrgFields.USER_CELLPHONE))
        {
            userInfo.cellphone=documentSnapshot.getString(OrgFields.USER_CELLPHONE);
        }
        if(documentSnapshot.contains(OrgFields.USER_UID))
        {
            userInfo.uid=documentSnapshot.getString(OrgFields.USER_UID);
        }
        else
        {
            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null)
            {
                userInfo.uid=firebaseUser.getUid();
            }
        }
        if(documentSnapshot.contains(OrgFields.USE_EXTERNAL_PROFILE_IMAGE))
        {
            userInfo.user_external_profile_image=documentSnapshot.getBoolean(OrgFields.USE_EXTERNAL_PROFILE_IMAGE);
        }

    }


}
