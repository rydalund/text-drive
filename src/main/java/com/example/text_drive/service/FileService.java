package com.example.text_drive.service;

import com.example.text_drive.model.File;
import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import com.example.text_drive.repository.FileRepository;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    /**
     * Uploads a file to a specific folder.
     * Validates the file type and folder ownership before saving the file.
     */
    public File uploadFile(MultipartFile file, Long folderId, Authentication authentication) {
        validateTextFile(file);
        User user = (User) authentication.getPrincipal();

        Folder folder = folderRepository.findByIdAndOwner(folderId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder with ID " + folderId + " not found or access denied"
                ));

        File fileEntity = new File(file.getOriginalFilename(), file.getOriginalFilename(), folder);
        try {
            fileEntity.setContent(new String(file.getBytes()));  // Store the file content as a string
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error reading file content"
            );
        }
        return fileRepository.save(fileEntity);
    }

    /**
     * Retrieves a file by its ID.
     * Only files belonging to the authenticated user's folders can be accessed.
     */
    public File getFileById(Long fileId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return fileRepository.findByIdAndFolderOwner(fileId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "File with ID " + fileId + " not found or access denied"
                ));
    }

    /**
     * Searches for files by name.
     * The search term is validated to ensure it is not empty.
     */
    public List<File> searchFilesByName(String name, Authentication authentication) {
        validateSearchTerm(name);  // Validate that the search term is not empty
        User user = (User) authentication.getPrincipal();
        return fileRepository.findByNameContainingIgnoreCaseAndFolderOwner(name, user);
    }

    /**
     * Validates the search term to ensure it is not empty.
     */
    private void validateSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search term cannot be empty"
            );
        }
    }

    /**
     * Validates that the uploaded file is a text file.
     * Throws an exception if the file is empty or not a text file.
     */
    private void validateTextFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file is empty and cannot be uploaded"
            );
        }

        String contentType = file.getContentType();  // Get the content type of the file
        if (contentType == null || !contentType.startsWith("text/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file is not a text file!"
            );
        }
    }

    /**
     * Downloads a file by its ID.
     * Returns the file if the user has permission to access it.
     */
    public File downloadFile(Long fileId, Authentication authentication) {
        return getFileById(fileId, authentication);
    }

    /**
     * Deletes a file by its ID.
     * Ensures the file belongs to the authenticated user's folder before deletion.
     */
    public void deleteFile(Long fileId, Authentication authentication) {
        File file = getFileById(fileId, authentication);  // Fetch the file by ID
        fileRepository.delete(file);  // Delete the file from the repository
    }

    /**
     * Renames a file.
     * Validates the new file name before renaming the file.
     */
    public File renameFile(Long fileId, String newName, Authentication authentication) {
        validateFileName(newName);  // Validate that the new file name is not empty

        File file = getFileById(fileId, authentication);
        file.setName(newName);  // Set the new file name
        return fileRepository.save(file);  // Save the renamed file
    }

    /**
     * Validates that the file name is not empty or null.
     */
    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file name cannot be empty"
            );
        }
    }

    /**
     * Retrieves all files in a folder.
     * Validates that the folder belongs to the authenticated user.
     */
    public List<File> getFilesByFolderId(Long folderId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        // Validate that the folder belongs to the authenticated user
        Folder folder = folderRepository.findByIdAndOwner(folderId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder with ID " + folderId + " not found or access denied"
                ));

        // Fetch files by folderId
        return fileRepository.findByFolderId(folderId);
    }
}