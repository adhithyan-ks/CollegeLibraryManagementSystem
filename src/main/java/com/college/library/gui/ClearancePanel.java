package com.college.library.gui;

import com.college.library.model.BorrowTransaction;
import com.college.library.model.Student;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel to check student library clearance status (FR-10).
 * Compatible with both Light and Dark OS themes.
 */
public class ClearancePanel extends JPanel {
    private final Library library;

    private JTextField ktuIdField;
    private JLabel statusBannerLabel;
    private JLabel studentDetailsLabel;
    private JTable activeBorrowsTable;
    private DefaultTableModel tableModel;

    public ClearancePanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // Top Search Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JLabel label = new JLabel("Enter Student KTU ID:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));

        ktuIdField = new JTextField(15);
        JButton checkBtn = new JButton("Check Clearance Status");
        checkBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        checkBtn.addActionListener(e -> checkClearance());

        searchBar.add(label);
        searchBar.add(ktuIdField);
        searchBar.add(checkBtn);

        topPanel.add(searchBar, BorderLayout.NORTH);

        // Status Card
        JPanel statusCard = new JPanel(new GridLayout(2, 1, 5, 5));
        statusCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(10, 15, 10, 15)
        ));

        statusBannerLabel = new JLabel("Enter a student KTU ID to check clearance eligibility", SwingConstants.CENTER);
        statusBannerLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        studentDetailsLabel = new JLabel("", SwingConstants.CENTER);
        studentDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        statusCard.add(statusBannerLabel);
        statusCard.add(studentDetailsLabel);
        topPanel.add(statusCard, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Center: Active Borrowed Books Table
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));

        JLabel tableTitle = new JLabel("Active Unreturned Books For Student");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"Transaction ID", "Accession ID", "Book Title", "Issue Date", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        activeBorrowsTable = new JTable(tableModel);
        activeBorrowsTable.setRowHeight(24);
        activeBorrowsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activeBorrowsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(activeBorrowsTable);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    private void checkClearance() {
        String ktuId = ktuIdField.getText().trim();
        if (ktuId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a KTU ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Student student = library.findStudentByKtuId(ktuId);
            if (student == null) {
                statusBannerLabel.setText("Student not found!");
                statusBannerLabel.setForeground(new Color(231, 76, 60));
                studentDetailsLabel.setText("No student registered with KTU ID '" + ktuId + "'.");
                tableModel.setRowCount(0);
                return;
            }

            studentDetailsLabel.setText("Student: " + student.getName() + " | Branch: " + student.getBranch() +
                    " | Semester: " + student.getSemester());

            boolean cleared = library.hasClearance(ktuId);
            List<BorrowTransaction> activeBorrows = library.getActiveBorrowsByStudent(ktuId);

            tableModel.setRowCount(0);
            for (BorrowTransaction tx : activeBorrows) {
                boolean overdue = tx.isOverdue(LocalDate.now());
                tableModel.addRow(new Object[]{
                        tx.getId(),
                        tx.getBook() != null ? tx.getBook().getAccessionId() : "-",
                        tx.getBook() != null ? tx.getBook().getTitle() : "-",
                        tx.getIssueDate(),
                        tx.getDueDate(),
                        overdue ? "OVERDUE (Pending fine)" : "Active (Due: " + tx.getDueDate() + ")"
                });
            }

            if (cleared) {
                statusBannerLabel.setText("✓ CLEARED: Student has no pending book returns or dues.");
                statusBannerLabel.setForeground(new Color(39, 174, 96));
            } else {
                statusBannerLabel.setText("✗ PENDING: Clearance denied. Student has " + activeBorrows.size() + " unreturned book(s).");
                statusBannerLabel.setForeground(new Color(231, 76, 60));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking clearance: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
