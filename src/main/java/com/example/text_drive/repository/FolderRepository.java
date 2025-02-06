package com.example.text_drive.repository;

import com.example.text_drive.model.Folder;
import com.example.text_drive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwner(User owner);
    Optional<Folder> findByIdAndOwner(Long id, User owner);
    List<Folder> findByNameContainingIgnoreCaseAndOwner(String name, User owner);
}