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

    public Jeepneys(int jeepneyId, String displayId, String plateNo, int driverId, String routeName, String status) {
        this.jeepneyId = jeepneyId;
        this.displayId = displayId;
        this.plateNo = plateNo;
        this.driverId = driverId;
        this.routeName = routeName;
        this.status = status;
    }

    public int getJeepneyId() {
        return jeepneyId;
    }

    public void setJeepneyId(int jeepneyId) {
        this.jeepneyId = jeepneyId;
    }

    public String getDisplayId() {
        return displayId;
    }

    public void setDisplayId(String displayId) {
        this.displayId = displayId;
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