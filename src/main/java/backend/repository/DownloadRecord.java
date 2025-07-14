package backend.repository;

public class DownloadRecord {
    private String platform, title, url, quality, filepath, date;

    public DownloadRecord(String platform, String title, String url,
                          String quality, String filepath, String date) {
        this.platform = platform;
        this.title = title;
        this.url = url;
        this.quality = quality;
        this.filepath = filepath;
        this.date = date;
    }

    // Getters
    public String getPlatform() { return platform; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getQuality() { return quality; }
    public String getFilepath() { return filepath; }
    public String getDate() { return date; }
}
