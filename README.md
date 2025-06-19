# Facturador

This project is a Spring Boot application. It now supports multiple runtime environments through Spring profiles.

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
