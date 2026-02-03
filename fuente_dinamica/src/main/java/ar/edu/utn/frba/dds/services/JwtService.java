package ar.edu.utn.frba.dds.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${authentication.jwt.secret}")
  private String secret;

  public String extractUsername(String token) {
    try {
      return extractAllClaims(token).getSubject();
    } catch (Exception e) {
      return null;
    }
  }

  public String extractRol(String token) {
    try {
      return extractAllClaims(token).get("rol", String.class);
    } catch (Exception e) {
      return null;
    }
  }

  public boolean isTokenValid(String token) {
    try {
      Claims claims = extractAllClaims(token);
      return !claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  public Claims extractAllClaims(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims;
    } catch (Exception e) {
      System.out.println("Error in extractAllClaims: " + e.getMessage());
      throw e;
    }
  }

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }
}
