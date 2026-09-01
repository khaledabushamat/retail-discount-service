package io.github.khaledabushamat.discount.billing.domain.policy;

import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.customer;
import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.electronics;
import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.bill;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;

class LoyaltyDiscountPolicyTest {

    private final DiscountRateProvider rates = type -> BigDecimal.valueOf(5);
    private final LoyaltyDiscountPolicy policy = new LoyaltyDiscountPolicy(rates);

    @ParameterizedTest
    @CsvSource({
            "2023-06-01, true",    // 2 years 7 months
            "2023-12-31, true",    // 2 years and 1 day
            "2024-01-01, false",   // exactly 2 years — not MORE than
            "2024-01-02, false",   // just under
            "2025-06-01, false"    // well under
    })
    void appliesOnlyAfterMoreThanTwoYears(LocalDate joinedAt, boolean expected) {
        var bill = bill(customer(joinedAt), electronics("100.00"));

        assertThat(policy.appliesTo(bill)).isEqualTo(expected);
    }
}