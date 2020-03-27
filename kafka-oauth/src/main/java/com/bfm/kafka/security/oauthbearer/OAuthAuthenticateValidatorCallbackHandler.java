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

import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

/**
 * A Callback for use by the SaslClient and Validator implementations when they require an OAuth 2 bearer token.
 */
public class OAuthAuthenticateValidatorCallbackHandler extends OAuthAuthenticateCallbackHandler<OAuthBearerValidatorCallback> {
	//region Member Variables

	private final Logger log = LoggerFactory.getLogger(OAuthAuthenticateValidatorCallbackHandler.class);
	private Time time = Time.SYSTEM;


	//endregion

	//region Constructors

	/**
	 * Instantiates a new O auth authenticate validator callback handler.
	 */
	public OAuthAuthenticateValidatorCallbackHandler() {
		super(new OAuthServiceImpl(), OAuthBearerValidatorCallback.class);
	}

	//endregion

	//region Protected Methods

	/**
	 * This callback validates an access token in the OAuth Server
	 * @param callback
	 * @throws IOException - if the access token cannot be validated in the OAuth Server
	 */
	protected void handleCallback(OAuthBearerValidatorCallback callback) throws IOException {
		log.debug("Starting to handle OAuth bearer token validation callback.");

		// make sure that the parameters are valid
		log.debug("Validate method parameters.");
		Objects.requireNonNull(callback);

		String accessToken = callback.tokenValue();
		if (accessToken == null) {
			String errMsg = "Callback missing required token value.";
			log.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		log.debug("Validate access token.");
		OAuthBearerTokenJwt token = this.getOauthService().validateAccessToken(accessToken);

		// the access token is valid, set token on the callback
		if (token == null) {
			log.info("The access token is not valid or has expired.");
		} else {
			log.info("The access token is valid, set token on the callback.");
		}

		callback.token(token);

		log.debug("Finished handling OAuth bearer token validation callback.");
	}

	//endregion
}

