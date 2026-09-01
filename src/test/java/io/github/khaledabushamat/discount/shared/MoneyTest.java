package io.github.khaledabushamat.discount.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

class MoneyTest {

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        void roundsToTwoDecimalPlacesHalfUp() {
            assertThat(Money.of("10.005")).isEqualTo(Money.of("10.01"));
            assertThat(Money.of("10.004")).isEqualTo(Money.of("10.00"));
        }

        @Test
        void rejectsNegativeAmounts() {
            assertThatThrownBy(() -> Money.of("-0.01"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsNull() {
            assertThatThrownBy(() -> Money.of((BigDecimal) null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("arithmetic")
    class Arithmetic {

        @Test
        void addsAmounts() {
            assertThat(Money.of("100.50").plus(Money.of("0.50")))
                    .isEqualTo(Money.of("101.00"));
        }

        @Test
        void subtractsAmounts() {
            assertThat(Money.of("100.00").minus(Money.of("30.00")))
                    .isEqualTo(Money.of("70.00"));
        }

        @Test
        void floorsAtZeroInsteadOfGoingNegative() {
            assertThat(Money.of("10.00").minus(Money.of("30.00")))
                    .isEqualTo(Money.ZERO);
        }

        @Test
        void multipliesByQuantity() {
            assertThat(Money.of("12.50").multipliedBy(4))
                    .isEqualTo(Money.of("50.00"));
        }
    }

    @Nested
    @DisplayName("percentage")
    class Percentage {

        @ParameterizedTest
        @CsvSource({
                "890.00, 30, 267.00",
                "100.00, 10, 10.00",
                "100.00,  5,  5.00",
                "  0.00, 30,   0.00"
        })
        void appliesPercentage(String amount, int percent, String expected) {
            assertThat(Money.of(amount).percentage(BigDecimal.valueOf(percent)))
                    .isEqualTo(Money.of(expected));
        }

        @Test
        void roundsHalfUpAtTheBoundary() {
            assertThat(Money.of("33.43").percentage(BigDecimal.valueOf(30)))
                    .isEqualTo(Money.of("10.03"));
        }
    }

    @Nested
    @DisplayName("wholeHundreds")
    class WholeHundreds {

        @ParameterizedTest
        @CsvSource({
                "990.00, 9",
                "100.00, 1",
                " 99.99, 0",
                "  0.00, 0",
                "199.99, 1"
        })
        void truncatesTowardsZero(String amount, long expected) {
            assertThat(Money.of(amount).wholeHundreds()).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("equality")
    class Equality {

        @Test
        void treatsDifferentScalesAsEqual() {
            assertThat(Money.of("5.0")).isEqualTo(Money.of("5.00"));
        }

        @Test
        void equalValuesShareHashCode() {
            assertThat(Money.of("5.0")).hasSameHashCodeAs(Money.of("5.00"));
        }
    }
}