package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.postservice.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = ValidationConfig.POST_TITLE_REQUIRE)
    @NotEmpty(message = ValidationConfig.POST_TITLE_REQUIRE)
    @Size(min = 5,max =25,message = ValidationConfig.POST_TITLE_MESSAGE)
    private String title;

    private List<String> file;
    @Size(max =ValidationConfig.POST_DESCRIPTION_MAX ,message = ValidationConfig.POST_DESCRIPTION_MESSAGE)
    private String description;

    private Float budget;
    private UUID subCategoryId;

    private Boolean status;


    public Post toEntity(UUID userId){
        return new Post(null,this.title,this.file.toString(),this.description, this.budget,this.subCategoryId,this.status, LocalDateTime.now(),LocalDateTime.now(),userId);
    }

}
