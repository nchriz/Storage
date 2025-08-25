package org.storage.service;

import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.storage.controller.request.FileUpdateRequest;
import org.storage.controller.resposne.FileUploadResponse;
import org.storage.model.*;
import org.storage.repository.MetadataRepository;
import org.storage.repository.entity.MetadataEntity;
import org.storage.repository.entity.TagEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class FileService {

    private final MetadataRepository metadataRepository;
    private final LocalFileStorage localFileStorage;


    public FileService(
            MetadataRepository metadataRepository,
            LocalFileStorage localFileStorage
    ) {
        this.metadataRepository = metadataRepository;
        this.localFileStorage = localFileStorage;
    }

    @Transactional
    public FileUploadResponse upload(
            MultipartFile file,
            FileUploadMetadata fileUploadMetadata
    ) throws IOException, NoSuchAlgorithmException {
        UUID userId = fileUploadMetadata.getUserId();

        String fileName = fileUploadMetadata.getFileName().orElse(file.getOriginalFilename());
        String fileLocation = userId.toString() + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        Optional<MetadataEntity> existingFileByName = metadataRepository.findByFileNameAndUserIdAndDeletedIsFalse(fileName, userId);
        if (existingFileByName.isPresent()) {
            return new FileUploadResponse(existingFileByName.get().getId(), "DUPLICATE_NAME", userId);
        }

        localFileStorage.createUserDirectory(userId);
        String contentHash = localFileStorage.upload(file, fileLocation);

        Optional<MetadataEntity> existingFile = metadataRepository.findByContentHashAndUserIdAndDeletedIsFalse(contentHash, userId);
        if (existingFile.isPresent()) {
            return new FileUploadResponse(existingFile.get().getId(), "DUPLICATE", userId);
        }

        String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");

        MetadataEntity metadataEntityToUpload = new MetadataEntity(fileUploadMetadata, fileName, file.getSize(), fileLocation, contentHash, contentType);
        for (String tag : fileUploadMetadata.getTags()) {
            TagEntity tagEntity = new TagEntity(tag, metadataEntityToUpload);
            metadataEntityToUpload.getTags().add(tagEntity);
        }
        metadataRepository.save(metadataEntityToUpload);
        return new FileUploadResponse(metadataEntityToUpload.getId(), "SUCCESS", userId);
    }

    public void update(
            UUID fileId,
            FileUpdateRequest fileUpdateRequest,
            Action action
    ) throws FileNotFoundException {
        MetadataEntity metadata = metadataRepository.findByIdAndDeletedIsFalse(fileId)
                .orElseThrow(() -> new FileNotFoundException("File with ID " + fileId + " not found"));
        if (!metadata.getUserId().equals(fileUpdateRequest.getUserId())) {
            throw new IllegalArgumentException("User is not authorized to update file");
        }

        switch (action) {
            case DELETE:
                delete(metadata);
                break;
            case UPDATE:
                update(metadata, fileUpdateRequest);
                break;
        }
        metadataRepository.save(metadata);
    }

    private void update(MetadataEntity metadata, FileUpdateRequest fileUpdateRequest) {
        metadata.setFileName(fileUpdateRequest.getFileName());
    }

    private void delete(MetadataEntity metadata) {
        metadata.setDeleted(true);
    }

    public Page<FileData> getFiles(UUID userId, Pageable pageable) {

        Page<MetadataEntity> entityPage = metadataRepository.findByUserIdAndDeletedIsFalseOrVisibilityAndDeletedIsFalse(userId, Visibility.PUBLIC, pageable);

        List<FileData> fileDataList = entityPage.getContent().stream()
                .map(this::convertToFileMetadataDto)
                .collect(Collectors.toList());

        return new PageImpl<>(fileDataList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    private FileData convertToFileMetadataDto(MetadataEntity entity) {
        List<String> tags = entity.getTags().stream().map(TagEntity::getTag).toList();
        FileData fileData = new FileData();
        fileData.setFileId(entity.getId());
        fileData.setFileName(entity.getFileName());
        fileData.setFileSize(entity.getFileSize());
        fileData.setFilePath(entity.getFilePath());
        fileData.setTags(tags);
        fileData.setUpdatedAt(entity.getUploadedAt());
        return fileData;
    }

    public FileDownloadData download(UUID fileId, UUID userId) throws FileNotFoundException, IllegalArgumentException {
        MetadataEntity metadata = metadataRepository.findByIdAndDeletedIsFalse(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found with ID: " + fileId));
        if (!metadata.getUserId().equals(userId) && metadata.getVisibility() != Visibility.PUBLIC) {
            throw new IllegalArgumentException("User is not authorized to download this file.");
        }
        try {
            Resource resource = localFileStorage.download(metadata);

            if (resource.exists()) {
                return new FileDownloadData(metadata.getFileName(), resource);
            } else {
                throw new FileNotFoundException("File not found on the server");
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("File path is invalid: " + metadata.getFilePath());
        }
    }
}
