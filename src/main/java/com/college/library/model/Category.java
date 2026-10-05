package com.college.library.model;

/**
 * Represents a book category or subject genre in the library.
 * Examples: Computer Science, Mathematics, Mechanical Engineering, Electronics, Literature.
 */
public class Category {
    private int id;
    private String name;

    // Default constructor
    public Category() {
    }

    // Constructor without database ID
    public Category(String name) {
        this(0, name);
    }

    // Full constructor
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public String toString() {
        return name;
    }
}
