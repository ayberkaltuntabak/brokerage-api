package com.brokerage.presentation.dto;

import lombok.Getter;

@Getter
public class SignUpResponseDTO {

  // Getters

  private final String token;
  private final Long customerId;

  public SignUpResponseDTO(String token, Long userId) {
    this.token = token;
    this.customerId = userId;
  }
}