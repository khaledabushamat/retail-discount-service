package io.github.khaledabushamat.discount.billing.domain.policy;

import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.khaledabushamat.discount.shared.Money;

class BulkDiscountPolicyTest {

    private final BulkDiscountPolicy policy = new BulkDiscountPolicy();

    @ParameterizedTest
    @CsvSource({
            "990.00, 45.00",
            "100.00,  5.00",
            " 99.99,  0.00",
            "199.99,  5.00"
    })
    void givesFiveDollarsPerCompleteHundred(String total, String expected) {
        var bill = bill(recentCustomer(), electronics(total));

        assertThat(policy.discountFor(bill)).isEqualTo(Money.of(expected));
    }

    @Test
    void includesGroceriesInTheThreshold() {
        var bill = bill(recentCustomer(), groceries("100.00"));

        assertThat(policy.discountFor(bill)).isEqualTo(Money.of("5.00"));
    }
}