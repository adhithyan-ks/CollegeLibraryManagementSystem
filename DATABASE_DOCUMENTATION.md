# Database Documentation
## College Library Management System

### 1. Database Overview

The College Library Management System uses **MySQL** for permanent data storage. The database stores librarian accounts, student records, book records, book categories, and borrowing/return transactions.

**Database name:** `library_db`

### 2. Database Tables

The system contains the following tables:

| Table | Purpose |
|---|---|
| `librarians` | Stores librarian account and contact information |
| `students` | Stores student/member information |
| `categories` | Stores book categories |
| `books` | Stores individual physical library books |
| `borrow_transactions` | Stores book issue and return transactions |

---

## 3. Table: `librarians`

### Purpose

Stores the librarians who operate the library management system. The application is intended for librarian use; students do not require application login accounts.

### Structure

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique identifier of the librarian |
| `name` | `VARCHAR(100)` | `NOT NULL` | Librarian's full name |
| `email` | `VARCHAR(100)` | `UNIQUE`, `NOT NULL` | Librarian's email address |
| `phone` | `VARCHAR(15)` | `NOT NULL` | Librarian's contact number |
| `username` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Login username |
| `password` | `VARCHAR(255)` | `NOT NULL` | Stored password/credential representation |

### SQL

```sql
CREATE TABLE librarians (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

---

## 4. Table: `students`

### Purpose

Stores the students who can borrow books from the library.

Students are records in the system rather than application users.

### Structure

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal unique student record ID |
| `ktu_id` | `VARCHAR(30)` | `UNIQUE`, `NOT NULL` | Student's KTU identification number |
| `name` | `VARCHAR(100)` | `NOT NULL` | Student's full name |
| `branch` | `VARCHAR(100)` | `NOT NULL` | Academic branch/program |
| `semester` | `INT` | `NOT NULL` | Current semester |
| `batch` | `VARCHAR(20)` | `NULL` | Batch identifier, when applicable |
| `email` | `VARCHAR(100)` | `NULL` | Student's email address |
| `phone` | `VARCHAR(15)` | `NULL` | Student's contact number |

### SQL

```sql
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ktu_id VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    branch VARCHAR(100) NOT NULL,
    semester INT NOT NULL,
    batch VARCHAR(20),
    email VARCHAR(100),
    phone VARCHAR(15)
);
```

### Design Note

`batch` is optional because some academic branches may have multiple batches while others may not.

---

## 5. Table: `categories`

### Purpose

Stores the categories used to classify library books.

### Structure

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique category identifier |
| `name` | `VARCHAR(100)` | `UNIQUE`, `NOT NULL` | Category name |

### SQL

```sql
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

---

## 6. Table: `books`

### Purpose

Stores information about individual physical books in the library.

Each physical copy is represented by a separate `books` record. The **accession ID** uniquely identifies the physical copy in the library.

### Structure

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Internal unique book record ID |
| `accession_id` | `VARCHAR(50)` | `UNIQUE`, `NOT NULL` | Unique library/accession ID of the physical book |
| `title` | `VARCHAR(200)` | `NOT NULL` | Book title |
| `author` | `VARCHAR(150)` | `NOT NULL` | Author of the book |
| `isbn` | `VARCHAR(20)` | `NULL` | ISBN of the published edition |
| `publisher` | `VARCHAR(150)` | `NULL` | Publisher name |
| `publication_year` | `INT` | `NULL` | Year of publication |
| `category_id` | `INT` | `NOT NULL`, `FOREIGN KEY` | Category to which the book belongs |
| `status` | `VARCHAR(20)` | `NOT NULL` | Current physical status of the book |

### SQL

```sql
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    accession_id VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20),
    publisher VARCHAR(150),
    publication_year INT,
    category_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',

    FOREIGN KEY (category_id)
        REFERENCES categories(id)
);
```

### Book Status

The application uses the following logical book statuses:

| Status | Meaning |
|---|---|
| `AVAILABLE` | Book is currently available for issue |
| `ISSUED` | Book is currently issued to a student |
| `LOST` | Book has been reported lost |
| `DAMAGED` | Book has been reported damaged |

### Design Note

ISBN identifies a **published edition**, whereas `accession_id` identifies the **individual physical copy owned by the library**. Therefore, multiple books can have the same ISBN but must have different accession IDs.

---

## 7. Table: `borrow_transactions`

### Purpose

Stores every book issue and return transaction.

One transaction represents one physical book being issued to one student by one librarian.

### Structure

