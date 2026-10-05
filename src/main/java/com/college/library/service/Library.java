package com.college.library.service;

import com.college.library.dao.*;
import com.college.library.exception.LibraryException;
import com.college.library.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class managing the core business logic of the College Library.
 * 
 * Demonstrates:
 * - Multi-tiered Architecture: Sits between Presentation (GUI/CLI) and Data Access (DAO).
 * - Encapsulation of Business Logic: Validates library constraints (loan periods, fines,
 *   clearance, availability) before database persistence.
 * - Flexibility: Supports both database persistence (via DAOs) and in-memory operations.
 */
public class Library {
    public static final int DEFAULT_BORROW_DAYS = 14;      // Standard 14 days borrowing period
    public static final double DAILY_FINE_RATE = 2.0;       // Rs. 2.00 per day overdue fine

    private final boolean useDatabase;

    // DAOs for permanent MySQL storage
    private final CategoryDao categoryDao;
    private final LibrarianDao librarianDao;
    private final StudentDao studentDao;
    private final BookDao bookDao;
    private final BorrowTransactionDao transactionDao;

    // In-memory fallback collections
    private final List<Book> memBooks;
    private final List<Student> memStudents;
    private final List<Librarian> memLibrarians;
    private final List<Category> memCategories;
    private final List<BorrowTransaction> memTransactions;
    private int memNextTransactionId = 1;

    /**
     * Default constructor uses MySQL Database persistence by default.
     */
    public Library() {
        this(true);
    }

    /**
     * Parameterized constructor allowing in-memory mode (e.g. for offline unit testing).
     */
    public Library(boolean useDatabase) {
        this.useDatabase = useDatabase;

        this.categoryDao = new CategoryDao();
        this.librarianDao = new LibrarianDao();
        this.studentDao = new StudentDao();
        this.bookDao = new BookDao();
        this.transactionDao = new BorrowTransactionDao();

        this.memBooks = new ArrayList<>();
        this.memStudents = new ArrayList<>();
        this.memLibrarians = new ArrayList<>();
        this.memCategories = new ArrayList<>();
        this.memTransactions = new ArrayList<>();
    }

    public boolean isDatabaseMode() {
        return useDatabase;
    }

    // ==========================================
    // Category Operations
    // ==========================================

    public void addCategory(Category category) throws LibraryException {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            throw new LibraryException("Category name cannot be empty.");
        }

