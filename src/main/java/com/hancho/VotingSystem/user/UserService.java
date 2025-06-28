package com.hancho.VotingSystem.user;

import com.hancho.VotingSystem.commons.dtos.TokenClaims;
import com.hancho.VotingSystem.security.GoogleTokenValidator;
import com.hancho.VotingSystem.security.JwtService;
import com.hancho.VotingSystem.user.dtos.AuthResponse;
import com.hancho.VotingSystem.user.dtos.GoogleAuthRequest;
import com.hancho.VotingSystem.user.dtos.UserRecord;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserInterface {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UsersRepository repository;
  private final GoogleTokenValidator validator;
  private final JwtService jwtService;

  public UserService(
      UsersRepository repository, GoogleTokenValidator validator, JwtService jwtService) {
    this.validator = validator;
    this.repository = repository;
    this.jwtService = jwtService;
  }

  @Override
  public Optional<Users> findUser(String email) {
    return repository.findByEmail(email);
  }

  @Override
  public Users createUser(UserRecord userRecord) {
    logger.info("Creating new user with email: {}", userRecord.email());

    Users user = new Users();
    user.setEmail(userRecord.email());
    user.setName(userRecord.name());
    user.setCreatedAt(LocalDateTime.now());
    user.setLastLoginAt(LocalDateTime.now());

    return repository.save(user);
  }

  @Override
  public AuthResponse authService(GoogleAuthRequest request) {
    try {
      logger.info("Processing Google authentication request");

      UserRecord record = validator.validateToken(request.googleToken());
      if (record == null) {
        logger.error("Invalid Google token provided");
        throw new IllegalArgumentException("Invalid Google token");
      }

      Users user =
          repository
              .findByEmail(record.email())
              .map(existingUser -> updateUserInfo(existingUser, record))
              .orElseGet(() -> createUser(record));

      TokenClaims tokenClaims =
          new TokenClaims(
              user.getEmail(),
              user.getName(),
              Map.of("userId", user.getId(), "type", "access-token"));

      String accessToken = jwtService.accessToken(tokenClaims);
      String refreshToken = jwtService.refreshToken(record);

      Date expiresAt = new Date(System.currentTimeMillis() + (60 * 60 * 1000));

      logger.info("Authentication successful for user: {}", user.getEmail());

      return new AuthResponse(
          accessToken,
          refreshToken,
          expiresAt,
          user.getId(),
          user.getEmail(),
          user.getName(),
          null);

    } catch (Exception e) {
      logger.error("Authentication failed: {}", e.getMessage(), e);
      throw new RuntimeException("Authentication failed", e);
    }
  }

  private Users updateUserInfo(Users user, UserRecord record) {
    logger.info("Updating user info for: {}", user.getEmail());

    if (record.name() != null && !record.name().equals(user.getName())) {
      user.setName(record.name());
    }

    user.setLastLoginAt(LocalDateTime.now());

    return repository.save(user);
  }

  public Optional<Users> getUserByEmail(String email) {
    return repository.findByEmail(email);
  }
}