| Column | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique transaction identifier |
| `student_id` | `INT` | `NOT NULL`, `FOREIGN KEY` | Student who borrowed the book |
| `book_id` | `INT` | `NOT NULL`, `FOREIGN KEY` | Physical book being borrowed |
| `librarian_id` | `INT` | `NOT NULL`, `FOREIGN KEY` | Librarian who processed the transaction |
| `issue_date` | `DATE` | `NOT NULL` | Date on which the book was issued |
| `due_date` | `DATE` | `NOT NULL` | Date by which the book should be returned |
| `return_date` | `DATE` | `NULL` | Actual return date; NULL while the book is not returned |
| `fine` | `DECIMAL(10,2)` | `NOT NULL`, `DEFAULT 0.00` | Fine associated with the transaction |
| `status` | `VARCHAR(20)` | `NOT NULL` | Current transaction status |

### SQL

```sql
CREATE TABLE borrow_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    librarian_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',

    FOREIGN KEY (student_id)
        REFERENCES students(id),

    FOREIGN KEY (book_id)
        REFERENCES books(id),

    FOREIGN KEY (librarian_id)
        REFERENCES librarians(id)
);
```

### Transaction Status

| Status | Meaning |
|---|---|
| `ISSUED` | Book is currently issued |
| `RETURNED` | Book has been returned |

---

# 8. Table Relationships

The database has the following relationships:

```text
categories
    │
    │ 1 : N
    ▼
  books
    │
    │ 1 : N
    ▼
borrow_transactions
    ▲             ▲
    │             │
    │             │
students       librarians
```

### Relationships in detail

#### Categories → Books

- One category can contain many books.
- Each book belongs to one category.
- Relationship: **1 : N**

```text
categories (1) ────────< books (N)
```

#### Students → Borrow Transactions

- One student can have many borrowing transactions over time.
- Each transaction belongs to one student.
- Relationship: **1 : N**

```text
students (1) ────────< borrow_transactions (N)
```

#### Books → Borrow Transactions

- One physical book can appear in multiple transactions over its lifetime.
- Each transaction refers to one physical book.
- Relationship: **1 : N**

```text
books (1) ────────< borrow_transactions (N)
```

#### Librarians → Borrow Transactions

- One librarian can process many borrowing/return transactions.
- Each transaction records the librarian who processed it.
- Relationship: **1 : N**

```text
librarians (1) ────────< borrow_transactions (N)
```

---

# 9. Foreign Key Summary

| Child Table | Foreign Key | Referenced Table | Referenced Column |
|---|---|---|---|
| `books` | `category_id` | `categories` | `id` |
| `borrow_transactions` | `student_id` | `students` | `id` |
| `borrow_transactions` | `book_id` | `books` | `id` |
| `borrow_transactions` | `librarian_id` | `librarians` | `id` |

---

# 10. Complete Database Creation Order

Because foreign keys reference other tables, the tables should be created in this order:

```text
1. librarians
2. students
3. categories
4. books
5. borrow_transactions
```

The corresponding SQL order is therefore:

```sql
CREATE DATABASE library_db;

USE library_db;

-- 1. librarians
-- 2. students
-- 3. categories
-- 4. books
-- 5. borrow_transactions
```

---

# 11. Design Principles

### Permanent Storage

MySQL provides persistent storage for the application. Java will communicate with the database using **JDBC**.

### Physical Book Representation

Each physical copy is represented by an individual `books` record. The system does not use a separate `BookTitle` or `BookCopy` table.

### Student Identification

The internal `id` is used as the database primary key, while `ktu_id` represents the student's actual KTU identification number.

### Transaction History

Borrowing records are retained in `borrow_transactions`, allowing the system to maintain historical issue and return information.

### Current Book State

The `status` field in `books` provides the current state of each physical book, while `borrow_transactions` provides the history of its borrowing activity.

---

# 12. Java Integration

The Java application will access these tables through the following architecture:

```text
Swing GUI / CLI
       ↓
Library / Service Layer
       ↓
DAO Layer
       ↓
JDBC
       ↓
MySQL
       ↓
library_db
```

The database layer will be implemented after the core Java OOP and CLI functionality has been developed.

---

# 13. Summary

The `library_db` database contains five main tables:

- **`librarians`** — system operators
- **`students`** — library members
- **`categories`** — book classifications
- **`books`** — individual physical books
- **`borrow_transactions`** — issue and return history

The design supports the main operations of the College Library Management System while providing a straightforward relational structure suitable for JDBC integration.
