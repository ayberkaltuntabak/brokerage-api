package com.brokerage.presentation.dto;

import lombok.Getter;

@Getter
public class AssetResponseDTO {

  // Getters and Setters

  private Long assetId;
  private String assetName;
  private int size;
  private int usableSize;

  // Constructor
  public AssetResponseDTO(Long assetId, String assetName, int size, int usableSize) {
    this.assetId = assetId;
    this.assetName = assetName;
    this.size = size;
    this.usableSize = usableSize;
  }

  public void setAssetId(Long assetId) {
    this.assetId = assetId;
  }

  public void setAssetName(String assetName) {
    this.assetName = assetName;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setUsableSize(int usableSize) {
    this.usableSize = usableSize;
  }

}