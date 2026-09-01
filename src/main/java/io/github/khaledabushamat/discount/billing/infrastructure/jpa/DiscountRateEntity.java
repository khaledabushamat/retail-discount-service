package io.github.khaledabushamat.discount.billing.infrastructure.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "discount_rates")
class DiscountRateEntity {

    @Id
    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    protected DiscountRateEntity() {
    }

    String getType() {
        return type;
    }

    BigDecimal getPercentage() {
        return percentage;
    }
}