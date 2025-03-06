package com.example.text_drive.dto;

import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import com.example.text_drive.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private UserDTO owner;
    private List<FileDTO> files;

    @JsonIgnore //So this is not included in response
    @Value("${system.user.fallback.password}")  // Password from application.properties
    private String systemFallbackPassword;

    /**
     * Constructor to convert Folder to FolderDTO.
     *
     * @param folder The entity to convert into a DTO.
     */
    public FolderDTO(Folder folder) {
        if (folder != null) {
            this.id = folder.getId();
            this.name = folder.getName();

            // Convert owner to UserDTO if present, otherwise use system fallback
            this.owner = folder.getOwner() != null
                    ? new UserDTO(folder.getOwner())
                    : new UserDTO(systemFallbackUser());  // Fallback to system user if owner is null

            // Map folder's files to FileDTOs
            this.files = folder.getFiles() != null
                    ? folder.getFiles().stream()
                    .map(FileDTO::new)
                    .collect(Collectors.toList())
                    : List.of();
        }
    }

    /**
     * Provides a fallback user when the folder owner is not available, mostly for development purposes.
     *
     * @return A User object representing the system user.
     */
    private User systemFallbackUser() {
        User systemUser = new User();
        systemUser.setUsername("systemUser");
        systemUser.setPassword(new BCryptPasswordEncoder().encode(systemFallbackPassword));

        // Admin role
        systemUser.setRole(Role.ROLE_ADMIN);
        return systemUser;
    }
}