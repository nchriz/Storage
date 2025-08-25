package org.storage.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.storage.repository.entity.MetadataEntity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LocalFileStoreageTest {

    private UserService userService;
    private LocalFileStorage localFileStorage;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        userService = mock(UserService.class);
        tempDir = Files.createTempDirectory("uploads");
        localFileStorage = new LocalFileStorage(userService, tempDir);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(java.io.File::delete);
    }

    @Test
    void testUpload() throws IOException, NoSuchAlgorithmException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        String location = "hello.txt";
        String hash = localFileStorage.upload(file, location);

        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void testDownload() throws IOException, MalformedURLException {
        Path filePath = tempDir.resolve("file.txt");
        Files.writeString(filePath, "Hello");

        MetadataEntity metadata = mock(MetadataEntity.class);
        when(metadata.getFilePath()).thenReturn("file.txt");

        Resource resource = localFileStorage.download(metadata);
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void testCreateUserDirectoryUserExists() throws IOException {
        UUID userId = UUID.randomUUID();
        when(userService.userExists(userId)).thenReturn(true);

        localFileStorage.createUserDirectory(userId);

        Path dir = tempDir.resolve(userId.toString());
        assertFalse(Files.exists(dir));
        verify(userService, times(1)).userExists(userId);
        verify(userService, never()).create(userId);
    }

    @Test
    void testCreateUserDirectoryUserDoesNotExist() throws IOException {
        UUID userId = UUID.randomUUID();
        when(userService.userExists(userId)).thenReturn(false);

        localFileStorage.createUserDirectory(userId);

        Path dir = tempDir.resolve(userId.toString());
        assertTrue(Files.exists(dir));
        verify(userService, times(1)).userExists(userId);
        verify(userService, times(1)).create(userId);
    }
}
