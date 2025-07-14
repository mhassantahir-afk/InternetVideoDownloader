package testBackend;

import backend.service.UrlChecker;
import java.util.Scanner;
import static java.lang.System.out;
import static testBackend.InstagramTest.instagram;
import static testBackend.RedditTest.reddit;
import static testBackend.YouTubeTest.youtube;

public interface EvaluationTestInterface {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        String url;


        out.println("----InternetVideoDownloader----");
        out.print("enter Url: ");

        url = in.nextLine();

        UrlChecker urlchecker = new UrlChecker(url);

        if(urlchecker.isvalidURL()){
            out.println("Url is valid");
            String platformName = urlchecker.platformName();
            switch (platformName){
                case "youtube":
                    out.println("Url is from Youtube");
                    youtube(url);
                    break;
                case "instagram":
                    out.println("Url is from Instagram");
                    instagram(url);
                    break;
                case "reddit":
                    out.println("Url is from Reddit");
                    reddit(url);
                    break;
                default:
                    out.println("Platform Not Supported");
                    break;
            }
        } else {
            out.println("Url is not valid");
        }
    }
}
