package com.example.text_drive.service;

import com.example.text_drive.model.Folder;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Creates a new folder with the specified name.
     *
     * @param name The name of the new folder.
     * @return The created Folder object.
     * @throws ResponseStatusException if the folder name is empty or invalid.
     */
    public Folder createFolder(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder name cannot be empty");
        }
        Folder folder = new Folder();
        folder.setName(name);
        return folderRepository.save(folder);
    }

    /**
     * Retrieves a folder by its unique identifier.
     *
     * @param id The ID of the folder to retrieve.
     * @return The Folder object with the specified ID.
     * @throws ResponseStatusException if the folder with the given ID does not exist.
     */
    public Folder getFolderById(Long id) {
        return folderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found with id " + id));
    }

    /**
     * Retrieves all folders in the database.
     *
     * @return A list of all Folder objects.
     */
    public List<Folder> getAllFolders() {
        return folderRepository.findAll();
    }

    /**
     * Searches for folders by name, ignoring case.
     *
     * @param name The name (or part of the name) to search for.
     * @return A list of Folder objects that match the search criteria.
     * @throws ResponseStatusException if the search name is empty or if no folders are found.
     */
    public List<Folder> searchFoldersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search name cannot be empty");
        }

        List<Folder> folders = folderRepository.findByNameContainingIgnoreCase(name);

        if (folders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No folders found with name " + name);
        }

        return folders;
    }

    /**
     * Deletes a folder by its unique identifier.
     *
     * @param id The ID of the folder to delete.
     * @throws ResponseStatusException if the folder with the given ID does not exist.
     */
    public void deleteFolder(Long id) {
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found with id " + id));
        folderRepository.delete(folder);
    }

    /**
     * Updates the name of an existing folder.
     *
     * @param id The ID of the folder to update.
     * @param newName The new name for the folder.
     * @return The updated Folder object.
     * @throws ResponseStatusException if the folder with the given ID does not exist or if the new name is invalid.
     */
    public Folder updateFolder(Long id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New folder name cannot be empty");
        }
        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Folder not found with id " + id));
        folder.setName(newName);
        return folderRepository.save(folder);
    }
}