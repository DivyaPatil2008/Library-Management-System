package src;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LibraryServer {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080),
            0
        );

        // ================= PAGES =================

        server.createContext("/", LibraryServer::home);
        server.createContext("/login.html", LibraryServer::loginPage);
        server.createContext("/index.html", LibraryServer::dashboard);
        server.createContext("/reset-password.html", LibraryServer::resetPage);
        server.createContext("/create-account.html", LibraryServer::createAccountPage);

        // ================= CSS / JS =================

        server.createContext("/style.css", LibraryServer::style);
        server.createContext("/script.js", LibraryServer::script);

        // ================= API =================

        server.createContext("/login", LibraryServer::login);
        server.createContext("/create-account", LibraryServer::createAccount);
        server.createContext("/reset-password", LibraryServer::resetPassword);
        server.createContext("/books", LibraryServer::books);
        server.createContext("/stats", LibraryServer::stats);
        server.createContext("/add-book", LibraryServer::addBook);
        server.createContext("/issue-book", LibraryServer::issueBook);
        server.createContext("/return-book", LibraryServer::returnBook);
        server.createContext("/update-book", LibraryServer::updateBook);
        server.createContext("/delete-book", LibraryServer::deleteBook);

        server.setExecutor(null);
        server.start();

        System.out.println("Library Server started!");
        System.out.println("Open: http://localhost:8080/login.html");
    }


    // ================= HOME =================

    private static void home(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/login.html",
            "text/html"
        );
    }


    // ================= LOGIN PAGE =================

    private static void loginPage(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/login.html",
            "text/html"
        );
    }


    // ================= DASHBOARD =================

    private static void dashboard(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/index.html",
            "text/html"
        );
    }


    // ================= RESET PASSWORD PAGE =================

    private static void resetPage(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/reset-password.html",
            "text/html"
        );
    }


    // ================= CREATE ACCOUNT PAGE =================

    private static void createAccountPage(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/create-account.html",
            "text/html"
        );
    }


    // ================= RESET PASSWORD =================

    private static void resetPassword(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Method Not Allowed",
                405
            );

            return;
        }

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            String newPassword =
                getValue(body, "newPassword");

            String confirmPassword =
                getValue(body, "confirmPassword");


            // ================= CHECK EMPTY FIELDS =================

            if (username.isEmpty() ||
                newPassword.isEmpty() ||
                confirmPassword.isEmpty()) {

                sendResponse(
                    exchange,
                    "Please fill all fields.",
                    400
                );

                return;
            }


            // ================= CHECK PASSWORD =================

            if (!newPassword.equals(confirmPassword)) {

                sendResponse(
                    exchange,
                    "Passwords do not match.",
                    400
                );

                return;
            }


            // ================= FIND USER =================

            Connection con =
                DBConnection.getConnection();

            String findQuery =
                "SELECT user_id FROM users " +
                "WHERE name = ? LIMIT 1";


            try (
                PreparedStatement findStmt =
                    con.prepareStatement(findQuery)
            ) {

                findStmt.setString(
                    1,
                    username
                );

                ResultSet rs =
                    findStmt.executeQuery();


                if (!rs.next()) {

                    sendResponse(
                        exchange,
                        "Username not found.",
                        404
                    );

                    con.close();
                    return;
                }


                int userId =
                    rs.getInt("user_id");


                // ================= UPDATE PASSWORD =================

                String updateQuery =
                    "UPDATE users SET password = ? " +
                    "WHERE user_id = ?";


                try (
                    PreparedStatement updateStmt =
                        con.prepareStatement(updateQuery)
                ) {

                    updateStmt.setString(
                        1,
                        newPassword
                    );

                    updateStmt.setInt(
                        2,
                        userId
                    );

                    updateStmt.executeUpdate();


                    sendResponse(
                        exchange,
                        "Password reset successfully!",
                        200
                    );
                }
            }

            con.close();

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to reset password.",
                500
            );
        }
    }


    // ================= CSS =================

    private static void style(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/style.css",
            "text/css"
        );
    }


    // ================= JAVASCRIPT =================

    private static void script(HttpExchange exchange)
            throws IOException {

        serveFile(
            exchange,
            "frontend/script.js",
            "application/javascript"
        );
    }


    // ================= SERVE FILE =================

    private static void serveFile(
            HttpExchange exchange,
            String filePath,
            String contentType)
            throws IOException {

        File file = new File(filePath);

        if (!file.exists()) {

            sendResponse(
                exchange,
                "File not found: " + filePath,
                404
            );

            return;
        }

        byte[] content =
            Files.readAllBytes(file.toPath());

        exchange.getResponseHeaders().set(
            "Content-Type",
            contentType + "; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
            200,
            content.length
        );

        OutputStream output =
            exchange.getResponseBody();

        output.write(content);
        output.close();
    }


    // ================= LOGIN =================

    private static void login(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use the login form.",
                405
            );

            return;
        }

        String body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String username =
            getValue(body, "username");

        String password =
            getValue(body, "password");

        String role =
            getValue(body, "role");

        role = role.toUpperCase();

        boolean result =
            LoginServer.login(
                username,
                password,
                role
            );

        if (result) {

            sendResponse(
                exchange,
                "Login successful!",
                200
            );

        } else {

            sendResponse(
                exchange,
                "Invalid username, password or role.",
                401
            );
        }
    }


    // ================= CREATE ACCOUNT =================

    private static void createAccount(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            String password =
                getValue(body, "password");

            String role =
                getValue(body, "role");


            if (username.isEmpty() ||
                password.isEmpty() ||
                role.isEmpty()) {

                sendResponse(
                    exchange,
                    "Please fill all fields.",
                    400
                );

                return;
            }

            role = role.toUpperCase();


            if (!role.equals("USER")) {

                sendResponse(
                    exchange,
                    "Only User accounts can be created here.",
                    403
                );

                return;
            }


            // ================= CHECK USERNAME =================

            String checkQuery =
                "SELECT user_id FROM users " +
                "WHERE name = ? LIMIT 1";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(checkQuery)
            ) {

                pst.setString(
                    1,
                    username
                );

                ResultSet rs =
                    pst.executeQuery();

                if (rs.next()) {

                    sendResponse(
                        exchange,
                        "Username already exists.",
                        409
                    );

                    return;
                }
            }


            // ================= INSERT USER =================

            String insertQuery =
                "INSERT INTO users " +
                "(name, password, role) " +
                "VALUES (?, ?, ?)";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(insertQuery)
            ) {

                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, role);

                pst.executeUpdate();
            }


            sendResponse(
                exchange,
                "Account created successfully!",
                201
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to create account.",
                500
            );
        }
    }


    // ================= VIEW BOOKS =================

    private static void books(HttpExchange exchange)
            throws IOException {

        StringBuilder response =
            new StringBuilder();

        String query =
            "SELECT book_id, title, author, available " +
            "FROM books ORDER BY book_id";

        try (
            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(query);

            ResultSet rs =
                pst.executeQuery()
        ) {

            response.append("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    response.append(",");
                }

                response.append("{");

                response.append("\"book_id\":")
                    .append(rs.getInt("book_id"))
                    .append(",");

                response.append("\"title\":\"")
                    .append(jsonEscape(
                        rs.getString("title")
                    ))
                    .append("\",");

                response.append("\"author\":\"")
                    .append(jsonEscape(
                        rs.getString("author")
                    ))
                    .append("\",");

                response.append("\"available\":")
                    .append(rs.getInt("available"));

                response.append("}");

                first = false;
            }

            response.append("]");

            exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8"
            );

            byte[] data =
                response.toString()
                    .getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(
                200,
                data.length
            );

            OutputStream output =
                exchange.getResponseBody();

            output.write(data);
            output.close();

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Error loading books.",
                500
            );
        }
    }


    // ================= DASHBOARD STATS =================

    private static void stats(HttpExchange exchange)
            throws IOException {

        String query =
            "SELECT " +
            "COUNT(*) AS total, " +
            "SUM(CASE WHEN available = 1 THEN 1 ELSE 0 END) AS available, " +
            "SUM(CASE WHEN available = 0 THEN 1 ELSE 0 END) AS issued " +
            "FROM books";

        try (
            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(query);

            ResultSet rs =
                pst.executeQuery()
        ) {

            if (rs.next()) {

                int total =
                    rs.getInt("total");

                int available =
                    rs.getInt("available");

                int issued =
                    rs.getInt("issued");

                String response =
                    "{"
                    + "\"total\":" + total + ","
                    + "\"available\":" + available + ","
                    + "\"issued\":" + issued
                    + "}";

                exchange.getResponseHeaders().set(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                );

                byte[] data =
                    response.getBytes(
                        StandardCharsets.UTF_8
                    );

                exchange.sendResponseHeaders(
                    200,
                    data.length
                );

                OutputStream output =
                    exchange.getResponseBody();

                output.write(data);
                output.close();
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to load statistics.",
                500
            );
        }
    }


    // ================= ADD BOOK =================

    private static void addBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            if (!isAdmin(username)) {

                sendResponse(
                    exchange,
                    "Only Admin can add books.",
                    403
                );

                return;
            }

            String bookId =
                getValue(body, "book_id");

            String title =
                getValue(body, "title");

            String author =
                getValue(body, "author");

            String query =
                "INSERT INTO books " +
                "(book_id, title, author, available) " +
                "VALUES (?, ?, ?, 1)";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(query)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                pst.setString(2, title);
                pst.setString(3, author);

                pst.executeUpdate();
            }


            sendResponse(
                exchange,
                "Book added successfully!",
                200
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to add book.",
                500
            );
        }
    }


    // ================= ISSUE BOOK =================

    private static void issueBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        Connection con = null;

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            String bookId =
                getValue(body, "book_id");

            int userId =
                getUserId(username);

            if (userId == -1) {

                sendResponse(
                    exchange,
                    "User not found.",
                    404
                );

                return;
            }

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            String checkBook =
                "SELECT available FROM books " +
                "WHERE book_id = ? FOR UPDATE";


            try (
                PreparedStatement pst =
                    con.prepareStatement(checkBook)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                ResultSet rs =
                    pst.executeQuery();

                if (!rs.next()) {

                    con.rollback();

                    sendResponse(
                        exchange,
                        "Book not found.",
                        404
                    );

                    return;
                }

                if (rs.getInt("available") == 0) {

                    con.rollback();

                    sendResponse(
                        exchange,
                        "Book is already issued.",
                        400
                    );

                    return;
                }
            }


            String insertRecord =
                "INSERT INTO issue_records " +
                "(book_id, user_id, issue_date, due_date, return_date, fine) " +
                "VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), NULL, 0.00)";


            try (
                PreparedStatement pst =
                    con.prepareStatement(insertRecord)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                pst.setInt(2, userId);

                pst.executeUpdate();
            }


            String updateBook =
                "UPDATE books SET available = 0 " +
                "WHERE book_id = ?";


            try (
                PreparedStatement pst =
                    con.prepareStatement(updateBook)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                pst.executeUpdate();
            }

            con.commit();


            sendResponse(
                exchange,
                "Book issued successfully!",
                200
            );

        } catch (Exception e) {

            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ignored) {
            }

            sendResponse(
                exchange,
                "Unable to issue book.",
                500
            );

        } finally {

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ignored) {
            }
        }
    }


    // ================= RETURN BOOK =================

    private static void returnBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        Connection con = null;

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            String bookId =
                getValue(body, "book_id");

            int userId =
                getUserId(username);

            if (userId == -1) {

                sendResponse(
                    exchange,
                    "User not found.",
                    404
                );

                return;
            }

            con = DBConnection.getConnection();

            con.setAutoCommit(false);

            String recordQuery =
                "SELECT record_id FROM issue_records " +
                "WHERE book_id = ? " +
                "AND user_id = ? " +
                "AND return_date IS NULL " +
                "ORDER BY issue_date DESC LIMIT 1";

            int recordId = -1;


            try (
                PreparedStatement pst =
                    con.prepareStatement(recordQuery)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                pst.setInt(2, userId);

                ResultSet rs =
                    pst.executeQuery();

                if (rs.next()) {

                    recordId =
                        rs.getInt("record_id");
                }
            }


            if (recordId == -1) {

                con.rollback();

                sendResponse(
                    exchange,
                    "No active issue record found for this book.",
                    400
                );

                return;
            }


            String updateRecord =
                "UPDATE issue_records " +
                "SET return_date = CURDATE(), fine = 0.00 " +
                "WHERE record_id = ?";


            try (
                PreparedStatement pst =
                    con.prepareStatement(updateRecord)
            ) {

                pst.setInt(1, recordId);

                pst.executeUpdate();
            }


            String updateBook =
                "UPDATE books SET available = 1 " +
                "WHERE book_id = ?";


            try (
                PreparedStatement pst =
                    con.prepareStatement(updateBook)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                pst.executeUpdate();
            }

            con.commit();


            sendResponse(
                exchange,
                "Book returned successfully!",
                200
            );

        } catch (Exception e) {

            e.printStackTrace();

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ignored) {
            }

            sendResponse(
                exchange,
                "Unable to return book.",
                500
            );

        } finally {

            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ignored) {
            }
        }
    }


    // ================= UPDATE BOOK =================

    private static void updateBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            if (!isAdmin(username)) {

                sendResponse(
                    exchange,
                    "Only Admin can update books.",
                    403
                );

                return;
            }

            String bookId =
                getValue(body, "book_id");

            String title =
                getValue(body, "title");

            String author =
                getValue(body, "author");

            String query =
                "UPDATE books " +
                "SET title = ?, author = ? " +
                "WHERE book_id = ?";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(query)
            ) {

                pst.setString(1, title);
                pst.setString(2, author);

                pst.setInt(
                    3,
                    Integer.parseInt(bookId)
                );

                int rows =
                    pst.executeUpdate();

                if (rows == 0) {

                    sendResponse(
                        exchange,
                        "Book not found.",
                        404
                    );

                    return;
                }
            }


            sendResponse(
                exchange,
                "Book updated successfully!",
                200
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to update book.",
                500
            );
        }
    }


    // ================= DELETE BOOK =================

    private static void deleteBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                exchange,
                "Please use POST method.",
                405
            );

            return;
        }

        try {

            String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
            );

            String username =
                getValue(body, "username");

            if (!isAdmin(username)) {

                sendResponse(
                    exchange,
                    "Only Admin can delete books.",
                    403
                );

                return;
            }

            String bookId =
                getValue(body, "book_id");


            // ================= CHECK ACTIVE ISSUE =================

            String checkQuery =
                "SELECT COUNT(*) FROM issue_records " +
                "WHERE book_id = ? AND return_date IS NULL";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(checkQuery)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                ResultSet rs =
                    pst.executeQuery();

                if (rs.next() &&
                    rs.getInt(1) > 0) {

                    sendResponse(
                        exchange,
                        "Cannot delete an issued book. Return it first.",
                        400
                    );

                    return;
                }
            }


            // ================= DELETE =================

            String deleteQuery =
                "DELETE FROM books WHERE book_id = ?";


            try (
                Connection con =
                    DBConnection.getConnection();

                PreparedStatement pst =
                    con.prepareStatement(deleteQuery)
            ) {

                pst.setInt(
                    1,
                    Integer.parseInt(bookId)
                );

                int rows =
                    pst.executeUpdate();

                if (rows == 0) {

                    sendResponse(
                        exchange,
                        "Book not found.",
                        404
                    );

                    return;
                }
            }


            sendResponse(
                exchange,
                "Book deleted successfully!",
                200
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                exchange,
                "Unable to delete book.",
                500
            );
        }
    }


    // ================= GET USER ID =================

    private static int getUserId(String username)
            throws Exception {

        String query =
            "SELECT user_id FROM users " +
            "WHERE name = ? LIMIT 1";


        try (
            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(query)
        ) {

            pst.setString(
                1,
                username
            );

            ResultSet rs =
                pst.executeQuery();

            if (rs.next()) {

                return rs.getInt("user_id");
            }
        }

        return -1;
    }


    // ================= CHECK ADMIN =================

    private static boolean isAdmin(String username)
            throws Exception {

        String query =
            "SELECT role FROM users " +
            "WHERE name = ? LIMIT 1";


        try (
            Connection con =
                DBConnection.getConnection();

            PreparedStatement pst =
                con.prepareStatement(query)
        ) {

            pst.setString(
                1,
                username
            );

            ResultSet rs =
                pst.executeQuery();

            if (rs.next()) {

                return "ADMIN".equalsIgnoreCase(
                    rs.getString("role")
                );
            }
        }

        return false;
    }


    // ================= GET JSON VALUE =================

    private static String getValue(
            String json,
            String key) {

        String search =
            "\"" + key + "\":\"";

        int start =
            json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start += search.length();

        int end =
            json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(start, end);
    }


    // ================= JSON ESCAPE =================

    private static String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }


    // ================= SEND RESPONSE =================

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            int status)
            throws IOException {

        byte[] data =
            response.getBytes(
                StandardCharsets.UTF_8
            );

        exchange.getResponseHeaders().set(
            "Content-Type",
            "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
            status,
            data.length
        );

        OutputStream output =
            exchange.getResponseBody();

        output.write(data);
        output.close();
    }
}