package org.storage.controller.resposne;

import java.util.UUID;

public class FileUploadResponse {

    private UUID fileId;
    private String message;
    private String downloadLink;

    public FileUploadResponse(UUID fileId, String message, UUID userId) {
        this.fileId = fileId;
        this.message = message;
        this.downloadLink= String.format("http://localhost:8080/files/download/%s?userId=%s",
                fileId,
                userId.toString());
    }

    public UUID getFileId() {
        return fileId;
    }

    public String getMessage() {
        return message;
    }

    public String getDownloadLink() {
        return downloadLink;
    }
}
