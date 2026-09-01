package io.github.khaledabushamat.discount.billing.domain.policy;

import io.github.khaledabushamat.discount.billing.domain.DiscountEngine;
import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.customer.domain.CustomerType;
import io.github.khaledabushamat.discount.shared.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.github.khaledabushamat.discount.billing.domain.BillFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

public class EndToEndDiscountCalculationTest {

    private DiscountEngine engine;

    @BeforeEach
    void setUp() {
        DiscountRateProvider rates = type -> switch (type) {
            case EMPLOYEE -> BigDecimal.valueOf(30);
            case AFFILIATE -> BigDecimal.valueOf(10);
            case LOYALTY -> BigDecimal.valueOf(5);
        };

        engine = new DiscountEngine(
                List.of(new EmployeeDiscountPolicy(rates),
                        new AffiliateDiscountPolicy(rates),
                        new LoyaltyDiscountPolicy(rates)),
                List.of(new BulkDiscountPolicy()));
    }

    @Nested
    @DisplayName("the worked example from the specification")
    class WorkedExample {

        @Test
        void employeeWithGroceriesAndElectronics() {
            var bill = bill(recentCustomer(CustomerType.EMPLOYEE),
                    electronics("890.00"),
                    groceries("100.00"));

            var result = engine.calculate(bill);

            assertThat(result.grossTotal()).isEqualTo(Money.of("990.00"));
            assertThat(result.percentageDiscount()).isEqualTo(Money.of("267.00"));
            assertThat(result.flatDiscount()).isEqualTo(Money.of("45.00"));
            assertThat(result.netPayable()).isEqualTo(Money.of("678.00"));
        }
    }

    @Nested
    @DisplayName("rule 6 — only one percentage discount")
    class SinglePercentageDiscount {

        @Test
        void employeeAndAffiliateGetsTheHigherRate() {
            var bill = bill(
                    recentCustomer(CustomerType.EMPLOYEE, CustomerType.AFFILIATE),
                    electronics("1000.00"));

            assertThat(engine.calculate(bill).percentageDiscount())
                    .isEqualTo(Money.of("300.00"));
        }

        @Test
        void longStandingAffiliateGetsAffiliateRateNotLoyalty() {
            var bill = bill(
                    customer(LocalDate.of(2015, 1, 1), CustomerType.AFFILIATE),
                    electronics("1000.00"));

            assertThat(engine.calculate(bill).percentageDiscount())
                    .isEqualTo(Money.of("100.00"));
        }

        @Test
        void longStandingCustomerWithNoTypeGetsLoyaltyRate() {
            var bill = bill(
                    customer(LocalDate.of(2015, 1, 1)),
                    electronics("1000.00"));

            assertThat(engine.calculate(bill).percentageDiscount())
                    .isEqualTo(Money.of("50.00"));
        }
    }

    @Nested
    @DisplayName("rule 5 — percentage discounts exclude groceries")
    class GroceryExclusion {

        @Test
        void employeeBuyingOnlyGroceriesGetsNoPercentageDiscount() {
            var bill = bill(recentCustomer(CustomerType.EMPLOYEE),
                    groceries("500.00"));

            var result = engine.calculate(bill);

            assertThat(result.percentageDiscount()).isEqualTo(Money.ZERO);
            assertThat(result.flatDiscount()).isEqualTo(Money.of("25.00"));
            assertThat(result.netPayable()).isEqualTo(Money.of("475.00"));
        }

        @Test
        void bulkDiscountStillCountsGroceriesTowardsTheThreshold() {
            var bill = bill(recentCustomer(),
                    groceries("300.00"),
                    electronics("50.00"));

            assertThat(engine.calculate(bill).flatDiscount())
                    .isEqualTo(Money.of("15.00"));
        }
    }

    @Nested
    @DisplayName("rule 4 — five dollars per complete hundred")
    class BulkDiscount {

        @Test
        void billUnderOneHundredGetsNothing() {
            var bill = bill(recentCustomer(), electronics("99.99"));

            var result = engine.calculate(bill);

            assertThat(result.flatDiscount()).isEqualTo(Money.ZERO);
            assertThat(result.netPayable()).isEqualTo(Money.of("99.99"));
        }

        @Test
        void billAtExactlyOneHundredGetsFive() {
            var bill = bill(recentCustomer(), electronics("100.00"));

            assertThat(engine.calculate(bill).flatDiscount())
                    .isEqualTo(Money.of("5.00"));
        }

        @Test
        void partialHundredsAreIgnored() {
            var bill = bill(recentCustomer(), electronics("199.99"));

            assertThat(engine.calculate(bill).flatDiscount())
                    .isEqualTo(Money.of("5.00"));
        }
    }

    @Nested
    @DisplayName("rule 3 — customer for over two years")
    class LoyaltyBoundary {

        @Test
        void exactlyTwoYearsDoesNotQualify() {
            var bill = bill(customer(BILLED_ON.minusYears(2)),
                    electronics("1000.00"));

            assertThat(engine.calculate(bill).percentageDiscount())
                    .isEqualTo(Money.ZERO);
        }

        @Test
        void twoYearsAndOneDayQualifies() {
            var bill = bill(customer(BILLED_ON.minusYears(2).minusDays(1)),
                    electronics("1000.00"));

            assertThat(engine.calculate(bill).percentageDiscount())
                    .isEqualTo(Money.of("50.00"));
        }
    }

    @Nested
    @DisplayName("combined effects")
    class Combined {

        @Test
        void newCustomerWithSmallBillGetsNothing() {
            var bill = bill(recentCustomer(), electronics("50.00"));

            assertThat(engine.calculate(bill).netPayable())
                    .isEqualTo(Money.of("50.00"));
        }

        @Test
        void discountsNeverExceedTheBill() {
            var bill = bill(recentCustomer(CustomerType.EMPLOYEE),
                    electronics("100.00"));

            var result = engine.calculate(bill);

            assertThat(result.netPayable()).isEqualTo(Money.of("65.00"));
            assertThat(result.netPayable()).isGreaterThanOrEqualTo(Money.ZERO);
        }
    }

}
