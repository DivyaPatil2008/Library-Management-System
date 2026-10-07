package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LibraryDashboard extends JFrame {

    // Colors
    private final Color NAVY = new Color(25, 55, 90);
    private final Color BLUE = new Color(45, 120, 220);
    private final Color LIGHT_BLUE = new Color(235, 245, 255);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(35, 45, 60);
    private final Color GREEN = new Color(40, 170, 110);
    private final Color RED = new Color(220, 70, 70);
    //private final Color ORANGE = new Color(240, 150, 45);

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private JLabel totalBooksLabel;
    private JLabel availableBooksLabel;
    private JLabel issuedBooksLabel;

    public LibraryDashboard() {

        setTitle("Library Management System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUI();

        loadBooks();
        updateStatistics();
    }

    // =========================
    // MAIN UI
    // =========================

    private void createUI() {

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 248, 252));

        // =========================
        // SIDEBAR
        // =========================

        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(210, 700));
        sidebar.setBackground(NAVY);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("📚");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel libraryName = new JLabel("LIBRARY");
        libraryName.setForeground(Color.WHITE);
        libraryName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        libraryName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel systemName = new JLabel("MANAGEMENT SYSTEM");
        systemName.setForeground(new Color(190, 210, 230));
        systemName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(libraryName);
        sidebar.add(systemName);
        sidebar.add(Box.createVerticalStrut(35));

        JButton dashboardBtn = createSidebarButton("🏠  Dashboard");
        JButton booksBtn = createSidebarButton("📖  View Books");
        JButton addBtn = createSidebarButton("➕  Add Book");
        JButton searchBtn = createSidebarButton("🔍  Search Book");
        JButton issueBtn = createSidebarButton("📤  Issue Book");
        JButton returnBtn = createSidebarButton("↩  Return Book");
        JButton updateBtn = createSidebarButton("✏  Update Book");
        JButton deleteBtn = createSidebarButton("🗑  Delete Book");

        sidebar.add(dashboardBtn);
        sidebar.add(booksBtn);
        sidebar.add(addBtn);
        sidebar.add(searchBtn);
        sidebar.add(issueBtn);
        sidebar.add(returnBtn);
        sidebar.add(updateBtn);
        sidebar.add(deleteBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton exitBtn = createSidebarButton("🚪  Exit");
        sidebar.add(exitBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // =========================
        // RIGHT SIDE
        // =========================

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(245, 248, 252));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("Library Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(NAVY);

        JLabel admin = new JLabel("Welcome, Admin  👤");
        admin.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        admin.setForeground(TEXT);

        header.add(title, BorderLayout.WEST);
        header.add(admin, BorderLayout.EAST);

        rightPanel.add(header, BorderLayout.NORTH);

        // =========================
        // CONTENT
        // =========================

        JPanel content = new JPanel();
        content.setBackground(new Color(245, 248, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel welcome = new JLabel("Welcome to your Library Dashboard");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(NAVY);

        JLabel subtitle = new JLabel(
                "Manage books, issue books and keep your library organized."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        content.add(welcome);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // =========================
        // STATISTICS CARDS
        // =========================

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setBackground(new Color(245, 248, 252));
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        JPanel totalCard = createCard(
                "TOTAL BOOKS",
                "0",
                BLUE
        );

        JPanel availableCard = createCard(
                "AVAILABLE BOOKS",
                "0",
                GREEN
        );

        JPanel issuedCard = createCard(
                "ISSUED BOOKS",
                "0",
                RED
        );

        totalBooksLabel = (JLabel) totalCard.getClientProperty("valueLabel");
        availableBooksLabel = (JLabel) availableCard.getClientProperty("valueLabel");
        issuedBooksLabel = (JLabel) issuedCard.getClientProperty("valueLabel");

        cardsPanel.add(totalCard);
        cardsPanel.add(availableCard);
        cardsPanel.add(issuedCard);

        content.add(cardsPanel);
        content.add(Box.createVerticalStrut(20));

        // =========================
        // SEARCH BAR
        // =========================

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 235)),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));

        JButton searchButton = new JButton("🔍 Search");
        styleButton(searchButton, BLUE);

        JButton resetButton = new JButton("Reset");
        styleButton(resetButton, new Color(120, 130, 145));

        JPanel searchButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchButtons.setBackground(WHITE);
        searchButtons.add(searchButton);
        searchButtons.add(resetButton);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButtons, BorderLayout.EAST);

        content.add(searchPanel);
        content.add(Box.createVerticalStrut(15));

        // Search action
        searchButton.addActionListener(e ->
                searchBooks(searchField.getText().trim())
        );

        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadBooks();
        });

        // =========================
        // BOOK TABLE
        // =========================

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 225, 235)),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );

        JLabel tableTitle = new JLabel("📚  Books List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(NAVY);

        tablePanel.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                        "Book ID",
                        "Title",
                        "Author",
                        "Status"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookTable = new JTable(tableModel);

        bookTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookTable.setRowHeight(35);
        bookTable.setSelectionBackground(new Color(220, 235, 255));
        bookTable.setSelectionForeground(TEXT);
        bookTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );
        bookTable.getTableHeader().setBackground(LIGHT_BLUE);
        bookTable.getTableHeader().setForeground(NAVY);

        // Center ID and Status
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        bookTable.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        bookTable.getColumnModel()
                .getColumn(3)
                .setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(800, 300));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        content.add(tablePanel);

        rightPanel.add(
                new JScrollPane(content),
                BorderLayout.CENTER
        );

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);

        // =========================
        // BUTTON ACTIONS
        // =========================

        dashboardBtn.addActionListener(e -> {
            loadBooks();
            updateStatistics();
        });

        booksBtn.addActionListener(e -> loadBooks());

        addBtn.addActionListener(e -> addBook());

        searchBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this,
                    "Enter Book ID:"
            );

            if (id != null && !id.trim().isEmpty()) {
                searchBooks(id.trim());
            }
        });

        issueBtn.addActionListener(e -> issueBook());

        returnBtn.addActionListener(e -> returnBook());

        updateBtn.addActionListener(e -> updateBook());

        deleteBtn.addActionListener(e -> deleteBook());

        exitBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to exit?",
                    "Exit",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    // =========================
    // SIDEBAR BUTTON
    // =========================

    private JButton createSidebarButton(String text) {

        JButton button = new JButton(text);

        button.setMaximumSize(new Dimension(190, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(NAVY);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(
                new EmptyBorder(10, 20, 10, 10)
        );

        button.setFocusPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(NAVY);
            }
        });

        return button;
    }

    // =========================
    // STATISTICS CARD
    // =========================

    private JPanel createCard(
            String title,
            String value,
            Color color
    ) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 235)
                        ),
                        new EmptyBorder(15, 20, 15, 20)
                )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 28)
        );
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        card.putClientProperty("valueLabel", valueLabel);

        return card;
    }

    // =========================
    // BUTTON STYLE
    // =========================

    private void styleButton(
            JButton button,
            Color color
    ) {

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        button.setFocusPainted(false);
        button.setBorder(
                new EmptyBorder(10, 18, 10, 18)
        );
    }

    // =========================
    // LOAD BOOKS
    // =========================

    private void loadBooks() {

        tableModel.setRowCount(0);

        String query = "SELECT * FROM books";

        try (
                Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {

            while (rs.next()) {

                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int available = rs.getInt("available");

                String status =
                        available == 1
                                ? "Available"
                                : "Issued";

                tableModel.addRow(
                        new Object[]{
                                id,
                                title,
                                author,
                                status
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load books.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // STATISTICS
    // =========================

    private void updateStatistics() {

        String query =
                "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN available = 1 THEN 1 ELSE 0 END) AS available, " +
                "SUM(CASE WHEN available = 0 THEN 1 ELSE 0 END) AS issued " +
                "FROM books";

        try (
                Connection con = DBConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {

            if (rs.next()) {

                totalBooksLabel.setText(
                        String.valueOf(rs.getInt("total"))
                );

                availableBooksLabel.setText(
                        String.valueOf(rs.getInt("available"))
                );

                issuedBooksLabel.setText(
                        String.valueOf(rs.getInt("issued"))
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Statistics error: " + e.getMessage()
            );
        }
    }

    // =========================
    // ADD BOOK
    // =========================

    private void addBook() {

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();

        JPanel panel = new JPanel(
                new GridLayout(0, 1, 5, 5)
        );

        panel.add(new JLabel("Book ID:"));
        panel.add(idField);

        panel.add(new JLabel("Book Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Book",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            int id = Integer.parseInt(
                    idField.getText().trim()
            );

            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields."
                );
                return;
            }

            // Check duplicate
            String checkQuery =
                    "SELECT * FROM books WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement check =
                            con.prepareStatement(checkQuery)
            ) {

                check.setInt(1, id);

                ResultSet rs = check.executeQuery();

                if (rs.next()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book ID already exists!",
                            "Duplicate ID",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }
            }

            String query =
                    "INSERT INTO books " +
                    "(book_id, title, author, available) " +
                    "VALUES (?, ?, ?, 1)";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(query)
            ) {

                ps.setInt(1, id);
                ps.setString(2, title);
                ps.setString(3, author);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(
                        this,
                        "Book added successfully!"
                );
            }

            loadBooks();
            updateStatistics();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =========================
    // SEARCH BOOK
    // =========================

    private void searchBooks(String text) {

        if (text.isEmpty()) {
            loadBooks();
            return;
        }

        tableModel.setRowCount(0);

        String query =
                "SELECT * FROM books " +
                "WHERE CAST(book_id AS CHAR) LIKE ? " +
                "OR title LIKE ? " +
                "OR author LIKE ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps =
                        con.prepareStatement(query)
        ) {

            String search = "%" + text + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {

                found = true;

                int available =
                        rs.getInt("available");

                tableModel.addRow(
                        new Object[]{
                                rs.getInt("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                available == 1
                                        ? "Available"
                                        : "Issued"
                        }
                );
            }

            if (!found) {

                JOptionPane.showMessageDialog(
                        this,
                        "No book found."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search error: " + e.getMessage()
            );
        }
    }

    // =========================
    // ISSUE BOOK
    // =========================

    private void issueBook() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter Book ID to issue:"
        );

        if (input == null) {
            return;
        }

        try {

            int id = Integer.parseInt(input.trim());

            String checkQuery =
                    "SELECT available FROM books WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(checkQuery)
            ) {

                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book not found!"
                    );

                    return;
                }

                if (rs.getInt("available") == 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "This book is already issued!"
                    );

                    return;
                }
            }

            String query =
                    "UPDATE books SET available=0 " +
                    "WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(query)
            ) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Book issued successfully!"
            );

            loadBooks();
            updateStatistics();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =========================
    // RETURN BOOK
    // =========================

    private void returnBook() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter Book ID to return:"
        );

        if (input == null) {
            return;
        }

        try {

            int id = Integer.parseInt(input.trim());

            String checkQuery =
                    "SELECT available FROM books WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(checkQuery)
            ) {

                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book not found!"
                    );

                    return;
                }

                if (rs.getInt("available") == 1) {

                    JOptionPane.showMessageDialog(
                            this,
                            "This book is already available!"
                    );

                    return;
                }
            }

            String query =
                    "UPDATE books SET available=1 " +
                    "WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(query)
            ) {

                ps.setInt(1, id);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Book returned successfully!"
            );

            loadBooks();
            updateStatistics();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =========================
    // UPDATE BOOK
    // =========================

    private void updateBook() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter Book ID to update:"
        );

        if (input == null) {
            return;
        }

        try {

            int id = Integer.parseInt(input.trim());

            String checkQuery =
                    "SELECT * FROM books WHERE book_id=?";

            String oldTitle = "";
            String oldAuthor = "";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(checkQuery)
            ) {

                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book not found!"
                    );

                    return;
                }

                oldTitle = rs.getString("title");
                oldAuthor = rs.getString("author");
            }

            JTextField titleField =
                    new JTextField(oldTitle);

            JTextField authorField =
                    new JTextField(oldAuthor);

            JPanel panel = new JPanel(
                    new GridLayout(0, 1, 5, 5)
            );

            panel.add(new JLabel("Book Title:"));
            panel.add(titleField);

            panel.add(new JLabel("Author:"));
            panel.add(authorField);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Update Book",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String query =
                    "UPDATE books SET title=?, author=? " +
                    "WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(query)
            ) {

                ps.setString(
                        1,
                        titleField.getText().trim()
                );

                ps.setString(
                        2,
                        authorField.getText().trim()
                );

                ps.setInt(3, id);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Book updated successfully!"
            );

            loadBooks();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =========================
    // DELETE BOOK
    // =========================

    private void deleteBook() {

        String input = JOptionPane.showInputDialog(
                this,
                "Enter Book ID to delete:"
        );

        if (input == null) {
            return;
        }

        try {

            int id = Integer.parseInt(input.trim());

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete Book ID "
                            + id + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String query =
                    "DELETE FROM books WHERE book_id=?";

            try (
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps =
                            con.prepareStatement(query)
            ) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows == 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book not found!"
                    );

                    return;
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Book deleted successfully!"
            );

            loadBooks();
            updateStatistics();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid Book ID."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + e.getMessage()
            );
        }
    }

    // =========================
    // MAIN
    // =========================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            new LibraryDashboard().setVisible(true);
        });
    }
}