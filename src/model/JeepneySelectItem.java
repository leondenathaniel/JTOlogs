/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class JeepneySelectItem {
    private int jeepneyId;
    private String displayText;

    public JeepneySelectItem(int jeepneyId, String displayText) {
        this.jeepneyId = jeepneyId;
        this.displayText = displayText;
    }

    public int getJeepneyId() {
        return jeepneyId;
    }

    @Override
    public String toString() {
        return displayText;
    }
}