package io.github.khaledabushamat.discount.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    public static final Money ZERO = Money.of(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(SCALE, ROUNDING);
    }

    public static Money of(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must not be negative: " + amount);
        }
        return new Money(amount);
    }

    public static Money of(String amount) {
        return of(new BigDecimal(amount));
    }

    public Money plus(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money minus(Money other) {
        BigDecimal result = this.amount.subtract(other.amount);
        return result.signum() < 0 ? ZERO : new Money(result);
    }

    public Money multipliedBy(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public Money percentage(BigDecimal percent) {
        return new Money(this.amount.multiply(percent).divide(BigDecimal.valueOf(100), SCALE, ROUNDING));
    }

    public long wholeHundreds() {
        return this.amount.divideToIntegralValue(BigDecimal.valueOf(100)).longValueExact();
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    @Override
    public int compareTo(Money other) {
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Money other && this.amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
