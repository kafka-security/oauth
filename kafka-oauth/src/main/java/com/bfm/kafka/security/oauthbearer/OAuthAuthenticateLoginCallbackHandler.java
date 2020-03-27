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

import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * A Callback for use by the SaslClient and Login implementations when they require an OAuth 2 bearer token.
 */
public class OAuthAuthenticateLoginCallbackHandler extends OAuthAuthenticateCallbackHandler<OAuthBearerTokenCallback> {
	//region Member Variables

	private final Logger log = LoggerFactory.getLogger(OAuthAuthenticateLoginCallbackHandler.class);

	//endregion

	//region Constructors

	/**
	 * Instantiates a new O auth authenticate login callback handler.
	 */
	public OAuthAuthenticateLoginCallbackHandler() {
		super(new OAuthServiceImpl(), OAuthBearerTokenCallback.class);
	}

	//endregion

	//region Protected Methods

	/**
	 * This method handles attempt to login by requesting an access token from the OAuth Server
	 * @param oauthBearerTokenCallback the oauth bearer token callback
	 * @throws IOException - if OAuth Server did not grant an access token
	 */
	protected void handleCallback(OAuthBearerTokenCallback oauthBearerTokenCallback) throws IOException {
		log.debug("Starting to handle OAuth bearer token login callback.");

		// make sure that the parameters are valid
		log.debug("Validate method parameters.");
		Objects.requireNonNull(oauthBearerTokenCallback);

		// check to see if oauthBearerTokenCallback already had a token
		if (oauthBearerTokenCallback.token() != null) {

			String errMsg = "OAuth bearer token callback already had a token.";

			log.error(errMsg);

			throw new IllegalArgumentException(errMsg);
		}

		// acquire access token
		log.debug("Acquire access token for OAuth bearer token callback.");
		OAuthBearerTokenJwt token = this.getOauthService().requestAccessToken();

		// check to see an access token was returned
		log.debug("Check to see if an access token as returned.");
		if (token == null) {
			String errMsg = "Access token was not returned.";

			log.error(errMsg);

			throw new IllegalArgumentException(errMsg);
		}

		// access token was returned, set the access token on the OAuth bearer token callback
		log.info("Access token was returned, set the access token on the OAuth bearer token callback.");
		oauthBearerTokenCallback.token(token);
		log.debug("Finished handling OAuth bearer token login callback.");
	}

	//endregion
}
