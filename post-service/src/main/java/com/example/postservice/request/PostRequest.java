package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.postservice.model.Post;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = ValidationConfig.POST_TITLE_REQUIRE)
    @NotEmpty(message = ValidationConfig.POST_TITLE_REQUIRE)
    @Size(min = 5, max = 25, message = ValidationConfig.POST_TITLE_MESSAGE)
    private String title;

    private String file;

    @Size(max =ValidationConfig.POST_DESCRIPTION_MAX ,message = ValidationConfig.POST_DESCRIPTION_MESSAGE)
    @NotNull(message = ValidationConfig.NULL_DESCRIPTION)
    @NotEmpty(message = ValidationConfig.EMPTY_DESCRIPTION)
    private String description;

    @NotNull(message = ValidationConfig.NULL_BUDGET_FROM)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE_BUDGET_TO)
    private Float budgetFrom;

    @NotNull(message = ValidationConfig.NULL_BUDGET_TO)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE_BUDGET_FROM)
    private Float budgetTo;

    @NotNull(message = ValidationConfig.NULL_SUB_CATEGORY)
    @NotEmpty(message = ValidationConfig.EMPTY_SUB_CATEGORY)
    private String subCategory;

    @NotNull(message = ValidationConfig.NULL_STATUS)
    private Boolean status;

    public Post toEntity(UUID userId){
        return new Post(null,this.title.trim(),this.file,this.description,this.budgetFrom,this.budgetTo,this.subCategory,this.status,LocalDateTime.now(),LocalDateTime.now(),userId);
    }

}
