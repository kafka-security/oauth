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

#Producer Example using the Kafka OAuth library

#### Add libkafka.oauthbearer dependency in your `pom.xml` file

    <dependency>
        <groupId>brs</groupId>
        <artifactId>libkafka.oauthbearer</artifactId>
        <version>1.0.0</version>
    </dependency>
    

#### Add the OAuth configuration to your Producer
        // OAuth Settings
        //	- sasl.jaas.config=org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;
        props.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required;");
        
        //	- security.protocol=SASL_PLAINTEXT
        props.put("security.protocol", "SASL_PLAINTEXT");
        
        //	- sasl.mechanism=OAUTHBEARER
        props.put("sasl.mechanism", "OAUTHBEARER");
        
        //	- sasl.login.callback.handler.class=com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler
        props.put("sasl.login.callback.handler.class", "com.bfm.kafka.security.oauthbearer.OAuthAuthenticateLoginCallbackHandler");


#### Build the JAR
- If you are running inside a IDE you can skip this step
    
#### Config Files
 
- Create a properties file for your Producer OAuth client {oauth-configuration.properties}.
    - There is one already in your resource folder!
- The file should contain the following properties:
- NOTE: you will need to change oauth.server.client.secret to be your client secret


        oauth.server.base.uri=http://localhost:8080/auth/realms/master/protocol/openid-connect
        oauth.server.token.endpoint.path=/token
        oauth.server.introspection.endpoint.path=/token/introspect
        oauth.server.client.id=test-producer
        oauth.server.client.secret=4ce54cfb-f359-4400-bd8e-810a1af10f71
        oauth.server.grant.type=client_credentials
        oauth.server.scopes=test
        oauth.server.accept.unsecure.server=true
        # oauth.server.accept.unsecure.server - this propertie is for SSL configuration, if you are using HTTP or a self-signed CERT set this true

    

    
#### Configure your ENV Variables
        
    set KAFKA_OAUTH_SERVER_PROP_FILE={PATH_TO_PROJECT}\kafka-producer-example\src\main\resources\oauth-configuration.properties
    
    
#### Run the Producer
    java -jar kafka-producer-example/target/kakfa-oauth-producer-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    
    OR
    
    Run the main Java class from your IDE
