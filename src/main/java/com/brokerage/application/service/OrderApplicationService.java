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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class OrderApplicationService {

  private final OrderRepository orderRepository;

  private final CustomerRepository customerRepository;

  private final AssetApplicationService assetApplicationService;

  private final AssetRepository assetRepository;

  public OrderApplicationService(OrderRepository orderRepository,
                                 CustomerRepository customerRepository,
                                 AssetApplicationService assetApplicationService, AssetRepository assetRepository) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.assetApplicationService = assetApplicationService;
    this.assetRepository = assetRepository;
  }

  /**
   * Creates a new stock order for a customer
   */
  @Transactional
  public Order createOrder(Long customerId, String assetName, OrderSide side, int size, Money price) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Calculate total cost
    Money totalCost = price.multiply(size);

    // Retrieve the customer's TRY asset
    Asset customerTryAsset = customer.getAssets().stream()
                                     .filter(asset -> asset.getAssetName().equals("TRY"))
                                     .findFirst()
                                     .orElseThrow(() -> new IllegalArgumentException("No TRY Asset found"));

    Money customerAccountBalance = new Money(BigDecimal.valueOf(customerTryAsset.getUsableSize()));

    // Validate balance for BUY order
    if (OrderSide.BUY.equals(side) && customerAccountBalance.isLessThan(totalCost)) {
      throw new IllegalArgumentException("Insufficient balance for BUY order");
    }

    // Update the TRY asset based on the order side
    if (OrderSide.BUY.equals(side)) {
      customerTryAsset.reserveShares(totalCost.getAmount().intValue());
    } else {
      customerTryAsset.releaseShares(totalCost.getAmount().intValue());
    }

    // Find or create the asset for the given asset name
    Asset assetByCustomerAndAssetName = assetApplicationService.findAssetByCustomerAndAssetName(customerId, assetName);
    if (OrderSide.BUY.equals(side)) {
      if (assetByCustomerAndAssetName != null) {
        assetByCustomerAndAssetName.releaseShares(size);
      } else {
        assetByCustomerAndAssetName = new Asset(customer, assetName, size, size);
      }
    } else if (OrderSide.SELL.equals(side)) {
      if (assetByCustomerAndAssetName == null) {
        throw new IllegalArgumentException("You can't sell an asset that you don't have");
      }
      assetByCustomerAndAssetName.reserveShares(size);
    }

    // Save the updated assets
    assetRepository.save(customerTryAsset);
    assetRepository.save(assetByCustomerAndAssetName);

    // Create and save the new order
    Order order = new Order(customer, assetName, side, size, price, OrderStatus.PENDING);
    return orderRepository.save(order);
  }


  @Transactional
  public void matchOrders(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Retrieve all pending orders for the customer
    List<Order> pendingOrders = orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING);

    for (Order order : pendingOrders) {
      // Find the relevant asset for the order
      Asset customerAsset = assetApplicationService.findAssetByCustomerAndAssetName(customerId, order.getAssetName());
      customerAsset.setSize(customerAsset.getUsableSize());
      // Mark the order as matched
      order.match();
      // Save the updated asset and order
      assetRepository.save(customerAsset);
      orderRepository.save(order);
    }
  }



  /**
   * Cancels a pending order and restores any reserved funds or assets
   */
  @Transactional
  public void cancelOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    Money totalCostOfOrder = order.getPrice().multiply(order.getSize());
    Asset customerTryAsset = order.getCustomer().getAssets()
                                  .stream()
                                  .filter(asset -> asset.getAssetName().equals("TRY"))
                                  .findFirst()
                                  .orElseThrow(() -> new IllegalArgumentException("No TRY Assests"));
    if (OrderSide.SELL.equals(order.getOrderSide())) {
      customerTryAsset.reserveShares(totalCostOfOrder.getAmount().intValue());
    }
    if (OrderSide.BUY.equals(order.getOrderSide())) {
      customerTryAsset.releaseShares(totalCostOfOrder.getAmount().intValue());
    }
    order.cancel();
    assetRepository.save(customerTryAsset); // Save the updated asset
    orderRepository.save(order);
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
