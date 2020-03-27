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

/**
 * The type O auth scope.
 */
public class OAuthScope {
	private String resourceType;
	private String resourceName;
	private String operation;

	/**
	 * Instantiates a new O auth scope.
	 */
	public OAuthScope() {

	}

	/**
	 * Gets resource type.
	 *
	 * @return the resource type
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * Sets resource type.
	 *
	 * @param resourceType the resource type
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	/**
	 * Gets resource name.
	 *
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * Sets resource name.
	 *
	 * @param resourceName the resource name
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * Gets operation.
	 *
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Sets operation.
	 *
	 * @param operation the operation
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
}
