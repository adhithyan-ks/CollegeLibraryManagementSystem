# College Library Management System

A desktop-based **College Library Management System** developed in Java as part of the Object Oriented Programming (PBCST304) project.

The application is intended for use by the **librarian**. Students are managed as library records; they do not need separate application accounts.

## Project Status

> **Current phase:** Core OOP structure and business logic  
> **Submission target:** Functional Java Swing application with MySQL/JDBC persistence

Development is being done incrementally:

1. Core Java/OOP model
2. Library business logic and CLI testing
3. MySQL/JDBC persistence
4. Java Swing GUI
5. Testing, documentation, and final integration

## Objectives

- Apply Java Object-Oriented Programming principles in a practical application.
- Manage books and students in a college library.
- Track each physical library book using its own library/accession ID.
- Record book issue and return transactions.
- Provide a librarian-oriented desktop interface.
- Store application data permanently using MySQL.
- Use JDBC for Java–MySQL connectivity.
- Demonstrate encapsulation, abstraction, inheritance, polymorphism, composition, and exception handling where appropriate.

## Core Features

### Librarian
- Librarian login
- Library dashboard
- Manage library operations

### Student Management
- Add, view, update, search, and remove student records
- Store KTU ID
- Store branch, semester, and batch
- Batch is optional because not every branch has multiple batches
- View current borrowing status and borrowing history

### Book Management
- Add, view, update, search, and remove books
- Each physical book is represented as an individual `Book` record
- Each book has a unique library/accession ID
- Store ISBN, title, author, publisher, edition, category, and status
- Track states such as `AVAILABLE`, `ISSUED`, `LOST`, and `DAMAGED`

### Borrowing
- Issue a specific physical book to a student
- Return a book
- Track issue date, due date, and return date
- Maintain transaction history
- Calculate/record fines
- Prevent invalid operations such as issuing an unavailable book
- Support library-clearance checking

### Dashboard
- Total books
- Available books
- Issued books
- Overdue books
- Registered students
- Other useful library statistics derived from the stored data

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application development |
| Java Swing | Desktop GUI |
| AWT | Supporting layouts/classes where required |
| Maven | Dependency and project management |
| MySQL | Permanent data storage |
| JDBC | Java–MySQL connectivity |
| Git/GitHub | Version control and team collaboration |
| IntelliJ IDEA | Primary development environment |

The application is intended to run on both Windows and Linux/Ubuntu with a compatible JDK and MySQL installation.

## Architecture

The core library logic is kept independent of Swing and JDBC.

### Initial development

```text
CLI / Tests
     |
     v
Library / Business Logic
     |
     v
Java Collections
```

### Final application

```text
Swing GUI / CLI
       |
       v
Library / Business Logic
       |
       v
DAO Layer
       |
       v
JDBC
       |
       v
MySQL
```

The GUI should call the application/business logic rather than contain database queries or duplicate library rules.

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/college/library/
    │       ├── model/
    │       │   ├── Person.java
    │       │   ├── Student.java
    │       │   ├── Librarian.java
    │       │   ├── Book.java
    │       │   ├── Category.java
    │       │   └── BorrowTransaction.java
    │       ├── service/
    │       │   └── Library.java
    │       ├── exception/
    │       │   └── LibraryException.java
    │       ├── cli/
    │       │   └── Main.java
    │       ├── database/
    │       │   └── DatabaseConnection.java
    │       ├── dao/          # added during JDBC integration
    │       └── gui/          # added during Swing implementation
    │
    └── resources/
```

## OOP Design

The main domain model is:

```text
Person
├── Student
└── Librarian

Book
Category
BorrowTransaction

Library
├── Students
├── Books
└── Transactions
```

### OOP concepts

- **Encapsulation** — private fields with controlled access through methods.
- **Abstraction** — `Person` provides common abstraction for people associated with the system.
- **Inheritance** — `Student` and `Librarian` extend `Person`.
- **Polymorphism** — common `Person` references can represent different person types.
- **Composition/Aggregation** — `Library` manages books, students, and transactions.
- **Exception Handling** — invalid library operations are handled through appropriate exceptions.

OOP concepts should be used because they make the application design clearer, not merely to increase the number of classes.

## Book Model

A deliberate design decision is that **each physical copy is represented as its own `Book` record**.

For example:

```text
LIB-001  Database System Concepts  AVAILABLE
LIB-002  Database System Concepts  ISSUED
LIB-003  Database System Concepts  AVAILABLE
```

The ISBN identifies the published edition, while the library/accession ID identifies the physical copy owned by the library.

This project does not introduce separate `BookTitle` and `BookCopy` entities because it is an OOP application project rather than a full-scale library DBMS. The simpler model is sufficient for the intended scope.

## Database

The planned database is:

```text
library_db
```

Core tables:

```text
librarians
students
books
categories
borrow_transactions
```

The database mirrors the important persistent domain objects. Database-specific implementation will be kept in DAO/JDBC classes.

A SQL schema/setup script will be maintained in the repository so team members can reproduce the database.

## Team Development

The repository uses Git for collaboration.

Recommended workflow:

```text
main
 |
 +-- feature/book-module
 +-- feature/student-module
 +-- feature/transaction-module
 +-- feature/gui
 +-- feature/database
```

- Work on feature branches.
- Keep commits focused and descriptive.
- Do not commit passwords or machine-specific database credentials.
- Test changes before merging.
- Avoid modifying another member's work unnecessarily.

## Development Principles

1. Keep core business logic independent of Swing.
2. Keep database-specific code inside the DAO/database layer.
3. Avoid SQL queries directly inside GUI classes.
4. Avoid duplicating business rules in multiple screens.
5. Prefer small, understandable classes and methods.
6. Validate user input before performing operations.
7. Handle expected errors clearly.
8. Keep the project runnable after each major change.
9. Prefer a complete, understandable implementation over unnecessary complexity.

## Getting Started

### Requirements

- JDK 17
- Maven
- IntelliJ IDEA or another Java IDE
- MySQL Server

### Build

```bash
mvn clean compile
```

### Database

The MySQL database will be integrated after the core OOP/business logic is established.

The database connection configuration should be kept in one place and should not contain credentials committed to Git.

## Project Documentation

Additional documentation will be added as the project progresses:

- Project overview
- Design documentation
- Database schema
- User manual
- Test cases
- Final report

## Academic Context

This project is developed for the Semester S3 Object Oriented Programming course (PBCST304). The application is intended to demonstrate Java OOP, user interaction, persistent storage, Swing GUI development, JDBC, and related programming concepts.
