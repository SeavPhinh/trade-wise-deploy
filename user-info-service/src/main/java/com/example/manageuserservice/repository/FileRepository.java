package com.example.manageuserservice.repository;

import com.example.manageuserservice.model.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileStorage, UUID> {

}
