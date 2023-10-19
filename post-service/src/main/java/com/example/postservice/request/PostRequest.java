package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.postservice.model.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private List<String> file;
    @Size(max =ValidationConfig.POST_DESCRIPTION_MAX ,message = ValidationConfig.POST_DESCRIPTION_MESSAGE)
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private String description;

    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private Float budget;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private UUID subCategoryId;

    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private Boolean status;


    public Post toEntity(UUID userId){
        return new Post(null,this.title,this.file.toString(),this.description, this.budget,this.subCategoryId,this.status, LocalDateTime.now(),LocalDateTime.now(),userId);
    }

}
