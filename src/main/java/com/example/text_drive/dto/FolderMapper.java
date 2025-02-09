package com.example.text_drive.dto;

import com.example.text_drive.model.Folder;
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

    public FolderDTO toDTO(Folder folder) {
        if (folder == null) {
            return null;
        }

        FolderDTO dto = new FolderDTO();
        dto.setId(folder.getId());
        dto.setName(folder.getName());

        // Map owner to UserDTO if available
        if (folder.getOwner() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(folder.getOwner().getId());
            userDTO.setUsername(folder.getOwner().getUsername());
            dto.setOwner(userDTO);
        }

        // Map files to FileDTOs using the detailed version of FileDTO
        if (folder.getFiles() != null) {
            List<FileDTO> fileDTOs = folder.getFiles().stream()
                    .map(FileDTO::new)  // Using the constructor of FileDTO that takes a File
                    .collect(Collectors.toList());
            dto.setFiles(fileDTOs);
        }

        return dto;
    }
}