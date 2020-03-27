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

import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.security.auth.AuthenticationContext;
import org.apache.kafka.common.security.auth.KafkaPrincipalBuilder;
import org.apache.kafka.common.security.auth.SaslAuthenticationContext;

/**
 * The type Custom principal builder.
 */
public class CustomPrincipalBuilder implements KafkaPrincipalBuilder {
	@Override
	public CustomPrincipal build(AuthenticationContext authenticationContext) throws KafkaException{
		try {
			CustomPrincipal customPrincipal;

			if (authenticationContext instanceof SaslAuthenticationContext) {
				SaslAuthenticationContext context = (SaslAuthenticationContext) authenticationContext;
				OAuthBearerTokenJwt token = (OAuthBearerTokenJwt) context.server()
						.getNegotiatedProperty("OAUTHBEARER.token");

				customPrincipal = new CustomPrincipal("User", token.principalName());
				customPrincipal.setOauthBearerTokenJwt(token);

				return customPrincipal;
			} else {
				throw new KafkaException("Failed to build CustomPrincipal. SaslAuthenticationContext is required.");
			}
		} catch (Exception ex) {
			throw new KafkaException("Failed to build CustomPrincipal due to: ", ex);
		}
	}
}
