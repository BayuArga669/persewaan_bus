package view;

import dao.BusDAO;
import model.Bus;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FormBus extends JFrame {
    private JTable tableBus;
    private DefaultTableModel tableModel;
    private JTextField txtNoPolisi, txtTipeBus, txtMerk, txtKapasitas, txtHarga, txtSearch;
    private JTextArea txtFasilitas;
    private JComboBox<String> cmbStatus, cmbFilterStatus;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnCari, btnRefresh;
    private BusDAO busDAO;
    private int selectedId = -1;
    private boolean isAdmin;
    
    public FormBus() {
        busDAO = new BusDAO();
        isAdmin = SessionManager.isAdmin();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Data Bus");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        JLabel lblTitle = new JLabel("DATA BUS PARIWISATA");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Input Bus"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - No Polisi
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("No. Polisi:"), gbc);
        gbc.gridx = 1;
        txtNoPolisi = new JTextField(20);
        formPanel.add(txtNoPolisi, gbc);
        
        // Row 0 - Tipe Bus
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tipe Bus:"), gbc);
        gbc.gridx = 3;
        txtTipeBus = new JTextField(20);
        formPanel.add(txtTipeBus, gbc);
        
        // Row 1 - Merk
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Merk:"), gbc);
        gbc.gridx = 1;
        txtMerk = new JTextField(20);
        formPanel.add(txtMerk, gbc);
        
        // Row 1 - Kapasitas
        gbc.gridx = 2;
        formPanel.add(new JLabel("Kapasitas:"), gbc);
        gbc.gridx = 3;
        txtKapasitas = new JTextField(20);
        formPanel.add(txtKapasitas, gbc);
        
        // Row 2 - Harga
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Harga/Hari:"), gbc);
        gbc.gridx = 1;
        txtHarga = new JTextField(20);
        formPanel.add(txtHarga, gbc);
        
        // Row 2 - Status
        gbc.gridx = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        cmbStatus = new JComboBox<>(new String[]{"tersedia", "disewa", "maintenance"});
        formPanel.add(cmbStatus, gbc);
        
        // Row 3 - Fasilitas
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Fasilitas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtFasilitas = new JTextArea(3, 20);
        txtFasilitas.setLineWrap(true);
        JScrollPane scrollFasilitas = new JScrollPane(txtFasilitas);
        formPanel.add(scrollFasilitas, gbc);
        
        // Button Panel
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnTambah = createButton("Tambah", new Color(46, 204, 113));
        btnUpdate = createButton("Update", new Color(52, 152, 219));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        formPanel.add(buttonPanel, gbc);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pencarian & Filter"));
        
        searchPanel.add(new JLabel("Cari:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        
        btnCari = createButton("Cari", new Color(52, 152, 219));
        searchPanel.add(btnCari);
        
        searchPanel.add(new JLabel("  Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "tersedia", "disewa", "maintenance"});
        searchPanel.add(cmbFilterStatus);
        
        btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        searchPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "No. Polisi", "Tipe", "Merk", "Kapasitas", "Harga/Hari", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBus = new JTable(tableModel);
        tableBus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBus.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableBus.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane scrollTable = new JScrollPane(tableBus);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahBus());
        btnUpdate.addActionListener(e -> updateBus());
        btnHapus.addActionListener(e -> hapusBus());
        btnBatal.addActionListener(e -> clearForm());
        btnCari.addActionListener(e -> cariBus());
        btnRefresh.addActionListener(e -> loadData());
        
        cmbFilterStatus.addActionListener(e -> cariBus());
        
        tableBus.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow();
            }
        });
        
        // Disable buttons for non-admin
        if (!isAdmin) {
            btnTambah.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnHapus.setEnabled(false);
            txtNoPolisi.setEditable(false);
            txtTipeBus.setEditable(false);
            txtMerk.setEditable(false);
            txtKapasitas.setEditable(false);
            txtHarga.setEditable(false);
            txtFasilitas.setEditable(false);
            cmbStatus.setEnabled(false);
        }
        
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
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Bus> busList = busDAO.getAllBus();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        for (Bus bus : busList) {
            Object[] row = {
                bus.getIdBus(),
                bus.getNoPolisi(),
                bus.getTipeBus(),
                bus.getMerk(),
                bus.getKapasitas() + " seat",
                currencyFormat.format(bus.getHargaPerHari()),
                bus.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahBus() {
        if (!validateInput()) return;
        
        Bus bus = new Bus();
        bus.setNoPolisi(txtNoPolisi.getText().trim());
        bus.setTipeBus(txtTipeBus.getText().trim());
        bus.setMerk(txtMerk.getText().trim());
        bus.setKapasitas(Integer.parseInt(txtKapasitas.getText().trim()));
        bus.setFasilitas(txtFasilitas.getText().trim());
        bus.setHargaPerHari(Double.parseDouble(txtHarga.getText().trim()));
        bus.setStatus(cmbStatus.getSelectedItem().toString());
        bus.setFoto("");
        
        if (busDAO.tambahBus(bus)) {
            JOptionPane.showMessageDialog(this, "Bus berhasil ditambahkan!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan bus!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih bus yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInput()) return;
        
        Bus bus = new Bus();
        bus.setIdBus(selectedId);
        bus.setNoPolisi(txtNoPolisi.getText().trim());
        bus.setTipeBus(txtTipeBus.getText().trim());
        bus.setMerk(txtMerk.getText().trim());
        bus.setKapasitas(Integer.parseInt(txtKapasitas.getText().trim()));
        bus.setFasilitas(txtFasilitas.getText().trim());
        bus.setHargaPerHari(Double.parseDouble(txtHarga.getText().trim()));
        bus.setStatus(cmbStatus.getSelectedItem().toString());
        bus.setFoto("");
        
        if (busDAO.updateBus(bus)) {
            JOptionPane.showMessageDialog(this, "Bus berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate bus!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusBus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih bus yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus bus ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (busDAO.hapusBus(selectedId)) {
                JOptionPane.showMessageDialog(this, "Bus berhasil dihapus!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus bus!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cariBus() {
        String keyword = txtSearch.getText().trim();
        String status = cmbFilterStatus.getSelectedItem().toString();
        
        tableModel.setRowCount(0);
        List<Bus> busList = busDAO.searchBus(keyword, status);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        for (Bus bus : busList) {
            Object[] row = {
                bus.getIdBus(),
                bus.getNoPolisi(),
                bus.getTipeBus(),
                bus.getMerk(),
                bus.getKapasitas() + " seat",
                currencyFormat.format(bus.getHargaPerHari()),
                bus.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void selectRow() {
        int row = tableBus.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            Bus bus = busDAO.getBusById(selectedId);
            
            if (bus != null) {
                txtNoPolisi.setText(bus.getNoPolisi());
                txtTipeBus.setText(bus.getTipeBus());
                txtMerk.setText(bus.getMerk());
                txtKapasitas.setText(String.valueOf(bus.getKapasitas()));
                txtHarga.setText(String.valueOf(bus.getHargaPerHari()));
                txtFasilitas.setText(bus.getFasilitas());
                cmbStatus.setSelectedItem(bus.getStatus());
                
                setButtonState(true);
            }
        }
    }
    
    private void clearForm() {
        txtNoPolisi.setText("");
        txtTipeBus.setText("");
        txtMerk.setText("");
        txtKapasitas.setText("");
        txtHarga.setText("");
        txtFasilitas.setText("");
        cmbStatus.setSelectedIndex(0);
        selectedId = -1;
        tableBus.clearSelection();
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        if (isAdmin) {
            btnTambah.setEnabled(!isUpdate);
            btnUpdate.setEnabled(isUpdate);
            btnHapus.setEnabled(isUpdate);
        }
    }
    
    private boolean validateInput() {
        if (txtNoPolisi.getText().trim().isEmpty() ||
            txtTipeBus.getText().trim().isEmpty() ||
            txtKapasitas.getText().trim().isEmpty() ||
            txtHarga.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txtKapasitas.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(txtHarga.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}