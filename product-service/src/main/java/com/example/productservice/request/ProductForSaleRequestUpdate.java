package com.example.productservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.productservice.model.ProductForSale;
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
public class ProductForSaleRequestUpdate {

    @NotEmpty(message = ValidationConfig.POST_TITLE_REQUIRE)
    @NotNull(message = ValidationConfig.NULL_TITLE)
    @Size(min = 5, max = 25, message = ValidationConfig.POST_TITLE_MESSAGE)
    private String title;

    @NotNull(message = ValidationConfig.NULL_FILE)
    @NotEmpty(message = ValidationConfig.EMPTY_FILE)
    @Size(min = 1 , max = 6, message = ValidationConfig.COUNT_IMAGE)
    private List<String> files;

    @NotNull(message = ValidationConfig.NULL_DESCRIPTION)
    @NotEmpty(message = ValidationConfig.EMPTY_DESCRIPTION)
    private String description;

    @NotNull(message = ValidationConfig.NULL_STATUS)
    private Boolean status;

}
