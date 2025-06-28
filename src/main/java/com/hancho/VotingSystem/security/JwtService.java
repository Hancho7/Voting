package com.hancho.VotingSystem.security;

import com.hancho.VotingSystem.commons.dtos.TokenClaims;
import com.hancho.VotingSystem.user.dtos.UserRecord;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  @Value("${jwt.private-key-path:classpath:keys/private_key.pem}")
  private Resource privateKeyResource;

  @Value("${jwt.public-key-path:classpath:keys/public_key.pem}")
  private Resource publicKeyResource;

  private PrivateKey privateKey;
  private PublicKey publicKey;

  private final long ACCESS_EXPIRY = 60 * 60 * 1; // 1 hour
  private final long REFRESH_EXPIRY = 60 * 60 * 24 * 7; // 7 days

  @PostConstruct
  public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    this.privateKey = loadPrivateKey();
    this.publicKey = loadPublicKey();
  }

  private PrivateKey loadPrivateKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String privateKeyContent =
        new String(privateKeyResource.getInputStream().readAllBytes())
            .replaceAll("-----BEGIN PRIVATE KEY-----", "")
            .replaceAll("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

    byte[] decoded = Base64.getDecoder().decode(privateKeyContent);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(spec);
  }

  private PublicKey loadPublicKey()
      throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String publicKeyContent =
        new String(publicKeyResource.getInputStream().readAllBytes())
            .replaceAll("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");

    byte[] decoded = Base64.getDecoder().decode(publicKeyContent);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(spec);
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
        .signWith(privateKey)
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
    return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
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
