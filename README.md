# Gym CRM System

A **Gym Customer Relationship Management (CRM)** system designed to manage **trainees**, **trainers**, and **trainings** efficiently. This project follows a modular design using Spring Framework, focusing on **clean architecture**, **testability**, and **extensibility**.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Technologies Used](#technologies-used)
5. [Project Setup](#project-setup)
6. [How to Run](#how-to-run)
7. [API/Console Commands](#api-console-commands)
8. [Testing](#testing)
9. [Future Improvements](#future-improvements)

---

## Overview

This project is a **Java-based Gym CRM system** that provides functionalities for:
- Managing **trainees**: Add, update, view, and delete trainees.
- Managing **trainers**: Add, update, and view trainers.
- Managing **trainings**: Create, view, and manage training sessions.
- Ensures data persistence through **CSV files** and supports dynamic configurations via **Spring Profiles** and **environment variables**.

---

## Features

- **CRUD Operations**:
  - Create, Read, Update, and Delete for Trainees, Trainers, and Trainings.
  
- **Spring-based Modular Architecture**:
  - Use cases are split into services with dependency injection for easy management and testability.

- **File-based Storage**:
  - Data is persisted in **CSV files** using utilities for serialization/deserialization.

- **User-friendly Console Interface**:
  - Commands and options are provided for managing trainees, trainers, and training sessions interactively.

- **Logger Integration**:
  - Logs activities and errors using **SLF4J** for better debugging.

---

## Architecture

The system is structured following **clean architecture principles**:

- **Domain Layer**:
  - Core business entities like `Trainee`, `Trainer`, and `Training`.

- **Persistence Layer**:
  - `DAO` classes for interacting with CSV-based storage.

- **Application Layer**:
  - Services for implementing business logic and connecting to the persistence layer.

- **Console Layer**:
  - Main entry point for interacting with the system using a console interface.

---

## Technologies Used

- **Java 17**
- **Spring Framework**:
  - Dependency Injection
  - Bean Management
- **SLF4J**:
  - Logging
- **Apache Commons Lang**:
  - Utility methods
- **JUnit** and **Mockito**:
  - Unit testing and mocking
- **Maven**:
  - Build and dependency management

---

## Project Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA is recommended)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/gym-crm.git
   cd gym-crm
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Configure environment variables for file paths:
   - **IntelliJ IDEA**: Add the following variables in `Run/Debug Configurations`:
     ```
     storage.data.trainee.file=/path/to/trainees.csv
     storage.data.trainer.file=/path/to/trainers.csv
     storage.data.training.file=/path/to/trainings.csv
     ```

---

## How to Run

1. Run the application:
   ```bash
   mvn spring-boot:run
   ```

2. Follow the on-screen instructions in the console to interact with the system.

---

## API/Console Commands

### Main Menu
- **1. Create Trainee**: Add a new trainee.
- **2. Select Trainee**: View a specific trainee by ID.
- **3. Update Trainee**: Update trainee details.
- **4. Delete Trainee**: Remove a trainee by ID.
- **5. Create Trainer**: Add a new trainer.
- **6. Select Trainer**: View a specific trainer by ID.
- **7. Update Trainer**: Update trainer details.
- **8. Create Training**: Add a new training session.
- **9. Select Training**: View training details.
- **10. Exit**: Exit the application.

---

## Testing

### Unit Testing
- **JUnit** is used to test service, utility, and DAO layers.
- **Mockito** is used for mocking dependencies.
  
Run the tests:
```bash
mvn test
```

### Code Coverage
- Use plugins like **JaCoCo** for code coverage reporting:
```bash
mvn test jacoco:report
```

---

## Future Improvements

1. **Database Integration**:
   - Replace CSV storage with a relational database (e.g., PostgreSQL).

2. **REST API**:
   - Add a REST API layer for web-based interaction.

3. **UI Integration**:
   - Develop a web or desktop interface for easier management.

4. **Enhanced Security**:
   - Hash passwords and use secure storage for sensitive data.

5. **Additional Features**:
   - Reporting, analytics, and notification systems.

---

## Contribution

Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`.
3. Commit your changes: `git commit -m "Add your feature"`.
4. Push to your branch: `git push origin feature/your-feature`.
5. Open a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

