package com.rsmovie.rsmovieauth.config;

import com.rsmovie.rsmovieauth.filter.EmbeddedKeycloakRequestFilter;
import com.rsmovie.rsmovieauth.keycloak.EmbeddedKeycloakApplication;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.*;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;

@Configuration
public class EmbeddedKeycloakConfig {

    @Bean
    public ServletRegistrationBean keycloakJaxRsApplication(
            KeycloakServerProperties keyCloakServerProperties,
            DataSource dataSource
    ) throws Exception {
        mockJndiEnvironment(dataSource);
        EmbeddedKeycloakApplication.keycloakServerProperties = keyCloakServerProperties;
        ServletRegistrationBean<HttpServlet30Dispatcher> servlet = new ServletRegistrationBean<>(
                new HttpServlet30Dispatcher()
        );
        servlet.addInitParameter(
                "javax.ws.rs.Application",
                EmbeddedKeycloakApplication.class.getName()
        );
        servlet.addInitParameter(
                ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
                keyCloakServerProperties.getContextPath()
        );
        servlet.addInitParameter(
                ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS,
                "true"
        );
        servlet.addUrlMappings(keyCloakServerProperties.getContextPath() + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        return servlet;
    }

    @Bean
    public FilterRegistrationBean<EmbeddedKeycloakRequestFilter> keycloakSessionManagement(
        KeycloakServerProperties keycloakServerProperties
    ) {
        FilterRegistrationBean<EmbeddedKeycloakRequestFilter> filter =
                new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new EmbeddedKeycloakRequestFilter());
        filter.addUrlPatterns(keycloakServerProperties.getContextPath() + "/*");
        return filter;
    }

    private void mockJndiEnvironment(DataSource dataSource)
            throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(
                (env) -> (environment) -> new InitialContext() {
                    @Override
                    public Object lookup(Name name) {
                        return lookup(name.toString());
                    }

                    @Override
                    public Object lookup(String name) {
                        if("spring/datasource".equals(name)) {
                            return dataSource;
                        }
                        return null;
                    }

                    @Override
                    public NameParser getNameParser(String name) {
                        return CompositeName::new;
                    }

                    @Override
                    public void close() {}
                }
        );
    }
}
