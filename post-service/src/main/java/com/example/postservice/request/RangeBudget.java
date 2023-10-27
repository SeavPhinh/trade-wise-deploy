package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeBudget {

    @NotNull(message = ValidationConfig.NULL_FIELD)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE)
    private Float budgetForm;
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_RANGE)
    @NotNull(message = ValidationConfig.NULL_FIELD)
    private Float budgetTo;

}
