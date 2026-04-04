package model;

public class Jeepneys {
    private int jeepneyId;
    private String displayId;
    private String plateNo;
    private int driverId;
    private String driverName;
    private String routeName;
    private String status;

    public Jeepneys() {
    }

    public int getJeepneyId() {
        return jeepneyId;
    }
    
    public String getDisplayId() {
        return displayId;
    }
    
    public void setDisplayId(String displayId) {
        this.displayId = displayId;
    }

    public void setJeepneyId(int jeepneyId) {
        this.jeepneyId = jeepneyId;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}