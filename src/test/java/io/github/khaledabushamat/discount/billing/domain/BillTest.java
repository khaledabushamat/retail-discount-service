package io.github.khaledabushamat.discount.billing.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.shared.Money;

class BillTest {

    private static final Customer ANY_CUSTOMER = new Customer("cust-1", LocalDate.of(2024, 1, 1), Set.of());
    private static final LocalDate ANY_DATE = LocalDate.of(2026, 1, 1);

    private static LineItem item(Category category, String price, int quantity) {
        return new LineItem("p-1", category, Money.of(price), quantity);
    }

    private static Bill billOf(LineItem... items) {
        return new Bill(ANY_CUSTOMER, List.of(items), ANY_DATE);
    }

    @Test
    void grossTotalSumsAllItems() {
        Bill bill = billOf(item(Category.GROCERY, "100.00", 1), item(Category.NON_GROCERY, "890.00", 1));

        assertThat(bill.grossTotal()).isEqualTo(Money.of("990.00"));
    }

    @Test
    void grossTotalAccountsForQuantity() {
        Bill bill = billOf(item(Category.NON_GROCERY, "12.50", 4));

        assertThat(bill.grossTotal()).isEqualTo(Money.of("50.00"));
    }

    @Test
    void nonGroceryTotalExcludesGroceries() {
        Bill bill = billOf(item(Category.GROCERY, "100.00", 1), item(Category.NON_GROCERY, "890.00", 1));

        assertThat(bill.nonGroceryTotal()).isEqualTo(Money.of("890.00"));
    }

    @Test
    void nonGroceryTotalIsZeroWhenEverythingIsGrocery() {
        Bill bill = billOf(item(Category.GROCERY, "100.00", 1));

        assertThat(bill.nonGroceryTotal()).isEqualTo(Money.ZERO);
    }

    @Test
    void rejectsEmptyItemList() {
        assertThatThrownBy(() -> new Bill(ANY_CUSTOMER, List.of(), ANY_DATE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void itemsAreImmutableAfterConstruction() {
        List<LineItem> mutable = new java.util.ArrayList<>();
        mutable.add(item(Category.NON_GROCERY, "10.00", 1));

        Bill bill = new Bill(ANY_CUSTOMER, mutable, ANY_DATE);
        mutable.add(item(Category.NON_GROCERY, "999.00", 1));

        assertThat(bill.grossTotal()).isEqualTo(Money.of("10.00"));
    }
}
