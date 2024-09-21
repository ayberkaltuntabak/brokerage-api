package com.brokerage.domain.repository;

import com.brokerage.domain.aggregate.Order;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.valueobject.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

  // Find all orders for a customer
  List<Order> findByCustomer(Customer customer);

  // Find all orders by customer and status (e.g., PENDING, MATCHED)
  List<Order> findByCustomerAndStatus(Customer customer, OrderStatus status);
}