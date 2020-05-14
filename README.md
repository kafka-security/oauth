# Lib-Kafka-OAuth

[logo]: https://https://avatars3.githubusercontent.com/u/64902484?s=400&u=1dcba824aa6a9860b7e40d5e1ebe0e9c888f9bfc&v=4

The purpose of the library is to improve the out of box Kafka security features, by providing concrete OAuth2 implementation of authentication and authorization for Kafka based on Kafka OAuth Security Framework.  
The library utilizes KeyCloak as the OAuth server, but other OAuth servers could be used.

Learn more [here](https://medium.com/blackrock-engineering/utilizing-oauth-for-kafka-security-5c1da9f3d3d).

## Goals

- Modern industry standard – ability to expire/refresh credentials for long running processes without downtime
- Enterprise grade – support of centralized management and segregation of duties in operation
- DX focused – capability of deployment time configuration

## Non-Goals

- Not to support other Kafka security frameworks or plugins.

### Installation

Prerequisite
- Install and configure JDK Version 8
- Install and configure Maven
- Install and configure Git Client

Instructions for how to download/install the code onto your machine.

Clone repository to your local machine:
```bash
git clone '{REPO_URL}'
```
Install project dependencies
```bash
mvn install
```
- Compile the project
```bash
mvn compile
```

### Usage

- It is recommended that you follow the guides in the order outlined below. 
- Prerequisites
    - Port 8080 open for local KeyCloak

1) [KeyCloak Configuration](./kafka-oauth/KeyCloak-Setup.md)
2) [Kafka OAuth Configuration](./kafka-oauth/README.md)
3) [Test Consumer Configuration](./kafka-consumer-example/README.md)
4) [Test Producer Configuration](./kafka-producer-example/README.md)

### Contributing

Contributions are welcomed! Read the [Contributing Guide](./CONTRIBUTING.md) for more information.

### Contact Us

- GitHub [Issue](./.github/.ISSUE_TEMPLATE/issue.md)

### Licensing

See [LICENSE](LICENSE) for more information.

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

### Credits

This project was inspired by:
-  https://github.com/jairsjunior/kafka-oauth
