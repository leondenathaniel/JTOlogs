/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataaccess;

import Konnectionz.DBConnection;
import java.util.List;
import model.JeepneySelectItem;
import java.sql.*;
import java.util.ArrayList;
import model.QueueItem;

/**
 *
 * @author Administrator
 */
public class QueueDAO {
    
    
    /*
    1st
    */
    
    public List<JeepneySelectItem> getAvailableJeepneysForComboBox() {
    List<JeepneySelectItem> list = new ArrayList<>();

    String sql = "SELECT j.jeepney_id, j.display_id, j.plate_no, d.driver_name " +
                 "FROM jeepneys j " +
                 "LEFT JOIN drivers d ON j.driver_id = d.driver_id " +
                 "WHERE j.status = 'AVAILABLE' " +
                 "ORDER BY j.display_id ASC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            String text = rs.getString("display_id") + " - " +
                          rs.getString("plate_no") + " - " +
                          rs.getString("driver_name");
            list.add(new JeepneySelectItem(rs.getInt("jeepney_id"), text));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    
    
    /*
    2nd
    */
    
    public boolean addJeepneyToQueue(int jeepneyId) {
    String checkSql = "SELECT COUNT(*) FROM queue_list WHERE jeepney_id = ? AND status IN ('queued', 'dispatched')";
    String insertSql = "INSERT INTO queue_list (jeepney_id, queue_time, status) VALUES (?, CURRENT_TIMESTAMP, 'queued')";
    String updateJeepneySql = "UPDATE jeepneys SET status = 'queued' WHERE jeepney_id = ?";

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement checkPst = conn.prepareStatement(checkSql)) {
            checkPst.setInt(1, jeepneyId);

            try (ResultSet rs = checkPst.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    return false;
                }
            }
        }

        try (PreparedStatement insertPst = conn.prepareStatement(insertSql);
             PreparedStatement updatePst = conn.prepareStatement(updateJeepneySql)) {

            insertPst.setInt(1, jeepneyId);
            updatePst.setInt(1, jeepneyId);

            int inserted = insertPst.executeUpdate();
            int updated = updatePst.executeUpdate();

            if (inserted > 0 && updated > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
//    3RD
    
    public List<QueueItem> getQueuedJeepneys() {
    List<QueueItem> list = new ArrayList<>();

    String sql = "SELECT q.queue_id, q.jeepney_id, j.display_id, j.plate_no, d.driver_name, " +
                 "j.route_name, q.queue_time, q.status " +
                 "FROM queue_list q " +
                 "JOIN jeepneys j ON q.jeepney_id = j.jeepney_id " +
                 "LEFT JOIN drivers d ON j.driver_id = d.driver_id " +
                 "WHERE q.status = 'queued' " +
                 "ORDER BY q.queue_time ASC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            QueueItem item = new QueueItem();
            item.setQueueId(rs.getInt("queue_id"));
            item.setJeepneyId(rs.getInt("jeepney_id"));
            item.setDisplayId(rs.getString("display_id"));
            item.setPlateNo(rs.getString("plate_no"));
            item.setDriverName(rs.getString("driver_name"));
            item.setRouteName(rs.getString("route_name"));
            item.setQueueTime(rs.getTimestamp("queue_time"));
            item.setStatus(rs.getString("status"));
            list.add(item);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    
//    4TH SECTION
    
    public List<QueueItem> getDispatchedJeepneys() {
    List<QueueItem> list = new ArrayList<>();

    String sql = "SELECT q.queue_id, q.jeepney_id, j.display_id, j.plate_no, d.driver_name, " +
                 "j.route_name, q.queue_time, q.status " +
                 "FROM queue_list q " +
                 "JOIN jeepneys j ON q.jeepney_id = j.jeepney_id " +
                 "LEFT JOIN drivers d ON j.driver_id = d.driver_id " +
                 "WHERE q.status = 'dispatched' " +
                 "ORDER BY q.queue_time ASC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        while (rs.next()) {
            QueueItem item = new QueueItem();
            item.setQueueId(rs.getInt("queue_id"));
            item.setJeepneyId(rs.getInt("jeepney_id"));
            item.setDisplayId(rs.getString("display_id"));
            item.setPlateNo(rs.getString("plate_no"));
            item.setDriverName(rs.getString("driver_name"));
            item.setRouteName(rs.getString("route_name"));
            item.setQueueTime(rs.getTimestamp("queue_time"));
            item.setStatus(rs.getString("status"));
            list.add(item);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    
//    5TH SECTION: 
    
    public boolean dispatchQueue(int queueId, int jeepneyId) {
        String updateQueueSql = "UPDATE queue_list SET status = 'dispatched', dispatched_time = CURRENT_TIMESTAMP WHERE queue_id = ?";
        String updateJeepneySql = "UPDATE jeepneys SET status = 'dispatched' WHERE jeepney_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateQueueSql);
                 PreparedStatement pst2 = conn.prepareStatement(updateJeepneySql)) {

                pst1.setInt(1, queueId);
                pst2.setInt(1, jeepneyId);

                int a = pst1.executeUpdate();
                int b = pst2.executeUpdate();

                if (a > 0 && b > 0) {
                    conn.commit();
                    return true;
                }

                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    6TH SECTION:
    
    public boolean markFinished(int queueId, int jeepneyId) {
        String updateQueueSql = "UPDATE queue_list SET status = 'finished', finished_time = CURRENT_TIMESTAMP WHERE queue_id = ?";
        String updateJeepneySql = "UPDATE jeepneys SET status = 'AVAILABLE' WHERE jeepney_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pst1 = conn.prepareStatement(updateQueueSql);
                 PreparedStatement pst2 = conn.prepareStatement(updateJeepneySql)) {

                pst1.setInt(1, queueId);
                pst2.setInt(1, jeepneyId);

                int a = pst1.executeUpdate();
                int b = pst2.executeUpdate();

                if (a > 0 && b > 0) {
                    conn.commit();
                    return true;
                }

                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
//    7TH SECTION:
    
    public boolean removeQueue(int queueId, int jeepneyId) {
    String updateQueueSql = "UPDATE queue_list SET status = 'cancelled' WHERE queue_id = ?";
    String updateJeepneySql = "UPDATE jeepneys SET status = 'AVAILABLE' WHERE jeepney_id = ?";

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement pst1 = conn.prepareStatement(updateQueueSql);
             PreparedStatement pst2 = conn.prepareStatement(updateJeepneySql)) {

            pst1.setInt(1, queueId);
            pst2.setInt(1, jeepneyId);

            int a = pst1.executeUpdate();
            int b = pst2.executeUpdate();

            if (a > 0 && b > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();
            return false;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
}
