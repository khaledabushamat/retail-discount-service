package io.github.khaledabushamat.discount.billing.domain.policy;

import org.springframework.stereotype.Component;

import io.github.khaledabushamat.discount.billing.domain.Bill;
import io.github.khaledabushamat.discount.billing.domain.FlatDiscountPolicy;
import io.github.khaledabushamat.discount.shared.Money;

@Component
class BulkDiscountPolicy implements FlatDiscountPolicy {

    private static final Money DISCOUNT_PER_HUNDRED = Money.of("5.00");

    @Override
    public boolean appliesTo(Bill bill) {
        return bill.grossTotal().wholeHundreds() > 0;
    }

    @Override
    public Money discountFor(Bill bill) {
        long hundreds = bill.grossTotal().wholeHundreds();
        return DISCOUNT_PER_HUNDRED.multipliedBy((int) hundreds);
    }
}
