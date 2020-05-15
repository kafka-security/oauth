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

import java.io.IOException;
import java.util.Map;

/**
 * This interface defines services from an OAuth Server that are needed for lib-kafka-oauth
 */
public interface OAuthService {

    /**
     * Request an access token for an OAuth client from an OAuth Server
     *
     * @return the o auth bearer token jwt
     */
    OAuthBearerTokenJwt requestAccessToken() throws IOException;

    /**
     * Validate an access token in an OAuth Server
     *
     * @param accessToken the access token
     * @return the o auth bearer token jwt
     */
    OAuthBearerTokenJwt validateAccessToken(String accessToken) throws IOException;

    /**
     * Gets configurations that are needed to connect to an OAuth server
     *
     * @return the o auth configuration
     */
    OAuthConfiguration getOAuthConfiguration();

    void setOAuthConfiguration(Map<String, String> jaasConfigEntries);
}