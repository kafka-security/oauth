/*
Copyright © 2020 BlackRock Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.bfm.kafka.security.oauthbearer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * The type O auth configuration.
 */
public class OAuthConfiguration {

    //region Constants

    private static final String KAFKA_OAUTH_SERVER_PROP_FILE_ENV_VAR = "KAFKA_OAUTH_SERVER_PROP_FILE";
    private static final String KAFKA_OAUTH_SERVER_BASE_URI_ENV_VAR = "KAFKA_OAUTH_SERVER_BASE_URI";
    private static final String KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH_ENV_VAR = "KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH";
    private static final String KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH_ENV_VAR = "KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH";
    private static final String KAFKA_OAUTH_SERVER_CLIENT_ID_ENV_VAR = "KAFKA_OAUTH_SERVER_CLIENT_ID";
    private static final String KAFKA_OAUTH_SERVER_CLIENT_SECRET_ENV_VAR = "KAFKA_OAUTH_SERVER_CLIENT_SECRET";
    private static final String KAFKA_OAUTH_SERVER_GRANT_TYPE_ENV_VAR = "KAFKA_OAUTH_SERVER_GRANT_TYPE";
    private static final String KAFKA_OAUTH_SERVER_SCOPES_ENV_VAR = "KAFKA_OAUTH_SERVER_SCOPES";
    private static final String KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER_ENV_VAR = "KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER";

    private static final String KAFKA_OAUTH_SERVER_BASE_URI = "oauth.server.base.uri";
    private static final String KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH = "oauth.server.token.endpoint.path";
    private static final String KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH = "oauth.server.introspection.endpoint.path";
    private static final String KAFKA_OAUTH_SERVER_CLIENT_ID = "oauth.server.client.id";
    private static final String KAFKA_OAUTH_SERVER_CLIENT_SECRET = "oauth.server.client.secret";
    private static final String KAFKA_OAUTH_SERVER_GRANT_TYPE = "oauth.server.grant.type";
    private static final String KAFKA_OAUTH_SERVER_SCOPES = "oauth.server.scopes";
    private static final String KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER = "oauth.server.accept.unsecure.server";

    //endregion

    //region Member Variables
    private final Logger log = LoggerFactory.getLogger(OAuthConfiguration.class);
    private String baseServerUri;
    private String tokenEndpointPath;
    private String introspectionEndpointPath;
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String scopes;
    private Boolean unsecureServer;

    //endregion

    //region Constructors

    /**
     * Instantiates a new O auth configuration.
     */
    public OAuthConfiguration() {

        try {
            log.debug("Starting to initialize OAuth configuration");

            log.debug("Look for OAuthConfiguration property file.");
            Properties prop = this.getConfigurationFileProperties();


            // get the OAuth server base Uri
            log.debug("Configure the OAuth server base Uri");
            String defaultBaseServerUri = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_BASE_URI)) {
                defaultBaseServerUri = prop.getProperty(KAFKA_OAUTH_SERVER_BASE_URI);
            }

