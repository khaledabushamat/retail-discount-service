package io.github.khaledabushamat.discount.billing.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface DiscountRateRepository extends JpaRepository<DiscountRateEntity, String> {
}