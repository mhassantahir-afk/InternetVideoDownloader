package testBackend;

import backend.downloader.Instagram;
import backend.model.Metadata;

public class InstagramTest {
    public static void instagram(String url){
        Instagram insta = new Instagram(url);
        Metadata data = insta.getMetadata();

        System.out.println("Title: " + data.getTitle());
        System.out.println("Channel: " + data.getChannelName());
        System.out.println("Duration: " + data.getDuration());
        System.out.println("Upload Date: " + data.getUploadDate());
        System.out.println("Thumbnail URL: " + data.getThumbnailUrl());


        //insta.downloadVideo("C:\\Games\\videos");


    }


}
