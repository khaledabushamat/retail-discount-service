package io.github.khaledabushamat.discount.customer.infrastructure.jpa;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerRepository;

@Component
class JpaCustomerRepository implements CustomerRepository {

    private final CustomerEntityRepository repository;

    JpaCustomerRepository(CustomerEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findByExternalId(String externalId) {
        return repository.findByExternalId(externalId).map(this::toDomain);
    }

    private Customer toDomain(CustomerEntity entity) {
        return new Customer(entity.getExternalId(), entity.getJoinedAt(), entity.getTypes());
    }
}
