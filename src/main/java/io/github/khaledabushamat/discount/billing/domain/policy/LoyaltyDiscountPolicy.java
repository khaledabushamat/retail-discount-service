package io.github.khaledabushamat.discount.billing.domain.policy;

import io.github.khaledabushamat.discount.billing.domain.Bill;
import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.billing.domain.DiscountType;
import io.github.khaledabushamat.discount.billing.domain.PercentageDiscountPolicy;
import io.github.khaledabushamat.discount.shared.Money;
import org.springframework.stereotype.Component;

@Component
class LoyaltyDiscountPolicy implements PercentageDiscountPolicy {

    private static final int MINIMUM_YEARS = 2;

    private final DiscountRateProvider rates;

    LoyaltyDiscountPolicy(DiscountRateProvider rates) {
        this.rates = rates;
    }

    @Override
    public boolean appliesTo(Bill bill) {
        return bill.customer().yearsAsCustomer(bill.billedOn()) > MINIMUM_YEARS;
    }

    @Override
    public Money discountFor(Bill bill) {
        return bill.nonGroceryTotal().percentage(rates.rateFor(DiscountType.LOYALTY));
    }
}