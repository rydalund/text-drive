package com.example.text_drive.hateoas;

import com.example.text_drive.controller.FileController;
import com.example.text_drive.controller.FolderController;
import com.example.text_drive.controller.UserController;
import com.example.text_drive.dto.LoginRequestDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Class responsible for generating HATEOAS links for controllers.
 * It uses WebMvcLinkBuilder to create links dynamically based on the controller methods.
 */
@Component
public class LinkBuilder {

    //Placeholder for LoginRequestDTO
    private final LoginRequestDTO loginRequestPlaceholder = new LoginRequestDTO("username", "password");

    //Placeholder for Authentication
    private final Authentication authenticationPlaceholder = null; // Or a mock authentication if needed

    //UserController Links
    /**
     * Generates a self-link for a user.
     *
     * @param userId The UUID of the user.
     * @return A self-link for the user.
     */
    public Link getUserSelfLink(UUID userId) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId, authenticationPlaceholder)
        ).withSelfRel();
    }

    /**
     * Generates a link for registering a new user.
     *
     * @return A link for user registration.
     */
    public Link getRegisterLink() {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).createUser(null)
        ).withRel("register");
    }

    /**
     * Generates a login link.
     *
     * @return A link for user login.
     */
    public Link getLoginLink() {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).login(loginRequestPlaceholder, authenticationPlaceholder)
        ).withRel("login");
    }

    /**
     * Generates a link for user details.
     *
     * @param userId The UUID of the user.
     * @return A link for user details.
     */
    public Link getUserDetailsLink(UUID userId) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).getUser(userId, authenticationPlaceholder)
        ).withRel("user-details");
    }

    //FolderController Links

    /**
     * Generates a self-link for a folder.
     *
     * @param folderId The ID of the folder.
     * @param authentication The authentication object.
     * @return A self-link for the folder.
     */
    public Link getFolderSelfLink(Long folderId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).getFolder(folderId, authentication)
        ).withSelfRel();
    }

    /**
     * Generates a link for creating a folder.
     *
     * @return A link for creating a folder.
     */
    public Link getCreateFolderLink() {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).createFolder(null, authenticationPlaceholder)
        ).withRel("create-folder");
    }

    /**
     * Generates a link for retrieving a folder.
     *
     * @param folderId The ID of the folder.
     * @param authentication The authentication object.
     * @return A link for retrieving a folder.
     */
    public Link getFolderLink(Long folderId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).getFolder(folderId, authentication)
        ).withRel("get-folder");
    }

    /**
     * Generates a link for retrieving all folders of a user.
     *
     * @param authentication The authentication object.
     * @return A link for retrieving user folders.
     */
    public Link getUserFoldersLink(Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).getUserFolders(authentication)
        ).withRel("get-user-folders");
    }

    /**
     * Generates a link for searching folders by name.
     *
     * @param name The name to search for.
     * @param authentication The authentication object.
     * @return A link for searching folders.
     */
    public Link getSearchFoldersLink(String name, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).searchFoldersByName(name, authentication)
        ).withRel("search-folders");
    }

    /**
     * Generates a link for deleting a folder.
     *
     * @param folderId The ID of the folder.
     * @param authentication The authentication object.
     * @return A link for deleting a folder.
     */
    public Link getDeleteFolderLink(Long folderId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).deleteFolder(folderId, authentication)
        ).withRel("delete-folder")
         .withTitle("Requires ROLE_ADMIN"); //To inform that Admin is needed for this
    }

    /**
     * Generates a link for updating a folder.
     *
     * @param folderId The ID of the folder.
     * @param authentication The authentication object.
     * @return A link for updating a folder.
     */
    public Link getUpdateFolderLink(Long folderId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FolderController.class).updateFolder(folderId, null, authentication)
        ).withRel("update-folder");
    }

    /**
     * Generates a link for retrieving files in a folder.
     *
     * @param folderId The ID of the folder.
     * @param authentication The authentication object.
     * @return A link for retrieving files in a folder.
     */
    public Link getFilesByFolderIdLink(Long folderId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).getFilesByFolderId(folderId, authentication)
        ).withRel("files-in-folder");
    }

    //FileController Links

    /**
     * Generates a self-link for a file.
     *
     * @param fileId The ID of the file.
     * @param authentication The authentication object.
     * @return A self-link for the file.
     */
    public Link getFileSelfLink(Long fileId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).getFile(fileId, authentication)
        ).withSelfRel();
    }

    /**
     * Generates a link for uploading a file.
     *
     * @return A link for uploading a file.
     */
    public Link getUploadFileLink() {
        //Placeholder for Long (e.g., folderId, fileId)
        Long longPlaceholder = 1L;
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).uploadFile(null, longPlaceholder, authenticationPlaceholder)
        ).withRel("upload-file");
    }

    /**
     * Generates a link for retrieving a file.
     *
     * @param fileId The ID of the file.
     * @param authentication The authentication object.
     * @return A link for retrieving a file.
     */
    public Link getFileLink(Long fileId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).getFile(fileId, authentication)
        ).withRel("get-file");
    }

    /**
     * Generates a link for downloading a file.
     *
     * @param fileId The ID of the file.
     * @param authentication The authentication object.
     * @return A link for downloading a file.
     */
    public Link getDownloadFileLink(Long fileId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).downloadFile(fileId, authentication)
        ).withRel("download-file");
    }

    /**
     * Generates a link for searching files by name.
     *
     * @param name The name to search for.
     * @param authentication The authentication object.
     * @return A link for searching files.
     */
    public Link getSearchFilesLink(String name, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).searchFilesByName(name, authentication)
        ).withRel("search-files");
    }

    /**
     * Generates a link for deleting a file.
     *
     * @param fileId The ID of the file.
     * @param authentication The authentication object.
     * @return A link for deleting a file.
     */
    public Link getDeleteFileLink(Long fileId, Authentication authentication) {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).deleteFile(fileId, authentication)
        ).withRel("delete-file");
    }

    /**
     * Generates a link for renaming a file.
     *
     * @param fileId The ID of the file.
     * @param authentication The authentication object.
     * @return A link for renaming a file.
     */
    public Link getRenameFileLink(Long fileId, Authentication authentication) {
        // Placeholder for String (e.g., name)
        String stringPlaceholder = "placeholder";
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(FileController.class).renameFile(fileId, stringPlaceholder, authentication)
        ).withRel("rename-file");
    }
}