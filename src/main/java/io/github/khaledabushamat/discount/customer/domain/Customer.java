package io.github.khaledabushamat.discount.customer.domain;

import java.time.LocalDate;
import java.util.Set;

public record Customer(String externalId, LocalDate joinedAt, Set<CustomerType> types) {
    public Customer {
        types = types == null ? Set.of() : Set.copyOf(types);
    }

    public boolean isOfType(CustomerType type) {
        return types.contains(type);
    }

    public boolean hasBeenCustomerForMoreThan(int years, LocalDate asOf) {
        return joinedAt.plusYears(years).isBefore(asOf);
    }
}
