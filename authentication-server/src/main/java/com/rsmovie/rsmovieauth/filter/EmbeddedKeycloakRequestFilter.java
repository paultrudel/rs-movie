package com.rsmovie.rsmovieauth.filter;

import org.keycloak.common.ClientConnection;
import org.keycloak.services.filters.AbstractRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class EmbeddedKeycloakRequestFilter extends AbstractRequestFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");

        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        servletResponse.setHeader("Access-Control-Allow-Origin", "*");
        servletResponse.setHeader("Access-Control-Allow-Methods",
                "GET, PUT, POST, DELETE, OPTIONS");
        servletResponse.setHeader("Access-Control-Max-Age", "3600");
        servletResponse.setHeader("Access-Control-Allow-Headers",
                "authorization, content-type, xsrf-token");
        servletResponse.setHeader("Access-Control-Expose-Headers", "xsrf-token");

        ClientConnection connection = createConnection((HttpServletRequest) request);

        filter(connection, (session) -> {
            try {
                chain.doFilter(request, response);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ClientConnection createConnection(HttpServletRequest request) {
        return new ClientConnection() {
            @Override
            public String getRemoteAddr() {
                return request.getRemoteAddr();
            }

            @Override
            public String getRemoteHost() {
                return request.getRemoteHost();
            }

            @Override
            public int getRemotePort() {
                return request.getRemotePort();
            }

            @Override
            public String getLocalAddr() {
                return request.getLocalAddr();
            }

            @Override
            public int getLocalPort() {
                return request.getLocalPort();
            }
        };
    }
}
