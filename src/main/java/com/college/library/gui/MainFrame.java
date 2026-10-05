package com.college.library.gui;

import com.college.library.model.Librarian;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application window for the librarian dashboard.
 * Houses the tabbed panels for Dashboard, Books, Students, Transactions, and Clearance.
 */
public class MainFrame extends JFrame {
    private final Library library;
    private final Librarian librarian;

    private DashboardPanel dashboardPanel;
    private BookPanel bookPanel;
    private StudentPanel studentPanel;
    private IssueReturnPanel issueReturnPanel;
    private ClearancePanel clearancePanel;

    public MainFrame(Library library, Librarian librarian) {
        this.library = library;
        this.librarian = librarian;

        setTitle("College Library Management System - [Librarian: " +
                (librarian != null ? librarian.getName() : "Administrator") + "]");
        setSize(1050, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainContainer = new JPanel(new BorderLayout());

        // Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("College Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Logged in as: " + (librarian != null ? librarian.getName() : "Admin"));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(new Color(236, 240, 241));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> handleLogout());

        userPanel.add(userLabel);
        userPanel.add(logoutBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        dashboardPanel = new DashboardPanel(library);
        bookPanel = new BookPanel(library);
        studentPanel = new StudentPanel(library);
        issueReturnPanel = new IssueReturnPanel(library, librarian);
        clearancePanel = new ClearancePanel(library);

        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("Books", bookPanel);
        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Issue & Return", issueReturnPanel);
        tabbedPane.addTab("Student Clearance", clearancePanel);

        // Auto refresh when tab is selected
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                dashboardPanel.refreshStatistics();
            } else if (selected == 1) {
                bookPanel.loadBooks(null);
            } else if (selected == 2) {
                studentPanel.loadStudents(null);
            } else if (selected == 3) {
                issueReturnPanel.loadTransactions();
            }
        });

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainContainer);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(library).setVisible(true));
        }
    }
}
