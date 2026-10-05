package com.college.library.model;

/**
 * Represents a student library member.
 * Demonstrates:
 * - Inheritance: Extends Person class.
 * - Polymorphism: Overrides getRole() and toString().
 */
public class Student extends Person {
    private String ktuId;
    private String branch;
    private int semester;
    private String batch; // Optional, as not all branches have multiple batches

    // Default constructor
    public Student() {
        super();
    }

    // Constructor without database ID and without batch
    public Student(String ktuId, String name, String branch, int semester, String email, String phone) {
        this(0, ktuId, name, branch, semester, null, email, phone);
    }

    // Constructor without database ID, with optional batch
    public Student(String ktuId, String name, String branch, int semester, String batch, String email, String phone) {
        this(0, ktuId, name, branch, semester, batch, email, phone);
    }

    // Full constructor including database ID
    public Student(int id, String ktuId, String name, String branch, int semester, String batch, String email, String phone) {
        super(id, name, email, phone);
        this.ktuId = ktuId;
        this.branch = branch;
        this.semester = semester;
        this.batch = batch;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    // Getters and Setters
    public String getKtuId() {
        return ktuId;
    }

    public void setKtuId(String ktuId) {
        this.ktuId = ktuId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    @Override
    public String toString() {
        String batchStr = (batch != null && !batch.trim().isEmpty()) ? ", Batch: " + batch : "";
        return "Student [" + super.toString() + ", KTU ID: " + ktuId + ", Branch: " + branch +
               ", Semester: " + semester + batchStr + "]";
    }
}
