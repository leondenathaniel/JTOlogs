package dataaccess;

import Konnectionz.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class User {

    /**
     * This method checks if the username and password exist in the database.
     * Returns the role if successful, otherwise returns null.
     */
    public String login(String username, String password) {

        String role = null;

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT role FROM users WHERE username = ? AND password = ?";

            PreparedStatement pst = conn.prepareStatement(sql);

            // Set values safely (prevents SQL injection)
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // If match found, get the role (admin or staff)
                role = rs.getString("role");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return role;
    }
}
