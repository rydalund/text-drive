package com.example.text_drive.controller;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.model.File;
import com.example.text_drive.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long folderId) {
        try {
            File uploadedFile = fileService.uploadFile(file, folderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new FileDTO(uploadedFile));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileDTO> getFile(@PathVariable Long fileId) {
        try {
            File file = fileService.getFileById(fileId);
            return ResponseEntity.ok(new FileDTO(file));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId) {
        try {
            File file = fileService.downloadFile(fileId);
            return ResponseEntity.ok(file.getContent());
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileDTO>> searchFilesByName(@RequestParam String name) {
        try {
            List<File> files = fileService.searchFilesByName(name);
            List<FileDTO> fileDTOs = files.stream()
                    .map(FileDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(fileDTOs);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<?> renameFile(@PathVariable Long fileId, @Valid @RequestBody FileDTO fileDTO) {
        try {
            if (fileDTO.getName() == null || fileDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("File name cannot be empty");
            }
            File file = fileService.renameFile(fileId, fileDTO.getName());
            return ResponseEntity.ok(new FileDTO(file));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}