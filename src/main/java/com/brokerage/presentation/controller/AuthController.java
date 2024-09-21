package com.brokerage.presentation.controller;

import com.brokerage.application.service.CustomerApplicationService;
import com.brokerage.domain.entity.User;
import com.brokerage.domain.repository.UserRepository;
import com.brokerage.infrastructure.security.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
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
                        PasswordEncoder passwordEncoder, CustomerApplicationService customerApplicationService) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.customerApplicationService = customerApplicationService;
  }

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestParam String username, @RequestParam String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );
    String token = jwtTokenProvider.generateToken(authentication);
    Map<String, String> response = new HashMap<>();
    response.put("token", token);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/signup/customer")
  public ResponseEntity<?> registerCustomer(@RequestParam String username, @RequestParam String password,
                                            @RequestParam BigDecimal initialBalance, @RequestParam String name) {
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.badRequest().body("Username is already taken.");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password)); // Hash the password

    user.setRole("ROLE_CUSTOMER");

    User customer = userRepository.save(user);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );
    String token = jwtTokenProvider.generateToken(authentication);

    Map<String, String> response = new HashMap<>();
    customerApplicationService.createCustomer(customer.getId(),name,initialBalance);
    response.put("token", token);
    response.put("customerId", String.valueOf(customer.getId()));
    return ResponseEntity.ok(response);
  }

  @PostMapping("/signup/admin")
  public ResponseEntity<?> registerAdmin(@RequestParam String username, @RequestParam String password) {
    if (userRepository.findByUsername(username).isPresent()) {
      return ResponseEntity.badRequest().body("Username is already taken.");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password)); // Hash the password

    user.setRole("ROLE_ADMIN");
    userRepository.save(user);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password)
                                                                      );
    String token = jwtTokenProvider.generateToken(authentication);

    Map<String, String> response = new HashMap<>();
    response.put("token", token);
    return ResponseEntity.ok(response);
  }
}
