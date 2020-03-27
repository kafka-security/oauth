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

import kafka.network.RequestChannel;
import kafka.security.auth.Operation;
import kafka.security.auth.Resource;
import kafka.security.auth.ResourceType;
import org.apache.kafka.common.acl.AclOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
@RunWith(MockitoJUnitRunner.class)
public class CustomAuthorizerTest {

	@Mock
	RequestChannel.Session session;

	@Mock
	Operation operation;

	@Mock
	Resource resource;

	@Mock
	OAuthBearerTokenJwt jwt;

	@Mock
	CustomPrincipal customPrincipal;

	@InjectMocks
	CustomAuthorizer customAuthorizer;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void authorize() {
		Mockito.when(session.principal()).thenReturn(customPrincipal);
		Mockito.when(customPrincipal.getOauthBearerTokenJwt()).thenReturn(jwt);

		Set<String> set = new HashSet<>();
		set.add("urn:kafka:topic:test:write");

		Mockito.when(jwt.scope()).thenReturn(set);
		Mockito.when(resource.name()).thenReturn("test");
		Mockito.when(resource.resourceType()).thenReturn(ResourceType.fromString("topic"));
		Mockito.when(operation.toJava()).thenReturn(AclOperation.fromString("write"));
		boolean result = customAuthorizer.authorize(session, operation, resource);

		assertTrue(result);
	}

	@Test
	public void checkAuthorization() {
		List<OAuthScope> list = new ArrayList<>();
		OAuthScope scope = new OAuthScope();
		scope.setOperation("Write");
		scope.setResourceName("test");
		scope.setResourceType("topic");
		list.add(scope);

		Resource resource = new Resource(ResourceType.fromString("Topic"), "TEST");

		CustomAuthorizer authorizer = new CustomAuthorizer();

		boolean result = authorizer.checkAuthorization(list, resource, "write");

		assertTrue(result);

	}

	@Test
	public void parseScopes() {
		Set<String> set = new HashSet<>();
		set.add("urn:kafka:topic:test:write");
		set.add("urn:kafka:group:test:read");
		CustomAuthorizer authorizer = new CustomAuthorizer();
		List<OAuthScope> scopes = authorizer.parseScopes(set);

		assertTrue(scopes.size() == 2);
		assertTrue(scopes.get(0).getOperation().equals("write"));
		assertTrue(scopes.get(0).getResourceName().equals("test"));
		assertTrue(scopes.get(0).getResourceType().equals("topic"));
	}

	@Test
	public void parseBadScope() {
		Set<String> set = new HashSet<>();
		set.add("urn:test:write");
		CustomAuthorizer authorizer = new CustomAuthorizer();
		List<OAuthScope> scopes = authorizer.parseScopes(set);

		assertTrue(scopes.size() == 0);
	}
}