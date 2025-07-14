package testBackend;

import backend.model.Metadata;
import backend.downloader.Reddit;
import java.io.IOException;

public class RedditTest {
    public static void reddit(String url){
        Reddit rt = new Reddit(url);

        Metadata data = rt.getMetadata();
        System.out.println("Title: " + data.getTitle());
        System.out.println("Channel: " + data.getChannelName());
        System.out.println("Thumbnail: " + data.getThumbnailUrl());

        rt.downloadVideo("C:\\Games\\videos");

    }
}

