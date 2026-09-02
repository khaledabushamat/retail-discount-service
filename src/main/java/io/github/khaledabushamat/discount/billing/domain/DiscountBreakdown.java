package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.shared.Money;

public record DiscountBreakdown(Money grossTotal, Money percentageDiscount, Money flatDiscount) {
    public Money totalDiscount() {
        return percentageDiscount.plus(flatDiscount);
    }

    public Money netPayable() {
        return grossTotal.minus(totalDiscount());
    }
}
