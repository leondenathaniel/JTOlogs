package model;

public class Drivers {
    private int driverId;
    private String displayId;
    private String driverName;
    private String licenseNo;
    private String contactNo;
    private String status;

    public Drivers() {
    }

    public Drivers(int driverId, String displayId, String driverName, String licenseNo, String contactNo, String status) {
        this.driverId = driverId;
        this.displayId = displayId;
        this.driverName = driverName;
        this.licenseNo = licenseNo;
        this.contactNo = contactNo;
        this.status = status;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}