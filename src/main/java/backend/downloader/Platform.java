package backend.downloader;

import backend.model.Metadata;

public class Platform {
    private String videoUrl;
    private static final String YT_DLP_PATH = "src/main/resources/yt-dlp.exe";
    protected Metadata metadata = new Metadata();

    public Platform(String url){
        this.videoUrl = url;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public static String getYtDlpPath() {
        return YT_DLP_PATH;
    }
}
