package com.hancho.VotingSystem.security;

import com.hancho.VotingSystem.commons.dtos.TokenClaims;
import com.hancho.VotingSystem.user.dtos.UserRecord;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
class JwtService {

  private SecretKey secretKey;

  private final long ACCESS_EXPIRY = 60 * 60 * 3;

  private final long REFRESH_EXPIRY = 60 * 60 * 3;

  public JwtService() {
    try {

      KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
      SecretKey key = keyGenerator.generateKey();
      String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
      byte[] bytes = Decoders.BASE64.decode(encodedKey);
      secretKey = Keys.hmacShaKeyFor(bytes);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  public String generateToken(TokenClaims tokenClaims, long validity) {
    Map<String, Object> claims = tokenClaims.toClaims();
    return Jwts.builder()
        .claims()
        .add(claims)
        .subject(tokenClaims.email())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + validity * 1000))
        .and()
        .signWith(secretKey)
        .compact();
  }

  public String accessToken(TokenClaims tokenClaims) {
    return generateToken(tokenClaims, ACCESS_EXPIRY);
  }

  public String refreshToken(UserRecord record) {
    TokenClaims claims =
        new TokenClaims(record.email(), record.name(), Map.of("type", "refresh-token"));
    return generateToken(claims, REFRESH_EXPIRY);
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {

    Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  public boolean isTokenValid(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }
}
