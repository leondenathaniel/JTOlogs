package dataaccess;

import Konnectionz.DBConnection;
import model.Jeepneys;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JeepneyDAO {

    public String generateNextJeepneyDisplayId() {
        String nextId = "JEP-0001";
        String sql = "SELECT display_id " +
                     "FROM jeepneys " +
                     "WHERE display_id LIKE 'JEP-%' " +
                     "ORDER BY CAST(SUBSTRING(display_id, 5) AS UNSIGNED) DESC " +
                     "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("display_id");
                String numberPart = lastId.substring(4);   // removes "JEP-"
                int nextNumber = Integer.parseInt(numberPart) + 1;
                nextId = String.format("JEP-%04d", nextNumber);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextId;
    }

    /*
    CONTAINMENT: CRUD OF JEEPNEY
    */

    public boolean insertJeepney(Jeepneys jeepney) {
        String sql = "INSERT INTO jeepneys (display_id, plate_no, driver_id, route_name, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        String displayId = generateNextJeepneyDisplayId();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, displayId);
            pst.setString(2, jeepney.getPlateNo());
            pst.setInt(3, jeepney.getDriverId());
            pst.setString(4, jeepney.getRouteName());
            pst.setString(5, jeepney.getStatus());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                jeepney.setDisplayId(displayId);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public boolean updateJeepney(Jeepneys jeepney) {
        String sql = "UPDATE jeepneys SET plate_no = ?, driver_id = ?, route_name = ?, status = ? WHERE jeepney_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, jeepney.getPlateNo());
            pst.setInt(2, jeepney.getDriverId());
            pst.setString(3, jeepney.getRouteName());
            pst.setString(4, jeepney.getStatus());
            pst.setInt(5, jeepney.getJeepneyId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteJeepney(int jeepneyId) {
        String sql = "DELETE FROM jeepneys WHERE jeepney_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, jeepneyId);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /*
    END BORDER: CRUD OF JEEPNEY
    */
    

    

    public List<Jeepneys> getAllJeepneys() {
        List<Jeepneys> jeepneys = new ArrayList<>();

        String sql = "SELECT j.jeepney_id, j.plate_no, j.driver_id, d.driver_name, j.route_name, j.status " +
                     "FROM jeepneys j " +
                     "LEFT JOIN drivers d ON j.driver_id = d.driver_id " +
                     "ORDER BY j.plate_no ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Jeepneys j = new Jeepneys();
                j.setJeepneyId(rs.getInt("jeepney_id"));
                j.setPlateNo(rs.getString("plate_no"));
                j.setDriverId(rs.getInt("driver_id"));
                j.setDriverName(rs.getString("driver_name"));
                j.setRouteName(rs.getString("route_name"));
                j.setStatus(rs.getString("status"));
                jeepneys.add(j);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jeepneys;
    }

    public List<Jeepneys> searchJeepneys(String keyword) {
        List<Jeepneys> jeepneys = new ArrayList<>();
        String sql = "SELECT jeepney_id, display_id, plate_no, driver_id, route_name, status " +
                     "FROM jeepneys " +
                     "WHERE display_id LIKE ? OR plate_no LIKE ? OR route_name LIKE ? " +
                     "ORDER BY CAST(SUBSTRING(display_id, 5) AS UNSIGNED) ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            pst.setString(1, like);
            pst.setString(2, like);
            pst.setString(3, like);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Jeepneys jeepney = new Jeepneys();
                    jeepney.setJeepneyId(rs.getInt("jeepney_id"));
                    jeepney.setDriverName(rs.getString("display_id"));
                    jeepney.setPlateNo(rs.getString("plate_no"));
                    jeepney.setDriverId(rs.getInt("driver_id"));
                    jeepney.setRouteName(rs.getString("route_name"));
                    jeepney.setStatus(rs.getString("status"));
                    jeepneys.add(jeepney);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jeepneys;
    }
}