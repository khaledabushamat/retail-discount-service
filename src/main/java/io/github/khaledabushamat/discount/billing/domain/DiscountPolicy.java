package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.shared.Money;

public interface DiscountPolicy {

    boolean appliesTo(Bill bill);

    Money discountFor(Bill bill);
}
