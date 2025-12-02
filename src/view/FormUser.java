package view;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FormUser extends JFrame {
    private JTable tableUser;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtNamaLengkap, txtEmail, txtNoTelp;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JComboBox<String> cmbRole, cmbStatus;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnRefresh;
    private UserDAO userDAO;
    private int selectedId = -1;

    // Warna tema
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color PRIMARY_DARK = new Color(31, 97, 141);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color INFO = new Color(52, 152, 219);
    private final Color WARNING = new Color(241, 196, 15);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color LIGHT = new Color(248, 249, 250);
    private final Color DARK = new Color(52, 58, 64);
    private final Color GRAY = new Color(206, 212, 218);
    private final Color LIGHT_GRAY = new Color(233, 236, 239);
    private final Color FOCUS_BLUE = new Color(60, 140, 200);

    public FormUser() {
        userDAO = new UserDAO();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("MANAJEMEN USER");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(0, 15));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Form Panel
        JPanel formPanel = createFormPanel();

        // Table Panel
        JScrollPane tablePanel = createTablePanel();

        // Layout
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY,
                    getWidth(), getHeight(), PRIMARY_DARK
                );
                g2.setPaint(gradient);
                g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel titleLabel = new JLabel("MANAJEMEN USER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Kelola admin, kasir, dan sopir");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "Form Input User"
        ));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // No SIM panel needed as driver management is removed

        // --- Form Fields Utama ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtUsername = createTextField(), gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        panel.add(txtPassword = createPasswordField(), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtNamaLengkap = createTextField(), gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        panel.add(txtEmail = createTextField(), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtNoTelp = createNumericField(), gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        cmbRole = new JComboBox<>(new String[]{"admin", "kasir"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbRole, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        cmbStatus = new JComboBox<>(new String[]{"aktif", "nonaktif"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbStatus, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.weightx = 1.0;
        panel.add(new JLabel("Alamat:"), gbc);
        gbc.gridy = 5;
        txtAlamat = new JTextArea(3, 40);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAlamat.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY));
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        panel.add(scrollAlamat, gbc);
        gbc.gridwidth = 1;

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.CENTER; gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnTambah = createModernButton("➕ Tambah", SUCCESS);
        btnUpdate = createModernButton("✏️ Update", INFO);
        btnHapus = createModernButton("🗑️ Hapus", DANGER);
        btnBatal = createModernButton("↩️ Batal", GRAY);
        btnRefresh = createModernButton("🔄 Refresh", WARNING);
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, gbc);

        // Event Listeners
        btnTambah.addActionListener(e -> tambahUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnHapus.addActionListener(e -> hapusUser());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadData());
        cmbRole.addActionListener(e -> toggleSIMFields());

        // Hover effect
        addHoverEffect(btnTambah, SUCCESS);
        addHoverEffect(btnUpdate, INFO);
        addHoverEffect(btnHapus, DANGER);
        addHoverEffect(btnBatal, GRAY);
        addHoverEffect(btnRefresh, WARNING);

        setButtonState(false);

        return panel;
    }

    private void toggleSIMFields() {
    // No SIM fields to toggle since driver management has been removed
    // This method exists to maintain the event listener but does nothing
}

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        addFieldFocusListener(field);
        return field;
    }
    
    private JTextField createNumericField() {
        JTextField field = new JTextField() {
            @Override
            protected void processKeyEvent(KeyEvent e) {
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
                    e.consume();
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        addFieldFocusListener(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        addFieldFocusListener(field);
        return field;
    }

    private void addFieldFocusListener(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOCUS_BLUE, 2),
                    BorderFactory.createEmptyBorder(4, 7, 4, 7)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
            }
        });
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Username", "Nama Lengkap", "Email", "No. Telp", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableUser = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };

        tableUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUser.setRowHeight(30);
        tableUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableUser.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableUser.getTableHeader().setBackground(PRIMARY);
        tableUser.getTableHeader().setForeground(Color.WHITE);
        tableUser.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tableUser.setGridColor(new Color(230, 230, 230));

        tableUser.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableUser.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableUser.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableUser.getColumnModel().getColumn(3).setPreferredWidth(180);
        tableUser.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableUser.getColumnModel().getColumn(5).setPreferredWidth(80);
        tableUser.getColumnModel().getColumn(6).setPreferredWidth(80);

        tableUser.getColumnModel().getColumn(5).setCellRenderer(new RoleCellRenderer());
        tableUser.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableUser);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "Daftar User"
        ));

        // ✅ PERUBAHAN PENTING: Mengganti MouseListener dengan ListSelectionListener
        // Ini lebih andal karena merespons perubahan pilihan dari mouse dan keyboard
        tableUser.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Mencegah event dipicu dua kali
                selectRow();
            }
        });

        return scrollTable;
    }

    // Renderers untuk role dan status
    private static class RoleCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            
            String role = value.toString();
            if ("admin".equals(role)) {
                setBackground(new Color(217, 237, 247));
                setForeground(new Color(49, 112, 143));
            } else if ("kasir".equals(role)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(60, 120, 60));
            }
            return this;
        }
    }

    private static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

            String status = value.toString();
            if ("aktif".equals(status)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(60, 120, 60));
            } else {
                setBackground(new Color(248, 215, 218));
                setForeground(new Color(180, 60, 60));
            }
            return this;
        }
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void addHoverEffect(JButton button, Color baseColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    // --- Metode CRUD ---
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

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private void tambahUser() {
        String role = cmbRole.getSelectedItem().toString();

        if (txtUsername.getText().trim().isEmpty() || txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (new String(txtPassword.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(new String(txtPassword.getPassword()));
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setRole(role);
        user.setStatus(cmbStatus.getSelectedItem().toString());

        if (userDAO.tambahUser(user)) {
            JOptionPane.showMessageDialog(this, "✅ User berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menambahkan user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUser() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = cmbRole.getSelectedItem().toString();

        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setIdUser(selectedId);
        user.setUsername(txtUsername.getText().trim());
        user.setRole(role);
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setStatus(cmbStatus.getSelectedItem().toString());

        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "✅ User berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal mengupdate user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusUser() {
    if (selectedId == -1) {
        JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "Apakah Anda yakin ingin menghapus user ini?\nUsername: " + txtUsername.getText(),
        "Konfirmasi Hapus",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            boolean success = userDAO.hapusUser(selectedId);
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ User berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

    // ✅ PERUBAHAN: Metode selectRow dengan log debug dan penanganan error
   private void selectRow() {
        int row = tableUser.getSelectedRow();

        if (row != -1) {
            try {
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                String role = tableModel.getValueAt(row, 5).toString();

                // Set semua field dari data tabel
                txtUsername.setText(tableModel.getValueAt(row, 1).toString());
                txtNamaLengkap.setText(tableModel.getValueAt(row, 2).toString());
                txtEmail.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                txtNoTelp.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
                cmbRole.setSelectedItem(role);
                cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());

                // Load alamat from the database
                txtAlamat.setText(""); // This would normally be loaded from the DB

                // Disable fields that should not be changed
                txtUsername.setEditable(false);
                txtPassword.setEnabled(false);
                txtPassword.setText("");
                cmbRole.setEnabled(false);

                // ✅ PENTING: Aktifkan tombol Update & Hapus
                setButtonState(true);

                System.out.println("✅ Row selected: ID=" + selectedId + ", Role=" + role); // Debug log

            } catch (Exception ex) {
                System.err.println("❌ Error selecting row: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error saat memilih data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Jika tidak ada yang dipilih, clear form
            clearForm();
        }
    }

    private void clearForm() {
        // Clear semua field
        txtUsername.setText("");
        txtPassword.setText("");
        txtNamaLengkap.setText("");
        txtEmail.setText("");
        txtNoTelp.setText("");
        txtAlamat.setText("");

        // Reset combo box
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);

        // Reset selected ID
        selectedId = -1;

        // Clear table selection
        tableUser.clearSelection();

        // Enable field yang tadi di-disable
        txtUsername.setEditable(true);
        txtPassword.setEnabled(true);
        cmbRole.setEnabled(true);

        // ✅ Nonaktifkan tombol Update & Hapus, aktifkan Tambah
        setButtonState(false);

        System.out.println("🧹 Form cleared"); // Debug log
    }

    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);  // Tambah aktif saat mode insert
        btnUpdate.setEnabled(isUpdate);    // Update aktif saat mode edit
        btnHapus.setEnabled(isUpdate);     // Hapus aktif saat mode edit

        System.out.println("🔘 Button state changed: isUpdate=" + isUpdate); // Debug log
    }
}