package com.college.library.dao;

import com.college.library.database.DatabaseConnection;
import com.college.library.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'borrow_transactions' table.
 */
public class BorrowTransactionDao {

    private static final String SELECT_BASE =
        "SELECT t.id, t.issue_date, t.due_date, t.return_date, t.fine, t.status, " +
        "       s.id AS s_id, s.ktu_id, s.name AS s_name, s.branch, s.semester, s.batch, s.email AS s_email, s.phone AS s_phone, " +
        "       b.id AS b_id, b.accession_id, b.title AS b_title, b.author AS b_author, b.isbn, b.publisher, b.publication_year, b.status AS b_status, " +
        "       c.id AS c_id, c.name AS c_name, " +
        "       l.id AS l_id, l.name AS l_name, l.email AS l_email, l.phone AS l_phone, l.username " +
        "FROM borrow_transactions t " +
        "JOIN students s ON t.student_id = s.id " +
        "JOIN books b ON t.book_id = b.id " +
        "JOIN categories c ON b.category_id = c.id " +
        "JOIN librarians l ON t.librarian_id = l.id ";

    public void insert(BorrowTransaction tx) throws SQLException {
        String sql = "INSERT INTO borrow_transactions (student_id, book_id, librarian_id, issue_date, due_date, return_date, fine, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tx.getStudent().getId());
            stmt.setInt(2, tx.getBook().getId());
            stmt.setInt(3, tx.getLibrarian().getId());
            stmt.setDate(4, Date.valueOf(tx.getIssueDate()));
            stmt.setDate(5, Date.valueOf(tx.getDueDate()));
            stmt.setDate(6, tx.getReturnDate() != null ? Date.valueOf(tx.getReturnDate()) : null);
            stmt.setDouble(7, tx.getFine());
            stmt.setString(8, tx.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    tx.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateReturn(int transactionId, LocalDate returnDate, double fine, TransactionStatus status) throws SQLException {
        String sql = "UPDATE borrow_transactions SET return_date = ?, fine = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, returnDate != null ? Date.valueOf(returnDate) : null);
            stmt.setDouble(2, fine);
            stmt.setString(3, status.name());
            stmt.setInt(4, transactionId);
            stmt.executeUpdate();
        }
    }

    public BorrowTransaction findActiveByAccessionId(String accessionId) throws SQLException {
        String sql = SELECT_BASE + "WHERE LOWER(b.accession_id) = LOWER(?) AND t.status = 'ISSUED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accessionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTransaction(rs);
                }
            }
        }
        return null;
    }

    public List<BorrowTransaction> findActiveByStudentKtuId(String ktuId) throws SQLException {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE LOWER(s.ktu_id) = LOWER(?) AND t.status = 'ISSUED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ktuId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<BorrowTransaction> findAllByStudentKtuId(String ktuId) throws SQLException {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE LOWER(s.ktu_id) = LOWER(?) ORDER BY t.issue_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ktuId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToTransaction(rs));
                }
            }
        }
        return list;
    }

    public List<BorrowTransaction> findAll() throws SQLException {
        List<BorrowTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY t.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
        }
        return list;
    }

    private BorrowTransaction mapRowToTransaction(ResultSet rs) throws SQLException {
        Student student = new Student(
                rs.getInt("s_id"),
                rs.getString("ktu_id"),
                rs.getString("s_name"),
                rs.getString("branch"),
                rs.getInt("semester"),
                rs.getString("batch"),
                rs.getString("s_email"),
                rs.getString("s_phone")
        );

        Category category = new Category(
                rs.getInt("c_id"),
                rs.getString("c_name")
        );

        BookStatus bStatus = BookStatus.AVAILABLE;
        try {
            bStatus = BookStatus.valueOf(rs.getString("b_status").toUpperCase());
        } catch (Exception ignored) {
        }

        Book book = new Book(
                rs.getInt("b_id"),
                rs.getString("accession_id"),
                rs.getString("b_title"),
                rs.getString("b_author"),
                rs.getString("isbn"),
                rs.getString("publisher"),
                null,
                rs.getInt("publication_year"),
                category,
                bStatus
        );

        Librarian librarian = new Librarian(
                rs.getInt("l_id"),
                rs.getString("l_name"),
                rs.getString("l_email"),
                rs.getString("l_phone"),
                rs.getString("username"),
                "" // Password omitted
        );

        Date retDateVal = rs.getDate("return_date");
        LocalDate returnDate = retDateVal != null ? retDateVal.toLocalDate() : null;

        TransactionStatus txStatus = TransactionStatus.ISSUED;
        try {
            txStatus = TransactionStatus.valueOf(rs.getString("status").toUpperCase());
        } catch (Exception ignored) {
        }

        return new BorrowTransaction(
                rs.getInt("id"),
                student,
                book,
                librarian,
                rs.getDate("issue_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate,
                rs.getDouble("fine"),
                txStatus
        );
    }
}
