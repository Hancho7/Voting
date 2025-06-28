package com.hancho.VotingSystem.user;

import com.hancho.VotingSystem.user.dtos.AuthResponse;
import com.hancho.VotingSystem.user.dtos.GoogleAuthRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/google")
  public ResponseEntity<AuthResponse> googleAuth(@RequestBody GoogleAuthRequest request) {
    try {
      logger.info("Received Google authentication request");
      AuthResponse response = userService.authService(request);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid authentication request: {}", e.getMessage());
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      logger.error("Authentication error: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/test")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok("Auth endpoint is working");
  }
}
