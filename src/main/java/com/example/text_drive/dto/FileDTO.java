package com.example.text_drive.dto;

import com.example.text_drive.model.File;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Data Transfer Object (DTO) for File entity, used to transfer file data between layers of the application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    private Long id;

    @NotBlank(message = "File name cannot be blank")
    private String name;

    private String content;
    private Long folderId;

    /**
     * Constructor to convert File to FileDTO.
     *
     * @param file The entity to convert into a DTO.
     */
    public FileDTO(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.content = file.getContent();

        // Set folderId, or null if folder is not available
        this.folderId = (file.getFolder() != null) ? file.getFolder().getId() : null;
    }
}