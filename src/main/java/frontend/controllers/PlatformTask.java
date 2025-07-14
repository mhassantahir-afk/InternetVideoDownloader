package frontend.controllers;

import backend.downloader.Facebook;
import backend.downloader.Instagram;
import backend.downloader.YouTube;
import backend.downloader.Reddit;
import backend.model.Metadata;
import javafx.concurrent.Task;

public class PlatformTask extends Task<Void> {

    private final String platform;
    private final String url;

    private YouTube yt;
    private Reddit reddit;
    private Instagram insta;
    private Facebook fb;

    public PlatformTask(String platform, String url) {
        this.platform = platform;
        this.url = url;
    }

    @Override
    protected Void call() {
        try {
            switch (platform) {
                case "youtube" -> {
                    yt = new YouTube(url);// heavy call
                }
                case "reddit" -> {
                    reddit = new Reddit(url);
                }
                case "instagram" -> {
                    insta = new Instagram(url);
                }
                case "facebook" -> {
                    fb = new Facebook(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public YouTube getYouTube() {
        return yt;
    }

    public Reddit getReddit() {
        return reddit;
    }

    public Facebook getFb() {
        return fb;
    }

    public Instagram getInsta() {
        return insta;
    }
}
