package com.enncompany.ecom.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class CorsConfigTest {

  private static class ExposedRegistry extends CorsRegistry {
    java.util.Map<String, CorsConfiguration> configs() {
      return getCorsConfigurations();
    }
  }

  @Test
  void test_cors_addCorsMappings_allowsSwaggerOriginOnApi() {
    ExposedRegistry registry = new ExposedRegistry();

    new CorsConfig(new String[] {"http://localhost:8091"}).addCorsMappings(registry);

    CorsConfiguration config = registry.configs().get("/api/**");
    assertNotNull(config);
    assertEquals(List.of("http://localhost:8091"), config.getAllowedOrigins());
    assertTrue(config.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "OPTIONS")));
    assertEquals(List.of("*"), config.getAllowedHeaders());
    assertEquals(1, registry.configs().size());
  }
}
