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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Utils.
 */
public class Utils {
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	/**
	 * Is uri valid boolean.
	 *
	 * @param str the str
	 * @return the boolean
	 */
	public static Boolean isURIValid(String str) {
		try {
			URI uri = new URI(str);
		} catch (NullPointerException e) {
			// If str is null
			return false;

		} catch (URISyntaxException e) {
			// str is not valid
			return false;
		}

		// the str is valid uri
		return true;
	}

	/**
	 * Is null or empty boolean.
	 *
	 * @param str the str
	 * @return the boolean
	 */
	public static Boolean isNullOrEmpty(String str) {
		if ((str == null) || (str.isEmpty())) {
			return true;
		}
		return false;
	}

	/**
	 * Create basic authorization header string.
	 *
	 * @param clientId     the client id
	 * @param clientSecret the client secret
	 * @return the string
	 */
	protected static String createBasicAuthorizationHeader(String clientId, String clientSecret) {
		try {
			log.debug("Starting to create basic authorization header value.");

			String usernameAndPassword = String.format(
					"%s:%s",
					clientId,
					clientSecret);

			String base64UsernameAndPassword = Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());

			String basicAuthHeader = String.format("Basic %s", base64UsernameAndPassword);

			return basicAuthHeader;
		} catch (Exception ex) {
			String errMsg = String.format(
					"Error creating basic authorization header value., Message: %s",
					ex.getMessage());

			log.error(errMsg, ex);

			throw ex;
		} finally {
			log.debug("Finished creating basic authorization header value.");
		}
	}

	/**
	 * Handle json response map.
	 *
	 * @param inputStream the input stream
	 * @return the map
	 */
	protected static Map<String, Object> handleJsonResponse(InputStream inputStream) {
		// initialize the result
		Map<String, Object> result = null;
		try {
			log.debug("Starting to convert HTTP JSON response into a key value pairs.");

			// check parameters
			log.debug("Validate method parameters.");
			Objects.requireNonNull(inputStream);

			// read the response into a string
			log.debug("Read the HTTP response into a string.");
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			String jsonResponse = response.toString();

			// parse the response into a key value pairs
			log.debug("Parse JSON string into a key value pairs.");
			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {
			});
		} catch (NullPointerException npe) {
			log.error("Error converting HTTP JSON response, null pointer exception, Message: {}.", npe.getMessage());
			throw npe;
		} catch (Exception ex) {
			log.error("Error converting HTTP JSON response into a key value pairs, Message: {}.", ex.getMessage());
		} finally {
			log.debug("Finished converting HTTP JSON response into a key value pairs");
		}
		return result;
	}

	/**
	 * Handle array response list.
	 *
	 * @param inputStream the input stream
	 * @return the list
	 */
	protected static List<Object> handleArrayResponse(InputStream inputStream) {
		// initialize the result
		List<Object>result = null;
		try {

			// check parameters
			log.debug("Validate method parameters.");
			Objects.requireNonNull(inputStream);

			// read the response into a string
			log.debug("Read the HTTP response into a string.");
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			String jsonResponse = response.toString();

			ObjectMapper objectMapper = new ObjectMapper();
			result = objectMapper.readValue(jsonResponse, new TypeReference<List<Object>>() {
			});
		} catch (NullPointerException npe) {
			log.error("Error converting HTTP JSON response, null pointer exception, Message: {}.", npe.getMessage());
			throw npe;
		} catch (Exception ex) {
			log.error("Error converting HTTP JSON response into a key value pairs, Message: {}.", ex.getMessage());
		} finally {
			log.debug("Finished converting HTTP JSON response into a key value pairs");
		}
		return result;
	}

	/**
	 * Accept unsecure server.
	 *
	 * @param unsecure the unsecure
	 */
	protected static void acceptUnsecureServer(boolean unsecure) {
		try {
			log.debug("Starting to enable unsecure HTTP connections.");

			if (!unsecure) {
				// unsecured servers are not allowed
				return;
			}

			// configure SSL context to allow unsecured servers
			TrustManager[] trustAllCerts = new TrustManager[]{
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}

						public void checkClientTrusted(
								java.security.cert.X509Certificate[] certs, String authType) {
						}

						public void checkServerTrusted(
								java.security.cert.X509Certificate[] certs, String authType) {
						}
					}
			};


			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
		} catch (NoSuchAlgorithmException e) {
			String errMsg = String.format("Error enabling unsecure HTTP connections, Message: %s", e.getMessage());
			log.error(errMsg, e);
		} catch (KeyManagementException e) {
			String errMsg = String.format("Error enabling unsecure HTTP connections, Message: %s", e.getMessage());
			log.error(errMsg, e);
		} catch (Exception e) {
			String errMsg = String.format("Error enabling unsecure HTTP connections, Message: %s", e.getMessage());
			log.error(errMsg, e);
		} finally {
			log.debug("Finished enabling unsecure HTTP connections.");
		}
	}

	/**
	 * Create bearer header string.
	 *
	 * @param accessToken the access token
	 * @return the string
	 */
	public static String createBearerHeader(String accessToken) {
		return "Bearer " + accessToken;
	}
}
