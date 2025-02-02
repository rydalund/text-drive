package com.example.text_drive.controller;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.model.File;
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

    @PostMapping("/upload")
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long folderId) {
        try {
            File uploadedFile = fileService.uploadFile(file, folderId);

            FileDTO uploadedFileDTO = new FileDTO(uploadedFile);

            return ResponseEntity.status(HttpStatus.CREATED).body(uploadedFileDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId) {
        try {
            File file = fileService.downloadFile(fileId);
            return ResponseEntity.ok(file.getContent());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}