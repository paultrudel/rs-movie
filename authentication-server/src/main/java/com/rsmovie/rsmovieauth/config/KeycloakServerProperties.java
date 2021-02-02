package com.rsmovie.rsmovieauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak.server")
public class KeycloakServerProperties {

    private String contextPath = "/auth";
    private String realmFile = "rs-realm.json";
    private AdminUser adminUser = new AdminUser();

    public String getContextPath() { return contextPath.replace("\"", ""); }
    public String getRealmFile() { return realmFile; }
    public AdminUser getAdminUser() { return adminUser; }

    public void setContextPath(String contextPath) { this.contextPath = contextPath; }
    public void setRealmFile(String realmFile) { this.realmFile = realmFile; }
    public void setAdminUser(AdminUser adminUser) { this.adminUser = adminUser; }

    public static class AdminUser {

        private String username = "devuser";
        private String password = "devuser";

        public String getUsername() { return username.replace("\"", ""); }
        public String getPassword() { return password.replace("\"", ""); }

        public void setUsername(String username) { this.username = username; }
        public void setPassword(String password) { this.password = password; }
    }
}
