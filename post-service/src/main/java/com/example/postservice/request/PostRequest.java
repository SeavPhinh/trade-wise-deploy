package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.postservice.model.Post;
import jakarta.validation.constraints.*;
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
    @Size(min = 5,max =25, message = ValidationConfig.POST_TITLE_MESSAGE)
    private String title;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private String file;
    @Size(max =ValidationConfig.POST_DESCRIPTION_MAX ,message = ValidationConfig.POST_DESCRIPTION_MESSAGE)
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private String description;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE)

    private Float budgetFrom;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE)
    private Float budgetTo;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private String subCategory;
    @NotNull(message = ValidationConfig.NULL_MESSAGE)
    private Boolean status;

    public Post toEntity(UUID userId){
        return new Post(null,this.title.trim(),this.file,this.description,this.budgetFrom,this.budgetTo,this.subCategory,this.status,LocalDateTime.now(),LocalDateTime.now(),userId);
    }

}
