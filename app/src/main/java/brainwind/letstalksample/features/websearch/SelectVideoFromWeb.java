package brainwind.letstalksample.features.websearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import brainwind.letstalksample.R;

public class SelectVideoFromWeb extends AppCompatActivity {

    public static final String ANNOUNCEMENT_ID = "ANNOUNCEMENT_ID";
    public static final String HAVE_UR_SAY_ID = "HAVE_UR_SAY_ID";
    public static final String TITLE = "TITLE";
    public static final String EVENT_ID = "EVENT_ID";
    public static final String ARTICLE_ID = "ARTICLE_ID";
    public static final String PROJECT_ID = "PROJECT_ID";
    public static final String LETS_TALK_ID = "LETS_TALK_ID";
    public static final String SURVEY_ID = "SURVEY_ID";

    private static final String LOOKING_FOR_RESULTS = "LOOKING_FOR_RESULTS";
    private static final String PAGING = "PAGING";

    public static final String UID = "UID";
    public static final String ORG_ID = "ORG_ID";
    public static final String PURPOSE = "PURPOSE";


    int purpose=-1;
    public static final int PICK_ORG_LOGO = 0;
    public static final int PICK_FOR_HAVE_YOUR_SAY = 1;
    public static final int PICK_FOR_ARTICLE = 2;
    public static final int PICK_USER_PROFILE_IMAGE = 3;
    public static final int PICK_FOR_LETS_TALK = 4;
    public static final int PICK_FOR_EVENT = 5;
    public static final int PICK_FOR_PROJECT = 6;
    public static final int PICK_FOR_ANNOUNCEMENT = 7;
    public static final int PICK_FOR_PARTNERSHIP = 8;
    public static final int PICK_FOR_INTEREST = 9;
    public static final int PICK_FOR_SURVEY = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video_from_web);




    }



}