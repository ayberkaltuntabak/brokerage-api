package com.brokerage.presentation.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderCommand {


  private Long customerId;
  private String assetName;
  private String orderSide; // BUY or SELL
  private int size;
  private BigDecimal price;
}