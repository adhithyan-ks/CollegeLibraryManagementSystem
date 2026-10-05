package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Student;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing college student records.
 */
public class StudentPanel extends JPanel {
    private final Library library;

    private JTable studentTable;
    private DefaultTableModel tableModel;

    private JTextField ktuIdField;
    private JTextField nameField;
    private JTextField branchField;
    private JComboBox<Integer> semesterComboBox;
    private JTextField batchField;
    private JTextField emailField;
    private JTextField phoneField;

    private JTextField searchField;

    public StudentPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        initComponents();
        loadStudents(null);
    }

    private void initComponents() {
        // Top Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Students:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search by Name");
        JButton showAllBtn = new JButton("Show All");

        searchBtn.addActionListener(e -> searchStudents());
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            loadStudents(null);
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "KTU ID", "Name", "Branch", "Semester", "Batch", "Email", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(24);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Add Student Form & Actions
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setOpaque(false);

        JPanel inputForm = new JPanel(new GridLayout(4, 4, 10, 10));
        inputForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                "Register New Student",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));
        inputForm.setOpaque(false);

        ktuIdField = new JTextField();
        nameField = new JTextField();
        branchField = new JTextField();
        semesterComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        batchField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        inputForm.add(new JLabel("KTU ID (e.g. TVE23CS001):"));
        inputForm.add(ktuIdField);
        inputForm.add(new JLabel("Full Name:"));
        inputForm.add(nameField);

        inputForm.add(new JLabel("Branch (e.g. Computer Science):"));
        inputForm.add(branchField);
        inputForm.add(new JLabel("Semester:"));
        inputForm.add(semesterComboBox);

        inputForm.add(new JLabel("Batch (Optional, e.g. A):"));
        inputForm.add(batchField);
        inputForm.add(new JLabel("Email Address:"));
        inputForm.add(emailField);

        inputForm.add(new JLabel("Phone Number:"));
        inputForm.add(phoneField);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setOpaque(false);

        JButton addBtn = new JButton("Register Student");
        addBtn.setBackground(new Color(39, 174, 96));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> addStudent());

        JButton deleteBtn = new JButton("Remove Selected Student");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> removeSelectedStudent());

        actionPanel.add(addBtn);
        actionPanel.add(deleteBtn);

        formContainer.add(inputForm, BorderLayout.CENTER);
        formContainer.add(actionPanel, BorderLayout.SOUTH);

        add(formContainer, BorderLayout.SOUTH);
    }

    public void loadStudents(List<Student> studentList) {
        try {
            tableModel.setRowCount(0);
            List<Student> students = (studentList != null) ? studentList : library.getAllStudents();
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getKtuId(),
                        s.getName(),
                        s.getBranch(),
                        s.getSemester(),
                        s.getBatch() != null ? s.getBatch() : "-",
                        s.getEmail(),
                        s.getPhone()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadStudents(null);
            return;
        }
        try {
            List<Student> results = library.searchStudentsByName(query);
            loadStudents(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudent() {
        String ktuId = ktuIdField.getText().trim();
        String name = nameField.getText().trim();
        String branch = branchField.getText().trim();
        int semester = (Integer) semesterComboBox.getSelectedItem();
        String batch = batchField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (ktuId.isEmpty() || name.isEmpty() || branch.isEmpty()) {
            JOptionPane.showMessageDialog(this, "KTU ID, Name, and Branch are required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student student = new Student(ktuId, name, branch, semester, batch.isEmpty() ? null : batch, email, phone);

        try {
            library.addStudent(student);
            JOptionPane.showMessageDialog(this, "Student '" + name + "' registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadStudents(null);
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ktuId = (String) tableModel.getValueAt(selectedRow, 1);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove student [" + ktuId + "] " + name + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                library.removeStudent(ktuId);
                JOptionPane.showMessageDialog(this, "Student removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents(null);
            } catch (LibraryException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        ktuIdField.setText("");
        nameField.setText("");
        branchField.setText("");
        batchField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}
