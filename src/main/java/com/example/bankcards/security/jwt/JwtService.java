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

  @Value("${logging.include-application-group:true}")
  private String loggingIncludeApplicationGroup;

  public JwtService(@Value("${jwt.secret.access}") String jwtAccessSecret) {
    this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
  }

  public JwtAuthenticationDto generateAuthToken(@NonNull String login) {
    return new JwtAuthenticationDto(generateJwtToken(login));
  }

  public String getLoginFromToken(@NonNull String token) {
    return Jwts.parserBuilder()
            .setSigningKey(jwtAccessSecret)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

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
