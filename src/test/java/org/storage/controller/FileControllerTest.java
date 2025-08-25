package org.storage.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.storage.controller.request.FileUpdateRequest;
import org.storage.controller.resposne.FileUploadResponse;
import org.storage.model.Action;
import org.storage.model.FileData;
import org.storage.service.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileControllerTest {

    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void testUploadSuccess() throws IOException, NoSuchAlgorithmException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        String userId = UUID.randomUUID().toString();
        String visibility = "PUBLIC";
        String tags = "tag1,tag2";

        FileUploadResponse response = new FileUploadResponse(UUID.randomUUID(), "SUCCESS", UUID.fromString(userId));
        when(fileService.upload(any(), any())).thenReturn(response);

        ResponseEntity<FileUploadResponse> result =
                fileController.upload(file, userId, visibility, tags, Optional.of("test.txt"));

        assertEquals(200, result.getStatusCode().value());
        assertEquals("SUCCESS", result.getBody().getMessage());
        verify(fileService, times(1)).upload(any(), any());
    }

    @Test
    void testUploadTooManyTags() throws IOException, NoSuchAlgorithmException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        String userId = UUID.randomUUID().toString();
        String visibility = "PUBLIC";
        String tags = "t1,t2,t3,t4,t5,t6";

        ResponseEntity<FileUploadResponse> result =
                fileController.upload(file, userId, visibility, tags, Optional.of("test.txt"));

        assertEquals(400, result.getStatusCode().value());
        verify(fileService, never()).upload(any(), any());
    }

    @Test
    void testGetFiles() {
        UUID userId = UUID.randomUUID();

        Page<FileData> page = new PageImpl<>(List.of(new FileData()));
        when(fileService.getFiles(eq(userId), any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<FileData>> result = fileController.getFiles(userId, Pageable.unpaged());

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().getContent().size());
        verify(fileService, times(1)).getFiles(eq(userId), any(Pageable.class));
    }

    @Test
    void testDeleteFile() throws FileNotFoundException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FileUpdateRequest req = new FileUpdateRequest("file.txt", userId);

        doNothing().when(fileService).update(fileId, req, Action.DELETE);

        ResponseEntity<String> result = fileController.deleteFile(fileId, req);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("File successfully deleted", result.getBody());
        verify(fileService, times(1)).update(fileId, req, Action.DELETE);
    }

    @Test
    void testDeleteFileNotFound() throws FileNotFoundException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FileUpdateRequest req = new FileUpdateRequest("file.txt", userId);

        doThrow(new FileNotFoundException("File not found")).when(fileService).update(fileId, req, Action.DELETE);

        ResponseEntity<String> response = fileController.deleteFile(fileId, req);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("File update failed", response.getBody());
        verify(fileService, times(1)).update(fileId, req, Action.DELETE);
    }

    @Test
    void testUpdateSuccess() throws FileNotFoundException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FileUpdateRequest req = new FileUpdateRequest("file.txt", userId);

        doNothing().when(fileService).update(fileId, req, Action.UPDATE);

        ResponseEntity<String> response = fileController.update(fileId, req);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("File successfully updated", response.getBody());
        verify(fileService, times(1)).update(fileId, req, Action.UPDATE);
    }

    @Test
    void testUpdateFileNotFound() throws FileNotFoundException {
        UUID fileId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FileUpdateRequest req = new FileUpdateRequest("file.txt", userId);

        doThrow(new FileNotFoundException("File not found")).when(fileService).update(fileId, req, Action.UPDATE);

        ResponseEntity<String> response = fileController.update(fileId, req);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("File update failed", response.getBody());
        verify(fileService, times(1)).update(fileId, req, Action.UPDATE);
    }
}
