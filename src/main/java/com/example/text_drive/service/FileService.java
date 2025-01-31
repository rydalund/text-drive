package com.example.text_drive.service;

import com.example.text_drive.dto.FileDTO;
import com.example.text_drive.model.File;
import com.example.text_drive.model.Folder;
import com.example.text_drive.repository.FileRepository;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    public FileDTO uploadFile(MultipartFile file, Long folderId) {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("text/")) {
            throw new IllegalArgumentException("Filen är inte en textfil!");
        }

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Mappen hittades inte"));

        File fileEntity = new File(file.getOriginalFilename(), file.getOriginalFilename(), folder);
        fileRepository.save(fileEntity);

        return new FileDTO(fileEntity);
    }

    public String downloadFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fil inte funnen"));
        return file.getContent();
    }

    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Fil inte funnen"));
        fileRepository.delete(file);
    }
}