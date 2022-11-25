package brainwind.letstalksample.features.websearch;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import com.github.kotvertolet.youtubejextractor.JExtractorCallback;
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.AdaptiveVideoStream;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.SetOptions;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.CloudWorker;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.database.user.user_info.UserInfoDatabase;
import brainwind.letstalksample.utils.ImageUtil;


public class VideoMenuHelper implements Player.Listener
{

    private boolean YES_PLAY = false;
    private ExoPlayer player;
    StyledPlayerView playerView;
    MaterialButton select_video_but;
    Intent intent;
    Activity activity;
    RecyclerView recyclerView;
    HashMap<Integer,Integer> position_video_size=new HashMap<Integer,Integer>();
    private MaterialDialog select_dialog;
    private MenuAdapter menuAdapter;
    private int current_index=0;
    private int current_size_index=0;
    private RelativeLayout pls_wait_area;
    private boolean stop=false;
    private String media_url="";
    private MaterialDialog please_wait;
    private MediaItem select_media_item;
    private boolean is_unlimited=false;
    private boolean player_ready=false;
    private CountDownTimer cm;
    private String video_Title="";

    public VideoMenuHelper(Intent intent, Activity activity) {
        this.intent = intent;
        this.activity = activity;
    }

    public void ShowDialog(String VideolInk,String video_Title)
    {

        this.video_Title=video_Title;
        stop=false;
        select_dialog=new MaterialDialog.Builder(this.activity)
                .title("Your choices for this video, Tap to Select")
                .customView(R.layout.dialog_video_list,false)
                .cancelable(false)
                .positiveText("Never mind")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        StopPlayer();


                    }
                })
                .build();

        player = new ExoPlayer.Builder(this.activity).build();
        playerView=(StyledPlayerView)select_dialog.getCustomView().findViewById(R.id.player_view);
        // Attach player to the view.
        playerView.setPlayer(player);
        player.addListener(this);
        select_video_but=(MaterialButton)select_dialog.getCustomView().findViewById(R.id.select_video_but);
        select_video_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(select_media_item!=null)
                {
                    selectVideoItem(select_media_item);
                }
                else
                {
                    Toast.makeText(activity, "Please select video or audio in the list, then after select it"
                            , Toast.LENGTH_SHORT).show();

                }

            }
        });
        pls_wait_area=(RelativeLayout)select_dialog.getCustomView().findViewById(R.id.pls_wait_area);
        recyclerView=select_dialog.getCustomView().findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.activity));
        menuAdapter=new MenuAdapter();
        recyclerView.setAdapter(menuAdapter);

        startLooking(VideolInk);
        select_dialog.show();
        pls_wait_area.setVisibility(View.VISIBLE);


    }

    private void startLooking(String VideolInk)
    {



        String videoId= ImageUtil.getVideoId(VideolInk);



    }

    private void GetSizes(SparseArray<YtFile> ytFiles)
    {

        if(current_size_index>-1&current_size_index<ytFiles.size()&stop==false)
        {

            int itag = ytFiles.keyAt(current_size_index);
            YtFile ytFile = ytFiles.get(itag);
            if(ytFile!=null)
            {
                final String link=ytFile.getUrl();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        URL myUrl = null;
                        try {

                            myUrl = new URL(ytFile.getUrl());
                            URLConnection urlConnection = myUrl.openConnection();
                            urlConnection.connect();
                            int file_size_o = urlConnection.getContentLength();
                            position_video_size.put(current_size_index,file_size_o);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    menuAdapter.notifyItemChanged(current_size_index);
                                    current_size_index++;
                                    GetSizes(ytFiles);
                                }
                            });


                        }
                        catch(Exception exception)
                        {

                        }

                    }
                }).start();
            }
            else
            {
                Log.i("frsdaffasf","m="+(ytFile!=null)+" current_size_index="+current_size_index);
            }


        }

    }

    private void GoEachFile(SparseArray<YtFile> ytFiles)
    {

        if(current_index>-1&current_index<ytFiles.size()&stop==false)
        {
            Log.i("nhsgjdk","current_index="+current_index);
            int itag = ytFiles.keyAt(current_index);
            YtFile ytFile = ytFiles.get(itag);

            if(ytFile.getFormat().getHeight()==-1)
            {

                final MediaItem mediaItem=new MediaItem();
                mediaItem.setVideo(false);
                mediaItem.setHasAudio(true);
                mediaItem.setMedia_url(ytFile.getUrl());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        menuAdapter.addMediaItem(mediaItem);
                        menuAdapter.notifyDataSetChanged();
                        current_index++;

                        GoEachFile(ytFiles);



                    }
                });


            }
            else
            {

                final MediaItem mediaItem=new MediaItem();
                mediaItem.setVideo(true);
                mediaItem.setMedia_url(ytFile.getUrl());
                if(ytFile.getFormat().getAudioBitrate()==-1&ytFile.getFormat().getHeight()>-1)
                {
                    mediaItem.setHasAudio(false);
                }
                else
                {
                    mediaItem.setHasAudio(true);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(current_index==0&is_unlimited)
                        {
                            select_media_item=mediaItem;
                            PlayVideo();
                        }
                        menuAdapter.addMediaItem(mediaItem);
                        menuAdapter.notifyDataSetChanged();
                        current_index++;
                        GoEachFile(ytFiles);

                    }
                });

            }
        }


    }

    private void selectVideoItem(MediaItem mediaItem)
    {

        StopPlayer();
        Log.i("jsudjka","url="+mediaItem.getMedia_url());
        boolean nmk=false;
        if(intent!=null)
        {

            if(intent.hasExtra(SelectVideoFromWeb.PURPOSE))
            {

                int purpose=intent.getIntExtra(SelectVideoFromWeb.PURPOSE,
                        SelectVideoFromWeb.PICK_FOR_HAVE_YOUR_SAY);

                if(purpose==SelectVideoFromWeb.PICK_FOR_HAVE_YOUR_SAY
                        &intent.hasExtra(OrgFields.HAVE_UR_SAY_ID)
                        &intent.getStringExtra(OrgFields.HAVE_UR_SAY_ID)!=null)
                {

                    nmk=true;
                    String hvsay_id=intent.getStringExtra(OrgFields.HAVE_UR_SAY_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_HAVE_YOUR_SAY);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your have your say"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(hvsay_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }
                if(purpose==SelectVideoFromWeb.PICK_FOR_ARTICLE
                        &intent.hasExtra(OrgFields.ARTICLE_ID)
                        &intent.getStringExtra(OrgFields.ARTICLE_ID)!=null)
                {

                    nmk=true;
                    String article_id=intent.getStringExtra(OrgFields.ARTICLE_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_ARTICLE);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your article"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(article_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }
                if(purpose==SelectVideoFromWeb.PICK_FOR_LETS_TALK
                        &intent.hasExtra(OrgFields.LETS_TALK_ID)
                        &intent.getStringExtra(OrgFields.LETS_TALK_ID)!=null)
                {

                    nmk=true;
                    String lets_talk_id=intent.getStringExtra(OrgFields.LETS_TALK_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_LETS_TALK);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your conversation"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(lets_talk_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }

                if(purpose==SelectVideoFromWeb.PICK_FOR_LETS_TALK
                        &intent.hasExtra(OrgFields.LETS_TALK_ID)
                        &intent.getStringExtra(OrgFields.LETS_TALK_ID)!=null)
                {

                    nmk=true;
                    String lets_talk_id=intent.getStringExtra(OrgFields.LETS_TALK_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_LETS_TALK);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your conversation"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(lets_talk_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }

                if(purpose==SelectVideoFromWeb.PICK_FOR_EVENT
                        &intent.hasExtra(OrgFields.EVENT_ID)
                        &intent.getStringExtra(OrgFields.EVENT_ID)!=null)
                {

                    nmk=true;
                    String event_id=intent.getStringExtra(OrgFields.EVENT_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_EVENT);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your event"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(event_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }

                if(purpose==SelectVideoFromWeb.PICK_FOR_PROJECT
                        &intent.hasExtra(OrgFields.PROJECT_ID)
                        &intent.getStringExtra(OrgFields.PROJECT_ID)!=null)
                {

                    nmk=true;
                    String project_id=intent.getStringExtra(OrgFields.PROJECT_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_PROJECT);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your project"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(project_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }

                if(purpose==SelectVideoFromWeb.PICK_FOR_PARTNERSHIP
                        &intent.hasExtra(OrgFields.PARTNERSHIP_ID)
                        &intent.getStringExtra(OrgFields.PARTNERSHIP_ID)!=null)
                {

                    nmk=true;
                    String partnership_id=intent.getStringExtra(OrgFields.PARTNERSHIP_ID);

                    //edit the have your say and attach a media
                    Map<String,Object> key_values=new HashMap<String,Object>();
                    key_values.put(OrgFields.HAS_ATTACH_MEDIA,true);
                    if(mediaItem.isVideo())
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_VIDEO,true);
                    }
                    else
                    {
                        key_values.put(OrgFields.IS_ATTACH_MEDIA_AUDIO,true);
                    }
                    key_values.put(OrgFields.MEDIA_URL,mediaItem.getMedia_url());
                    key_values.put(OrgFields.ACTIVITY_TYPE,OrgFields.ACTIVITY_TYPE_PARTNERSHIP);

                    if(please_wait!=null)
                    {
                        please_wait.dismiss();
                    }
                    String title="";
                    if(intent.hasExtra(OrgFields.TITLE))
                    {
                        title=intent.getStringExtra(OrgFields.TITLE);
                        if(title.isEmpty()==false)
                        {
                            title=" '"+title+"'";
                        }
                    }
                    please_wait=new MaterialDialog.Builder(activity)
                            .title("Please wait")
                            .content("Please wait we are updating your partnership"+title)
                            .cancelable(false)
                            .show();

                    CloudWorker
                            .getUserActivities()
                            .document(partnership_id)
                            .set(key_values, SetOptions.merge())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }

                                    please_wait=new MaterialDialog.Builder(activity)
                                            .title("Oops")
                                            .content(e.getMessage()+". Try again")
                                            .positiveText("Try again")
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog,
                                                                    @NonNull DialogAction which) {

                                                    selectVideoItem(mediaItem);

                                                }
                                            })
                                            .negativeText("Never mind")
                                            .cancelable(false)
                                            .show();


                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    if(please_wait!=null)
                                    {
                                        please_wait.dismiss();
                                    }
                                    activity.startActivity(intent);

                                }
                            });

                }

                if(nmk==false)
                {
                    chooseActivity(mediaItem);
                }

            }
            else
            {

                chooseActivity(mediaItem);

            }

        }
        else
        {
            chooseActivity(mediaItem);
        }

    }

    private void chooseActivity(MediaItem mediaItem)
    {

        if(select_dialog!=null)
        {
            select_dialog.dismiss();
        }

        List<String> list=new ArrayList<String>();
        list.add("Create and Attach to 'Have Your Say' session");
        list.add("Create and Attach to an Event");
        list.add("Create and Attach to a Project");
        list.add("Create and Attach to an Article");
        list.add("Create and Attach to a Conversation");
        list.add("Create and Attach to a Partnership");
        new MaterialDialog.Builder(activity)
                .title("What to do next")
                .itemsColor(activity.getResources().getColor(R.color.purple_700))
                .items(list)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView,
                                            int position, CharSequence text) {

                        StopPlayer();


                    }
                })
                .positiveText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        StopPlayer();

                    }
                })
                .cancelable(false)
                .show();

    }

    class MenuAdapter extends RecyclerView.Adapter<MenuHolder>
    {

        List<MediaItem> mediaItemList=new ArrayList<MediaItem>();
        @Override
        public int getItemViewType(int position) {
            if(position_video_size.containsKey(position))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }

        @NonNull
        @Override
        public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==0)
            {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_media_item_loading,
                        parent,false);
                return new MenuHolder(view);
            }
            else
            {
                View view= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_video_media_item
                        ,parent,false);
                return new MenuHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull MenuHolder holder, int position) {

            MediaItem mediaItem=mediaItemList.get(position);

            if(mediaItem.isVideo())
            {

                holder.media_type.setText("VIDEO");
                if(mediaItem.isHasAudio())
                {
                    holder.audio_label.setText("+AUDIO");
                    holder.jkl.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_music_note_24));
                }
                else
                {
                    holder.audio_label.setText("NO AUDIO");
                    holder.jkl.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_baseline_music_off_24));
                }

            }
            else
            {
                holder.media_type.setText("");
                holder.audio_label.setText("+AUDIO ONLY");
            }

            if(position_video_size.containsKey(position)&holder.media_size!=null)
            {

                int file_size_o=position_video_size.get(position);
                String mes="KB";
                int file_size = (file_size_o /1024);
                if(file_size>1000)
                {
                    file_size=file_size/1024;
                    mes="MB";
                }

                holder.media_size.setText(file_size+" "+mes);

            }
            else
            {



            }

            if(getItemViewType(position)==1)
            {
                holder.select_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        select_media_item=mediaItem;
                        media_url=mediaItem.getMedia_url();
                        PlayVideo();

                        new MaterialDialog.Builder(activity)
                                .title("Do want to view it or just select it")
                                .content("If you already know the contents of this video, " +
                                        "then do you want to just attach it without viewing it first")
                                .positiveText("Select it and Attach")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        selectVideoItem(select_media_item);

                                    }
                                })
                                .negativeText("View it first")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        pointOutTheSelectButton();

                                    }
                                })
                                .cancelable(false)
                                .show();

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return mediaItemList.size();
        }

        public void addMediaItem(MediaItem mediaItem)
        {
            this.mediaItemList.add(mediaItem);
        }

        public void setMediaItem(int position,MediaItem mediaItem)
        {
            this.mediaItemList.set(position, mediaItem);
        }


    }

    private void pointOutTheSelectButton()
    {


        /*TapTargetView.showFor(activity,                 // `this` is an Activity
                TapTarget.forView(select_video_but, "When you finish viewing the video/audio"
                                , "The press this button to select it for the activity")
                        // All options below are optional
                        .outerCircleColor(R.color.purple_700)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.purple_500)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.purple_200)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                    }
                });

         */

    }

    private void PlayVideo()
    {

        if(cm!=null)
        {
            cm.cancel();
        }
        Log.i("msdoak","url="+select_media_item.getMedia_url());
        if(player==null)
        {
            player = new ExoPlayer.Builder(this.activity).build();
            playerView.setPlayer(player);
            player.addListener(this);
        }
        if(player!=null)
        {
            com.google.android.exoplayer2.MediaItem mediaItem =
                    com.google.android.exoplayer2.MediaItem.fromUri(Uri.parse(select_media_item.getMedia_url()));
            // Set the media item to be played.
            player.setMediaItem(mediaItem);
            // Prepare the player.
            player.prepare();
            player.play();


        }
    }

    class MediaItem
    {

        String media_url="";
        boolean isVideo=false;
        boolean hasAudio=false;
        int size=0;

        public boolean isVideo() {
            return isVideo;
        }

        public void setVideo(boolean video) {
            isVideo = video;
        }

        public boolean isHasAudio() {
            return hasAudio;
        }

        public void setHasAudio(boolean hasAudio) {
            this.hasAudio = hasAudio;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getMedia_url() {
            return media_url;
        }

        public void setMedia_url(String media_url) {
            this.media_url = media_url;
        }
    }

    class MenuHolder extends RecyclerView.ViewHolder {

        public TextView media_type;
        public TextView audio_label;
        public TextView media_size;
        public Button select_button;
        public ImageView jkl;

        public MenuHolder(@NonNull View itemView) {
            super(itemView);

            jkl=(ImageView)itemView.findViewById(R.id.jkl);
            media_type=(TextView) itemView.findViewById(R.id.media_type);
            audio_label=(TextView) itemView.findViewById(R.id.audio_label);
            media_size=(TextView) itemView.findViewById(R.id.media_size);
            select_button=(Button) itemView.findViewById(R.id.select_button);

        }


    }

    @Override
    public void onPlaybackStateChanged(int playbackState) {
        
        if(playbackState==ExoPlayer.STATE_READY&YES_PLAY)
        {
            player.play();
            YES_PLAY=false;
            player_ready=true;
        }

    }

    @Override
    public void onPlayerError(PlaybackException error) {
        Player.Listener.super.onPlayerError(error);

        //PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND
        //Log.i("")
        if(error!=null)
        {
            if(PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT==error.errorCode
            ||PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED==error.errorCode)
            {
                RelativeLayout root_dialog_view=select_dialog.getCustomView().findViewById(R.id.root_dialog_view);
                showMsg("No connection",root_dialog_view);
                cm= new CountDownTimer(5000,1000) {
                    @Override
                    public void onTick(long l) {
                        long seconds=l/1000;
                        RelativeLayout root_dialog_view=select_dialog.getCustomView().findViewById(R.id.root_dialog_view);
                        showMsg(seconds+" seconds till retry",root_dialog_view);
                    }

                    @Override
                    public void onFinish() {
                        PlayVideo();
                    }
                };
                cm.start();
            }
            else
            {
                RelativeLayout root_dialog_view=select_dialog.getCustomView().findViewById(R.id.root_dialog_view);
                showMsg("Oops, failed to load video",root_dialog_view);
                cm= new CountDownTimer(5000,1000) {
                    @Override
                    public void onTick(long l) {
                        long seconds=l/1000;
                        RelativeLayout root_dialog_view=select_dialog.getCustomView().findViewById(R.id.root_dialog_view);
                        showMsg(seconds+" seconds till retry",root_dialog_view);
                    }

                    @Override
                    public void onFinish() {
                        PlayVideo();
                    }
                };
                cm.start();
            }
        }

    }

    private void showMsg(String msg, View root_dialog_view)
    {

        Snackbar snackbar = Snackbar
                .make(root_dialog_view, msg, Snackbar.LENGTH_LONG);
        snackbar.setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlayVideo();

            }
        });
        snackbar.show();

    }

    public void StopPlayer()
    {

        if(player!=null)
        {
            if(player.isPlaying())
            {
                player.pause();
                player.stop();
                player.release();
                player=null;
            }
        }

    }

    public boolean isIs_unlimited() {
        return is_unlimited;
    }

    public void setIs_unlimited(boolean is_unlimited) {
        this.is_unlimited = is_unlimited;
    }

}
