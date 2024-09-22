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
import jakarta.transaction.Transactional;
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
  @Transactional
  public Order createOrder(Long customerId, String assetName, OrderSide side, int size, int usableSize, Money price) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Calculate total cost
    Money totalCost = price.multiply(size);

    if (OrderSide.BUY.equals(side) && customer.getBalance().isLessThan(totalCost)) {
      throw new IllegalArgumentException("Insufficient balance for BUY order");
    }

    Asset asset = assetRepository.findByCustomerAndAssetName(customer, assetName).orElse(null);
    if(asset == null){
      asset = new Asset(customer, assetName, size, usableSize);
    }
    if (OrderSide.BUY.equals(side)) {
      assetRepository.save(asset);
      asset.reserveShares(size);
    }
    if (OrderSide.SELL.equals(side)) {
      asset.releaseShares(size);
      assetRepository.save(asset);
    }

    // Create and save the order
    Order order = new Order(customer, assetName, side, size, price, OrderStatus.PENDING);
    orderRepository.save(order);

    return order;
  }

  /**
   * Matches a pending order and updates customer balances and assets accordingly
   */
  @Transactional
  public void matchOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Customer customer = order.getCustomer();
    Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName())
                                 .orElse(null); // Create a new asset if not found

    if (OrderSide.BUY.equals(order.getOrderSide())) {
      customer.withdraw(order.getPrice().multiply(order.getSize()));
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
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    Customer customer = order.getCustomer();
    Asset asset = assetRepository.findByCustomerAndAssetName(customer, order.getAssetName())
                                 .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    order.cancel();
    if (OrderSide.BUY.equals(order.getOrderSide())) {
      asset.releaseShares(order.getSize());
    }
    if (OrderSide.SELL.equals(order.getOrderSide())) {
      asset.reserveShares(order.getSize());
    }
    assetRepository.save(asset); // Save the updated asset
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
