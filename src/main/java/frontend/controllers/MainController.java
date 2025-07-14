package frontend.controllers;

import backend.downloader.Facebook;
import backend.downloader.Instagram;
import backend.downloader.Reddit;
import backend.downloader.YouTube;
import backend.model.Metadata;
import backend.repository.DownloadHistory;
import backend.repository.DownloadRecord;
import backend.service.UrlChecker;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    private String lastPlatform;
    private YouTube currentYouTube;
    private Reddit currentReddit;
    private Instagram currentInsta;
    private Facebook currentFB;

    //for back from metadata
    @FXML private Button backFromPreviewButton;

    //for progressbar
    @FXML private ProgressBar downloadProgressBar;

    //For History
    @FXML private VBox historyView;
    @FXML private TableView<DownloadRecord> historyTable;
    @FXML private TableColumn<DownloadRecord, String> colPlatform, colTitle, colUrl, colQuality, colPath, colDate;

    //for metadata display
    @FXML private VBox previewBox;
    @FXML private ImageView previewThumbnail;
    @FXML private Label titleLabel, channelLabel, durationLabel, uploadDateLabel;
    @FXML private ComboBox<String> qualityComboBox;
    @FXML private HBox qualityBox;
    @FXML private Button finalDownloadButton;


    @FXML private Rectangle facebookBackground;
    @FXML private ImageView facebookLogo;

    @FXML private Rectangle redditBackground;
    @FXML private ImageView redditLogo;

    @FXML private ImageView instagramBackground;
    @FXML private ImageView instagramLogo;

    @FXML
    private Rectangle youtubeBackground;

    @FXML
    private ImageView youtubePlayIcon;

    @FXML
    private ImageView platformBackground;

    @FXML
    private Button historyButton;
    @FXML
    private Button deleteHistoryButton;

    @FXML
    private TextField urlField;

    @FXML private Node downloadButton;

    @FXML
    private void onHistoryClicked() {
        System.out.println("History button clicked.");
        // Hide current UI
        downloadButton.setVisible(false);
        urlField.setVisible(false);
        previewBox.setVisible(false);
        historyButton.setVisible(false);
        deleteHistoryButton.setVisible(true);
        resetPlatformVisuals();

        // Load data from DB
        ObservableList<DownloadRecord> data = DownloadHistory.getHistory();
        historyTable.setItems(data);

        // Map columns
        colPlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colUrl.setCellValueFactory(new PropertyValueFactory<>("url"));
        colQuality.setCellValueFactory(new PropertyValueFactory<>("quality"));
        colPath.setCellValueFactory(new PropertyValueFactory<>("filepath"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Show history UI
        historyView.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), historyView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    @FXML
    private void onBackFromHistory() {
        // Hide history
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), historyView);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            historyView.setVisible(false);
            deleteHistoryButton.setVisible(false);
            historyButton.setVisible(true);
            downloadButton.setVisible(true);
            urlField.setVisible(true);
            showInputUI(); // bring back input bar
        });
        fadeOut.play();
    }

    @FXML
    private void onDeleteHistoryClicked() {
        DownloadHistory.deleteAllHistory();
        historyTable.getItems().clear(); // clear UI table too
    }


    @FXML
    private void onDownloadClicked(ActionEvent event) {
        String url = urlField.getText();
        historyButton.setVisible(false);
        resetPlatformVisuals();

        if (url == null || url.isBlank()) {
            System.out.println("No URL entered.");
            return;
        }

        handleURLInput(url);
    }

    private void handleURLInput(String Url) {
        UrlChecker UrlValidator = new UrlChecker(Url);
        String platform = UrlValidator.platformName();

        switch (platform) {
            case "youtube":
                System.out.println("Detected: YouTube");
                animateYouTubeSplash();
                break;
            case "reddit":
                System.out.println("Detected: Reddit");
                animateRedditSplash();
                break;

            case "instagram":
                System.out.println("Detected: Instagram");
                animateInstagramSplash();
                break;
            case "facebook":
                System.out.println("Detected: Facebook");
                animateFacebookSplash();
                break;
            default:
                System.out.println("Unsupported or unknown platform");
                resetPlatformVisuals();
        }

        // Run the backend task
        PlatformTask task = new PlatformTask(platform, Url);
        task.setOnSucceeded(e -> {
            if (platform.equals("youtube")) {
                YouTube yt = task.getYouTube();
                Metadata meta = yt.getMetadata();
                if (meta != null) {
                    displayMetadata(meta, true);
                    backFromPreviewButton.setVisible(true);
                }
                currentYouTube = new YouTube(Url);
                lastPlatform = platform;
            } else if(platform.equals("instagram")) {
                Instagram insta = task.getInsta();
                Metadata meta = insta.getMetadata();
                if (meta != null) {
                    displayMetadata(meta, false);
                    backFromPreviewButton.setVisible(true);
                }
                currentInsta = new Instagram(Url);
                lastPlatform = platform;
            } else if(platform.equals("reddit")) {
                Reddit rt = task.getReddit();
                Metadata meta = rt.getMetadata();
                if (meta != null) {
                    displayMetadata(meta, false);
                    backFromPreviewButton.setVisible(true);
                }
                currentReddit = new Reddit(Url);
                lastPlatform = platform;
            } else if(platform.equals("facebook")) {
                Facebook fb = task.getFb();
                Metadata meta = fb.getMetadata();
                if (meta != null) {
                    displayMetadata(meta, false);
                    backFromPreviewButton.setVisible(true);
                }
                currentFB = new Facebook(Url);
                lastPlatform = platform;
            }
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    // for youtube splash logo fade
    private void animateYouTubeSplash() {

        // Background fade-in
        FadeTransition bgFade = new FadeTransition(Duration.millis(600), youtubeBackground);
        bgFade.setFromValue(0.0);
        bgFade.setToValue(0.2); // light red glow
        bgFade.setCycleCount(1);
        bgFade.play();

        //hide bar and download button
        hideInputUI();

        // Play icon fade-in
        youtubePlayIcon.setImage(new Image("/frontend/images/youtube_play.png"));

        FadeTransition iconFade = new FadeTransition(Duration.millis(800), youtubePlayIcon);
        iconFade.setFromValue(0.0);
        iconFade.setToValue(0.8);
        iconFade.setDelay(Duration.millis(300));
        iconFade.setCycleCount(1);
        iconFade.play();

        // Optional: soft floating motion (wave feel)
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(3), youtubePlayIcon);
        floatAnim.setFromY(10);
        floatAnim.setToY(-10);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(TranslateTransition.INDEFINITE);
        floatAnim.play();
    }

    private void resetPlatformVisuals() {
        // === YouTube cleanup ===
        FadeTransition ytBgOut = new FadeTransition(Duration.millis(300), youtubeBackground);
        ytBgOut.setToValue(0.0);
        ytBgOut.play();

        FadeTransition ytIconOut = new FadeTransition(Duration.millis(300), youtubePlayIcon);
        ytIconOut.setToValue(0.0);
        ytIconOut.play();
        youtubePlayIcon.setTranslateY(0);

        // === Instagram cleanup ===
        FadeTransition fadeOutBG = new FadeTransition(Duration.millis(300), instagramBackground);
        fadeOutBG.setToValue(0.0);
        fadeOutBG.play();

        FadeTransition fadeOutLogo = new FadeTransition(Duration.millis(300), instagramLogo);
        fadeOutLogo.setToValue(0.0);
        fadeOutLogo.play();

        // === Reddit cleanup ===
        FadeTransition redditBGOut = new FadeTransition(Duration.millis(300), redditBackground);
        redditBGOut.setToValue(0.0);
        redditBGOut.play();

        FadeTransition redditLogoOut = new FadeTransition(Duration.millis(300), redditLogo);
        redditLogoOut.setToValue(0.0);
        redditLogoOut.play();

        redditLogo.setTranslateY(0);

        FadeTransition fbBGOut = new FadeTransition(Duration.millis(300), facebookBackground);
        fbBGOut.setToValue(0.0);
        fbBGOut.play();

        FadeTransition fbLogoOut = new FadeTransition(Duration.millis(300), facebookLogo);
        fbLogoOut.setToValue(0.0);
        fbLogoOut.play();

        facebookLogo.setTranslateY(0);

    }


    //instagram fade out animation
    private void animateInstagramSplash() {
        //hide bar and download button
        hideInputUI();

        System.out.println("Instagram splash triggered.");

        instagramBackground.setImage(new Image("/frontend/images/instagram_background.png"));
        instagramLogo.setImage(new Image("/frontend/images/instagram_logo.png"));

        // === Fade in background ===
        FadeTransition fadeBG = new FadeTransition(Duration.millis(600), instagramBackground);
        fadeBG.setFromValue(0.0);
        fadeBG.setToValue(0.3);
        fadeBG.play();

        // === Fade in logo ===
        FadeTransition fadeLogo = new FadeTransition(Duration.millis(800), instagramLogo);
        fadeLogo.setFromValue(0.0);
        fadeLogo.setToValue(0.9);
        fadeLogo.setDelay(Duration.millis(200));
        fadeLogo.play();

        // === Bobbing effect (floating animation) ===
        TranslateTransition bob = new TranslateTransition(Duration.seconds(3), instagramLogo);
        bob.setFromY(8);
        bob.setToY(-8);
        bob.setCycleCount(TranslateTransition.INDEFINITE);
        bob.setAutoReverse(true);
        bob.play();
    }

    private void animateRedditSplash() {
        //hide bar and download button
        hideInputUI();

        System.out.println("Reddit splash triggered.");

        redditLogo.setImage(new Image("/frontend/images/reddit_logo.png"));

        // === Fade in background ===
        FadeTransition fadeBG = new FadeTransition(Duration.millis(600), redditBackground);
        fadeBG.setFromValue(0.0);
        fadeBG.setToValue(0.25);
        fadeBG.play();

        // === Fade in logo ===
        FadeTransition fadeLogo = new FadeTransition(Duration.millis(800), redditLogo);
        fadeLogo.setFromValue(0.0);
        fadeLogo.setToValue(0.9);
        fadeLogo.setDelay(Duration.millis(200));
        fadeLogo.play();

        // === Bobbing effect ===
        TranslateTransition bob = new TranslateTransition(Duration.seconds(3), redditLogo);
        bob.setFromY(8);
        bob.setToY(-8);
        bob.setCycleCount(TranslateTransition.INDEFINITE);
        bob.setAutoReverse(true);
        bob.play();
    }

    private void animateFacebookSplash() {
        //hide bar and download button
        hideInputUI();

        System.out.println("Facebook splash triggered.");

        facebookLogo.setImage(new Image("/frontend/images/facebook_logo.png"));

        // === Fade in background ===
        FadeTransition fadeBG = new FadeTransition(Duration.millis(600), facebookBackground);
        fadeBG.setFromValue(0.0);
        fadeBG.setToValue(0.25); // soft background
        fadeBG.play();

        // === Fade in logo ===
        FadeTransition fadeLogo = new FadeTransition(Duration.millis(800), facebookLogo);
        fadeLogo.setFromValue(0.0);
        fadeLogo.setToValue(0.9);
        fadeLogo.setDelay(Duration.millis(200));
        fadeLogo.play();

        // === Bobbing motion ===
        TranslateTransition bob = new TranslateTransition(Duration.seconds(3), facebookLogo);
        bob.setFromY(8);
        bob.setToY(-8);
        bob.setCycleCount(TranslateTransition.INDEFINITE);
        bob.setAutoReverse(true);
        bob.play();
    }

    private void hideInputUI() {
        // Fade out URL field
        FadeTransition fadeUrl = new FadeTransition(Duration.millis(300), urlField);
        fadeUrl.setToValue(0.0);
        fadeUrl.play();

        // Fade out download button
        FadeTransition fadeBtn = new FadeTransition(Duration.millis(300), downloadButton);
        fadeBtn.setToValue(0.0);
        fadeBtn.play();

        // Optional: disable them so they don’t accept input
        urlField.setDisable(true);
        downloadButton.setDisable(true);
    }

    private void showInputUI() {
        urlField.setDisable(false);
        downloadButton.setDisable(false);

        FadeTransition fadeUrl = new FadeTransition(Duration.millis(300), urlField);
        fadeUrl.setToValue(1.0);
        fadeUrl.play();

        FadeTransition fadeBtn = new FadeTransition(Duration.millis(300), downloadButton);
        fadeBtn.setToValue(1.0);
        fadeBtn.play();
    }

    private void displayMetadata(Metadata meta, boolean showQuality) {
        // Fill labels
        titleLabel.setText("Title: " + meta.getTitle());
        channelLabel.setText("Channel: " + meta.getChannelName());
        durationLabel.setText("Duration: " + meta.getDuration());
        uploadDateLabel.setText("Uploaded: " + meta.getUploadDate());

        // Load thumbnail
        previewThumbnail.setImage(new Image(meta.getThumbnailUrl(), true));

        // Handle quality dropdown
        if (showQuality && meta.getQualityList() != null) {
            qualityBox.setVisible(true);
            qualityComboBox.getItems().setAll(meta.getQualityList());
            qualityComboBox.getSelectionModel().selectFirst();
        } else {
            qualityBox.setVisible(false);
        }

        // Animate fade-in
        previewBox.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), previewBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void onFinalDownloadClicked() {
        // Step 1: Ask for save folder
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Save Location");
        File selectedDir = directoryChooser.showDialog(previewBox.getScene().getWindow());

        if (selectedDir == null) return; // user cancelled

        // Step 2: Get selected quality
        String selectedQuality = qualityComboBox.getValue();
        if (selectedQuality == null) selectedQuality = "360p"; // fallback default

        // 2. Hide preview
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), previewBox);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> previewBox.setVisible(false));
        fadeOut.play();

        if (!"reddit".equals(lastPlatform)){
            downloadProgressBar.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), downloadProgressBar);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            downloadProgressBar.setProgress(0);
        }

        // Step 3: Build options map
        Map<String, String> options = new HashMap<>();
        options.put("quality", selectedQuality);

        // Step 4: Clean title for use inside download method (done inside download)
        String finalSelectedQuality = selectedQuality;
        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    if ("youtube".equals(lastPlatform) && currentYouTube != null) {
                        System.out.println("Download Started");
                        currentYouTube.downloadVideo(options, selectedDir.getAbsolutePath(), progress -> {
                            updateProgress(progress, 1.0);
                        });
                        System.out.println("Download Complete");
                    } else if ("instagram".equals(lastPlatform) && currentInsta != null) {
                        System.out.println("Download Started");
                        currentInsta.downloadVideo(selectedDir.getAbsolutePath(), progress -> {
                            updateProgress(progress, 1.0);
                        });
                        System.out.println("Download Complete");
                    } else if ("reddit".equals(lastPlatform) && currentReddit != null) {
                        System.out.println("Download Started");
                        currentReddit.downloadVideo(selectedDir.getAbsolutePath());
                        System.out.println("Download Complete");
                    } else if ("facebook".equals(lastPlatform) && currentFB != null) {
                        System.out.println("Download Started");
                        currentFB.downloadVideo(selectedDir.getAbsolutePath(), progress -> {
                            updateProgress(progress, 1.0);
                        });
                        System.out.println("Download Complete");
                    }
                    // Add logic for other platforms here (e.g. currentInstagram.downloadVideo(...))
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                if ("youtube".equals(lastPlatform) && currentYouTube != null) {
                    Metadata meta = currentYouTube.getMetadata();
                    backend.repository.DownloadHistory.saveHistory(
                            meta,
                            finalSelectedQuality,
                            selectedDir.getAbsolutePath()
                    );
                } else if ("instagram".equals(lastPlatform) && currentInsta != null) {
                    Metadata meta = currentInsta.getMetadata();
                    backend.repository.DownloadHistory.saveHistory(
                            meta,
                            finalSelectedQuality,
                            selectedDir.getAbsolutePath()
                    );
                } else if ("facebook".equals(lastPlatform) && currentFB != null) {
                    Metadata meta = currentFB.getMetadata();
                    backend.repository.DownloadHistory.saveHistory(
                            meta,
                            finalSelectedQuality,
                            selectedDir.getAbsolutePath()
                    );
                } else if ("reddit".equals(lastPlatform) && currentReddit != null) {
                    Metadata meta = currentReddit.getMetadata();
                    backend.repository.DownloadHistory.saveHistory(
                            meta,
                            finalSelectedQuality,
                            selectedDir.getAbsolutePath()
                    );
                }
                if (!"reddit".equals(lastPlatform)) {
                    progressBarFadeOUT();
                }
                backFromPreviewButton.setVisible(false);
                historyButton.setVisible(true);
                resetToInputScreen();
            }
        };

        if (!"reddit".equals(lastPlatform)) {
            downloadProgressBar.progressProperty().bind(downloadTask.progressProperty());
        }

        Thread thread = new Thread(downloadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void resetToInputScreen() {
        // Fade out preview
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), previewBox);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            previewBox.setVisible(false);

            // Reset visuals
            resetPlatformVisuals();     // ✅ hides background colors and logos
            showInputUI();              // ✅ shows URL bar and button
        });
        fadeOut.play();
    }

    private void progressBarFadeIN(){
        downloadProgressBar.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), downloadProgressBar);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        downloadProgressBar.setProgress(0);
    }
    private void progressBarFadeOUT(){
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), downloadProgressBar);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> downloadProgressBar.setVisible(false));
        fadeOut.play();
        downloadProgressBar.setVisible(false);
    }

    @FXML
    private void onBackFromPreview() {
        System.out.print("Back button Clicked");
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), previewBox);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            previewBox.setVisible(false);
            backFromPreviewButton.setVisible(false);
            historyButton.setVisible(true);
            resetPlatformVisuals(); // hide splash
            showInputUI();          // show input bar again
        });
        fadeOut.play();
    }


}

