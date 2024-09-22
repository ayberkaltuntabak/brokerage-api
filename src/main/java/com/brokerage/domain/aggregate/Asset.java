package com.brokerage.domain.aggregate;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name="stock_asset")
public class Asset {


  @Getter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @Getter
  private String assetName;

  @Getter
  private int size; // Total shares owned

  @Getter
  private int usableSize; // Available for trading

  public Asset() {}

  public Asset(Customer customer, String assetName, int size, int usableSize) {
    this.customer = customer;
    this.assetName = assetName;
    this.size = size;
    this.usableSize = usableSize;
  }

  public void reserveShares(int shares) {
    if (shares > usableSize) {
      throw new IllegalArgumentException("Not enough usable shares");
    }
    this.usableSize -= shares;
  }

  public void releaseShares(int shares) {
    this.usableSize += shares;
  }

  public void setSize(int newSize) {
    this.size = newSize;
  }

  public void setUsableSize(int newSize) {
    this.usableSize = newSize;
  }
}
