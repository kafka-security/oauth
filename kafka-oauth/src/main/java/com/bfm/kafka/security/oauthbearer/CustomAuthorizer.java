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

import kafka.network.RequestChannel.Session;
import kafka.security.auth.Acl;
import kafka.security.auth.Authorizer;
import kafka.security.auth.Operation;
import kafka.security.auth.Resource;
import org.apache.kafka.common.security.auth.KafkaPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Oauth authorizer.
 */
public class CustomAuthorizer implements Authorizer {
	private static final Logger log = LoggerFactory.getLogger(CustomAuthorizer.class);

	/**
	 * Instantiates a new Custom authorizer.
	 */
	public CustomAuthorizer() {
	}

	/**
	 * Check scopes from JWT to validate topic/resource operations.
	 *
	 * @param session   session info
	 * @param operation Kafka operation
	 * @param resource  resource being operated one
	 * @return true/false
	 */
	@Override
	public boolean authorize(Session session, Operation operation, Resource resource) {
		try {
			log.info("Starting Authorization.");
			// log.info("Session Info: {}", session.toString());
			log.info("Operation request Info: {}", operation.toString());
			log.info("Resource request Info: {}", resource.toString());
			if (!(session.principal() instanceof CustomPrincipal)) {
				log.error("Session Principal is not using the proper class. Should be instance of CustomPrincipal.");
				return false;
			}

			CustomPrincipal principal = (CustomPrincipal) session.principal();
			if (principal.getOauthBearerTokenJwt() == null) {
				log.error("Custom Principal does not contain token information.");
				return false;
			}

			OAuthBearerTokenJwt jwt = principal.getOauthBearerTokenJwt();
			if (jwt.scope() == null || jwt.scope().isEmpty()) {
				log.error("No scopes provided in JWT. Unable to Authorize.");
				return false;
			}

			java.util.Set<String> scopes = jwt.scope();
			List<OAuthScope> scopeInfo = parseScopes(scopes);
			String operationStr = operation.toJava().toString();
			return checkAuthorization(scopeInfo, resource, operationStr);
		} catch (Exception e) {
			log.error("Error in authorization. ", e);
		}
		return false;
	}

	/**
	 * Check authorization against scopes.
	 *
	 * @param scopeInfo list of scopes
	 * @param resource  resource info
	 * @param operation operation performed
	 * @return true /false
	 */
	protected boolean checkAuthorization(List<OAuthScope> scopeInfo, Resource resource, String operation) {
		for (int i = 0; i < scopeInfo.size(); i++) {
			OAuthScope scope = scopeInfo.get(i);
			String lowerCaseOperation = operation.toLowerCase();
			String lowerCaseResourceName = resource.name().toLowerCase();
			String lowerCaseCaseResourceType = resource.resourceType().toString().toLowerCase();

			boolean operationVal = scope.getOperation().toLowerCase().equals(lowerCaseOperation);
			boolean nameVal = scope.getResourceName().toLowerCase().equals(lowerCaseResourceName);
			boolean typeVal = scope.getResourceType().toLowerCase().equals(lowerCaseCaseResourceType);

			if (operationVal && nameVal && typeVal) {
				log.info("Successfully Authorized.");
				return true;
			}
		}
		log.info("Not Authorized to operate on the given resource.");
		return false;
	}

	/**
	 * Parse topic and Operation out of scope.
	 *
	 * @param scopes set of scopes
	 * @return return list of pairs, each pair is a topic/operation <p> Scope format urn:kafka:<resourceType>:<resourceName>:<operation>
	 */
	protected List<OAuthScope> parseScopes(java.util.Set<String> scopes) {
		List<OAuthScope> result = new ArrayList<>();
		for (String scope : scopes) {
			String[] scopeArray = scope.split("\\s+");
			for (String str : scopeArray){
				convertScope(result, str);
			}
		}
		return result;
	}

	/**
	 * convertScope.
	 * @param result list of scopesInfo
	 * @param scope string of scope
	 */
	private void convertScope(List<OAuthScope> result, String scope) {
		String[] str = scope.split(":");
		if (str.length == 5) {
			String type = str[2];
			String name = str[3];
			String operation = str[4];
			OAuthScope oAuthScope = new OAuthScope();
			oAuthScope.setOperation(operation);
			oAuthScope.setResourceName(name);
			oAuthScope.setResourceType(type);
			result.add(oAuthScope);
		} else {
			log.error("Unable to parse scope. Incorrect format: {}.", scope);
		}
	}

	@Override
	public void addAcls(Set<Acl> acls, Resource resource) {

	}

	@Override
	public boolean removeAcls(Set<Acl> acls, Resource resource) {
		return false;
	}

	@Override
	public boolean removeAcls(Resource resource) {
		return false;
	}

	@Override
	public Set<Acl> getAcls(Resource resource) {
		return null;
	}

	@Override
	public Map<Resource, Set<Acl>> getAcls(KafkaPrincipal principal) {
		return null;
	}

	@Override
	public Map<Resource, Set<Acl>> getAcls() {
		return null;
	}

	@Override
	public void close() {

	}


	@Override
	public void configure(java.util.Map<String, ?> map) {

	}
}
