package com.college.library.gui;

import com.college.library.database.DatabaseInitializer;
import com.college.library.model.Librarian;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Login window for the librarian.
 * Demonstrates:
 * - FR-01: Librarian Login authentication
 * - Clean UI layout adapting to OS light and dark themes
 */
public class LoginFrame extends JFrame {
    private final Library library;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusMessageLabel;

    public LoginFrame(Library library) {
        this.library = library;

        setTitle("Librarian Login - College Library Management System");
        setSize(450, 360);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Librarian Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subLabel = new JLabel("College Library Management System", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(189, 195, 199));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        usernameField = new JTextField("admin");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField("admin123");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(passwordField, gbc);

        // Status message
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        statusMessageLabel = new JLabel("Default login: admin / admin123", SwingConstants.CENTER);
        statusMessageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        formPanel.add(statusMessageLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Login Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JButton loginButton = new JButton("Login to System");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.setPreferredSize(new Dimension(200, 36));
        loginButton.addActionListener(e -> performLogin());

        // Press Enter to login
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());

        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Librarian librarian = library.authenticate(username, password);
            if (librarian != null) {
                // Successful login
                dispose();
                SwingUtilities.invokeLater(() -> new MainFrame(library, librarian).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set System Look and Feel for native OS appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize Database tables and seeds
        DatabaseInitializer.initializeDatabase();

        Library library;
        try {
            library = new Library(true);
        } catch (Exception e) {
            System.err.println("Could not connect to database, falling back to memory: " + e.getMessage());
            library = new Library(false);
        }

        Library finalLibrary = library;
        SwingUtilities.invokeLater(() -> new LoginFrame(finalLibrary).setVisible(true));
    }
}
