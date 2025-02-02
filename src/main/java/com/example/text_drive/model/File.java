package com.example.text_drive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File name cannot be blank")
    private String name;

    @NotBlank(message = "File content cannot be blank")
    private String content;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    public File(String name, String content, Folder folder) {
        this.name = name;
        this.content = content;
        this.folder = folder;
    }
}