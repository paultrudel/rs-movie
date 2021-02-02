package com.rsmovie.rsmovieauth;

import com.rsmovie.rsmovieauth.config.KeycloakServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties({ KeycloakServerProperties.class })
public class RsMovieAuthApplication {

	private static final Logger logger = LoggerFactory.getLogger(RsMovieAuthApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RsMovieAuthApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(
			ServerProperties serverProperties,
			KeycloakServerProperties keycloakServerProperties
	) {

		return (evt) -> {

			Integer port = serverProperties.getPort();
			String keycloakContextPath = keycloakServerProperties.getContextPath();

			logger.info("Embedded Keycloak started: http://localhost:{}{} to use keycloak"
					, port, keycloakContextPath);
		};
	}
}
