package com.college.library.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility to initialize database tables and seed data if they do not exist.
 * Keeps the application setup beginner-friendly and reproducible.
 */
public class DatabaseInitializer {

    public static void initializeDatabase() {
        System.out.println("Checking and initializing database tables...");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Table: librarians
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS librarians (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    phone VARCHAR(15) NOT NULL,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL
                );
            """);

            // 2. Table: students
            stmt.executeUpdate("""
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
            """);

            // 3. Table: categories
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL UNIQUE
                );
            """);

            // 4. Table: books
            stmt.executeUpdate("""
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
            """);

            // 5. Table: borrow_transactions
            stmt.executeUpdate("""
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
            """);

            // Default seed data
            stmt.executeUpdate("""
                INSERT IGNORE INTO librarians (id, name, email, phone, username, password)
                VALUES (1, 'Chief Librarian', 'librarian@college.edu', '9876543210', 'admin', 'admin123');
            """);

            stmt.executeUpdate("""
                INSERT IGNORE INTO categories (id, name) VALUES
                (1, 'Computer Science and Engineering'),
                (2, 'Electronics and Communication'),
                (3, 'Mechanical Engineering'),
                (4, 'Civil Engineering'),
                (5, 'Mathematics and Basic Sciences'),
                (6, 'General Literature');
            """);

            System.out.println("Database tables initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
