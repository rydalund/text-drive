package com.example.text_drive.repository;

import com.example.text_drive.model.File;
import com.example.text_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findByIdAndFolderOwner(Long id, User owner);
    List<File> findByNameContainingIgnoreCaseAndFolderOwner(String name, User owner);
}