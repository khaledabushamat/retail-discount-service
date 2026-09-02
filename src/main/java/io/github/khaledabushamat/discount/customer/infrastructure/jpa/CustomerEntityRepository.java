package io.github.khaledabushamat.discount.customer.infrastructure.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByExternalId(String externalId);
}
