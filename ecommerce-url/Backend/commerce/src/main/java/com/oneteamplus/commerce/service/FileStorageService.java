package com.oneteamplus.commerce.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public String downloadAndStore(String sourceUrl) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            throw new IllegalArgumentException("Image URL is empty");
        }

        String extension = extractExtension(sourceUrl);
        String filename = UUID.randomUUID() + extension;

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(uploadPath);
            Path target = uploadPath.resolve(filename);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sourceUrl))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                log.error("Failed to download image, status={} url={}", response.statusCode(), sourceUrl);
                throw new RuntimeException("Failed to download image: HTTP " + response.statusCode());
            }

            try (InputStream in = response.body()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String localUrl = baseUrl + "/" + filename;
            log.info("Downloaded image from {} and stored as {}", sourceUrl, localUrl);
            return localUrl;

        } catch (IOException | InterruptedException e) {
            log.error("Failed to download/store image from url={}", sourceUrl, e);
            throw new RuntimeException("Failed to download image from: " + sourceUrl, e);
        }
    }

    private String extractExtension(String url) {
        try {
            String path = new URL(url).getPath();
            if (path.contains(".")) {
                String ext = path.substring(path.lastIndexOf('.'));
                return ext.length() <= 5 ? ext : ".jpg";
            }
        } catch (Exception ignored) {
        }
        return ".jpg";
    }
}