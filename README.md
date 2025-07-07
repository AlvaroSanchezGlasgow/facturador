# Facturador

This project is a Spring Boot application. It now supports multiple runtime environments through Spring profiles.

The web interface has been refreshed with [Bulma](https://bulma.io/) styles for a modern look and feel.

## Running with profiles

The application uses the `spring.profiles.active` property to select which configuration file to load. Three profiles are provided out of the box:

- **dev** – default profile for development
- **test** – settings for running tests
- **live** – settings for production

You can choose the profile at runtime using an environment variable:

```bash
SPRING_PROFILES_ACTIVE=live ./mvnw spring-boot:run
```

If no profile is specified, the application falls back to the `dev` profile.

The database schema is automatically created on startup using Hibernate's
`ddl-auto=update` setting.

## Actuator endpoints

The application includes [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
for monitoring and management. By default the base path is `/actuator` and the
`health` and `info` endpoints are exposed. In the `dev` and `test` profiles all
endpoints are available.
