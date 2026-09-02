package io.github.khaledabushamat.discount.billing.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record CalculateDiscountRequest(
        @NotEmpty(message = "at least one line item is required")
        @Size(max = 100, message = "a maximum of 100 line items is allowed")
        @Valid
        List<LineRequest> lines
) {
    public record LineRequest(
            @NotBlank(message = "productId is required")
            String productId,

            @Positive(message = "quantity must be greater than zero")
            @Max(value = 1000, message = "quantity must not exceed 1000")
            int quantity
    ) {
    }
}