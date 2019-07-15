package org.lxc.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.lxc.platform.exception.HttpException;
import org.lxc.platform.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

  private final String secretKey;
  private final long validityInMilliseconds;
  private final MyUserDetails myUserDetails;

  @Autowired
  public JwtTokenProvider(
      MyUserDetails myUserDetails,
      @Value("${security.jwt.token.expireLength}") long validityInMilliseconds,
      @Value("${security.jwt.token.secretKey}") String secretKey) {
    this.myUserDetails = myUserDetails;
    this.validityInMilliseconds = validityInMilliseconds;
    this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String createToken(String username, List<Role> roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put("auth", roles.stream()
        .map(s -> new SimpleGrantedAuthority(s.getAuthority()))
        .collect(Collectors.toList()));

    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  public Authentication getAuthentication(String token) {
    UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String getUsername(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring("Bearer ".length());
    }
    return null;
  }

  public Optional<String> resolveToken(StompHeaderAccessor accessor) {
    String bearerToken = Optional.ofNullable(accessor.getNativeHeader("Authorization"))
        .map(list -> list.get(0))
        .orElse("");

    if (bearerToken.startsWith("Bearer ")) {
      return Optional.of(bearerToken.substring("Bearer ".length()));
    }

    return Optional.empty();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      LOG.info("Exception in jwt token provider when validation: {}", e);
      throw new HttpException("Expired or invalid JWT token", HttpStatus.UNAUTHORIZED);
    }
  }

}
