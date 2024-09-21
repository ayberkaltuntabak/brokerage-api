package com.brokerage.presentation.dto;

import com.brokerage.domain.aggregate.Order;
import com.brokerage.domain.valueobject.OrderSide;
import com.brokerage.domain.valueobject.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class OrderResponseDTO {

  // Getters and Setters

  private final Long orderId;
  private final Long customerId;
  private final String assetName;
  private final OrderSide orderSide;
  private final int size;
  private final BigDecimal price;
  private final OrderStatus status;
  private final LocalDateTime createDate;

  // Constructor
  public OrderResponseDTO(Long orderId, Long customerId, String assetName, OrderSide orderSide, int size,
                          BigDecimal price, OrderStatus status, LocalDateTime createDate) {
    this.orderId = orderId;
    this.customerId = customerId;
    this.assetName = assetName;
    this.orderSide = orderSide;
    this.size = size;
    this.price = price;
    this.status = status;
    this.createDate = createDate;
  }
  public static OrderResponseDTO mapToOrderResponseDTO(Order order) {
    return new OrderResponseDTO(
        order.getId(),
        order.getCustomer().getId(),
        order.getAssetName(),
        order.getOrderSide(),
        order.getSize(),
        order.getPrice().getAmount(),
        order.getStatus(),
        order.getCreateDate()
    );
  }
}