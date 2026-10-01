# Project Overview

## 1. Project Title

**College Library Management System**

## 2. Project Type

Java desktop application for managing the day-to-day operations of a college library.

## 3. Problem Statement

A college library needs a simple system through which a librarian can maintain book and student records and manage the issue and return of books.

The system should reduce manual record keeping, provide quick access to library information, and maintain a history of borrowing transactions.

## 4. Intended User

### Primary User

**Librarian**

The application is designed as a librarian-side management system. Students are stored as records in the system and do not require separate application accounts.

## 5. Scope

The system covers:

- Librarian authentication
- Book record management
- Student record management
- Book issue and return
- Transaction history
- Individual physical-book identification
- Book availability/status tracking
- Search and record viewing
- KTU-aware student information
- Library-clearance checking
- Dashboard statistics
- Permanent storage through MySQL
- Desktop GUI using Java Swing

The project does not aim to provide:

- A web application
- A student mobile application
- Student login
- Online library access
- Cloud deployment
- Online payment or external service integration

## 6. College-Specific Information

Student records include:

- KTU ID
- Name
- Branch
- Semester
- Batch
- Email
- Phone

`Batch` is optional because some branches may have multiple batches while others may not.

The KTU-specific information is intended to make the system suitable for a college environment while remaining useful for ordinary library operations.

## 7. Functional Requirements

### FR-01: Librarian Login

The system shall allow an authorized librarian to access the management application.

### FR-02: Book Management

The librarian shall be able to add, view, update, search, and remove book records.

Each physical book shall have a unique library/accession ID.

### FR-03: Student Management

The librarian shall be able to add, view, update, search, and remove student records.

### FR-04: Book Issue

The librarian shall be able to issue an available physical book to a registered student.

### FR-05: Book Return

The librarian shall be able to record the return of an issued book.

### FR-06: Book Status

Each physical book shall have a status such as:

- `AVAILABLE`
- `ISSUED`
- `LOST`
- `DAMAGED`

### FR-07: Transaction History

The system shall maintain records of book issue and return transactions.

### FR-08: Validation

The system shall prevent invalid operations, such as issuing an unavailable book or returning a book that is not currently issued.

### FR-09: Search

The librarian shall be able to search students and books using relevant identifiers and attributes.

### FR-10: Clearance

The system shall be able to determine whether a student has outstanding library obligations such as currently borrowed books or fines.

## 8. Proposed Domain Model

```text
                 Person
                /                     /                  Student      Librarian

Book
Category
BorrowTransaction

Library
├── Students
├── Books
└── Transactions
```

### Relationships

```text
Student    1 ──── * BorrowTransaction
Book       1 ──── * BorrowTransaction
Librarian  1 ──── * BorrowTransaction
Category   1 ──── * Book
```

## 9. Planned Java Classes

### `Person`

Abstract base class containing common person information.

### `Student`

Represents a college student who can borrow books. Extends `Person`.

Additional information includes KTU ID, branch, semester, and optional batch.

### `Librarian`

Represents the librarian who operates the application. Extends `Person`.

### `Book`

Represents one physical book owned by the library.

Important information includes library/accession ID, ISBN, title, author, publisher, edition, category, and status.

Multiple physical copies of the same published book are represented as separate `Book` objects with different library/accession IDs.

### `Category`

Represents a book category.

### `BorrowTransaction`

Represents one issue/return transaction connecting a student, a physical book, and the librarian who performed the operation.

### `Library`

Coordinates library operations and acts as the main business-logic component.

### `LibraryException`

Represents expected errors in library operations.

### `DatabaseConnection`

Provides JDBC connections to MySQL.

## 10. Database Model

The planned database contains the following core tables:

```text
librarians
students
books
categories
borrow_transactions
```

The `books` table represents individual physical books rather than an aggregate quantity.

For example:

```text
LIB-001 | Database System Concepts | AVAILABLE
LIB-002 | Database System Concepts | ISSUED
LIB-003 | Database System Concepts | AVAILABLE
```

This avoids maintaining a separate quantity/available-quantity field for the current project scope.

## 11. Development Strategy

Because the project has a short development timeline and team members have different levels of database experience, development will happen incrementally.

### Phase 1 — Core OOP

Implement the domain classes and business rules without database dependency.

Temporary data can be held using Java collections.

### Phase 2 — CLI Testing

Create a simple command-line interface to exercise the core operations.

### Phase 3 — JDBC/MySQL

Introduce DAO classes and connect the application to the `library_db` MySQL database.

### Phase 4 — Swing GUI

Build the desktop interface using Java Swing while reusing the existing business logic.

### Phase 5 — Testing and Documentation

Test the complete application and prepare the final project documentation and demonstration.

## 12. Design Principles

The project follows:

- Separation of concerns
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Low coupling between GUI and business logic
- Database abstraction through DAO/JDBC
- Reusable service logic
- Input validation
- Clear exception handling

The design should remain simple enough for the team to understand and explain during a viva.

## 13. Non-Functional Requirements

- The application should be simple enough for a librarian to operate.
- The program should provide clear error messages for invalid operations.
- Data should persist after the application is closed once MySQL integration is complete.
- The code should be understandable and maintainable by the student development team.
- The application should be usable on supported Windows and Linux/Ubuntu environments.

## 14. Current Development State

The Maven Java project has been initialized with JDK 17.

JDBC connectivity to the local MySQL server has been tested successfully.

Current package structure is being built under:

```text
com.college.library
```

The `database` package contains `DatabaseConnection.java`, and the `model` package has been created for the core OOP implementation.

## 15. Immediate Development Target

The immediate target is to establish the basic Java class structure and make the core library operations functional before adding database persistence and the Swing GUI.
