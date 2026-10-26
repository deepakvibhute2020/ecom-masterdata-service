package com.enncompany.ecom.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthFilterTest {

  private static final String SECRET =
      "ZWNvbS11c2VyYXV0aC1zZXJ2aWNlLWRldi1vbmx5LXNlY3JldC1rZXktMzItYnl0ZXM=";
  private static final String OTHER_SECRET =
      "b3RoZXItc2VjcmV0LWtleS1vdGhlci1zZWNyZXQta2V5LW90aGVyLXNlY3JldA==";

  private final JwtAuthFilter filter = new JwtAuthFilter(new JwtProperties(SECRET, "iss"));
  private final FilterChain chain = mock(FilterChain.class);

  private static String token(String secret, String issuer, long ttlMillis) {
    return Jwts.builder()
        .issuer(issuer)
        .subject("bob")
        .expiration(new Date(System.currentTimeMillis() + ttlMillis))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
        .compact();
  }

  private MockHttpServletResponse call(String path, String authHeader) throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest("GET", path);
    if (authHeader != null) {
      req.addHeader("Authorization", authHeader);
    }
    MockHttpServletResponse res = new MockHttpServletResponse();
    filter.doFilter(req, res, chain);
    return res;
  }

  @Test
  void validTokenPasses() throws Exception {
    MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/customers");
    req.addHeader("Authorization", "Bearer " + token(SECRET, "iss", 60_000));
    MockHttpServletResponse res = new MockHttpServletResponse();
    filter.doFilter(req, res, chain);
    verify(chain).doFilter(req, res);
    assertThat(req.getAttribute(JwtAuthFilter.CLAIMS_ATTRIBUTE)).isNotNull();
  }

  @Test
  void missingHeaderRejected() throws Exception {
    assertThat(call("/api/customers", null).getStatus()).isEqualTo(401);
    verifyNoInteractions(chain);
  }

  @Test
  void invalidTokensRejected() throws Exception {
    assertThat(call("/api/customers", "Bearer garbage").getStatus()).isEqualTo(401);
    assertThat(call("/api/customers", "Bearer " + token(OTHER_SECRET, "iss", 60_000)).getStatus())
        .isEqualTo(401);
    assertThat(call("/api/customers", "Bearer " + token(SECRET, "other", 60_000)).getStatus())
        .isEqualTo(401);
    assertThat(call("/api/customers", "Bearer " + token(SECRET, "iss", -60_000)).getStatus())
        .isEqualTo(401);
    verifyNoInteractions(chain);
  }

  @Test
  void docsAndHealthSkipFilter() throws Exception {
    assertThat(call("/api-docs", null).getStatus()).isEqualTo(200);
    assertThat(call("/actuator/health/liveness", null).getStatus()).isEqualTo(200);
  }
}
