package com.brokerage.application.service;

import com.brokerage.domain.aggregate.Asset;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.entity.User;
import com.brokerage.domain.repository.AssetRepository;
import com.brokerage.domain.repository.CustomerRepository;
import com.brokerage.domain.repository.UserRepository;
import com.brokerage.domain.valueobject.Money;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class CustomerApplicationService {

  private final CustomerRepository customerRepository;

  private final UserRepository userRepository;

  private final JwtTokenProvider jwtTokenProvider;

  private final AssetApplicationService assetApplicationService;

  private final String CUSTOMER_ROLE = "ROLE_CUSTOMER";

  private AssetRepository assetRepository;

  public CustomerApplicationService(CustomerRepository customerRepository, UserRepository userRepository,
                                    JwtTokenProvider jwtTokenProvider,
                                    AssetApplicationService assetApplicationService, AssetRepository assetRepository) {
    this.customerRepository = customerRepository;
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.assetApplicationService = assetApplicationService;
    this.assetRepository = assetRepository;
  }

  /**
   * Create a new customer for a given user.
   */
  public Customer createCustomer(Long userId, String customerName, Integer initialBalance) {
    User user = userRepository.findById(userId)
                              .orElseThrow(() -> new IllegalArgumentException("User not found"));

    Customer customer = new Customer();
    customer.setName(customerName);
    customer.setUser(user); // Associate customer with user
    customerRepository.save(customer);
    assetApplicationService.createAsset(customer.getId(), "TRY", initialBalance, initialBalance);
    return customer;
  }

  /**
   * Deposits money into the customer's account.
   */
  public void deposit(Long customerId, Money amount, String token) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    // Check if the user is authorized to perform the operation
    isCustomerAuthorizedForOperation(token, customer);

    Asset customerTryAsset = customer.getAssets()
                                     .stream()
                                     .filter(asset -> asset.getAssetName().equals("TRY"))
                                     .findFirst()
                                     .orElseThrow(() -> new IllegalArgumentException("No TRY Asset found for the customer"));

    int moneyAmountToBeDeposited = amount.getAmount().intValue();
    int currentSize = customerTryAsset.getSize();
    int currentUsableSize = customerTryAsset.getUsableSize();

    // Update the asset size and usable size
    int newSize = currentSize + moneyAmountToBeDeposited;
    customerTryAsset.setSize(newSize);
    customerTryAsset.setUsableSize(currentUsableSize + moneyAmountToBeDeposited);

    // Save the updated asset details
    assetRepository.save(customerTryAsset);
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
    Asset customerTryAsset =
        customer.getAssets()
                .stream()
                .filter(asset -> asset.getAssetName().equals("TRY"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TRY Assests"));
    int moneyAmountToBeTaken = amount.getAmount().intValue();
    int tryAssetSize = customerTryAsset.getUsableSize();
    if (moneyAmountToBeTaken >= tryAssetSize) {
      throw new IllegalArgumentException("You cant withdraw more than your usable size you have");
    }
    int newSize = tryAssetSize - moneyAmountToBeTaken;
    customerTryAsset.setSize(newSize);
    int tryUsableSize = customerTryAsset.getUsableSize();
    if (newSize <= tryUsableSize) {
      customerTryAsset.setUsableSize(newSize);
    }
    assetRepository.save(customerTryAsset);
    isCustomerAuthorizedForOperation(token, customer);
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