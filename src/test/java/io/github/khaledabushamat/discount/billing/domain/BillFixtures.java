package io.github.khaledabushamat.discount.billing.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import io.github.khaledabushamat.discount.shared.Money;

public final class BillFixtures {

    public static final LocalDate BILLED_ON = LocalDate.of(2026, 1, 1);

    public static Customer customer(LocalDate joinedAt, CustomerType... types) {
        return new Customer("cust-1", joinedAt, Set.of(types));
    }

    public static Customer recentCustomer(CustomerType... types) {
        return customer(LocalDate.of(2025, 6, 1), types);
    }

    public static LineItem groceries(String price) {
        return new LineItem("g-1", Category.GROCERY, Money.of(price), 1);
    }

    public static LineItem electronics(String price) {
        return new LineItem("e-1", Category.NON_GROCERY, Money.of(price), 1);
    }

    public static Bill bill(Customer customer, LineItem... items) {
        return new Bill(customer, List.of(items), BILLED_ON);
    }
}
