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
  //igual token que en auth server
  @Value("${authentication.jwt.secretToken}")
  private String secretToken;

  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(secretToken.getBytes(StandardCharsets.UTF_8));
  }

  public String extractUsername(String token) {
    Claims jwtToken = Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    return jwtToken.getSubject();
  }

  public String extractRol(String token) {
    Claims claims = Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Object rol = claims.get("rol");
    if (rol instanceof String) {
      return rol.toString();
    } else {
      return null;
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

  public Boolean isTokenValid(String token) {
    String username = extractUsername(token);
    return username !=null && !this.isTokenExpired(token);
  }

  public Boolean isTokenExpired(String token) {
    Date expiration = this.extractExpiration(token);
    return expiration.before(new Date());
  }
}
