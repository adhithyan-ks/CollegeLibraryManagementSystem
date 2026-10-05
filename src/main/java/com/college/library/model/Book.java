package com.college.library.model;

/**
 * Represents an individual physical book in the library.
 * 
 * Design Note:
 * Each physical copy owned by the library has its own Book record and unique accessionId.
 * For example:
 *   - LIB-001: "Operating Systems" (Copy 1) -> AVAILABLE
 *   - LIB-002: "Operating Systems" (Copy 2) -> ISSUED
 */
public class Book {
    private int id;
    private String accessionId;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private String edition;
    private int publicationYear;
    private Category category;
    private BookStatus status;

    // Default constructor
    public Book() {
        this.status = BookStatus.AVAILABLE;
    }

    // Constructor without database ID (default status AVAILABLE)
    public Book(String accessionId, String title, String author, String isbn,
                String publisher, String edition, int publicationYear, Category category) {
        this(0, accessionId, title, author, isbn, publisher, edition, publicationYear, category, BookStatus.AVAILABLE);
    }

    // Full constructor
    public Book(int id, String accessionId, String title, String author, String isbn,
                String publisher, String edition, int publicationYear, Category category, BookStatus status) {
        this.id = id;
        this.accessionId = accessionId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.edition = edition;
        this.publicationYear = publicationYear;
        this.category = category;
        this.status = (status != null) ? status : BookStatus.AVAILABLE;
    }

    // Helper methods for business status checks
    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE;
    }

    public void markIssued() {
        this.status = BookStatus.ISSUED;
    }

    public void markReturned() {
        this.status = BookStatus.AVAILABLE;
    }

    public void markLost() {
        this.status = BookStatus.LOST;
    }

    public void markDamaged() {
        this.status = BookStatus.DAMAGED;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessionId() {
        return accessionId;
    }

    public void setAccessionId(String accessionId) {
        this.accessionId = accessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book [" + accessionId + "] " + title + " by " + author +
               " (Category: " + (category != null ? category.getName() : "None") +
               ", Status: " + status + ")";
    }
}
