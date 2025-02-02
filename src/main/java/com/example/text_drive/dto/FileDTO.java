package com.example.text_drive.dto;

import com.example.text_drive.model.File;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import com.example.text_drive.model.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO {

    private Long id;

    @NotBlank(message = "File name cannot be blank")
    private String name;

    @NotBlank(message = "File content cannot be blank")
    private String content;
    private Long folderId;

    public FileDTO(File file) {
        this.id = file.getId();
        this.name = file.getName();
        this.content = file.getContent();
        this.folderId = (file.getFolder() != null) ? file.getFolder().getId() : null;
    }
}