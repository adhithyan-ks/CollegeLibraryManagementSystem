package com.college.library.model;

/**
 * Represents a librarian who operates the system.
 * Demonstrates:
 * - Inheritance: Extends Person class.
 * - Polymorphism: Overrides getRole() and toString().
 */
public class Librarian extends Person {
    private String username;
    private String password;

    // Default constructor
    public Librarian() {
        super();
    }

    // Constructor without database ID
    public Librarian(String name, String email, String phone, String username, String password) {
        this(0, name, email, phone, username, password);
    }

    // Full constructor including database ID
    public Librarian(int id, String name, String email, String phone, String username, String password) {
        super(id, name, email, phone);
        this.username = username;
        this.password = password;
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Librarian [" + super.toString() + ", Username: " + username + "]";
    }
}
