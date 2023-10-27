package com.example.postservice.request;

import com.example.commonservice.config.ValidationConfig;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RangeBudget {

    @NotNull(message = ValidationConfig.NULL_FIELD)
    @NotEmpty(message = ValidationConfig.EMPTY_FIELD)
    @Pattern(regexp = "^[+]?(?:\\d+\\.?\\d*|\\d*\\.\\d+|\\d+)$", message = ValidationConfig.INVALID_RANGE)
    private Float budgetForm;
    @Pattern(regexp = "^[+]?(?:\\d+\\.?\\d*|\\d*\\.\\d+|\\d+)$", message = ValidationConfig.INVALID_RANGE)
    @NotNull(message = ValidationConfig.NULL_FIELD)
    @NotEmpty(message = ValidationConfig.EMPTY_FIELD)
    private Float budgetTo;
}
