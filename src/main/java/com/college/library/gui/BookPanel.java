package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Book;
import com.college.library.model.Category;
import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing individual physical books in the library.
 */
public class BookPanel extends JPanel {
    private final Library library;

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private JTextField accessionField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField isbnField;
    private JTextField publisherField;
    private JTextField yearField;
    private JComboBox<Category> categoryComboBox;

    private JTextField searchField;

    public BookPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 247, 250));

        initComponents();
        loadCategories();
        loadBooks(null);
    }

    private void initComponents() {
        // Top Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search Books:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search by Title");
        JButton showAllBtn = new JButton("Show All");

        searchBtn.addActionListener(e -> searchBooks());
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            loadBooks(null);
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Center Table
        String[] columnNames = {"ID", "Accession ID", "Title", "Author", "ISBN", "Publisher", "Year", "Category", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(24);
        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bookTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Add Book Form & Actions
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setOpaque(false);

        JPanel inputForm = new JPanel(new GridLayout(4, 4, 10, 10));
        inputForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210)),
                "Add New Physical Book Record",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)
        ));
        inputForm.setOpaque(false);

        accessionField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        isbnField = new JTextField();
        publisherField = new JTextField();
        yearField = new JTextField();
        categoryComboBox = new JComboBox<>();

        inputForm.add(new JLabel("Accession ID (e.g. LIB-001):"));
        inputForm.add(accessionField);
        inputForm.add(new JLabel("Title:"));
        inputForm.add(titleField);

        inputForm.add(new JLabel("Author:"));
        inputForm.add(authorField);
        inputForm.add(new JLabel("ISBN:"));
        inputForm.add(isbnField);

        inputForm.add(new JLabel("Publisher:"));
        inputForm.add(publisherField);
        inputForm.add(new JLabel("Publication Year:"));
        inputForm.add(yearField);

        inputForm.add(new JLabel("Category:"));
        inputForm.add(categoryComboBox);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setOpaque(false);

        JButton addBtn = new JButton("Add Book");
        addBtn.setBackground(new Color(39, 174, 96));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addBtn.addActionListener(e -> addBook());

        JButton deleteBtn = new JButton("Remove Selected Book");
        deleteBtn.setBackground(new Color(231, 76, 60));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteBtn.addActionListener(e -> removeSelectedBook());

        actionPanel.add(addBtn);
        actionPanel.add(deleteBtn);

        formContainer.add(inputForm, BorderLayout.CENTER);
        formContainer.add(actionPanel, BorderLayout.SOUTH);

        add(formContainer, BorderLayout.SOUTH);
    }

    private void loadCategories() {
        try {
            categoryComboBox.removeAllItems();
            List<Category> categories = library.getAllCategories();
            for (Category c : categories) {
                categoryComboBox.addItem(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadBooks(List<Book> bookList) {
        try {
            tableModel.setRowCount(0);
            List<Book> books = (bookList != null) ? bookList : library.getAllBooks();
            for (Book b : books) {
                tableModel.addRow(new Object[]{
                        b.getId(),
                        b.getAccessionId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getIsbn(),
                        b.getPublisher(),
                        b.getPublicationYear(),
                        b.getCategory() != null ? b.getCategory().getName() : "-",
                        b.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load books: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadBooks(null);
            return;
        }
        try {
            List<Book> results = library.searchBooksByTitle(query);
            loadBooks(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBook() {
        String accessionId = accessionField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String isbn = isbnField.getText().trim();
        String publisher = publisherField.getText().trim();
        String yearStr = yearField.getText().trim();
        Category category = (Category) categoryComboBox.getSelectedItem();

        if (accessionId.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Accession ID, Title, and Author are required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int year = 0;
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Publication year must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Book newBook = new Book(accessionId, title, author, isbn, publisher, null, year, category);

        try {
            library.addBook(newBook);
            JOptionPane.showMessageDialog(this, "Book '" + title + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadBooks(null);
        } catch (LibraryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String accessionId = (String) tableModel.getValueAt(selectedRow, 1);
        String title = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove book [" + accessionId + "] " + title + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                library.removeBook(accessionId);
                JOptionPane.showMessageDialog(this, "Book removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadBooks(null);
            } catch (LibraryException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        accessionField.setText("");
        titleField.setText("");
        authorField.setText("");
        isbnField.setText("");
        publisherField.setText("");
        yearField.setText("");
    }
}
