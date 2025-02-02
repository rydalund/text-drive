package com.example.text_drive.repository;

import com.example.text_drive.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    /**
     * Finds folders containing the given string, ignoring case.
     *
     * @param name is the name or part of the name to search.
     * @return A list of folders that match the criteria.
     */
    List<Folder> findByNameContainingIgnoreCase(String name);
}