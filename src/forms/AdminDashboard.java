package forms;

import java.awt.CardLayout;
import dataaccess.DriverDAO;
import model.Drivers;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Administrator
 */
public class AdminDashboard extends javax.swing.JFrame {

    // Holds the currently selected driver ID from the table
    // This is important for update and delete operations.
    private int selectedDriverId = -1;
    
    /**
     * Creates new form Dashboard
     */
    public AdminDashboard() {
        initComponents();
        loadDrivers();      // Load all driver records into the table on startup
        clearFields();      // Start with clean inputs
        
        
        
    // Add the panels to the CardLayout using card names
    pnlContent.add(pnlHome, "HOME");
    pnlContent.add(pnlQueue, "QUEUE");
    pnlContent.add(pnlJeepneys, "JEEPNEY");
    pnlContent.add(pnlDrivers, "DRIVER");
    pnlContent.add(pnlTripHistory, "TRIPHISTORY");
    pnlContent.add(pnlAccounts, "ACCOUNTS");
    pnlContent.add(pnlReports, "REPORTS");

    // Show Home panel first when dashboard opens
    showPanel("HOME");

    }
    
    private Drivers getDriverFromForm() {
        Drivers driver = new Drivers(); // Create a blank Driver object

        // If a row is selected, keep its ID.
        // If not selected yet, this stays -1 until a row click happens.
        driver.setDriverId(selectedDriverId);

        // Read values from the form and trim extra spaces
        driver.setDriverName(txtDriverName.getText().trim());
        driver.setLicenseNo(txtLicenseNo.getText().trim());
        driver.setContactNo(txtContactNo.getText().trim());
        driver.setStatus(cmbStatus.getSelectedItem().toString());

        return driver; // Return the completed model object
    }
    
    /**
     * Loads all drivers from the database into the JTable.
     * This is called on startup and after add/update/delete operations.
     */
    private void loadDrivers() {
        DriverDAO dao = new DriverDAO(); // Create DAO object to talk to the database
        List<Drivers> drivers = dao.getAllDrivers(); // Fetch all driver records
        loadDriversToTable(drivers);
        
        // Get the table model used by the JTable
        DefaultTableModel model = (DefaultTableModel) tblDrivers.getModel();
            

        // Clear old rows before loading fresh data
        model.setRowCount(0);

        // Loop through each driver and add it to the table
        for (Drivers d : drivers) {
            Object[] row = {
                d.getDriverId(),
                d.getDriverName(),
                d.getLicenseNo(),
                d.getContactNo(),
                d.getStatus()
            };

            model.addRow(row); // Add one row at a time
        }
    }
    
    /**
     * Clears all input fields so the form is ready for a new action.
     */
    private void clearFields() {
        //txtDriverId.setText("");         // Clear ID field
        txtDriverName.setText("");       // Clear name field
        txtLicenseNo.setText("");        // Clear license field
        txtContactNo.setText("");        // Clear contact field
        cmbStatus.setSelectedIndex(0);   // Reset status dropdown

        // Reset selected ID so update/delete won't act on an old record
        selectedDriverId = -1;
    }
    
    /**
     * Validates the required fields before saving.
     * Returns true if the input is valid, otherwise false.
     */
    private boolean validateDriverForm() {
        String name = txtDriverName.getText().trim();
        String licenseNo = txtLicenseNo.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Driver name is required.");
            txtDriverName.requestFocus();
            return false;
        }

