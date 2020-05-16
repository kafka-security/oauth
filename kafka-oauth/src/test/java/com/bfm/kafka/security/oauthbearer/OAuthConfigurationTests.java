/*
Copyright © 2019 BlackRock Inc.

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

import org.apache.kafka.common.protocol.types.Field;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class OAuthConfigurationTests {

    @Test
    public void constructor_NoParameters_ReturnsDefaultObjectCreated() {
        // act
        OAuthConfiguration oauthConfiguration = new OAuthConfiguration();

        // assert
        assertNotNull(oauthConfiguration);
    }

    @Test
    public void testSetConfigurationUsingJaasConfiguratioFile(){
        Map<String, String> jaasConfigurationEntries = new TreeMap<>();

        String serverBaseUri = "http://localhost:8080/auth/realms/master1/protocol/openid-connect";
        String serverEndpointPath = "/token1";
        String serverIntrospectionEndpointPath = "/token1/introspect";
        String serverClientId = "test-consumer-1";
        String serverClientSecret = "7b3c23e1-8909-489e-bf4a-64a84abb197e";
        String serverGrantType = "client_credentials";
        String serverScopes = "test-1";
        String serverAcceptUnsecureServer = "false";

        jaasConfigurationEntries.put("oauth.server.base.uri", serverBaseUri);
        jaasConfigurationEntries.put("oauth.server.token.endpoint.path", serverEndpointPath);
        jaasConfigurationEntries.put("oauth.server.introspection.endpoint.path", serverIntrospectionEndpointPath);
        jaasConfigurationEntries.put("oauth.server.client.id", serverClientId);
        jaasConfigurationEntries.put("oauth.server.client.secret", serverClientSecret);
        jaasConfigurationEntries.put("oauth.server.grant.type", serverGrantType);
        jaasConfigurationEntries.put("oauth.server.scopes", serverScopes);
        jaasConfigurationEntries.put("oauth.server.accept.unsecure.server", serverAcceptUnsecureServer);

        OAuthConfiguration oauthConfiguration = new OAuthConfiguration();
        oauthConfiguration.setConfigurationFromJaasConfigEntries(jaasConfigurationEntries);

        assertTrue(oauthConfiguration.getBaseServerUri().equals(serverBaseUri));
        assertTrue(oauthConfiguration.getTokenEndpointPath().equals(serverEndpointPath));
        assertTrue(oauthConfiguration.getIntrospectionEndpointPath().equals(serverIntrospectionEndpointPath));
        assertTrue(oauthConfiguration.getClientId().equals(serverClientId));
        assertTrue(oauthConfiguration.getClientSecret().equals(serverClientSecret));
        assertTrue(oauthConfiguration.getGrantType().equals(serverGrantType));
        assertTrue(oauthConfiguration.getScopes().equals(serverScopes));
        assertFalse(oauthConfiguration.getUnsecureServer());
    }
}
