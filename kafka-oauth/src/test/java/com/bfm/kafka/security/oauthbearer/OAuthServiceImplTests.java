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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * The type O auth service impl tests.
 */
@RunWith(MockitoJUnitRunner.class)
public class OAuthServiceImplTests {

    //region Tests

    /**
     * Constructor initialized object created.
     */
    @Test()
    public void constructor_InitializedObjectCreated() {
        // act
        OAuthServiceImpl oauthServiceImpl = new OAuthServiceImpl();

        // assert
		assertNotNull(oauthServiceImpl.getOAuthConfiguration());
    }

    /**
     * Request access token http call does not return response returns null.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void requestAccessToken_HttpCallDoesNotReturnResponse_ReturnsNull() throws IOException {
        // arrange
        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Mockito.doReturn(null).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());

        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.requestAccessToken();

        // assert
        assertNull(oAuthBearerTokenJwt);
    }

    /**
     * Request access token http call does return response returns null.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void requestAccessToken_HttpCallDoesReturnResponse_ReturnsNull() throws IOException {
        // arrange
        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Mockito.doReturn(null).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());

        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.requestAccessToken();

        // assert
        assertNull(oAuthBearerTokenJwt);
    }

    /**
     * Request access token http call does return response returns valid object.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void requestAccessToken_HttpCallDoesReturnResponse_ReturnsValidObject() throws IOException {
        // arrange
        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Map<String, Object> response = new HashMap<>();
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN, "test-client-id");
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN_EXPIRES_IN, 12);
        Mockito.doReturn(response).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());



        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.requestAccessToken();

        // assert
        assertNotNull(oAuthBearerTokenJwt);
    }

    /**
     * Validate access token http call returns response returns null.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void validateAccessToken_HttpCallReturnsResponse_ReturnsNull() throws IOException {
        // arrange
        Map<String, Object> response = new HashMap<>();
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN, "test-client-id");
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN_EXPIRES_IN, 12);
        response.put("active", false);

        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Mockito.doReturn(response).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());

        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.validateAccessToken("test");

        // assert
        assertNull(oAuthBearerTokenJwt);
    }

    /**
     * Validate access token http call does not return response returns null.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void validateAccessToken_HttpCallDoesNotReturnResponse_ReturnsNull() throws IOException {
        // arrange
        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Mockito.doReturn(null).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());

        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.validateAccessToken("test");

        // assert
        assertNull(oAuthBearerTokenJwt);
    }

    /**
     * Validate access token http call returns response returns valid object.
     *
     * @throws IOException the io exception
     */
    @Test()
    public void validateAccessToken_HttpCallReturnsResponse_ReturnsValidObject() throws IOException {
        // arrange
        Map<String, Object> response = new HashMap<>();
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN, "test-client-id");
        response.put(OAuthServiceImpl.OAUTH_ACCESS_TOKEN_EXPIRES_IN, 12);
        response.put("active", true);
        response.put("jti", "");
        response.put("iat", 1);
        response.put("exp", 1);

        OAuthServiceImpl oauthServiceImplSpy = Mockito.spy(new OAuthServiceImpl());
        Mockito.doReturn(response).when(oauthServiceImplSpy).doHttpCall(anyString(), anyString(), anyString());

        // act
        OAuthBearerTokenJwt oAuthBearerTokenJwt = oauthServiceImplSpy.validateAccessToken("test");

        // assert
        assertNotNull(oAuthBearerTokenJwt);
    }

    //endregion
}
