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

# Kafka OAuth Library
 - Provides Authentication and Authorization via OAuth/OIDC
 
## Kafka Broker Configuration

#### Download Apache Kafka
- https://kafka.apache.org/downloads
- Quick Tutorial on Kafka
    - https://kafka.apache.org/quickstart
    - Use this tutorial to get the Kafka running without any modifications
    - Once that is complete move on to adding the OAuth configuration to Kafka!

#### Config Files
- Create a properties file for your Broker OAuth client {broker-configuration.properties}.
- The file should contain the following properties:
- NOTES: 
    - you will need to update the oauth.server.client.secret to be your client secret!!!!!
    - you can pass this properties under the kafka_server_jaas.conf
        ```
        oauth.server.base.uri=http://localhost:8080/auth/realms/master/protocol/openid-connect
        oauth.server.token.endpoint.path=/token
        oauth.server.introspection.endpoint.path=/token/introspect
        oauth.server.client.id=kafka-broker
        oauth.server.client.secret=4ce54cfb-f359-4400-bd8e-810a1af10f71
        oauth.server.grant.type=client_credentials
        oauth.server.scopes=test
        oauth.server.accept.unsecure.server=true
         # oauth.server.accept.unsecure.server - this property is for SSL configuration, if you are using HTTP or a self-signed CERT set this true
        ```

- Create a config file for your JAAS security {kafka_server_jaas.conf}
    - The file must contain the following:
    - NOTE: the properties started by oauth.server only needed if you don't want to user the broker-configuration.properties file!
    
            KafkaServer {
                org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required 
                oauth.server.base.uri="http://localhost:8080/auth/realms/master/protocol/openid-connect"
                oauth.server.token.endpoint.path="/token"
                oauth.server.introspection.endpoint.path="/token/introspect"
                oauth.server.client.id="kafka-broker"
                oauth.server.client.secret="4ce54cfb-f359-4400-bd8e-810a1af10f71"
                oauth.server.grant.type="client_credentials"
                oauth.server.scopes="test"
                oauth.server.accept.unsecure.server="true";
            };


- Update the Kafka server.properties file.
    - The original can be found in the kafka config folder, add these properties to the end of the file:
    

            # The following were added to test OAuth Bearer SASL
            listeners=SASL_PLAINTEXT://localhost:9092
            advertised.listeners=SASL_PLAINTEXT://localhost:9092
            listener.name.sasl_plaintext.oauthbearer.sasl.login.callback.handler.class=com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
            listener.name.sasl_plaintext.oauthbearer.sasl.server.callback.handler.class=com.bfm.kafka.security.oauthbearer.OAuthAuthenticateValidatorCallbackHandler
            
            ############################# Security/SASL Settings #############################
            security.inter.broker.protocol=SASL_PLAINTEXT
            sasl.mechanism.inter.broker.protocol=OAUTHBEARER
            sasl.enabled.mechanisms=OAUTHBEARER
            connections.max.reauth.ms=60000
            
            
            
            ############## Authorizer ###############
            authorizer.class.name=com.bfm.kafka.security.oauthbearer.CustomAuthorizer
            principal.builder.class=com.bfm.kafka.security.oauthbearer.CustomPrincipalBuilder

#### Add dependencies to Kafka folder
- Build the kafka-oauth JAR and then copy it from the target directory into the Kafka lib folder.
    - This is needed for Kafka to utilize the custom classes communicating with the OAuth server.
    
            C:> git clone https://blade-git.blackrock.com/cachematrix/lib-kafka-oauth.git
            C:> cd lib-kafka-oauth\    
            C:\lib-kafka-oauth> mvn install            - NOTE: this will build all JARs (Kafka OAuth, Test consumer, and Test Producer)
                .
                .
                .
            C:\lib-kafka-oauth> copy kafka-oauth\target\libkafka.oauthbearer-1.0.0.jar {KAFKA_DIRECTORY}\libs\.

- Move your updated server.properties file into the kafka config directory. 
- Move your kafka_server_jaas.conf file to the kafka config directory.

## Run Kafka with OAuth
- Open two CMD windows to run the zookeeper and broker
    - Run the Zookeeper
    
    ---
    
        bin\windows\zookeeper-server-start.bat config\zookeeper.properties
        
    - Run the Broker
    
    ---
        set KAFKA_OPTS=-Djava.security.auth.login.config={PATH_TO_JASS_CONFIG_FILE}\kafka_server_jaas.conf
        
        set KAFKA_OAUTH_SERVER_PROP_FILE={PATH_TO_PROPERTIES_FILE}\broker-configuration.properties
        
        bin\windows\kafka-server-start.bat config\server.properties
    
    
