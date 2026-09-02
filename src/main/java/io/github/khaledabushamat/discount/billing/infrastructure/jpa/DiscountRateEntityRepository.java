package io.github.khaledabushamat.discount.billing.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface DiscountRateEntityRepository extends JpaRepository<DiscountRateEntity, String> {
}