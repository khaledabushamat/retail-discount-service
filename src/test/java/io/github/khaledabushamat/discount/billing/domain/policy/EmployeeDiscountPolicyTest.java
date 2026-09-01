package io.github.khaledabushamat.discount.billing.domain.policy;

import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import io.github.khaledabushamat.discount.shared.Money;

class EmployeeDiscountPolicyTest {

    private final DiscountRateProvider rates =
            type -> BigDecimal.valueOf(30);

    private final EmployeeDiscountPolicy policy = new EmployeeDiscountPolicy(rates);

    @Test
    void appliesToEmployees() {
        var bill = bill(recentCustomer(CustomerType.EMPLOYEE), electronics("100.00"));

        assertThat(policy.appliesTo(bill)).isTrue();
    }

    @Test
    void doesNotApplyToNonEmployees() {
        var bill = bill(recentCustomer(CustomerType.AFFILIATE), electronics("100.00"));

        assertThat(policy.appliesTo(bill)).isFalse();
    }

    @Test
    void discountsThirtyPercentOfNonGroceryItems() {
        var bill = bill(recentCustomer(CustomerType.EMPLOYEE),
                electronics("890.00"),
                groceries("100.00"));

        assertThat(policy.discountFor(bill)).isEqualTo(Money.of("267.00"));
    }

    @Test
    void discountsNothingWhenEverythingIsGrocery() {
        var bill = bill(recentCustomer(CustomerType.EMPLOYEE), groceries("500.00"));

        assertThat(policy.discountFor(bill)).isEqualTo(Money.ZERO);
    }
}