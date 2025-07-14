package backend.downloader;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend.model.Metadata;
import org.json.JSONArray;
import org.json.JSONObject;

public class YouTube extends Platform{

    private Map<String, String> qualityUrls = new HashMap<>();

    public YouTube(String url) {
        super(url);
        extractMetadata();
    }

    private void extractMetadata() {
        try {
            String[] command = {
                    getYtDlpPath(),
                    "--ffmpeg-location",
                    "src/main/resources/ffmpeg.exe",
                    "-j",
                    getVideoUrl()
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("yt-dlp metadata extraction failed with exit code " + exitCode);
            }

            JSONObject responseJson = new JSONObject(output.toString());
            saveMetadata(responseJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMetadata(JSONObject json) {
        try {
            metadata = new Metadata();

            metadata.setPlatform("youtube");
            metadata.setTitle(json.optString("title", "Unknown Title"));
            metadata.setChannelName(json.optString("uploader", "Unknown Channel"));
            metadata.setDuration(json.optString("duration_string", "Unknown Duration"));
            metadata.setUploadDate(json.optString("upload_date", "Unknown Date"));
            metadata.setThumbnailUrl(json.optString("thumbnail", ""));

            List<String> qualityLabels = new ArrayList<>();
            JSONArray formats = json.getJSONArray("formats");

            for (int i = 0; i < formats.length(); i++) {
                JSONObject format = formats.getJSONObject(i);

                if (format.has("format_note") && format.has("url")) {
                    String qualityLabel = format.getString("format_note").trim();
                    String url = format.getString("url");

                    if (!qualityLabel.equals("tiny")) { // low quality not necessary
                        qualityLabels.add(qualityLabel);
                        qualityUrls.put(qualityLabel, url);
                    }
                }
            }

            metadata.setQualityList(qualityLabels);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadVideo(Map<String, String> specifications, String saveLocation, Consumer<Double> progressCallback) {
        try {
            if (metadata == null) {
                System.out.println("Metadata not loaded yet!");
                return;
            }

            String selectedQuality = specifications.get("quality").replace("p", "");

            String[] command = {
                    getYtDlpPath(),
                    "--ffmpeg-location",
                    "src/main/resources/ffmpeg.exe",
                    "--no-mtime",
                    "-f",
                    "bestvideo[height<=?" + selectedQuality + "]+bestaudio/best[height<=?" + selectedQuality + "]",
                    "-o",
                    saveLocation + java.io.File.separator + "%(title)s.%(ext)s",
                    getVideoUrl()
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                String line;
                Pattern progressPattern = Pattern.compile("\\[download\\]\\s+(\\d+(\\.\\d+)?)%");

                while((line = reader.readLine()) != null){
                    Matcher matcher = progressPattern.matcher(line);
                    if (matcher.find()){
                        double progress = Double.parseDouble(matcher.group(1)) /100.0;
                        progressCallback.accept(progress);
                    }
                }

            }
            process.waitFor();


            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Video downloaded successfully at: " + saveLocation);
            } else {
                System.out.println("Video download failed. Exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }
}
