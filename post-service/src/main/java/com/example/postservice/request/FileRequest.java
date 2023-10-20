package com.example.postservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {

    private String fileName;
    private String fileUrl;
    private String fileType;
    private String filePath;
    private Long size;

}
