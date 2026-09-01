package io.github.khaledabushamat.discount.billing.domain;

import io.github.khaledabushamat.discount.shared.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.electronics;
import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.recentCustomer;
import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.bill;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountEngineTest {

    private final Bill bill = bill(
            recentCustomer(),
            electronics("100.00"));

    @Test
    void selectsTheLargestApplicablePercentageDiscount() {
        PercentageDiscountPolicy thirty = percentagePolicy(true, Money.of("30.00"));
        PercentageDiscountPolicy ten = percentagePolicy(true, Money.of("10.00"));

        var engine = new DiscountEngine(List.of(thirty, ten), List.of());

        var result = engine.calculate(bill);

        assertThat(result.percentageDiscount()).isEqualTo(Money.of("30.00"));
    }

    @Test
    void ignoresPoliciesThatDoNotApply() {
        PercentageDiscountPolicy applicable = percentagePolicy(true, Money.of("10.00"));
        PercentageDiscountPolicy notApplicable = percentagePolicy(false, null);

        var engine = new DiscountEngine(List.of(notApplicable, applicable), List.of());

        var result = engine.calculate(bill);

        assertThat(result.percentageDiscount()).isEqualTo(Money.of("10.00"));
        verify(notApplicable, never()).discountFor(any());
    }

    @Test
    void sumsAllApplicableFlatDiscounts() {
        FlatDiscountPolicy five = flatPolicy(true, Money.of("5.00"));
        FlatDiscountPolicy three = flatPolicy(true, Money.of("3.00"));

        var engine = new DiscountEngine(List.of(), List.of(five, three));

        var result = engine.calculate(bill);

        assertThat(result.flatDiscount()).isEqualTo(Money.of("8.00"));
    }

    @Test
    void returnsZeroDiscountWhenNothingApplies() {
        var engine = new DiscountEngine(
                List.of(percentagePolicy(false, null)),
                List.of(flatPolicy(false, null)));

        var result = engine.calculate(bill);

        assertThat(result.percentageDiscount()).isEqualTo(Money.ZERO);
        assertThat(result.flatDiscount()).isEqualTo(Money.ZERO);
        assertThat(result.netPayable()).isEqualTo(bill.grossTotal());
    }

    @Test
    void handlesEmptyPolicyLists() {
        var engine = new DiscountEngine(List.of(), List.of());

        assertThat(engine.calculate(bill).netPayable()).isEqualTo(bill.grossTotal());
    }

    private PercentageDiscountPolicy percentagePolicy(boolean applies, Money amount) {
        PercentageDiscountPolicy policy = mock(PercentageDiscountPolicy.class);
        when(policy.appliesTo(any())).thenReturn(applies);
        if (applies) {
            when(policy.discountFor(any())).thenReturn(amount);
        }
        return policy;
    }

    private FlatDiscountPolicy flatPolicy(boolean applies, Money amount) {
        FlatDiscountPolicy policy = mock(FlatDiscountPolicy.class);
        when(policy.appliesTo(any())).thenReturn(applies);
        if (applies) {
            when(policy.discountFor(any())).thenReturn(amount);
        }
        return policy;
    }
}
