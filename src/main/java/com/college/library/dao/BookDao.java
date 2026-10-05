package com.college.library.dao;

import com.college.library.database.DatabaseConnection;
import com.college.library.model.Book;
import com.college.library.model.BookStatus;
import com.college.library.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'books' table.
 * 
 * Note: Each physical copy owned by the library has its own database row
 * with a unique accession_id.
 */
public class BookDao {

    private static final String SELECT_BASE = 
        "SELECT b.id, b.accession_id, b.title, b.author, b.isbn, b.publisher, " +
        "b.publication_year, b.status, c.id AS category_id, c.name AS category_name " +
        "FROM books b JOIN categories c ON b.category_id = c.id ";

    public void insert(Book book) throws SQLException {
        String sql = "INSERT INTO books (accession_id, title, author, isbn, publisher, publication_year, category_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getAccessionId());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getIsbn());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublicationYear());
            stmt.setInt(7, book.getCategory() != null ? book.getCategory().getId() : 1);
            stmt.setString(8, book.getStatus() != null ? book.getStatus().name() : BookStatus.AVAILABLE.name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    book.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publisher = ?, publication_year = ?, category_id = ?, status = ? " +
                     "WHERE accession_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getPublicationYear());
            stmt.setInt(6, book.getCategory() != null ? book.getCategory().getId() : 1);
            stmt.setString(7, book.getStatus().name());
            stmt.setString(8, book.getAccessionId());
            stmt.executeUpdate();
        }
    }

    public void updateStatus(int bookId, BookStatus status) throws SQLException {
        String sql = "UPDATE books SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public boolean deleteByAccessionId(String accessionId) throws SQLException {
        String sql = "DELETE FROM books WHERE accession_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accessionId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Book findById(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }
        return null;
    }

    public Book findByAccessionId(String accessionId) throws SQLException {
        String sql = SELECT_BASE + "WHERE LOWER(b.accession_id) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBook(rs);
                }
            }
        }
        return null;
    }

    public List<Book> searchByTitle(String titleQuery) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE LOWER(b.title) LIKE ? ORDER BY b.title ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + titleQuery.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        }
        return list;
    }

    public List<Book> searchByAuthor(String authorQuery) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE LOWER(b.author) LIKE ? ORDER BY b.author ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + authorQuery.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        }
        return list;
    }

    public List<Book> findByCategory(int categoryId) throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE b.category_id = ? ORDER BY b.title ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBook(rs));
                }
            }
        }
        return list;
    }

    public List<Book> findAll() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY b.accession_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToBook(rs));
            }
        }
        return list;
    }

    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Category category = new Category(
                rs.getInt("category_id"),
                rs.getString("category_name")
        );

        String statusStr = rs.getString("status");
        BookStatus status = BookStatus.AVAILABLE;
        if (statusStr != null) {
            try {
                status = BookStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return new Book(
                rs.getInt("id"),
                rs.getString("accession_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getString("publisher"),
                null, // edition optional
                rs.getInt("publication_year"),
                category,
                status
        );
    }
}
