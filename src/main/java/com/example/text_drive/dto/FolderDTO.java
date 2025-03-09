package com.example.text_drive.dto;

import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import com.example.text_drive.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO extends RepresentationModel<FolderDTO> {

    private Long id;
    private String name;
    private UserDTO owner;
    private List<FileDTO> files;

    @JsonIgnore
    @Value("${system.user.fallback.password}")
    private String systemFallbackPassword;

    /**
     * Constructor to convert Folder to FolderDTO.
     *
     * @param folder The entity to convert into a DTO.
     * @param linkBuilder The LinkBuilder instance to generate HATEOAS links.
     * @param authentication The Authentication object for link generation.
     */
    public FolderDTO(Folder folder, LinkBuilder linkBuilder, Authentication authentication) {
        if (folder != null) {
            this.id = folder.getId();
            this.name = folder.getName();

            // Convert owner to UserDTO if present, otherwise use system fallback
            this.owner = folder.getOwner() != null
                    ? new UserDTO(folder.getOwner())
                    : new UserDTO(systemFallbackUser());

            // Map folder's files to FileDTOs
            this.files = folder.getFiles() != null
                    ? folder.getFiles().stream()
                    .map(file -> new FileDTO(file, linkBuilder, authentication))
                    .collect(Collectors.toList())
                    : List.of();

            // Add HATEOAS links using LinkBuilder
            this.add(linkBuilder.getFolderSelfLink(folder.getId(), authentication)); //Folder self-link
            this.add(linkBuilder.getUserFoldersLink(authentication)); // Link to get user's folders
            this.add(linkBuilder.getCreateFolderLink()); // Link to create a folder
            this.add(linkBuilder.getSearchFoldersLink(null, authentication)); // Link to search folders
            this.add(linkBuilder.getUpdateFolderLink(null, authentication)); // Link to update a folder
            this.add(linkBuilder.getDeleteFolderLink(null, authentication)); // Link to delete a folder
            this.add(linkBuilder.getFilesByFolderIdLink(null, authentication)); // Link to get files in a folder
            this.add(linkBuilder.getUploadFileLink()); // Link to upload a file
            this.add(linkBuilder.getFileLink(null, authentication)); // Link to get a file
            this.add(linkBuilder.getSearchFilesLink(null, authentication)); // Link to search files
            this.add(linkBuilder.getDownloadFileLink(null, authentication)); // Link to download a file
            this.add(linkBuilder.getRenameFileLink(null, authentication)); // Link to rename a file
            this.add(linkBuilder.getDeleteFileLink(null, authentication)); // Link to delete a file
            this.add(linkBuilder.getRegisterLink()); // Link to register a new user
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
        systemUser.setRole(Role.ROLE_ADMIN);
        return systemUser;
    }
}
