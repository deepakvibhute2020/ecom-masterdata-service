package com.enncompany.ecom.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

  @Bean
  public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtProperties properties) {
    return new FilterRegistrationBean<>(new JwtAuthFilter(properties));
  }
}
