package com.example.bankcards.security.jwt;

import com.example.bankcards.dto.auth.JwtAuthenticationDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.crypto.SecretKey;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final Logger log = LoggerFactory.getLogger(JwtService.class);
  private final SecretKey jwtAccessSecret;

  public JwtService(@Value("${jwt.secret.access}") String jwtAccessSecret) {
    this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
  }

  /**
   * Generates an authentication token for the given login.
   *
   * @param login The login for which to generate the token.
   * @return A DTO containing the JWT.
   */
  public JwtAuthenticationDto generateAuthToken(@NonNull String login) {
    return new JwtAuthenticationDto(generateJwtToken(login));
  }

  /**
   * Extracts the login from the given JWT.
   *
   * @param token The JWT from which to extract the login.
   * @return The login contained in the token.
   */
  public String getLoginFromToken(@NonNull String token) {
    return Jwts.parserBuilder()
            .setSigningKey(jwtAccessSecret)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  /**
   * Validates the given JWT.
   *
   * @param token The JWT to validate.
   * @return {@code true} if the token is valid, {@code false} otherwise.
   */
  public boolean validateJwtToken(@NonNull String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(jwtAccessSecret)
          .build()
          .parseClaimsJws(token)
          .getBody();
      return true;
    } catch (ExpiredJwtException expEx) {
      log.error("Token expired", expEx);
    } catch (UnsupportedJwtException unsEx) {
      log.error("Unsupported jwt", unsEx);
    } catch (MalformedJwtException mjEx) {
      log.error("Malformed jwt", mjEx);
    } catch (SignatureException sEx) {
      log.error("Invalid signature", sEx);
    } catch (Exception e) {
      log.error("invalid token", e);
    }
    return false;
  }

  private String generateJwtToken(String login) {
    var currentTime = LocalDateTime.now();
    var accessExpiration = currentTime.plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant();
    return Jwts.builder()
        .setSubject(login)
        .setExpiration(Date.from(accessExpiration))
        .signWith(jwtAccessSecret)
        .compact();
  }
}