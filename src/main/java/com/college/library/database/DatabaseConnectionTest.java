package com.college.library.database;

import java.sql.Connection;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        System.out.println("Application started.");

        try {
            Connection connection = DatabaseConnection.getConnection();

            System.out.println("Database connection successful!");

            connection.close();

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}