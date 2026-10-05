package com.college.library.model;

/**
 * Abstract base class representing a person in the library management system.
 * Demonstrates:
 * - Abstraction: Cannot be instantiated directly; provides common base structure.
 * - Encapsulation: Private member variables accessible via getters and setters.
 */
public abstract class Person {
    private int id;
    private String name;
    private String email;
    private String phone;

    // Default constructor
    public Person() {
    }

    // Constructor without ID (useful before record is saved to database)
    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Full constructor with ID
    public Person(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Abstract method demonstrating polymorphism across subclasses
    public abstract String getRole();

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email + ", Phone: " + phone;
    }
}
