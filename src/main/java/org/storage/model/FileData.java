package org.storage.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FileData {

    private UUID fileId;

    private String fileName;

    private Long fileSize;

    private String filePath;

    private List<String> tags;

    private Instant updatedAt;

    public FileData() {

    }

    public UUID getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<String> getTags() {
        return tags;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
