package src;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class LibraryManagementSystem {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== LIBRARY MANAGEMENT SYSTEM =====");

        while (true) {

            System.out.println("\n1. View Books");
            System.out.println("2. Add Book");
            System.out.println("3. Search Book");
            System.out.println("4. Issue Book");
            System.out.println("5. Return Book");
            System.out.println("6. delete Book");
            System.out.println("7. Update Book");
            System.out.println("8. Exit");

            System.out.print("\nEnter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Please enter a number from 1 to 8.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();

            if (choice == 1) {
                viewBooks();
            } 
            else if (choice == 2) {
                addBook(sc);
            }
            else if (choice == 3) {
                searchBook(sc);
            }
            else if (choice == 4) {
                issueBook(sc);
            }
            else if (choice == 5) {
                returnBook(sc);
            }
            else if (choice == 6) {
                deleteBook(sc);
            }
            else if (choice == 7) {
                updateBook(sc);
            }
            else if (choice == 8) {
                System.out.println("Thank you for using Library Management System!");
                break;
            } 
            else {
                System.out.println("This feature is coming next.");
            }
        }

        sc.close();
    }

    public static void viewBooks() {

        try {
            Connection con = DBConnection.getConnection();


            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            System.out.println("\nBook ID | Title | Author | Available");
            System.out.println("--------------------------------------");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("book_id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("author") + " | " +
                    rs.getInt("available")
                );
            }

            con.close();

        } catch (Exception e) {
            System.out.println("Error while fetching books!");
            e.printStackTrace();
        }
    }
    public static void addBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid Book ID.");
            sc.nextLine();
            return;
        }

        int bookId = sc.nextInt();
      
        sc.nextLine();

        Connection con = DBConnection.getConnection();

        Statement checkStmt = con.createStatement();

        String checkQuery = "SELECT book_id FROM books WHERE book_id = " + bookId;

        ResultSet checkRs = checkStmt.executeQuery(checkQuery);

        if (checkRs.next()) {
            System.out.println("Book ID already exists!");
            con.close();
            return;
        }

        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Author Name: ");
        String author = sc.nextLine();

        Statement stmt = con.createStatement();

        String query = "INSERT INTO books (book_id, title, author, available) VALUES ("
                + bookId + ", '" + title + "', '" + author + "', 1)";

        stmt.executeUpdate(query);

        System.out.println("Book added successfully!");

        con.close();      

    } catch (Exception e) {
        System.out.println("Error while adding book!");
        e.printStackTrace();
    }
}
public static void searchBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID to search: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid Book ID.");
            sc.nextLine();
            return;
        }

        int bookId = sc.nextInt();

        Connection con = DBConnection.getConnection();

        Statement stmt = con.createStatement();

        String query = "SELECT * FROM books WHERE book_id = " + bookId;

        ResultSet rs = stmt.executeQuery(query);

        if (rs.next()) {
            System.out.println("\nBook Found!");
            System.out.println("Book ID: " + rs.getInt("book_id"));
            System.out.println("Title: " + rs.getString("title"));
            System.out.println("Author: " + rs.getString("author"));
            System.out.println("Available: " + rs.getInt("available"));
        } else {
            System.out.println("Book not found!");
        }

        con.close();

    } catch (Exception e) {
        System.out.println("Error while searching book!");
        e.printStackTrace();
    }
}
public static void issueBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID to issue: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid Book ID.");
            sc.nextLine();
            return;
        }

        int bookId = sc.nextInt();

        Connection con = DBConnection.getConnection();

        Statement stmt = con.createStatement();

        String checkQuery = "SELECT available FROM books WHERE book_id = " + bookId;
        ResultSet rs = stmt.executeQuery(checkQuery);

        if (rs.next()) {

            int available = rs.getInt("available");

            if (available == 1) {

                String updateQuery =
                    "UPDATE books SET available = 0 WHERE book_id = " + bookId;

                stmt.executeUpdate(updateQuery);

                System.out.println("Book issued successfully!");

            } else {
                System.out.println("Book is already issued!");
            }

        } else {
            System.out.println("Book not found!");
        }

        con.close();

    } catch (Exception e) {
        System.out.println("Error while issuing book!");
        e.printStackTrace();
    }
}
public static void returnBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID to return: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid Book ID.");
            sc.nextLine();
            return;
        }

        int bookId = sc.nextInt();
        Connection con = DBConnection.getConnection();

        Statement stmt = con.createStatement();

        String checkQuery = "SELECT available FROM books WHERE book_id = " + bookId;
        ResultSet rs = stmt.executeQuery(checkQuery);

        if (rs.next()) {

            int available = rs.getInt("available");

            if (available == 0) {

                String updateQuery =
                    "UPDATE books SET available = 1 WHERE book_id = " + bookId;

                stmt.executeUpdate(updateQuery);

                System.out.println("Book returned successfully!");

            } else {
                System.out.println("Book is already available!");
            }

        } else {
            System.out.println("Book not found!");
        }

        con.close();

    } catch (Exception e) {
        System.out.println("Error while returning book!");
        e.printStackTrace();
    }
}
public static void deleteBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID to delete: ");

        if (!sc.hasNextInt()) {
           System.out.println("Please enter a valid Book ID.");
           sc.nextLine();
           return;
        }

        int bookId = sc.nextInt();

        Connection con = DBConnection.getConnection();
        Statement stmt = con.createStatement();

        String checkQuery = "SELECT * FROM books WHERE book_id = " + bookId;
        ResultSet rs = stmt.executeQuery(checkQuery);

        if (rs.next()) {

            String deleteQuery = "DELETE FROM books WHERE book_id = " + bookId;
            stmt.executeUpdate(deleteQuery);

            System.out.println("Book deleted successfully!");

        } else {

            System.out.println("Book not found!");
        }

        con.close();

    } catch (Exception e) {

        System.out.println("Error while deleting book!");
        e.printStackTrace();
    }
}
public static void updateBook(Scanner sc) {

    try {
        System.out.print("Enter Book ID to update: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid Book ID.");
            sc.nextLine();
             return;
        }

        int bookId = sc.nextInt();
        sc.nextLine();

        Connection con = DBConnection.getConnection();
        Statement stmt = con.createStatement();

        String checkQuery = "SELECT * FROM books WHERE book_id = " + bookId;
        ResultSet rs = stmt.executeQuery(checkQuery);

        if (rs.next()) {

            System.out.print("Enter new Book Title: ");
            String title = sc.nextLine();

            System.out.print("Enter new Author Name: ");
            String author = sc.nextLine();

            String updateQuery =
                "UPDATE books SET title = '" + title +
                "', author = '" + author +
                "' WHERE book_id = " + bookId;

            stmt.executeUpdate(updateQuery);

            System.out.println("Book updated successfully!");

        } else {

            System.out.println("Book not found!");
        }

        con.close();

    } catch (Exception e) {

        System.out.println("Error while updating book!");
        e.printStackTrace();
    }
}

}
