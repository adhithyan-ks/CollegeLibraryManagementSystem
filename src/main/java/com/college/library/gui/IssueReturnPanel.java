package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Librarian;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel handling book issue and return operations, including overdue fine calculations.
 * Compatible with both Light and Dark desktop themes.
 */
public class IssueReturnPanel extends JPanel {
    private final Library library;
    private final Librarian loggedInLibrarian;

    private JTextField issueAccessionField;
    private JTextField issueKtuField;

    private JTextField returnAccessionField;

    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JCheckBox activeOnlyCheckBox;

    public IssueReturnPanel(Library library, Librarian librarian) {
        this.library = library;
        this.loggedInLibrarian = librarian;

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initComponents();
        loadTransactions();
    }

    private void initComponents() {
        // Top Section: Issue & Return Forms side-by-side
        JPanel formsGrid = new JPanel(new GridLayout(1, 2, 15, 15));

        // --- Issue Book Form ---
        JPanel issueForm = new JPanel(new GridBagLayout());
        issueForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Issue Book",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        issueForm.add(new JLabel("Book Accession ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        issueAccessionField = new JTextField();
        issueForm.add(issueAccessionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        issueForm.add(new JLabel("Student KTU ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        issueKtuField = new JTextField();
        issueForm.add(issueKtuField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton issueBtn = new JButton("Confirm Book Issue");
        issueBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        issueBtn.addActionListener(e -> processIssue());
        issueForm.add(issueBtn, gbc);

        // --- Return Book Form ---
        JPanel returnForm = new JPanel(new GridBagLayout());
        returnForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Return Book",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        returnForm.add(new JLabel("Book Accession ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        returnAccessionField = new JTextField();
        returnForm.add(returnAccessionField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JLabel ruleNote = new JLabel("<html><small>Standard Loan: 14 days | Overdue Fine: Rs. 2.00/day</small></html>");
        returnForm.add(ruleNote, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton returnBtn = new JButton("Process Book Return");
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        returnBtn.addActionListener(e -> processReturn());
        returnForm.add(returnBtn, gbc);

        formsGrid.add(issueForm);
        formsGrid.add(returnForm);
        add(formsGrid, BorderLayout.NORTH);

        // Center: Transaction History Table
        JPanel tableContainer = new JPanel(new BorderLayout(5, 5));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());

        JLabel tableTitle = new JLabel("Borrowing Transactions & History");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel tableControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        activeOnlyCheckBox = new JCheckBox("Show Active (Unreturned) Only");
        activeOnlyCheckBox.addActionListener(e -> loadTransactions());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadTransactions());

        tableControls.add(activeOnlyCheckBox);
        tableControls.add(refreshBtn);

        tableHeaderPanel.add(tableTitle, BorderLayout.WEST);
        tableHeaderPanel.add(tableControls, BorderLayout.EAST);
        tableContainer.add(tableHeaderPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Accession ID", "Book Title", "KTU ID", "Student Name", "Issue Date", "Due Date", "Return Date", "Fine (Rs.)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(24);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
    }

    private void processIssue() {
        String accessionId = issueAccessionField.getText().trim();
        String ktuId = issueKtuField.getText().trim();

        if (accessionId.isEmpty() || ktuId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both Book Accession ID and Student KTU ID are required.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BorrowTransaction tx = library.issueBook(accessionId, ktuId, loggedInLibrarian, LocalDate.now());
            JOptionPane.showMessageDialog(this,
                    "Book successfully issued!\n" +
                    "Title: " + tx.getBook().getTitle() + "\n" +
                    "Student: " + tx.getStudent().getName() + " (" + tx.getStudent().getKtuId() + ")\n" +
                    "Due Date: " + tx.getDueDate(),
                    "Issue Successful", JOptionPane.INFORMATION_MESSAGE);

            issueAccessionField.setText("");
            issueKtuField.setText("");
            loadTransactions();
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Issue Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processReturn() {
        String accessionId = returnAccessionField.getText().trim();

        if (accessionId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Book Accession ID is required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            BorrowTransaction tx = library.returnBook(accessionId, LocalDate.now());
            String message = "Book returned successfully!\n" +
                    "Title: " + tx.getBook().getTitle() + "\n" +
                    "Return Date: " + tx.getReturnDate() + "\n" +
                    "Fine: Rs. " + String.format("%.2f", tx.getFine());

            if (tx.getFine() > 0) {
                JOptionPane.showMessageDialog(this, message, "Overdue Fine Applicable", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, message, "Return Completed", JOptionPane.INFORMATION_MESSAGE);
            }

            returnAccessionField.setText("");
            loadTransactions();
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Return Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadTransactions() {
        try {
            tableModel.setRowCount(0);
            List<BorrowTransaction> transactions = library.getAllTransactions();
            boolean activeOnly = activeOnlyCheckBox.isSelected();

            for (BorrowTransaction tx : transactions) {
                if (activeOnly && tx.isReturned()) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        tx.getId(),
                        tx.getBook() != null ? tx.getBook().getAccessionId() : "-",
                        tx.getBook() != null ? tx.getBook().getTitle() : "-",
                        tx.getStudent() != null ? tx.getStudent().getKtuId() : "-",
                        tx.getStudent() != null ? tx.getStudent().getName() : "-",
                        tx.getIssueDate(),
                        tx.getDueDate(),
                        tx.getReturnDate() != null ? tx.getReturnDate() : "Not Returned",
                        String.format("%.2f", tx.getFine()),
                        tx.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load transactions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
