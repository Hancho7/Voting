package com.hancho.VotingSystem.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public ResponseEntity<Users> getCurrentUser(Authentication authentication) {
    String email = authentication.getName();
    return userService
        .getUserByEmail(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/profile")
  public ResponseEntity<String> getProfile(Authentication authentication) {
    return ResponseEntity.ok("Profile for: " + authentication.getName());
  }
}
