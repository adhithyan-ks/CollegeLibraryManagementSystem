# AGENT.md

## Project

**College Library Management System**

Java 17 desktop application using Swing, JDBC, and MySQL.

## Purpose

This file provides development instructions for AI coding agents and automated coding assistance working in this repository.

## Project Context

This is primarily an **Object Oriented Programming project**, not a database-design project. MySQL and JDBC are required for persistent storage, but the implementation should not become unnecessarily database-centric.

The application is for **librarians**. Students are records managed by the librarian and do not have separate application accounts.

The project has a very short development timeline. Prefer a small, complete, working implementation over unnecessary abstraction or feature expansion.

## Current Development Priority

1. Core Java/OOP model
2. Library business logic
3. CLI testing
4. MySQL/JDBC integration
5. Swing GUI
6. Testing and documentation

## Technology Constraints

- Java 17
- Maven
- Java Swing for the GUI
- AWT only for supporting classes/layouts where appropriate
- MySQL for persistent storage
- JDBC for database access
- No web backend
- No JavaScript frontend
- No JavaFX unless project requirements are explicitly changed

## Architecture

Use the following dependency direction:

```text
GUI / CLI
   ↓
Library / Business Logic
   ↓
DAO
   ↓
JDBC
   ↓
MySQL
```

During the initial phase:

```text
CLI / Tests
   ↓
Library / Business Logic
   ↓
Java Collections
```

The model layer must not depend on Swing or JDBC.

The business/service layer contains library rules.

DAO classes contain database persistence logic.

GUI classes handle presentation and user interaction, not SQL queries or duplicated business rules.

## Package Structure

Use:

```text
com.college.library
├── model
├── service
├── exception
├── cli
├── database
├── dao
└── gui
```

Current core classes:

```text
model/
├── Person.java
├── Student.java
├── Librarian.java
├── Book.java
├── Category.java
└── BorrowTransaction.java

service/
└── Library.java

exception/
└── LibraryException.java

cli/
└── Main.java

database/
└── DatabaseConnection.java
```

Do not create unnecessary packages or classes.

## Domain Model

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

### Person

`Person` is the common abstract base for `Student` and `Librarian`.

### Student

A student record includes:

- KTU ID
- Name
- Branch
- Semester
- Optional batch
- Email
- Phone

Batch must be optional because not every branch necessarily has multiple batches.

### Librarian

A librarian has:

- ID
- Name
- Email
- Phone
- Username
- Password/credential representation

### Book

**Each physical book is represented as its own `Book` object and database record.**

Do not introduce separate `BookTitle` and `BookCopy` entities unless the project requirements are explicitly changed.

A `Book` contains:

- Library/accession ID
- ISBN
- Title
- Author
- Publisher
- Edition
- Category
- Status

Possible status values:

```text
AVAILABLE
ISSUED
LOST
DAMAGED
```

If the library owns three physical copies of the same published book, represent them as three `Book` objects with different library/accession IDs.

Do not add `quantity` or `availableQuantity` to the current design.

### BorrowTransaction

A transaction represents one physical book issued to one student.

It contains:

- Transaction ID
- Student
- Book
- Librarian
- Issue date
- Due date
- Return date
- Fine
- Status

The transaction should identify the exact physical `Book` object.

## OOP Guidelines

Use OOP concepts naturally:

- Encapsulate state with private fields.
- Use constructors to establish valid object state.
- Use inheritance where there is a genuine "is-a" relationship.
- Use abstraction/interfaces where they improve the design.
- Use polymorphism where common behavior has multiple implementations.
- Prefer composition when inheritance is not appropriate.

Do not add artificial classes solely to claim an OOP concept.

The design should be easy for the student team to explain during a viva.

## Naming

Use standard Java naming conventions:

- Classes: `PascalCase`
- Methods: `camelCase`
- Variables: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Packages: lowercase

## Database Rules

- Do not put SQL directly inside Swing GUI classes.
- Do not duplicate JDBC connection code.
- Use `DatabaseConnection` for obtaining connections.
- DAO classes should own SQL queries.
- Use `PreparedStatement` for parameterized queries.
- Use try-with-resources for JDBC resources.
- Never commit database passwords or other secrets to Git.
- Keep database setup reproducible with SQL scripts.

The database should mirror the simple domain model rather than introduce unnecessary normalization or entities.

Core planned tables:

```text
librarians
students
books
categories
borrow_transactions
```

## GUI Rules

Use Swing components such as:

- `JFrame`
- `JPanel`
- `JButton`
- `JLabel`
- `JTextField`
- `JTable`
- `JComboBox`
- `JDialog`

Keep GUI code separate from business logic.

A button handler should call application/service logic rather than implement the entire business operation itself.

## Error Handling

Expected application errors should be represented clearly.

Examples:

- Book not found
- Student not found
- Book unavailable
- Invalid return
- Duplicate KTU ID
- Invalid input

Use custom exceptions where they improve clarity.

Do not silently catch exceptions.

Avoid:

```java
catch (Exception e) {
}
```

Prefer meaningful handling or propagation.

## Testing

Every major feature should be manually tested before integration.

At minimum test:

- Adding a book
- Adding a student
- Searching records
- Issuing an available book
- Attempting to issue an unavailable book
- Returning an issued book
- Attempting an invalid return
- Database connection
- Persistence after restarting the application

## Git

Keep commits focused.

Good examples:

```text
Add Person and Student models
Add Book model and status
Implement student management
Implement book issue validation
Add MySQL database schema
Implement book DAO
Add librarian login screen
```

Avoid vague commits such as:

```text
update
changes
final
stuff
```

Do not commit:

- `target/`
- compiled `.class` files
- database passwords
- local machine-specific configuration
- temporary test files

Follow the team's repository policy for IDE metadata such as `.idea/`.

## Working With Existing Code

Before changing a file:

1. Read the existing implementation.
2. Preserve working behavior.
3. Make the smallest change required.
4. Compile/test after the change.
5. Avoid unrelated refactoring.

Do not rewrite functioning project architecture without a concrete reason.

## Documentation

Keep `README.md` updated when the project structure, setup process, or major features change.

Keep `PROJECT_OVERVIEW.md` updated when scope, requirements, domain model, or design decisions change.

Keep this `AGENT.md` aligned with the actual architecture and implementation rules.

## Important Constraint

The final application must satisfy the academic requirements for Java OOP, Swing GUI, JDBC, and permanent data storage using MySQL.

When choosing between a complex implementation and a simpler implementation that satisfies the same requirement, prefer the simpler implementation.

Do not turn this OOP project into an unnecessarily complex DBMS or enterprise architecture.