            this.baseServerUri = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                KAFKA_OAUTH_SERVER_BASE_URI_ENV_VAR,
                defaultBaseServerUri);

            // get the OAuth server token path
            log.debug("Configure the OAuth server token endpoint path.");
            String defaultTokenEndpointPath = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH)) {
                defaultTokenEndpointPath = prop.getProperty(KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH);
            }

            this.tokenEndpointPath = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH_ENV_VAR,
                defaultTokenEndpointPath);

            // get the OAuth server introspection endpoint path
            log.debug("Configure the OAuth server introspection endpoint path.");
            String defaultIntrospectionEndpointPath = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH)) {
                defaultIntrospectionEndpointPath = prop.getProperty(KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH);
            }

            this.introspectionEndpointPath = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH_ENV_VAR,
                defaultIntrospectionEndpointPath);

            // get the OAuth server client id
            log.debug("Configure the OAuth server client id.");
            String defaultClientId = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_CLIENT_ID)) {
                defaultClientId = prop.getProperty(KAFKA_OAUTH_SERVER_CLIENT_ID);
            }

            this.clientId = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                KAFKA_OAUTH_SERVER_CLIENT_ID_ENV_VAR,
                defaultClientId);

            // get the OAuth server client secret
            log.debug("Configure the OAuth server client secret.");
            String defaultClientSecret = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_CLIENT_SECRET)) {
                defaultClientSecret = prop.getProperty(KAFKA_OAUTH_SERVER_CLIENT_SECRET);
            }

            this.clientSecret = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                KAFKA_OAUTH_SERVER_CLIENT_SECRET_ENV_VAR,
                defaultClientSecret);

            // get the OAuth server grant type
            log.debug("Configure the OAuth server grant type");
            String defaultGrantType = "client_credentials";
            if (prop.containsKey(KAFKA_OAUTH_SERVER_GRANT_TYPE)) {
                defaultGrantType = prop.getProperty(KAFKA_OAUTH_SERVER_GRANT_TYPE);
            }

            this.grantType = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                    KAFKA_OAUTH_SERVER_GRANT_TYPE_ENV_VAR,
                    defaultGrantType);

            // get the OAuth server scopes
            log.debug("Configure the OAuth server scopes");
            String defaultScopes = null;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_SCOPES)) {
                defaultScopes = prop.getProperty(KAFKA_OAUTH_SERVER_SCOPES);
            }

            this.scopes = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                    KAFKA_OAUTH_SERVER_SCOPES_ENV_VAR,
                    defaultScopes);

            // get the OAuth server unsecure server
            log.debug("Configure the OAuth server unsecure server");
            Boolean defaultUnsecureServer = false;
            if (prop.containsKey(KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER)) {
                String propValue = prop.getProperty(KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER);

                defaultUnsecureServer = Boolean.valueOf(propValue);
            }

            this.unsecureServer = EnvironmentVariablesUtil.getBooleanEnvironmentVariable(
                KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER_ENV_VAR,
                defaultUnsecureServer);

            if (!this.isValid()) {
                throw new IllegalStateException("Configuration entries are invalid.");
            }

        } catch (Exception ex) {
            log.error("Error getting OAuth configuration, Message: %s", ex);
            throw ex;
        } finally {
            log.debug("Finished initializing OAuth configuration");
        }
    }

    //endregion

    //region Public Properties

    /**
     * Gets base server uri.
     *
     * @return the base server uri
     */
    public String getBaseServerUri() {
        return this.baseServerUri;
    }

    /**
     * Gets token endpoint path.
     *
     * @return the token endpoint path
     */
    public String getTokenEndpointPath() {
        return tokenEndpointPath;
    }


    /**
     * Gets token endpoint.
     *
     * @return the token endpoint
     */
    public String getTokenEndpoint() {
        return this.baseServerUri + this.tokenEndpointPath;
    }

    /**
     * Gets introspection endpoint.
     *
     * @return the introspection endpoint
     */
    public String getIntrospectionEndpoint() {
        return this.baseServerUri + this.introspectionEndpointPath;
    }

    /**
     * Gets introspection endpoint path.
     *
     * @return the introspection endpoint path
     */
    public String getIntrospectionEndpointPath() {
        return this.introspectionEndpointPath;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return this.clientId;
    }


    /**
     * Gets client secret.
     *
     * @return the client secret
     */
    public String getClientSecret() {
        return this.clientSecret;
    }


    /**
     * Gets grant type.
     *
     * @return the grant type
     */
    public String getGrantType() {
        return this.grantType;
    }


    /**
     * Gets scopes.
     *
     * @return the scopes
     */
    public String getScopes() {
        return this.scopes;
    }

    /**
     * Gets unsecure server.
     *
     * @return the unsecure server
     */
    public Boolean getUnsecureServer() {
        return this.unsecureServer;
    }


    //endregion

    //region Public Methods

    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    private Boolean isValid() {

        if (!Utils.isURIValid(this.baseServerUri)) {
            // the token endpoint is not valid
            return false;
        }

        if (!Utils.isURIValid(this.baseServerUri + this.tokenEndpointPath)) {
            // the token endpoint is not valid
            return false;
        }

        if (!Utils.isURIValid(this.baseServerUri + this.introspectionEndpointPath)) {
            // the introspection endpoint is not valid
            return false;
        }

        if (Utils.isNullOrEmpty(this.clientId)) {
            return false;
        }

        if (Utils.isNullOrEmpty(this.clientSecret)) {
            return false;
        }

        if (Utils.isNullOrEmpty(this.grantType)) {
            return false;
        }

        return true;
    }

    public void setConfigurationFromJaasConfigEntries(Map<String,String> jaasConfigEntries) {
        //validate the parameters
        Objects.requireNonNull(jaasConfigEntries);

        // get the OAuth server base Uri
        String defaultBaseServerUri = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_BASE_URI, "");
        if (!Utils.isNullOrEmpty(defaultBaseServerUri)) {
            this.baseServerUri = defaultBaseServerUri;
        }

        // get the OAuth server token path
        String defaultTokenEndpointPath = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH, "");
        if (!Utils.isNullOrEmpty(defaultTokenEndpointPath)) {
            this.tokenEndpointPath = defaultTokenEndpointPath;
        }

        // get the OAuth server introspection endpoint path
        String defaultIntrospectionEndpointPath = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH, "");
        if (!Utils.isNullOrEmpty(defaultIntrospectionEndpointPath)) {
            this.introspectionEndpointPath = defaultIntrospectionEndpointPath;
        }

        // get the OAuth server client id
        String defaultClientId = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_CLIENT_ID, "");
        if (!Utils.isNullOrEmpty(defaultClientId)) {
            this.clientId = defaultClientId;
        }

        // get the OAuth server client secret
        String defaultClientSecret = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_CLIENT_SECRET, "");
        if (!Utils.isNullOrEmpty(defaultClientSecret)) {
            this.clientSecret = defaultClientSecret;
        }

        // get the OAuth server grant type
        String defaultGrantType = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_GRANT_TYPE, "");
        if (!Utils.isNullOrEmpty(defaultGrantType)) {
            this.grantType = defaultGrantType;
        }

        // get the OAuth server scopes
        String defaultScopes = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_SCOPES, "");
        if (!Utils.isNullOrEmpty(defaultScopes)) {
            this.scopes = defaultScopes;
        }

        // get the OAuth server unsecure server
        String defaultUnsecureServer = jaasConfigEntries.getOrDefault(KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER, "");
        if (!Utils.isNullOrEmpty(defaultUnsecureServer)) {
            this.unsecureServer = Boolean.valueOf(defaultUnsecureServer);
        }

        //check if the configuration remains valid
        if (!this.isValid()) {
            throw new IllegalStateException("Configuration entries at jaas configuration file are invalid.");
        }
    }

    //endregion

    //region Protected Methods


    /**
     * Gets configuration file properties.
     *
     * @return the configuration file properties
     */
    protected Properties getConfigurationFileProperties() {

        Properties properties = new Properties();
        try {
            log.debug("Starting to load configuration properties from property file.");

            String configurationFilePath = EnvironmentVariablesUtil.getStringEnvironmentVariable(
                    KAFKA_OAUTH_SERVER_PROP_FILE_ENV_VAR,
                    null);

            if (!Utils.isNullOrEmpty(configurationFilePath)) {
                log.debug(
                    "Load properties from {}",
                    KAFKA_OAUTH_SERVER_PROP_FILE_ENV_VAR);

                InputStream input = new FileInputStream(configurationFilePath);

                // load a properties file
                properties.load(input);
            } else {
                properties.setProperty(KAFKA_OAUTH_SERVER_BASE_URI, "http://localhost:8080/auth/realms/master/protocol/openid-connect");
                properties.setProperty(KAFKA_OAUTH_SERVER_TOKEN_ENDPOINT_PATH, "/token");
                properties.setProperty(KAFKA_OAUTH_SERVER_INTROSPECTION_ENDPOINT_PATH, "/token/introspect");
                properties.setProperty(KAFKA_OAUTH_SERVER_CLIENT_ID, "test-consumer");
                properties.setProperty(KAFKA_OAUTH_SERVER_CLIENT_SECRET, "7b3c23ef-8909-489e-bf4a-64a84abb197e");
                properties.setProperty(KAFKA_OAUTH_SERVER_GRANT_TYPE, "client_credentials");
                properties.setProperty(KAFKA_OAUTH_SERVER_SCOPES, "test");
                properties.setProperty(KAFKA_OAUTH_SERVER_ACCEPT_UNSECURE_SERVER, "true");

                log.debug(
                    "{} enviroment variable is not set, cannot load properties.",
                    KAFKA_OAUTH_SERVER_PROP_FILE_ENV_VAR);
            }
        }catch (FileNotFoundException ex) {
            String errMsg = String.format("OAuth property file not found, Message: %s", ex.getMessage());
            log.warn(errMsg);
        } catch (IOException ex) {
            String errMsg = String.format("Error reading OAuth property file not found, Message: %s", ex.getMessage());
            log.warn(errMsg);
        } finally {
            log.debug("Finished loading configuration properties from property file.");
        }

        return properties;
    }

    //endregion
}



