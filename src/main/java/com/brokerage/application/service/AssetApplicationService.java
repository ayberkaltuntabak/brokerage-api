package com.brokerage.application.service;

import com.brokerage.domain.aggregate.Asset;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.repository.AssetRepository;
import com.brokerage.domain.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetApplicationService {

  private final AssetRepository assetRepository;

  private final CustomerRepository customerRepository;

  public AssetApplicationService(AssetRepository assetRepository, CustomerRepository customerRepository) {
    this.assetRepository = assetRepository;
    this.customerRepository = customerRepository;
  }

  /**
   * Lists all assets for a given customer.
   */
  public List<Asset> listAssetsForCustomer(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    return assetRepository.findByCustomer(customer);
  }

  /**
   * Finds a specific asset for a customer by asset name.
   */
  public Asset findAssetByCustomerAndAssetName(Long customerId, String assetName) {
    Customer customer = customerRepository.findById(customerId)
                                          .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

    return assetRepository.findByCustomerAndAssetName(customer, assetName)
                          .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
  }
}