        if (useDatabase) {
            try {
                if (categoryDao.findByName(category.getName().trim()) != null) {
                    throw new LibraryException("Category '" + category.getName() + "' already exists.");
                }
                categoryDao.insert(category);
            } catch (SQLException e) {
                throw new LibraryException("Database error adding category: " + e.getMessage(), e);
            }
        } else {
            for (Category c : memCategories) {
                if (c.getName().equalsIgnoreCase(category.getName().trim())) {
                    throw new LibraryException("Category '" + category.getName() + "' already exists.");
                }
            }
            memCategories.add(category);
        }
    }

    public List<Category> getAllCategories() throws LibraryException {
        if (useDatabase) {
            try {
                return categoryDao.findAll();
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching categories: " + e.getMessage(), e);
            }
        }
        return Collections.unmodifiableList(memCategories);
    }

    public Category findCategoryByName(String name) throws LibraryException {
        if (name == null || name.trim().isEmpty()) return null;
        if (useDatabase) {
            try {
                return categoryDao.findByName(name.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error finding category: " + e.getMessage(), e);
            }
        }
        for (Category c : memCategories) {
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

        if (useDatabase) {
            try {
                if (librarianDao.findByUsername(librarian.getUsername().trim()) != null) {
                    throw new LibraryException("Username '" + librarian.getUsername() + "' is already taken.");
                }
                librarianDao.insert(librarian);
            } catch (SQLException e) {
                throw new LibraryException("Database error adding librarian: " + e.getMessage(), e);
            }
        } else {
            for (Librarian l : memLibrarians) {
                if (l.getUsername().equalsIgnoreCase(librarian.getUsername().trim())) {
                    throw new LibraryException("Username '" + librarian.getUsername() + "' is already taken.");
                }
            }
            memLibrarians.add(librarian);
        }
    }

    public Librarian authenticate(String username, String password) throws LibraryException {
        if (username == null || password == null) return null;

        if (useDatabase) {
            try {
                return librarianDao.authenticate(username.trim(), password);
            } catch (SQLException e) {
                throw new LibraryException("Database error during authentication: " + e.getMessage(), e);
            }
        } else {
            for (Librarian l : memLibrarians) {
                if (l.getUsername().equals(username.trim()) && l.getPassword().equals(password)) {
                    return l;
                }
            }
            return null;
        }
    }

    public List<Librarian> getAllLibrarians() throws LibraryException {
        if (useDatabase) {
            try {
                return librarianDao.findAll();
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching librarians: " + e.getMessage(), e);
            }
        }
        return Collections.unmodifiableList(memLibrarians);
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

        if (useDatabase) {
            try {
                if (studentDao.findByKtuId(student.getKtuId().trim()) != null) {
                    throw new LibraryException("A student with KTU ID '" + student.getKtuId() + "' already exists.");
                }
                studentDao.insert(student);
            } catch (SQLException e) {
                throw new LibraryException("Database error adding student: " + e.getMessage(), e);
            }
        } else {
            if (findStudentByKtuId(student.getKtuId()) != null) {
                throw new LibraryException("A student with KTU ID '" + student.getKtuId() + "' already exists.");
            }
            memStudents.add(student);
        }
    }

    public void updateStudent(Student student) throws LibraryException {
        if (student == null || student.getKtuId() == null) {
            throw new LibraryException("Student or KTU ID cannot be null for update.");
        }
        if (useDatabase) {
            try {
                studentDao.update(student);
            } catch (SQLException e) {
                throw new LibraryException("Database error updating student: " + e.getMessage(), e);
            }
        }
    }

    public Student findStudentByKtuId(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) return null;

        if (useDatabase) {
            try {
                return studentDao.findByKtuId(ktuId.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error finding student: " + e.getMessage(), e);
            }
        } else {
            for (Student s : memStudents) {
                if (s.getKtuId().equalsIgnoreCase(ktuId.trim())) {
                    return s;
                }
            }
            return null;
        }
    }

    public List<Student> searchStudentsByName(String nameQuery) throws LibraryException {
        if (nameQuery == null || nameQuery.trim().isEmpty()) return Collections.emptyList();

        if (useDatabase) {
            try {
                return studentDao.searchByName(nameQuery.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error searching students: " + e.getMessage(), e);
            }
        } else {
            List<Student> results = new ArrayList<>();
            String query = nameQuery.trim().toLowerCase();
            for (Student s : memStudents) {
                if (s.getName() != null && s.getName().toLowerCase().contains(query)) {
                    results.add(s);
                }
            }
            return results;
        }
    }

    public List<Student> getAllStudents() throws LibraryException {
        if (useDatabase) {
            try {
                return studentDao.findAll();
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching students: " + e.getMessage(), e);
            }
        }
        return Collections.unmodifiableList(memStudents);
    }

    public boolean removeStudent(String ktuId) throws LibraryException {
        if (ktuId == null || ktuId.trim().isEmpty()) {
            throw new LibraryException("KTU ID is required to remove student.");
        }

        Student student = findStudentByKtuId(ktuId);
        if (student == null) {
            throw new LibraryException("Student with KTU ID '" + ktuId + "' not found.");
        }

        if (!hasClearance(ktuId)) {
            throw new LibraryException("Cannot remove student. Student has active borrowed books or pending dues.");
        }

        if (useDatabase) {
            try {
                return studentDao.deleteByKtuId(ktuId.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error removing student: " + e.getMessage(), e);
            }
        } else {
            return memStudents.remove(student);
        }
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

        if (useDatabase) {
            try {
                if (bookDao.findByAccessionId(book.getAccessionId().trim()) != null) {
                    throw new LibraryException("A book with Accession ID '" + book.getAccessionId() + "' already exists.");
                }
                bookDao.insert(book);
            } catch (SQLException e) {
                throw new LibraryException("Database error adding book: " + e.getMessage(), e);
            }
        } else {
            if (findBookByAccessionId(book.getAccessionId()) != null) {
                throw new LibraryException("A book with Accession ID '" + book.getAccessionId() + "' already exists.");
            }
            memBooks.add(book);
        }
    }

    public void updateBook(Book book) throws LibraryException {
        if (book == null || book.getAccessionId() == null) {
            throw new LibraryException("Book or Accession ID cannot be null for update.");
        }
        if (useDatabase) {
            try {
                bookDao.update(book);
            } catch (SQLException e) {
                throw new LibraryException("Database error updating book: " + e.getMessage(), e);
            }
        }
    }

    public Book findBookByAccessionId(String accessionId) throws LibraryException {
        if (accessionId == null || accessionId.trim().isEmpty()) return null;

        if (useDatabase) {
            try {
                return bookDao.findByAccessionId(accessionId.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error finding book: " + e.getMessage(), e);
            }
        } else {
            for (Book b : memBooks) {
                if (b.getAccessionId().equalsIgnoreCase(accessionId.trim())) {
                    return b;
                }
            }
            return null;
        }
    }

    public List<Book> searchBooksByTitle(String titleQuery) throws LibraryException {
        if (titleQuery == null || titleQuery.trim().isEmpty()) return Collections.emptyList();

        if (useDatabase) {
            try {
                return bookDao.searchByTitle(titleQuery.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error searching books: " + e.getMessage(), e);
            }
        } else {
            List<Book> results = new ArrayList<>();
            String query = titleQuery.trim().toLowerCase();
            for (Book b : memBooks) {
                if (b.getTitle() != null && b.getTitle().toLowerCase().contains(query)) {
                    results.add(b);
                }
            }
            return results;
        }
    }

    public List<Book> searchBooksByAuthor(String authorQuery) throws LibraryException {
        if (authorQuery == null || authorQuery.trim().isEmpty()) return Collections.emptyList();

        if (useDatabase) {
            try {
                return bookDao.searchByAuthor(authorQuery.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error searching books by author: " + e.getMessage(), e);
            }
        } else {
            List<Book> results = new ArrayList<>();
            String query = authorQuery.trim().toLowerCase();
            for (Book b : memBooks) {
                if (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(query)) {
                    results.add(b);
                }
            }
            return results;
        }
    }

    public List<Book> getAllBooks() throws LibraryException {
        if (useDatabase) {
            try {
                return bookDao.findAll();
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching books: " + e.getMessage(), e);
            }
        }
        return Collections.unmodifiableList(memBooks);
    }

    public boolean removeBook(String accessionId) throws LibraryException {
        Book book = findBookByAccessionId(accessionId);
        if (book == null) {
            throw new LibraryException("Book with Accession ID '" + accessionId + "' not found.");
        }
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new LibraryException("Cannot remove book. It is currently issued to a student.");
        }

        if (useDatabase) {
            try {
                return bookDao.deleteByAccessionId(accessionId.trim());
            } catch (SQLException e) {
                throw new LibraryException("Database error removing book: " + e.getMessage(), e);
            }
        } else {
            return memBooks.remove(book);
        }
    }

    // ==========================================
    // Borrow & Return Operations
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

        if (librarian == null) {
            List<Librarian> allLibrarians = getAllLibrarians();
            librarian = allLibrarians.isEmpty() ? null : allLibrarians.get(0);
        }

        LocalDate dueDate = issueDate.plusDays(DEFAULT_BORROW_DAYS);

        if (useDatabase) {
            try {
                BorrowTransaction tx = new BorrowTransaction(
                        0,
                        student,
                        book,
                        librarian,
                        issueDate,
                        dueDate,
                        null,
                        0.0,
                        TransactionStatus.ISSUED
                );
                transactionDao.insert(tx);
                bookDao.updateStatus(book.getId(), BookStatus.ISSUED);
                book.setStatus(BookStatus.ISSUED);
                return tx;
            } catch (SQLException e) {
                throw new LibraryException("Database error issuing book: " + e.getMessage(), e);
            }
        } else {
            BorrowTransaction tx = new BorrowTransaction(
                    memNextTransactionId++,
                    student,
                    book,
                    librarian,
                    issueDate,
                    dueDate,
                    null,
                    0.0,
                    TransactionStatus.ISSUED
            );
            book.markIssued();
            memTransactions.add(tx);
            return tx;
        }
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

        if (useDatabase) {
            try {
                BorrowTransaction tx = transactionDao.findActiveByAccessionId(accessionId.trim());
                if (tx == null) {
                    throw new LibraryException("No active borrowing record found for book '" + accessionId + "'.");
                }

                double fine = calculateFine(tx.getDueDate(), returnDate);
                transactionDao.updateReturn(tx.getId(), returnDate, fine, TransactionStatus.RETURNED);
                bookDao.updateStatus(book.getId(), BookStatus.AVAILABLE);

                tx.setReturnDate(returnDate);
                tx.setFine(fine);
                tx.setStatus(TransactionStatus.RETURNED);
                book.setStatus(BookStatus.AVAILABLE);

                return tx;
            } catch (SQLException e) {
                throw new LibraryException("Database error returning book: " + e.getMessage(), e);
            }
        } else {
            BorrowTransaction activeTx = null;
            for (BorrowTransaction tx : memTransactions) {
                if (tx.getBook().getAccessionId().equalsIgnoreCase(accessionId) &&
                    tx.getStatus() == TransactionStatus.ISSUED) {
                    activeTx = tx;
                    break;
                }
            }

            if (activeTx == null) {
                throw new LibraryException("No active borrowing record found for book '" + accessionId + "'.");
            }

            double fine = calculateFine(activeTx.getDueDate(), returnDate);
            activeTx.setReturnDate(returnDate);
            activeTx.setFine(fine);
            activeTx.setStatus(TransactionStatus.RETURNED);
            book.markReturned();

            return activeTx;
        }
    }

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

    public boolean hasClearance(String ktuId) throws LibraryException {
        if (useDatabase) {
            try {
                List<BorrowTransaction> active = transactionDao.findActiveByStudentKtuId(ktuId);
                return active.isEmpty();
            } catch (SQLException e) {
                throw new LibraryException("Database error checking clearance: " + e.getMessage(), e);
            }
        } else {
            for (BorrowTransaction tx : memTransactions) {
                if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId) &&
                    tx.getStatus() == TransactionStatus.ISSUED) {
                    return false;
                }
            }
            return true;
        }
    }

    public List<BorrowTransaction> getActiveBorrowsByStudent(String ktuId) throws LibraryException {
        if (useDatabase) {
            try {
                return transactionDao.findActiveByStudentKtuId(ktuId);
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching active borrows: " + e.getMessage(), e);
            }
        } else {
            List<BorrowTransaction> list = new ArrayList<>();
            for (BorrowTransaction tx : memTransactions) {
                if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId) &&
                    tx.getStatus() == TransactionStatus.ISSUED) {
                    list.add(tx);
                }
            }
            return list;
        }
    }

    public List<BorrowTransaction> getTransactionHistoryByStudent(String ktuId) throws LibraryException {
        if (useDatabase) {
            try {
                return transactionDao.findAllByStudentKtuId(ktuId);
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching transaction history: " + e.getMessage(), e);
            }
        } else {
            List<BorrowTransaction> list = new ArrayList<>();
            for (BorrowTransaction tx : memTransactions) {
                if (tx.getStudent().getKtuId().equalsIgnoreCase(ktuId)) {
                    list.add(tx);
                }
            }
            return list;
        }
    }

    public List<BorrowTransaction> getAllTransactions() throws LibraryException {
        if (useDatabase) {
            try {
                return transactionDao.findAll();
            } catch (SQLException e) {
                throw new LibraryException("Database error fetching transactions: " + e.getMessage(), e);
            }
        }
        return Collections.unmodifiableList(memTransactions);
    }

    // ==========================================
    // Dashboard Statistics
    // ==========================================

    public int getTotalBooksCount() throws LibraryException {
        return getAllBooks().size();
    }

    public int getAvailableBooksCount() throws LibraryException {
        int count = 0;
        for (Book b : getAllBooks()) {
            if (b.isAvailable()) count++;
        }
        return count;
    }

    public int getIssuedBooksCount() throws LibraryException {
        int count = 0;
        for (Book b : getAllBooks()) {
            if (b.getStatus() == BookStatus.ISSUED) count++;
        }
        return count;
    }

    public int getOverdueBooksCount(LocalDate currentDate) throws LibraryException {
        int count = 0;
        for (BorrowTransaction tx : getAllTransactions()) {
            if (tx.getStatus() == TransactionStatus.ISSUED && tx.isOverdue(currentDate)) {
                count++;
            }
        }
        return count;
    }

    public int getTotalStudentsCount() throws LibraryException {
        return getAllStudents().size();
    }
}
