package com.example.text_drive.controller;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long folderId) {
        try {
            FileDTO uploadedFile = fileService.uploadFile(file, folderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Endast textfiler är tillåtna!");
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId) {
        String fileContent = fileService.downloadFile(fileId);
        return ResponseEntity.ok(fileContent);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }
}