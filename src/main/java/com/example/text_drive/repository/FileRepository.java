package com.example.text_drive.repository;

import com.example.text_drive.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    /**
     * Finds files containing the given string, ignoring case.
     *
     * @param name is the name or part of the name to search.
     * @return A list of files that match the criteria.
     */
    List<File> findByNameContainingIgnoreCase(String name);
}
