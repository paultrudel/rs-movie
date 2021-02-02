package com.rsmovie.rsmovieauth.keycloak;

import com.rsmovie.rsmovieauth.config.KeycloakServerProperties;
import com.rsmovie.rsmovieauth.json.RegularJsonConfigProviderFactory;
import lombok.Data;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.NoSuchElementException;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    public static KeycloakServerProperties keycloakServerProperties;

    protected void loadConfig() {
        JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
        Config.init(factory.create().orElseThrow(
                () -> new NoSuchElementException("No value present.")
        ));
    }

    public EmbeddedKeycloakApplication() {
        super();
        createMasterRealmAdminUser();
        createRsRealm();
    }

    private void createMasterRealmAdminUser() {
        KeycloakSession session = getSessionFactory().create();
        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
        KeycloakServerProperties.AdminUser admin = keycloakServerProperties.getAdminUser();

        try {
            session.getTransactionManager().begin();
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
            session.getTransactionManager().commit();
        } catch(Exception e) {
            e.printStackTrace();
            session.getTransactionManager().rollback();
        }

        session.close();
    }

    private void createRsRealm() {
        KeycloakSession session = getSessionFactory().create();

        try {
            session.getTransactionManager().begin();
            RealmManager realmManager = new RealmManager(session);
            Resource realmFile = new ClassPathResource(keycloakServerProperties.getRealmFile());
            realmManager.importRealm(
                    JsonSerialization.readValue(realmFile.getInputStream(),
                            RealmRepresentation.class)
            );
            session.getTransactionManager().commit();
        } catch(Exception e) {
            e.printStackTrace();
            session.getTransactionManager().rollback();
        }

        session.close();
    }
}
