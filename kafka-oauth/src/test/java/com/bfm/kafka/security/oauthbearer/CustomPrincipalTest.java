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

import static org.junit.Assert.assertTrue;

public class CustomPrincipalTest {

	@Test
	public void testGettersSetters() {
		CustomPrincipal customPrincipal = new CustomPrincipal("User", "TEST");
		OAuthBearerTokenJwt jwt = new OAuthBearerTokenJwt("token", 1, 1, "User");
		customPrincipal.setOauthBearerTokenJwt(jwt);

		assertTrue(customPrincipal.getOauthBearerTokenJwt().equals(jwt));
		assertTrue(customPrincipal.getPrincipalType().equals("User"));
		assertTrue(customPrincipal.getName().equals("TEST"));
	}

	@Test
	public void toStringTest() {
		CustomPrincipal customPrincipal = new CustomPrincipal("User", "TEST");
		OAuthBearerTokenJwt jwt = new OAuthBearerTokenJwt("token", 1, 1, "User");
		customPrincipal.setOauthBearerTokenJwt(jwt);

		assertTrue(customPrincipal.toString().equals("CustomPrincipal{oauthBearerTokenJwt=OauthBearerTokenJwt {value='token', lifetimeMs=1001, " +
				"principalName='User', startTimeMs=1, scope=null, expirationTime=1001, jti='null'}} User:TEST"));

	}
}