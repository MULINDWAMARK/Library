package com.mycompany.focused;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Library implements ActionListener {
    private static JLabel bookidLabel;
    private static JTextField bookidText;
    private static JLabel titleLabel;
    private static JTextField titleText;
    private static JLabel authorLabel;
    private static JTextField authorText;
    private static JLabel yearLabel;
    private static JTextField yearText;
    private static JButton addButton;
    private static JButton delButton;
    private static JButton refreshButton;
    private static JTable bookTable;
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        JPanel panel = new JPanel();
        JFrame frame = new JFrame();
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        bookidLabel = new JLabel("Book ID");
        bookidLabel.setBounds(10, 20, 80, 25);
        panel.add(bookidLabel);

        bookidText = new JTextField(20);
        bookidText.setBounds(100, 20, 165, 25);
        panel.add(bookidText);

        titleLabel = new JLabel("Title");
        titleLabel.setBounds(10, 50, 80, 25);
        panel.add(titleLabel);

        titleText = new JTextField(20);
        titleText.setBounds(100, 50, 165, 25);
        panel.add(titleText);

        authorLabel = new JLabel("Author");
        authorLabel.setBounds(10, 80, 80, 25);
        panel.add(authorLabel);

        authorText = new JTextField(20);
        authorText.setBounds(100, 80, 165, 25);
        panel.add(authorText);

        yearLabel = new JLabel("Year");
        yearLabel.setBounds(10, 110, 80, 25);
        panel.add(yearLabel);

        yearText = new JTextField(20);
        yearText.setBounds(100, 110, 165, 25);
        panel.add(yearText);

        addButton = new JButton("Add Book");
        addButton.setBounds(10, 140, 100, 25);
        addButton.addActionListener(new Focused());
        panel.add(addButton);

        delButton = new JButton("Del Book");
        delButton.setBounds(120, 140, 100, 25);
        delButton.addActionListener(new Focused());
        panel.add(delButton);

        refreshButton = new JButton("Refresh List");
        refreshButton.setBounds(230, 140, 120, 25);
        refreshButton.addActionListener(new Focused());
        panel.add(refreshButton);

        tableModel = new DefaultTableModel(new Object[]{"Book ID", "Title", "Author", "Year"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBounds(10, 180, 560, 150);
        panel.add(scrollPane);

        frame.setVisible(true);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addBook();
        } else if (e.getSource() == delButton) {
            deleteBook();
        } else if (e.getSource() == refreshButton) {
            refreshBookList();
        }
    }

    private void addBook() {
        String bookID = bookidText.getText();
        String title = titleText.getText();
        String author = authorText.getText();
        String year = yearText.getText();

        if (!bookID.isEmpty() && !title.isEmpty() && !author.isEmpty() && !year.isEmpty()) {
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DriverManager.getConnection("jdbc:ucanaccess://E:/DKTP/drivers");
                stmt = conn.prepareStatement("INSERT INTO books (bookID, title, author, year) VALUES (?, ?, ?, ?)");
                stmt.setString(1, bookID);
                stmt.setString(2, title);
                stmt.setString(3, author);
                stmt.setString(4, year);
                stmt.executeUpdate();
                refreshBookList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                // This prints the stack trace of the exception to the standard error stream
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "All fields must be filled out", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            String bookID = (String) tableModel.getValueAt(selectedRow, 0);
            Connection conn = null;
            PreparedStatement stmt = null;
            try {
                conn = DriverManager.getConnection("jdbc:ucanaccess://E:/DKTP/drivers");
                stmt = conn.prepareStatement("DELETE FROM books WHERE bookID = ?");
                stmt.setString(1, bookID);
                stmt.executeUpdate();
                refreshBookList();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a book to delete", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBookList() {
        tableModel.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            conn = DriverManager.getConnection("jdbc:ucanaccess://E:/DKTP/drivers");
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM books");
            while (resultSet.next()) {
                String bookID = resultSet.getString("bookID");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String year = resultSet.getString("year");
                tableModel.addRow(new Object[]{bookID, title, author, year});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }
    }

    static class Book {
        private final String bookID;
        private final String title;
        private final String author;
        private final String year;

        public Book(String bookID, String title, String author, String year) {
            this.bookID = bookID;
            this.title = title;
            this.author = author;
            this.year = year;
        }

        public String getBookID() {
            return bookID;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getYear() {
            return year;
        }
    }
}


