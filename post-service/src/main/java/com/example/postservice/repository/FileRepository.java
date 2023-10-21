package com.example.postservice.repository;


import com.example.postservice.model.FileStorage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileStorage, UUID> {

    @Transactional
    @Query("select f from FileStorage f where f.fileName= :fileName")
    FileStorage findByName(String fileName);
}
