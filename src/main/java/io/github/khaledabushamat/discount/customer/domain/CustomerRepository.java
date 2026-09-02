package io.github.khaledabushamat.discount.customer.domain;

import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByExternalId(String externalId);
}