package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.shared.Money;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DiscountEngine {

    private final List<PercentageDiscountPolicy> percentagePolicies;
    private final List<FlatDiscountPolicy> flatPolicies;

    public DiscountEngine(List<PercentageDiscountPolicy> percentagePolicies,
                          List<FlatDiscountPolicy> flatPolicies) {
        this.percentagePolicies = percentagePolicies;
        this.flatPolicies = flatPolicies;
    }

    public DiscountBreakdown calculate(Bill bill) {
        Money percentageDiscount = percentagePolicies.stream()
                .filter(policy -> policy.appliesTo(bill))
                .map(policy -> policy.discountFor(bill))
                .max(Comparator.naturalOrder())
                .orElse(Money.ZERO);

        Money flatDiscount = flatPolicies.stream()
                .filter(policy -> policy.appliesTo(bill))
                .map(policy -> policy.discountFor(bill))
                .reduce(Money.ZERO, Money::plus);

        return new DiscountBreakdown(
                bill.grossTotal(),
                percentageDiscount,
                flatDiscount);
    }
}