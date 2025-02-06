package com.example.text_drive.service;

import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public Folder createFolder(String name, Authentication authentication) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name cannot be empty");
        }
        User user = (User) authentication.getPrincipal();

        Folder folder = new Folder();
        folder.setName(name);
        folder.setOwner(user);
        return folderRepository.save(folder);
    }

    public Folder getFolderById(Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return folderRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Folder not found with id " + id + " for current user"));
    }

    public List<Folder> getUserFolders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return folderRepository.findByOwner(user);
    }

    public List<Folder> searchFoldersByName(String name, Authentication authentication) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search name cannot be empty");
        }

        User user = (User) authentication.getPrincipal();
        List<Folder> folders = folderRepository.findByNameContainingIgnoreCaseAndOwner(name, user);

        if (folders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No folders found with name " + name + " for current user");
        }

        return folders;
    }

    public void deleteFolder(Long id, Authentication authentication) {
        Folder folder = getFolderById(id, authentication);
        folderRepository.delete(folder);
    }

    public Folder updateFolder(Long id, String newName, Authentication authentication) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New folder name cannot be empty");
        }

        Folder folder = getFolderById(id, authentication);
        folder.setName(newName);
        return folderRepository.save(folder);
    }
}