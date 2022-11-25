package brainwind.letstalksample.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil
{


    public static String getBase64String(String imageurl)
    {

        if(imageurl.indexOf(",")>-1&imageurl.indexOf(",")<imageurl.length()-1)
        {

            int hj=imageurl.indexOf(",");
            return imageurl.substring(hj+1);

        }
        else
        {
            return imageurl;
        }

    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                getBase64String(base64Str),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public static String getVideoId(@NonNull String videoUrl) {
        String videoId = "";
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);
        if(matcher.find()){
            videoId = matcher.group(1);
        }
        return videoId;
    }

    public static boolean isYoutubeUrl(String youTubeURl)
    {
        try {
            URL url=new URL(youTubeURl);
            return url.getHost().trim().toLowerCase().indexOf("youtube.com")>-1
                    ||url.getHost().trim().toLowerCase().indexOf("youtu.be")>-1;
        } catch (MalformedURLException e) {
            return false;
        }

    }


}
