package org.storage.controller.request;

import java.util.UUID;

public class FileUpdateRequest {

    private String fileName;
    private UUID userId;

    public FileUpdateRequest(String fileName, UUID userId) {
        this.fileName = fileName;
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public UUID getUserId() {
        return userId;
    }
}
