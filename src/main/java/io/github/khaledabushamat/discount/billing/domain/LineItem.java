package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.shared.Money;

public record LineItem(
        String productId,
        Category category,
        Money unitPrice,
        int quantity
) {
    public LineItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }

    public Money total() {
        return unitPrice.multipliedBy(quantity);
    }

    public boolean isGrocery() {
        return category == Category.GROCERY;
    }
}