package org.storage.model;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUploadMetadata {

    private UUID userId;
    private Optional<String> fileName;
    private Visibility visibility;
    private List<String> tags;

    public FileUploadMetadata(UUID userId, Optional<String> fileName, Visibility visibility, List<String> tags) {
        this.userId = userId;
        this.fileName = fileName;
        this.visibility = visibility;
        this.tags = tags;
    }

    public FileUploadMetadata(String userId, Optional<String> fileName, Visibility visibility, List<String> tags) {
        this.userId = UUID.fromString(userId);
        this.fileName = fileName;
        this.visibility = visibility;
        this.tags = tags;
    }

    public UUID getUserId() {
        return userId;
    }

    public List<String> getTags() {
        return tags;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Optional<String> getFileName() {
        return fileName;
    }
}
