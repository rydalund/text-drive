package com.example.text_drive.dto;

import com.example.text_drive.hateoas.LinkBuilder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.Authentication;

/**
 * Data Transfer Object (DTO) representing the response for a login operation.
 * Extends RepresentationModel to support HATEOAS links.
 */
@Getter
public class LoginResponseDTO extends RepresentationModel<LoginResponseDTO> {

    private final String token;
    private final UserResponseDTO user;

    /**
     * Constructs a new LoginResponseDTO with the provided token, user details, and HATEOAS links.
     *
     * @param token The authentication token.
     * @param user The user details.
     * @param linkBuilder The LinkBuilder instance used to generate HATEOAS links.
     * @param authentication The Authentication object used for link generation.
     */
    public LoginResponseDTO(String token, UserResponseDTO user, LinkBuilder linkBuilder, Authentication authentication) {
        this.token = token;
        this.user = user;

        // Add HATEOAS links using LinkBuilder
        this.add(linkBuilder.getLoginLink()); // Self-link for login
        this.add(linkBuilder.getUserDetailsLink(user.getId())); // Link to user details
        this.add(linkBuilder.getCreateFolderLink()); // Link to create a folder
        this.add(linkBuilder.getFolderLink(null, authentication)); // Link to get a folder
        this.add(linkBuilder.getUserFoldersLink(authentication)); // Link to get user's folders
        this.add(linkBuilder.getSearchFoldersLink(null, authentication)); // Link to search folders
        this.add(linkBuilder.getDeleteFolderLink(null, authentication)); // Link to delete a folder
        this.add(linkBuilder.getUpdateFolderLink(null, authentication)); // Link to update a folder
        this.add(linkBuilder.getUploadFileLink()); // Link to upload a file
        this.add(linkBuilder.getFileLink(null, authentication)); // Link to get a file
        this.add(linkBuilder.getDownloadFileLink(null, authentication)); // Link to download a file
        this.add(linkBuilder.getSearchFilesLink(null, authentication)); // Link to search files
        this.add(linkBuilder.getDeleteFileLink(null, authentication)); // Link to delete a file
        this.add(linkBuilder.getRenameFileLink(null, authentication)); // Link to rename a file
        this.add(linkBuilder.getFilesByFolderIdLink(null, authentication)); // Link to get files in a folder
    }
}