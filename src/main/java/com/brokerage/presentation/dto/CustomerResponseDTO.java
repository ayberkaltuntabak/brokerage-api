package com.brokerage.presentation.dto;

import com.brokerage.domain.aggregate.Customer;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class CustomerResponseDTO {

  // Getters

  private final Long customerId;
  private final String username;
  private final BigDecimal balance;
  private final List<AssetResponseDTO> assets;
  private final List<OrderResponseDTO> orders;

  // Constructor
  public CustomerResponseDTO(Long customerId,
                             String username,
                             BigDecimal balance,
                             List<AssetResponseDTO> assets,
                             List<OrderResponseDTO> orders) {
    this.customerId = customerId;
    this.username = username;
    this.balance = balance;
    this.assets = assets;
    this.orders = orders;
  }

  public static CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
    List<AssetResponseDTO> assetSummaries = customer.getAssets().stream()
                                                    .map(asset -> new AssetResponseDTO(
                                                        asset.getId(),
                                                        asset.getAssetName(),
                                                        asset.getSize(),
                                                        asset.getUsableSize()
                                                    ))
                                                    .toList();

    List<OrderResponseDTO> orderSummaries = customer.getOrders().stream()
                                                    .map(OrderResponseDTO::mapToOrderResponseDTO)
                                                    .toList();

    // Create and return CustomerResponseDTO
    return new CustomerResponseDTO(
        customer.getId(),
        customer.getUser().getUsername(),
        customer.getBalance().getAmount(),
        assetSummaries,
        orderSummaries
    );
  }
}