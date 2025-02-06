package com.example.text_drive.controller;

import com.example.text_drive.dto.FolderDTO;
import com.example.text_drive.model.Folder;
import com.example.text_drive.service.FolderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> createFolder(@Valid @RequestBody FolderDTO folderDTO, Authentication authentication) {
        try {
            if (folderDTO.getName() == null || folderDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Folder name cannot be empty");
            }
            Folder folder = folderService.createFolder(folderDTO.getName(), authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(new FolderDTO(folder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFolder(@PathVariable Long id, Authentication authentication) {
        try {
            Folder folder = folderService.getFolderById(id, authentication);
            return ResponseEntity.ok(new FolderDTO(folder));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found or access denied");
        }
    }

    @GetMapping
    public ResponseEntity<List<FolderDTO>> getUserFolders(Authentication authentication) {
        List<Folder> folders = folderService.getUserFolders(authentication);
        List<FolderDTO> folderDTOs = folders.stream()
                .map(FolderDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(folderDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFoldersByName(@RequestParam String name, Authentication authentication) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Search cannot be empty");
            }
            List<Folder> folders = folderService.searchFoldersByName(name, authentication);
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
    public ResponseEntity<?> deleteFolder(@PathVariable Long id, Authentication authentication) {
        try {
            folderService.deleteFolder(id, authentication);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found or access denied");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFolder(@PathVariable Long id, @Valid @RequestBody FolderDTO folderDTO, Authentication authentication) {
        try {
            if (folderDTO.getName() == null || folderDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Folder name cannot be empty");
            }
            Folder folder = folderService.updateFolder(id, folderDTO.getName(), authentication);
            return ResponseEntity.ok(new FolderDTO(folder));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found or access denied");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}