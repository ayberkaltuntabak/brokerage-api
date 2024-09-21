package com.brokerage.application.service;

import com.brokerage.domain.aggregate.Asset;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.aggregate.Order;
import com.brokerage.domain.repository.AssetRepository;
import com.brokerage.domain.repository.CustomerRepository;
import com.brokerage.domain.repository.OrderRepository;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.domain.valueobject.OrderSide;
import com.brokerage.domain.valueobject.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class OrderApplicationService {

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final AssetRepository assetRepository;

  public OrderApplicationService(OrderRepository orderRepository,
                                 CustomerRepository customerRepository,
                                 AssetRepository assetRepository) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.assetRepository = assetRepository;
  }

  /**
   * Creates a new stock order for a customer
   */
  public Order createOrder(Long customerId, String assetName, OrderSide side, int size, Money price) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Calculate total cost
    Money totalCost = price.multiply(size);

    if (OrderSide.BUY.equals(side) && customer.getBalance().isLessThan(totalCost)) {
      throw new IllegalArgumentException("Insufficient balance for BUY order");
    }

    Order order = new Order(customer, assetName, side, size, price, OrderStatus.PENDING);
    orderRepository.save(order);

    if (side == OrderSide.BUY) {
      customer.withdraw(totalCost);
      customerRepository.save(customer);
    }

    return order;
  }

  /**
   * Matches a pending order and updates customer balances and assets accordingly
   */
  public void matchOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Customer customer = order.getCustomer();

    if (OrderSide.BUY.equals(order.getOrderSide())) {
      Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName())
                                   .orElse(new Asset(customer, order.getAssetName(), 0,
                                                     0)); // Create a new asset if not found
      asset.reserveShares(order.getSize());
      assetRepository.save(asset);
    }

    if (OrderSide.SELL.equals(order.getOrderSide())) {
      Money totalSaleValue = order.getPrice().multiply(order.getSize());
      customer.deposit(totalSaleValue);
      customerRepository.save(customer);
    }

    order.match();
    orderRepository.save(order);
  }

  /**
   * Cancels a pending order and restores any reserved funds or assets
   */
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Customer customer = order.getCustomer();

    if (OrderSide.BUY.equals(order.getOrderSide())) {
      Money totalCost = order.getPrice().multiply(order.getSize());
      customer.deposit(totalCost); // Refund the money to the customer
    }

    if (OrderSide.SELL.equals(order.getOrderSide())) {
      Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName())
                                   .orElseThrow(() -> new IllegalArgumentException("Asset not found"));

      asset.releaseShares(order.getSize());
      assetRepository.save(asset); // Save the updated asset
    }

    // Mark the order as canceled
    order.cancel();

    // Save the updated order and customer
    orderRepository.save(order);
    customerRepository.save(customer);
  }

  // Method to list orders with optional filters
  public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate, OrderStatus status) {
    // Validate if the customer exists
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Fetch orders based on filters
    if (isNull(status)) {
      return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate);
    }
    return orderRepository.findByCustomerIdAndCreateDateBetweenAndStatus(customerId, startDate, endDate, status);
  }
}
