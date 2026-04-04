/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

    public class DriverItem {
        private int driverId;
        private String driverName;

        public DriverItem(int driverId, String driverName) {
            this.driverId = driverId;
            this.driverName = driverName;
        }

        public int getDriverId() {
            return driverId;
        }

        @Override
        public String toString() {
            return driverName;
        }
    }
