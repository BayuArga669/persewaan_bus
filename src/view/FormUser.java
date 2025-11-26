package view;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
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

    // Warna
    private final Color PRIMARY = new Color(155, 89, 182);
    private final Color PRIMARY_DARK = new Color(136, 78, 160);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color INFO = new Color(52, 152, 219);
    private final Color WARNING = new Color(241, 196, 15);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color LIGHT = new Color(248, 249, 250);
    private final Color DARK = new Color(52, 58, 64);
    private final Color GRAY = new Color(206, 212, 218);
    private final Color LIGHT_GRAY = new Color(233, 236, 239);

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
        setLayout(new BorderLayout());

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
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel titleLabel = new JLabel("MANAJEMEN USER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Kelola pengguna sistem: admin, kasir, dan sopir");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, 15, 15));
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "Form Input User"
        ));
        panel.setPreferredSize(new Dimension(0, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblNamaLengkap = new JLabel("Nama Lengkap:");
        lblNamaLengkap.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblNoTelp = new JLabel("No. Telepon:");
        lblNoTelp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblAlamat = new JLabel("Alamat:");
        lblAlamat.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Row 0 - Username & Password
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblUsername, gbc);
        gbc.gridx = 1;
        txtUsername = createTextField();
        panel.add(txtUsername, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(lblPassword, gbc);
        gbc.gridx = 3;
        txtPassword = createPasswordField();
        panel.add(txtPassword, gbc);

        // Row 1 - Nama Lengkap & Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(lblNamaLengkap, gbc);
        gbc.gridx = 1;
        txtNamaLengkap = createTextField();
        panel.add(txtNamaLengkap, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(lblEmail, gbc);
        gbc.gridx = 3;
        txtEmail = createTextField();
        panel.add(txtEmail, gbc);

        // Row 2 - No Telp & Role
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(lblNoTelp, gbc);
        gbc.gridx = 1;
        txtNoTelp = createTextField();
        panel.add(txtNoTelp, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        panel.add(lblRole, gbc);
        gbc.gridx = 3;
        cmbRole = new JComboBox<>(new String[]{"kasir", "admin", "sopir"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbRole, gbc);

        // Row 3 - Status
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(lblStatus, gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>(new String[]{"aktif", "nonaktif"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbStatus, gbc);

        // Row 4 - Alamat
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(lblAlamat, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtAlamat = new JTextArea(2, 30);
        txtAlamat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAlamat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        panel.add(scrollAlamat, gbc);

        // Button Panel
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 4;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        btnTambah = createModernButton("Tambah User", SUCCESS);
        btnUpdate = createModernButton("Update", INFO);
        btnHapus = createModernButton("Hapus", DANGER);
        btnBatal = createModernButton("Batal", new Color(149, 165, 166));
        btnRefresh = createModernButton("Refresh", WARNING);
        
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

        // Hover effect
        addHoverEffect(btnTambah, SUCCESS);
        addHoverEffect(btnUpdate, INFO);
        addHoverEffect(btnHapus, DANGER);
        addHoverEffect(btnBatal, new Color(149, 165, 166));
        addHoverEffect(btnRefresh, WARNING);

        setButtonState(false);

        return panel;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("Ketik di sini...", getInsets().left + 5, getHeight()/2 + 5);
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setColumns(20); // Increased width
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        addFieldFocusListener(field);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString("Ketik di sini...", getInsets().left + 5, getHeight()/2 + 5);
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setColumns(20); // Increased width
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        addFieldFocusListener(field);
        return field;
    }

    private void addFieldFocusListener(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
        tableUser.setRowHeight(35); // Increased row height
        tableUser.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Increased font size
        tableUser.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableUser.getTableHeader().setBackground(PRIMARY);
        tableUser.getTableHeader().setForeground(Color.WHITE);
        tableUser.getTableHeader().setPreferredSize(new Dimension(0, 40)); // Increased header height
        tableUser.setGridColor(new Color(230, 230, 230));

        tableUser.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableUser.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableUser.getColumnModel().getColumn(2).setPreferredWidth(200);
        tableUser.getColumnModel().getColumn(3).setPreferredWidth(200);
        tableUser.getColumnModel().getColumn(4).setPreferredWidth(150);
        tableUser.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableUser.getColumnModel().getColumn(6).setPreferredWidth(100);

        tableUser.getColumnModel().getColumn(5).setCellRenderer(new RoleCellRenderer());
        tableUser.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableUser);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "Daftar User"
        ));

        tableUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow();
            }
        });

        return scrollTable;
    }

    // Custom renderer untuk role
    private static class RoleCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            
            String role = value.toString();
            if ("admin".equals(role)) {
                setBackground(new Color(217, 237, 247));
                setForeground(new Color(49, 112, 143));
            } else if ("kasir".equals(role)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(46, 107, 46));
            } else if ("sopir".equals(role)) {
                setBackground(new Color(252, 248, 227));
                setForeground(new Color(153, 126, 12));
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }

    // Custom renderer untuk status
    private static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            
            String status = value.toString();
            if ("aktif".equals(status)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(46, 107, 46));
            } else {
                setBackground(new Color(248, 215, 218));
                setForeground(new Color(169, 68, 66));
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint background
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Paint text
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false); // Important for custom painting
        button.setOpaque(true); // Ensure it's opaque
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setBackground(bgColor);
        
        return button;
    }

    private void addHoverEffect(JButton button, Color baseColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
                button.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(baseColor.darker().darker());
                button.repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.getModel().isRollover()) {
                    button.setBackground(baseColor.darker());
                } else {
                    button.setBackground(baseColor);
                }
                button.repaint();
            }
        });
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
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "User berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
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
            "Apakah Anda yakin ingin menghapus user ini?\nUsername: " + txtUsername.getText(),
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.hapusUser(selectedId)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectRow() {
        int row = tableUser.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            
            txtUsername.setText(tableModel.getValueAt(row, 1).toString());
            txtNamaLengkap.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtNoTelp.setText(tableModel.getValueAt(row, 4).toString());
            cmbRole.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            cmbStatus.setSelectedItem(tableModel.getValueAt(row, 6).toString());
            
            // Disable field yang tidak boleh diubah
            txtUsername.setEditable(false);
            txtPassword.setEnabled(false);
            txtPassword.setText(""); // Kosongkan karena tidak ditampilkan
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
        tableUser.clearSelection();
        
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
            JOptionPane.showMessageDialog(this, 
                "Username, Password, dan Nama Lengkap wajib diisi!", 
                "Validasi", JOptionPane.WARNING_MESSAGE);
            
            if (txtUsername.getText().trim().isEmpty()) {
                txtUsername.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            if (new String(txtPassword.getPassword()).isEmpty()) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DANGER, 2),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            return false;
        }
        return true;
    }

    private boolean validateInputUpdate() {
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nama Lengkap wajib diisi!", 
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Optional: untuk testing langsung
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set a consistent Look and Feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Hanya untuk UI test (tanpa DAO sebenarnya)
            // new FormUser().setVisible(true);
        });
    }
}