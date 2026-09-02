package io.github.khaledabushamat.discount.billing.domain;

import java.time.LocalDate;
import java.util.List;

import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.shared.Money;

public record Bill(Customer customer, List<LineItem> items, LocalDate billedOn) {
    public Bill {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("bill must have at least one item");
        }
        items = List.copyOf(items);
    }

    public Money grossTotal() {
        return items.stream().map(LineItem::total).reduce(Money.ZERO, Money::plus);
    }

    public Money nonGroceryTotal() {
        return items.stream()
                .filter(item -> !item.isGrocery())
                .map(LineItem::total)
                .reduce(Money.ZERO, Money::plus);
    }
}
