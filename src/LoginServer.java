package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServer {

    public static boolean login(String name, String password, String role) {

        String query = "SELECT * FROM users WHERE name = ? AND password = ? AND role = ?";

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement pst = con.prepareStatement(query);

            pst.setString(1, name);
            pst.setString(2, password);
            pst.setString(3, role);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful!");
                return true;
            } else {
                System.out.println("Invalid username, password or role.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("Login failed!");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

        boolean result = login("admin", "admin123", "ADMIN");

        if (result) {
            System.out.println("Welcome Admin!");
        } else {
            System.out.println("Login failed.");
        }
    }
}