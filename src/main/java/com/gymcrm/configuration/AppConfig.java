package com.gymcrm.configuration;

import com.gymcrm.common.ApplicationProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.gymcrm")
@EnableJpaRepositories(basePackages = "com.gymcrm")
@PropertySource("classpath:application.properties")
public class AppConfig {
  private final ApplicationProperties applicationProperties;

  @Autowired
  public AppConfig(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(applicationProperties.getDriverClassName());
    config.setJdbcUrl(applicationProperties.getJdbcUrl());
    config.setUsername(applicationProperties.getUsername());
    config.setPassword(applicationProperties.getPassword());
    config.setMaximumPoolSize(applicationProperties.getMaximumPoolSize());
    config.setMinimumIdle(applicationProperties.getMinimumIdle());
    config.setIdleTimeout(applicationProperties.getIdleTimeout());
    config.setConnectionTimeout(applicationProperties.getConnectionTimeout());

    return new HikariDataSource(config);
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .load();
  }
}
