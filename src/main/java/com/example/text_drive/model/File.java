package com.example.text_drive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a File entity in the system.
 * This class is mapped to a database table using JPA annotations.
 */
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

    //Text instead of "standard" varchar, to enable bigger files
    @NotBlank(message = "File content cannot be empty")
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "folder_id")
    private Folder folder;

    /**
     * Constructor to create a new File instance with specified parameters.
     *
     * @param name    The name of the file.
     * @param content The content of the file.
     * @param folder  The folder to which the file belongs.
     */
    public File(String name, String content, Folder folder) {
        this.name = name;
        this.content = content;
        this.folder = folder;
    }
}