/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

public class QueueItem {
    private int queueId;
    private int jeepneyId;
    private String displayId;
    private String plateNo;
    private String driverName;
    private String routeName;
    private Timestamp queueTime;
    private String status;

    public QueueItem() {
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
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

    public Timestamp getQueueTime() {
        return queueTime;
    }

    public void setQueueTime(Timestamp queueTime) {
        this.queueTime = queueTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
