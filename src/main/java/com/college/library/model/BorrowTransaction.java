package com.college.library.model;

import java.time.LocalDate;

/**
 * Represents a borrowing transaction connecting a Student, a physical Book,
 * and the Librarian who processed the transaction.
 */
public class BorrowTransaction {
    private int id;
    private Student student;
    private Book book;
    private Librarian librarian;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private TransactionStatus status;

    // Default constructor
    public BorrowTransaction() {
        this.status = TransactionStatus.ISSUED;
        this.fine = 0.0;
    }

    // Constructor for a new book issue transaction
    public BorrowTransaction(Student student, Book book, Librarian librarian,
                             LocalDate issueDate, LocalDate dueDate) {
        this(0, student, book, librarian, issueDate, dueDate, null, 0.0, TransactionStatus.ISSUED);
    }

    // Full constructor
    public BorrowTransaction(int id, Student student, Book book, Librarian librarian,
                             LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                             double fine, TransactionStatus status) {
        this.id = id;
        this.student = student;
        this.book = book;
        this.librarian = librarian;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.status = (status != null) ? status : TransactionStatus.ISSUED;
    }

    // Status checks
    public boolean isReturned() {
        return this.status == TransactionStatus.RETURNED;
    }

    public boolean isOverdue(LocalDate currentDate) {
        if (isReturned()) {
            return returnDate != null && returnDate.isAfter(dueDate);
        }
        return currentDate != null && currentDate.isAfter(dueDate);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction #" + id + " [" + status + "] Book: '" +
               (book != null ? book.getTitle() : "Unknown") + "' (" +
               (book != null ? book.getAccessionId() : "-") + ") -> Student: " +
               (student != null ? student.getName() : "Unknown") + " (" +
               (student != null ? student.getKtuId() : "-") + "), Due: " + dueDate +
               (fine > 0 ? ", Fine: Rs." + fine : "");
    }
}
