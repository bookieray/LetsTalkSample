package brainwind.letstalksample;


import static brainwind.letstalksample.data.checks.CheckVars.CAPTURED_USER_PROFILE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import brainwind.letstalksample.data.checks.CheckVars;
import brainwind.letstalksample.data.common.Common;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgCollections;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.activity.UserActivity;
import brainwind.letstalksample.data.database.user.activity.UserActivityDao;
import brainwind.letstalksample.data.database.user.activity.UserActivityDatabase;
import brainwind.letstalksample.data.database.user.user_info.UserInfo;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDao;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.features.letstalk.LetsTalkManageEach;
import brainwind.letstalksample.forms.general.SelectCountry;
import brainwind.letstalksample.forms.user.FirstName;
import brainwind.letstalksample.forms.user.LastName;
import brainwind.letstalksample.forms.user.UserCellphoneNumber;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;


public class CaptureInfo1 extends AppCompatActivity implements StepperFormListener {

    @BindView(R.id.stepper_form)
    VerticalStepperFormView stepperFormView;

    private FirebaseUser firebaseUser;
    private FirstName firstName;
    private LastName lastName;
    private SelectCountry selectCountry;
    private UserCellphoneNumber userCellphoneNumber;

    private UserInfo userInfo;
    private MaterialDialog please_wait;
    private boolean user_document_exists_online=true;
    private boolean created_when_created=true;
    private boolean finished=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_info1);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        ButterKnife.bind(this);

        //If the user has not signed in
        //leads to  SignInActivity if not signed in
        //or closes the activity if user opts to not sign in
        if(firebaseUser==null)
        {
            Common.IfUserNotSignedIn(this);

        }
        //the user is Signed In
        else
        {

            //Steps to be filled In
            firstName=new FirstName("Your FirstName","eg, Ray");
            lastName=new LastName("Your LastName","eg, Mdunge");
            selectCountry=new SelectCountry(this,"Select Your Country","Please select correct country");
            userCellphoneNumber=new UserCellphoneNumber();
            // Find the form view, set it up and initialize it.
            stepperFormView
                    .setup(this, firstName, lastName,selectCountry,userCellphoneNumber)
                    .init();

            //Getting the user's previous submitted information from the local database
            UserInfoDatabase.databaseWriteExecutor
                    .execute(new Runnable() {
                        @Override
                        public void run() {


                            UserInfoDao userInfoDao= UserInfoDatabase.getDatabase(CaptureInfo1.this).userInfoDao();
                            userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                            if(userInfo!=null)
                            {

                                Memory memory=new Memory(CaptureInfo1.this);
                                memory.Save(CAPTURED_USER_PROFILE,true);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //Setting Form Steps so the User can edit their current data
                                        firstName.setFirstName(userInfo.firstname);
                                        lastName.setLastName(userInfo.lastname);
                                        selectCountry.setCountryCode(userInfo.country_code);
                                        userCellphoneNumber.setCellphone(userInfo.cellphone);

                                    }
                                });

                            }


                        }
                    });


        }

        //if the user has never inputed their information, check on the online database
        //to avoid assuming they have never save data previously
        Memory memory=new Memory(this);
        if(memory.getBoolean(CAPTURED_USER_PROFILE)==false&firebaseUser!=null)
        {

            downloadUserDocument();

        }



    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(stepperFormView!=null)
        {

            if(stepperFormView.getTotalNumberOfSteps()>0)
            {

                stepperFormView.cancelFormCompletionOrCancellationAttempt();
                String txt="";
                if(finished)
                {

                    txt="You were here and finished updating your account profile.";
                    new MaterialDialog.Builder(this)
                            .title("You Back here")
                            .content(txt)
                            .cancelable(false)
                            .positiveText("Explore")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    Intent intent=new Intent(CaptureInfo1.this,LetsTalkManageEach.class);
                                    startActivity(intent);

                                }
                            })
                            .negativeText("Stay")
                            .show();
                }
                else
                {
                    txt="You were here and you did not make any update.";
                    new MaterialDialog.Builder(this)
                            .title("You Back here")
                            .content(txt)
                            .cancelable(false)
                            .positiveText("Explore")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {

                                    Intent intent=new Intent(CaptureInfo1.this,LetsTalkManageEach.class);
                                    startActivity(intent);

                                }
                            })
                            .negativeText("Stay")
                            .show();
                }


            }

        }

    }

    private void downloadUserDocument()
    {

        please_wait=new MaterialDialog.Builder(this)
                .title("Please wait")
                .content("we checking for your information if you have previously captured user information")
                .cancelable(false)
                .show();

        CloudWorker.getUserDocument()
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if(please_wait!=null)
                        {
                            please_wait.dismiss();
                        }

                        Log.i("lxksgdha","e="+e.getMessage());
                        please_wait=new MaterialDialog.Builder(CaptureInfo1.this)
                                .title("Oops something went wrong")
                                .content(e.getMessage()+". We need to perform a compulsory user info check." +
                                        "")
                                .cancelable(false)
                                .positiveText("Try again")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        downloadUserDocument();

                                    }
                                })
                                .negativeText("Nope")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        finish();

                                    }
                                })
                                .show();

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.i("lxksgdha","exists="+documentSnapshot.exists());
                        Memory memory=new Memory(CaptureInfo1.this);
                        memory.Save(CAPTURED_USER_PROFILE,true);

                        if(documentSnapshot.exists())
                        {

                            if(documentSnapshot.contains(OrgFields.USER_CREATED_DATE))
                            {
                                created_when_created=true;
                            }
                            else
                            {
                                created_when_created=false;
                            }

                            user_document_exists_online=true;
                            UserInfoDatabase.databaseWriteExecutor
                                    .execute(new Runnable() {
                                        @Override
                                        public void run() {


                                            UserInfoDao userInfoDao=UserInfoDatabase.getDatabase(CaptureInfo1.this)
                                                    .userInfoDao();

                                            userInfo=new UserInfo();
                                            Common.DecodeUser(CaptureInfo1.this,userInfo,documentSnapshot);
                                            userInfoDao.insertAll(userInfo);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    //fill in Form Steps after getting the user's online document
                                                    if(userInfo!=null)
                                                    {
                                                        if(userInfo.firstname!=null)
                                                        {
                                                            firstName.setFirstName(userInfo.firstname);
                                                        }
                                                        if(userInfo.lastname!=null)
                                                        {
                                                            lastName.setLastName(userInfo.lastname);
                                                        }
                                                        selectCountry.setCountryCode(userInfo.country_code);
                                                        if(userInfo.cellphone!=null)
                                                        {
                                                            userCellphoneNumber.setCellphone(userInfo.cellphone);
                                                        }
                                                    }
                                                    if(please_wait!=null)
                                                    {
                                                        please_wait.dismiss();
                                                    }

                                                }
                                            });

                                        }
                                    });

                        }
                        else {
                            user_document_exists_online=false;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }



                                }
                            });

                        }

                    }
                });


    }

    @Override
    public void onCompletedForm() {

        createUser();

    }

    private void createUser()
    {

        Memory memory=new Memory(this);
        if(memory.getBoolean(CheckVars.NOTIFIED_USER_CELLPHONE_EMAIL)==false)
        {

            new MaterialDialog.Builder(this)
                    .title("One more thing")
                    .content("Your email or Cellphone is not visible to any organization " +
                            "but when you join them, " +
                            "they can send you sms messages or emails notifications to you until you switch it off " +
                            "but they cannot record this contact information themselves nor view it.")
                    .cancelable(false)
                    .positiveText("Okay")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog,
                                            @NonNull DialogAction which) {

                            Crt();

                        }
                    })
                    .show();

        }
        else
        {
            Crt();
        }



    }

    private void Crt()
    {

        //Save online and then save locally
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user
        Map<String, Object> user_object = new HashMap<>();
        user_object.put(OrgFields.USER_UID,firebaseUser.getUid());
        user_object.put(OrgFields.USER_FIRSTNAME,firstName.getStepData());
        user_object.put(OrgFields.USER_LASTNAME,lastName.getStepData());
        user_object.put(OrgFields.USER_COUNTRY_CODE,selectCountry.getStepData());
        user_object.put(OrgFields.USER_COUNTRY_NAME,selectCountry.getStepDataAsHumanReadableString());
        user_object.put(OrgFields.USER_CELLPHONE,userCellphoneNumber.getStepData());
        if(userInfo==null)
        {
            user_object.put(OrgFields.USE_EXTERNAL_PROFILE_IMAGE,false);
        }

        if(user_document_exists_online&created_when_created)
        {
            user_object.put(OrgFields.USER_MODIFIED_DATE, FieldValue.serverTimestamp());
        }
        else
        {
            user_object.put(OrgFields.USER_CREATED_DATE, FieldValue.serverTimestamp());
            user_object.put(OrgFields.USER_MODIFIED_DATE, FieldValue.serverTimestamp());
        }

        db.collection(OrgCollections.USERS)
                .document(firebaseUser.getUid())
                .set(user_object, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        finished=true;
                        Memory memory=new Memory(CaptureInfo1.this);
                        memory.Save("names_empty",false);
                        memory.Save("captured",true);
                        memory.Save("user_ref",firebaseUser.getUid());
                        memory.Save(CAPTURED_USER_PROFILE,true);

                        stepperFormView.cancelFormCompletionOrCancellationAttempt();

                        UserActivityDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {

                                UserActivityDao userActivityDao= UserActivityDatabase.getDatabase(CaptureInfo1.this)
                                        .userActivityDao();
                                UserActivity userActivity=userActivityDao.getLastUserActivity();

                                UserInfoDao userInfoDao= UserInfoDatabase.getDatabase(CaptureInfo1.this).userInfoDao();
                                UserInfo userInfo=userInfoDao.getUserInfo(firebaseUser.getUid());

                                //creating or updating the user object in the local database
                                if(userInfo!=null)
                                {

                                    userInfo.firstname=firstName.getStepData();
                                    userInfo.lastname=lastName.getStepData();
                                    userInfo.country_code=Integer.parseInt(selectCountry.getStepData());
                                    userInfo.cellphone= userCellphoneNumber.getStepData();
                                    userInfoDao.updateOrgs(userInfo);

                                }
                                else
                                {

                                    UserInfo userInfo1=new UserInfo();
                                    userInfo1.uid=firebaseUser.getUid();
                                    userInfo1.firstname=firstName.getStepData();
                                    userInfo1.lastname=lastName.getStepData();
                                    userInfo1.country_code=Integer.parseInt(selectCountry.getStepData());
                                    userInfo1.cellphone= userCellphoneNumber.getStepData();
                                    userInfoDao.insertAll(userInfo1);

                                }

                                stepperFormView.cancelFormCompletionOrCancellationAttempt();
                                Intent intent=new Intent(CaptureInfo1.this, LetsTalkManageEach.class);
                                startActivity(intent);


                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        String problem="error, "+e.getMessage()+". Please try again";
                        stepperFormView.cancelFormCompletionOrCancellationAttempt();

                        new MaterialDialog.Builder(CaptureInfo1.this)
                                .title("Oops, something went wrong")
                                .content(problem)
                                .positiveText("Try again")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        createUser();

                                    }
                                })
                                .negativeText("Nope")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        stepperFormView.cancelFormCompletionOrCancellationAttempt();

                                    }
                                })
                                .cancelable(false)
                                .show();

                    }
                });


    }

    @Override
    public void onCancelledForm() {

    }

    @OnClick(R.id.back_but)
    void GoBack()
    {
        finish();
    }


}