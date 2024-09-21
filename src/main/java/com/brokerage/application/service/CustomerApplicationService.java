package com.brokerage.application.service;

import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.entity.User;
import com.brokerage.domain.repository.CustomerRepository;
import com.brokerage.domain.repository.UserRepository;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import java.math.BigDecimal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerApplicationService {

  private final CustomerRepository customerRepository;

  private final UserRepository userRepository;

  private final JwtTokenProvider jwtTokenProvider;

  private final String CUSTOMER_ROLE = "ROLE_CUSTOMER";

  public CustomerApplicationService(CustomerRepository customerRepository, UserRepository userRepository,
                                    JwtTokenProvider jwtTokenProvider) {
    this.customerRepository = customerRepository;
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Create a new customer for a given user.
   */
  public Customer createCustomer(Long userId, String customerName, BigDecimal initialBalance) {
    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Customer customer = new Customer();
    customer.setName(customerName);
    customer.setBalance(new Money(initialBalance));
    customer.setUser(user); // Associate customer with user

    return customerRepository.save(customer);
  }

  /**
   * Deposits money into the customer's account.
   */
  public void deposit(Long customerId, Money amount, String token) {

    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    customer.deposit(amount);
    customerRepository.save(customer);
  }

  private void isCustomerAuthorizedForOperation(String token, Customer customer) {
    String usernameFromJwtToken = jwtTokenProvider.getUsernameFromJwtToken(token);

    User userFromToken = userRepository.findByUsername(usernameFromJwtToken)
                                       .orElseThrow(
                                           () -> new IllegalArgumentException("Invalid token: User not found"));

    User userFromCustomerTable = customer.getUser();

    if (CUSTOMER_ROLE.equals(userFromToken.getRole()) && !userFromCustomerTable.getId().equals(userFromToken.getId())) {
      throw new AccessDeniedException("You are not authorized to perform this operation on this customer's data.");
    }
  }

  /**
   * Withdraws money from the customer's account.
   */
  public void withdraw(Long customerId, Money amount, String token) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    isCustomerAuthorizedForOperation(token, customer);

    if (customer.getBalance().isLessThan(amount)) {
      throw new IllegalArgumentException("Insufficient funds");
    }

    customer.withdraw(amount);
    customerRepository.save(customer);
  }

  /**
   * Finds a customer by ID.
   */
  public Optional<Customer> findCustomerById(Long customerId, String tokenFromHeader) {
    String usernameFromJwtToken = jwtTokenProvider.getUsernameFromJwtToken(tokenFromHeader);

    User userFromToken = userRepository.findByUsername(usernameFromJwtToken)
                                       .orElseThrow(
                                           () -> new IllegalArgumentException("Invalid token: User not found"));
    if (CUSTOMER_ROLE.equals(userFromToken.getRole())) {
      throw new AccessDeniedException("You are not authorized to perform this operation on this customer's data.");
    }
    Optional<Customer> byId = customerRepository.findById(customerId);
    return byId;
  }
}