# Tarrific Backend

Tarrific Backend is a Spring Boot application that provides RESTful APIs for managing tariffs, HS codes, countries, and trade agreements. It powers the Tarrific Frontend by handling business logic, data persistence, and tariff calculations.

---

## Tech Stack

- Framework: Spring Boot 3
- Language: Java 22
- Database: MySQL / MariaDB
- ORM: Spring Data JPA (Hibernate)
- Build Tool: Maven
- Security: Spring Security + JWT
- Containerization: Docker

---

## Features

- Tariff and preferential rate management.
- HS code and country administration.
- Trade agreement tracking.
- Database seeding through DataLoader.
- Health check and error handling endpoints.
- CORS configuration for frontend integration.

---

## Project Structure

```

tarrific-backend/
├── src/
│   ├── main/
│   │   ├── java/com/tarrific/backend/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── TarrificBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── data.sql (optional)
│   └── test/
│       └── java/com/tarrific/backend/
│           └── BackendTests.java
├── pom.xml
├── Dockerfile
└── README.md

````

---

## Environment Setup

### 1. Prerequisites
- Java 22 installed (`java -version`)
- Maven installed (`mvn -v`)
- MySQL or MariaDB running

### 2. Configure Database
Create a database named `tariff` and update your credentials in `application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/tariff
spring.datasource.username=tarrific
spring.datasource.password=tarrific123
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
````

### 3. Run Locally

```bash
mvn spring-boot:run
```

Then open [http://localhost:8080/api](http://localhost:8080/api).

---

## Development Notes

* Entities are stored in `com.tarrific.backend.model`.
* Controllers follow REST conventions in `com.tarrific.backend.controller`.
* DataLoader initializes seed data on startup.
* CORS settings are defined in `CorsConfig.java`.
* `@Service` and `@Repository` layers separate business logic and data access.

---

## Scripts

| Command                              | Description                 |
| ------------------------------------ | --------------------------- |
| `mvn spring-boot:run`                | Run the application locally |
| `mvn clean package`                  | Build the project JAR       |
| `mvn test`                           | Run tests                   |
| `docker build -t tarrific-backend .` | Build Docker image          |

---

## Deployment

To build and run:

```bash
mvn clean package
java -jar target/tarrific-backend.jar
```

For Docker:

```bash
docker build -t tarrific-backend .
docker run -p 8080:8080 tarrific-backend
```
