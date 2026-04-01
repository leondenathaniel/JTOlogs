/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataaccess;

import Konnectionz.DBConnection;
import model.Driver;
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
public class Driver {

    /**
     * Inserts a new driver record into the database.
     * Returns true if the insert was successful, otherwise false.
     */
    public boolean insertDriver(Driver driver) {
        String sql = "INSERT INTO drivers (driver_name, license_no, contact_no, status) "
                   + "VALUES (?, ?, ?, ?)";

        // try-with-resources automatically closes the connection and statement
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, driver.getDriverName()); // driver name from the form
            pst.setString(2, driver.getLicenseNo());   // unique license number
            pst.setString(3, driver.getContactNo());   // contact number
            pst.setString(4, driver.getStatus());      // active or inactive

            return pst.executeUpdate() > 0; // true if one row was inserted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing driver record using driver_id.
     * driver_id is important because it tells the database exactly which row to edit.
     */
    public boolean updateDriver(Driver driver) {
        String sql = "UPDATE drivers "
                   + "SET driver_name = ?, license_no = ?, contact_no = ?, status = ? "
                   + "WHERE driver_id = ?";

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
    public List<Driver> getAllDrivers() {
        List<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers ORDER BY driver_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                Driver driver = new Driver();
                driver.setDriverId(rs.getInt("driver_id"));
                driver.setDriverName(rs.getString("driver_name"));
                driver.setLicenseNo(rs.getString("license_no"));
                driver.setContactNo(rs.getString("contact_no"));
                driver.setStatus(rs.getString("status"));

                drivers.add(driver); // add one driver object to the list
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drivers;
    }

    /**
     * Searches drivers by name, license number, or contact number.
     * This is useful for the search bar in the DriverForm.
     */
    public List<Driver> searchDrivers(String keyword) {
        List<Driver> drivers = new ArrayList<>();
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
                    Driver driver = new Driver();
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
    public Driver getDriverById(int driverId) {
        String sql = "SELECT * FROM drivers WHERE driver_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, driverId);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Driver driver = new Driver();
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
}
