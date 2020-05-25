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
package com.bfm.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

import com.bfm.kafka.security.oauthbearer.EnvironmentVariablesUtil;

public class ProducerCreator {

	public static Producer<Long, String> createProducer() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, EnvironmentVariablesUtil.getStringEnvironmentVariable("BOOTSTRAP_SERVERS", IKafkaConstants.KAFKA_BROKERS));
		props.put(ProducerConfig.CLIENT_ID_CONFIG, IKafkaConstants.CLIENT_ID);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		// OAuth Settings
		//	- sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
		props.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");

		//	- security.protocol=SASL_PLAINTEXT
		props.put("security.protocol", "SASL_PLAINTEXT");

		//	- sasl.mechanism=OAUTHBEARER
		props.put("sasl.mechanism", "OAUTHBEARER");

		//	- sasl.login.callback.handler.class=com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
		props.put("sasl.login.callback.handler.class", "com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler");

		return new KafkaProducer<>(props);
	}
}