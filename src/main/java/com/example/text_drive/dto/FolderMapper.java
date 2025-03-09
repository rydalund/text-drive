package com.example.text_drive.dto;

import com.example.text_drive.hateoas.LinkBuilder;
import com.example.text_drive.model.Folder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Folder entities and FolderDTOs.
 * This class provides methods for mapping a Folder object to a FolderDTO
 * including nested mappings for owner (UserDTO) and files (FileDTOs).
 */
@Component
public class FolderMapper {

    private final LinkBuilder linkBuilder;

    public FolderMapper(LinkBuilder linkBuilder) {
        this.linkBuilder = linkBuilder;
    }

    public FolderDTO toDTO(Folder folder, Authentication authentication) {
        if (folder == null) {
            return null;
        }

        FolderDTO dto = new FolderDTO();
        dto.setId(folder.getId());
        dto.setName(folder.getName());

        // Map owner to UserDTO if available
        if (folder.getOwner() != null) {
            UserDTO userDTO = new UserDTO(folder.getOwner());
            dto.setOwner(userDTO);
        }

        // Map files to FileDTOs using the detailed version of FileDTO
        if (folder.getFiles() != null) {
            List<FileDTO> fileDTOs = folder.getFiles().stream()
                    .map(file -> new FileDTO(file, linkBuilder, authentication)) // Use LinkBuilder
                    .collect(Collectors.toList());
            dto.setFiles(fileDTOs);
        }

        // Add HATEOAS links using LinkBuilder
        dto.add(linkBuilder.getFolderSelfLink(folder.getId(), authentication));
        dto.add(linkBuilder.getUserFoldersLink(authentication));
        dto.add(linkBuilder.getCreateFolderLink());
        dto.add(linkBuilder.getUpdateFolderLink(folder.getId(), authentication));
        dto.add(linkBuilder.getDeleteFolderLink(folder.getId(), authentication));
        dto.add(linkBuilder.getSearchFoldersLink(null, authentication));
        dto.add(linkBuilder.getFilesByFolderIdLink(folder.getId(), authentication));
        dto.add(linkBuilder.getUploadFileLink()); // Link to upload a file
        dto.add(linkBuilder.getFileLink(null, authentication)); // Link to get a file
        dto.add(linkBuilder.getSearchFilesLink(null, authentication)); // Link to search files
        dto.add(linkBuilder.getDownloadFileLink(null, authentication)); // Link to download a file
        dto.add(linkBuilder.getRenameFileLink(null, authentication)); // Link to rename a file
        dto.add(linkBuilder.getDeleteFileLink(null, authentication)); // Link to delete a file
        dto.add(linkBuilder.getRegisterLink()); // Link to register a new user

        return dto;
    }
}