package com.brokerage.domain.repository;

import com.brokerage.domain.aggregate.Asset;
import com.brokerage.domain.aggregate.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AssetRepository extends JpaRepository<Asset, Long> {

  // Find an asset by customer and asset name
  Optional<Asset> findByCustomerAndAssetName(Customer customer, String assetName);

  // Find all assets for a customer
  List<Asset> findByCustomer(Customer customer);

}
