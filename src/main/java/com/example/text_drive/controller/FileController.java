package com.example.text_drive.controller;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.File;
import com.example.text_drive.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final LinkBuilder linkBuilder; // Inject LinkBuilder

    @PostMapping
    public ResponseEntity<FileDTO> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long folderId, Authentication authentication) {
        File uploadedFile = fileService.uploadFile(file, folderId, authentication);
        FileDTO responseDTO = new FileDTO(uploadedFile, linkBuilder, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<FileDTO> getFile(@PathVariable Long fileId, Authentication authentication) {
        File file = fileService.getFileById(fileId, authentication);
        FileDTO responseDTO = new FileDTO(file, linkBuilder, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<String> downloadFile(@PathVariable Long fileId, Authentication authentication) {
        File file = fileService.downloadFile(fileId, authentication);
        return ResponseEntity.ok(file.getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<FileDTO>> searchFilesByName(@RequestParam String name, Authentication authentication) {
        List<File> files = fileService.searchFilesByName(name, authentication);
        List<FileDTO> fileDTOs = files.stream()
                .map(file -> new FileDTO(file, linkBuilder, authentication))
                .collect(Collectors.toList());

        CollectionModel<FileDTO> collectionModel = CollectionModel.of(fileDTOs);
        collectionModel.add(linkBuilder.getSearchFilesLink(name, authentication));

        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId, Authentication authentication) {
        fileService.deleteFile(fileId, authentication);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<FileDTO> renameFile(@PathVariable Long fileId, @RequestParam String newName, Authentication authentication) {
        File file = fileService.renameFile(fileId, newName, authentication);
        FileDTO responseDTO = new FileDTO(file, linkBuilder, authentication);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/folder/{folderId}")
    public ResponseEntity<CollectionModel<FileDTO>> getFilesByFolderId(@PathVariable Long folderId, Authentication authentication) {
        List<File> files = fileService.getFilesByFolderId(folderId, authentication);
        List<FileDTO> fileDTOs = files.stream()
                .map(file -> new FileDTO(file, linkBuilder, authentication))
                .collect(Collectors.toList());

        CollectionModel<FileDTO> collectionModel = CollectionModel.of(fileDTOs);
        collectionModel.add(linkBuilder.getFilesByFolderIdLink(folderId, authentication));

        return ResponseEntity.ok(collectionModel);
    }
}