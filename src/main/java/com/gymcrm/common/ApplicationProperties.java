package com.gymcrm.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {
  @Value("${spring.datasource.driver-class-name}")
  private String driverClassName;

  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.hikari.maximum-pool-size}")
  private int maximumPoolSize;

  @Value("${spring.datasource.hikari.minimum-idle}")
  private int minimumIdle;

  @Value("${spring.datasource.hikari.idle-timeout}")
  private int idleTimeout;

  @Value("${spring.datasource.hikari.connection-timeout}")
  private int connectionTimeout;
}
