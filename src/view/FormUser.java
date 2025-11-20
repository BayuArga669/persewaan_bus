package view;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class FormUser extends JFrame {
    private JTable tablUser;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtNamaLengkap, txtEmail, txtNoTelp;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JComboBox<String> cmbRole, cmbStatus;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnRefresh;
    private UserDAO userDAO;
    private int selectedId = -1;
    
    public FormUser() {
        userDAO = new UserDAO();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Data User");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(155, 89, 182));
        JLabel lblTitle = new JLabel("DATA USER SISTEM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Input User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0 - Username
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        formPanel.add(txtUsername, gbc);
        
        // Row 0 - Password
        gbc.gridx = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3;
        txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);
        
        // Row 1 - Nama Lengkap
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        txtNamaLengkap = new JTextField(20);
        formPanel.add(txtNamaLengkap, gbc);
        
        // Row 1 - Email
        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);
        
        // Row 2 - No Telp
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1;
        txtNoTelp = new JTextField(20);
        formPanel.add(txtNoTelp, gbc);
        
        // Row 2 - Role
        gbc.gridx = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3;
        cmbRole = new JComboBox<>(new String[]{"kasir", "admin"});
        formPanel.add(cmbRole, gbc);
        
        // Row 3 - Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtAlamat = new JTextArea(3, 20);
        txtAlamat.setLineWrap(true);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        formPanel.add(scrollAlamat, gbc);
        
        // Row 3 - Status
        gbc.gridx = 3; gbc.gridwidth = 1;
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"aktif", "nonaktif"});
        statusPanel.add(cmbStatus);
        formPanel.add(statusPanel, gbc);
        
        // Button Panel
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        btnTambah = createButton("Tambah", new Color(46, 204, 113));
        btnUpdate = createButton("Update", new Color(52, 152, 219));
        btnHapus = createButton("Hapus", new Color(231, 76, 60));
        btnBatal = createButton("Batal", new Color(149, 165, 166));
        btnRefresh = createButton("Refresh", new Color(241, 196, 15));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnRefresh);
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columns = {"ID", "Username", "Nama Lengkap", "Email", "No. Telp", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablUser = new JTable(tableModel);
        tablUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablUser.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablUser.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablUser.getColumnModel().getColumn(6).setPreferredWidth(80);
        
        JScrollPane scrollTable = new JScrollPane(tablUser);
        
        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollTable, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnHapus.addActionListener(e -> hapusUser());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
        
        tablUser.addMouseListener(new MouseAdapter() {
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
        List<User> userList = userDAO.getAllUsers();
        
        for (User user : userList) {
            Object[] row = {
                user.getIdUser(),
                user.getUsername(),
                user.getNamaLengkap(),
                user.getEmail(),
                user.getNoTelp(),
                user.getRole(),
                user.getStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahUser() {
        if (!validateInput()) return;
        
        User user = new User();
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(new String(txtPassword.getPassword()));
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setRole(cmbRole.getSelectedItem().toString());
        user.setStatus(cmbStatus.getSelectedItem().toString());
        
        if (userDAO.tambahUser(user)) {
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateUser() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        User user = new User();
        user.setIdUser(selectedId);
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setStatus(cmbStatus.getSelectedItem().toString());
        
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User berhasil diupdate!");
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusUser() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus user ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.hapusUser(selectedId)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!");
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectRow() {
        int row = tablUser.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            txtUsername.setText(tableModel.getValueAt(row, 1).toString());
            txtNamaLengkap.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtNoTelp.setText(tableModel.getValueAt(row, 4).toString());
            cmbRole.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
            
            txtUsername.setEditable(false);
            txtPassword.setEnabled(false);
            cmbRole.setEnabled(false);
            
            setButtonState(true);
        }
    }
    
    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtNamaLengkap.setText("");
        txtEmail.setText("");
        txtNoTelp.setText("");
        txtAlamat.setText("");
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        selectedId = -1;
        tablUser.clearSelection();
        
        txtUsername.setEditable(true);
        txtPassword.setEnabled(true);
        cmbRole.setEnabled(true);
        
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnHapus.setEnabled(isUpdate);
    }
    
    private boolean validateInput() {
        if (txtUsername.getText().trim().isEmpty() ||
            new String(txtPassword.getPassword()).isEmpty() ||
            txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Password, dan Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}