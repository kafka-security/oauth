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

import org.apache.kafka.common.security.auth.AuthenticateCallbackHandler;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * As the implementation of the Kafka AuthenticateCallbackHandler interface, this class is an abstract callback handler
 * that allows subclasses to reuse common logic and to inject custom functions
 *
 * @param <T> the generic type for callback functions
 */
public abstract class OAuthAuthenticateCallbackHandler<T> implements AuthenticateCallbackHandler {
	//region Member Variables

	private final Logger log = LoggerFactory.getLogger(OAuthAuthenticateCallbackHandler.class);
	private OAuthService oauthService;
	private boolean configured = false;
	private Map<String, String> moduleOptions = null;
	private List<AppConfigurationEntry> jaasConfigEntries;
	private Class callBackHandlerClass;

	//endregion

	//region Constructors

	/**
	 * Instantiates a new O auth authenticate callback handler.
	 *
	 * @param oauthService         the oauth service
	 * @param callBackHandlerClass the call back handler class
	 */
	public OAuthAuthenticateCallbackHandler(OAuthService oauthService, Class callBackHandlerClass) {
		// validate the parameters
		Objects.requireNonNull(oauthService);
		Objects.requireNonNull(callBackHandlerClass);

		// initialize member variables
		this.oauthService = oauthService;
		this.callBackHandlerClass = callBackHandlerClass;
	}

	//endregion

	//region Protected Properties

	/**
	 * Gets oauth service.
	 *
	 * @return the oauth service
	 */
	protected OAuthService getOauthService() {
		return this.oauthService;
	}

	/**
	 * Is configured boolean.
	 *
	 * @return the boolean
	 */
	protected boolean isConfigured() {
		return this.configured;
	}

	//endregion

	//region Public Methods

	/**
	 * Configures this callback handler for the specified SASL mechanism.
	 *
	 * @param configs           Key-value pairs containing the parsed configuration options of the client or broker.
	 *                          Note that these are the Kafka configuration options and not the JAAS configuration
	 *                          options. JAAS config options may be obtained from `jaasConfigEntries` for callbacks
	 *                          which obtain some configs from the JAAS configuration. For configs that may be specified
	 *                          as both Kafka config as well as JAAS config (e.g. sasl.kerberos.service.name), the
	 *                          configuration is treated as invalid if conflicting values are provided.
	 * @param saslMechanism     Negotiated SASL mechanism. For clients, this is the SASL mechanism configured for the
	 *                          client. For brokers, this is the mechanism negotiated with the client and is one of the
	 *                          mechanisms enabled on the broker.
	 * @param jaasConfigEntries JAAS configuration entries from the JAAS login context. This list contains a single
	 *                          entry for clients and may contain more than one entry for brokers if multiple mechanisms
	 *                          are enabled on a listener using static JAAS configuration where there is no mapping
	 *                          between mechanisms and login module entries. In this case, callback handlers can use
	 *                          the login module in `jaasConfigEntries` to identify the entry corresponding to
	 *                          `saslMechanism`. Alternatively, dynamic JAAS configuration option
	 *                          SaslConfigs.SASL_JAAS_CONFIG may be configured on brokers with listener and mechanism
	 *                          prefix, in which case only the configuration entry corresponding to `saslMechanism` will
	 *                          be provided in `jaasConfigEntries`.
	 */
	@Override
	public void configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries) {
		log.debug("Starting to configure OAuth authentication callback handler.");

		log.debug("Validate method parameters.");
		Objects.requireNonNull(configs);
		Objects.requireNonNull(saslMechanism);
		Objects.requireNonNull(jaasConfigEntries);
		String errMsg;

		// validate the SASL Mechanism is set correctly
		if (OAuthBearerLoginModule.OAUTHBEARER_MECHANISM.equals(saslMechanism)) {
			if (jaasConfigEntries.size() == 1 && jaasConfigEntries.get(0) != null) {
				this.moduleOptions = Collections.unmodifiableMap(
						(Map<String, String>) jaasConfigEntries.get(0).getOptions());
				//Configure OAuthService using the JAAS file properties
				this.oauthService.setOAuthConfiguration(moduleOptions);
				// the handle has been configured
				configured = true;
			} else {
				errMsg = String.format(
						"Must supply exactly 1 non-null JAAS mechanism configuration (size was %d)",
						jaasConfigEntries.size());

				log.error(errMsg);
			}
		} else {
			errMsg = String.format("Unexpected SASL mechanism: %s", saslMechanism);

			log.error(errMsg);
		}
	}

	/**
	 * Handle OAuth bearer token callbacks which invokes the generic function provided by the sub classs
	 * <p>
	 * The handle method implementation checks the instance(s) of the Callback object(s) passed in to retrieve or
	 * display the requested information.
	 *
	 * @param callbacks An array of Callback objects provided by an underlying security service which contains the
	 *                  information requested to be retrieved or displayed.
	 * @throws IOException                  If the generic callback function throws an Exception
	 * @throws UnsupportedCallbackException If the input callbackType does not match the subclass
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		log.debug("Starting to handle OAuth bearer token login callbacks.");

		// check to see if the handler has been configured
		log.debug("Check to see of the handler has been configured.");
		if (!isConfigured()) {
			String errMsg = "Callback handler not configured";

			log.error(errMsg);

			throw new IllegalStateException(errMsg);
		}

		// loop through all of the callbacks looking for the OAuth bearer token callbacks
		log.debug("Loop through all of the callbacks looking for the OAuth bearer token callbacks.");
		for (Callback callback : callbacks) {
			// check the type of the callback
			log.debug("Check the type of the callback.");
			Class callBackType = callback.getClass();
			if (callBackType.equals(this.callBackHandlerClass)) {
				// the callback is the correct type for this handler, process the callback
				log.debug("The callback is the correct type of this handler, process the callback.");
				try {
					T oauthBearerTokenCallback = (T) callback;
					handleCallback(oauthBearerTokenCallback);
				} catch (Exception e) { // handle exception here so that we can log exception where it happens
					String errMsg = String.format(
							"Error handing OAuth bearer token login callback, Message: %s",
							e.getMessage());

					log.error(errMsg);

					throw new IOException(errMsg, e);
				}
			} else {
				// the callback is not the correct type for this handler, throw a exception
				String errMsg = String.format(
						"Callback is not a OAuthBearerTokenCallback, Type: %s",
						callback);
				log.debug(errMsg);

				throw new UnsupportedCallbackException(callback);
			}
		}
		log.debug("Finished handling OAuth bearer token login callbacks.");
	}

	/**
	 * Implementation of the interface.
	 * This class does not have resource dependent attributes, therefore this method is empty.
	 */
	@Override
	public void close() {
	}

	//endregion

	//region Protected Methods

	/**
	 * Handle callback and should be implemented by sub classes
	 *
	 * @param oauthBearerTokenCallback the oauth bearer token callback
	 */
	protected abstract void handleCallback(T oauthBearerTokenCallback) throws IOException;

	//endregion
}
