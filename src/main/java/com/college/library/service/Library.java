package com.college.library.service;

import com.college.library.exception.LibraryException;
import com.college.library.model.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class managing the core business logic of the College Library.
 * 
 * Demonstrates:
 * - Composition/Aggregation: Manages collections of Books, Students, Librarians,
 *   Categories, and BorrowTransactions.
 * - Business Rule Validation: Enforces constraints (e.g. cannot issue an already issued book)
 *   and throws custom LibraryException.
 */
public class Library {
    // Standard library rules
    public static final int DEFAULT_BORROW_DAYS = 14;      // 14 days borrowing period
    public static final double DAILY_FINE_RATE = 2.0;       // Rs. 2 per day overdue fine

    // In-memory collections (Phase 1 & 2)
    private final List<Book> books;
    private final List<Student> students;
    private final List<Librarian> librarians;
    private final List<Category> categories;
    private final List<BorrowTransaction> transactions;

    // Transaction ID generator for in-memory tracking
    private int nextTransactionId = 1;

    public Library() {
        this.books = new ArrayList<>();
        this.students = new ArrayList<>();
        this.librarians = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    // ==========================================
    // Category Operations
    // ==========================================

    public void addCategory(Category category) throws LibraryException {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new LibraryException("Category name cannot be empty.");
        }
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(category.getName().trim())) {
                throw new LibraryException("Category '" + category.getName() + "' already exists.");
            }
        }
        categories.add(category);
    }

    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Category findCategoryByName(String name) {
        if (name == null) return null;
        for (Category c : categories) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                return c;
            }
        }
        return null;
    }

    // ==========================================
    // Librarian Operations
    // ==========================================

    public void addLibrarian(Librarian librarian) throws LibraryException {
        if (librarian == null) {
            throw new LibraryException("Librarian details cannot be null.");
        }
        if (librarian.getUsername() == null || librarian.getUsername().trim().isEmpty()) {
            throw new LibraryException("Librarian username is required.");
        }
        for (Librarian l : librarians) {
            if (l.getUsername().equalsIgnoreCase(librarian.getUsername().trim())) {
                throw new LibraryException("Username '" + librarian.getUsername() + "' is already taken.");
            }
        }
        librarians.add(librarian);
    }

    public Librarian authenticate(String username, String password) {
        if (username == null || password == null) return null;
        for (Librarian l : librarians) {
            if (l.getUsername().equals(username.trim()) && l.getPassword().equals(password)) {
                return l;
            }
        }
        return null;
    }

    public List<Librarian> getAllLibrarians() {
        return Collections.unmodifiableList(librarians);
    }

    // ==========================================
    // Student Operations
    // ==========================================

    public void addStudent(Student student) throws LibraryException {
        if (student == null) {
            throw new LibraryException("Student details cannot be null.");
        }
        if (student.getKtuId() == null || student.getKtuId().trim().isEmpty()) {
            throw new LibraryException("Student KTU ID is required.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new LibraryException("Student name is required.");
        }
        if (findStudentByKtuId(student.getKtuId()) != null) {
            throw new LibraryException("A student with KTU ID '" + student.getKtuId() + "' already exists.");
        }
        students.add(student);
    }

    public Student findStudentByKtuId(String ktuId) {
        if (ktuId == null) return null;
        for (Student s : students) {
            if (s.getKtuId().equalsIgnoreCase(ktuId.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Student> searchStudentsByName(String nameQuery) {
        List<Student> results = new ArrayList<>();
        if (nameQuery == null || nameQuery.trim().isEmpty()) return results;
        String query = nameQuery.trim().toLowerCase();
        for (Student s : students) {
            if (s.getName() != null && s.getName().toLowerCase().contains(query)) {
                results.add(s);
            }
        }
        return results;
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    public boolean removeStudent(String ktuId) throws LibraryException {
        Student student = findStudentByKtuId(ktuId);
        if (student == null) {
            throw new LibraryException("Student with KTU ID '" + ktuId + "' not found.");
        }
        // Verify clearance before removing
        if (!hasClearance(ktuId)) {
            throw new LibraryException("Cannot remove student. Student has active borrowed books or unpaid fines.");
        }
        return students.remove(student);
    }

    // ==========================================
    // Book Operations
    // ==========================================

    public void addBook(Book book) throws LibraryException {
        if (book == null) {
            throw new LibraryException("Book details cannot be null.");
        }
        if (book.getAccessionId() == null || book.getAccessionId().trim().isEmpty()) {
            throw new LibraryException("Book Accession ID is required.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new LibraryException("Book title is required.");
        }
        if (findBookByAccessionId(book.getAccessionId()) != null) {
            throw new LibraryException("A book with Accession ID '" + book.getAccessionId() + "' already exists.");
        }
        books.add(book);
    }

    public Book findBookByAccessionId(String accessionId) {
        if (accessionId == null) return null;
        for (Book b : books) {
            if (b.getAccessionId().equalsIgnoreCase(accessionId.trim())) {
                return b;
            }
        }
        return null;
    }

    public List<Book> searchBooksByTitle(String titleQuery) {
        List<Book> results = new ArrayList<>();
        if (titleQuery == null || titleQuery.trim().isEmpty()) return results;
        String query = titleQuery.trim().toLowerCase();
        for (Book b : books) {
            if (b.getTitle() != null && b.getTitle().toLowerCase().contains(query)) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchBooksByAuthor(String authorQuery) {
        List<Book> results = new ArrayList<>();
        if (authorQuery == null || authorQuery.trim().isEmpty()) return results;
        String query = authorQuery.trim().toLowerCase();
        for (Book b : books) {
            if (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(query)) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchBooksByCategory(String categoryName) {
        List<Book> results = new ArrayList<>();
        if (categoryName == null || categoryName.trim().isEmpty()) return results;
        for (Book b : books) {
            if (b.getCategory() != null && b.getCategory().getName().equalsIgnoreCase(categoryName.trim())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(books);
    }

    public boolean removeBook(String accessionId) throws LibraryException {
        Book book = findBookByAccessionId(accessionId);
        if (book == null) {
            throw new LibraryException("Book with Accession ID '" + accessionId + "' not found.");
        }
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new LibraryException("Cannot remove book. It is currently issued to a student.");
        }
        return books.remove(book);
    }

    // ==========================================
    // Borrow and Return Operations
    // ==========================================

    /**
     * Issues an available physical book to a registered student.
     */
    public BorrowTransaction issueBook(String accessionId, String ktuId, Librarian librarian, LocalDate issueDate)
            throws LibraryException {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }

        Book book = findBookByAccessionId(accessionId);
        if (book == null) {
            throw new LibraryException("Book with Accession ID '" + accessionId + "' not found.");
        }

        Student student = findStudentByKtuId(ktuId);
        if (student == null) {
            throw new LibraryException("Student with KTU ID '" + ktuId + "' not found.");
        }

        if (!book.isAvailable()) {
            throw new LibraryException("Cannot issue book. Current status is " + book.getStatus() + ".");
        }

        LocalDate dueDate = issueDate.plusDays(DEFAULT_BORROW_DAYS);

        // Create transaction
        BorrowTransaction transaction = new BorrowTransaction(
                nextTransactionId++,
                student,
                book,
                librarian,
                issueDate,
                dueDate,
                null,
                0.0,
                TransactionStatus.ISSUED
        );

        // Update book state
        book.markIssued();
        transactions.add(transaction);

        return transaction;
    }

    /**
     * Returns an issued book and calculates any overdue fine.
     */
    public BorrowTransaction returnBook(String accessionId, LocalDate returnDate) throws LibraryException {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }

        Book book = findBookByAccessionId(accessionId);
        if (book == null) {
            throw new LibraryException("Book with Accession ID '" + accessionId + "' not found.");
        }

        // Find the active issued transaction for this book
        BorrowTransaction activeTransaction = null;
        for (BorrowTransaction tx : transactions) {
            if (tx.getBook().getAccessionId().equalsIgnoreCase(accessionId) &&
                tx.getStatus() == TransactionStatus.ISSUED) {
                activeTransaction = tx;
                break;
            }
        }

        if (activeTransaction == null) {
            throw new LibraryException("No active borrowing record found for book '" + accessionId + "'.");
        }

        // Calculate fine if overdue
        double fine = calculateFine(activeTransaction.getDueDate(), returnDate);

        // Update transaction
        activeTransaction.setReturnDate(returnDate);
        activeTransaction.setFine(fine);
        activeTransaction.setStatus(TransactionStatus.RETURNED);

        // Update book status back to AVAILABLE
        book.markReturned();

        return activeTransaction;
    }

    /**
     * Calculates overdue fine based on due date and return date.
     */
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (dueDate != null && returnDate != null && returnDate.isAfter(dueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            return overdueDays * DAILY_FINE_RATE;
        }
        return 0.0;
    }

    // ==========================================
    // Clearance & History Operations
    // ==========================================

    /**
     * Checks if a student has library clearance (no currently borrowed books).
     */
    public boolean hasClearance(String ktuId) {
        for (BorrowTransaction tx : transactions) {
            if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId) &&
                tx.getStatus() == TransactionStatus.ISSUED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieves all active borrowed books for a specific student.
     */
    public List<BorrowTransaction> getActiveBorrowsByStudent(String ktuId) {
        List<BorrowTransaction> list = new ArrayList<>();
        for (BorrowTransaction tx : transactions) {
            if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId) &&
                tx.getStatus() == TransactionStatus.ISSUED) {
                list.add(tx);
            }
        }
        return list;
    }

    /**
     * Retrieves the complete borrowing history for a specific student.
     */
    public List<BorrowTransaction> getTransactionHistoryByStudent(String ktuId) {
        List<BorrowTransaction> list = new ArrayList<>();
        for (BorrowTransaction tx : transactions) {
            if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId)) {
                list.add(tx);
            }
        }
        return list;
    }

    public List<BorrowTransaction> getAllTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    // ==========================================
    // Dashboard Statistics
    // ==========================================

    public int getTotalBooksCount() {
        return books.size();
    }

    public int getAvailableBooksCount() {
        int count = 0;
        for (Book b : books) {
            if (b.isAvailable()) count++;
        }
        return count;
    }

    public int getIssuedBooksCount() {
        int count = 0;
        for (Book b : books) {
            if (b.getStatus() == BookStatus.ISSUED) count++;
        }
        return count;
    }

    public int getOverdueBooksCount(LocalDate currentDate) {
        int count = 0;
        for (BorrowTransaction tx : transactions) {
            if (tx.getStatus() == TransactionStatus.ISSUED && tx.isOverdue(currentDate)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalStudentsCount() {
        return students.size();
    }
}
