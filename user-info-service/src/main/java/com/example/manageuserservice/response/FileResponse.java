package com.example.manageuserservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long size;
}
