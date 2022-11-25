package brainwind.letstalksample.features.letstalk.fragments.item;

public class MediaItemWebSearch
{


    String heading="";
    String base64Image="";
    String link="";
    String thumnail_url="";
    String timestamp="";
    boolean isVideo=false;
    boolean isImage=false;
    boolean isNews=false;
    boolean isBook=false;
    String source_name="";

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }

    public boolean isBook() {
        return isBook;
    }

    public void setBook(boolean book) {
        isBook = book;
    }

    public String getThumnail_url() {
        return thumnail_url;
    }

    public void setThumnail_url(String thumnail_url) {
        this.thumnail_url = thumnail_url;
    }

    public void setSource(String source_name)
    {
        this.source_name=source_name;
    }
    public String getSource()
    {
        return this.source_name;
    }


    public void setTimeStamp(String timestamp) {
        this.timestamp=timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
