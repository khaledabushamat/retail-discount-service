package io.github.khaledabushamat.discount.billing.domain.policy;

import io.github.khaledabushamat.discount.billing.domain.Bill;
import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.billing.domain.DiscountType;
import io.github.khaledabushamat.discount.billing.domain.PercentageDiscountPolicy;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import io.github.khaledabushamat.discount.shared.Money;
import org.springframework.stereotype.Component;

@Component
class AffiliateDiscountPolicy implements PercentageDiscountPolicy {

    private final DiscountRateProvider rates;

    AffiliateDiscountPolicy(DiscountRateProvider rates) {
        this.rates = rates;
    }

    @Override
    public boolean appliesTo(Bill bill) {
        return bill.customer().isOfType(CustomerType.AFFILIATE);
    }

    @Override
    public Money discountFor(Bill bill) {
        return bill.nonGroceryTotal().percentage(rates.rateFor(DiscountType.AFFILIATE));
    }
}