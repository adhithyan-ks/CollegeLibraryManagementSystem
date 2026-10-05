-- =====================================================================
-- College Library Management System - Database Schema
-- Database: library_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 1. Table: librarians
CREATE TABLE IF NOT EXISTS librarians (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Table: students
CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    ktu_id VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    branch VARCHAR(100) NOT NULL,
    semester INT NOT NULL,
    batch VARCHAR(20),
    email VARCHAR(100),
    phone VARCHAR(15)
);

-- 3. Table: categories
CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 4. Table: books
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    accession_id VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20),
    publisher VARCHAR(150),
    publication_year INT,
    category_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
);

-- 5. Table: borrow_transactions
CREATE TABLE IF NOT EXISTS borrow_transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    librarian_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE RESTRICT,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
    FOREIGN KEY (librarian_id) REFERENCES librarians(id) ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- Seed / Initial Default Data
-- ---------------------------------------------------------------------

-- Default Librarian (admin / admin123)
INSERT IGNORE INTO librarians (id, name, email, phone, username, password)
VALUES (1, 'Chief Librarian', 'librarian@college.edu', '9876543210', 'admin', 'admin123');

-- Standard Engineering & Science Categories
INSERT IGNORE INTO categories (id, name) VALUES
(1, 'Computer Science and Engineering'),
(2, 'Electronics and Communication'),
(3, 'Mechanical Engineering'),
(4, 'Civil Engineering'),
(5, 'Mathematics and Basic Sciences'),
(6, 'General Literature');
