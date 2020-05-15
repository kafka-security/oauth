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

import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * The class that handles the logic to interact with the OAuth server.
 */
public class OAuthServiceImpl implements OAuthService {

	//region Constants

	public static final String OAUTH_GRANT_TYPE = "grant_type";
	public static final String OAUTH_SCOPE = "scope";
	public static final String OAUTH_ACCESS_TOKEN = "access_token";
	public static final String OAUTH_ACCESS_TOKEN_EXPIRES_IN = "expires_in";

	//endregion

	//region Member Variables

	private static final Logger log = LoggerFactory.getLogger(OAuthServiceImpl.class);
	private OAuthConfiguration oauthConfiguration;
	private static Time time = Time.SYSTEM;

	//endregion

	//region Constructors

	/**
	 * Instantiates a new O auth service.
	 */
	public OAuthServiceImpl() {
		this.oauthConfiguration = new OAuthConfiguration();
	}

	//endregion

	//region Public Properties

	public OAuthConfiguration getOAuthConfiguration() {
		return this.oauthConfiguration;
	}

	@Override
	public void setOAuthConfiguration(Map<String, String> jaasConfigEntries) {
		try {
			//validate parameters
			Objects.requireNonNull(jaasConfigEntries);
			this.oauthConfiguration.setConfigurationFromJaasConfigEntries(jaasConfigEntries);
		} catch (RuntimeException e) {
			log.warn("Error on trying to configure oauth using jaas configuration entries. Using environment or properties file configuration");
		}
	}

	//endregion

	//region Public Methods

	/**
	 * @return OAuthBearerTokenJwt which has accessToken string as an attribute of the object
	 */
	/**
	 * This method gets a JWT token from an OAuth Server.
	 * The JWT token contains the access token string.
	 * @return a JWT token if client ID exist in the OAuth Server or null if not exist in the OAuth Server
	 * @throws IOException - if Call to OAuth Server fails
	 */
	public OAuthBearerTokenJwt requestAccessToken() throws IOException {
		OAuthBearerTokenJwt result = null;
		log.debug("Starting to request access token from OAuth server.");

		String clientId = this.oauthConfiguration.getClientId();

		// initialize the time of the request
		long callTime = time.milliseconds();

		// setup post parameters
		String grantType = String.format("%s=%s", OAUTH_GRANT_TYPE, this.oauthConfiguration.getGrantType());
		String scope = String.format("%s=%s", OAUTH_SCOPE, this.oauthConfiguration.getScopes());
		String postParameters = String.format("%s&%s", grantType, scope);

		log.info("Send access token request to the OAuth server.");
		Map<String, Object> resp = doHttpCall(
				this.oauthConfiguration.getTokenEndpoint(),
				postParameters,
				Utils.createBasicAuthorizationHeader(clientId, this.oauthConfiguration.getClientSecret()));

		// check to see if the response is not null
		if (resp != null) {
			// create a new token from the response
			log.debug("Access token response is not null, create an token.");
			String accessToken = (String) resp.get(OAUTH_ACCESS_TOKEN);
			long expiresIn = ((Integer) resp.get(OAUTH_ACCESS_TOKEN_EXPIRES_IN)).longValue();
			result = new OAuthBearerTokenJwt(accessToken, expiresIn, callTime, clientId);
		} else {
			log.error("Error requesting access token from OAuth server, the HTTP response was null.");
		}
		log.debug("Finished requesting access token from OAuth server.");
		return result;
	}

	/**
	 * This method vaidates an access token string in the OAuth Server
	 * @param accessToken the access token string
	 * @return a JWT token if accessToken exists in the OAuth Server, or null if not exist in the OAuth Server
	 * @throws IOException - if Call to OAuth Server fails
	 */
	public OAuthBearerTokenJwt validateAccessToken(String accessToken) throws IOException {
		OAuthBearerTokenJwt result = null;
		log.debug("Starting to validate access token against OAuth server.");

		// check parameters
		log.debug("Validate method parameters.");
		Objects.requireNonNull(accessToken);

		// create post parameters
		String token = "token=" + accessToken;

		// validate the access token by calling the oauth introspection endpoint
		log.debug("Validate the access token by calling the OAuth introspection endpoint.");
		Map<String, Object> resp = doHttpCall(
				this.oauthConfiguration.getIntrospectionEndpoint(),
				token,
				Utils.createBasicAuthorizationHeader(this.oauthConfiguration.getClientId(), this.oauthConfiguration.getClientSecret()));

		// check to see if the response is not null - accessToken exists in the OAuth Server
		if (resp != null) {
			// check to see if the access token is still active
			log.debug("Validation response was not null check to see if the access token is active.");
			boolean active = (boolean) resp.get("active");

			if (active) {
				// the access token is still active create a new token with the response
				log.debug("Access token is still active create a new token with the response.");
				result = new OAuthBearerTokenJwt(resp, accessToken);
			} else {
				// the access token is no longer active
				String errMsg = String.format("Access token has expired.");
				log.error(errMsg);
			}
		} else { // accessToken does not exist in the OAuth Server
			// the http response was null, cannot validate access token
			String errMsg = "Error validating access token against OAuth server, the HTTP response was null.";
			log.error(errMsg);
		}
		log.debug("Finished validating access token against OAuth server.");
		return result;
	}

	//endregion

	//region Protected Methods

	/**
	 * Do http call to the OAuth Server.
	 * @param urlStr OAuth Server URL
	 * @param postParameters
	 * @param authorizationHeaderValue
	 * @return null if HTTP response code is not 200
	 * @throws IOException
	 */
	protected Map<String, Object> doHttpCall(String urlStr, String postParameters, String authorizationHeaderValue) throws IOException {
		log.debug(String.format("Starting to make HTTP call, Url: %s.", urlStr));

		// check parameters
		log.debug("Validate method parameters.");
		Objects.requireNonNull(urlStr);
		Objects.requireNonNull(postParameters);
		//Objects.requireNonNull(authorizationHeaderValue);

		// configure SSL context to allow unsecured connections if configured
		log.debug("Configure SSL context to allow unsecured connections if configured.");
		Utils.acceptUnsecureServer(this.oauthConfiguration.getUnsecureServer());

		log.debug(String.format("Send POST request, Url: %s.", urlStr));
		byte[] postData = postParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;

		URL url = new URL(urlStr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setInstanceFollowRedirects(true);
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", authorizationHeaderValue);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("charset", "utf-8");
		con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		con.setUseCaches(false);
		con.setDoOutput(true);

		try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
			wr.write(postData);
		}

		log.debug(String.format("Get HTTP response code, Url: %s.", urlStr));
		int responseCode = con.getResponseCode();

		// check to see if the response was successful
		log.debug(String.format("Check to see if the response was successful, Url: %s.", urlStr));
		if (responseCode == 200) {
			// the response was successful, parse to json into a key value pairs
			log.debug("The response was successful, parse to json into a key value pairs, Url: {}.", urlStr);
			return Utils.handleJsonResponse(con.getInputStream());
		} else {
			// the response was not successful
			String errMsg = String.format(
					"The response was not successful, Url: %s, Response Code: %s",
					urlStr,
					responseCode);

			log.error(errMsg);
			return null;

		}
	}

	//endregion
}
