package brainwind.letstalksample.bookie_activity;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

import brainwind.letstalksample.data.database.OrgFields;

public class BookieActivity
{

    /*
    Activity Head
    ACTIVITY_ID
    ACTIVITY_TITLE
    ACTIVITY_DESC
    ACTIVITY_TYPE
     */

    private String activity_id="";
    private String activity_title="";
    private String activity_desc="";
    private String activity_type="";

        /*
    (Media)
    HAS_ATTACH_MEDIA
    VIDEO_THUMBNAIL
    IS_ATTACH_MEDIA_VIDEO
    IS_ATTACH_MEDIA_AUDIO
    IS_ATTACH_MEDIA_IMAGE
    MEDIA_URL
     */

    private boolean has_attached_media;
    private boolean is_attach_media_video;
    private boolean is_attach_media_audio;
    private boolean is_attach_media_image;
    private String media_url="";
    private String video_thumbnail="";

    /*
    (News/Activity Reference)
    IS_NEWS_REFERENCE
    NEWS_SOURCE
    NEWS_SOURCELINK
    NEWS_HEADLINE
    IS_ACTIVITY_REFERENCE
    ACTIVITY_REFERENCE
     */

    private boolean is_news_reference;
    private String news_source="";
    private String news_sourcelink="";
    private String news_headline="";
    private boolean is_activity_reference;
    private String activity_reference="";

    /*
    (Admin/Creator of Activity)
    UID
    ADMIN_NAME
    ADMIN_PROFILE_PATH
     */

    private String admin_uid="";
    private String admin_name="";
    private String admin_profile_path="";

    /*
    (Group/Organization)
    ORGID
    ORG_MAIN_COLOR
    ORG_ONLINE_PATH
     */

    private String orgname="";
    private String orgid="";
    private String org_main_color="";
    private String org_online_path="";

    /*
    (Time of Focus)
    START_DATE
    END_DATE
     */
    private Date start_date;
    private Date end_date;

    private boolean is_standalone=false;

    public BookieActivity(DocumentSnapshot documentSnapshot)
    {

        if(documentSnapshot!=null)
        {

            //Activity Head
            this.activity_id=documentSnapshot.getId();

            if(documentSnapshot.contains(OrgFields.ACTIVITY_TITLE))
            {
                String title=documentSnapshot.getString(OrgFields.ACTIVITY_TITLE);
                this.activity_title=title;
            }
            if(documentSnapshot.contains(OrgFields.ACTIVITY_DESC))
            {
                String desc=documentSnapshot.getString(OrgFields.ACTIVITY_DESC);
                this.activity_desc=desc;
            }
            if(documentSnapshot.contains(OrgFields.ACTIVITY_TYPE))
            {
                String type=documentSnapshot.getString(OrgFields.ACTIVITY_TYPE);
                this.activity_type=type;
            }

            //Activity Media
             /*
            (Media)
            HAS_ATTACH_MEDIA
            IS_ATTACH_MEDIA_VIDEO
            IS_ATTACH_MEDIA_AUDIO
            IS_ATTACH_MEDIA_IMAGE
            MEDIA_URL
             */
            if(documentSnapshot.contains(OrgFields.HAS_ATTACH_MEDIA))
            {
                this. has_attached_media=documentSnapshot.getBoolean(OrgFields.HAS_ATTACH_MEDIA);
            }
            if(documentSnapshot.contains(OrgFields.VIDEO_THUMBNAIL))
            {
                this.video_thumbnail=documentSnapshot.getString(OrgFields.VIDEO_THUMBNAIL);
            }
            if(documentSnapshot.contains(OrgFields.IS_ATTACH_MEDIA_VIDEO))
            {
                this.is_attach_media_video=documentSnapshot.getBoolean(OrgFields.IS_ATTACH_MEDIA_VIDEO);
            }
            if(documentSnapshot.contains(OrgFields.IS_ATTACH_MEDIA_AUDIO))
            {
                this.is_attach_media_audio=documentSnapshot.getBoolean(OrgFields.IS_ATTACH_MEDIA_AUDIO);
            }
            if(documentSnapshot.contains(OrgFields.IS_ATTACH_MEDIA_IMAGE))
            {
                this.is_attach_media_image=documentSnapshot.getBoolean(OrgFields.IS_ATTACH_MEDIA_IMAGE);
            }
            if(documentSnapshot.contains(OrgFields.MEDIA_URL))
            {
                this.media_url=documentSnapshot.getString(OrgFields.MEDIA_URL);
            }

            /*
            (News/Activity Reference)
            IS_NEWS_REFERENCE
            NEWS_SOURCE
            NEWS_SOURCELINK
            NEWS_HEADLINE
            IS_ACTIVITY_REFERENCE
            ACTIVITY_REFERENCE
             */

            if(documentSnapshot.contains(OrgFields.IS_NEWS_REFERENCE))
            {
                this.is_news_reference=documentSnapshot.getBoolean(OrgFields.IS_NEWS_REFERENCE);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_SOURCE))
            {
                this.news_source=documentSnapshot.getString(OrgFields.NEWS_SOURCE);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_SOURCELINK))
            {
                this.news_sourcelink=documentSnapshot.getString(OrgFields.NEWS_SOURCELINK);
            }
            if(documentSnapshot.contains(OrgFields.NEWS_HEADLINE))
            {
                this.news_headline=documentSnapshot.getString(OrgFields.NEWS_HEADLINE);
            }
            if(documentSnapshot.contains(OrgFields.IS_ACTIVITY_REFERENCE))
            {
                this.is_activity_reference=documentSnapshot.getBoolean(OrgFields.IS_ACTIVITY_REFERENCE);
            }
            if(documentSnapshot.contains(OrgFields.ACTIVITY_REFERENCE))
            {
                this.activity_reference=documentSnapshot.getString(OrgFields.ACTIVITY_REFERENCE);
            }

