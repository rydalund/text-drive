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

/**
 * Service class responsible for managing folders.
 */
@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Creates a new folder with the specified name and assigns it to the authenticated user.
     *
     * @param name The name of the folder to create.
     * @param authentication The authentication object containing user details.
     * @return The created folder entity.
     * @throws ResponseStatusException if the folder name is empty.
     */
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

    /**
     * Retrieves a folder by its ID, ensuring that the folder belongs to the authenticated user.
     *
     * @param id The ID of the folder to retrieve.
     * @param authentication The authentication object containing user details.
     * @return The folder entity.
     * @throws ResponseStatusException if the folder is not found or the user does not have access to it.
     */
    public Folder getFolderById(Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();  // Get the authenticated user
        return folderRepository.findByIdAndOwner(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Folder not found with id " + id + " for current user"));
    }

    /**
     * Retrieves all folders owned by the authenticated user.
     *
     * @param authentication The authentication object containing user details.
     * @return A list of folders owned by the user.
     */
    public List<Folder> getUserFolders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return folderRepository.findByOwner(user);
    }

    /**
     * Searches for folders by name, ensuring the search term is not empty.
     *
     * @param name The name of the folder(s) to search for.
     * @param authentication The authentication object containing user details.
     * @return A list of folders matching the search term.
     * @throws ResponseStatusException if the search term is empty or no folders are found.
     */
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

    /**
     * Deletes a folder by its ID after verifying it belongs to the authenticated user, only ADMIN.
     *
     * @param id The ID of the folder to delete.
     * @param authentication The authentication object containing user details.
     */
    public void deleteFolder(Long id, Authentication authentication) {
        Folder folder = getFolderById(id, authentication);
        folderRepository.delete(folder);  // Delete the folder from the repository
    }

    /**
     * Updates the name of a folder.
     * Validates that the new folder name is not empty.
     *
     * @param id The ID of the folder to update.
     * @param newName The new name for the folder.
     * @param authentication The authentication object containing user details.
     * @return The updated folder entity.
     * @throws ResponseStatusException if the new folder name is empty.
     */
    public Folder updateFolder(Long id, String newName, Authentication authentication) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New folder name cannot be empty");
        }

        Folder folder = getFolderById(id, authentication);
        folder.setName(newName);  // Set the new folder name
        return folderRepository.save(folder);  // Save the updated folder to the database
    }
}