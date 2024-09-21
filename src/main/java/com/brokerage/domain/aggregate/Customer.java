package com.brokerage.domain.aggregate;

import com.brokerage.domain.entity.User;
import jakarta.persistence.*;
import com.brokerage.domain.valueobject.Money;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock_customer")
public class Customer {


  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @Setter
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @Setter// Foreign key to User
  @Getter
  private User user;  // Link to User entity

  @Getter
  @Setter
  @Embedded
  private Money balance; // Value Object to represent TRY balance

  @OneToMany(mappedBy = "customer")
  private List<Asset> assets;

  @OneToMany(mappedBy = "customer")
  private List<Order> orders;

  public Customer() {}

  public Customer(String name, Money balance) {
    this.name = name;
    this.balance = balance;
  }

  public void deposit(Money amount) {
    this.balance = this.balance.add(amount);
  }

  public void withdraw(Money amount) {
    if (this.balance.getAmount().compareTo(amount.getAmount()) < 0) {
      throw new IllegalArgumentException("Insufficient balance");
    }
    this.balance = this.balance.subtract(amount);
  }
}