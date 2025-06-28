package com.hancho.VotingSystem.security;

import com.hancho.VotingSystem.commons.dtos.GoogleTokenInfo;
import com.hancho.VotingSystem.user.dtos.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleTokenValidator {

  private static final Logger logger = LoggerFactory.getLogger(GoogleTokenValidator.class);

  @Value("${googleTokenInfoUrl}")
  private String googleTokenInfoUrl;

  private final RestTemplate restTemplate;

  public GoogleTokenValidator() {
    this.restTemplate = new RestTemplate();
  }

  public UserRecord validateToken(String token) {
    try {
      String url = googleTokenInfoUrl + "?access_token=" + token;
      ResponseEntity<GoogleTokenInfo> resp = restTemplate.getForEntity(url, GoogleTokenInfo.class);

      if (resp.getStatusCode() == HttpStatus.OK) {
        var googleTokenInfo = resp.getBody();

        if (googleTokenInfo != null && googleTokenInfo.getEmail() != null) {
          return new UserRecord(googleTokenInfo.getEmail(), googleTokenInfo.getName());
        }
      }
    } catch (Exception e) {
      logger.error("Google token validation error: {}", e.getMessage(), e);
    }
    return null;
  }
}
