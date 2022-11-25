package brainwind.letstalksample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import brainwind.letstalksample.utils.ImageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayVideoActivity extends YouTubeBaseActivity {


    public static final String URL = "URL";
    public static final String TITLE = "TITLE";

    @BindView(R.id.player_area)
    RelativeLayout player_area;
    @BindView(R.id.youtubeplayer)
    YouTubePlayerView youtubeplayer;
    private String url="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        ButterKnife.bind(this);

        if (getIntent().hasExtra(URL))
        {
            url=getIntent().getStringExtra(URL);
        }


        player_area.setVisibility(View.VISIBLE);
        YouTubePlayer.OnInitializedListener listener=new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer,
                                                boolean b) {

                String videoId= ImageUtil.getVideoId(url);
                youTubePlayer.loadVideo(videoId);
                youTubePlayer.play();
                Log.i("msjkas","successed");


            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {

                Log.i("msjkas","failed "+youTubeInitializationResult.toString());

            }
        };

        youtubeplayer.initialize("AIzaSyBMrd-w4b_9V5v3ZhjwgS3mn7P1aLos9II",listener);


    }



}