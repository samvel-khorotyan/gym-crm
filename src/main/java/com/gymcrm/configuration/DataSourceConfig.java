package com.gymcrm.configuration;

import com.gymcrm.common.ApplicationProperties;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataSourceConfig {
	private final ApplicationProperties applicationProperties;

	public DataSourceConfig(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	@Bean
	@Profile("dev")
	public DataSource devDataSource() {
		return DataSourceBuilder.create().url(applicationProperties.getJdbcUrl())
		        .username(applicationProperties.getUsername()).password(applicationProperties.getPassword())
		        .driverClassName(applicationProperties.getDriverClassName()).build();
	}

	@Bean
	@Profile("stg")
	public DataSource stgDataSource() {
		return DataSourceBuilder.create().url(applicationProperties.getJdbcUrl())
		        .username(applicationProperties.getUsername()).password(applicationProperties.getPassword())
		        .driverClassName(applicationProperties.getDriverClassName()).build();
	}

	@Bean
	@Profile("prod")
	public DataSource prodDataSource() {
		return DataSourceBuilder.create().url(applicationProperties.getJdbcUrl())
		        .username(applicationProperties.getUsername()).password(applicationProperties.getPassword())
		        .driverClassName(applicationProperties.getDriverClassName()).build();
	}
}
