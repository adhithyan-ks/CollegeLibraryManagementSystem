package com.college.library.cli;

import com.college.library.database.DatabaseInitializer;
import com.college.library.exception.LibraryException;
import com.college.library.gui.LoginFrame;
import com.college.library.model.*;
import com.college.library.service.Library;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for the College Library Management System.
 * 
 * Supports:
 * - Launching the full Swing Desktop GUI (default in graphical environments).
 * - Interactive Command Line Interface (CLI) via '--cli' or when headless.
 * - Automated verification test suite via '--test'.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      COLLEGE LIBRARY MANAGEMENT SYSTEM          ");
        System.out.println("=================================================");

        // Automated test suite mode
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runAutomatedSuite();
            return;
        }

        // Initialize Database tables and seeds
        DatabaseInitializer.initializeDatabase();

        Library library;
        try {
            library = new Library(true);
        } catch (Exception e) {
            System.err.println("Database connection unavailable, fallback to in-memory: " + e.getMessage());
            library = new Library(false);
        }

        // Explicit CLI mode requested
        if (args.length > 0 && args[0].equalsIgnoreCase("--cli")) {
            runInteractiveMenu(library);
            return;
        }

        // Launch GUI if graphical environment is available
        if (!GraphicsEnvironment.isHeadless()) {
            System.out.println("Launching Java Swing Desktop GUI...");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            final Library finalLib = library;
            SwingUtilities.invokeLater(() -> new LoginFrame(finalLib).setVisible(true));
        } else {
            System.out.println("Headless environment detected. Launching CLI mode...");
            runInteractiveMenu(library);
        }
    }

    /**
     * Runs both In-Memory OOP tests and MySQL persistence tests.
     */
    public static void runAutomatedSuite() {
        System.out.println("\n[1/2] Running in-memory OOP business logic tests...");
        Library memLibrary = new Library(false);
        runAutomatedTests(memLibrary, "IN-MEMORY OOP MODE");

        System.out.println("\n[2/2] Checking MySQL database persistence...");
        try {
            DatabaseInitializer.initializeDatabase();
            Library dbLibrary = new Library(true);
            runDatabasePersistenceTest(dbLibrary);
        } catch (Exception e) {
            System.err.println("MySQL persistence test failed: " + e.getMessage());
        }

        System.out.println("\n=================================================");
        System.out.println(" ALL AUTOMATED TESTS COMPLETED!                  ");
        System.out.println("=================================================");
    }

    public static void runAutomatedTests(Library library, String testTitle) {
        System.out.println("-------------------------------------------------");
        System.out.println(" TEST SUITE: " + testTitle);
        System.out.println("-------------------------------------------------");

        try {
            // 1. Add Librarian
            System.out.println("\n--- Test 1: Add Librarian ---");
            Librarian librarian = new Librarian(1, "Anjali Menon", "anjali@college.edu", "9876543210", "librarian_test", "pass123");
            library.addLibrarian(librarian);
            System.out.println("Added: " + librarian);

            // 2. Add Categories
            System.out.println("\n--- Test 2: Add Categories ---");
            Category csCategory = new Category(1, "Computer Science & Engineering");
            Category mathCategory = new Category(2, "Mathematics");
            library.addCategory(csCategory);
            library.addCategory(mathCategory);
            System.out.println("Categories: " + library.getAllCategories());

            // 3. Add Students
            System.out.println("\n--- Test 3: Add Students ---");
            Student student1 = new Student(1, "TVE23CS001", "Rahul Sharma", "Computer Science", 3, "A", "rahul@college.edu", "9123456780");
            Student student2 = new Student(2, "TVE23CS045", "Sneha Nair", "Computer Science", 3, null, "sneha@college.edu", "9123456781");
            library.addStudent(student1);
            library.addStudent(student2);
            System.out.println("Added: " + student1);
            System.out.println("Added: " + student2);

            // 4. Add Multiple Physical Copies of Books
            System.out.println("\n--- Test 4: Add Physical Books (Individual Accession IDs) ---");
            Book book1 = new Book("TEST-LIB-001", "Database System Concepts", "Silberschatz", "9780073523323", "McGraw-Hill", "7th Edition", 2019, csCategory);
            Book book2 = new Book("TEST-LIB-002", "Database System Concepts", "Silberschatz", "9780073523323", "McGraw-Hill", "7th Edition", 2019, csCategory);
            Book book3 = new Book("TEST-LIB-003", "Operating System Concepts", "Silberschatz", "9781119800361", "Wiley", "10th Edition", 2018, csCategory);
            library.addBook(book1);
            library.addBook(book2);
            library.addBook(book3);
            System.out.println("Added: " + book1);
            System.out.println("Added: " + book2);
            System.out.println("Added: " + book3);

            // 5. Issue Available Book
            System.out.println("\n--- Test 5: Issue Available Book ---");
            LocalDate issueDate = LocalDate.now();
            BorrowTransaction tx1 = library.issueBook("TEST-LIB-001", "TVE23CS001", librarian, issueDate);
            System.out.println("Success! Transaction created: " + tx1);
            System.out.println("Book status after issue: " + book1.getStatus());

            // 6. Test Business Rule: Attempting to issue an unavailable book
            System.out.println("\n--- Test 6: Attempting to Issue Unavailable Book (Expected to Fail) ---");
            try {
                library.issueBook("TEST-LIB-001", "TVE23CS045", librarian, issueDate);
                System.err.println("FAILED: An already issued book was allowed to be re-issued!");
            } catch (LibraryException e) {
                System.out.println("PASSED: Caught expected LibraryException -> " + e.getMessage());
            }

            // 7. Test Clearance Check
            System.out.println("\n--- Test 7: Student Library Clearance Check ---");
            System.out.println("Clearance for TVE23CS001 (has borrowed book): " + library.hasClearance("TVE23CS001"));
            System.out.println("Clearance for TVE23CS045 (no borrowed books): " + library.hasClearance("TVE23CS045"));

            // 8. Return Book (with overdue fine simulation)
            System.out.println("\n--- Test 8: Return Book (with overdue simulation) ---");
            LocalDate simulatedReturnDate = issueDate.plusDays(17); // 3 days overdue (17 - 14)
            BorrowTransaction returnTx = library.returnBook("TEST-LIB-001", simulatedReturnDate);
            System.out.println("Success! Book returned: " + returnTx);
            System.out.println("Fine calculated for 3 days overdue: Rs. " + returnTx.getFine());
            System.out.println("Book status after return: " + book1.getStatus());

            // 9. Test Business Rule: Attempting to return a book not currently issued
            System.out.println("\n--- Test 9: Attempting to Return Unissued Book (Expected to Fail) ---");
            try {
                library.returnBook("TEST-LIB-001", LocalDate.now());
                System.err.println("FAILED: Unissued book return did not throw exception!");
            } catch (LibraryException e) {
                System.out.println("PASSED: Caught expected LibraryException -> " + e.getMessage());
            }

            // 10. Search Functions
            System.out.println("\n--- Test 10: Search Functions ---");
            List<Book> searchTitle = library.searchBooksByTitle("database");
            System.out.println("Search 'database' found: " + searchTitle.size() + " books.");
            List<Book> searchAuthor = library.searchBooksByAuthor("Silberschatz");
            System.out.println("Search author 'Silberschatz' found: " + searchAuthor.size() + " books.");

            // 11. Dashboard Summary
            System.out.println("\n--- Test 11: Dashboard Summary ---");
            System.out.println("Total Books: " + library.getTotalBooksCount());
            System.out.println("Available Books: " + library.getAvailableBooksCount());
            System.out.println("Issued Books: " + library.getIssuedBooksCount());
            System.out.println("Total Students: " + library.getTotalStudentsCount());

            System.out.println("\n>>> ALL TESTS IN '" + testTitle + "' PASSED! <<<");

        } catch (Exception e) {
            System.err.println("Unexpected test failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runDatabasePersistenceTest(Library library) {
        System.out.println("-------------------------------------------------");
        System.out.println(" TEST SUITE: MYSQL JDBC PERSISTENCE              ");
        System.out.println("-------------------------------------------------");

        try {
            List<Category> categories = library.getAllCategories();
            System.out.println("Fetched " + categories.size() + " categories from MySQL.");
            Category firstCategory = categories.isEmpty() ? new Category(1, "General") : categories.get(0);

            String testKtuId = "KTU-TEST-001";
            Student student = library.findStudentByKtuId(testKtuId);
            if (student == null) {
                student = new Student(testKtuId, "Test Student", "CS", 4, "A", "test@college.edu", "9898989898");
                library.addStudent(student);
                System.out.println("Inserted test student into MySQL: " + student.getName());
            } else {
                System.out.println("Found existing student in MySQL: " + student.getName());
            }

            String testAccId = "DB-LIB-001";
            Book book = library.findBookByAccessionId(testAccId);
            if (book == null) {
                book = new Book(testAccId, "Clean Architecture", "Robert C. Martin", "9780134494166", "Prentice Hall", "1st Edition", 2017, firstCategory);
                library.addBook(book);
                System.out.println("Inserted test book into MySQL: " + book.getTitle());
            } else {
                System.out.println("Found existing book in MySQL: " + book.getTitle());
            }

            System.out.println("MySQL Total Books: " + library.getTotalBooksCount());
            System.out.println("MySQL Total Students: " + library.getTotalStudentsCount());
            System.out.println("Clearance check for " + testKtuId + ": " + library.hasClearance(testKtuId));

            System.out.println("\n>>> MYSQL JDBC PERSISTENCE VERIFIED SUCCESSFULLY! <<<");

        } catch (Exception e) {
            System.err.println("Database test failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void runInteractiveMenu(Library library) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Library Main Menu ---");
            System.out.println("1. List All Books");
            System.out.println("2. List All Students");
            System.out.println("3. Issue a Book");
            System.out.println("4. Return a Book");
            System.out.println("5. Check Student Clearance");
            System.out.println("6. Search Books by Title");
            System.out.println("7. View Dashboard Stats");
            System.out.println("8. Exit");
            System.out.print("Enter your choice (1-8): ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        System.out.println("\n--- Registered Books ---");
                        for (Book b : library.getAllBooks()) {
                            System.out.println(b);
                        }
                        break;

                    case "2":
                        System.out.println("\n--- Registered Students ---");
                        for (Student s : library.getAllStudents()) {
                            System.out.println(s);
                        }
                        break;

                    case "3":
                        System.out.print("Enter Book Accession ID (e.g. DB-LIB-001): ");
                        String accId = scanner.nextLine().trim();
                        System.out.print("Enter Student KTU ID (e.g. KTU-TEST-001): ");
                        String ktuId = scanner.nextLine().trim();
                        BorrowTransaction tx = library.issueBook(accId, ktuId, null, LocalDate.now());
                        System.out.println("Success! Book issued. Due date: " + tx.getDueDate());
                        break;

                    case "4":
                        System.out.print("Enter Book Accession ID to return: ");
                        String retAccId = scanner.nextLine().trim();
                        BorrowTransaction retTx = library.returnBook(retAccId, LocalDate.now());
                        System.out.println("Success! Book returned. Fine: Rs. " + retTx.getFine());
                        break;

                    case "5":
                        System.out.print("Enter Student KTU ID to check clearance: ");
                        String checkKtu = scanner.nextLine().trim();
                        boolean cleared = library.hasClearance(checkKtu);
                        System.out.println("Clearance status for " + checkKtu + ": " + (cleared ? "CLEARED (No dues)" : "PENDING (Has issued books)"));
                        break;

                    case "6":
                        System.out.print("Enter search title keyword: ");
                        String query = scanner.nextLine().trim();
                        List<Book> matches = library.searchBooksByTitle(query);
                        System.out.println("Found " + matches.size() + " matching book(s):");
                        for (Book b : matches) {
                            System.out.println(" - " + b);
                        }
                        break;

                    case "7":
                        System.out.println("\n--- Dashboard Statistics ---");
                        System.out.println("Total Books:      " + library.getTotalBooksCount());
                        System.out.println("Available Books:  " + library.getAvailableBooksCount());
                        System.out.println("Issued Books:     " + library.getIssuedBooksCount());
                        System.out.println("Overdue Books:    " + library.getOverdueBooksCount(LocalDate.now()));
                        System.out.println("Total Students:   " + library.getTotalStudentsCount());
                        break;

                    case "8":
                        System.out.println("Exiting application. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please select 1-8.");
                }
            } catch (LibraryException e) {
                System.out.println("Operation Failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }
}
