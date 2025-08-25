package org.storage.controller;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.storage.controller.request.FileUpdateRequest;
import org.storage.controller.resposne.FileUploadResponse;
import org.storage.model.*;
import org.storage.service.FileService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("userId") String userId,
            @RequestPart("visibility") String visibility,
            @RequestPart("tags") Optional<String> tag,
            @RequestPart("fileName") Optional<String> fileName
    ) {
        try {
            FileUploadMetadata fileUploadMetadata = buildFileuploadMetadata(userId, fileName, visibility, tag);
            FileUploadResponse response = fileService.upload(file, fileUploadMetadata);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException | NoSuchAlgorithmException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/update/{fileId}")
    public ResponseEntity<String> update(@PathVariable UUID fileId, @RequestBody FileUpdateRequest fileUpdateRequest) {
        try {
            fileService.update(fileId, fileUpdateRequest, Action.UPDATE);
            return ResponseEntity.ok("File successfully updated");
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().body("File update failed");
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<FileData>> getFiles(@RequestParam("userId") UUID userId, Pageable pageable) {
        Page<FileData> files = fileService.getFiles(userId, pageable);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable UUID fileId, @RequestBody FileUpdateRequest fileUpdateRequest) {
        try {
            fileService.update(fileId, fileUpdateRequest, Action.DELETE);
            return ResponseEntity.ok("File successfully deleted");
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().body("File update failed");
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable UUID fileId, @RequestParam("userId") UUID userId) {
        try {
            FileDownloadData fileDownloadData = fileService.download(fileId, userId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDownloadData.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(fileDownloadData.getResource().getFile().toPath()))
                    .body(fileDownloadData.getResource());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private FileUploadMetadata buildFileuploadMetadata(String id, Optional<String> fileName, String visibility, Optional<String> tag) throws IllegalArgumentException {
        List<String> tags = Arrays.stream(tag.orElse("").split(",")).toList();
        if (tags.size() > 5) {
            throw new IllegalArgumentException();
        }
        Visibility vis = Visibility.fromString(visibility);
        return new FileUploadMetadata(id, fileName, vis, tags);
    }
}
