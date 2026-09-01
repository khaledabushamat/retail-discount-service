package io.github.khaledabushamat.discount.customer.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public record Customer(
        String externalId,
        LocalDate joinedAt,
        Set<CustomerType> types
) {
    public Customer {
        types = types == null ? Set.of() : Set.copyOf(types);
    }

    public boolean isOfType(CustomerType type) {
        return types.contains(type);
    }

    public long yearsAsCustomer(LocalDate asOf) {
        return ChronoUnit.YEARS.between(joinedAt, asOf);
    }
}