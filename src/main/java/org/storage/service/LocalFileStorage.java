package org.storage.service;

import jakarta.annotation.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.storage.repository.entity.MetadataEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static org.storage.common.Utils.bytesToHex;

@Service
public class LocalFileStorage {

    private final UserService userService;

    private final Path uploadDir;

    @Autowired
    public LocalFileStorage(
            UserService userService
    ) throws IOException {
        this(userService, Paths.get("uploads"));
    }

    public LocalFileStorage(UserService userService, Path uploadDir) throws IOException {
        this.userService = userService;
        this.uploadDir = uploadDir;
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }


    public String upload(MultipartFile file, String fileLocation) throws IOException, NoSuchAlgorithmException {
        Path destination = uploadDir.resolve(fileLocation);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (InputStream input = file.getInputStream(); DigestInputStream dis = new DigestInputStream(input, md)) {
            Files.copy(dis, destination);
        }

        byte[] hashBytes = md.digest();
        return bytesToHex(hashBytes);
    }

    public Resource download(MetadataEntity metadata) throws MalformedURLException {
        Path filePath = uploadDir.resolve(metadata.getFilePath());
        return new UrlResource(filePath.toUri());
    }

    public void createUserDirectory(UUID userId) throws IOException {
        if (!userService.userExists(userId)) {
            userService.create(userId);
            Path parentDirectory = uploadDir.resolve(userId.toString());
            Files.createDirectories(parentDirectory);
        }
    }
}
