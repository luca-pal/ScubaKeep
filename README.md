# 🤿 ScubaKeep – Dive Log REST API

A secure RESTful backend for managing scuba diving logs, featuring JWT authentication, role-based access control, and object storage for dive images and users' profile pictures.

---

## 🐡 Project Overview

ScubaKeep is a Spring Boot backend application designed to manage scuba diving logs through a secure RESTful API. 

The system allows divers to create, manage, and review detailed records of their dives, including information such as dive location, depth, duration, dive buddies, and personal notes. Dive entries can also include uploaded images, which are stored using object storage and linked to the corresponding dive log.

The application supports both anonymous and authenticated users. Anonymous users can browse dive logs, while registered users can create and manage their own entries. Authentication is implemented using JWT tokens, and role-based access control ensures that users can only modify their own data while administrators have full management privileges.

The project demonstrates the design of a modern backend service built with a layered architecture, secure authentication mechanisms, database persistence, and cloud-ready file storage.

---

## 🦑 Key Features

- Full CRUD support for divers and dive logs
- Dive log images and profile pictures stored using object storage (MinIO)
- JWT-based authentication and role-based authorization
- Pagination, sorting, and filtering for dive logs
- Input validation and centralized exception handling
- Modular layered architecture (controllers, services, repositories, DTOs, mappers)
- Comprehensive testing with JUnit, Mockito, and JaCoCo coverage reporting
- Structured logging using SLF4J

---

## ⚙️ Technology Stack

- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA (Hibernate)
- **Authentication:** JSON Web Tokens (JWT)
- **Database:** MariaDB
- **Object Storage:** MinIO
- **Testing:** JUnit 5, Mockito, JaCoCo
- **Infrastructure:** Docker, Docker Compose
- **Code Quality:** Checkstyle

---

## 🧩 Architecture

The application follows a layered architecture separating responsibilities across different components:

- **Controllers** handle HTTP requests and responses.
- **Services** contain the core business logic.
- **Repositories** manage database access through Spring Data JPA.
- **Security** is handled using Spring Security with JWT-based authentication.
- **Object storage** (MinIO) is used to store images associated with dive logs and diver profiles.

This structure promotes clear separation of concerns and improves maintainability and testability.

---

## 🗂️ Project Structure

```text
scubakeep
├── Dockerfile                # Container configuration for the Spring Boot application
├── docker-compose.yml        # Docker services (MariaDB, MinIO)
├── checkstyle.xml            # Code style rules used by the Checkstyle plugin
├── pom.xml                   # Maven configuration
│
├── src
│   ├── main
│   │   ├── java/com/lucap/scubakeep
│   │   │
│   │   │   ├── ScubaKeepApplication.java   # Spring Boot entry point
│   │   │   │
│   │   │   ├── config        # Application configuration (security, swagger, MinIO)
│   │   │   ├── controller    # REST API endpoints
│   │   │   ├── dto           # Request and response DTOs
│   │   │   ├── entity        # JPA entities
│   │   │   ├── exception     # Custom exceptions and global error handling
│   │   │   ├── mapper        # Entity ↔ DTO mapping
│   │   │   ├── repository    # Spring Data JPA repositories
│   │   │   ├── security      # JWT authentication and authorization
│   │   │   ├── service       # Business logic layer
│   │   │   ├── storage       # MinIO object storage integration
│   │   │   └── validation    # Custom validation logic
│   │   │
│   │   └── resources
│   │       ├── application.properties
│   │       └── application-example.properties
│   │
│   └── test
│       └── java/com/lucap/scubakeep
│           └── unit and integration tests
```

---

## 🔐 Authentication & Authorization

The API uses **JWT-based authentication** implemented with Spring Security.

Users authenticate through the `/auth/token` endpoint and receive a JSON Web Token (JWT). This token must be included in subsequent requests using the `Authorization` header.

The application supports two roles:

- **USER** — can manage their own dive logs and profile data.
- **ADMIN** — can manage all users and dive logs.

Access control is enforced through Spring Security configuration and service-layer checks to ensure that users can only modify resources they own, while administrators have full access.

---

## ▶️ Running the Project

### Start the Application

Clone the repository and run:

```bash
docker-compose up --build
```
This command builds and starts all required services:

- **Spring Boot application**
- **MariaDB database**
- **MinIO object storage**

Docker Compose orchestrates the containers and configures the networking between the application, the database, and the object storage service.

### Access the Application

Once the containers are running, the API will be available at:

http://localhost:8080

Interactive API documentation is available through Swagger UI:

http://localhost:8080/swagger-ui.html

### Object Storage Console

The MinIO web console can be accessed at:

http://localhost:9001

MinIO is used to store images associated with dive logs and diver profiles.

---

## 🔗 API Overview

The API exposes endpoints for authentication, diver management, and dive log management. Interactive documentation is available through **Swagger UI**, but the main resources are summarized below.

### Authentication

| Method | Endpoint | Description |
|------|------|------|
| POST | `/auth/register` | Create a new user account |
| POST | `/auth/token` | Authenticate and obtain a JWT token |

### Dive Logs

| Method | Endpoint | Description |
|------|------|------|
| GET | `/api/divelogs` | Retrieve a paginated list of dive logs |
| GET | `/api/divelogs/{id}` | Retrieve a specific dive log |
| POST | `/api/divelogs` | Create a new dive log |
| PUT | `/api/divelogs/{id}` | Update an existing dive log |
| DELETE | `/api/divelogs/{id}` | Delete a dive log |

Dive logs can be **filtered, sorted, and paginated** using query parameters.

### Dive Log Images

| Method | Endpoint | Description |
|------|------|------|
| POST | `/api/divelogs/{id}/image` | Upload an image for a dive log |
| GET | `/api/divelogs/{id}/image` | Retrieve the image associated with a dive log |

Images are stored in **MinIO object storage**.

### Divers

| Method | Endpoint | Description                            |
|------|------|----------------------------------------|
| GET | `/api/divers` | Retrieve all divers (admin only access) |
| GET | `/api/divers/{id}` | Retrieve a specific diver              |
| PUT | `/api/divers/{id}` | Update diver information               |
| DELETE | `/api/divers/{id}` | Delete a diver           |

### Diver Profile Images

| Method | Endpoint | Description |
|------|------|------|
| POST | `/api/divers/{id}/image` | Upload a diver profile picture |
| GET | `/api/divers/{id}/image` | Retrieve the diver profile picture |

---

## 🔬 Testing

The project includes unit and integration tests to verify the correctness of the service layer and the application configuration.

### Testing Tools

- **JUnit 5** for writing and executing tests
- **Mockito** for mocking dependencies in unit tests
- **Spring Boot Test** for loading the application context during integration testing
- **JaCoCo** for measuring test coverage

### Running the Tests

Tests can be executed using Maven:

```bash
mvn test
```

JaCoCo generates a coverage report after the test execution. The current test suite achieves almost 90% code coverage, the report can be found at:

`target/site/jacoco/index.html`

---

## 📝 License

This project is licensed under the [MIT License](https://choosealicense.com/licenses/mit/).  
You’re free to use it, modify it, share it or dive deep into it... Just don’t hold me liable if your code implodes. 🛟