package io.github.khaledabushamat.discount.billing.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.khaledabushamat.discount.catalog.domain.Category;
import io.github.khaledabushamat.discount.shared.Money;

class LineItemTest {

    @Test
    void totalMultipliesPriceByQuantity() {
        LineItem item = new LineItem("p-1", Category.NON_GROCERY, Money.of("19.99"), 3);

        assertThat(item.total()).isEqualTo(Money.of("59.97"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveQuantity(int quantity) {
        assertThatThrownBy(() ->
                new LineItem("p-1", Category.NON_GROCERY, Money.of("10.00"), quantity))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void identifiesGroceries() {
        assertThat(new LineItem("p-1", Category.GROCERY, Money.of("1.00"), 1).isGrocery())
                .isTrue();
        assertThat(new LineItem("p-1", Category.NON_GROCERY, Money.of("1.00"), 1).isGrocery())
                .isFalse();
    }
}