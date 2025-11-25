package view;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;

public class FormAssignmentSopir extends JFrame {
    private JTable tableAssignment;
    private DefaultTableModel tableModel;
    private JTextField txtFeeSopir;
    private JTextArea txtKeterangan;
    private JComboBox<String> cmbBooking, cmbSopir, cmbStatusBayar;
    private JDateChooser dateBayar;
    private JButton btnTambah, btnUpdate, btnBayar, btnBatal, btnRefresh;
    private AssignmentSopirDAO assignmentDAO;
    private BookingDAO bookingDAO;
    private SopirDAO sopirDAO;
    private int selectedId = -1;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public FormAssignmentSopir() {
        assignmentDAO = new AssignmentSopirDAO();
        bookingDAO = new BookingDAO();
        sopirDAO = new SopirDAO();
        initComponents();
        loadComboBoxData();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Assignment Sopir");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(230, 126, 34));
        JLabel lblTitle = new JLabel("ASSIGNMENT SOPIR & PEMBAYARAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Assignment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - Booking
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Booking:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cmbBooking = new JComboBox<>();
        formPanel.add(cmbBooking, gbc);
        gbc.gridwidth = 1;
        
        // Row 1 - Sopir
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Sopir:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cmbSopir = new JComboBox<>();
        formPanel.add(cmbSopir, gbc);
        gbc.gridwidth = 1;
        
        // Row 2 - Fee Sopir
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Fee Sopir:"), gbc);
        gbc.gridx = 1;
        txtFeeSopir = new JTextField(15);
        formPanel.add(txtFeeSopir, gbc);
        
        // Row 2 - Status Bayar
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status Bayar:"), gbc);
        gbc.gridx = 3;
        cmbStatusBayar = new JComboBox<>(new String[]{"belum_bayar", "dibayar"});
        formPanel.add(cmbStatusBayar, gbc);
        
        // Row 3 - Tanggal Bayar
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tanggal Bayar:"), gbc);
        gbc.gridx = 1;
        dateBayar = new JDateChooser();
        dateBayar.setDateFormatString("dd/MM/yyyy");
        formPanel.add(dateBayar, gbc);
        
        // Row 4 - Keterangan
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtKeterangan = new JTextArea(3, 20);
        txtKeterangan.setLineWrap(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtKeterangan);
        formPanel.add(scrollKeterangan, gbc);
        
        // Row 5 - Buttons
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnTambah = createButton("Assign Sopir", new Color(46, 204, 113));
        btnUpdate = createButton("Update", new Color(52, 152, 219));
        btnBayar = createButton("Bayar", new Color(241, 196, 15));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        btnRefresh = createButton("Refresh", new Color(155, 89, 182));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBayar);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnRefresh);
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columns = {"ID", "Kode Booking", "Sopir", "Pelanggan", "Tujuan", "Tanggal", "Fee", "Status", "Tgl Bayar"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAssignment = new JTable(tableModel);
        tableAssignment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAssignment.getColumnModel().getColumn(0).setPreferredWidth(40);
        
        JScrollPane scrollTable = new JScrollPane(tableAssignment);
        
        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahAssignment());
        btnUpdate.addActionListener(e -> updateAssignment());
        btnBayar.addActionListener(e -> bayarSopir());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadComboBoxData();
            loadData();
        });
        
        tableAssignment.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectRow();
            }
        });
        
        setButtonState(false);
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void loadComboBoxData() {
        // Load Booking (hanya yang sudah dikonfirmasi dan belum ada assignment)
        cmbBooking.removeAllItems();
        List<Booking> bookingList = bookingDAO.getAllBooking();
        for (Booking b : bookingList) {
            if (b.getStatusBooking().equals("dikonfirmasi")) {
                // Check if already has assignment
                if (assignmentDAO.getAssignmentByBooking(b.getIdBooking()) == null) {
                    cmbBooking.addItem(b.getIdBooking() + " - " + b.getKodeBooking() + " (" + b.getNamaPelanggan() + ")");
                }
            }
        }
        
        // Load Sopir Aktif
        cmbSopir.removeAllItems();
        List<Sopir> sopirList = sopirDAO.getSopirAktif();
        for (Sopir s : sopirList) {
            cmbSopir.addItem(s.getIdSopir() + " - " + s.getNamaSopir() + " (" + s.getNoSim() + ")");
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<AssignmentSopir> assignments = assignmentDAO.getAllAssignments();
        
        for (AssignmentSopir assignment : assignments) {
            Object[] row = {
                assignment.getIdAssignment(),
                assignment.getKodeBooking(),
                assignment.getNamaSopir(),
                assignment.getNamaPelanggan(),
                assignment.getTujuan(),
                dateFormat.format(assignment.getTanggalMulai()) + " - " + dateFormat.format(assignment.getTanggalSelesai()),
                currencyFormat.format(assignment.getFeeSopir()),
                assignment.getStatusBayar().equals("dibayar") ? "Dibayar" : "Belum Bayar",
                assignment.getTanggalBayar() != null ? dateFormat.format(assignment.getTanggalBayar()) : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahAssignment() {
        if (!validateInput()) return;
        
        int idBooking = getSelectedIdFromCombo(cmbBooking);
        int idSopir = getSelectedIdFromCombo(cmbSopir);
        
        // Get tanggal booking
        java.sql.Date[] dates = assignmentDAO.getBookingDates(idBooking);
        if (dates == null) {
            JOptionPane.showMessageDialog(this, "Data booking tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cek availability sopir
        if (!assignmentDAO.isSopirAvailable(idSopir, dates[0], dates[1])) {
            JOptionPane.showMessageDialog(this, 
                "Sopir tidak tersedia!\n\nSopir ini sudah memiliki jadwal perjalanan yang bentrok.\n" +
                "Tanggal: " + new SimpleDateFormat("dd/MM/yyyy").format(dates[0]) + 
                " - " + new SimpleDateFormat("dd/MM/yyyy").format(dates[1]) +
                "\n\nSilakan pilih sopir lain atau tunggu jadwal sopir ini selesai.", 
                "Sopir Tidak Tersedia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        AssignmentSopir assignment = new AssignmentSopir();
        assignment.setIdBooking(idBooking);
        assignment.setIdSopir(idSopir);
        assignment.setFeeSopir(Double.parseDouble(txtFeeSopir.getText().trim()));
        assignment.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        assignment.setKeterangan(txtKeterangan.getText().trim());
        
        if (assignmentDAO.tambahAssignment(assignment)) {
            JOptionPane.showMessageDialog(this, "Sopir berhasil di-assign!");
            loadComboBoxData();
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal assign sopir!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateAssignment() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih assignment yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        AssignmentSopir assignment = new AssignmentSopir();
        assignment.setIdAssignment(selectedId);
        assignment.setIdSopir(getSelectedIdFromCombo(cmbSopir));
        assignment.setFeeSopir(Double.parseDouble(txtFeeSopir.getText().trim()));
        assignment.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        assignment.setTanggalBayar(dateBayar.getDate());
        assignment.setKeterangan(txtKeterangan.getText().trim());
        
        if (assignmentDAO.updateAssignment(assignment)) {
            JOptionPane.showMessageDialog(this, "Assignment berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update assignment!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void bayarSopir() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih assignment yang akan dibayar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Konfirmasi pembayaran fee sopir?", 
            "Konfirmasi Bayar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (assignmentDAO.updateStatusBayar(selectedId, "dibayar", new java.sql.Date(System.currentTimeMillis()))) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal melakukan pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectRow() {
        int row = tableAssignment.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            AssignmentSopir assignment = assignmentDAO.getAssignmentById(selectedId);
            
            if (assignment != null) {
                txtFeeSopir.setText(String.valueOf(assignment.getFeeSopir()));
                cmbStatusBayar.setSelectedItem(assignment.getStatusBayar());
                dateBayar.setDate(assignment.getTanggalBayar());
                txtKeterangan.setText(assignment.getKeterangan());
                
                cmbBooking.setEnabled(false);
                setButtonState(true);
            }
        }
    }
    
    private void clearForm() {
        txtFeeSopir.setText("");
        txtKeterangan.setText("");
        dateBayar.setDate(null);
        cmbBooking.setSelectedIndex(-1);
        cmbSopir.setSelectedIndex(-1);
        cmbStatusBayar.setSelectedIndex(0);
        selectedId = -1;
        tableAssignment.clearSelection();
        cmbBooking.setEnabled(true);
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnBayar.setEnabled(isUpdate);
    }
    
    private int getSelectedIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }
    
    private boolean validateInput() {
        if (cmbBooking.getSelectedItem() == null || cmbSopir.getSelectedItem() == null ||
            txtFeeSopir.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking, Sopir, dan Fee wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(txtFeeSopir.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Fee harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (txtFeeSopir.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fee wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}