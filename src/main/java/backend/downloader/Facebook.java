package backend.downloader;

import backend.model.Metadata;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Facebook extends Platform {
    public Facebook(String url) {
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
            metadata.setPlatform("facebook");
            metadata.setTitle(json.optString("title", "Unknown Title"));
            metadata.setChannelName(json.optString("uploader", "Unknown Uploader"));
            metadata.setDuration(json.optString("duration_string", "Unknown Duration"));
            metadata.setUploadDate(json.optString("upload_date", "Unknown Date"));
            metadata.setThumbnailUrl(json.optString("thumbnail", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadVideo(String saveLocation, Consumer<Double> progressCallback) {
        try {
            if (metadata == null) {
                System.out.println("Metadata not loaded yet!");
                return;
            }

            String[] command = {
                    getYtDlpPath(),
                    "--ffmpeg-location",
                    "src/main/resources/ffmpeg.exe",
                    "--no-mtime",
                    "-f",
                    "bestvideo+bestaudio/best",
                    "-o",
                    saveLocation + java.io.File.separator + "%(title)s.%(ext)s",
                    getVideoUrl()
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                Pattern progressPattern = Pattern.compile("\\[download\\]\\s+(\\d+(\\.\\d+)?)%");

                while ((line = reader.readLine()) != null) {
                    Matcher matcher = progressPattern.matcher(line);
                    if (matcher.find()) {
                        double progress = Double.parseDouble(matcher.group(1)) / 100.0;
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

    @Override
    public Metadata getMetadata() {
        return metadata;
    }
}
