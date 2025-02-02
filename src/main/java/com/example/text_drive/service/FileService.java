package com.example.text_drive.service;
import com.example.text_drive.model.File;
import com.example.text_drive.model.Folder;
import com.example.text_drive.repository.FileRepository;
import com.example.text_drive.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;

    public File uploadFile(MultipartFile file, Long folderId) {
        validateTextFile(file);

        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("Mappen med ID " + folderId + " hittades inte"));

        File fileEntity = new File(file.getOriginalFilename(), file.getOriginalFilename(), folder);
        try {
            fileEntity.setContent(new String(file.getBytes()));  // Här sätts filens innehåll
        } catch (IOException e) {
            throw new RuntimeException("Fel vid läsning av filinnehållet", e);  // Om något går fel vid filbehandling
        }

        return fileRepository.save(fileEntity);  // Returnera entiteten
    }

    public List<File> searchFilesByName(String name) {
        return fileRepository.findByNameContainingIgnoreCase(name);
    }

    private void validateTextFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Filen är tom och kan inte laddas upp");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("text/")) {
            throw new IllegalArgumentException("Filen är inte en textfil!");
        }
    }

    public File downloadFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Fil med ID " + fileId + " hittades inte"));
    }

    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("Fil med ID " + fileId + " hittades inte"));

        fileRepository.delete(file);
    }
}