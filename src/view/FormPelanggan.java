package view;

import dao.PelangganDAO;
import model.Pelanggan;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FormPelanggan extends JFrame {
    private JTable tablePelanggan;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtNoTelp, txtEmail, txtNoKtp, txtSearch;
    private JTextArea txtAlamat;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnCari, btnRefresh;
    private PelangganDAO pelangganDAO;
    private int selectedId = -1;
    
    public FormPelanggan() {
        pelangganDAO = new PelangganDAO();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Data Pelanggan");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(241, 196, 15));
        JLabel lblTitle = new JLabel("DATA PELANGGAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Input Pelanggan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - Nama
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Pelanggan:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField(20);
        formPanel.add(txtNama, gbc);
        
        // Row 0 - No KTP
        gbc.gridx = 2;
        formPanel.add(new JLabel("No. KTP:"), gbc);
        gbc.gridx = 3;
        txtNoKtp = new JTextField(20);
        formPanel.add(txtNoKtp, gbc);
        
        // Row 1 - No Telp
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        txtNoTelp = new JTextField(20);
        formPanel.add(txtNoTelp, gbc);
        
        // Row 1 - Email
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        // Row 2 - Alamat
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        formPanel.add(scrollAlamat, gbc);
        
        // Button Panel
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
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
        searchPanel.setBorder(BorderFactory.createTitledBorder("Pencarian"));
        
        searchPanel.add(new JLabel("Cari (Nama/No. Telp/Email):"));
        txtSearch = new JTextField(25);
        searchPanel.add(txtSearch);
        
        btnCari = createButton("Cari", new Color(52, 152, 219));
        searchPanel.add(btnCari);
        
        btnRefresh = createButton("Refresh", new Color(149, 165, 166));
        searchPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Nama Pelanggan", "No. Telepon", "Email", "Alamat", "No. KTP"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePelanggan = new JTable(tableModel);
        tablePelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePelanggan.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePelanggan.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablePelanggan.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        JScrollPane scrollTable = new JScrollPane(tablePelanggan);
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahPelanggan());
        btnUpdate.addActionListener(e -> updatePelanggan());
        btnHapus.addActionListener(e -> hapusPelanggan());
        btnBatal.addActionListener(e -> clearForm());
        btnCari.addActionListener(e -> cariPelanggan());
        btnRefresh.addActionListener(e -> loadData());
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtSearch.getText().trim().isEmpty()) {
                    cariPelanggan();
                } else {
                    loadData();
                }
            }
        });
        
        tablePelanggan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Pelanggan> pelangganList = pelangganDAO.getAllPelanggan();
        
        for (Pelanggan pelanggan : pelangganList) {
            Object[] row = {
                pelanggan.getIdPelanggan(),
                pelanggan.getNamaPelanggan(),
                pelanggan.getNoTelp(),
                pelanggan.getEmail(),
                pelanggan.getAlamat(),
                pelanggan.getNoKtp()
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahPelanggan() {
        if (!validateInput()) return;
        
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setNamaPelanggan(txtNama.getText().trim());
        pelanggan.setNoTelp(txtNoTelp.getText().trim());
        pelanggan.setEmail(txtEmail.getText().trim());
        pelanggan.setAlamat(txtAlamat.getText().trim());
        pelanggan.setNoKtp(txtNoKtp.getText().trim());
        
        if (pelangganDAO.tambahPelanggan(pelanggan)) {
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePelanggan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInput()) return;
        
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setIdPelanggan(selectedId);
        pelanggan.setNamaPelanggan(txtNama.getText().trim());
        pelanggan.setNoTelp(txtNoTelp.getText().trim());
        pelanggan.setEmail(txtEmail.getText().trim());
        pelanggan.setAlamat(txtAlamat.getText().trim());
        pelanggan.setNoKtp(txtNoKtp.getText().trim());
        
        if (pelangganDAO.updatePelanggan(pelanggan)) {
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusPelanggan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus pelanggan ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (pelangganDAO.hapusPelanggan(selectedId)) {
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pelanggan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cariPelanggan() {
        String keyword = txtSearch.getText().trim();
        
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Pelanggan> pelangganList = pelangganDAO.searchPelanggan(keyword);
        
        for (Pelanggan pelanggan : pelangganList) {
            Object[] row = {
                pelanggan.getIdPelanggan(),
                pelanggan.getNamaPelanggan(),
                pelanggan.getNoTelp(),
                pelanggan.getEmail(),
                pelanggan.getAlamat(),
                pelanggan.getNoKtp()
            };
            tableModel.addRow(row);
        }
        
        if (pelangganList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void selectRow() {
        int row = tablePelanggan.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            txtNoTelp.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
            txtAlamat.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
            txtNoKtp.setText(tableModel.getValueAt(row, 5) != null ? tableModel.getValueAt(row, 5).toString() : "");
            
            setButtonState(true);
        }
    }
    
    private void clearForm() {
        txtNama.setText("");
        txtNoTelp.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        txtNoKtp.setText("");
        txtSearch.setText("");
        selectedId = -1;
        tablePelanggan.clearSelection();
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnHapus.setEnabled(isUpdate);
    }
    
    private boolean validateInput() {
        if (txtNama.getText().trim().isEmpty() ||
            txtNoTelp.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan No. Telepon wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}