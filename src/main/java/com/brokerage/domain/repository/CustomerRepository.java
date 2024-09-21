package com.brokerage.domain.repository;

import com.brokerage.domain.aggregate.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  // Optional method for finding a customer by ID
  Optional<Customer> findById(Long id);
}
