package org.storage.model;

import org.springframework.core.io.Resource;

public class FileDownloadData {

    private String fileName;
    private Resource resource;

    public FileDownloadData(String fileName, Resource resource) {
        this.fileName = fileName;
        this.resource = resource;
    }

    public String getFileName() {
        return fileName;
    }

    public Resource getResource() {
        return resource;
    }
}
