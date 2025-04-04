package com.gymcrm.configuration;

import com.gymcrm.common.ApplicationProperties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
	private final ApplicationProperties applicationProperties;

	@Bean
	@Profile("local")
	public DataSource localDataSource() {
		return DataSourceBuilder.create().url(applicationProperties.getJdbcUrl())
		        .username(applicationProperties.getUsername()).password(applicationProperties.getPassword())
		        .driverClassName(applicationProperties.getDriverClassName()).build();
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
