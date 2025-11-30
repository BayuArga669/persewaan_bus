package view;

import dao.*;
import model.*;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.toedter.calendar.JDateChooser;

public class FormBiayaOperasional extends JFrame {
    private JTable tableBiaya;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtKeterangan, txtJumlah;
    private JComboBox<String> cmbBooking, cmbJenisBiaya, cmbStatusBayar, cmbFilterBooking;
    private JDateChooser dateTanggalBayar;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnBayar, btnRefresh;
    private JLabel lblTotalBiaya, lblSudahBayar, lblBelumBayar;
    
    private BiayaOperasionalDAO biayaDAO;
    private BookingDAO bookingDAO;
    private Map<String, Integer> bookingMap;
    private BiayaOperasional selectedBiaya;
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color WARNING = new Color(241, 196, 15);
    
    public FormBiayaOperasional() {
        biayaDAO = new BiayaOperasionalDAO();
        bookingDAO = new BookingDAO();
        bookingMap = new HashMap<>();
        
        initComponents();
        loadBookingComboBox();
        loadTableData();
        updateStatistics();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Biaya Operasional Bus");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel lblTitle = new JLabel("💰 KELOLA BIAYA OPERASIONAL BUS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Left Panel - Form Input
        JPanel leftPanel = createFormPanel();
        
        // Right Panel - Table & Statistics
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(createStatisticsPanel(), BorderLayout.NORTH);
        rightPanel.add(createTablePanel(), BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.3);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            "Form Input Biaya Operasional",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 14),
            PRIMARY
        ));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // Booking
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Booking:"), gbc);
        gbc.gridy = ++row;
        cmbBooking = new JComboBox<>();
        cmbBooking.setPreferredSize(new Dimension(0, 35));
        formPanel.add(cmbBooking, gbc);
        
        // Jenis Biaya
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Jenis Biaya:"), gbc);
        gbc.gridy = ++row;
        cmbJenisBiaya = new JComboBox<>(new String[]{
            "gaji_sopir", "bbm", "tol", "parkir", "makan_sopir", "maintenance", "lainnya"
        });
        cmbJenisBiaya.setPreferredSize(new Dimension(0, 35));
        formPanel.add(cmbJenisBiaya, gbc);
        
        // Keterangan
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridy = ++row;
        txtKeterangan = new JTextField();
        txtKeterangan.setPreferredSize(new Dimension(0, 35));
        formPanel.add(txtKeterangan, gbc);
        
        // Jumlah
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Jumlah (Rp):"), gbc);
        gbc.gridy = ++row;
        txtJumlah = new JTextField();
        txtJumlah.setPreferredSize(new Dimension(0, 35));
        formPanel.add(txtJumlah, gbc);
        
        // Status Bayar
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Status Bayar:"), gbc);
        gbc.gridy = ++row;
        cmbStatusBayar = new JComboBox<>(new String[]{"belum_bayar", "sudah_bayar"});
        cmbStatusBayar.setPreferredSize(new Dimension(0, 35));
        cmbStatusBayar.addActionListener(e -> toggleTanggalBayar());
        formPanel.add(cmbStatusBayar, gbc);
        
        // Tanggal Bayar
        gbc.gridy = ++row;
        formPanel.add(new JLabel("Tanggal Bayar:"), gbc);
        gbc.gridy = ++row;
        dateTanggalBayar = new JDateChooser();
        dateTanggalBayar.setDateFormatString("dd/MM/yyyy");
        dateTanggalBayar.setPreferredSize(new Dimension(0, 35));
        dateTanggalBayar.setEnabled(false);
        formPanel.add(dateTanggalBayar, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        btnTambah = createStyledButton("Tambah", SUCCESS);
        btnUpdate = createStyledButton("Update", WARNING);
        btnHapus = createStyledButton("Hapus", DANGER);
        btnBatal = createStyledButton("Batal", new Color(149, 165, 166));
        
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahBiaya());
        btnUpdate.addActionListener(e -> updateBiaya());
        btnHapus.addActionListener(e -> hapusBiaya());
        btnBatal.addActionListener(e -> clearForm());
        
        return panel;
    }
    
    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        lblTotalBiaya = new JLabel("Rp 0", SwingConstants.CENTER);
        lblSudahBayar = new JLabel("Rp 0", SwingConstants.CENTER);
        lblBelumBayar = new JLabel("Rp 0", SwingConstants.CENTER);
        
        panel.add(createStatCard("Total Biaya Operasional", lblTotalBiaya, PRIMARY));
        panel.add(createStatCard("Sudah Dibayar", lblSudahBayar, SUCCESS));
        panel.add(createStatCard("Belum Dibayar", lblBelumBayar, DANGER));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(color.darker());
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter Booking:"));
        
        cmbFilterBooking = new JComboBox<>();
        cmbFilterBooking.setPreferredSize(new Dimension(200, 30));
        cmbFilterBooking.addActionListener(e -> filterByBooking());
        filterPanel.add(cmbFilterBooking);
        
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        filterPanel.add(new JLabel("Cari:"));
        filterPanel.add(txtSearch);
        
        btnRefresh = createStyledButton("Refresh", PRIMARY);
        btnBayar = createStyledButton("Bayar", SUCCESS);
        btnBayar.setEnabled(false);
        
        filterPanel.add(btnRefresh);
        filterPanel.add(btnBayar);
        
        // Table
        String[] columns = {"ID", "Kode Booking", "Tanggal", "Jenis Biaya", "Keterangan", 
                          "Jumlah", "Status", "Tgl Bayar", "Input By"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableBiaya = new JTable(tableModel);
        tableBiaya.setRowHeight(30);
        tableBiaya.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableBiaya.getSelectedRow() != -1) {
                loadSelectedBiaya();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableBiaya);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Event Listeners
        btnRefresh.addActionListener(e -> {
            loadTableData();
            updateStatistics();
        });
        
        btnBayar.addActionListener(e -> bayarBiaya());
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchBiaya();
            }
        });
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 35));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }
    
    private void loadBookingComboBox() {
        cmbBooking.removeAllItems();
        cmbFilterBooking.removeAllItems();
        cmbFilterBooking.addItem("Semua Booking");
        bookingMap.clear();
        
        List<Booking> bookings = bookingDAO.getAllBooking();
        for (Booking b : bookings) {
            String display = b.getKodeBooking() + " - " + b.getNamaPelanggan() + 
                           " (" + dateFormat.format(b.getTanggalMulai()) + ")";
            cmbBooking.addItem(display);
            cmbFilterBooking.addItem(display);
            bookingMap.put(display, b.getIdBooking());
        }
    }
    
    private void loadTableData() {
        tableModel.setRowCount(0);
        List<BiayaOperasional> list = biayaDAO.getAllBiaya();
        
        for (BiayaOperasional b : list) {
            Object[] row = {
                b.getIdBiaya(),
                b.getKodeBooking(),
                dateFormat.format(b.getTanggalBiaya()),
                b.getJenisBiayaLabel(),
                b.getKeterangan(),
                currencyFormat.format(b.getJumlah()),
                b.getStatusBayar().equals("sudah_bayar") ? "Sudah Bayar" : "Belum Bayar",
                b.getTanggalBayar() != null ? dateFormat.format(b.getTanggalBayar()) : "-",
                b.getCreatedByName()
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterByBooking() {
        String selected = (String) cmbFilterBooking.getSelectedItem();
        tableModel.setRowCount(0);
        
        if (selected == null || selected.equals("Semua Booking")) {
            loadTableData();
            return;
        }
        
        Integer idBooking = bookingMap.get(selected);
        if (idBooking != null) {
            List<BiayaOperasional> list = biayaDAO.getBiayaByBooking(idBooking);
            
            for (BiayaOperasional b : list) {
                Object[] row = {
                    b.getIdBiaya(),
                    b.getKodeBooking(),
                    dateFormat.format(b.getTanggalBiaya()),
                    b.getJenisBiayaLabel(),
                    b.getKeterangan(),
                    currencyFormat.format(b.getJumlah()),
                    b.getStatusBayar().equals("sudah_bayar") ? "Sudah Bayar" : "Belum Bayar",
                    b.getTanggalBayar() != null ? dateFormat.format(b.getTanggalBayar()) : "-",
                    b.getCreatedByName()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void searchBiaya() {
        String keyword = txtSearch.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<BiayaOperasional> list = biayaDAO.getAllBiaya();
        for (BiayaOperasional b : list) {
            if (b.getKodeBooking().toLowerCase().contains(keyword) ||
                b.getJenisBiayaLabel().toLowerCase().contains(keyword) ||
                b.getKeterangan().toLowerCase().contains(keyword)) {
                
                Object[] row = {
                    b.getIdBiaya(),
                    b.getKodeBooking(),
                    dateFormat.format(b.getTanggalBiaya()),
                    b.getJenisBiayaLabel(),
                    b.getKeterangan(),
                    currencyFormat.format(b.getJumlah()),
                    b.getStatusBayar().equals("sudah_bayar") ? "Sudah Bayar" : "Belum Bayar",
                    b.getTanggalBayar() != null ? dateFormat.format(b.getTanggalBayar()) : "-",
                    b.getCreatedByName()
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void updateStatistics() {
        double totalBiaya = 0;
        double sudahBayar = 0;
        double belumBayar = 0;
        
        List<BiayaOperasional> list = biayaDAO.getAllBiaya();
        for (BiayaOperasional b : list) {
            totalBiaya += b.getJumlah();
            if (b.getStatusBayar().equals("sudah_bayar")) {
                sudahBayar += b.getJumlah();
            } else {
                belumBayar += b.getJumlah();
            }
        }
        
        lblTotalBiaya.setText(currencyFormat.format(totalBiaya));
        lblSudahBayar.setText(currencyFormat.format(sudahBayar));
        lblBelumBayar.setText(currencyFormat.format(belumBayar));
    }
    
    private void toggleTanggalBayar() {
        boolean sudahBayar = cmbStatusBayar.getSelectedItem().equals("sudah_bayar");
        dateTanggalBayar.setEnabled(sudahBayar);
        if (sudahBayar && dateTanggalBayar.getDate() == null) {
            dateTanggalBayar.setDate(new Date());
        }
    }
    
    private void tambahBiaya() {
        if (!validateInput()) return;
        
        String selected = (String) cmbBooking.getSelectedItem();
        Integer idBooking = bookingMap.get(selected);
        
        BiayaOperasional biaya = new BiayaOperasional();
        biaya.setIdBooking(idBooking);
        biaya.setJenisBiaya((String) cmbJenisBiaya.getSelectedItem());
        biaya.setKeterangan(txtKeterangan.getText());
        biaya.setJumlah(Double.parseDouble(txtJumlah.getText()));
        biaya.setStatusBayar((String) cmbStatusBayar.getSelectedItem());
        biaya.setTanggalBayar(dateTanggalBayar.getDate());
        biaya.setCreatedBy(SessionManager.getCurrentUserId());
        
        if (biayaDAO.tambahBiaya(biaya)) {
            JOptionPane.showMessageDialog(this, "Biaya operasional berhasil ditambahkan!");
            loadTableData();
            updateStatistics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan biaya!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBiaya() {
        if (selectedBiaya == null || !validateInput()) return;
        
        selectedBiaya.setJenisBiaya((String) cmbJenisBiaya.getSelectedItem());
        selectedBiaya.setKeterangan(txtKeterangan.getText());
        selectedBiaya.setJumlah(Double.parseDouble(txtJumlah.getText()));
        selectedBiaya.setStatusBayar((String) cmbStatusBayar.getSelectedItem());
        selectedBiaya.setTanggalBayar(dateTanggalBayar.getDate());
        
        if (biayaDAO.updateBiaya(selectedBiaya)) {
            JOptionPane.showMessageDialog(this, "Biaya operasional berhasil diupdate!");
            loadTableData();
            updateStatistics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate biaya!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusBiaya() {
        if (selectedBiaya == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin ingin menghapus biaya ini?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (biayaDAO.deleteBiaya(selectedBiaya.getIdBiaya())) {
                JOptionPane.showMessageDialog(this, "Biaya berhasil dihapus!");
                loadTableData();
                updateStatistics();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus biaya!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void bayarBiaya() {
        if (selectedBiaya == null) return;
        
        if (selectedBiaya.getStatusBayar().equals("sudah_bayar")) {
            JOptionPane.showMessageDialog(this, "Biaya ini sudah dibayar!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Konfirmasi pembayaran biaya ini?",
            "Konfirmasi Bayar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (biayaDAO.updateStatusBayar(selectedBiaya.getIdBiaya(), "sudah_bayar", new Date())) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dicatat!");
                loadTableData();
                updateStatistics();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mencatat pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadSelectedBiaya() {
        int row = tableBiaya.getSelectedRow();
        if (row == -1) return;
        
        int idBiaya = (int) tableModel.getValueAt(row, 0);
        selectedBiaya = biayaDAO.getBiayaById(idBiaya);
        
        if (selectedBiaya != null) {
            String bookingDisplay = selectedBiaya.getKodeBooking() + " - " + 
                                  selectedBiaya.getNamaPelanggan();
            
            for (int i = 0; i < cmbBooking.getItemCount(); i++) {
                if (cmbBooking.getItemAt(i).startsWith(selectedBiaya.getKodeBooking())) {
                    cmbBooking.setSelectedIndex(i);
                    break;
                }
            }
            
            cmbJenisBiaya.setSelectedItem(selectedBiaya.getJenisBiaya());
            txtKeterangan.setText(selectedBiaya.getKeterangan());
            txtJumlah.setText(String.valueOf(selectedBiaya.getJumlah()));
            cmbStatusBayar.setSelectedItem(selectedBiaya.getStatusBayar());
            dateTanggalBayar.setDate(selectedBiaya.getTanggalBayar());
            
            btnTambah.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnHapus.setEnabled(true);
            btnBayar.setEnabled(selectedBiaya.getStatusBayar().equals("belum_bayar"));
        }
    }
    
    private void clearForm() {
        cmbBooking.setSelectedIndex(0);
        cmbJenisBiaya.setSelectedIndex(0);
        txtKeterangan.setText("");
        txtJumlah.setText("");
        cmbStatusBayar.setSelectedIndex(0);
        dateTanggalBayar.setDate(null);
        dateTanggalBayar.setEnabled(false);
        
        selectedBiaya = null;
        tableBiaya.clearSelection();
        
        btnTambah.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnHapus.setEnabled(false);
        btnBayar.setEnabled(false);
    }
    
    private boolean validateInput() {
        if (cmbBooking.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Pilih booking terlebih dahulu!");
            return false;
        }
        
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah biaya harus diisi!");
            txtJumlah.requestFocus();
            return false;
        }
        
        try {
            double jumlah = Double.parseDouble(txtJumlah.getText());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
            txtJumlah.requestFocus();
            return false;
        }
        
        if (cmbStatusBayar.getSelectedItem().equals("sudah_bayar") && 
            dateTanggalBayar.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal bayar harus diisi!");
            return false;
        }
        
        return true;
    }
}