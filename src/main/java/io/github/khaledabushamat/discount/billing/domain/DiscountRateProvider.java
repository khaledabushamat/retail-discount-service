package io.github.khaledabushamat.discount.billing.domain;

import java.math.BigDecimal;

public interface DiscountRateProvider {

    BigDecimal rateFor(DiscountType type);

}