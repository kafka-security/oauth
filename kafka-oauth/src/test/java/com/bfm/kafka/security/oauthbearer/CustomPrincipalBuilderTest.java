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

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.auth.SaslAuthenticationContext;
import org.apache.kafka.common.security.auth.SslAuthenticationContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import javax.security.sasl.SaslServer;

import static org.junit.Assert.assertTrue;
@RunWith(MockitoJUnitRunner.class)
public class CustomPrincipalBuilderTest {

	@Mock
	SaslAuthenticationContext saslAuthenticationContext;

	@Mock
	SslAuthenticationContext sslAuthenticationContext;

	@Mock
	SaslServer saslServer;

	@InjectMocks
	CustomPrincipalBuilder customPrincipalBuilder;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void build() {
		OAuthBearerTokenJwt jwt = new OAuthBearerTokenJwt("token", 1, 1, "User");
		Mockito.when(saslAuthenticationContext.server()).thenReturn(saslServer);
		Mockito.when(saslServer.getNegotiatedProperty("OAUTHBEARER.token")).thenReturn(jwt);
		CustomPrincipal customPrincipal = customPrincipalBuilder.build(saslAuthenticationContext);

		assertTrue(customPrincipal != null);
	}

	@Test(expected = KafkaException.class)
	public void buildThrowException() {
		OAuthBearerTokenJwt jwt = new OAuthBearerTokenJwt("token", 1, 1, "User");
		Mockito.when(saslAuthenticationContext.server()).thenReturn(null);

		CustomPrincipal customPrincipal = customPrincipalBuilder.build(saslAuthenticationContext);
	}

	@Test(expected = KafkaException.class)
	public void buildBadType() {
		CustomPrincipal customPrincipal = customPrincipalBuilder.build(sslAuthenticationContext);
	}
}