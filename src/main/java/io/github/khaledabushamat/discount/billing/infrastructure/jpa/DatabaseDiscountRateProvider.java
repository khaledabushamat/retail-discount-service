package io.github.khaledabushamat.discount.billing.infrastructure.jpa;

import static java.util.stream.Collectors.toUnmodifiableMap;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.billing.domain.DiscountType;

@Component
class DatabaseDiscountRateProvider implements DiscountRateProvider {

    private final Map<DiscountType, BigDecimal> rates;

    DatabaseDiscountRateProvider(DiscountRateEntityRepository repository) {
        this.rates = repository.findAll().stream()
                .collect(toUnmodifiableMap(r -> DiscountType.valueOf(r.getType()), DiscountRateEntity::getPercentage));
    }

    @Override
    public BigDecimal rateFor(DiscountType type) {
        BigDecimal rate = rates.get(type);
        if (rate == null) {
            throw new IllegalStateException("No discount rate configured for " + type);
        }
        return rate;
    }
}
