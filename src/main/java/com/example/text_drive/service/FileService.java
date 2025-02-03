package com.example.text_drive.service;

import com.example.text_drive.model.File;
import com.example.text_drive.model.Folder;
import com.example.text_drive.repository.FileRepository;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
     * Uploads a file and saves it in the specified folder.
     * Validates that the file is a text file before proceeding.
     *
     * @param file the file to upload
     * @param folderId the ID of the folder where the file will be stored
     * @return the saved File entity
     * @throws ResponseStatusException if the file is not a valid text file or the folder cannot be found
     */
    public File uploadFile(MultipartFile file, Long folderId) {
        validateTextFile(file);

        // Retrieve the folder where the file will be saved
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder with ID " + folderId + " not found"
                ));

        // Create a new File entity
        File fileEntity = new File(file.getOriginalFilename(), file.getOriginalFilename(), folder);
        try {
            fileEntity.setContent(new String(file.getBytes()));
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
     *
     * @param fileId the ID of the file to retrieve
     * @return the File entity
     * @throws ResponseStatusException if the file with the specified ID is not found
     */
    public File getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "File with ID " + fileId + " not found"
                ));
    }

    /**
     * Searches for files by their name.
     * The search is case-insensitive and looks for partial matches.
     *
     * @param name the name of the file(s) to search for
     * @return a list of files that match the search term
     * @throws ResponseStatusException if the search term is empty or null
     */
    public List<File> searchFilesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search term cannot be empty"
            );
        }
        return fileRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Validates that the uploaded file is a text file.
     *
     * @param file the file to validate
     * @throws ResponseStatusException if the file is empty or not a text file
     */
    private void validateTextFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file is empty and cannot be uploaded"
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("text/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file is not a text file!"
            );
        }
    }

    /**
     * Gets a file by its ID.
     *
     * @param fileId the ID of the file to download
     * @return the File entity
     * @throws ResponseStatusException if the file with the specified ID is not found
     */
    public File downloadFile(Long fileId) {
        return getFileById(fileId);
    }

    /**
     * Deletes a file by its ID.
     *
     * @param fileId the ID of the file to delete
     * @throws ResponseStatusException if the file with the specified ID is not found
     */
    public void deleteFile(Long fileId) {
        File file = getFileById(fileId);
        fileRepository.delete(file);
    }


    /**
     * Renames a file, the new name can´t be empty.
     *
     * @param fileId the ID of the file to rename
     * @param newName the new name for the file
     * @return the updated File entity
     * @throws ResponseStatusException if the new name is invalid or the file is not found
     */
    public File renameFile(Long fileId, String newName) {
        // Validate the new name
        if (newName == null || newName.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The new file name cannot be empty"
            );
        }

        // Find the file, throwing an exception if not found
        File file = getFileById(fileId);
        file.setName(newName);
        return fileRepository.save(file);
    }
}