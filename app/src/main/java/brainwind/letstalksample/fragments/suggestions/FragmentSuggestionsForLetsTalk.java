package brainwind.letstalksample.fragments.suggestions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.PlayVideoActivity;
import brainwind.letstalksample.R;
import brainwind.letstalksample.data.textwork.PartExtractor;
import brainwind.letstalksample.features.letstalk.fragments.item.NewsFactsMedia;
import brainwind.letstalksample.features.websearch.VideoMenuHelper;
import brainwind.letstalksample.utils.ImageUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSuggestionsForLetsTalk#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSuggestionsForLetsTalk extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONVO_TITLE = "convo_title";

    // TODO: Rename and change types of parameters
    private String convo_title;
    private NewsFactsMediaAdapter newsFactsMediaAdapter;
    private VideoAdapter videoAdapter;
    private VideoMenuHelper videoMenuHelper;
    private int offset=0;

    public FragmentSuggestionsForLetsTalk() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param convo_title Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSuggestionsForLetsTalk.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSuggestionsForLetsTalk newInstance(String convo_title, String param2) {
        FragmentSuggestionsForLetsTalk fragment = new FragmentSuggestionsForLetsTalk();
        Bundle args = new Bundle();
        args.putString(CONVO_TITLE, convo_title);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.new_media_facts_list)
    RecyclerView new_media_facts_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            convo_title = getArguments().getString(CONVO_TITLE);
            Log.i("mujska","convo_title="+convo_title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_suggestions_for_lets_talk,
                container, false);
        ButterKnife.bind(this,view);
        setUpList();
        return view;
    }

    List<NewsFactsMedia> images_list=new ArrayList<NewsFactsMedia>();
    List<NewsFactsMedia> videolist=new ArrayList<NewsFactsMedia>();
    List<NewsFactsMedia> newsFactsMediaList=new ArrayList<NewsFactsMedia>();


    private class NewsFactsMediaAdapter extends
            RecyclerView.Adapter<NewsFactsMediaAdapter.NewsFactsMediaHolder>
    {

        @Override
        public int getItemViewType(int position) {
            return newsFactsMediaList.get(position).getType();
        }

        @NonNull
        @Override
        public NewsFactsMediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view=null;

            if(viewType==NewsFactsMedia.NEWS||viewType==NewsFactsMedia.VIDEO)
            {
                view=LayoutInflater.from(getContext()).inflate(R.layout.news_media_layout,
                        parent,false);
            }
            else if(viewType==NewsFactsMedia.FACT)
            {
                view=LayoutInflater.from(getContext()).inflate(R.layout.fact_layout,
                        parent,false);
            }
            else
            {
                view=LayoutInflater.from(getContext()).inflate(R.layout.news_media_layout,
                        parent,false);
            }

            return new NewsFactsMediaHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsFactsMediaHolder holder, int position) {

            final NewsFactsMedia newsFactsMedia=newsFactsMediaList.get(position);

            if(newsFactsMedia.getType()==NewsFactsMedia.FACT)
            {
                holder.title.setText(newsFactsMedia.getTitle().replace("+"," "));
                holder.desc.setText(newsFactsMedia.getDefinition());
            }
            else if(newsFactsMedia.getType()==NewsFactsMedia.VIDEO)
            {
                holder.title.setText(newsFactsMedia.getTitle());
                holder.desc.setText("Video");

                String videoId=ImageUtil.getVideoId(newsFactsMedia.getLink());
                if(videoId!=null)
                {
                    //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                    loadImg(videoId,holder.thumbnail,position);

                }

                //holder.num_videos.setVisibility(View.VISIBLE);
                holder.action_panel.setVisibility(View.VISIBLE);
                holder.viewmore.setVisibility(View.VISIBLE);
                holder.play_video.setVisibility(View.VISIBLE);

                holder.play_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /*new MaterialDialog.Builder(getActivity())
                                .title("Just to Help")
                                .content("Are you using unlimited internet or limited data plan?")
                                .positiveText("Unlimited internet")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                        videoMenuHelper=new VideoMenuHelper(null,getActivity());
                                        videoMenuHelper.setIs_unlimited(true);
                                        videoMenuHelper.ShowDialog(newsFactsMedia.getLink()
                                                ,newsFactsMedia.getTitle());


                                    }
                                })
                                .negativeText("Limited Data")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog,
                                                        @NonNull DialogAction which) {

                                        videoMenuHelper=new VideoMenuHelper(null,getActivity());
                                        videoMenuHelper.setIs_unlimited(false);
                                        videoMenuHelper.ShowDialog(newsFactsMedia.getLink()
                                                ,newsFactsMedia.getTitle());



                                    }
                                })
                                .cancelable(false)
                                .show();

                         */

                        Intent intent=new Intent(getActivity(), PlayVideoActivity.class);
                        intent.putExtra(PlayVideoActivity.TITLE,newsFactsMedia.getTitle());
                        intent.putExtra(PlayVideoActivity.URL,newsFactsMedia.getLink());
                        startActivity(intent);


                    }
                });


            }
            else if(newsFactsMedia.getType()==NewsFactsMedia.NEWS)
            {


                if(newsFactsMedia.getThumbnail_url().isEmpty())
                {
                    holder.thumbnail.setVisibility(View.GONE);
                }
                else
                {

                    holder.thumbnail.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).clear(holder.thumbnail);
                    Glide.with(getActivity())
                            .load(Uri.parse(newsFactsMedia.getThumbnail_url()))
                            .into(holder.thumbnail);

                }
                if(newsFactsMedia.getIcon().isEmpty()==false)
                {
                    holder.icon.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).clear(holder.icon);
                    Glide.with(getActivity())
                            .load(Uri.parse(newsFactsMedia.getIcon()))
                            .into(holder.icon);
                }
                holder.title.setText(newsFactsMedia.getTitle().replace("+"," "));
                if(newsFactsMedia.getTitle().isEmpty()==false)
                {
                    String hj=newsFactsMedia.getTitle();
                    if(hj.indexOf("...")>-1)
                    {
                        hj=hj.substring(0,hj.indexOf("..."));
                        scrapeLink(position,newsFactsMedia.getLink(),hj);

                    }
                    else
                    {
                        holder.action_panel.setVisibility(View.VISIBLE);
                        holder.viewmore.setText("TALK ABOUT THIS");
                        holder.viewmore.setVisibility(View.VISIBLE);

                        holder.play_video.setVisibility(View.GONE);
                    }
                }

                if(newsFactsMedia.getSource().isEmpty()!=false)
                {
                    holder.desc.setText("NEWS:"+newsFactsMedia.getSource());
                    Log.i("hsygai","position="+position+" "+newsFactsMedia.getSource()
                            +" "+newsFactsMedia.getSource().isEmpty());
                }
                else
                {
                    holder.desc.setText("NEWS");
                }






            }

        }



        @Override
        public int getItemCount() {
            return newsFactsMediaList.size();
        }

        public class NewsFactsMediaHolder extends RecyclerView.ViewHolder {

            //News/Media views
            public ImageView thumbnail;
            public ImageView icon;

            //The Facts Layout views
            public LinearLayout rootview;
            //Common views between fact and news/media views
            public ExpandableTextView title;
            public ExpandableTextView desc;
            //action views
            public LinearLayout action_panel;
            public TextView viewmore;
            public TextView play_video;

            public NewsFactsMediaHolder(@NonNull View itemView) {
                super(itemView);

                rootview=(LinearLayout) itemView.findViewById(R.id.rootview);
                thumbnail=(ImageView) itemView.findViewById(R.id.thumbnail);
                icon=(ImageView) itemView.findViewById(R.id.icon);
                title=(ExpandableTextView) itemView.findViewById(R.id.title);
                desc=(ExpandableTextView) itemView.findViewById(R.id.desc);
                //action views
                action_panel=(LinearLayout) itemView.findViewById(R.id.action_panel);
                viewmore=(TextView) itemView.findViewById(R.id.viewmore);
                play_video=(TextView) itemView.findViewById(R.id.play_video);

            }

        }

        public void loadImg(String videoId, ImageView imageView, Integer position)
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            if(videoId!=null)
            {
                //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                if(videoId.isEmpty()==false)
                {

                    Glide.with(getActivity())
                            .load(Uri.parse("https://img.youtube.com/vi/"
                                    +videoId+"/0.jpg"))
                            .centerCrop()
                            .apply(options)
                            .into(imageView);
                    Log.i("gysha","videoID="+videoId);
                }

            }

        }


    }

    private class VideoAdapter extends
            RecyclerView.Adapter<VideoAdapter.NewsFactsMediaHolder>
    {

        @Override
        public int getItemViewType(int position) {
            return videolist.get(position).getType();
        }

        @NonNull
        @Override
        public NewsFactsMediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view=null;

            view=LayoutInflater.from(getContext()).inflate(R.layout.news_media_layout,
                    parent,false);

            return new NewsFactsMediaHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsFactsMediaHolder holder, int position) {

            final NewsFactsMedia newsFactsMedia=videolist.get(position);

            if(newsFactsMedia.getType()==NewsFactsMedia.VIDEO)
            {
                holder.title.setText(newsFactsMedia.getTitle());
                holder.desc.setText("Video");

                String videoId=ImageUtil.getVideoId(newsFactsMedia.getLink());
                if(videoId!=null)
                {
                    //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                    loadImg(videoId,holder.thumbnail,position);

                }

                //holder.num_videos.setVisibility(View.VISIBLE);
                holder.action_panel.setVisibility(View.VISIBLE);
                holder.viewmore.setVisibility(View.VISIBLE);
                holder.play_video.setVisibility(View.VISIBLE);

                holder.play_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /*new MaterialDialog.Builder(getActivity())
                                .title("Just to Help")
                                .content("Are you using unlimited internet or limited data plan?")
                                .positiveText("Unlimited internet")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {


                                        videoMenuHelper=new VideoMenuHelper(null,getActivity());
                                        videoMenuHelper.setIs_unlimited(true);
                                        videoMenuHelper.ShowDialog(newsFactsMedia.getLink()
                                                ,newsFactsMedia.getTitle());


                                    }
                                })
                                .negativeText("Limited Data")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog,
                                                        @NonNull DialogAction which) {

                                        videoMenuHelper=new VideoMenuHelper(null,getActivity());
                                        videoMenuHelper.setIs_unlimited(false);
                                        videoMenuHelper.ShowDialog(newsFactsMedia.getLink()
                                                ,newsFactsMedia.getTitle());



                                    }
                                })
                                .cancelable(false)
                                .show();

                         */

                        Intent intent=new Intent(getActivity(), PlayVideoActivity.class);
                        intent.putExtra(PlayVideoActivity.TITLE,newsFactsMedia.getTitle());
                        intent.putExtra(PlayVideoActivity.URL,newsFactsMedia.getLink());
                        startActivity(intent);


                    }
                });


            }
            else if(newsFactsMedia.getType()==NewsFactsMedia.NEWS)
            {

                if(newsFactsMedia.getThumbnail_url().isEmpty())
                {
                    holder.thumbnail.setVisibility(View.GONE);
                }
                else
                {

                    holder.thumbnail.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).clear(holder.thumbnail);
                    Glide.with(getActivity())
                            .load(Uri.parse(newsFactsMedia.getThumbnail_url()))
                            .into(holder.thumbnail);

                }
                if(newsFactsMedia.getIcon().isEmpty()==false)
                {
                    holder.icon.setVisibility(View.VISIBLE);
                    Glide.with(getActivity()).clear(holder.icon);
                    Glide.with(getActivity())
                            .load(Uri.parse(newsFactsMedia.getIcon()))
                            .into(holder.icon);
                }
                holder.title.setText(newsFactsMedia.getTitle().replace("+"," "));
                if(newsFactsMedia.getTitle().isEmpty()==false)
                {
                    String hj=newsFactsMedia.getTitle();
                    if(hj.indexOf("...")>-1)
                    {
                        hj=hj.substring(0,hj.indexOf("..."));
                        scrapeLink(position,newsFactsMedia.getLink(),hj);

                    }
                    else
                    {
                        holder.action_panel.setVisibility(View.VISIBLE);
                        holder.viewmore.setText("TALK ABOUT THIS");
                        holder.viewmore.setVisibility(View.VISIBLE);

                        holder.play_video.setVisibility(View.GONE);
                    }
                }
                if(newsFactsMedia.getSource().isEmpty()!=false)
                {
                    holder.desc.setText("NEWS:"+newsFactsMedia.getSource());
                }
                else
                {
                    holder.desc.setText("NEWS");
                }





            }

        }



        @Override
        public int getItemCount() {
            return videolist.size();
        }

        public class NewsFactsMediaHolder extends RecyclerView.ViewHolder {

            //News/Media views
            public ImageView thumbnail;
            public ImageView icon;

            //The Facts Layout views
            public LinearLayout rootview;
            //Common views between fact and news/media views
            public ExpandableTextView title;
            public ExpandableTextView desc;
            //action views
            public LinearLayout action_panel;
            public TextView viewmore;
            public TextView play_video;

            public NewsFactsMediaHolder(@NonNull View itemView) {
                super(itemView);

                rootview=(LinearLayout) itemView.findViewById(R.id.rootview);
                thumbnail=(ImageView) itemView.findViewById(R.id.thumbnail);
                icon=(ImageView) itemView.findViewById(R.id.icon);
                title=(ExpandableTextView) itemView.findViewById(R.id.title);
                desc=(ExpandableTextView) itemView.findViewById(R.id.desc);
                //action views
                action_panel=(LinearLayout) itemView.findViewById(R.id.action_panel);
                viewmore=(TextView) itemView.findViewById(R.id.viewmore);
                play_video=(TextView) itemView.findViewById(R.id.play_video);

            }

        }

        public void loadImg(String videoId, ImageView imageView, Integer position)
        {

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            if(videoId!=null)
            {
                //https://i.ytimg.com/vi/fhWaJi1Hsfo/sddefault.jpg
                if(videoId.isEmpty()==false)
                {

                    Glide.with(getActivity())
                            .load(Uri.parse("https://img.youtube.com/vi/"
                                    +videoId+"/0.jpg"))
                            .centerCrop()
                            .apply(options)
                            .into(imageView);
                    Log.i("gysha","videoID="+videoId);
                }

            }

        }


    }

    private void setUpList()
    {

        new_media_facts_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        newsFactsMediaAdapter=new NewsFactsMediaAdapter();
        new_media_facts_list.setAdapter(newsFactsMediaAdapter);

        video_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoAdapter=new VideoAdapter();
        video_list.setAdapter(videoAdapter);

        getImages();
        getNewsSuggestions();
        getFact();
        getVideos();

    }

    int num_videos_more=9;
    private void getVideos()
    {


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url="https://www.google.com/search?q=youtube "+convo_title+"&tbm=vid";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Document doc = Jsoup.parse(response);
                        Elements body=doc.getElementsByTag("body");

                        Log.i("chsihd","got the response");
                        if(body!=null)
                        {

                            if(body.isEmpty()==false)
                            {

                                Log.i("chsihd","body is not null ");
                                Element body_1=body.get(0);
                                Elements links = body_1.getElementsByTag("a");

                                boolean found=false;
                                for (Element link : links)
                                {

                                    String linkHref = link.attr("href");
                                    String videoId= ImageUtil.getVideoId(linkHref);
                                    boolean has_video_id=false;
                                    if(videoId!=null)
                                    {
                                        if(videoId.isEmpty()==false)
                                        {
                                            has_video_id=true;
                                        }
                                    }
                                    String linkText = link.text();
                                    Elements imgs=link.getElementsByTag("img");

                                    if(imgs.isEmpty()==false&link.hasAttr("aria-label")
                                            &has_video_id&ImageUtil.isYoutubeUrl(linkHref))
                                    {

                                        String heading=link.attr("aria-label");
                                        Element thumbnail=imgs.get(0);
                                        if(thumbnail.hasAttr("src"))
                                        {
                                            String srcdata=thumbnail.attr("src");
                                            Log.i("chsihd",
                                                    "linkHref="+linkHref+" "+heading+" "+srcdata);

                                            NewsFactsMedia newsFactsMedia=new NewsFactsMedia();
                                            newsFactsMedia.setType(NewsFactsMedia.VIDEO);
                                            newsFactsMedia.setTitle(heading);
                                            newsFactsMedia.setLink(linkHref);
                                            videolist.add(newsFactsMedia);

                                            found=true;
                                            num_videos_more=links.size()-1;
                                            break;

                                        }

                                    }

                                }

                                videoAdapter.notifyDataSetChanged();

                            }

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @BindView(R.id.images_list)
    RecyclerView images;
    @BindView(R.id.video_list)
    RecyclerView video_list;
    ImagesAdapter imagesAdapter;
    private void getImages()
    {

        imagesAdapter=new ImagesAdapter();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        images.setLayoutManager(linearLayoutManager);
        images.setAdapter(imagesAdapter);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://www.google.com/search?q="+convo_title+"&tbm=isch";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Display the first 500 characters of the response string.
                        Log.i("lksuaghd","got the response");
                        Document doc = Jsoup.parse(response);
                        Elements body=doc.getElementsByTag("body");

                        if(body!=null)
                        {

                            if(body.isEmpty()==false)
                            {

                                Log.i("lxksfa","body is not null ");
                                Element body_1=body.get(0);

                                Elements images=body_1.select("img[data-src]");
                                Log.i("lxksfa","images isEmpty="+images.isEmpty());
                                for(Element image : images)
                                {

                                    if(image.hasAttr("alt")&image.hasAttr("data-src"))
                                    {

                                        String alt=image.attr("alt");
                                        if(alt.trim().isEmpty()==false)
                                        {

                                            Log.i("lxksfa","image="+image.outerHtml());
                                            String image_link=image.attr("data-src");
                                            String image_desc=image.attr("alt");

                                            NewsFactsMedia newsFactsMedia=new NewsFactsMedia();
                                            newsFactsMedia.setType(NewsFactsMedia.IMAGE);
                                            newsFactsMedia.setTitle(image_desc);
                                            newsFactsMedia.setLink(image_link);
                                            images_list.add(newsFactsMedia);

                                        }

                                    }

                                }

                                imagesAdapter.notifyDataSetChanged();

                            }

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void getFact()
    {

        List<String> helping_verbs_prepositions=new ArrayList<String>();
        helping_verbs_prepositions.add("is");
        helping_verbs_prepositions.add("has");
        helping_verbs_prepositions.add("are");
        helping_verbs_prepositions.add("was");
        helping_verbs_prepositions.add("will");
        helping_verbs_prepositions.add("on");
        helping_verbs_prepositions.add("over");
        helping_verbs_prepositions.add("at");
        helping_verbs_prepositions.add("around");
        helping_verbs_prepositions.add("for");
        helping_verbs_prepositions.add("in");
        helping_verbs_prepositions.add("to");
        helping_verbs_prepositions.add("aboard");
        helping_verbs_prepositions.add("about");
        helping_verbs_prepositions.add("above");
        helping_verbs_prepositions.add("across");
        helping_verbs_prepositions.add("against");
        helping_verbs_prepositions.add("along");
        helping_verbs_prepositions.add("amid");
        helping_verbs_prepositions.add("among");
        helping_verbs_prepositions.add("as");
        helping_verbs_prepositions.add("at");
        helping_verbs_prepositions.add("before");
        helping_verbs_prepositions.add("behind");
        helping_verbs_prepositions.add("below");
        helping_verbs_prepositions.add("beneath");
        helping_verbs_prepositions.add("beside");
        helping_verbs_prepositions.add("’");
        helping_verbs_prepositions.add("between");
        helping_verbs_prepositions.add("beyond");
        helping_verbs_prepositions.add("but");
        helping_verbs_prepositions.add("by");
        helping_verbs_prepositions.add("concerning");
        helping_verbs_prepositions.add("considering");
        helping_verbs_prepositions.add("despite");
        helping_verbs_prepositions.add("down");
        helping_verbs_prepositions.add("during");
        helping_verbs_prepositions.add("except");
        helping_verbs_prepositions.add("excepting");
        helping_verbs_prepositions.add("excluding");
        helping_verbs_prepositions.add("following");
        helping_verbs_prepositions.add("for");
        helping_verbs_prepositions.add("from");
        helping_verbs_prepositions.add("in");
        helping_verbs_prepositions.add("inside");
        helping_verbs_prepositions.add("into");
        helping_verbs_prepositions.add("like");
        helping_verbs_prepositions.add("minus");
        helping_verbs_prepositions.add("near");
        helping_verbs_prepositions.add("of");
        helping_verbs_prepositions.add("off");
        helping_verbs_prepositions.add("on");
        helping_verbs_prepositions.add("onto");
        helping_verbs_prepositions.add("opposite");
        helping_verbs_prepositions.add("outside");
        helping_verbs_prepositions.add("over");
        helping_verbs_prepositions.add("past");
        helping_verbs_prepositions.add("per");
        helping_verbs_prepositions.add("plus");
        helping_verbs_prepositions.add("regarding");
        helping_verbs_prepositions.add("round");
        helping_verbs_prepositions.add("save");
        helping_verbs_prepositions.add("since");
        helping_verbs_prepositions.add("than");
        helping_verbs_prepositions.add("through");
        helping_verbs_prepositions.add("to");
        helping_verbs_prepositions.add("toward");
        helping_verbs_prepositions.add("towards");
        helping_verbs_prepositions.add("under");
        helping_verbs_prepositions.add("underneath");
        helping_verbs_prepositions.add("unlike");
        helping_verbs_prepositions.add("until");
        helping_verbs_prepositions.add("up");
        helping_verbs_prepositions.add("upon");
        helping_verbs_prepositions.add("versus");
        helping_verbs_prepositions.add("via");
        helping_verbs_prepositions.add("with");
        helping_verbs_prepositions.add("within");
        helping_verbs_prepositions.add("without");

        helping_verbs_prepositions.add("and");
        helping_verbs_prepositions.add("or");
        helping_verbs_prepositions.add("but");
        helping_verbs_prepositions.add("yet");
        helping_verbs_prepositions.add("so");
        helping_verbs_prepositions.add("nor");
        helping_verbs_prepositions.add("after");
        helping_verbs_prepositions.add("although");
        helping_verbs_prepositions.add("as if");
        helping_verbs_prepositions.add("as");
        helping_verbs_prepositions.add("as long as");//As long as
        helping_verbs_prepositions.add("as much as");//As much as
        helping_verbs_prepositions.add("as soon as");//as soon as
        helping_verbs_prepositions.add("as though");//as though
        helping_verbs_prepositions.add("because");//because
        helping_verbs_prepositions.add("before");//before
        helping_verbs_prepositions.add("even if");//even if
        helping_verbs_prepositions.add("even");//even
        helping_verbs_prepositions.add("even though");//even though
        helping_verbs_prepositions.add("if");//if
        helping_verbs_prepositions.add("if only");//if only
        helping_verbs_prepositions.add("If then");//If then
        helping_verbs_prepositions.add("In order that");//In order that

        helping_verbs_prepositions.add("and");
        helping_verbs_prepositions.add("or");
        helping_verbs_prepositions.add("nor");
        helping_verbs_prepositions.add("but");

        helping_verbs_prepositions.add("for");
        helping_verbs_prepositions.add("after");
        helping_verbs_prepositions.add("although");
        helping_verbs_prepositions.add("as if");
        helping_verbs_prepositions.add("as");
        helping_verbs_prepositions.add("as long as");
        helping_verbs_prepositions.add("as much as");
        helping_verbs_prepositions.add("as soon as");
        helping_verbs_prepositions.add("as though");
        helping_verbs_prepositions.add("because");//because
        helping_verbs_prepositions.add("before");//before
        helping_verbs_prepositions.add("even if");//even if
        helping_verbs_prepositions.add("even");//even
        helping_verbs_prepositions.add("even though");//even though
        helping_verbs_prepositions.add("if");//if
        helping_verbs_prepositions.add("if only");//if only
        helping_verbs_prepositions.add("If then");//If then
        helping_verbs_prepositions.add("In order that");//In order that



        String searchkey=convo_title;
        PartExtractor partExtractor=new PartExtractor();
        searchkey=partExtractor.ExtractPartBeforePos(helping_verbs_prepositions,searchkey);

        if(searchkey.split("’").length>1)
        {
            searchkey=searchkey.split("’")[0];
        }
        searchkey=searchkey.trim().replace(" ","+");
        final String noun=searchkey;
        Log.i("mksla","searchkey="+searchkey);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://www.google.com/search?q=about+"+searchkey;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.


                        Document doc = Jsoup.parse(response);
                        Elements body=doc.getElementsByTag("body");

                        Elements abouts=body.get(0).select("h3:contains(Description)");

                        boolean found_description=false;
                        boolean found_source=false;
                        String description="";
                        String source="";


                        if(abouts.isEmpty()==false)
                        {
                            for(Element element:abouts)
                            {
                                if(element.text().equals("Description"))
                                {
                                    Log.i("mksla",element
                                            .text());
                                    for(Element element1:element.nextElementSiblings())
                                    {

                                        if(element1.tagName().equals("span"))
                                        {

                                            if(found_description==false)
                                            {
                                                found_description=true;
                                                description=element1.text();
                                            }
                                            else if(found_description&found_source==false)
                                            {
                                                found_source=true;
                                                source=element1.text();
                                            }
                                        }

                                    }

                                    Log.i("mksla","description="+description
                                            +" source="+source);
                                    Log.i("mksla","source="+source);
                                    Log.i("mksla","noun="+noun);

                                }
                            }
                        }

                        if(found_description&found_source)
                        {

                            NewsFactsMedia newsFactsMedia=new NewsFactsMedia();
                            newsFactsMedia.setType(NewsFactsMedia.FACT);
                            newsFactsMedia.setTitle(noun);
                            newsFactsMedia.setSource(source);
                            newsFactsMedia.setDefinition(description);
                            newsFactsMediaList.add(0,newsFactsMedia);
                            newsFactsMediaAdapter.notifyItemInserted(0);

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("mksla","error="+error.getMessage());
            }

        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesHolder>
    {


        @NonNull
        @Override
        public ImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.image_news_facf,
                    parent,false);
            return new ImagesHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImagesHolder holder, int position)
        {

            final NewsFactsMedia newsFactsMedia=images_list.get(position);

            Glide.with(getActivity())
                    .load(newsFactsMedia.getLink())
                    .centerCrop()
                    .into(holder.image);

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CommentListener commentListener=(CommentListener) getActivity();
                    if(commentListener!=null)
                    {
                        commentListener.onImageComment(newsFactsMedia);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return images_list.size();
        }

        public class ImagesHolder extends RecyclerView.ViewHolder {

            public ImageView image;
            public ImagesHolder(@NonNull View itemView) {
                super(itemView);

                image=(ImageView) itemView.findViewById(R.id.image);

            }

        }


    }

    private void getNewsSuggestions()
    {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://www.google.com/search?q=kevin+samuels+death&tbm=nws";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.i("getNewsSuggestions","onResponse");

                        Document doc = Jsoup.parse(response);
                        Elements body=doc.getElementsByTag("body");

                        if(body!=null)
                        {

                            if(body.isEmpty()==false)
                            {

                                Element body_1=body.get(0);
                                Elements alinks=body_1.select("a:matches(\\b(?:[a-z]){3,4}\\b)");

                                Log.i("getNewsSuggestions","alinks.isEmpty()="
                                        +(alinks.isEmpty()));

                                for(int i=0;i<alinks.size();i++)
                                {

                                    Element a=alinks.get(i);
                                    Elements spans=a.select("span:matches(\\b(?:[a-z]{0,10})\\b||\\b\\d{0,10})");
                                    Elements gms=a.getElementsByTag("span");
                                    if(a.hasParent())
                                    {
                                        gms=a.parent().getElementsByTag("span");
                                    }

                                    Elements djks=a.select("div[role=heading]");

                                    String sourcelink=a.attr("href");
                                    String heading="";
                                    String source="";
                                    String timestamp="";
                                    if(djks.isEmpty()==false)
                                    {
                                        Log.i("getNewsSuggestions",djks.get(0).text());
                                        heading=djks.get(0).text();

                                        if(spans.isEmpty()==false)
                                        {

                                            for(Element span:spans)
                                            {
                                                String p="^\\d\\s(minutes|weeks|moments|years|days|hours|months|a minute|a week|a moment|a year|a day|a hour|a month|minute|week|moment|year|day|hour|month|minute)\\s(ago)$";
                                                String p2="\\d\\s[A-Z]{1}[a-z]{2,8}\\s\\d{4}";
                                                Pattern pattern = Pattern.compile(p, Pattern.CASE_INSENSITIVE);
                                                Pattern pattern2 = Pattern.compile(p2, Pattern.CASE_INSENSITIVE);
                                                Matcher matcher = pattern.matcher(span.text());
                                                Matcher matcher2 = pattern2.matcher(span.text());
                                                boolean matchFound = matcher.find();
                                                boolean matchFound2 = matcher2.find();
                                                if(matchFound||matchFound2)
                                                {
                                                    timestamp=span.text();
                                                }
                                                else
                                                {
                                                    source=span.text();
                                                }

                                            }

                                        }
                                    }

                                    //decide whether to scrap the link for the complete title
                                    if(heading.indexOf("...")>-1||source.isEmpty()==false)
                                    {
                                        Log.i("getNewsSuggestions","scraping "+i
                                                +" "+heading+" "+source);
                                        String hj=heading;
                                        if(hj.indexOf("...")>-1)
                                        {
                                            hj=hj.substring(0,hj.indexOf("..."));
                                        }

                                    }
                                    else
                                    {
                                        Log.i("getNewsSuggestions",i
                                                +" "+heading+" "+source+" "+timestamp);
                                    }

                                    for(Element span:gms)
                                    {

                                        Pattern p = Pattern.compile("\\d\\s(?:[a-z]{3,10})\\s(ago)||(a)\\s(?:[a-z]{3,10})\\s(ago)||\\d\\s(?:[a-z]{3,10})\\s\\d{4}");
                                        Matcher m = p.matcher(span.text());

                                        if(span.text().trim().isEmpty()==false&m.matches()==false)
                                        {

                                            Log.i("jusgald_"+i,"i="+i+" text="+span.text());
                                            source=span.text();


                                        }
                                    }

                                    if(heading.isEmpty()==false&sourcelink.isEmpty()==false)
                                    {


                                        NewsFactsMedia newsFactsMedia=new NewsFactsMedia();
                                        newsFactsMedia.setTitle(heading);
                                        newsFactsMedia.setSource(source);
                                        newsFactsMedia.setLink(sourcelink);
                                        newsFactsMedia.setType(NewsFactsMedia.NEWS);
                                        Log.i("jsnajs","source="+newsFactsMedia.getSource()+" i="+i);
                                        newsFactsMediaList.add(newsFactsMedia);



                                    }



                                }

                                newsFactsMediaAdapter.notifyDataSetChanged();

                            }

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("getNewsSuggestions","error="+error.getMessage());

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void scrapeLink(int i, String sourcelink, String hj)
    {


        Log.i("found_title_"+(i+offset),"start scraping for "+(i+offset));
        Log.i("getNewsSuggestions","start scraping for "+(i+offset)+" "+hj);
        sourcelink=sourcelink.replace("http:","https:");
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = sourcelink;
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Display the first 500 characters of the response string.
                        Log.i("getNewsSuggestions","onResponse for "+(i+offset));
                        Log.i("found_title_"+(i+offset),"onResponse "+(i+offset));

                        Document doc = Jsoup.parse(response);
                        Element body=doc.body();
                        Elements heads=doc.getElementsByTag("head");

                        NewsFactsMedia newsFactsMedia=newsFactsMediaList.get((i+offset));

                        if(heads!=null
                                &newsFactsMedia.getTitle().indexOf("...")>-1)
                        {

                            if(heads.isEmpty()==false)
                            {

                                Element head= heads.get(0);

                                Elements link_con=head.select("link[rel=icon]");
                                Elements link_con2=head.select("link[rel=shortcut icon]");
                                Elements title=head.getElementsByTag("title");

                                if(title.isEmpty()==false)
                                {


                                    if(title.get(0).text().indexOf(newsFactsMedia.getTitle()
                                            .replace("...",""))>-1)
                                    {
                                        newsFactsMedia.setTitle(title.get(0).text());
                                        newsFactsMediaList.set((i+offset),newsFactsMedia);
                                    }
                                    else
                                    {
                                        newsFactsMedia=newsFactsMediaList.get((i));
                                        if(title.get(0).text().indexOf(newsFactsMedia.getTitle()
                                                .replace("...",""))>-1)
                                        {
                                            newsFactsMedia.setTitle(title.get(0).text());
                                            newsFactsMediaList.set((i+offset),newsFactsMedia);
                                            offset++;
                                        }

                                    }

                                    Log.i("getNewsSuggestions","title for "+(i+offset)+" "+title.get(0).text());
                                    Log.i("found_title_"+(i+offset),"title for "+title.get(0).text());
                                }
                                else
                                {
                                    Log.i("found_title_"+(i+offset),"false no title");
                                }
                                if(link_con.isEmpty()==false)
                                {
                                    if(link_con.get(0).hasAttr("href"))
                                    {
                                        Log.i("getNewsSuggestions","icon for "+(i+offset)+" "
                                                +link_con.get(0).attr("href"));

                                        newsFactsMedia.setIcon(link_con.get(0).attr("href"));
                                        newsFactsMediaList.set((i+offset),newsFactsMedia);
                                    }
                                }
                                if(link_con2.isEmpty()==false&link_con.isEmpty())
                                {
                                    if(link_con2.get(0).hasAttr("href"))
                                    {
                                        Log.i("getNewsSuggestions","2icon for "+(i+offset)+" "
                                                +link_con2.get(0).attr("href"));

                                        newsFactsMedia.setIcon(link_con2.get(0).attr("href"));
                                        newsFactsMediaList.set((i+offset),newsFactsMedia);
                                    }
                                }





                            }
                            else
                            {
                                Log.i("found_title_"+(i+offset),"false no head 1");
                            }

                        }
                        else if(newsFactsMedia.getTitle().indexOf("...")>-1)
                        {
                            Log.i("found_title_"+(i+offset),"false no head 2");
                        }

                        //try to get some thumbnail for the news
                        if(body!=null)
                        {

                            Element h1=body.selectFirst("h1");
                            Element h2=body.selectFirst("h2");
                            Element h3=body.selectFirst("h3");
                            Element h4=body.selectFirst("h4");
                            Element h5=body.selectFirst("h5");

                            if(h1!=null)
                            {

                                Log.i("found_title_"+(i+offset),"h1 is not null");
                                //look for the next image element which is sibiling of this heading

                                if(newsFactsMedia.getTitle().trim().indexOf("...")>-1)
                                {
                                    newsFactsMedia.setTitle(h1.text());
                                    newsFactsMediaList.set((i+offset),newsFactsMedia);
                                }




                            }
                            if(h2!=null)
                            {

                                Log.i("found_title_"+(i+offset),"h1 is not null");
                                //look for the next image element which is sibiling of this heading

                                if(newsFactsMedia.getTitle().trim().indexOf("...")>-1)
                                {
                                    newsFactsMedia.setTitle(h2.text());
                                    newsFactsMediaList.set((i+offset),newsFactsMedia);
                                }



                            }
                            if(h3!=null)
                            {

                                Log.i("found_title_"+(i+offset),"h1 is not null");
                                //look for the next image element which is sibiling of this heading

                                if(newsFactsMedia.getTitle().trim().indexOf("...")>-1)
                                {
                                    newsFactsMedia.setTitle(h3.text());
                                    newsFactsMediaList.set((i+offset),newsFactsMedia);
                                }



                            }
                            if(h4!=null)
                            {

                                Log.i("found_title_"+(i+offset),"h1 is not null");
                                //look for the next image element which is sibiling of this heading

                                if(newsFactsMedia.getTitle().trim().indexOf("...")>-1)
                                {
                                    newsFactsMedia.setTitle(h4.text());
                                    newsFactsMediaList.set((i+offset),newsFactsMedia);
                                }


                            }
                            if(h5!=null)
                            {

                                Log.i("found_title_"+(i+offset),"h1 is not null");
                                //look for the next image element which is sibiling of this heading

                                if(newsFactsMedia.getTitle().trim().indexOf("...")>-1)
                                {
                                    newsFactsMedia.setTitle(h5.text());
                                    newsFactsMediaList.set((i+offset),newsFactsMedia);
                                }



                            }



                        }

                        newsFactsMediaAdapter.notifyItemChanged((i+offset));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("getNewsSuggestions",i+" "+error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}