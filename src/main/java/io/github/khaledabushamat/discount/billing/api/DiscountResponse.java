package io.github.khaledabushamat.discount.billing.api;

import io.github.khaledabushamat.discount.billing.domain.DiscountBreakdown;

import java.math.BigDecimal;

public record DiscountResponse(
        BigDecimal grossTotal,
        BigDecimal percentageDiscount,
        BigDecimal flatDiscount,
        BigDecimal totalDiscount,
        BigDecimal netPayable
) {
    static DiscountResponse from(DiscountBreakdown breakdown) {
        return new DiscountResponse(
                breakdown.grossTotal().toBigDecimal(),
                breakdown.percentageDiscount().toBigDecimal(),
                breakdown.flatDiscount().toBigDecimal(),
                breakdown.totalDiscount().toBigDecimal(),
                breakdown.netPayable().toBigDecimal());
    }
}