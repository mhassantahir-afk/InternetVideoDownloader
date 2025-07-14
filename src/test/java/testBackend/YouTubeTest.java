package testBackend;

import java.util.Scanner;
import backend.downloader.YouTube;
import backend.model.Metadata;
import java.util.HashMap;
import java.util.Map;

public class YouTubeTest {
    public static void youtube(String url) {
        try {
            YouTube yt = new YouTube(url);

            Scanner in = new Scanner(System.in);
            Metadata data = yt.getMetadata();
            System.out.println("Title: " + data.getTitle());
            System.out.println("Channel: " + data.getChannelName());
            System.out.println("Duration: " + data.getDuration());
            System.out.println("Upload Date: " + data.getUploadDate());
            System.out.println("Thumbnail URL: " + data.getThumbnailUrl());
            System.out.println("Available Qualities: " + data.getQualityList());

            Map<String, String> specs = new HashMap<>();
            System.out.print("Enter your desired quality: ");
            String quality = in.next();
            specs.put("quality", quality);

            //yt.downloadVideo(specs, "C:\\Games\\videos");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
