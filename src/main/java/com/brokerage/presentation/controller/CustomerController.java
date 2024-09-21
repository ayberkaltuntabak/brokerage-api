package com.brokerage.presentation.controller;

import com.brokerage.application.service.CustomerApplicationService;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import com.brokerage.presentation.dto.ApiResponse;
import com.brokerage.presentation.dto.CustomerResponseDTO;
import java.math.BigDecimal;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static com.brokerage.presentation.dto.CustomerResponseDTO.mapToCustomerResponseDTO;

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
  public ResponseEntity<ApiResponse<Void>> deposit(@PathVariable Long customerId, @RequestParam BigDecimal amount,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                   String authorizationHeader) {
    customerApplicationService.deposit(customerId, new Money(amount),
                                       jwtTokenProvider.getTokenFromHeader(authorizationHeader));
    return ResponseEntity.ok(ApiResponse.success("Deposit successful", null));
  }

  /**
   * Withdraw money from a customer's account
   */
  @PostMapping("/{customerId}/withdraw")
  public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable Long customerId, @RequestParam BigDecimal amount,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                                                    String authorizationHeader) {
    customerApplicationService.withdraw(customerId, new Money(amount),
                                        jwtTokenProvider.getTokenFromHeader(authorizationHeader));
    return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", null));
  }

  /**
   * Retrieve a customer by ID
   */
  @GetMapping("/{customerId}")
  public ResponseEntity<ApiResponse<CustomerResponseDTO>> getCustomerById(
      @PathVariable Long customerId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

    return customerApplicationService.findCustomerById(customerId,
                                                       jwtTokenProvider.getTokenFromHeader(authorizationHeader))
                                     .map(customer -> ResponseEntity.ok(
                                         ApiResponse.success("Customer retrieved successfully",
                                                             mapToCustomerResponseDTO(customer))))
                                     .orElse(ResponseEntity.ok(ApiResponse.failure("Customer not found")));
  }
}