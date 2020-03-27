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

import org.apache.kafka.common.security.oauthbearer.OAuthBearerToken;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The b64token value as defined in RFC 6750 Section 2.1 along with the token's specific scope and lifetime and
 * principal name.
 */
public class OAuthBearerTokenJwt implements OAuthBearerToken {

    //region Constants

    private static final String SUB = "sub";
    private static final String SCOPE = "scope";
    private static final String EXP = "exp";
    private static final String IAT = "iat";
    private static final String JTI = "jti";

    //endregion

    //region Member Variables

    private String accessToken;
    private long lifetimeMs;
    private String principalName;
    private Long startTimeMs;
    private Set<String> scope;
    private long expirationTime;
    private String jti;

    //endregion

    //region Constructors

    /**
     * Initializes a new instance of the OAuthBearerTokenJwt class.
     *
     * @param accessToken   The b64token value as defined in RFC 6750 Section 2.1
     * @param lifeTimeMs    The token's lifetime, expressed as the number of milliseconds since the epoch, as per RFC 6749 Section 1.4
     * @param startTimeMs   When the credential became valid, in terms of the number of milliseconds since the epoch, if known, otherwise null.
     * @param principalName The name of the principal to which this credential applies
     */
    public OAuthBearerTokenJwt(String accessToken, long lifeTimeMs, long startTimeMs, String principalName) {
        super();

        this.accessToken = accessToken;
        this.principalName = principalName;
        this.lifetimeMs = startTimeMs + (lifeTimeMs * 1000);
        this.startTimeMs = startTimeMs;
        this.expirationTime = startTimeMs + (lifeTimeMs * 1000);
    }

    /**
     * Initializes a new instance of the OAuthBearerTokenJwt class based on a collection of key value pairs
     *
     * @param jwtToken    - A JWT Token expressed as a key value pair
     * @param accessToken The b64token value as defined in RFC 6750 Section 2.1
     */
    public OAuthBearerTokenJwt(Map<String, Object> jwtToken, String accessToken) {
        super();
        this.accessToken = accessToken;
        this.principalName = (String) jwtToken.get(SUB);

        if (this.scope == null) {
            this.scope = new TreeSet<>();
        }

        if (jwtToken.get(SCOPE) instanceof String) {
            this.scope.add((String) jwtToken.get(SCOPE));
        } else if (jwtToken.get(SCOPE) instanceof List) {
            for (String s : (List<String>) jwtToken.get(SCOPE)) {
                this.scope.add(s);
            }
        }

        Object exp = jwtToken.get(EXP);
        if (exp instanceof Integer) {
            this.expirationTime = Integer.toUnsignedLong((Integer) jwtToken.get(EXP));
        } else {
            this.expirationTime = (Long) jwtToken.get(EXP);
        }

        Object iat = jwtToken.get(IAT);
        if (iat instanceof Integer) {
            this.startTimeMs = Integer.toUnsignedLong((Integer) jwtToken.get(IAT));
        } else {
            this.startTimeMs = (Long) jwtToken.get(IAT);
        }

        this.lifetimeMs = expirationTime * 1000;
        this.jti = (String) jwtToken.get(JTI);
    }

    //endregion

    //region Public Properties

    @Override
    public String value() {
        return this.accessToken;
    }

    @Override
    public Set<String> scope() {
        return this.scope;
    }

    @Override
    public long lifetimeMs() {
        return this.lifetimeMs;
    }

    @Override
    public String principalName() {
        return this.principalName;
    }

    @Override
    public Long startTimeMs() {
        return this.startTimeMs != null ? this.startTimeMs : 0;
    }

    public long expirationTime() {
        return this.expirationTime;
    }

    //endregion

    //region Public Methods

    @Override
    public String toString() {
        return "OauthBearerTokenJwt {" +
                "value='" + accessToken + '\'' +
                ", lifetimeMs=" + lifetimeMs +
                ", principalName='" + principalName + '\'' +
                ", startTimeMs=" + startTimeMs +
                ", scope=" + scope +
                ", expirationTime=" + expirationTime +
                ", jti='" + jti + '\'' +
                '}';
    }

    //endregion
}