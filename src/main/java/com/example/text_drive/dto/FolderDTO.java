package com.example.text_drive.dto;

import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for Folder entity, used to transfer folder data between layers of the application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {

    private Long id;
    private String name;
    private User owner;
    private List<FileDTO> files;

    /**
     * Constructor to convert Folder to FolderDTO.
     *
     * @param folder The entity to convert into a DTO.
     */
    public FolderDTO(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();

        // Map folder's files to their corresponding FileDTOs
        this.files = (folder.getFiles() == null) ?
                List.of() : folder.getFiles().stream()
                .map(FileDTO::new)  // Convert each File entity to a FileDTO
                .collect(Collectors.toList());

        // Set owner of the folder, fallback to system user if owner is null
        this.owner = (folder.getOwner() != null) ?
                folder.getOwner() : systemFallbackUser();
    }

    /**
     * Provides a fallback user when the folder owner is not available, mostly for developing purpose.
     *
     * @return A User object representing the system user.
     */
    private User systemFallbackUser() {
        User systemUser = new User();
        systemUser.setUsername("systemUser");
        return systemUser;
    }
}