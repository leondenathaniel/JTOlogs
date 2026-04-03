/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataaccess;

import Konnectionz.DBConnection;
import model.Drivers;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DriverDAO handles all database operations for the drivers table.
 * This keeps SQL away from the GUI and makes the code easier to maintain.
 */
public class DriverDAO {

    
     /*
    ================================================================ CONTAINMENT: ALL DRIVERS TABLE ==========================================================================
    */
    
    public String generateNextDriverDisplayId() {
        String nextId = "DRV-0001";
        String sql = "SELECT display_id " +
                     "FROM drivers " +
                     "WHERE display_id LIKE 'DRV-%' " +
                     "ORDER BY CAST(SUBSTRING(display_id, 5) AS UNSIGNED) DESC " +
                     "LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("display_id");
                String numberPart = lastId.substring(4); // removes "DRV-"
                int nextNumber = Integer.parseInt(numberPart) + 1;
                nextId = String.format("DRV-%04d", nextNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return nextId;
    }
    
    
        public List<Drivers> getAllDrivers() {
        List<Drivers> drivers = new ArrayList<>();

        String sql = "SELECT driver_id, display_id, driver_name, license_no, contact_no, status " +
                     "FROM drivers " +
                     "ORDER BY CAST(SUBSTRING(display_id, 5) AS UNSIGNED) ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Drivers driver = new Drivers();
                driver.setDriverId(rs.getInt("driver_id"));
                driver.setDisplayId(rs.getString("display_id"));
                driver.setDriverName(rs.getString("driver_name"));
                driver.setLicenseNo(rs.getString("license_no"));
                driver.setContactNo(rs.getString("contact_no"));
                driver.setStatus(rs.getString("status"));

                drivers.add(driver);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drivers;
    }
    
    /* 
    WITHIN THIS SECTION CONTAINS THE DRIVERS TABLE END_BORDER;=====================================
    */
    
    
    /**
     * Inserts a new driver record into the database.
     * Returns true if the insert was successful, otherwise false.
     */
    public boolean insertDriver(Drivers driver) {
        String sql = "INSERT INTO drivers (display_id, driver_name, license_no, contact_no, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        String displayId = generateNextDriverDisplayId();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, displayId);
            pst.setString(2, driver.getDriverName());
            pst.setString(3, driver.getLicenseNo());
            pst.setString(4, driver.getContactNo());
            pst.setString(5, driver.getStatus());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                driver.setDisplayId(displayId);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Updates an existing driver record using driver_id.
     * driver_id is important because it tells the database exactly which row to edit.
     */
    public boolean updateDriver(Drivers driver) {
        String sql = "UPDATE drivers " +
                     "SET driver_name = ?, license_no = ?, contact_no = ?, status = ? " +
                     "WHERE driver_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, driver.getDriverName());
            pst.setString(2, driver.getLicenseNo());
            pst.setString(3, driver.getContactNo());
            pst.setString(4, driver.getStatus());
            pst.setInt(5, driver.getDriverId());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a driver record permanently.
     * If you later want soft delete, this can be changed to update status = 'inactive'.
     */
    public boolean deleteDriver(int driverId) {
        String sql = "DELETE FROM drivers WHERE driver_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, driverId);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all driver records from the database.
     * This is used to fill the JTable.
     */

    
    /**
     * Searches drivers by name, license number, or contact number.
     * This is useful for the search bar in the DriverForm.
     */
    public List<Drivers> searchDrivers(String keyword) {
        List<Drivers> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers "
                   + "WHERE driver_name LIKE ? "
                   + "OR license_no LIKE ? "
                   + "OR contact_no LIKE ? "
                   + "ORDER BY driver_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            // Add % wildcards so partial matches are found
            String searchValue = "%" + keyword + "%";

            pst.setString(1, searchValue);
            pst.setString(2, searchValue);
            pst.setString(3, searchValue);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Drivers driver = new Drivers();
                    driver.setDriverId(rs.getInt("driver_id"));
                    driver.setDriverName(rs.getString("driver_name"));
                    driver.setLicenseNo(rs.getString("license_no"));
                    driver.setContactNo(rs.getString("contact_no"));
                    driver.setStatus(rs.getString("status"));

                    drivers.add(driver);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drivers;
    }

    /**
     * Gets a single driver by ID.
     * This is useful if you want to load one record for editing.
     */
    public Drivers getDriverById(int driverId) {
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, driverId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Drivers driver = new Drivers();
                    driver.setDriverId(rs.getInt("driver_id"));
                    driver.setDriverName(rs.getString("driver_name"));
                    driver.setLicenseNo(rs.getString("license_no"));
                    driver.setContactNo(rs.getString("contact_no"));
                    driver.setStatus(rs.getString("status"));

                    return driver;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public List<Drivers> getDriversSortedByName(boolean ascending) {
    List<Drivers> drivers = new ArrayList<>();

    // Choose ASC or DESC based on the selected sort option
    String order = ascending ? "ASC" : "DESC";

    // Sort by driver_name using SQL ORDER BY
    String sql = "SELECT * FROM drivers ORDER BY driver_name " + order;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            Drivers driver = new Drivers();
            driver.setDriverId(rs.getInt("driver_id"));
            driver.setDriverName(rs.getString("driver_name"));
            driver.setLicenseNo(rs.getString("license_no"));
            driver.setContactNo(rs.getString("contact_no"));
            driver.setStatus(rs.getString("status"));

            drivers.add(driver); // Add each record to the list
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return drivers;
    }
    
}
