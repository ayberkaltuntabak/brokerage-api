package com.brokerage.presentation.controller;

import com.brokerage.application.service.OrderApplicationService;
import com.brokerage.domain.aggregate.Order;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.domain.valueobject.OrderSide;
import com.brokerage.presentation.dto.CreateOrderCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderApplicationService orderService;

  public OrderController(OrderApplicationService orderService) {
    this.orderService = orderService;
  }

  /**
   * Create a new stock order
   */
  @PostMapping("/create")
  public ResponseEntity<Order> createOrder(@RequestBody CreateOrderCommand command) {
    Order order = orderService.createOrder(
        command.getCustomerId(),
        command.getAssetName(),
        OrderSide.valueOf(command.getOrderSide()),
        command.getSize(),
        new Money(command.getPrice())
                                          );
    return ResponseEntity.ok(order);
  }

  /**
   * Match an existing pending order
   */
  @PostMapping("/match/{orderId}")
  public ResponseEntity<Void> matchOrder(@PathVariable Long orderId) {
    orderService.matchOrder(orderId);
    return ResponseEntity.ok().build();
  }

  /**
   * Cancel a pending order
   */
  @DeleteMapping("/cancel/{orderId}")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok().build();
  }
}