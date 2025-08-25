package org.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    private MetadataRepository metadataRepository;
    private LocalFileStorage localFileStorage;
    private FileService fileService;

    @BeforeEach
    void setUp() {
        metadataRepository = mock(MetadataRepository.class);
        localFileStorage = mock(LocalFileStorage.class);
        fileService = new FileService(metadataRepository, localFileStorage);
    }

    @Test
    void testUploadSuccess() throws IOException, NoSuchAlgorithmException {
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "Hello".getBytes());
        FileUploadMetadata metadata = new FileUploadMetadata(userId, Optional.of("file.txt"), Visibility.PUBLIC, List.of("tag1"));

        when(metadataRepository.findByFileNameAndUserIdAndDeletedIsFalse("file.txt", userId)).thenReturn(Optional.empty());
        when(metadataRepository.findByContentHashAndUserIdAndDeletedIsFalse(anyString(), eq(userId))).thenReturn(Optional.empty());
        when(localFileStorage.upload(file, userId + "/file.txt")).thenReturn("fakehash");

        FileUploadResponse response = fileService.upload(file, metadata);

        System.out.println(response);

        assertEquals("SUCCESS", response.getMessage());

        verify(localFileStorage, times(1)).createUserDirectory(userId);
        verify(metadataRepository, times(1)).save(any(MetadataEntity.class));
    }

    @Test
    void testUploadDuplicateName() throws IOException, NoSuchAlgorithmException {
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "Hello".getBytes());
        FileUploadMetadata metadata = new FileUploadMetadata(userId, Optional.of("file.txt"), Visibility.PUBLIC, List.of());

        MetadataEntity existing = new MetadataEntity();
        when(metadataRepository.findByFileNameAndUserIdAndDeletedIsFalse("file.txt", userId)).thenReturn(Optional.of(existing));

        FileUploadResponse response = fileService.upload(file, metadata);

        assertEquals("DUPLICATE_NAME", response.getMessage());
    }

    @Test
    void testUploadDuplicateContent() throws IOException, NoSuchAlgorithmException {
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "newFile.txt", "text/plain", "Hello World".getBytes());
        FileUploadMetadata metadata = new FileUploadMetadata(userId, Optional.of("newFile.txt"), Visibility.PUBLIC, List.of("tag1"));

        when(metadataRepository.findByFileNameAndUserIdAndDeletedIsFalse("newFile.txt", userId))
                .thenReturn(Optional.empty());

        MetadataEntity existingFile = new MetadataEntity();
        existingFile.setId(UUID.randomUUID());

        when(localFileStorage.upload(any(), any())).thenReturn("duplicateHash");
        when(metadataRepository.findByContentHashAndUserIdAndDeletedIsFalse(any(), any()))
                .thenReturn(Optional.of(existingFile));

        FileUploadResponse response = fileService.upload(file, metadata);

        assertEquals("DUPLICATE", response.getMessage());
        assertEquals(existingFile.getId(), response.getFileId());

        verify(localFileStorage, times(1)).createUserDirectory(userId);
        verify(localFileStorage, times(1)).upload(any(), any());

        verify(metadataRepository, never()).save(any(MetadataEntity.class));
    }

    @Test
    void testUpdateSuccess() throws FileNotFoundException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MetadataEntity metadata = new MetadataEntity();
        metadata.setDeleted(false);
        metadata.setUserId(userId);
        when(metadataRepository.findByIdAndDeletedIsFalse(fileId)).thenReturn(Optional.of(metadata));

        FileUpdateRequest request = new FileUpdateRequest("newFileName.txt", userId);

        fileService.update(fileId, request, Action.UPDATE);

        assertEquals("newFileName.txt", metadata.getFileName());
        verify(metadataRepository, times(1)).save(metadata);
    }

    @Test
    void testGetFiles() {
        UUID userId = UUID.randomUUID();
        MetadataEntity entity = new MetadataEntity();
        entity.setId(UUID.randomUUID());
        entity.setFileName("file1");
        entity.setFileSize(100L);
        entity.setUserId(userId);
        entity.setTags(List.of(new TagEntity("tag1", entity)));
        Page<MetadataEntity> page = new PageImpl<>(List.of(entity));

        when(metadataRepository.findByUserIdAndDeletedIsFalseOrVisibilityAndDeletedIsFalse(eq(userId), eq(Visibility.PUBLIC), any(Pageable.class)))
                .thenReturn(page);

        Page<FileData> result = fileService.getFiles(userId, Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals("file1", result.getContent().getFirst().getFileName());
        assertEquals(List.of("tag1"), result.getContent().getFirst().getTags());
    }

    @Test
    void testUpdateUnauthorized() {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MetadataEntity metadata = new MetadataEntity();
        metadata.setDeleted(false);
        metadata.setUserId(UUID.randomUUID());
        when(metadataRepository.findByIdAndDeletedIsFalse(fileId)).thenReturn(Optional.of(metadata));

        FileUpdateRequest request = new FileUpdateRequest("newFileName.txt", userId);

        assertThrows(IllegalArgumentException.class, () -> fileService.update(fileId, request, Action.UPDATE));
    }

    @Test
    void testDownloadSuccess() throws FileNotFoundException, MalformedURLException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MetadataEntity metadata = new MetadataEntity();
        metadata.setFileName("file.txt");
        metadata.setUserId(userId);
        metadata.setVisibility(Visibility.PRIVATE);

        when(metadataRepository.findByIdAndDeletedIsFalse(fileId)).thenReturn(Optional.of(metadata));
        Resource resource = new ByteArrayResource("data".getBytes());
        when(localFileStorage.download(metadata)).thenReturn(resource);

        FileDownloadData data = fileService.download(fileId, userId);

        assertEquals("file.txt", data.getFileName());
        assertEquals(resource, data.getResource());
    }

    @Test
    void testDownloadUnauthorized() {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        MetadataEntity metadata = new MetadataEntity();
        metadata.setUserId(UUID.randomUUID()); // different user
        metadata.setVisibility(Visibility.PRIVATE);

        when(metadataRepository.findByIdAndDeletedIsFalse(fileId)).thenReturn(Optional.of(metadata));

        assertThrows(IllegalArgumentException.class, () -> fileService.download(fileId, userId));
    }

    @Test
    void testDownloadNotFound() {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(metadataRepository.findByIdAndDeletedIsFalse(fileId)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> fileService.download(fileId, userId));
    }

}
