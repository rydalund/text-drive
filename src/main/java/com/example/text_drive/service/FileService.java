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

    public File uploadFile(MultipartFile file, Long folderId, Authentication authentication) {
        validateTextFile(file);
        User user = (User) authentication.getPrincipal();

        Folder folder = folderRepository.findByIdAndOwner(folderId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Folder with ID " + folderId + " not found"
                ));

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

    public File getFileById(Long fileId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return fileRepository.findByIdAndFolderOwner(fileId, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "File with ID " + fileId + " not found"
                ));
    }

    public List<File> searchFilesByName(String name, Authentication authentication) {
        validateSearchTerm(name);
        User user = (User) authentication.getPrincipal();
        return fileRepository.findByNameContainingIgnoreCaseAndFolderOwner(name, user);
    }

    private void validateSearchTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search term cannot be empty"
            );
        }
    }

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

    public File downloadFile(Long fileId, Authentication authentication) {
        return getFileById(fileId, authentication);
    }

    public void deleteFile(Long fileId, Authentication authentication) {
        File file = getFileById(fileId, authentication);
        fileRepository.delete(file);
    }

    public File renameFile(Long fileId, String newName, Authentication authentication) {
        validateFileName(newName);

        File file = getFileById(fileId, authentication);
        file.setName(newName);
        return fileRepository.save(file);
    }

    private void validateFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The file name cannot be empty"
            );
        }
    }
}