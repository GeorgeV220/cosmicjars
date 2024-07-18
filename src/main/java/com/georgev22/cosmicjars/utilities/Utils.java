package com.georgev22.cosmicjars.utilities;

import com.georgev22.cosmicjars.CosmicJars;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

    /**
     * Generates a progress bar string based on the given progress percentage.
     *
     * @param progress Progress percentage.
     * @return Progress bar string.
     */
    public static @NotNull String getProgressBar(int progress) {
        StringBuilder progressBar = new StringBuilder("[");
        int numChars = progress / 2;
        for (int i = 0; i < 50; i++) {
            if (i < numChars) {
                progressBar.append("=");
            } else if (i == numChars) {
                progressBar.append(">");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ");
        progressBar.append(progress);
        progressBar.append("%");
        return progressBar.toString();
    }

    /**
     * Downloads a file from the specified API URL and saves it to the given file path with the specified file name.
     * Provides real-time progress updates during the download process.
     *
     * @param apiUrl   The URL of the file to download.
     * @param filePath The directory where the downloaded file will be saved.
     * @param fileName The name of the downloaded file.
     * @return The absolute path of the downloaded file.
     * @throws IOException If an I/O error occurs during the download process.
     */
    public static @NotNull String downloadFile(String apiUrl, String filePath, String fileName) throws IOException {
        File outputFile = new File(filePath + fileName);
        if (outputFile.getParentFile().mkdirs()) {
            CosmicJars.getInstance().getLogger().info("Created directory: {}", filePath);
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        int contentLength = connection.getContentLength();

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            long startTime = System.nanoTime();
            int lastLoggedProgress = -1; // To keep track of the last logged progress

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                int progress = (int) (totalBytesRead * 100.0 / contentLength);

                if (progress >= lastLoggedProgress + 10 || progress == 100) {
                    lastLoggedProgress = progress;
                    long currentTime = System.nanoTime();
                    long elapsedTime = currentTime - startTime;
                    double speed = totalBytesRead / (elapsedTime / 1e9);
                    double remainingBytes = contentLength - totalBytesRead;
                    double timeLeft = remainingBytes / speed;

                    String progressBar = Utils.getProgressBar(progress);

                    CosmicJars.getInstance().getLogger().info("\rDownloading... {}  Speed: {} KB/s  Time left: {} seconds",
                            progressBar, String.format("%.2f", speed / 1024), String.format("%.2f", timeLeft));
                }
            }
        }

        CosmicJars.getInstance().getLogger().info("File downloaded successfully: {}", outputFile.getAbsolutePath());
        return outputFile.getAbsolutePath();
    }

    /**
     * Copies the contents of the given InputStream to the specified file.
     *
     * @param inputStream The InputStream to read from.
     * @param file        The file to write to.
     * @return true if the operation is successful, false otherwise.
     */
    public static boolean copyInputStreamToFile(@NotNull InputStream inputStream, File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (IOException e) {
            CosmicJars.getInstance().getLogger().log(Level.ERROR, () -> String.format("Failed to copy input stream to file: %s Error: %s", file.getAbsolutePath(), e.getMessage()), e);
            return false;
        }
    }

    /**
     * Checks if the current operating system is Windows.
     *
     * @return True if the current operating system is Windows, false otherwise.
     */
    public static boolean isWindows() {
        return (System.getProperty("os") != null ? System.getProperty("os") : "").toLowerCase().contains("windows");
    }

}
