package com.enncompany.ecom.userinfo.controller;

import com.enncompany.ecom.enums.UserType;
import com.enncompany.ecom.userinfo.repository.UserInfoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal credential verification used by the gateway. Not exposed through the gateway routes.
 */
@RestController
@RequestMapping("/internal/auth")
public class AuthController {

  public record VerifyRequest(@NotBlank String loginId, @NotBlank String password) {}

  public record VerifyResponse(Long userId, String loginId, UserType userType) {}

  private final UserInfoRepository repository;
  private final PasswordEncoder passwordEncoder;

  public AuthController(UserInfoRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/verify")
  public ResponseEntity<VerifyResponse> verify(@Valid @RequestBody VerifyRequest request) {
    return repository.findByLoginIdAndActiveTrue(request.loginId()).stream()
        .filter(u -> passwordEncoder.matches(request.password(), u.getUserPass()))
        .findFirst()
        .map(u -> ResponseEntity.ok(new VerifyResponse(u.getId(), u.getLoginId(), u.getUserType())))
        .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }
}
