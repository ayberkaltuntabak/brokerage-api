package com.brokerage.presentation.controller;

import com.brokerage.application.service.CustomerApplicationService;
import com.brokerage.domain.entity.User;
import com.brokerage.domain.repository.UserRepository;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import com.brokerage.presentation.dto.ApiResponse;
import com.brokerage.presentation.dto.LoginResponseDTO;
import com.brokerage.presentation.dto.SignUpResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CustomerApplicationService customerApplicationService;

  public AuthController(AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        CustomerApplicationService customerApplicationService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.customerApplicationService = customerApplicationService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> authenticateUser(@RequestParam String username,
                                                                        @RequestParam String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );

    String token = jwtTokenProvider.generateToken(authentication);
    LoginResponseDTO response = new LoginResponseDTO(token);
    return ResponseEntity.ok(ApiResponse.success("Login successful", response));
  }

  @PostMapping("/signup/customer")
  public ResponseEntity<ApiResponse<SignUpResponseDTO>> registerCustomer(@RequestParam String username,
                                                                         @RequestParam String password,
                                                                         @RequestParam Integer initialBalance,
                                                                         @RequestParam String name) {
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.badRequest().body(ApiResponse.failure("Username is already taken."));
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password)); // Hash the password
    user.setRole("ROLE_CUSTOMER");

    User savedUser = userRepository.save(user);

    // Authenticate and generate a token
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );
    String token = jwtTokenProvider.generateToken(authentication);

    // Create the customer using the CustomerApplicationService
    customerApplicationService.createCustomer(savedUser.getId(), name, initialBalance);

    // Create response DTO
    SignUpResponseDTO response = new SignUpResponseDTO(token, savedUser.getId());
    return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", response));
  }

  @PostMapping("/signup/admin")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> registerAdmin(@RequestParam String username,
                                                                     @RequestParam String password) {
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.badRequest().body(ApiResponse.failure("Username is already taken."));
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password)); // Hash the password
    user.setRole("ROLE_ADMIN");

    userRepository.save(user);

    // Authenticate and generate a token
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );
    String token = jwtTokenProvider.generateToken(authentication);

    // Create response DTO
    LoginResponseDTO response = new LoginResponseDTO(token);
    return ResponseEntity.ok(ApiResponse.success("Admin registered successfully", response));
  }
}