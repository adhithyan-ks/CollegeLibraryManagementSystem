package com.college.library.gui;

import com.college.library.service.Library;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dashboard Panel displaying key library metrics and real-time statistics.
 */
public class DashboardPanel extends JPanel {
    private final Library library;

    private JLabel totalBooksLabel;
    private JLabel availableBooksLabel;
    private JLabel issuedBooksLabel;
    private JLabel overdueBooksLabel;
    private JLabel totalStudentsLabel;

    public DashboardPanel(Library library) {
        this.library = library;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        initComponents();
        refreshStatistics();
    }

    private void initComponents() {
        // Title header
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Library Overview Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));

        JButton refreshButton = new JButton("Refresh Stats");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshStatistics());

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(refreshButton, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        // Stats grid
        JPanel cardsGrid = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsGrid.setOpaque(false);

        totalBooksLabel = new JLabel("0", SwingConstants.CENTER);
        availableBooksLabel = new JLabel("0", SwingConstants.CENTER);
        issuedBooksLabel = new JLabel("0", SwingConstants.CENTER);
        overdueBooksLabel = new JLabel("0", SwingConstants.CENTER);
        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createCard("Total Physical Books", totalBooksLabel, new Color(41, 128, 185)));
        cardsGrid.add(createCard("Available for Issue", availableBooksLabel, new Color(39, 174, 96)));
        cardsGrid.add(createCard("Currently Issued", issuedBooksLabel, new Color(243, 156, 18)));
        cardsGrid.add(createCard("Overdue Books", overdueBooksLabel, new Color(231, 76, 60)));
        cardsGrid.add(createCard("Registered Students", totalStudentsLabel, new Color(142, 68, 173)));

        // Instructions / quick info card
        JPanel infoCard = new JPanel(new BorderLayout(5, 5));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel infoTitle = new JLabel("Quick Guidelines", SwingConstants.CENTER);
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(new Color(52, 73, 94));

        JLabel infoText = new JLabel("<html><center>Issue duration: 14 days<br>Overdue fine: Rs. 2/day<br>Use tabs above to manage records</center></html>", SwingConstants.CENTER);
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(new Color(108, 117, 125));

        infoCard.add(infoTitle, BorderLayout.NORTH);
        infoCard.add(infoText, BorderLayout.CENTER);
        cardsGrid.add(infoCard);

        add(cardsGrid, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                        new EmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(108, 117, 125));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshStatistics() {
        try {
            totalBooksLabel.setText(String.valueOf(library.getTotalBooksCount()));
            availableBooksLabel.setText(String.valueOf(library.getAvailableBooksCount()));
            issuedBooksLabel.setText(String.valueOf(library.getIssuedBooksCount()));
            overdueBooksLabel.setText(String.valueOf(library.getOverdueBooksCount(LocalDate.now())));
            totalStudentsLabel.setText(String.valueOf(library.getTotalStudentsCount()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load dashboard statistics: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
