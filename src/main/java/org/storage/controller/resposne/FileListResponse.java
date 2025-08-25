package org.storage.controller.resposne;

import org.storage.model.FileData;

import java.util.List;

public class FileListResponse {

    private List<FileData> files;

    public FileListResponse(List<FileData> files) {
        this.files = files;
    }
}
