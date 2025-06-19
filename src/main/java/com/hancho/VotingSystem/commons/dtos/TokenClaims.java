package com.hancho.VotingSystem.commons.dtos;

import java.util.HashMap;
import java.util.Map;

public record TokenClaims(String name, String email, Map<String, Object> additionalClaims) {
  public Map<String, Object> toClaims() {
    Map<String, Object> claims = Map.of("email", email, "name", name);

    if (additionalClaims != null) {
      Map<String, Object> allClaims = new HashMap<>(claims);
      allClaims.putAll(additionalClaims());
      return allClaims;
    }

    return claims;
  }
}
