package com.brokerage.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;


@Embeddable
public class Money {

  @Getter
  private BigDecimal amount;

  private final String currency = "TRY"; // Default currency is TRY

  public Money() {
  }

  public Money(BigDecimal amount) {
    this.amount = amount;
  }

  public boolean isLessThan(Money other) {
    return this.amount.compareTo(other.amount) < 0;
  }

  public Money multiply(int quantity) {
    return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
  }

  public Money add(Money other) {
    return new Money(this.amount.add(other.amount));
  }

  public Money subtract(Money other) {
    return new Money(this.amount.subtract(other.amount));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Money money = (Money) o;
    return Objects.equals(amount, money.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, currency);
  }
}
