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

import org.apache.kafka.common.security.auth.KafkaPrincipal;

/**
 * The type Custom principal.
 */
public class CustomPrincipal extends KafkaPrincipal {

	private OAuthBearerTokenJwt oauthBearerTokenJwt;

	/**
	 * Instantiates a new Custom principal.
	 *
	 * @param principalType the principal type
	 * @param name          the name
	 */
	public CustomPrincipal(String principalType, String name) {
		super(principalType, name);
	}

	/**
	 * Gets oauth bearer token jwt.
	 *
	 * @return the oauth bearer token jwt
	 */
	public OAuthBearerTokenJwt getOauthBearerTokenJwt() {
		return oauthBearerTokenJwt;
	}

	/**
	 * Sets oauth bearer token jwt.
	 *
	 * @param oauthBearerTokenJwt the oauth bearer token jwt
	 */
	public void setOauthBearerTokenJwt(OAuthBearerTokenJwt oauthBearerTokenJwt) {
		this.oauthBearerTokenJwt = oauthBearerTokenJwt;
	}

	@Override
	public String toString() {
		return "CustomPrincipal{" +
				"oauthBearerTokenJwt=" + oauthBearerTokenJwt +
				"} " + super.toString();
	}
}