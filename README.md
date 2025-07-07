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

## Technical overview

Facturador is organised as a [Spring Modulith](https://docs.spring.io/spring-modulith/reference/) application with two main modules:

- **user** – management of application users and authentication
- **invoice** – creation of invoices, PDF generation and integration with the Verifactu service

Both modules share common base classes under **shared**. Asynchronous tasks are enabled via `@Async` and configured in `AsyncConfig`.

### Key dependencies

- Spring Boot 3.5
- Spring Web MVC and Thymeleaf for the UI
- Spring Data JPA (Hibernate) with an H2 in-memory database
- Spring Security with JDBC-backed sessions
- [openPDF](https://github.com/LibrePDF/OpenPDF) for PDF generation
- Apache Santuario `xmlsec` for XML digital signatures
- Lombok to reduce boilerplate

Check `pom.xml` for the full list of dependencies.

### Architecture and context

Below is a simplified diagram showing the main building blocks and their interactions:

```
+------------+     HTTP     +-----------------+
|  Browser   | ----------> |  Spring MVC UI  |
+------------+             +-----------------+
                                |
                                v
                         +--------------+
                         |  Invoice     |-----> Verifactu API
                         |  Module      |            ^
                         +--------------+            |
                                |               send invoices
                                v                    |
                         +--------------+            |
                         |  User Module |            |
                         +--------------+            |
                                |                    |
                                v                    |
                         +--------------+            |
                         |   Database   |<-----------+
                         +--------------+
```

The application exposes a web interface secured by Spring Security. Users can manage customers and invoices. Generated invoices can be downloaded as PDF files and optionally sent to Verifactu. All data is persisted in an embedded H2 database.

## Building and running

Use the provided Maven wrapper to build the project:

```bash
./mvnw clean package
```

To start the application in development mode simply run:

```bash
./mvnw spring-boot:run
```

Access `http://localhost:8080` in your browser once the application has started.
