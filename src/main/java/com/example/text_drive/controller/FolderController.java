package com.example.text_drive.controller;

import com.example.text_drive.dto.FolderDTO;
import com.example.text_drive.model.Folder;
import com.example.text_drive.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<?> createFolder(@Valid @RequestBody FolderDTO folderDTO) {
        try {
            if (folderDTO.getName() == null || folderDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Folder name cannot be empty");
            }
            Folder folder = folderService.createFolder(folderDTO.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(new FolderDTO(folder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFolder(@PathVariable Long id) {
        try {
            Folder folder = folderService.getFolderById(id);
            return ResponseEntity.ok(new FolderDTO(folder));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
        }
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> getAllFolders() {
        List<Folder> folders = folderService.getAllFolders();
        List<FolderDTO> folderDTOs = folders.stream()
                .map(FolderDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(folderDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFoldersByName(@RequestParam String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search cannot be empty");
            }
            List<Folder> folders = folderService.searchFoldersByName(name);
            List<FolderDTO> folderDTOs = folders.stream()
                    .map(FolderDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(folderDTOs);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFolder(@PathVariable Long id) {
        try {
            folderService.deleteFolder(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFolder(@PathVariable Long id, @Valid @RequestBody FolderDTO folderDTO) {
        try {
            if (folderDTO.getName() == null || folderDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Folder name cannot be empty");
            }
            Folder folder = folderService.updateFolder(id, folderDTO.getName());
            return ResponseEntity.ok(new FolderDTO(folder));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}