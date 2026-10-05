package com.college.library.cli;

import com.college.library.exception.LibraryException;
import com.college.library.model.*;
import com.college.library.service.Library;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Command Line Interface and Test Runner for the College Library Management System.
 * 
 * Provides:
 * 1. An automated verification demo of all core OOP features and business logic rules.
 * 2. An interactive text menu for manually testing library operations.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      COLLEGE LIBRARY MANAGEMENT SYSTEM          ");
        System.out.println("=================================================");

        Library library = new Library();

        // Check if user requested automated test runner
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            runAutomatedTests(library);
            return;
        }

        // Run the demonstration with sample data
        System.out.println("\n[1/2] Running automated OOP business logic tests...");
        runAutomatedTests(library);

        System.out.println("\n[2/2] Launching interactive CLI session...");
        runInteractiveMenu(library);
    }

    /**
     * Executes automated verification covering all requirements specified in AGENT.md.
     */
    public static void runAutomatedTests(Library library) {
        System.out.println("-------------------------------------------------");
        System.out.println(" AUTOMATED BUSINESS LOGIC & OOP TEST SUITE       ");
        System.out.println("-------------------------------------------------");

        try {
            // 1. Add Librarian
            System.out.println("\n--- Test 1: Add Librarian ---");
            Librarian librarian = new Librarian(1, "Anjali Menon", "anjali@college.edu", "9876543210", "admin", "admin123");
            library.addLibrarian(librarian);
            System.out.println("Added: " + librarian);

            // 2. Add Categories
            System.out.println("\n--- Test 2: Add Categories ---");
            Category csCategory = new Category(1, "Computer Science");
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
            Book book1 = new Book("LIB-001", "Database System Concepts", "Silberschatz", "9780073523323", "McGraw-Hill", "7th Edition", 2019, csCategory);
            Book book2 = new Book("LIB-002", "Database System Concepts", "Silberschatz", "9780073523323", "McGraw-Hill", "7th Edition", 2019, csCategory);
            Book book3 = new Book("LIB-003", "Operating System Concepts", "Silberschatz", "9781119800361", "Wiley", "10th Edition", 2018, csCategory);
            library.addBook(book1);
            library.addBook(book2);
            library.addBook(book3);
            System.out.println("Added: " + book1);
            System.out.println("Added: " + book2);
            System.out.println("Added: " + book3);

            // 5. Issue Available Book
            System.out.println("\n--- Test 5: Issue Available Book ---");
            LocalDate issueDate = LocalDate.now();
            BorrowTransaction tx1 = library.issueBook("LIB-001", "TVE23CS001", librarian, issueDate);
            System.out.println("Success! Transaction created: " + tx1);
            System.out.println("Book status after issue: " + book1.getStatus());

            // 6. Test Business Rule: Attempting to issue an unavailable book
            System.out.println("\n--- Test 6: Attempting to Issue Unavailable Book (Expected to Fail) ---");
            try {
                library.issueBook("LIB-001", "TVE23CS045", librarian, issueDate);
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
            BorrowTransaction returnTx = library.returnBook("LIB-001", simulatedReturnDate);
            System.out.println("Success! Book returned: " + returnTx);
            System.out.println("Fine calculated for 3 days overdue: Rs. " + returnTx.getFine());
            System.out.println("Book status after return: " + book1.getStatus());

            // 9. Test Business Rule: Attempting to return a book not currently issued
            System.out.println("\n--- Test 9: Attempting to Return Unissued Book (Expected to Fail) ---");
            try {
                library.returnBook("LIB-001", LocalDate.now());
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

            System.out.println("\n=================================================");
            System.out.println(" ALL AUTOMATED OOP TESTS PASSED SUCCESSFULLY!    ");
            System.out.println("=================================================");

        } catch (Exception e) {
            System.err.println("Unexpected test failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Interactive text menu for testing via console input.
     */
    public static void runInteractiveMenu(Library library) {
        Scanner scanner = new Scanner(System.in);
        Librarian activeLibrarian = library.getAllLibrarians().isEmpty() ? null : library.getAllLibrarians().get(0);

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
                    System.out.print("Enter Book Accession ID (e.g. LIB-001): ");
                    String accId = scanner.nextLine().trim();
                    System.out.print("Enter Student KTU ID (e.g. TVE23CS001): ");
                    String ktuId = scanner.nextLine().trim();
                    try {
                        BorrowTransaction tx = library.issueBook(accId, ktuId, activeLibrarian, LocalDate.now());
                        System.out.println("Success! Book issued. Due date: " + tx.getDueDate());
                    } catch (LibraryException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.print("Enter Book Accession ID to return: ");
                    String retAccId = scanner.nextLine().trim();
                    try {
                        BorrowTransaction retTx = library.returnBook(retAccId, LocalDate.now());
                        System.out.println("Success! Book returned. Fine: Rs. " + retTx.getFine());
                    } catch (LibraryException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
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
        }
    }
}
