package com.example.text_drive.controller;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.model.File;
import com.example.text_drive.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long folderId, Authentication authentication) {
        File uploadedFile = fileService.uploadFile(file, folderId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FileDTO(uploadedFile));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileDTO> getFile(@PathVariable Long fileId, Authentication authentication) {
        File file = fileService.getFileById(fileId, authentication);
        return ResponseEntity.ok(new FileDTO(file));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId, Authentication authentication) {
        File file = fileService.downloadFile(fileId, authentication);
        return ResponseEntity.ok(file.getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileDTO>> searchFilesByName(@RequestParam String name, Authentication authentication) {
        List<File> files = fileService.searchFilesByName(name, authentication);
        List<FileDTO> fileDTOs = files.stream()
                .map(FileDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(fileDTOs);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, Authentication authentication) {
        fileService.deleteFile(fileId, authentication);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<FileDTO> renameFile(@PathVariable Long fileId,
                                              @RequestParam String newName,
                                              Authentication authentication) {
        File file = fileService.renameFile(fileId, newName, authentication);
        return ResponseEntity.ok(new FileDTO(file));
    }
}