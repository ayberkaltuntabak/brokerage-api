package com.brokerage.domain.aggregate;

import jakarta.persistence.*;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.domain.valueobject.OrderSide;
import com.brokerage.domain.valueobject.OrderStatus;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_order")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Customer customer;

  private String assetName;

  @Enumerated(EnumType.STRING)
  private OrderSide orderSide; // BUY or SELL

  private int size;

  @Embedded
  private Money price; // Price per share in TRY

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private LocalDateTime createDate;

  // Constructors
  public Order() {}

  public Order(Customer customer, String assetName, OrderSide orderSide, int size, Money price, OrderStatus status) {
    this.customer = customer;
    this.assetName = assetName;
    this.orderSide = orderSide;
    this.size = size;
    this.price = price;
    this.status = status;
    this.createDate = LocalDateTime.now();
  }

  // Getters and Setters
  public Long getId() { return id; }

  public Customer getCustomer() { return customer; }

  public String getAssetName() { return assetName; }

  public OrderSide getOrderSide() { return orderSide; }

  public int getSize() { return size; }

  public Money getPrice() { return price; }

  public OrderStatus getStatus() { return status; }

  public LocalDateTime getCreateDate() { return createDate; }

  public void cancel() {
    if (this.status != OrderStatus.PENDING) {
      throw new IllegalArgumentException("Only pending orders can be canceled");
    }
    this.status = OrderStatus.CANCELED;
  }

  public void match() {
    if (this.status != OrderStatus.PENDING) {
      throw new IllegalStateException("Only pending orders can be matched");
    }
    this.status = OrderStatus.MATCHED;
  }
}
