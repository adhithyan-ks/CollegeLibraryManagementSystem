package com.college.library.dao;

import com.college.library.database.DatabaseConnection;
import com.college.library.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for the 'students' table.
 */
public class StudentDao {

    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (ktu_id, name, branch, semester, batch, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getKtuId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getBranch());
            stmt.setInt(4, student.getSemester());
            stmt.setString(5, student.getBatch());
            stmt.setString(6, student.getEmail());
            stmt.setString(7, student.getPhone());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    student.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, branch = ?, semester = ?, batch = ?, email = ?, phone = ? WHERE ktu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getName());
            stmt.setString(2, student.getBranch());
            stmt.setInt(3, student.getSemester());
            stmt.setString(4, student.getBatch());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getKtuId());
            stmt.executeUpdate();
        }
    }

    public boolean deleteByKtuId(String ktuId) throws SQLException {
        String sql = "DELETE FROM students WHERE ktu_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ktuId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Student findById(int id) throws SQLException {
        String sql = "SELECT id, ktu_id, name, branch, semester, batch, email, phone FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        }
        return null;
    }

    public Student findByKtuId(String ktuId) throws SQLException {
        String sql = "SELECT id, ktu_id, name, branch, semester, batch, email, phone FROM students WHERE LOWER(ktu_id) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ktuId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }
        }
        return null;
    }

    public List<Student> searchByName(String nameQuery) throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, ktu_id, name, branch, semester, batch, email, phone FROM students WHERE LOWER(name) LIKE ? ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nameQuery.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToStudent(rs));
                }
            }
        }
        return list;
    }

    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, ktu_id, name, branch, semester, batch, email, phone FROM students ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToStudent(rs));
            }
        }
        return list;
    }

    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("id"),
                rs.getString("ktu_id"),
                rs.getString("name"),
                rs.getString("branch"),
                rs.getInt("semester"),
                rs.getString("batch"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}
