package com.example.text_drive.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a Folder in the system,
 * the class maps to the folder table in the database
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private User owner;  // The owner of the folder (User entity)

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;  // The list of files inside the folder. Cascade operations are applied.

    /**
     * Constructor to create a Folder with a name and an owner.
     * Initializes the files list as an empty list.
     *
     * @param name The name of the folder.
     * @param owner The owner of the folder (User).
     */
    public Folder(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.files = new ArrayList<>();  // Initialize files as an empty list to avoid NullPointerException
    }
}