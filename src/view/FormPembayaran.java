package view;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;

public class FormPembayaran extends JFrame {
    private JTable tablePembayaran;
    private DefaultTableModel tableModel;
    private JTextField txtJumlahBayar, txtSearch;
    private JTextArea txtKeterangan;
    private JComboBox<String> cmbBooking, cmbMetodeBayar, cmbStatusBayar, cmbFilterStatus;
    private JDateChooser dateBayar;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnRefresh, btnCari, btnHitungDP;
    private JLabel lblTotalHarga, lblTotalDibayar, lblSisaPembayaran;
    private PembayaranDAO pembayaranDAO;
    private BookingDAO bookingDAO;
    private int selectedId = -1;
    private int selectedBookingId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
    public FormPembayaran() {
        pembayaranDAO = new PembayaranDAO();
        bookingDAO = new BookingDAO();
        initComponents();
        loadComboBoxData();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Pembayaran");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        JLabel lblTitle = new JLabel("MANAJEMEN PEMBAYARAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Search Panel
        JPanel searchPanel = createSearchPanel();
        
        // Table
        String[] columns = {"ID", "Kode Booking", "Pelanggan", "Tgl Bayar", "Jumlah", "Metode", "Status", "Total Harga", "Sisa"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePembayaran = new JTable(tableModel);
        tablePembayaran.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePembayaran.getColumnModel().getColumn(0).setPreferredWidth(50);
        
        JScrollPane scrollTable = new JScrollPane(tablePembayaran);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahPembayaran());
        btnUpdate.addActionListener(e -> updatePembayaran());
        btnHapus.addActionListener(e -> hapusPembayaran());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadData();
            loadComboBoxData(); // ✅ Refresh dropdown juga
        });
        btnCari.addActionListener(e -> cariPembayaran());
        btnHitungDP.addActionListener(e -> hitungDP());
        
        cmbBooking.addActionListener(e -> loadBookingInfo());
        
        // ✅ PERUBAHAN: Menggunakan ListSelectionListener untuk pemilihan baris
        tablePembayaran.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectRow();
            }
        });
        
        setButtonState(false);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Form Pembayaran"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - Booking
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Booking:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cmbBooking = new JComboBox<>();
        panel.add(cmbBooking, gbc);
        gbc.gridwidth = 1;
        
        // Row 1 - Info Pembayaran
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Total Harga:"), gbc);
        gbc.gridx = 1;
        lblTotalHarga = new JLabel("Rp 0");
        lblTotalHarga.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblTotalHarga, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Total Dibayar:"), gbc);
        gbc.gridx = 3;
        lblTotalDibayar = new JLabel("Rp 0");
        lblTotalDibayar.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalDibayar.setForeground(new Color(46, 204, 113));
        panel.add(lblTotalDibayar, gbc);
        
        // Row 2 - Sisa Pembayaran
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Sisa Pembayaran:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        lblSisaPembayaran = new JLabel("Rp 0");
        lblSisaPembayaran.setFont(new Font("Arial", Font.BOLD, 16));
        lblSisaPembayaran.setForeground(new Color(231, 76, 60));
        panel.add(lblSisaPembayaran, gbc);
        gbc.gridwidth = 1;
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        panel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Row 4 - Tanggal Bayar
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tanggal Bayar:"), gbc);
        gbc.gridx = 1;
        dateBayar = new JDateChooser();
        dateBayar.setDateFormatString("dd/MM/yyyy");
        dateBayar.setDate(new Date());
        panel.add(dateBayar, gbc);
        
        // Row 4 - Status Bayar
        gbc.gridx = 2;
        panel.add(new JLabel("Status Bayar:"), gbc);
        gbc.gridx = 3;
        cmbStatusBayar = new JComboBox<>(new String[]{"dp", "lunas"});
        panel.add(cmbStatusBayar, gbc);
        
        // Row 5 - Jumlah Bayar
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Jumlah Bayar:"), gbc);
        gbc.gridx = 1;
        // ✅ PERUBAHAN: Menggunakan createNumericField() agar hanya menerima angka
        txtJumlahBayar = createNumericField();
        panel.add(txtJumlahBayar, gbc);
        
        gbc.gridx = 2;
        btnHitungDP = createButton("30% (DP)", new Color(230, 126, 34));
        btnHitungDP.setPreferredSize(new Dimension(120, 25));
        panel.add(btnHitungDP, gbc);
        
        // Row 5 - Metode Bayar
        gbc.gridx = 3;
        cmbMetodeBayar = new JComboBox<>(new String[]{"cash", "transfer", "ewallet"});
        panel.add(cmbMetodeBayar, gbc);
        
        // Row 6 - Bukti Transfer
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Bukti Transfer:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        gbc.gridwidth = 1;
        
        // Row7 - Keterangan
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtKeterangan = new JTextArea(3, 20);
        txtKeterangan.setLineWrap(true);
        JScrollPane scrollKeterangan = new JScrollPane(txtKeterangan);
        panel.add(scrollKeterangan, gbc);
        
        // Row 8 - Buttons
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnTambah = createButton("Tambah Pembayaran", new Color(46, 204, 113));
        btnUpdate = createButton("Update", new Color(52, 152, 219));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pencarian & Filter"));
        
        panel.add(new JLabel("Cari:"));
        txtSearch = new JTextField(20);
        panel.add(txtSearch);
        
        btnCari = createButton("Cari", new Color(52, 152, 219));
        panel.add(btnCari);
        
        panel.add(new JLabel("  Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "belum_bayar", "dp", "lunas"});
        panel.add(cmbFilterStatus);
        
        btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        panel.add(btnRefresh);
        
        cmbFilterStatus.addActionListener(e -> cariPembayaran());
        
        return panel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    // ✅ METODE BARU: Membuat field yang hanya menerima input numerik
    private JTextField createNumericField() {
        JTextField field = new JTextField() {
            @Override
            protected void processKeyEvent(KeyEvent e) {
                // Mengizinkan angka, backspace, delete, tab, enter, arrow keys
                if (Character.isDigit(e.getKeyChar()) || 
                    e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
                    e.getKeyCode() == KeyEvent.VK_DELETE ||
                    e.getKeyCode() == KeyEvent.VK_TAB ||
                    e.getKeyCode() == KeyEvent.VK_ENTER ||
                    e.getKeyCode() == KeyEvent.VK_LEFT ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT ||
                    e.getKeyCode() == KeyEvent.VK_HOME ||
                    e.getKeyCode() == KeyEvent.VK_END ||
                    e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_C || 
                                         e.getKeyCode() == KeyEvent.VK_V || 
                                         e.getKeyCode() == KeyEvent.VK_X)) {
                    super.processKeyEvent(e);
                } else {
                    // Mencegah input karakter non-angka
                    e.consume();
                    Toolkit.getDefaultToolkit().beep(); // Opsional: Bunyi peringatan
                }
            }
        };
        return field;
    }
    
    // ✅ FULL FIX: Hanya tampilkan booking yang belum lunas
    private void loadComboBoxData() {
        cmbBooking.removeAllItems();
        List<Booking> bookingList = bookingDAO.getAllBooking();
        
        for (Booking b : bookingList) {
            // Hanya tampilkan booking yang dikonfirmasi/selesai DAN belum lunas
            if ("dikonfirmasi".equals(b.getStatusBooking()) || "selesai".equals(b.getStatusBooking())) {
                double totalDibayar = pembayaranDAO.getTotalPembayaranByBookingId(b.getIdBooking());
                if (totalDibayar < b.getTotalHarga()) { // ← belum lunas
                    cmbBooking.addItem(b.getIdBooking() + " - " + b.getKodeBooking() + 
                        " - " + b.getNamaPelanggan() + " (" + currencyFormat.format(b.getTotalHarga()) + ")");
                }
            }
        }
    }
    
    private void loadBookingInfo() {
        if (cmbBooking.getSelectedItem() != null) {
            int idBooking = getSelectedIdFromCombo(cmbBooking);
            Booking booking = bookingDAO.getBookingById(idBooking);
            
            if (booking != null) {
                lblTotalHarga.setText(currencyFormat.format(booking.getTotalHarga()));
                
                // Hitung total yang sudah dibayar
                double totalDibayar = pembayaranDAO.getTotalPembayaranByBookingId(idBooking);
                lblTotalDibayar.setText(currencyFormat.format(totalDibayar));
                
                // Hitung sisa
                double sisa = booking.getTotalHarga() - totalDibayar;
                lblSisaPembayaran.setText(currencyFormat.format(sisa));
                
                // Update warna sisa
                if (sisa <= 0) {
                    lblSisaPembayaran.setForeground(new Color(46, 204, 113));
                    lblSisaPembayaran.setText("LUNAS");
                } else {
                    lblSisaPembayaran.setForeground(new Color(231, 76, 60));
                }
            }
        }
    }
    
    private void hitungDP() {
        if (cmbBooking.getSelectedItem() != null) {
            int idBooking = getSelectedIdFromCombo(cmbBooking);
            Booking booking = bookingDAO.getBookingById(idBooking);
            
            if (booking != null) {
                double dp = booking.getTotalHarga() * 0.3; // 30% DP
                txtJumlahBayar.setText(String.valueOf(dp));
                cmbStatusBayar.setSelectedItem("dp");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih booking terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Pembayaran> pembayaranList = pembayaranDAO.getAllPembayaran();
        
        for (Pembayaran p : pembayaranList) {
            Object[] row = {
                p.getIdPembayaran(),
                p.getKodeBooking(),
                p.getNamaPelanggan(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(p.getTanggalBayar()),
                currencyFormat.format(p.getJumlahBayar()),
                p.getMetodeBayar().toUpperCase(),
                p.getStatusBayar().toUpperCase(),
                currencyFormat.format(p.getTotalHarga()),
                currencyFormat.format(p.getSisaPembayaran())
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahPembayaran() {
        if (!validateInput()) return;
        
        int idBooking = getSelectedIdFromCombo(cmbBooking);
        Booking booking = bookingDAO.getBookingById(idBooking);
        
        // Cek apakah sudah lunas
        double totalDibayar = pembayaranDAO.getTotalPembayaranByBookingId(idBooking);
        double jumlahBayar = Double.parseDouble(txtJumlahBayar.getText().trim());
        
        if (totalDibayar + jumlahBayar > booking.getTotalHarga()) {
            JOptionPane.showMessageDialog(this, 
                "Jumlah pembayaran melebihi sisa tagihan!\nSisa: " + currencyFormat.format(booking.getTotalHarga() - totalDibayar), 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdBooking(idBooking);
        pembayaran.setTanggalBayar(dateBayar.getDate());
        pembayaran.setJumlahBayar(jumlahBayar);
        pembayaran.setMetodeBayar(cmbMetodeBayar.getSelectedItem().toString());
        pembayaran.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        pembayaran.setKeterangan(txtKeterangan.getText().trim());
        
        if (pembayaranDAO.tambahPembayaran(pembayaran)) {
            // Update status booking jika lunas
            double totalSetelah = totalDibayar + jumlahBayar;
            if (totalSetelah >= booking.getTotalHarga()) {
                bookingDAO.updateStatusBooking(idBooking, "dikonfirmasi");
            }
            
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            // ✅ Refresh dropdown agar booking lunas hilang
            loadComboBoxData(); 
            loadBookingInfo();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePembayaran() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pembayaran yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        Pembayaran pembayaran = new Pembayaran();
        pembayaran.setIdPembayaran(selectedId);
        pembayaran.setJumlahBayar(Double.parseDouble(txtJumlahBayar.getText().trim()));
        pembayaran.setMetodeBayar(cmbMetodeBayar.getSelectedItem().toString());
        pembayaran.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        pembayaran.setKeterangan(txtKeterangan.getText().trim());
        
        if (pembayaranDAO.updatePembayaran(pembayaran)) {
            JOptionPane.showMessageDialog(this, "Pembayaran berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusPembayaran() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pembayaran yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus pembayaran ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (pembayaranDAO.hapusPembayaran(selectedId)) {
                JOptionPane.showMessageDialog(this, "Pembayaran berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                // ✅ Refresh dropdown juga
                loadComboBoxData();
                loadBookingInfo();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cariPembayaran() {
        String keyword = txtSearch.getText().trim();
        String status = cmbFilterStatus.getSelectedItem().toString();
        
        tableModel.setRowCount(0);
        List<Pembayaran> pembayaranList = pembayaranDAO.searchPembayaran(keyword, status);
        
        for (Pembayaran p : pembayaranList) {
            Object[] row = {
                p.getIdPembayaran(),
                p.getKodeBooking(),
                p.getNamaPelanggan(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(p.getTanggalBayar()),
                currencyFormat.format(p.getJumlahBayar()),
                p.getMetodeBayar().toUpperCase(),
                p.getStatusBayar().toUpperCase(),
                currencyFormat.format(p.getTotalHarga()),
                currencyFormat.format(p.getSisaPembayaran())
            };
            tableModel.addRow(row);
        }
    }
    
    private void selectRow() {
        int row = tablePembayaran.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Pembayaran pembayaran = pembayaranDAO.getPembayaranById(selectedId);
            
            if (pembayaran != null) {
                // Set combo booking
                selectedBookingId = pembayaran.getIdBooking();
                for (int i = 0; i < cmbBooking.getItemCount(); i++) {
                    String item = cmbBooking.getItemAt(i);
                    if (item.startsWith(pembayaran.getIdBooking() + " -")) {
                        cmbBooking.setSelectedIndex(i);
                        break;
                    }
                }
                
                dateBayar.setDate(pembayaran.getTanggalBayar());
                txtJumlahBayar.setText(String.valueOf(pembayaran.getJumlahBayar()));
                cmbMetodeBayar.setSelectedItem(pembayaran.getMetodeBayar());
                cmbStatusBayar.setSelectedItem(pembayaran.getStatusBayar());
                txtKeterangan.setText(pembayaran.getKeterangan());
                
                loadBookingInfo();
                setButtonState(true);
            }
        }
    }
    
    private void clearForm() {
        txtJumlahBayar.setText("");
        txtKeterangan.setText("");
        dateBayar.setDate(new Date());
        cmbBooking.setSelectedIndex(-1);
        cmbMetodeBayar.setSelectedIndex(0);
        cmbStatusBayar.setSelectedIndex(0);
        lblTotalHarga.setText("Rp 0");
        lblTotalDibayar.setText("Rp 0");
        lblSisaPembayaran.setText("Rp 0");
        selectedId = -1;
        selectedBookingId = -1;
        tablePembayaran.clearSelection();
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnHapus.setEnabled(isUpdate);
        cmbBooking.setEnabled(!isUpdate);
    }
    
    private int getSelectedIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }
    
    private boolean validateInput() {
        if (cmbBooking.getSelectedItem() == null || txtJumlahBayar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Booking dan jumlah bayar wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double jumlah = Double.parseDouble(txtJumlahBayar.getText().trim());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah bayar harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (txtJumlahBayar.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah bayar wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}