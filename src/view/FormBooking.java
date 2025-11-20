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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import com.toedter.calendar.JDateChooser;

public class FormBooking extends JFrame {
    private JTable tableBooking;
    private DefaultTableModel tableModel;
    private JTextField txtKodeBooking, txtTujuan, txtJumlahPenumpang, txtLamaSewa, txtTotalHarga;
    private JTextArea txtCatatan;
    private JComboBox<String> cmbPelanggan, cmbBus, cmbStatus;
    private JDateChooser dateStart, dateEnd;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnRefresh, btnHitung;
    private BookingDAO bookingDAO;
    private PelangganDAO pelangganDAO;
    private BusDAO busDAO;
    private int selectedId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
    public FormBooking() {
        bookingDAO = new BookingDAO();
        pelangganDAO = new PelangganDAO();
        busDAO = new BusDAO();
        initComponents();
        loadComboBoxData();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Booking & Transaksi");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(46, 204, 113));
        JLabel lblTitle = new JLabel("MANAJEMEN BOOKING & TRANSAKSI");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel - Using Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Data Booking
        JPanel bookingPanel = createBookingPanel();
        tabbedPane.addTab("Data Booking", bookingPanel);
        
        // Table
        String[] columns = {"ID", "Kode", "Pelanggan", "Bus", "Tgl Mulai", "Tgl Selesai", "Tujuan", "Lama", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBooking = new JTable(tableModel);
        tableBooking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBooking.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableBooking.getColumnModel().getColumn(7).setPreferredWidth(60);
        
        JScrollPane scrollTable = new JScrollPane(tableBooking);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tabbedPane, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahBooking());
        btnUpdate.addActionListener(e -> updateBooking());
        btnHapus.addActionListener(e -> hapusBooking());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
        btnHitung.addActionListener(e -> hitungTotal());
        
        dateStart.addPropertyChangeListener("date", e -> hitungLamaSewa());
        dateEnd.addPropertyChangeListener("date", e -> hitungLamaSewa());
        
        tableBooking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow();
            }
        });
        
        setButtonState(false);
    }
    
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - Kode Booking
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Kode Booking:"), gbc);
        gbc.gridx = 1;
        txtKodeBooking = new JTextField(20);
        txtKodeBooking.setEditable(false);
        txtKodeBooking.setBackground(Color.LIGHT_GRAY);
        panel.add(txtKodeBooking, gbc);
        
        // Row 0 - Status
        gbc.gridx = 2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        cmbStatus = new JComboBox<>(new String[]{"pending", "dikonfirmasi", "selesai", "dibatalkan"});
        panel.add(cmbStatus, gbc);
        
        // Row 1 - Pelanggan
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Pelanggan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cmbPelanggan = new JComboBox<>();
        panel.add(cmbPelanggan, gbc);
        gbc.gridwidth = 1;
        
        // Row 2 - Bus
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Bus:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        cmbBus = new JComboBox<>();
        panel.add(cmbBus, gbc);
        gbc.gridwidth = 1;
        
        // Row 3 - Tanggal Mulai
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Tanggal Mulai:"), gbc);
        gbc.gridx = 1;
        dateStart = new JDateChooser();
        dateStart.setDateFormatString("dd/MM/yyyy");
        dateStart.setMinSelectableDate(new Date());
        panel.add(dateStart, gbc);
        
        // Row 3 - Tanggal Selesai
        gbc.gridx = 2;
        panel.add(new JLabel("Tanggal Selesai:"), gbc);
        gbc.gridx = 3;
        dateEnd = new JDateChooser();
        dateEnd.setDateFormatString("dd/MM/yyyy");
        dateEnd.setMinSelectableDate(new Date());
        panel.add(dateEnd, gbc);
        
        // Row 4 - Tujuan
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Tujuan:"), gbc);
        gbc.gridx = 1;
        txtTujuan = new JTextField(20);
        panel.add(txtTujuan, gbc);
        
        // Row 4 - Jumlah Penumpang
        gbc.gridx = 2;
        panel.add(new JLabel("Jumlah Penumpang:"), gbc);
        gbc.gridx = 3;
        txtJumlahPenumpang = new JTextField(20);
        panel.add(txtJumlahPenumpang, gbc);
        
        // Row 5 - Lama Sewa
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Lama Sewa (hari):"), gbc);
        gbc.gridx = 1;
        txtLamaSewa = new JTextField(20);
        txtLamaSewa.setEditable(false);
        txtLamaSewa.setBackground(Color.LIGHT_GRAY);
        panel.add(txtLamaSewa, gbc);
        
        // Row 5 - Total Harga
        gbc.gridx = 2;
        panel.add(new JLabel("Total Harga:"), gbc);
        gbc.gridx = 3;
        txtTotalHarga = new JTextField(20);
        txtTotalHarga.setEditable(false);
        txtTotalHarga.setBackground(Color.LIGHT_GRAY);
        panel.add(txtTotalHarga, gbc);
        
        // Row 6 - Catatan
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Catatan:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtCatatan = new JTextArea(3, 20);
        txtCatatan.setLineWrap(true);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        panel.add(scrollCatatan, gbc);
        
        // Row 7 - Buttons
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnTambah = createButton("Buat Booking", new Color(46, 204, 113));
        btnUpdate = createButton("Update", new Color(52, 152, 219));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        btnRefresh = createButton("Refresh", new Color(241, 196, 15));
        btnHitung = createButton("Hitung Total", new Color(230, 126, 34));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnHitung);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, gbc);
        
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
    
    private void loadComboBoxData() {
        // Load Pelanggan
        cmbPelanggan.removeAllItems();
        List<Pelanggan> pelangganList = pelangganDAO.getAllPelanggan();
        for (Pelanggan p : pelangganList) {
            cmbPelanggan.addItem(p.getIdPelanggan() + " - " + p.getNamaPelanggan() + " (" + p.getNoTelp() + ")");
        }
        
        // Load Bus Tersedia
        cmbBus.removeAllItems();
        List<Bus> busList = busDAO.getBusTersedia();
        for (Bus b : busList) {
            cmbBus.addItem(b.getIdBus() + " - " + b.getNoPolisi() + " (" + b.getTipeBus() + " - " + b.getKapasitas() + " seat)");
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Booking> bookingList = bookingDAO.getAllBooking();
        
        for (Booking booking : bookingList) {
            Object[] row = {
                booking.getIdBooking(),
                booking.getKodeBooking(),
                booking.getNamaPelanggan(),
                booking.getNoPolisi(),
                new SimpleDateFormat("dd/MM/yyyy").format(booking.getTanggalMulai()),
                new SimpleDateFormat("dd/MM/yyyy").format(booking.getTanggalSelesai()),
                booking.getTujuan(),
                booking.getLamaSewa() + " hari",
                currencyFormat.format(booking.getTotalHarga()),
                booking.getStatusBooking()
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahBooking() {
        if (!validateInput()) return;
        
        Booking booking = new Booking();
        booking.setKodeBooking(bookingDAO.generateKodeBooking());
        booking.setIdPelanggan(getSelectedIdFromCombo(cmbPelanggan));
        booking.setIdBus(getSelectedIdFromCombo(cmbBus));
        booking.setIdKasir(SessionManager.getCurrentUserId());
        booking.setTanggalMulai(dateStart.getDate());
        booking.setTanggalSelesai(dateEnd.getDate());
        booking.setTujuan(txtTujuan.getText().trim());
        booking.setJumlahPenumpang(Integer.parseInt(txtJumlahPenumpang.getText().trim()));
        booking.setLamaSewa(Integer.parseInt(txtLamaSewa.getText().trim()));
        booking.setTotalHarga(Double.parseDouble(txtTotalHarga.getText().replace("Rp", "").replace(".", "").replace(",", ".").trim()));
        booking.setStatusBooking(cmbStatus.getSelectedItem().toString());
        booking.setCatatan(txtCatatan.getText().trim());
        
        if (bookingDAO.tambahBooking(booking)) {
            // Update status bus menjadi disewa
            busDAO.updateStatusBus(booking.getIdBus(), "disewa");
            
            JOptionPane.showMessageDialog(this, 
                "Booking berhasil dibuat!\nKode Booking: " + booking.getKodeBooking(), 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
            loadComboBoxData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal membuat booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBooking() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        Booking booking = new Booking();
        booking.setIdBooking(selectedId);
        booking.setTanggalMulai(dateStart.getDate());
        booking.setTanggalSelesai(dateEnd.getDate());
        booking.setTujuan(txtTujuan.getText().trim());
        booking.setJumlahPenumpang(Integer.parseInt(txtJumlahPenumpang.getText().trim()));
        booking.setLamaSewa(Integer.parseInt(txtLamaSewa.getText().trim()));
        booking.setTotalHarga(Double.parseDouble(txtTotalHarga.getText().replace("Rp", "").replace(".", "").replace(",", ".").trim()));
        booking.setStatusBooking(cmbStatus.getSelectedItem().toString());
        booking.setCatatan(txtCatatan.getText().trim());
        
        if (bookingDAO.updateBooking(booking)) {
            JOptionPane.showMessageDialog(this, "Booking berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusBooking() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus booking ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "Fitur hapus booking tidak diaktifkan.\nGunakan update status menjadi 'dibatalkan'.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void hitungLamaSewa() {
        if (dateStart.getDate() != null && dateEnd.getDate() != null) {
            long diff = dateEnd.getDate().getTime() - dateStart.getDate().getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            
            if (days > 0) {
                txtLamaSewa.setText(String.valueOf(days));
            } else {
                txtLamaSewa.setText("0");
            }
        }
    }
    
    private void hitungTotal() {
        if (cmbBus.getSelectedItem() == null || txtLamaSewa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bus dan tentukan lama sewa terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idBus = getSelectedIdFromCombo(cmbBus);
        Bus bus = busDAO.getBusById(idBus);
        
        if (bus != null) {
            int lamaSewa = Integer.parseInt(txtLamaSewa.getText());
            double total = bus.getHargaPerHari() * lamaSewa;
            txtTotalHarga.setText(currencyFormat.format(total));
        }
    }
    
    private void selectRow() {
        int row = tableBooking.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Booking booking = bookingDAO.getBookingById(selectedId);
            
            if (booking != null) {
                txtKodeBooking.setText(booking.getKodeBooking());
                dateStart.setDate(booking.getTanggalMulai());
                dateEnd.setDate(booking.getTanggalSelesai());
                txtTujuan.setText(booking.getTujuan());
                txtJumlahPenumpang.setText(String.valueOf(booking.getJumlahPenumpang()));
                txtLamaSewa.setText(String.valueOf(booking.getLamaSewa()));
                txtTotalHarga.setText(currencyFormat.format(booking.getTotalHarga()));
                cmbStatus.setSelectedItem(booking.getStatusBooking());
                txtCatatan.setText(booking.getCatatan());
                
                setButtonState(true);
            }
        }
    }
    
    private void clearForm() {
        txtKodeBooking.setText("");
        txtTujuan.setText("");
        txtJumlahPenumpang.setText("");
        txtLamaSewa.setText("");
        txtTotalHarga.setText("");
        txtCatatan.setText("");
        dateStart.setDate(null);
        dateEnd.setDate(null);
        cmbPelanggan.setSelectedIndex(-1);
        cmbBus.setSelectedIndex(-1);
        cmbStatus.setSelectedIndex(0);
        selectedId = -1;
        tableBooking.clearSelection();
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        cmbPelanggan.setEnabled(!isUpdate);
        cmbBus.setEnabled(!isUpdate);
    }
    
    private int getSelectedIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }
    
    private boolean validateInput() {
        if (cmbPelanggan.getSelectedItem() == null || cmbBus.getSelectedItem() == null ||
            dateStart.getDate() == null || dateEnd.getDate() == null ||
            txtTujuan.getText().trim().isEmpty() || txtJumlahPenumpang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txtJumlahPenumpang.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah penumpang harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (dateStart.getDate() == null || dateEnd.getDate() == null ||
            txtTujuan.getText().trim().isEmpty() || txtJumlahPenumpang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}