# 🌊 ScubaKeep – Spring Boot Dive Logbook

ScubaKeep is a Spring Boot backend application that helps scuba divers log, manage, and track their dives -from local reef dives to adventures across the seven seas- through a clean and intuitive REST API. It features a layered architecture with full service-level testing, validation, and meaningful logging.

This project was developed independently as a portfolio piece to build practical proficiency with Spring Boot, JPA, validation, RESTful design, and unit testing in a real-world backend system.

---

## 🐠 Features

- Full CRUD support for divers and dive logs
- Rank system based on total logged dives
- Certification & specialty tracking per diver
- Input validation and global exception handling
- Unit-tested service layer with JUnit and Mockito
- Modular, layered architecture (DTOs, mappers, services, controllers)
- Clean logging with SLF4J

---

## ⚙️ Technologies

- Java 17
- Spring Boot 3
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- JUnit 5 + Mockito
- IntelliJ IDEA

---

## 🗂️ Project Structure

```text
src/
├── main/
│   ├── java/com/lucap/scubakeep/
│   │   ├── controller/   # REST API endpoints
│   │   ├── dto/          # Request & response models
│   │   ├── entity/       # JPA entities
│   │   ├── exception/    # Custom exceptions & global handlers
│   │   ├── mapper/       # Entity <-> DTO mappers
│   │   ├── repository/   # Spring Data JPA interfaces
│   │   ├── service/      # Business logic layer
│   │   └── ScubaKeepApplication.java  # Main entry point
│   └── resources/
│       ├── application.properties            # Local config (gitignored)
│       └── application-example.properties    # Sample config
└── test/
    └── java/com/lucap/scubakeep/             # Unit tests
```

---

## ▶️ How to Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/luca-pal/ScubaKeep.git
cd ScubaKeep
```
### 2. Set up the database

Log into MySQL and run:

```sql
CREATE DATABASE scubakeep_db;
```

Create a user:

```sql
CREATE USER 'scubakeep_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON scubakeep_db.* TO 'scubakeep_user'@'localhost';
```

### 3. Configure application properties

Copy the example config file:

```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```

Then edit `application.properties` with your local DB credentials.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

Or open the project from your IDE.

---

## 🔗 Sample Endpoints

All endpoints follow RESTful conventions and are prefixed with `/api`.

### Divers

- `GET    /api/divers` – Get all divers
- `GET    /api/divers/{id}` – Get a diver by ID
- `POST   /api/divers` – Create a new diver
- `PUT    /api/divers/{id}` – Update an existing diver
- `DELETE /api/divers/{id}` – Delete a diver

### Dive Logs

- `GET    /api/divelogs` – Get all dive logs
- `GET    /api/divelogs/{id}` – Get a dive log by ID
- `POST   /api/divelogs` – Create a new dive log
- `PUT    /api/divelogs/{id}` – Update an existing dive log
- `DELETE /api/divelogs/{id}` – Delete a dive log

---

## 🔬 How to Run Tests

This project includes unit tests for the business logic layer using JUnit 5 and Mockito. To execute the tests, run:

```bash
mvn test
```

This will automatically:

- Compile test sources
- Run all tests inside `src/test/java`
- Display the results in the terminal

---

## 📝 License

This project is licensed under the [MIT License](https://choosealicense.com/licenses/mit/).  
You’re free to use it, modify it, share it or dive deep into it... Just don’t hold me liable if your code implodes. 🛟