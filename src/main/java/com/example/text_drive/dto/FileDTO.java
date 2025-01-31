package com.example.text_drive.dto;

import com.example.text_drive.model.File;
import lombok.Getter;

@Getter
public class FileDTO {

    private final Long id;
    private final String name;

    public FileDTO(File file) {
        this.id = file.getId();
        this.name = file.getName();
    }
}