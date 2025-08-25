package org.storage.repository.entity;

import jakarta.persistence.*;
import org.storage.model.FileUploadMetadata;
import org.storage.model.Visibility;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "metadata")
public class MetadataEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "metadata", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("tag ASC")
    private List<TagEntity> tags = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "content_hash", length = 64)
    private String contentHash;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    public MetadataEntity() {
    }

    public MetadataEntity(FileUploadMetadata metadata, String fileName, Long fileSize, String filePath, String contentHash, String contentType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.visibility = metadata.getVisibility();
        this.userId = metadata.getUserId();
        this.contentHash = contentHash;
        this.contentType = contentType;
    }

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
