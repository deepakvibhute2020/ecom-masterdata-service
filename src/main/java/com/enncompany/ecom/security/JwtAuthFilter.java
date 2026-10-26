package com.enncompany.ecom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Re-validates the user's JWT (signature, expiry, issuer) forwarded by the gateway, so the service
 * does not trust the network alone.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

  public static final String CLAIMS_ATTRIBUTE = "jwtClaims";
  private static final String BEARER = "Bearer ";

  private final SecretKey key;
  private final String issuer;

  public JwtAuthFilter(JwtProperties properties) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(properties.secret()));
    this.issuer = properties.issuer();
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String uri = request.getRequestURI();
    return uri.equals("/api-docs") || uri.startsWith("/actuator/health");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    Claims claims = header != null && header.startsWith(BEARER) ? parse(header) : null;
    if (claims == null) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\":\"Invalid or missing token\"}");
      return;
    }
    request.setAttribute(CLAIMS_ATTRIBUTE, claims);
    chain.doFilter(request, response);
  }

  private Claims parse(String header) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .requireIssuer(issuer)
          .build()
          .parseSignedClaims(header.substring(BEARER.length()).trim())
          .getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }
}
