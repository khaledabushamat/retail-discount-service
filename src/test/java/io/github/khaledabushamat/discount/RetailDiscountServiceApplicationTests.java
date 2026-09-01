package io.github.khaledabushamat.discount;

import io.github.khaledabushamat.discount.billing.domain.DiscountRateProvider;
import io.github.khaledabushamat.discount.billing.domain.DiscountType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RetailDiscountServiceApplicationTests {

    @Autowired
    private DiscountRateProvider rateProvider;

    @Test
    void loadsDiscountRatesFromTheDatabase() {
        assertThat(rateProvider.rateFor(DiscountType.EMPLOYEE))
                .isEqualByComparingTo("30.00");
    }

}
