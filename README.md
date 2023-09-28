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
This project uses Kotest following
the [Behaviour style](https://kotest.io/docs/framework/testing-styles.html#behavior-spec).

This project has 3 types of tests:

* **unit**: Unit tests are automated tests that verify the functionality of small, independent units of code in isolation,
  typically at the function or method level, and are used to ensure that each unit performs as expected and that changes
  to the code do not introduce unintended consequences.
* **integration**: Integration tests are automated tests that verify the interactions and compatibility between different
  modules or components of a system, typically at the system or subsystem level, and are used to ensure that the overall
  system works as expected and that changes to one component do not break the functionality of other components.
* **acceptance**: Acceptance tests are automated tests that verify that a system or software meets the requirements
  and specifications defined by the customer or end-user, typically at the end-to-end or user workflow level, and are
  used to ensure that the system is ready for delivery and meets the needs of the customer.

#### Running test
Before pushing your changes make sure that all test pass:
```bash
make test
```
