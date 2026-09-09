package com.college.library.database;

import java.sql.Connection;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" College Library Management System");
        System.out.println("========================================");

        System.out.println("Java application is running!");

        try {
            Connection connection = DatabaseConnection.getConnection();

            System.out.println("Database connection successful!");
            System.out.println("Connected to: library_db");

            connection.close();

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}