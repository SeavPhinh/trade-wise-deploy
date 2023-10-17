package com.example.manageuserservice.request;

import com.example.manageuserservice.model.FileStorage;
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
    private Long size;

    public FileStorage toEntity(){
        return new FileStorage(null, this.fileName,this.fileUrl,this.fileType,this.size);
    }

}
