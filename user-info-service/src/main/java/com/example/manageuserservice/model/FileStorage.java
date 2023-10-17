package com.example.manageuserservice.model;

import com.example.manageuserservice.response.FileResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "size")
    private Long size;

    public FileResponse toDto(){
        return new FileResponse(this.id,this.fileName,this.fileUrl,this.fileType,this.size);
    }
}
