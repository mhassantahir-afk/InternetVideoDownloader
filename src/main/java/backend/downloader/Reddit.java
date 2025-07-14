package backend.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import backend.model.Metadata;

import org.json.JSONArray;
import org.json.JSONObject;

public class Reddit extends Platform{

    private String redditVideoUrl;

    public Reddit(String url) {
        super(url);
        extractMetadata();
    }

    private void extractMetadata() {
        try {
            String line;
            StringBuilder responseBuilder = new StringBuilder();
            String apiUrl = getVideoUrl() + ".json"; // Reddit API: append .json

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));


            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            JSONArray responseArray = new JSONArray(responseBuilder.toString());
            JSONObject postData = responseArray.getJSONObject(0)
                    .getJSONObject("data")
                    .getJSONArray("children")
                    .getJSONObject(0)
                    .getJSONObject("data");

            /*for(int i=0;i<responseArray.length();i++){
                System.out.println(responseArray.getJSONObject(i));
            }
            System.out.println(postData);*/

            saveMetadata(postData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMetadata(JSONObject postData) {
        try {
            metadata = new Metadata();

            metadata.setPlatform("reddit");
            metadata.setTitle(postData.optString("title", "Unknown Title"));
            metadata.setChannelName(postData.optString("author", "Unknown User"));
            metadata.setDuration("Unknown Duration");
            metadata.setUploadDate("Unknown Date");
            metadata.setThumbnailUrl(postData.optString("thumbnail", ""));

            // this is to extract v.reddit video url
            JSONObject media = postData.optJSONObject("secure_media");
            if (media != null) {
                JSONObject redditVideo = media.optJSONObject("reddit_video");
                if (redditVideo != null) {
                    redditVideoUrl = redditVideo.optString("fallback_url", null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadVideo(String saveLocation) {
        try {
            if (metadata == null || redditVideoUrl == null) {
                System.out.println("Metadata or video URL not available.");
                return;
            }

            URL url = new URL(redditVideoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            File outputFile = new File(saveLocation, metadata.getTitle() + ".mp4");
            Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            conn.disconnect();

            System.out.println("Reddit video downloaded successfully at: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
