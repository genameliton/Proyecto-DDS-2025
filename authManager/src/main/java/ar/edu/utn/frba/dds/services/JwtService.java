package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @Value("${authentication.jwt.secretToken}")
  private String secretToken;
  @Value("${authentication.jwt.expiration}")
  private Duration expiration;
  @Value("${authentication.jwt.refreshExpiration}")
  private Duration refreshExpiration;

  private String buildToken(User user, Long expiration) {
    return Jwts.builder()
        .claims(Map.of("username", user.getUsername(),
            "rol", user.getRol().getTiporol().toString()))
        .subject(user.getUsername())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getKey(), Jwts.SIG.HS256)
        .compact();
  }

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(secretToken.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(User user) {
    return this.buildToken(user, expiration.toMillis());
  }

  public String generateRefreshToken(User user) {
    return this.buildToken(user, refreshExpiration.toMillis());
  }

  public String extractUsername(String token) {
    try {
      Claims jwtToken = Jwts.parser()
          .verifyWith(getKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return jwtToken.getSubject();
    } catch (Exception e) {
      throw new InsufficientAuthenticationException("Token with invalid signature");
    }
  }

  public Date extractExpiration(String token) {
    Claims jwtToken = Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return jwtToken.getExpiration();
  }

  public Boolean isTokenValid(String token, User user) {
    String username = extractUsername(token);
    return username.equals(user.getUsername()) && !this.isTokenExpired(token);
  }

  public Boolean isTokenExpired(String token) {
    Date expiration = this.extractExpiration(token);
    return expiration.before(new Date());
  }
}
