package model;

/**
 * Driver is a model class that represents one row in the drivers table.
 * It stores driver information in Java before sending it to or reading it from the database.
 */
public class Drivers {

    // Fields match the columns in the drivers table
    private int driverId;
    private String driverName;
    private String licenseNo;
    private String contactNo;
    private String status;

    // Empty constructor
    // This is useful when creating an object first, then setting values one by one
    public Drivers() {
    }

    // Constructor with all fields
    // This is useful when you already have all the data
    public Drivers(int driverId, String driverName, String licenseNo, String contactNo, String status) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.licenseNo = licenseNo;
        this.contactNo = contactNo;
        this.status = status;
    }

    // Getter and setter for driverId
    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    // Getter and setter for driverName
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    // Getter and setter for licenseNo
    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    // Getter and setter for contactNo
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    // Getter and setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}