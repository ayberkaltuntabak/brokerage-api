package com.brokerage.presentation.controller;

import com.brokerage.application.service.AssetApplicationService;
import com.brokerage.domain.aggregate.Asset;
import com.brokerage.presentation.dto.ApiResponse;
import com.brokerage.presentation.dto.AssetResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

  private final AssetApplicationService assetApplicationService;

  public AssetController(AssetApplicationService assetApplicationService) {
    this.assetApplicationService = assetApplicationService;
  }

  /**
   * List all assets for a given customer.
   */
  @GetMapping("/{customerId}")
  public ResponseEntity<ApiResponse<List<AssetResponseDTO>>> listAssetsForCustomer(@PathVariable Long customerId) {
    List<Asset> assets = assetApplicationService.listAssetsForCustomer(customerId);

    // Map assets to AssetResponseDTOs
    List<AssetResponseDTO> assetResponseDTOs = assets.stream()
                                                     .map(this::mapToAssetResponseDTO)
                                                     .collect(Collectors.toList());

    return ResponseEntity.ok(ApiResponse.success("Assets retrieved successfully", assetResponseDTOs));
  }

  /**
   * Find a specific asset by customer and asset name.
   */
  @GetMapping("/{customerId}/find")
  public ResponseEntity<ApiResponse<AssetResponseDTO>> findAssetByCustomerAndAssetName(
      @PathVariable Long customerId,
      @RequestParam String assetName) {
    Asset asset = assetApplicationService.findAssetByCustomerAndAssetName(customerId, assetName);
    AssetResponseDTO responseDTO = mapToAssetResponseDTO(asset);

    return ResponseEntity.ok(ApiResponse.success("Asset retrieved successfully", responseDTO));
  }

  // Helper method to map Asset to AssetResponseDTO
  private AssetResponseDTO mapToAssetResponseDTO(Asset asset) {
    return new AssetResponseDTO(
        asset.getId(),
        asset.getAssetName(),
        asset.getSize(),
        asset.getUsableSize()
    );
  }
}