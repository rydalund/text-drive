package com.example.text_drive.dto;

import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.File;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.Authentication;

/**
 * Data Transfer Object (DTO) for File entity, used to transfer file data between layers of the application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO extends RepresentationModel<FileDTO> {

    private Long id;

    @NotBlank(message = "File name cannot be blank")
    private String name;
    private String content;
    private Long folderId;

    /**
     * Constructor to convert File to FileDTO.
     *
     * @param file The entity to convert into a DTO.
     * @param linkBuilder The LinkBuilder instance to generate HATEOAS links.
     * @param authentication The Authentication object for link generation.
     */
    public FileDTO(File file, LinkBuilder linkBuilder, Authentication authentication) {
        this.id = file.getId();
        this.name = file.getName();
        this.content = file.getContent();

        // Set folderId, or null if folder is not available
        this.folderId = (file.getFolder() != null) ? file.getFolder().getId() : null;

        // Add HATEOAS self-link using LinkBuilder
        this.add(linkBuilder.getFileSelfLink(file.getId(), authentication));

        // Add link to the folder containing this file
        if (this.folderId != null) {
            this.add(linkBuilder.getFolderLink(this.folderId, authentication));
        }
    }
}
