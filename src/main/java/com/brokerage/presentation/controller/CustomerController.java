package com.brokerage.presentation.controller;

import com.brokerage.application.service.CustomerApplicationService;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

  private final CustomerApplicationService customerApplicationService;

  private final JwtTokenProvider jwtTokenProvider;

  public CustomerController(CustomerApplicationService customerApplicationService, JwtTokenProvider jwtTokenProvider) {
    this.customerApplicationService = customerApplicationService;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Deposit money into a customer's account
   */
  @PostMapping("/{customerId}/deposit")
  public ResponseEntity<Void> deposit(@PathVariable Long customerId, @RequestParam BigDecimal amount, @RequestHeader(
      HttpHeaders.AUTHORIZATION) String authorizationHeader) {
    customerApplicationService.deposit(customerId, new Money(amount),
                                       jwtTokenProvider.getTokenFromHeader(authorizationHeader));
    return ResponseEntity.ok().build();
  }

  /**
   * Withdraw money from a customer's account
   */
  @PostMapping("/{customerId}/withdraw")
  public ResponseEntity<Void> withdraw(@PathVariable Long customerId, @RequestParam BigDecimal amount,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
    customerApplicationService.withdraw(customerId, new Money(amount),jwtTokenProvider.getTokenFromHeader(authorizationHeader));
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieve a customer by ID
   */
  @GetMapping("/{customerId}")
  public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId,
                                                  @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                  String authorizationHeader) {
    return customerApplicationService.findCustomerById(customerId,jwtTokenProvider.getTokenFromHeader(authorizationHeader))
                                     .map(ResponseEntity::ok)
                                     .orElse(ResponseEntity.notFound().build());
  }
}