        if (licenseNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "License number is required.");
            txtLicenseNo.requestFocus();
            return false;
        }

        return true;
    }
    
    /**
     * Adds a new driver record to the database.
     */
    private void addDriver() {
        // Check input first
        if (!validateDriverForm()) {
            return; // Stop here if validation fails
        }

        // Build the Driver object from the form
        Drivers driver = getDriverFromForm();

        // Send it to the DAO for saving
        DriverDAO dao = new DriverDAO();
        boolean success = dao.insertDriver(driver);

        if (success) {
            JOptionPane.showMessageDialog(this, "Driver added successfully.");
            loadDrivers();   // Refresh table so new record appears
            clearFields();   // Reset form for the next entry
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add driver.");
        }
    }

    /**
     * Updates the selected driver record in the database.
     */
    private void updateDriver() {
        // Make sure the user clicked a row first
        if (selectedDriverId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a driver to update.");
            return;
        }

        // Validate input before updating
        if (!validateDriverForm()) {
            return;
        }

        // Build Driver object using the selected ID
        Drivers driver = getDriverFromForm();

        // Update through DAO
        DriverDAO dao = new DriverDAO();
        boolean success = dao.updateDriver(driver);

        if (success) {
            JOptionPane.showMessageDialog(this, "Driver updated successfully.");
            loadDrivers();   // Reload table to show changes
            clearFields();   // Reset form
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update driver.");
        }
    }
    
    /**
     * Deletes the selected driver record from the database.
     */
    private void deleteDriver() {
        // Make sure a row was selected
        if (selectedDriverId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a driver to delete.");
            return;
        }

        // Ask for confirmation first
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this driver?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Stop if user clicked No
        }

        DriverDAO dao = new DriverDAO();
        boolean success = dao.deleteDriver(selectedDriverId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Driver deleted successfully.");
            loadDrivers();   // Refresh the table
            clearFields();   // Reset form
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete driver.");
        }
    }
    
    /**
     * @param username
     * @param role
     */
    public void setUser(String username, String role) {
    lblCurrentUser.setText("User: " + username + " (" + role + ")");
    }
    
    private void showPanel(String panelName) {
    CardLayout card = (CardLayout) pnlContent.getLayout(); // Get the CardLayout from the center panel
    card.show(pnlContent, panelName); // Show the panel whose name matches panelName
    }
    
    /*
    ================================================================ CONTAINMENT: DRIVERS FORM PANEL ==========================================================================
    */
    
    private void loadDriversToTable(List<Drivers> drivers) {
    DefaultTableModel model = (DefaultTableModel) tblDrivers.getModel();

    // Remove all old rows before showing new data
    model.setRowCount(0);

    // Loop through each Driver object and put it into the JTable
    for (Drivers d : drivers) {
        Object[] row = {
            d.getDriverId(),
            d.getDriverName(),
            d.getLicenseNo(),
            d.getContactNo(),
            d.getStatus()
        };

        model.addRow(row); // Add one row to the table
        }
    }
    
    private void sortDrivers() {
    // Read the selected text from the combo box
    String selectedSort = cmbSortDrivers.getSelectedItem().toString();

    // Create DAO object to fetch sorted records
    DriverDAO dao = new DriverDAO();

    // Decide whether to sort ascending or descending
    boolean ascending = selectedSort.equals("Sort by Name (A-Z)");

    // Get the sorted driver list from the database
    List<Drivers> drivers = dao.getDriversSortedByName(ascending);

    // Load the sorted result into the JTable
    loadDriversToTable(drivers);
    
    }
    
    
    
    
    /* 
    WITHIN THIS SECTION CONTAINS THE DRIVERS PANEL END_BORDER;=====================================
    */
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        lblDashboardLogo = new javax.swing.JLabel();
        lblSystemTitle = new javax.swing.JLabel();
        lblDashboardSubtitle = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        lblCurrentDateTime = new javax.swing.JLabel();
        lblCurrentUser = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnJeepneys = new javax.swing.JButton();
        btnDrivers = new javax.swing.JButton();
        btnTripHistory = new javax.swing.JButton();
        btnAccounts = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnQueue = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        pnlReports = new javax.swing.JPanel();
        lblJeepneyTitle4 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        cmbJeepneySelect3 = new javax.swing.JComboBox<>();
        btnAddToQueue11 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        pnlAccounts = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        lblJeepneyTitle3 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        btnAddToQueue9 = new javax.swing.JButton();
        btnAddToQueue12 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox<>();
        btnAddToQueue6 = new javax.swing.JButton();
        btnAddToQueue5 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        btnAddToQueue7 = new javax.swing.JButton();
        btnAddToQueue8 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        btnAddToQueue10 = new javax.swing.JButton();
        btnRefreshQueue7 = new javax.swing.JButton();
        pnlTripHistory = new javax.swing.JPanel();
        lblJeepneyTitle2 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        cmbJeepneySelect2 = new javax.swing.JComboBox<>();
        btnDispatch5 = new javax.swing.JButton();
        btnAddToQueue4 = new javax.swing.JButton();
        btnRefreshQueue6 = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        pnlDrivers = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        txtContactNo = new javax.swing.JTextField();
        txtDriverName = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtLicenseNo = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblDrivers = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        cmbSortDrivers = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblJeepneyTitle1 = new javax.swing.JLabel();
        pnlJeepneys = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btnDispatch1 = new javax.swing.JButton();
        btnUndoDispatch1 = new javax.swing.JButton();
        btnAddToQueue1 = new javax.swing.JButton();
        btnRefreshQueue1 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnDispatch2 = new javax.swing.JButton();
        btnUndoDispatch2 = new javax.swing.JButton();
        btnRefreshQueue2 = new javax.swing.JButton();
        lblJeepneyTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pnlHome = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        lblInstruction = new javax.swing.JLabel();
        pnlStat1 = new javax.swing.JPanel();
        pnlStat2 = new javax.swing.JPanel();
        pnlStat3 = new javax.swing.JPanel();
        pnlStat4 = new javax.swing.JPanel();
        pnlQueue = new javax.swing.JPanel();
        lblQueueTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblQueue = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDispatched = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbJeepneySelect = new javax.swing.JComboBox<>();
        btnDispatch = new javax.swing.JButton();
        btnUndoDispatch = new javax.swing.JButton();
        btnAddToQueue = new javax.swing.JButton();
        btnRefreshQueue = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(23, 128, 145));

        lblDashboardLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/logo-v0.0.1.png"))); // NOI18N

        lblSystemTitle.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblSystemTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblSystemTitle.setText("Jeepney Terminal Organizer");

        lblDashboardSubtitle.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        lblDashboardSubtitle.setForeground(new java.awt.Color(204, 204, 204));
        lblDashboardSubtitle.setText("Admin Dashboard - Welcome, admin");

        btnLogout.setBackground(new java.awt.Color(177, 0, 0));
        btnLogout.setFont(new java.awt.Font("Trebuchet MS", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("LOGOUT");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        lblCurrentDateTime.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCurrentDateTime.setForeground(new java.awt.Color(255, 255, 255));
        lblCurrentDateTime.setText("Current Time:");

        lblCurrentUser.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCurrentUser.setForeground(new java.awt.Color(255, 255, 255));
        lblCurrentUser.setText("Current User:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDashboardLogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSystemTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDashboardSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 449, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCurrentUser, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCurrentDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblDashboardLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblSystemTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCurrentDateTime))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblDashboardSubtitle, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblCurrentUser))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));

        btnHome.setBackground(new java.awt.Color(255, 255, 255));
        btnHome.setForeground(new java.awt.Color(0, 0, 0));
        btnHome.setText("Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnJeepneys.setBackground(new java.awt.Color(255, 255, 255));
        btnJeepneys.setForeground(new java.awt.Color(0, 0, 0));
        btnJeepneys.setText("Jeepneys");
        btnJeepneys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJeepneysActionPerformed(evt);
            }
        });

        btnDrivers.setBackground(new java.awt.Color(255, 255, 255));
        btnDrivers.setForeground(new java.awt.Color(0, 0, 0));
        btnDrivers.setText("Drivers");
        btnDrivers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDriversActionPerformed(evt);
            }
        });

        btnTripHistory.setBackground(new java.awt.Color(255, 255, 255));
        btnTripHistory.setForeground(new java.awt.Color(0, 0, 0));
        btnTripHistory.setText("Trip History");
        btnTripHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTripHistoryActionPerformed(evt);
            }
        });

        btnAccounts.setBackground(new java.awt.Color(255, 255, 255));
        btnAccounts.setForeground(new java.awt.Color(0, 0, 0));
        btnAccounts.setText("Accounts");
        btnAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAccountsActionPerformed(evt);
            }
        });

        btnReports.setBackground(new java.awt.Color(255, 255, 255));
        btnReports.setForeground(new java.awt.Color(0, 0, 0));
        btnReports.setText("Reports");
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
            }
        });

        btnQueue.setBackground(new java.awt.Color(255, 255, 255));
        btnQueue.setForeground(new java.awt.Color(0, 0, 0));
        btnQueue.setText("Queue");
        btnQueue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQueueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAccounts, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTripHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnJeepneys, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnQueue, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnJeepneys, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTripHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAccounts, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContent.setLayout(new java.awt.CardLayout());

        pnlReports.setBackground(new java.awt.Color(255, 255, 255));

        lblJeepneyTitle4.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblJeepneyTitle4.setForeground(new java.awt.Color(0, 0, 0));
        lblJeepneyTitle4.setText("Reports:");

        jPanel16.setBackground(new java.awt.Color(204, 204, 204));
        jPanel16.setForeground(new java.awt.Color(204, 204, 204));

        jLabel30.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setText("Select Report:");

        cmbJeepneySelect3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Summary Report", "Jeepney Report", "Driver Report", "Trip History Report", "Queue Report" }));

        btnAddToQueue11.setBackground(new java.awt.Color(0, 153, 153));
        btnAddToQueue11.setText("Generate Report");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbJeepneySelect3, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddToQueue11)
                .addContainerGap(464, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJeepneySelect3)
                    .addComponent(btnAddToQueue11)
                    .addComponent(jLabel30))
                .addContainerGap())
        );

        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane7.setViewportView(jTable5);

        javax.swing.GroupLayout pnlReportsLayout = new javax.swing.GroupLayout(pnlReports);
        pnlReports.setLayout(pnlReportsLayout);
        pnlReportsLayout.setHorizontalGroup(
            pnlReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportsLayout.createSequentialGroup()
                .addGap(445, 445, 445)
                .addComponent(lblJeepneyTitle4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlReportsLayout.createSequentialGroup()
                .addGroup(pnlReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlReportsLayout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jScrollPane7))
                    .addGroup(pnlReportsLayout.createSequentialGroup()
                        .addContainerGap(91, Short.MAX_VALUE)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35))
        );
        pnlReportsLayout.setVerticalGroup(
            pnlReportsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlReportsLayout.createSequentialGroup()
                .addComponent(lblJeepneyTitle4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );

        pnlContent.add(pnlReports, "card8");

        pnlAccounts.setBackground(new java.awt.Color(255, 255, 255));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(jTable4);

        lblJeepneyTitle3.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblJeepneyTitle3.setForeground(new java.awt.Color(0, 0, 0));
        lblJeepneyTitle3.setText("Account Management:");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        btnAddToQueue9.setBackground(new java.awt.Color(204, 51, 0));
        btnAddToQueue9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue9.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue9.setText("Change Password");
        btnAddToQueue9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue9ActionPerformed(evt);
            }
        });

        btnAddToQueue12.setBackground(new java.awt.Color(0, 153, 255));
        btnAddToQueue12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue12.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue12.setText("Change Username");
        btnAddToQueue12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue12ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI Emoji", 2, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("Username: (10-30 characters)");

        jLabel27.setFont(new java.awt.Font("Segoe UI Emoji", 2, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("Password: (6 min characters)");

        jLabel28.setFont(new java.awt.Font("Segoe UI Emoji", 2, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 0, 0));
        jLabel28.setText("Role:");

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STAFF" }));

        btnAddToQueue6.setBackground(new java.awt.Color(51, 102, 0));
        btnAddToQueue6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue6.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue6.setText("Unlock Account");
        btnAddToQueue6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue6ActionPerformed(evt);
            }
        });

        btnAddToQueue5.setBackground(new java.awt.Color(0, 153, 153));
        btnAddToQueue5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue5.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue5.setText("Initiliaze Creation");
        btnAddToQueue5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue5ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(128, 0, 0));
        jLabel29.setText("User Management Controls:");

        btnAddToQueue7.setBackground(new java.awt.Color(204, 153, 0));
        btnAddToQueue7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue7.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue7.setText("Lock Account");
        btnAddToQueue7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue7ActionPerformed(evt);
            }
        });

        btnAddToQueue8.setBackground(new java.awt.Color(102, 0, 0));
        btnAddToQueue8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue8.setForeground(new java.awt.Color(255, 255, 255));
        btnAddToQueue8.setText("Delete Account");
        btnAddToQueue8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue8ActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Segoe UI Emoji", 2, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setText("Confirm Password:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddToQueue8, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(btnAddToQueue7, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddToQueue12, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(btnAddToQueue6, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddToQueue9, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddToQueue5, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                        .addComponent(jTextField6)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField8))
                    .addComponent(jLabel29))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddToQueue5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddToQueue6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddToQueue9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddToQueue7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddToQueue12, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddToQueue8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("Create new account:");

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("Existing accounts:");

        btnAddToQueue10.setBackground(javax.swing.UIManager.getDefaults().getColor("Actions.Blue"));
        btnAddToQueue10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAddToQueue10.setText("Show Locked Accounts");
        btnAddToQueue10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue10ActionPerformed(evt);
            }
        });

        btnRefreshQueue7.setBackground(java.awt.SystemColor.controlDkShadow);
        btnRefreshQueue7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRefreshQueue7.setText("Refresh");

        javax.swing.GroupLayout pnlAccountsLayout = new javax.swing.GroupLayout(pnlAccounts);
        pnlAccounts.setLayout(pnlAccountsLayout);
        pnlAccountsLayout.setHorizontalGroup(
            pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccountsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJeepneyTitle3)
                .addGap(354, 354, 354))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlAccountsLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addGap(334, 334, 334))
            .addGroup(pnlAccountsLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAccountsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(pnlAccountsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAddToQueue10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshQueue7)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pnlAccountsLayout.setVerticalGroup(
            pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAccountsLayout.createSequentialGroup()
                .addComponent(lblJeepneyTitle3)
                .addGap(18, 18, 18)
                .addGroup(pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlAccountsLayout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlAccountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnRefreshQueue7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddToQueue10, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        pnlContent.add(pnlAccounts, "card7");

        pnlTripHistory.setBackground(new java.awt.Color(255, 255, 255));

        lblJeepneyTitle2.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblJeepneyTitle2.setForeground(new java.awt.Color(0, 0, 0));
        lblJeepneyTitle2.setText("Trip History:");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(jTable3);

        jPanel14.setBackground(new java.awt.Color(204, 204, 204));
        jPanel14.setForeground(new java.awt.Color(204, 204, 204));

        jLabel22.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("Sort By:");

        cmbJeepneySelect2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnDispatch5.setBackground(new java.awt.Color(255, 153, 0));
        btnDispatch5.setText("Search");

        btnAddToQueue4.setBackground(new java.awt.Color(0, 153, 153));
        btnAddToQueue4.setText("Apply Sort");
        btnAddToQueue4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToQueue4ActionPerformed(evt);
            }
        });

        btnRefreshQueue6.setText("Refresh");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDispatch5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbJeepneySelect2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddToQueue4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshQueue6)
                .addGap(12, 12, 12))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJeepneySelect2)
                    .addComponent(btnDispatch5)
                    .addComponent(btnRefreshQueue6)
                    .addComponent(btnAddToQueue4)
                    .addComponent(jLabel22)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText("Status: Ready");

        javax.swing.GroupLayout pnlTripHistoryLayout = new javax.swing.GroupLayout(pnlTripHistory);
        pnlTripHistory.setLayout(pnlTripHistoryLayout);
        pnlTripHistoryLayout.setHorizontalGroup(
            pnlTripHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTripHistoryLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJeepneyTitle2)
                .addGap(411, 411, 411))
            .addGroup(pnlTripHistoryLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlTripHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTripHistoryLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel23))
                    .addGroup(pnlTripHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane5)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        pnlTripHistoryLayout.setVerticalGroup(
            pnlTripHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTripHistoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblJeepneyTitle2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel23)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pnlContent.add(pnlTripHistory, "card6");

        pnlDrivers.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setText("Contact No. (09XX-XXX-XXXX):");

        jPanel10.setBackground(new java.awt.Color(204, 204, 204));
        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel10.setForeground(new java.awt.Color(204, 204, 204));

        jLabel14.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Confirmation of Details:");

        btnAdd.setBackground(new java.awt.Color(0, 153, 153));
        btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnClear)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnClear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtContactNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContactNoActionPerformed(evt);
            }
        });

        jLabel21.setText("Full Name:");

        jLabel32.setText("License Number:");

        jPanel13.setBackground(new java.awt.Color(204, 204, 204));
        jPanel13.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel13.setForeground(new java.awt.Color(204, 204, 204));

        btnUpdate.setBackground(new java.awt.Color(255, 204, 0));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(204, 51, 0));
        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(204, 0, 0));
        jLabel33.setText("Management Controls:");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete))
                    .addComponent(jLabel33))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete))
                .addContainerGap())
        );

        jLabel13.setText("Status:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Inactive" }));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(txtDriverName, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel32)
                            .addComponent(txtLicenseNo, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(cmbStatus, 0, 155, Short.MAX_VALUE)))
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDriverName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLicenseNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel12)
                        .addGap(12, 12, 12)
                        .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        tblDrivers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Driver's ID", "Driver's Name", "License No.", "Contact No.", "Status"
            }
        ));
        tblDrivers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDriversMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblDrivers);

        jPanel12.setBackground(new java.awt.Color(204, 204, 204));
        jPanel12.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel12.setForeground(new java.awt.Color(204, 204, 204));

        cmbSortDrivers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sort by Name (A-Z)", "Sort by Name (Z-A)" }));

        btnSearch.setBackground(new java.awt.Color(213, 100, 33));
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmbSortDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbSortDrivers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Lists of Controls:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel15)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Driver Details:");

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("All Drivers:");

        lblJeepneyTitle1.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblJeepneyTitle1.setForeground(new java.awt.Color(0, 0, 0));
        lblJeepneyTitle1.setText("Driver Management:");

        javax.swing.GroupLayout pnlDriversLayout = new javax.swing.GroupLayout(pnlDrivers);
        pnlDrivers.setLayout(pnlDriversLayout);
        pnlDriversLayout.setHorizontalGroup(
            pnlDriversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDriversLayout.createSequentialGroup()
                .addGap(188, 188, 188)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(232, 232, 232))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDriversLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlDriversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDriversLayout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlDriversLayout.createSequentialGroup()
                        .addComponent(lblJeepneyTitle1)
                        .addGap(352, 352, 352))))
        );
        pnlDriversLayout.setVerticalGroup(
            pnlDriversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDriversLayout.createSequentialGroup()
                .addComponent(lblJeepneyTitle1)
                .addGap(26, 26, 26)
                .addGroup(pnlDriversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDriversLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        pnlContent.add(pnlDrivers, "card5");

        pnlJeepneys.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setText("Plate Number: ");

        jLabel5.setText("Driver");

        jLabel6.setText("Route ID");

        jLabel7.setText("Status");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AVAILABLE", "DISPATCHED", "MAINTENANCE" }));

        jPanel7.setBackground(new java.awt.Color(204, 204, 204));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel7.setForeground(new java.awt.Color(204, 204, 204));

        jLabel8.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Select Jeepney:");

        btnDispatch1.setBackground(new java.awt.Color(255, 204, 0));
        btnDispatch1.setText("Update");

        btnUndoDispatch1.setBackground(new java.awt.Color(204, 51, 0));
        btnUndoDispatch1.setText("Delete");

        btnAddToQueue1.setBackground(new java.awt.Color(0, 153, 153));
        btnAddToQueue1.setText("Add");

        btnRefreshQueue1.setText("Clear");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(btnAddToQueue1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDispatch1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUndoDispatch1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshQueue1)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnUndoDispatch1)
                        .addComponent(btnRefreshQueue1))
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDispatch1)
                        .addComponent(btnAddToQueue1)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jComboBox2, 0, 259, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addGap(4, 4, 4)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel6)
                .addGap(12, 12, 12)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Jeepneys"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));
        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel8.setForeground(new java.awt.Color(204, 204, 204));

        jLabel9.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Lists of Controls:");

        btnDispatch2.setBackground(new java.awt.Color(0, 153, 51));
        btnDispatch2.setText("Sort");

        btnUndoDispatch2.setBackground(new java.awt.Color(213, 100, 33));
        btnUndoDispatch2.setText("Search");

        btnRefreshQueue2.setText("Refresh");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(229, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDispatch2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUndoDispatch2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshQueue2)
                .addGap(40, 40, 40))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDispatch2)
                    .addComponent(btnUndoDispatch2)
                    .addComponent(btnRefreshQueue2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        lblJeepneyTitle.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblJeepneyTitle.setForeground(new java.awt.Color(0, 0, 0));
        lblJeepneyTitle.setText("Jeepney Management:");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("All Jeepneys:");

        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Jeepney Details:");

        javax.swing.GroupLayout pnlJeepneysLayout = new javax.swing.GroupLayout(pnlJeepneys);
        pnlJeepneys.setLayout(pnlJeepneysLayout);
        pnlJeepneysLayout.setHorizontalGroup(
            pnlJeepneysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlJeepneysLayout.createSequentialGroup()
                .addGap(150, 150, 150)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(221, 221, 221))
            .addGroup(pnlJeepneysLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlJeepneysLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblJeepneyTitle)
                .addGap(356, 356, 356))
        );
        pnlJeepneysLayout.setVerticalGroup(
            pnlJeepneysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlJeepneysLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblJeepneyTitle)
                .addGap(29, 29, 29)
                .addGroup(pnlJeepneysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlJeepneysLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlContent.add(pnlJeepneys, "card4");

        pnlHome.setBackground(new java.awt.Color(255, 255, 255));
        pnlHome.setForeground(new java.awt.Color(255, 255, 255));

        lblWelcome.setFont(new java.awt.Font("Yu Gothic UI Semibold", 0, 24)); // NOI18N
        lblWelcome.setForeground(new java.awt.Color(0, 0, 0));
        lblWelcome.setText("Welcome to JTO Admin Dashboard");
        lblWelcome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblInstruction.setFont(new java.awt.Font("Segoe UI Semibold", 1, 14)); // NOI18N
        lblInstruction.setForeground(new java.awt.Color(153, 153, 153));
        lblInstruction.setText("Select menus from the left to begin.");

        javax.swing.GroupLayout pnlStat1Layout = new javax.swing.GroupLayout(pnlStat1);
        pnlStat1.setLayout(pnlStat1Layout);
        pnlStat1Layout.setHorizontalGroup(
            pnlStat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );
        pnlStat1Layout.setVerticalGroup(
            pnlStat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlStat2Layout = new javax.swing.GroupLayout(pnlStat2);
        pnlStat2.setLayout(pnlStat2Layout);
        pnlStat2Layout.setHorizontalGroup(
            pnlStat2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );
        pnlStat2Layout.setVerticalGroup(
            pnlStat2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlStat3Layout = new javax.swing.GroupLayout(pnlStat3);
        pnlStat3.setLayout(pnlStat3Layout);
        pnlStat3Layout.setHorizontalGroup(
            pnlStat3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );
        pnlStat3Layout.setVerticalGroup(
            pnlStat3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlStat4Layout = new javax.swing.GroupLayout(pnlStat4);
        pnlStat4.setLayout(pnlStat4Layout);
        pnlStat4Layout.setHorizontalGroup(
            pnlStat4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 283, Short.MAX_VALUE)
        );
        pnlStat4Layout.setVerticalGroup(
            pnlStat4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 55, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout pnlHomeLayout = new javax.swing.GroupLayout(pnlHome);
        pnlHome.setLayout(pnlHomeLayout);
        pnlHomeLayout.setHorizontalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomeLayout.createSequentialGroup()
                .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addGap(293, 293, 293)
                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblWelcome, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHomeLayout.createSequentialGroup()
                                .addComponent(lblInstruction)
                                .addGap(86, 86, 86))))
                    .addGroup(pnlHomeLayout.createSequentialGroup()
                        .addGap(334, 334, 334)
                        .addGroup(pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlStat2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlStat1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlStat3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlStat4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(316, Short.MAX_VALUE))
        );
        pnlHomeLayout.setVerticalGroup(
            pnlHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHomeLayout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(lblWelcome, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInstruction)
                .addGap(66, 66, 66)
                .addComponent(pnlStat1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlStat2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlStat3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlStat4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(159, Short.MAX_VALUE))
        );

        pnlContent.add(pnlHome, "card2");

        pnlQueue.setBackground(new java.awt.Color(255, 255, 255));

        lblQueueTitle.setFont(new java.awt.Font("Noto Sans Lisu", 1, 24)); // NOI18N
        lblQueueTitle.setForeground(new java.awt.Color(0, 0, 0));
        lblQueueTitle.setText("JTO Queue Management:");

        tblQueue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Jeepneys in Queue"
            }
        ));
        jScrollPane1.setViewportView(tblQueue);

        tblDispatched.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Dispatched Jeepneys"
            }
        ));
        jScrollPane2.setViewportView(tblDispatched);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));
        jPanel4.setForeground(new java.awt.Color(204, 204, 204));

        jLabel2.setFont(new java.awt.Font("Rockwell", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Select Jeepney:");

        cmbJeepneySelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnDispatch.setBackground(new java.awt.Color(0, 153, 51));
        btnDispatch.setText("Dispatch");

        btnUndoDispatch.setBackground(new java.awt.Color(255, 172, 24));
        btnUndoDispatch.setText("Undo Dispatch");

        btnAddToQueue.setBackground(new java.awt.Color(0, 153, 153));
        btnAddToQueue.setText("Add to Queue");

        btnRefreshQueue.setText("Refresh");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbJeepneySelect, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                .addComponent(btnAddToQueue)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDispatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUndoDispatch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshQueue)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbJeepneySelect)
                    .addComponent(btnDispatch)
                    .addComponent(btnUndoDispatch)
                    .addComponent(btnRefreshQueue)
                    .addComponent(btnAddToQueue)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlQueueLayout = new javax.swing.GroupLayout(pnlQueue);
        pnlQueue.setLayout(pnlQueueLayout);
        pnlQueueLayout.setHorizontalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                            .addComponent(lblQueueTitle)
                            .addGap(338, 338, 338))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlQueueLayout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(49, 49, 49)))))
        );
        pnlQueueLayout.setVerticalGroup(
            pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQueueLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(lblQueueTitle)
                .addGap(46, 46, 46)
                .addGroup(pnlQueueLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
        );

        pnlContent.add(pnlQueue, "card3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlContent, javax.swing.GroupLayout.PREFERRED_SIZE, 991, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnDriversActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDriversActionPerformed
        showPanel("DRIVER");
    }//GEN-LAST:event_btnDriversActionPerformed

    private void btnAddToQueue4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue4ActionPerformed

    private void btnAddToQueue6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue6ActionPerformed

    private void btnAddToQueue5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue5ActionPerformed

    private void btnAddToQueue7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue7ActionPerformed

    private void btnAddToQueue8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue8ActionPerformed

    private void btnAddToQueue9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue9ActionPerformed

    private void btnAddToQueue10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue10ActionPerformed

    private void btnAddToQueue12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToQueue12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddToQueue12ActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        showPanel("HOME"); // Show the Home card
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnQueueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQueueActionPerformed
        showPanel("QUEUE");
    }//GEN-LAST:event_btnQueueActionPerformed

    private void btnJeepneysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJeepneysActionPerformed
        showPanel("JEEPNEY");
    }//GEN-LAST:event_btnJeepneysActionPerformed

    private void btnTripHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTripHistoryActionPerformed
        showPanel("TRIPHISTORY");
    }//GEN-LAST:event_btnTripHistoryActionPerformed

    private void btnAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAccountsActionPerformed
        showPanel("ACCOUNTS");
    }//GEN-LAST:event_btnAccountsActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        showPanel("REPORTS");
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        clearFields();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        addDriver();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        updateDriver();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteDriver();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        loadDrivers();   // Reload everything from database
        clearFields();   // Reset form
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void tblDriversMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDriversMouseClicked
        
        int row = tblDrivers.getSelectedRow(); // Get clicked row index

        if (row != -1) {
            // Read the row values from the table
            selectedDriverId = Integer.parseInt(tblDrivers.getValueAt(row, 0).toString());
            txtDriverName.setText(tblDrivers.getValueAt(row, 1).toString());
            txtLicenseNo.setText(tblDrivers.getValueAt(row, 2).toString());
            txtContactNo.setText(tblDrivers.getValueAt(row, 3).toString());
            cmbStatus.setSelectedItem(tblDrivers.getValueAt(row, 4).toString());
        }
    }//GEN-LAST:event_tblDriversMouseClicked

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        sortDrivers();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void txtContactNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContactNoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAccounts;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddToQueue;
    private javax.swing.JButton btnAddToQueue1;
    private javax.swing.JButton btnAddToQueue10;
    private javax.swing.JButton btnAddToQueue11;
    private javax.swing.JButton btnAddToQueue12;
    private javax.swing.JButton btnAddToQueue4;
    private javax.swing.JButton btnAddToQueue5;
    private javax.swing.JButton btnAddToQueue6;
    private javax.swing.JButton btnAddToQueue7;
    private javax.swing.JButton btnAddToQueue8;
    private javax.swing.JButton btnAddToQueue9;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDispatch;
    private javax.swing.JButton btnDispatch1;
    private javax.swing.JButton btnDispatch2;
    private javax.swing.JButton btnDispatch5;
    private javax.swing.JButton btnDrivers;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnJeepneys;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnQueue;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRefreshQueue;
    private javax.swing.JButton btnRefreshQueue1;
    private javax.swing.JButton btnRefreshQueue2;
    private javax.swing.JButton btnRefreshQueue6;
    private javax.swing.JButton btnRefreshQueue7;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnTripHistory;
    private javax.swing.JButton btnUndoDispatch;
    private javax.swing.JButton btnUndoDispatch1;
    private javax.swing.JButton btnUndoDispatch2;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbJeepneySelect;
    private javax.swing.JComboBox<String> cmbJeepneySelect2;
    private javax.swing.JComboBox<String> cmbJeepneySelect3;
    private javax.swing.JComboBox<String> cmbSortDrivers;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JLabel lblCurrentDateTime;
    private javax.swing.JLabel lblCurrentUser;
    private javax.swing.JLabel lblDashboardLogo;
    private javax.swing.JLabel lblDashboardSubtitle;
    private javax.swing.JLabel lblInstruction;
    private javax.swing.JLabel lblJeepneyTitle;
    private javax.swing.JLabel lblJeepneyTitle1;
    private javax.swing.JLabel lblJeepneyTitle2;
    private javax.swing.JLabel lblJeepneyTitle3;
    private javax.swing.JLabel lblJeepneyTitle4;
    private javax.swing.JLabel lblQueueTitle;
    private javax.swing.JLabel lblSystemTitle;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel pnlAccounts;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlDrivers;
    private javax.swing.JPanel pnlHome;
    private javax.swing.JPanel pnlJeepneys;
    private javax.swing.JPanel pnlQueue;
    private javax.swing.JPanel pnlReports;
    private javax.swing.JPanel pnlStat1;
    private javax.swing.JPanel pnlStat2;
    private javax.swing.JPanel pnlStat3;
    private javax.swing.JPanel pnlStat4;
    private javax.swing.JPanel pnlTripHistory;
    private javax.swing.JTable tblDispatched;
    private javax.swing.JTable tblDrivers;
    private javax.swing.JTable tblQueue;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JTextField txtDriverName;
    private javax.swing.JTextField txtLicenseNo;
    // End of variables declaration//GEN-END:variables
}
