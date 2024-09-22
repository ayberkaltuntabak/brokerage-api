package com.brokerage.application.service;

import com.brokerage.domain.aggregate.Asset;
import com.brokerage.domain.aggregate.Customer;
import com.brokerage.domain.repository.AssetRepository;
import com.brokerage.domain.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class AssetApplicationService {

  private final AssetRepository assetRepository;

  private final CustomerRepository customerRepository;

  public AssetApplicationService(AssetRepository assetRepository, CustomerRepository customerRepository) {
    this.assetRepository = assetRepository;
    this.customerRepository = customerRepository;
  }

  public Asset createAsset(Long customerId, String assetName, Integer size, Integer usableSize) {
    Customer byId = customerRepository.findById(customerId).orElseThrow(() -> new IllegalArgumentException("Customer "
                                                                                                           + "is not "
                                                                                                           + "found"));
    if (size > usableSize) {
      throw new IllegalArgumentException("Size cant bigger than usableSize");
    }
    Asset asset = assetRepository.findByCustomerAndAssetName(byId, assetName).orElse(null);
    if (!isNull(asset)) {
      throw new IllegalArgumentException("Asset already exists for customer");
    }
    Asset createdAsset = new Asset(byId, assetName, size, usableSize);
    assetRepository.save(createdAsset);
    return createdAsset;
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
                          .orElse(null);
  }
}