            /*
            (Admin/Creator of Activity)
            UID
            ADMIN_NAME
            ADMIN_PROFILE_PATH
             */

            if(documentSnapshot.contains(OrgFields.UID))
            {
                String uid=documentSnapshot.getString(OrgFields.UID);
                this.admin_uid=uid;
            }
            if(documentSnapshot.contains(OrgFields.ADMIN_NAME))
            {
                String admin_name=documentSnapshot.getString(OrgFields.ADMIN_NAME);
                this.admin_name=admin_name;
            }
            if(documentSnapshot.contains(OrgFields.ADMIN_PROFILE_PATH))
            {
                String admin_profile_path=documentSnapshot.getString(OrgFields.ADMIN_PROFILE_PATH);
                this.admin_profile_path=admin_profile_path;
            }

             /*
            (Group/Organization)
            ORGID
            ORG_MAIN_COLOR
            ORG_ONLINE_PATH
             */

            if(documentSnapshot.contains(OrgFields.ORG_NAME))
            {
                String orgname=documentSnapshot.getString(OrgFields.ORG_NAME);
                this.orgname=orgname;
            }
            if(documentSnapshot.contains(OrgFields.ORGID))
            {
                String orgid=documentSnapshot.getString(OrgFields.ORGID);
                this.orgid=orgid;
            }
            if(documentSnapshot.contains(OrgFields.ORG_MAIN_COLOR))
            {
                String org_main_color=documentSnapshot.getString(OrgFields.ORG_MAIN_COLOR);
                this.org_main_color=org_main_color;
            }
            if(documentSnapshot.contains(OrgFields.ORG_ONLINE_PATH))
            {
                String org_online_path=documentSnapshot.getString(OrgFields.ORG_ONLINE_PATH);
                this.org_online_path=org_online_path;
            }

            /*
            (Time of Focus)
            START_DATE
            END_DATE
             */

            if(documentSnapshot.contains(OrgFields.START_DATE))
            {
                this.start_date=documentSnapshot.getTimestamp(OrgFields.START_DATE).toDate();

            }
            if(documentSnapshot.contains(OrgFields.END_DATE))
            {
                this.end_date=documentSnapshot.getTimestamp(OrgFields.END_DATE).toDate();

            }

            if(documentSnapshot.contains(OrgFields.IS_STANDALONE_CONVO))
            {
                boolean is_s=documentSnapshot.getBoolean(OrgFields.IS_STANDALONE_CONVO);
                this.is_standalone=is_s;
            }


        }

    }

    public String getActivity_id() {
        return activity_id;
    }

    public String getActivity_title() {
        return activity_title;
    }

    public String getActivity_desc() {
        return activity_desc;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public boolean isHas_attached_media() {
        return has_attached_media;
    }

    public boolean isIs_attach_media_video() {
        return is_attach_media_video;
    }

    public boolean isIs_attach_media_audio() {
        return is_attach_media_audio;
    }

    public boolean isIs_attach_media_image() {
        return is_attach_media_image;
    }

    public String getMedia_url() {
        return media_url;
    }

    public boolean isIs_news_reference() {
        return is_news_reference;
    }

    public String getNews_source() {
        return news_source;
    }

    public String getNews_sourcelink() {
        return news_sourcelink;
    }

    public String getNews_headline() {
        return news_headline;
    }

    public boolean isIs_activity_reference() {
        return is_activity_reference;
    }

    public String getActivity_reference() {
        return activity_reference;
    }

    public String getAdmin_uid() {
        return admin_uid;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public String getAdmin_profile_path() {
        return admin_profile_path;
    }

    public String getOrgname() {
        return orgname;
    }

    public String getOrgid() {
        return orgid;
    }

    public String getOrg_main_color() {
        return org_main_color;
    }

    public String getOrg_online_path() {
        return org_online_path;
    }

    public Date getStart_date() {
        return start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public boolean isIs_standalone() {
        return is_standalone;
    }

    public void setIs_standalone(boolean is_standalone) {
        this.is_standalone = is_standalone;
    }


}
