# roadlink-core-service
This service is responsible for managing user information, relationships, feedback and authentication.

### Requirements
- Java 17
- Gradle 7.4
- docker & docker-compose v2.22.0

### Setup
In order to get all the requirements needed to run this project run:
```bash
make setup
```

### Local deployment
Once you have configured docker, run the following command in order to execute a local deployment:
```bash
make deploy
```

### Test
Before pushing your changes make sure that all test pass:
```bash
make test
```
