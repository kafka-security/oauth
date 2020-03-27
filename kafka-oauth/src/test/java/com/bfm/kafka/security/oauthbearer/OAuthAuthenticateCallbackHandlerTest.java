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

import org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerTokenCallback;
import org.apache.kafka.common.security.oauthbearer.OAuthBearerValidatorCallback;
import org.junit.Test;
import org.mockito.Mockito;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AppConfigurationEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OAuthAuthenticateCallbackHandlerTest {

	@Test(expected = NullPointerException.class)
	public void constructor_NullOAuthServiceParameter_ThrowsArgumentNullException() {
		// act
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				null
		);
	}

	@Test()
	public void constructor_ValidParameters_ReturnsInitializedObject() {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();


		// act
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		// assert
		assertNotNull(callbackHandler);
	}

	@Test()
	public void getOauthService_ReturnsOAuthService() {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();


		// act
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		// assert
		assertNotNull(callbackHandler.getOauthService());
	}

	@Test(expected = NullPointerException.class)
	public void configure_NullConfigsParameter_ThrowsNullPointerException() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		// act
		callbackHandler.configure(null, null, null);
	}

	@Test(expected = NullPointerException.class)
	public void configure_NullSaslMechanismParameter_ThrowsNullPointerException() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		Map<String, ?> configs = new HashMap<>();

		// act
		callbackHandler.configure(configs, null, null);
	}

	@Test(expected = NullPointerException.class)
	public void configure_NullJaasConfigEntriesParameter_ThrowsNullPointerException() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		Map<String, ?> configs = new HashMap<>();
		String saslMechanism = "Test";

		// act
		callbackHandler.configure(configs, saslMechanism, null);
	}

	@Test()
	public void configure_ValidParameters_CallbackHandlerConfigured() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		Map<String, ?> configs = new HashMap<>();
		String saslMechanism = OAuthBearerLoginModule.OAUTHBEARER_MECHANISM;
		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry("Test", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options));

		// act
		callbackHandler.configure(configs, saslMechanism, jaasConfigEntries);

		// assert
		assertTrue(callbackHandler.isConfigured());
	}

	@Test()
	public void configure_InvalidSaslMechanismParameter_CallbackHandlerIsNotConfigured() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		Map<String, ?> configs = new HashMap<>();
		String saslMechanism = OAuthBearerLoginModule.OAUTHBEARER_MECHANISM;;
		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		// act
		callbackHandler.configure(configs, saslMechanism, jaasConfigEntries);

		// assert
		assertFalse(callbackHandler.isConfigured());
	}

	@Test()
	public void configure_InvalidJaasConfigEntriesParameter_CallbackHandlerIsNotConfigured() {
		//configure(Map<String, ?> configs, String saslMechanism, List<AppConfigurationEntry> jaasConfigEntries

		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService
		);

		Map<String, ?> configs = new HashMap<>();
		String saslMechanism = "Test";
		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry("Test", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options));

		// act
		callbackHandler.configure(configs, saslMechanism, jaasConfigEntries);

		// assert
		assertFalse(callbackHandler.isConfigured());
	}

	@Test(expected = IllegalStateException.class)
	public void handle_NotConfigured_ThrowsIllegalStateException() throws IOException, UnsupportedCallbackException {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService,
				false,
				false
		);

		Map<String, ?> configs = new HashMap<>();
		String saslMechanism = "Test";
		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry(
				"Test",
				AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
				options));

		Callback[] callbacks = new Callback[1];
		callbacks[0] = new OAuthBearerTokenCallback();

		// act
		callbackHandler.handle(callbacks);
	}

	@Test()
	public void handle_Configured_ProcessCallbacks() throws IOException, UnsupportedCallbackException {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService,
				true,
				false
		);

		TestOAuthAuthenticateCallbackHandler callbackHandlerSpy = Mockito.spy(callbackHandler);
		doCallRealMethod().when(callbackHandlerSpy).handle(any());
		doCallRealMethod().when(callbackHandlerSpy).isConfigured();

		Map<String, ?> configs = new HashMap<>();
		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry(
				"Test",
				AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
				options));

		Callback[] callbacks = new Callback[1];
		callbacks[0] = new OAuthBearerTokenCallback();

		// act
		callbackHandlerSpy.handle(callbacks);

		// assert
		verify(callbackHandlerSpy, times(1)).handleCallback(any());
	}

	@Test(expected = IOException.class)
	public void handle_HandleCallbackThrowsException_ThrowsIOException() throws IOException, UnsupportedCallbackException {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService,
				true,
				true
		);

		TestOAuthAuthenticateCallbackHandler callbackHandlerSpy = Mockito.spy(callbackHandler);
		doCallRealMethod().when(callbackHandlerSpy).handle(any());
		doCallRealMethod().when(callbackHandlerSpy).isConfigured();

		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry(
				"Test",
				AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
				options));

		OAuthBearerTokenCallback oauthBearerTokenCallback = new OAuthBearerTokenCallback();

		Callback[] callbacks = new Callback[1];
		callbacks[0] = oauthBearerTokenCallback;

		// act
		callbackHandlerSpy.handle(callbacks);

		// assert
		verify(callbackHandlerSpy, times(1)).handleCallback(any());
	}

	//
	@Test(expected = UnsupportedCallbackException.class)
	public void handle_HandleCallbackThrowsException_ThrowsUnsupportedCallbackException() throws IOException, UnsupportedCallbackException {
		// arrange
		OAuthService oauthService = new OAuthServiceImpl();
		TestOAuthAuthenticateCallbackHandler callbackHandler = new TestOAuthAuthenticateCallbackHandler(
				oauthService,
				true,
				true
		);

		TestOAuthAuthenticateCallbackHandler callbackHandlerSpy = Mockito.spy(callbackHandler);
		doCallRealMethod().when(callbackHandlerSpy).handle(any());
		doCallRealMethod().when(callbackHandlerSpy).isConfigured();

		List<AppConfigurationEntry> jaasConfigEntries = new ArrayList<AppConfigurationEntry>();
		Map<String, ?> options = new HashMap<>();

		jaasConfigEntries.add(new AppConfigurationEntry(
				"Test",
				AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
				options));

		OAuthBearerValidatorCallback oauthBearerValidatorCallback = new OAuthBearerValidatorCallback("Test");

		Callback[] callbacks = new Callback[1];
		callbacks[0] = oauthBearerValidatorCallback;

		// act
		callbackHandlerSpy.handle(callbacks);

		// assert
		verify(callbackHandlerSpy, times(1)).handleCallback(any());
	}

	private class TestOAuthAuthenticateCallbackHandler extends OAuthAuthenticateCallbackHandler<OAuthBearerTokenCallback> {

		private boolean overrideIsConfigured;
		private boolean isConfigured = true;
		private boolean handleCallBackThrow = false;

		public TestOAuthAuthenticateCallbackHandler(OAuthService oauthService) {
			super(oauthService, OAuthBearerTokenCallback.class);
		}

		public TestOAuthAuthenticateCallbackHandler(OAuthService oauthService, boolean isConfigured, boolean handleCallBackThrow) {
			super(oauthService, OAuthBearerTokenCallback.class);
			this.overrideIsConfigured = isConfigured;
			this.isConfigured = isConfigured;
			this.handleCallBackThrow = handleCallBackThrow;
		}

		@Override
		protected void handleCallback(OAuthBearerTokenCallback oauthBearerTokenCallback) throws IOException {
			if (handleCallBackThrow) {
				throw new IOException("Error");
			}
		}

		@Override
		protected boolean isConfigured() {
			if (this.overrideIsConfigured) {
				return this.isConfigured;
			}

			return super.isConfigured();
		}
	}
}
