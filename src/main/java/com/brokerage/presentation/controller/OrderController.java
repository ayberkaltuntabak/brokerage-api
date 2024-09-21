package com.brokerage.presentation.controller;

import com.brokerage.application.service.OrderApplicationService;
import com.brokerage.domain.aggregate.Order;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.domain.valueobject.OrderSide;
import com.brokerage.domain.valueobject.OrderStatus;
import com.brokerage.presentation.dto.ApiResponse;
import com.brokerage.presentation.dto.CreateOrderCommand;
import com.brokerage.presentation.dto.OrderResponseDTO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.brokerage.presentation.dto.OrderResponseDTO.mapToOrderResponseDTO;

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
  public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@RequestBody CreateOrderCommand command) {
    Order order = orderService.createOrder(
        command.getCustomerId(),
        command.getAssetName(),
        OrderSide.valueOf(command.getOrderSide()),
        command.getSize(),
        new Money(command.getPrice())
                                          );
    OrderResponseDTO responseDTO = mapToOrderResponseDTO(order);
    return ResponseEntity.ok(ApiResponse.success("Order created successfully", responseDTO));
  }

  /**
   * Match an existing pending order
   */
  @PostMapping("/match/{orderId}")
  public ResponseEntity<ApiResponse<Void>> matchOrder(@PathVariable Long orderId) {
    orderService.matchOrder(orderId);
    return ResponseEntity.ok(ApiResponse.success("Order matched successfully", null));
  }

  /**
   * Cancel a pending order
   */
  @DeleteMapping("/cancel/{orderId}")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long orderId) {
    orderService.cancelOrder(orderId);
    return ResponseEntity.ok(ApiResponse.success("Order canceled successfully", null));
  }

  /**
   * List orders for a given customer and date range with an optional status filter
   */
  @GetMapping("/{customerId}/orders")
  public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> listOrders(
      @PathVariable Long customerId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
      @RequestParam(required = false) OrderStatus status) {

    // Fetch orders
    List<Order> orders = orderService.listOrders(customerId, startDate, endDate, status);

    // Map domain entities to response DTOs
    List<OrderResponseDTO> orderResponseDTOs = orders.stream()
                                                     .map(OrderResponseDTO::mapToOrderResponseDTO)
                                                     .toList();

    return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orderResponseDTOs));
  }


}
