package com.hancho.VotingSystem.commons.dtos;

import java.util.HashMap;
import java.util.Map;

public record TokenClaims(String email, String name, Map<String, Object> additionalClaims) {

  public Map<String, Object> toClaims() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    claims.put("name", name);

    if (additionalClaims != null && !additionalClaims.isEmpty()) {
      claims.putAll(additionalClaims);
    }

    return claims;
  }
}
