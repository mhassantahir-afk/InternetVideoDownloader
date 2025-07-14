package backend.model;

import java.util.List;

public class Metadata {
    private String sourceUrl;
    private String platform;
    private String title;
    private String channelName;
    private String duration;
    private String uploadDate;
    private String thumbnailUrl;
    private List<String> qualityList;


    public Metadata() {}

    public String getSourceUrl() { return sourceUrl; }

    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getPlatform() {return platform;}
    public void setPlatform(String platform) { this.platform = platform; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getUploadDate() { return uploadDate; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public List<String> getQualityList() { return qualityList; }
    public void setQualityList(List<String> qualityList) { this.qualityList = qualityList; }
}
