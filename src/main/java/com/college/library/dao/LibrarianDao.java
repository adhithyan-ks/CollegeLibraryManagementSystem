package com.college.library.dao;

import com.college.library.database.DatabaseConnection;
import com.college.library.model.Librarian;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'librarians' table.
 */
public class LibrarianDao {

    public void insert(Librarian librarian) throws SQLException {
        String sql = "INSERT INTO librarians (name, email, phone, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, librarian.getName());
            stmt.setString(2, librarian.getEmail());
            stmt.setString(3, librarian.getPhone());
            stmt.setString(4, librarian.getUsername());
            stmt.setString(5, librarian.getPassword());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    librarian.setId(rs.getInt(1));
                }
            }
        }
    }

    public Librarian findById(int id) throws SQLException {
        String sql = "SELECT id, name, email, phone, username, password FROM librarians WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLibrarian(rs);
                }
            }
        }
        return null;
    }

    public Librarian findByUsername(String username) throws SQLException {
        String sql = "SELECT id, name, email, phone, username, password FROM librarians WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLibrarian(rs);
                }
            }
        }
        return null;
    }

    public Librarian authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, name, email, phone, username, password FROM librarians WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLibrarian(rs);
                }
            }
        }
        return null;
    }

    public List<Librarian> findAll() throws SQLException {
        List<Librarian> list = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, username, password FROM librarians ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToLibrarian(rs));
            }
        }
        return list;
    }

    private Librarian mapRowToLibrarian(ResultSet rs) throws SQLException {
        return new Librarian(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("username"),
                rs.getString("password")
        );
    }
}
