package brainwind.letstalksample.features.letstalk.fragments.adapters;

import static android.view.View.GONE;
import static com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_BOTTOM_LEFT;
import static com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_BOTTOM_RIGHT;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.skyfishjy.library.RippleBackground;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;
import brainwind.letstalksample.data.utilities.TimeUtilities;
import brainwind.letstalksample.features.letstalk.CommentTimeStampNavigation;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder>
{

    public HashMap<String, ArrayList<Comment>> timestamps_comments=new HashMap<String,ArrayList<Comment>>();

    public int last_known_pos=-1;
    public List<Comment> commentListUnderHeadComment = new ArrayList<Comment>();
    public HashMap<String,Boolean> no_more_comments_previous=new HashMap<String,Boolean>();
    public HashMap<String,String> started_loading_older_coments =new HashMap<String,String>();
    public String currentLoadingTimestampLabel="";
    public HashMap<String,Boolean> no_more_comments_next=new HashMap<String,Boolean>();
    public HashMap<String,String> started_loading_next_coments =new HashMap<String,String>();
    public boolean is_loading=false;

    public String starting_timestamp="";
    public int starting_set_number=0;

    //Morning is the period from sunrise to noon.5 am to 12 pm (noon)
    //Early Morning 5 to 8 am
    public static String EARLY_MORNING="es1";
    //Late morning 11 am to 12pm
    public static String LATE_MORNING="es2";
    public static String NOON="es3";
    //Afternoon 12 pm to 5 pm
    //Early afternoon   1 to 3pm
    public static String EARLY_AFTERNOON="es4";
    //Late afternoon    4 to 5pm
    public static String LATE_AFTERNOON="es5";
    //Evening     5 pm to 9 pm
    //Early evening   5 to 7 pm
    public static String EARLY_EVENING="es6";
    //Night 9 pm to 4 am
    public static String NIGHT="es7";

    public HashMap<String, String> markers=new HashMap<String,String>();
    public HashMap<String, String> markersx=new HashMap<String,String>();
    public HashMap<Integer, String> markers_positions=new HashMap<Integer,String>();
    public HashMap<Integer, String> news_positions=new HashMap<Integer,String>();

    private Activity activity;

    public TestAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public TestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==-1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_list
                    , parent, false);
            return new TestHolder(view);
        }
        else if(viewType==Comment.NEWS_AD)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_news
                    , parent, false);
            return new TestHolder(view);
        }
        else if (viewType == Comment.AGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agree_comment
                    , parent, false);
            return new TestHolder(view);
        }
        else if (viewType == Comment.DISAGREES) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disagree_comment
                    , parent, false);
            return new TestHolder(view);
        }
        else if (viewType == Comment.QUESTION||viewType==Comment.ANSWER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_comment
                    , parent, false);
            return new TestHolder(view);
        }
        else {

            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.agree_comment
                            , parent, false);
            return new TestHolder(view);
        }
    }


    int numshowads=0;
    int shownadindex=0;
    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {

        int list_position=position;

        if(list_position<commentListUnderHeadComment.size())
        {

            Comment comment = commentListUnderHeadComment.get(list_position);
            comment.setAdapter_position(position);
            commentListUnderHeadComment.set(position,comment);
            if(getItemViewType(position)>-1&getItemViewType(position)!=Comment.NEWS_AD) {


                if(comment.getComment_type()!=Comment.NEWS_AD)
                {

                    decideIfTimeStamp(comment, list_position);
                    holder.timestamp_stance.setVisibility(GONE);
                    holder.loading_next_comments.setVisibility(View.GONE);
                    holder.loading_prev_comments.setVisibility(View.GONE);
                    holder.summary_area.setVisibility(View.GONE);
                    holder.view_older.setVisibility(View.GONE);

                    //display the timestamp,deciding and if so displaying the view_holder or summary area
                    if(comment.isIs_timestamp())
                    {
                        holder.timestamp_area.setVisibility(View.VISIBLE);
                        holder.animationView.setVisibility(GONE);
                        holder.timestamp_area.setVisibility(View.VISIBLE);
                        holder.view_older.setVisibility(GONE);
                        holder.summary_area.setVisibility(GONE);
                        //show the timestamp label
                        String timestamp_label=getTimestamp(comment,holder.timestamp_area.getContext());
                        holder.timestamp_label.setText(timestamp_label);
                        holder.timestamp_label.setVisibility(View.VISIBLE);

                        //deciding to show view older area
                        if(timestamps_comments.containsKey(comment.getTimestamp())==false)
                        {

                            Comment lastt=commentListUnderHeadComment.get(commentListUnderHeadComment.size()-1);
                            Log.i("fsdhgshd",comment.getTimestamp()+" "+(no_more_comments_previous.containsKey(comment.getTimestamp()))
                                    +" "+(comment.getTimestamp().equals(lastt.getTimestamp()))
                                    +" "+(comment.getTimestamp().isEmpty()));

                            if(no_more_comments_previous.containsKey(comment.getTimestamp())==false
                                    &started_loading_older_coments.containsKey(comment.getTimestamp()))
                            {
                                Log.i("fsdhgshd","s1 "+comment.getTimestamp()+" "+getItemViewType(position));
                                holder.view_older.setVisibility(View.GONE);
                                holder.loading_prev_comments.setVisibility(View.VISIBLE);
                            }
                            else if(no_more_comments_previous.containsKey(comment.getTimestamp())==false
                                    &comment.getTimestamp().equals(lastt.getTimestamp())
                                    &comment.getTimestamp().isEmpty()==false)
                            {


                                Log.i("fsdhgshd","s2 "+comment.getTimestamp()+" "+getItemViewType(position));
                                holder.view_older.setVisibility(View.VISIBLE);
                                holder.loading_prev_comments.setVisibility(View.GONE);

                            }
                            else if(no_more_comments_previous.containsKey(comment.getTimestamp()))
                            {
                                holder.view_older.setVisibility(View.VISIBLE);
                                holder.loading_prev_comments.setVisibility(GONE);
                                holder.view_older_txt.setText("No more Previous Comments");
                                holder.view_older_txt.setTextColor(holder.view_older_txt.getResources().getColor(R.color.black));
                                holder.view_older_txt.setAnimation(null);
                            }

                        }
                        else
                        {

                            holder.view_older.setVisibility(View.VISIBLE);
                            if(started_loading_older_coments.containsKey(comment.getTimestamp()))
                            {
                                holder.loading_prev_comments.setVisibility(View.VISIBLE);
                            }
                            else if(started_loading_older_coments.containsKey(comment.getTimestamp())==false
                                    &no_more_comments_previous.containsKey(comment.getTimestamp()))
                            {
                                holder.view_older.setVisibility(View.VISIBLE);
                                holder.loading_prev_comments.setVisibility(GONE);
                                holder.view_older_txt.setText("No more Previous Comments");
                                holder.view_older_txt.setTextColor(holder.view_older_txt.getResources().getColor(R.color.black));
                                holder.view_older_txt.setAnimation(null);
                            }
                            else
                            {
                                holder.loading_prev_comments.setVisibility(GONE);
                            }


                        }

                        //deciding to show summary area



                    }
                    else
                    {
                        holder.timestamp_area.setVisibility(GONE);
                    }

                    if(comment.getComment().isEmpty())
                    {
                        holder.comment_view.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.comment_view.setVisibility(View.VISIBLE);
                        holder.comment.setText(comment.getComment());
                    }

                    holder.comment.setText(comment.getComment());

                    if(comment.isSent()||position<last_known_pos)
                    {
                        comment.setSent(true);
                        commentListUnderHeadComment.set(position,comment);

                        Glide.with(holder.comment_view.getContext())
                                .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_done_24))
                                .into(holder.sent_status);

                        if(comment.getNumOFCommentsRead()>0)
                        {
                            Glide.with(holder.comment_view.getContext())
                                    .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_done_all_24))
                                    .into(holder.sent_status);
                        }


                    }
                    else
                    {
                        Glide.with(holder.comment_view.getContext())
                                .load(holder.comment_view.getContext().getDrawable(R.drawable.ic_baseline_access_time_24))
                                .into(holder.sent_status);
                    }

                    if(holder.view_older.getVisibility()==View.VISIBLE&no_more_comments_previous.containsKey(comment.getTimestamp())==false)
                    {
                        setUpViewOlder(holder,position);
                    }

                    //displaying the time sent
                    holder.time_sent.setText(comment.getTimeStr());

                    //display the comment type
                    if(comment.getComment_type()==Comment.AGREES)
                    {
                        holder.comment_type.setText("Agrees");


                    }
                    if(comment.getComment_type()==Comment.DISAGREES)
                    {
                        holder.comment_type.setText("Disagrees");
                    }
                    if(comment.getComment_type()==Comment.QUESTION)
                    {
                        holder.comment_type.setText("Question");
                    }
                    if(comment.getComment_type()==Comment.ANSWER)
                    {
                        holder.comment_type.setText("Answer");
                    }


                }





            }
            else if(getItemViewType(position)==Comment.NEWS_AD)
            {

            /*
                    //news views
                ImageView news_bookie_logo;
                TextView mediatype1;
                TextView news_title;
                TextView lead_label;
             */
                Log.i("getNewsSuggestions","onBindViewHolder position="+position
                        +" newsFactsMediaArrayList.size()="+newsFactsMediaArrayList.size());

                if(comment.isItAd()&nativeAdArrayList.size()>0)
                {

                    if(shownadindex<nativeAdArrayList.size())
                    {
                        NativeAd nativeAd=nativeAdArrayList.get(shownadindex);
                        displayNativeAd(holder,nativeAd);
                        comment.setShowAdIndex(shownadindex);
                        commentListUnderHeadComment.set(position,comment);

                        if(shownadindex+1<nativeAdArrayList.size())
                        {
                            shownadindex++;
                        }
                        else
                        {
                            shownadindex=0;
                            nativeAdArrayList.clear();
                        }
                    }


                }
                else if(newsFactsMediaArrayList.size()>0)
                {
                    NewsFactsMedia newsFactsMedia=newsFactsMediaArrayList.get(laks);
                    holder.ad_badge.setVisibility(View.GONE);
                    holder.lead_label.setText("         ");
                    if(newsFactsMedia!=null)
                    {
                        holder.news_bookie_logo.setVisibility(View.VISIBLE);
                        holder.news_bookie_logo.setImageDrawable(holder.news_bookie_logo.getResources().getDrawable(R.drawable.ic_bookie));
                        holder.mediatype1.setVisibility(View.VISIBLE);
                        if(newsFactsMedia.getTitle().trim().length()<=70)
                        {
                            if(newsFactsMedia.getTitle().trim().length()==70)
                            {
                                holder.news_title.setText(newsFactsMedia.getTitle().trim());
                            }
                            else
                            {
                                StringBuilder ksld=new StringBuilder();
                                int kl=70-newsFactsMedia.getTitle().trim().length();
                                for(int y=0;y<kl;y++)
                                {
                                    ksld.append(" ");
                                }
                                holder.news_title.setText(newsFactsMedia.getTitle().trim()+ksld);
                            }
                        }
                        else
                        {
                            holder.news_title.setText(newsFactsMedia.getTitle().trim().substring(0,67)+"...");
                        }
                        if(newsFactsMedia.getSource().isEmpty()==false)
                        {
                            holder.mediatype1.setText("News:"+newsFactsMedia.getSource());
                        }
                        else
                        {
                            holder.mediatype1.setText("News");
                        }
                        holder.lead_label.setVisibility(View.VISIBLE);
                        holder.lead_label.setText("read more");
                    }



                    if(laks+1<newsFactsMediaArrayList.size())
                    {
                        laks++;
                    }
                    else
                    {
                        laks=0;
                    }

                }
                else
                {



                }

            }

        }

    }

    private void setUpViewOlder(TestHolder holder, int position)
    {

        final Comment comment=commentListUnderHeadComment.get(position);
        final String timestamp=comment.getYear()+"-"+comment.getMonth()
                +"-"+comment.getDay();
        if(no_more_comments_previous.containsKey(comment.getTimestamp())==false)
        {
            holder.view_older_txt
                    .startAnimation(AnimationUtils
                            .loadAnimation(holder
                                            .view_older_txt
                                            .getContext()
                                    , R.anim.pulse));
        }
        else
        {

        }

        holder.view_older.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(no_more_comments_previous.containsKey(timestamp))
                {
                    Toast.makeText(holder.view_older_txt.getContext()
                            ,"No More Previous Comments for "+holder.timestamp_label.getText(),
                            Toast.LENGTH_LONG).show();
                }
                else if(started_loading_older_coments.size()>0||currentLoadingTimestampLabel.isEmpty()==false)
                {

                    Toast.makeText(holder.view_older_txt.getContext()
                            ,"Loading comments for "+currentLoadingTimestampLabel,
                            Toast.LENGTH_LONG).show();
                }
                else
                {

                    holder.loading_prev_comments.setVisibility(View.VISIBLE);
                    holder.view_older.setVisibility(View.INVISIBLE);
                    if(activity!=null)
                    {

                        CommentTimeStampNavigation commentTimeStampNavigation=(CommentTimeStampNavigation) activity;
                        if(commentTimeStampNavigation!=null)
                        {

                            currentLoadingTimestampLabel=holder.timestamp_label.getText().toString();
                            commentTimeStampNavigation.getCommentsForTimeStampsPrevTo(comment);


                        }

                    }

                }

            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull TestHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(holder.getBindingAdapterPosition()<commentListUnderHeadComment.size())
        {
            Comment comment = commentListUnderHeadComment.get(holder.getBindingAdapterPosition());
            comment.setAdapter_position(holder.getBindingAdapterPosition());
            commentListUnderHeadComment.set(holder.getBindingAdapterPosition(),comment);

            if(comment.isItAd()==false&comment.isIs_timestamp())
            {

            }

        }


    }

    @Override
    public void onViewRecycled(@NonNull TestHolder holder) {
        super.onViewRecycled(holder);

        if(holder.getBindingAdapterPosition()>-1)
        {
            if(getItemViewType(holder.getBindingAdapterPosition())==Comment.NEWS_AD)
            {

                Log.i("onViewRecycled","position="+(holder.getBindingAdapterPosition()));
                Comment comment=commentListUnderHeadComment.get(holder.getBindingAdapterPosition());
                int shownindex=comment.getShowAdIndex();
                if(shownindex<nativeAdArrayList.size())
                {
                    NativeAd nativeAd=nativeAdArrayList.get(shownindex);
                    //nativeAd.destroy();
                }

            }
        }


    }

    private void displayNativeAd(TestHolder holder, NativeAd nativeAd)
    {

        holder.ad_badge.setVisibility(View.VISIBLE);

        holder.mediatype1.setTextColor(holder.mediatype1.getResources().getColor(R.color.purple_700));
        if(nativeAd.getAdvertiser()!=null)
        {
            holder.mediatype1.setText(" "+nativeAd.getAdvertiser());
            holder.adView.setAdvertiserView(holder.mediatype1);
        }
        else if(nativeAd.getStore()!=null)
        {
            holder.mediatype1.setText(" "+nativeAd.getStore());
            holder.adView.setStoreView(holder.mediatype1);
        }
        if(nativeAd.getHeadline().length()>25)
        {
            holder.news_title.setText(nativeAd.getHeadline().substring(0,24)+"...");

        }
        else
        {
            holder.news_title.setText(nativeAd.getHeadline());
        }
        holder.adView.setHeadlineView(holder.news_title);
        holder.adView.setHeadlineView(holder.news_title);
        if(nativeAd.getIcon()!=null)
        {
            holder.news_bookie_logo.setImageDrawable(nativeAd.getIcon().getDrawable());
            holder.adView.setImageView(holder.news_bookie_logo);
            holder.adView.setIconView(holder.news_bookie_logo);
        }
        else
        {
            holder.news_bookie_logo.setVisibility(View.GONE);
        }
        if(nativeAd.getCallToAction()!=null)
        {
            if(nativeAd.getCallToAction().isEmpty()==false)
            {
                holder.lead_label.setVisibility(View.VISIBLE);
                holder.lead_label.setText(" "+nativeAd.getCallToAction().toLowerCase());
                holder.adView.setCallToActionView(holder.lead_label);
            }
        }
        // Call the NativeAdView's setNativeAd method to register the
        // NativeAdObject.
        holder.adView.setNativeAd(nativeAd);



    }

    public String getTimestamp(Comment comment, Context context)
    {

        Memory memory=new Memory(context);
        String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);

        if(jnm.isEmpty()==false)
        {

            long localtimeoffset=Long.parseLong(jnm);
            long estimatedServerTimeMs = System.currentTimeMillis() + localtimeoffset;

            org.joda.time.LocalDateTime localDateTime
                    =new org.joda.time.LocalDateTime(estimatedServerTimeMs);
            android.util.Log.w("jskasd", "offset="+localtimeoffset
                    +" estimatedServerTimeMs="+estimatedServerTimeMs
                    +" "+localDateTime.getDayOfMonth()+" "+localDateTime.getMonthOfYear()
                    +" "+localDateTime.getYear()
                    +" "+String.valueOf(localtimeoffset));

            String timelabel= TimeUtilities.getTimeLabel(localDateTime,comment);
            Log.i("tinsja","timelabel="+timelabel+" "+localDateTime.getDayOfMonth()
                    +" "+localDateTime.getMonthOfYear()+" "+localDateTime.getYear());
            return timelabel;
        }
        else
        {
            return "";
        }

    }

    private void decideIfTimeStamp(Comment comment,int position)
    {

        //decide if it is a timestamp
        if(position==0)
        {
            comment.setIs_timestamp(true);
            this.commentListUnderHeadComment.set(position,comment);
        }
        else
        {

            Comment prev_comment=this.commentListUnderHeadComment.get(position-1);
            if(comment.getComment_type()==Comment.NEWS_AD||prev_comment.getComment_type()==Comment.NEWS_AD)
            {
                comment.setIs_timestamp(false);
                this.commentListUnderHeadComment.set(position,comment);
            }
            else if(comment.isOnTheSameDay(prev_comment))
            {
                comment.setIs_timestamp(false);
                this.commentListUnderHeadComment.set(position,comment);
            }
            else
            {
                comment.setIs_timestamp(true);
                this.commentListUnderHeadComment.set(position,comment);
            }


        }


    }

    public boolean checkIfLastCommentUnderTimestamp(int position)
    {

        if(position+1<commentListUnderHeadComment.size())
        {
            Comment current_comment=commentListUnderHeadComment.get(position);
            Comment next_comment=commentListUnderHeadComment.get(position+1);
            if(current_comment.getTimestamp().equals(next_comment.getTimestamp()))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        else if(position==commentListUnderHeadComment.size()-1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public static final int AD_NEWS=-99;
    @Override
    public int getItemViewType(int position) {


        if(is_loading&position==getItemCount()-1)
        {
            return -1;
        }
        else
        {
            Comment comment=commentListUnderHeadComment.get(position);
            return comment.getComment_type();

        }

    }

    public int offset=0;
    @Override
    public int getItemCount() {
        if(is_loading)
        {
            return commentListUnderHeadComment.size()+1;
        }
        else
        {
            return commentListUnderHeadComment.size();
        }
    }

    public ArrayList<NewsFactsMedia> newsFactsMediaArrayList=new ArrayList<NewsFactsMedia>();
    int laks=0;
    public void addNewsAdsList(ArrayList<NewsFactsMedia> newsFactsMediaArrayList)
    {

        this.newsFactsMediaArrayList.addAll(newsFactsMediaArrayList);


    }

    ArrayList<NativeAd> nativeAdArrayList=new ArrayList<NativeAd>();
    public void addNativeAd(NativeAd nativeAd)
    {

        nativeAdArrayList.add(nativeAd);

    }


    public class TestHolder extends RecyclerView.ViewHolder {
        CardView timestamp_area;
        ImageView expand_collape_timestamp_group;
        RelativeLayout timestamp_area_l;
        TextView timestamp_stance;
        TextView timestamp_label;
        RelativeLayout comment_view;
        LottieAnimationView animationView;
        TextView time_sent;
        TextView num_people_read;
        ImageView share_day_convo;

        TextView part_of_day;
        TextView comment_name;
        TextView comment_type;
        TextView num_comments_view;
        TextView summary_label;
        RelativeLayout summary_area;
        CardView summary_area_card;
        EmojiconTextView comment;
        ImageView sent_status;

        RelativeLayout view_older;
        TextView view_older_txt;
        ProgressBar loading_prev_comments;
        ProgressBar loading_next_comments;

        //reply area
        LinearLayout replied_area;
        FrameLayout reply_agree_disagree_flag;
        TextView replied_comment_name;
        TextView comment_reply;
        TextView reply_comment_type;

        //news views
        NativeAdView adView;
        ImageView ad_badge;
        ImageView news_bookie_logo;
        TextView mediatype1;
        TextView news_title;
        TextView lead_label;

        public TestHolder(@NonNull View itemView) {
            super(itemView);
            share_day_convo=(ImageView) itemView.findViewById(R.id.share_day_convo);
            loading_prev_comments = (ProgressBar) itemView.findViewById(R.id.loading_prev_comments);
            loading_next_comments = (ProgressBar) itemView.findViewById(R.id.loading_next_comments);
            animationView = (LottieAnimationView) itemView.findViewById(R.id.animationView);
            time_sent = (TextView) itemView.findViewById(R.id.time_sent);
            num_people_read = (TextView) itemView.findViewById(R.id.num_people_read);
            timestamp_area = (CardView) itemView.findViewById(R.id.timestamp_area);
            expand_collape_timestamp_group = (ImageView)
                    itemView.findViewById(R.id.expand_collape_timestamp_group);
            timestamp_area_l = (RelativeLayout)
                    itemView.findViewById(R.id.timestamp_area_l);
            timestamp_label=(TextView)itemView.findViewById(R.id.timestamp_label);
            timestamp_stance=(TextView)itemView.findViewById(R.id.timestamp_stance);

            summary_area_card = (CardView) itemView.findViewById(R.id.summary_area_card);
            summary_area = (RelativeLayout) itemView.findViewById(R.id.summary_area);
            comment_view = (RelativeLayout) itemView.findViewById(R.id.comment_view);
            summary_label = (TextView) itemView.findViewById(R.id.summary_label);
            part_of_day = (TextView) itemView.findViewById(R.id.part_of_day);
            comment_name = (TextView) itemView.findViewById(R.id.comment_name);
            comment_type = (TextView) itemView.findViewById(R.id.comment_type);
            num_comments_view = (TextView) itemView.findViewById(R.id.num_comments_view);
            comment = (EmojiconTextView) itemView.findViewById(R.id.comment);
            sent_status = (ImageView) itemView.findViewById(R.id.sent_status);

            view_older=(RelativeLayout) itemView.findViewById(R.id.view_older);
            view_older_txt=(TextView) itemView.findViewById(R.id.view_older_txt);

                /*
                            //reply agree
            LinearLayout replied_area_agree;
            FrameLayout agree_disagree_flag_agree;
            TextView replied_comment_name_agree;
            TextView comment_reply_agree;
            //reply disagree
            LinearLayout replied_area_disagree;
            FrameLayout agree_disagree_flag_disagree;
            TextView replied_comment_name_disagree;
            TextView comment_reply_disagree;
            //reply question
            LinearLayout replied_area_question;
            FrameLayout agree_disagree_flag_question;
            TextView replied_comment_name_question;
            TextView comment_reply_question;
                 */

            //reply area
            replied_area=(LinearLayout) itemView.findViewById(R.id.replied_area);
            reply_agree_disagree_flag=(FrameLayout)itemView.findViewById(R.id.reply_agree_disagree_flag);
            replied_comment_name=(TextView) itemView.findViewById(R.id.replied_comment_name);
            comment_reply=(TextView) itemView.findViewById(R.id.comment_reply);
            reply_comment_type=(TextView) itemView.findViewById(R.id.reply_comment_type);

            adView =(NativeAdView)itemView.findViewById(R.id.adView);
            news_bookie_logo=(ImageView) itemView.findViewById(R.id.news_bookie_logo);
            ad_badge=(ImageView) itemView.findViewById(R.id.ad_badge);
            mediatype1=(TextView) itemView.findViewById(R.id.mediatype1);
            news_title=(TextView) itemView.findViewById(R.id.news_title);
            lead_label=(TextView) itemView.findViewById(R.id.lead_label);

        }


    }

    public void SignalLoading(Comment timestamp_comment)
    {
        started_loading_older_coments.put(timestamp_comment.getTimestamp(),timestamp_comment.getComment_id());

    }

    public void SignalLoadingLastComment()
    {

        Comment timestamp_comment=commentListUnderHeadComment.get(commentListUnderHeadComment.size()-1);
        SignalLoading(timestamp_comment);

    }

    NewsFactsMedia newsFactsMedia;
    public void showNewsAlerts(NewsFactsMedia newsFactsMedia)
    {

        this.newsFactsMedia=newsFactsMedia;
        int offset=getItemCount()-commentListUnderHeadComment.size();
        if(offset>0)
        {
            int news_ad_pos1=getItemCount()-2;
            notifyItemChanged(news_ad_pos1);
        }

    }

    public int getOffset()
    {
        return getItemCount()-commentListUnderHeadComment.size();
    }

    public String getDeviceId(Context context){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            String androidId =
                    Settings.Secure.getString((ContentResolver)context.getContentResolver(),Settings.Secure.ANDROID_ID);
            messageDigest.update(androidId.getBytes());
            byte[] arrby = messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            int n = arrby.length;
            for(int i=0; i<n; ++i){
                String oseamiya = Integer.toHexString((int)(255 & arrby[i]));
                while(oseamiya.length() < 2){
                    oseamiya = "0" + oseamiya;
                }
                sb.append(oseamiya);
            }
            String result = sb.toString();
            return result;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    public int getActualNumberofComments()
    {
        return commentListUnderHeadComment.size()-news_positions.size();
    }

}
