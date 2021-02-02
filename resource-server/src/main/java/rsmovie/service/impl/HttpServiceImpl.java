package rsmovie.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rsmovie.service.auth.AuthError;
import rsmovie.service.auth.AuthResponse;
import rsmovie.service.auth.AuthSuccess;
import rsmovie.entity.User;
import rsmovie.service.HttpService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HttpServiceImpl implements HttpService {

    private static final Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);
    private static final String KEYCLOAK_BASE_URL = "http://localhost:8081/auth";
    private static final String KEYCLOAK_MASTER_REALM_USER_USERNAME = "devuser";
    private static final String KEYCLOAK_MASTER_REALM_USER_PASSWORD = "devuser";
    private static final String HASH_FUNCTION = "pbkdf2-sha256";
    private static final int HASH_ITERATIONS = 27500;

    private AuthSuccess admin;

    public HttpServiceImpl() {
        getAdminAccess();
    }

    public int registerUser(User user, String password) {
        logger.info("========== Adding new user to keycloak realm ==========");
        String userUrl = KEYCLOAK_BASE_URL + "/admin/realms/rs-movie/users";

        try {
            logger.info("Opening connection to: {}", userUrl);
            URL url = new URL(userUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(RequestProperty.POST.getValue());
            connection.setRequestProperty(
                    RequestProperty.CONTENT_TYPE.getValue(),
                    RequestProperty.APP_JSON.getValue()
            );
            connection.setRequestProperty(
                    RequestProperty.AUTHORIZATION.getValue(),
                    RequestProperty.BEARER.getValue() + " " + admin.getAccessToken()
            );
            connection.setDoOutput(true);

            KeycloakUser keycloakUser = mapUserToKeycloakUser(user, password);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String keycloakUserAsJsonString = mapper.writeValueAsString(keycloakUser);
            logger.info("User JSON String: {}", keycloakUserAsJsonString);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(keycloakUserAsJsonString);
            outputStream.flush();
            outputStream.close();

            logger.info("Server responded with status code: {}", connection.getResponseCode());

            return connection.getResponseCode();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return 401;
    }

    public AuthResponse authenticateUser(String username, String password) {
        String authUrl = KEYCLOAK_BASE_URL + "/realms/rs-movie/protocol/openid-connect/token";

        try {
            URL url = new URL(authUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(RequestProperty.POST.getValue());
            connection.setRequestProperty(
                    RequestProperty.CONTENT_TYPE.getValue(),
                    RequestProperty.APP_FORM.getValue()
            );
            connection.setDoOutput(true);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(
                    RequestProperty.CLIENT_ID.getValue(),
                    RequestProperty.NEW_CLIENT.getValue()
            );
            parameters.put(
                    RequestProperty.GRANT_TYPE.getValue(),
                    RequestProperty.PASSWORD.getValue()
            );
            parameters.put(
                    RequestProperty.CLIENT_SECRET.getValue(),
                    RequestProperty.NEW_CLIENT_SECRET.getValue()
            );
            parameters.put(
                    RequestProperty.SCOPE.getValue(),
                    RequestProperty.OPEN_ID.getValue()
            );
            parameters.put(
                    RequestProperty.USERNAME.getValue(),
                    username
            );
            parameters.put(
                    RequestProperty.PASSWORD.getValue(),
                    password
            );

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(getParameterString(parameters));
            outputStream.flush();
            outputStream.close();

            String response = handleResponse(connection);

            if(connection.getResponseCode() < 300) {
                return parseResponse(response, AuthSuccess.class);
            } else {
                return parseResponse(response, AuthError.class);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        AuthError error = new AuthError();
        error.setError("Error");
        error.setErrorDescription("An unknown error occurred");
        return error;
    }

    private void getAdminAccess() {
        logger.info("========== Attempting to acquire admin authorization token =========");
        String adminAccessUrl = KEYCLOAK_BASE_URL + "/realms/master/protocol/openid-connect/token";

        try {
            logger.info("Opening connection to: {}", adminAccessUrl);
            URL url = new URL(adminAccessUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(RequestProperty.POST.getValue());
            connection.setRequestProperty(
                    RequestProperty.CONTENT_TYPE.getValue(),
                    RequestProperty.APP_FORM.getValue()
            );
            connection.setDoOutput(true);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(
                    RequestProperty.USERNAME.getValue(),
                    KEYCLOAK_MASTER_REALM_USER_USERNAME
            );
            parameters.put(
                    RequestProperty.PASSWORD.getValue(),
                    KEYCLOAK_MASTER_REALM_USER_PASSWORD
            );
            parameters.put(
                    RequestProperty.GRANT_TYPE.getValue(),
                    RequestProperty.PASSWORD.getValue()
            );
            parameters.put(
                    RequestProperty.CLIENT_ID.getValue(),
                    RequestProperty.ADMIN_CLI.getValue()
            );

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(getParameterString(parameters));
            outputStream.flush();
            outputStream.close();

            String response = handleResponse(connection);
            logger.info("Response: {}", response);
            if(connection.getResponseCode() < 300) {
                this.admin = parseResponse(response, AuthSuccess.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getParameterString(Map<String, String> params)
            throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    private String handleResponse(HttpURLConnection connection) throws IOException {
        logger.info("Handling response from {}", connection.getURL().getPath());
        Reader reader = null;

        logger.info("Server responded with status code: {}", connection.getResponseCode());
        if(connection.getResponseCode() > 299) {
            reader = new InputStreamReader(connection.getErrorStream());
        } else {
            reader = new InputStreamReader(connection.getInputStream());
        }

        BufferedReader bufferedReader = new BufferedReader(reader);
        String inputLine;
        StringBuilder responseContent = new StringBuilder();

        while((inputLine = bufferedReader.readLine()) != null) {
            responseContent.append(inputLine);
        }
        bufferedReader.close();

        return responseContent.toString();
    }

    private <T> T parseResponse(String responseString, Class<T> mappingClass)
            throws JsonProcessingException {
        logger.info("Parsing response string: {}", responseString);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseString, mappingClass);
    }

    private KeycloakUser mapUserToKeycloakUser(User user, String password) {
        logger.info("Mapping user into keycloak user");
        logger.info("User: {}", user);
        KeycloakUser  keycloakUser = new KeycloakUser();
        keycloakUser.setId(user.getId());
        keycloakUser.setUsername(user.getUsername());

        Credentials credentials = new Credentials();
        credentials.setValue(password);

        keycloakUser.addCredentials(credentials);

        return keycloakUser;
    }

    private enum RequestProperty {
        GET("GET"),
        POST("POST"),
        CONTENT_TYPE("Content-Type"),
        AUTHORIZATION("Authorization"),
        BEARER("Bearer"),
        USERNAME("username"),
        PASSWORD("password"),
        GRANT_TYPE("grant_type"),
        CLIENT_ID("client_id"),
        NEW_CLIENT("newClient"),
        CLIENT_SECRET("client_secret"),
        NEW_CLIENT_SECRET("newClientSecret"),
        SCOPE("scope"),
        OPEN_ID("openid"),
        ADMIN_CLI("admin-cli"),
        APP_FORM("application/x-www-form-urlencoded"),
        APP_JSON("application/json");

        private final String value;

        RequestProperty(String value) {
            this.value = value;
        }

        public String getValue() { return this.value; }

    }

    @Data
    private static class KeycloakUser {

        private String id;
        private String username;
        private boolean enabled = true;
        private List<Credentials> credentials;
        private String[] realmRoles = {"offline_access", "uma_authorization"};

        @JsonIgnore
        private String[] accountRoles = {"manage-account", "view-profile"};

        private Map<String, String[]> clientRoles = Stream.of(new Object[][] {
                {"account", accountRoles}
        }).collect(Collectors.toMap(data -> (String) data[0], data -> (String[]) data[1]));

        public void addCredentials(Credentials credential) {
            if(credentials == null) {
                credentials = new ArrayList<>();
            }
            credentials.add(credential);
        }
    }

    @Data
    private static class Credentials {

        private String type = "password";
        private String algorithm = HASH_FUNCTION;
        private String hashIterations = String.valueOf(HASH_ITERATIONS);
        private String value;
    }

}
