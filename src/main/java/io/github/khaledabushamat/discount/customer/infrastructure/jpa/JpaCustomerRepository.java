package io.github.khaledabushamat.discount.customer.infrastructure.jpa;

import io.github.khaledabushamat.discount.customer.domain.Customer;
import io.github.khaledabushamat.discount.customer.domain.CustomerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        return new Customer(
                entity.getExternalId(),
                entity.getJoinedAt(),
                entity.getTypes());
    }
}