package com.brokerage.presentation.controller;

import com.brokerage.application.service.AssetApplicationService;
import com.brokerage.domain.aggregate.Asset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

  private final AssetApplicationService assetApplicationService;

  public AssetController(AssetApplicationService assetApplicationService) {
    this.assetApplicationService = assetApplicationService;
  }

  /**
   * List all assets for a given customer
   */
  @GetMapping("/{customerId}")
  public ResponseEntity<List<Asset>> listAssetsForCustomer(@PathVariable Long customerId) {
    return ResponseEntity.ok(assetApplicationService.listAssetsForCustomer(customerId));
  }

  /**
   * Retrieve a specific asset by customer and asset name
   */
  @GetMapping("/{customerId}/{assetName}")
  public ResponseEntity<Asset> getAssetByCustomerAndName(@PathVariable Long customerId,
                                                         @PathVariable String assetName) {
    return ResponseEntity.ok(assetApplicationService.findAssetByCustomerAndAssetName(customerId, assetName));
  }
}
