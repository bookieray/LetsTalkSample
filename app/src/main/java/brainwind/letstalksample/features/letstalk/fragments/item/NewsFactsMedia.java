package brainwind.letstalksample.features.letstalk.fragments.item;

public class NewsFactsMedia
{

    public static final int NEWS=0;
    public static final int FACT=1;
    public static final int IMAGE=2;
    public static final int VIDEO=3;

    private int type=NEWS;
    private String link="";
    //if it is a video
    private String thumbnail_url="";
    private String icon="";
    //if it is a video or news
    private String title="";
    private String source="";
    private String definition="";

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
