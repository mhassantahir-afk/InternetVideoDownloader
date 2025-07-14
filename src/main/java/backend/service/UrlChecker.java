package backend.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlChecker {
    private String videoUrl;
    private Pattern ValidURLRegex = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private Pattern youtubeurlRegex = Pattern.compile("^(?:https?://)?(?:www\\.)?(?:(?:youtu\\.be/)|(?:youtube\\.com/(?:watch\\?(?:.*&)?v=|embed/|shorts/|live/)))([A-Za-z0-9_-]{11})(?:[?&][^\\s]*)?$");
    private Pattern instagramRegex = Pattern.compile("https?:\\/\\/(www\\.)?instagram\\.com\\/(reel|p)\\/[A-Za-z0-9_-]+");
    private Pattern redditRegex = Pattern.compile("https?://(www\\.)?reddit\\.com/r/[a-zA-Z0-9_]+/comments/[a-zA-Z0-9]+(/[^\\s]*)?");
    private Pattern facebookRegex = Pattern.compile(".*(?:facebook\\.com|fb\\.watch).*");

    public UrlChecker(String url){
        this.videoUrl = url;
    }

    public boolean isvalidURL(){
        Matcher m = ValidURLRegex.matcher(videoUrl);
        return m.find();
    }

    public String platformName(){
        Matcher y = youtubeurlRegex.matcher(videoUrl);
        Matcher i = instagramRegex.matcher(videoUrl);
        Matcher r = redditRegex.matcher(videoUrl);
        Matcher f = facebookRegex.matcher(videoUrl);
        if(y.find()){
            return "youtube";
        } else if(i.find()) {
            return "instagram";
        } else if(r.find()) {
            return "reddit";
        } else if(f.find()) {
            return "facebook";
        }
        return null;
    }
}
