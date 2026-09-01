package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void treatsNullTypesAsEmpty() {
        var customer = new Customer("c-1", LocalDate.of(2024, 1, 1), null);

        assertThat(customer.types()).isEmpty();
    }

    @Test
    void identifiesItsTypes() {
        var customer = new Customer("c-1", LocalDate.of(2024, 1, 1),
                Set.of(CustomerType.EMPLOYEE));

        assertThat(customer.isOfType(CustomerType.EMPLOYEE)).isTrue();
        assertThat(customer.isOfType(CustomerType.AFFILIATE)).isFalse();
    }

    @Test
    void typesAreImmutableAfterConstruction() {
        var mutable = new java.util.HashSet<CustomerType>();
        mutable.add(CustomerType.AFFILIATE);

        var customer = new Customer("c-1", LocalDate.of(2024, 1, 1), mutable);
        mutable.add(CustomerType.EMPLOYEE);

        assertThat(customer.isOfType(CustomerType.EMPLOYEE)).isFalse();
    }

    @Test
    void tenureIsMeasuredFromJoinDate() {
        var customer = new Customer("c-1", LocalDate.of(2024, 1, 1), Set.of());

        assertThat(customer.hasBeenCustomerForMoreThan(2, LocalDate.of(2026, 1, 2))).isTrue();
        assertThat(customer.hasBeenCustomerForMoreThan(2, LocalDate.of(2026, 1, 1))).isFalse();
    }
}