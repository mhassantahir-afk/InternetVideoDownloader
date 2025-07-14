package backend.repository;

import backend.model.Metadata;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class DownloadHistory {
    private Metadata downloadedVideoData = new Metadata();
    private boolean successOrFailure;

    private static String DBLocation = "jdbc:sqlite:data/downloadHistory.db";
    private static String initialSQL = """
                CREATE TABLE IF NOT EXISTS download_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    platform TEXT,
                    title TEXT,
                    url TEXT,
                    quality TEXT,
                    filepath TEXT,
                    date_downloaded TEXT
                );
            """;

    public static void initializeDatabase() {
        try(Connection conn = DriverManager.getConnection(DBLocation)) {
            Statement sml = conn.createStatement();
            sml.execute(initialSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveHistory(Metadata metadata, String Quality, String filePath) {

        String insertSQL = "INSERT INTO download_history (platform, title, url, quality, filepath, date_downloaded) VALUES (?, ?, ?, ?, ?, datetime('now'))";

        try(Connection conn = DriverManager.getConnection(DBLocation)){
            PreparedStatement sml = conn.prepareStatement(insertSQL);

            sml.setString(1, metadata.getPlatform());
            sml.setString(2, metadata.getTitle());
            sml.setString(3, metadata.getSourceUrl());
            sml.setString(4, Quality);
            sml.setString(5, filePath);

            sml.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static ObservableList<DownloadRecord> getHistory() {
        ObservableList<DownloadRecord> records = FXCollections.observableArrayList();

        String query = "SELECT platform, title, url, quality, filepath, date_downloaded FROM download_history";

        try (Connection conn = DriverManager.getConnection(DBLocation);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DownloadRecord record = new DownloadRecord(
                        rs.getString("platform"),
                        rs.getString("title"),
                        rs.getString("url"),
                        rs.getString("quality"),
                        rs.getString("filepath"),
                        rs.getString("date_downloaded")
                );
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public static void deleteAllHistory() {
        String deleteSQL = "DELETE FROM download_history";

        try (Connection conn = DriverManager.getConnection(DBLocation);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void setSuccessOrFailure(boolean successOrFailure) {
        this.successOrFailure = successOrFailure;
    }

    public void setDownloadedVideoData(Metadata downloadedVideoData) {
        this.downloadedVideoData = downloadedVideoData;
    }
}
