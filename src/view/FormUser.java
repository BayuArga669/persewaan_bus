package view;

import dao.UserDAO;
import dao.SopirDAO;
import model.User;
import model.Sopir;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FormUser extends JFrame {
    private JTable tableUser;
    private DefaultTableModel tableModel;
    private JTextField txtUsername, txtNamaLengkap, txtEmail, txtNoTelp, txtNoSim, txtJenisSim, txtMasaBerlakuSim;
    private JPasswordField txtPassword;
    private JTextArea txtAlamat;
    private JComboBox<String> cmbRole, cmbStatus;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnRefresh;
    private UserDAO userDAO;
    private SopirDAO sopirDAO;
    private int selectedId = -1;

    // Panel untuk field SIM
    private JPanel panelSIM;

    // Warna tema
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color PRIMARY_DARK = new Color(31, 97, 141);
    private final Color SECONDARY = new Color(52, 152, 219);
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
        sopirDAO = new SopirDAO();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("MANAJEMEN USER");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(0, 15)); // Add spacing between header and content

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
        headerPanel.setPreferredSize(new Dimension(0, 80)); // Reduced height
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

        // --- SIM Panel (dibuat di awal, tapi disembunyikan) ---
        panelSIM = new JPanel(new GridBagLayout()); // Menggunakan GridBagLayout untuk kontrol lebih baik
        panelSIM.setBorder(BorderFactory.createTitledBorder("Detail SIM"));
        panelSIM.setBackground(Color.WHITE);
        panelSIM.setVisible(false);
        
        // GridBagConstraints khusus untuk panel SIM
        GridBagConstraints gbcSim = new GridBagConstraints();
        gbcSim.insets = new Insets(5, 5, 5, 5);
        gbcSim.fill = GridBagConstraints.HORIZONTAL; // Buat field memanjang horizontal
        gbcSim.anchor = GridBagConstraints.WEST;

        // Baris 1 SIM: No SIM & Jenis SIM (berdampingan)
        gbcSim.gridx = 0; gbcSim.gridy = 0; gbcSim.weightx = 0.2;
        panelSIM.add(new JLabel("No. SIM:"), gbcSim);
        
        gbcSim.gridx = 1; gbcSim.weightx = 0.8; // Beri lebih banyak ruang
        panelSIM.add(txtNoSim = createTextField(), gbcSim);

        gbcSim.gridx = 2; gbcSim.weightx = 0.2;
        panelSIM.add(new JLabel("Jenis SIM:"), gbcSim);
        
        gbcSim.gridx = 3; gbcSim.weightx = 0.8; // Beri lebih banyak ruang
        panelSIM.add(txtJenisSim = createTextField(), gbcSim);

        // Baris 2 SIM: Masa Berlaku (memanjang)
        gbcSim.gridx = 0; gbcSim.gridy = 1; gbcSim.weightx = 0.2;
        panelSIM.add(new JLabel("Masa Berlaku (dd/MM/yyyy):"), gbcSim);
        
        gbcSim.gridx = 1; gbcSim.gridwidth = 3; gbcSim.weightx = 1.0; // Span 3 kolom
        panelSIM.add(txtMasaBerlakuSim = createTextField(), gbcSim);
        // Reset gridwidth untuk komponen lain jika perlu
        gbcSim.gridwidth = 1; 

        // --- Form Fields Utama ---
        // Baris 1: Username & Password
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtUsername = createTextField(), gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        panel.add(txtPassword = createPasswordField(), gbc);

        // Baris 2: Nama Lengkap & Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtNamaLengkap = createTextField(), gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        panel.add(txtEmail = createTextField(), gbc);

        // Baris 3: No. Telepon & Role
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(new JLabel("No. Telepon:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(txtNoTelp = createTextField(), gbc);

        gbc.gridx = 2; gbc.gridy = 2; gbc.weightx = 0.2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.8;
        cmbRole = new JComboBox<>(new String[]{"admin", "kasir", "sopir"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbRole, gbc);

        // Baris 4: Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.2;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        cmbStatus = new JComboBox<>(new String[]{"aktif", "nonaktif"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cmbStatus, gbc);
        
        // Baris 5: Alamat
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
        gbc.gridwidth = 1; // Reset gridwidth

        // Baris 6: Panel SIM
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(panelSIM, gbc);
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill

        // Baris 7: Buttons
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.CENTER; gbc.anchor = GridBagConstraints.CENTER;
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

    // 🔹 Toggle field SIM berdasarkan role
    private void toggleSIMFields() {
        boolean isSopir = "sopir".equals(cmbRole.getSelectedItem());
        panelSIM.setVisible(isSopir);
        // Sembunyikan/show password
        txtPassword.setVisible(!isSopir);
        // Ubah label password
        Component parent = txtPassword.getParent();
        if (parent instanceof JPanel) {
            for (Component comp : ((JPanel) parent).getComponents()) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().equals("Password:")) {
                    comp.setVisible(!isSopir);
                }
            }
        }
        
        // Revalidate dan repaint container utama untuk menyesuaikan layout
        SwingUtilities.getWindowAncestor(this).revalidate();
        SwingUtilities.getWindowAncestor(this).repaint();
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

        // Set column widths
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

        tableUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
            } else if ("sopir".equals(role)) {
                setBackground(new Color(252, 248, 227));
                setForeground(new Color(180, 140, 0));
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
        boolean isSopir = "sopir".equals(role);

        if (txtUsername.getText().trim().isEmpty() || txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isSopir) {
            if (txtNoSim.getText().trim().isEmpty() || 
                txtJenisSim.getText().trim().isEmpty() || 
                txtMasaBerlakuSim.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data SIM wajib diisi untuk sopir!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (parseDate(txtMasaBerlakuSim.getText().trim()) == null) {
                JOptionPane.showMessageDialog(this, "Format tanggal masa berlaku SIM salah! Gunakan dd/MM/yyyy", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (new String(txtPassword.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        User user = new User();
        user.setUsername(txtUsername.getText().trim());
        user.setPassword(isSopir ? "default_sopir_password" : new String(txtPassword.getPassword()));
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setRole(role);
        user.setStatus(cmbStatus.getSelectedItem().toString());

        if (userDAO.tambahUser(user)) {
            int userId = userDAO.getUserIdByUsername(user.getUsername());
            if (isSopir) {
                Sopir sopir = new Sopir();
                sopir.setIdUser(userId);
                sopir.setNoSim(txtNoSim.getText().trim());
                sopir.setJenisSim(txtJenisSim.getText().trim());
                sopir.setMasaBerlakuSim(parseDate(txtMasaBerlakuSim.getText().trim()));
                sopir.setStatusSopir("aktif");
                sopirDAO.tambahSopir(sopir);
            }
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
        boolean isSopir = "sopir".equals(role);

        if (txtNamaLengkap.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Lengkap wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setIdUser(selectedId);
        user.setNamaLengkap(txtNamaLengkap.getText().trim());
        user.setEmail(txtEmail.getText().trim());
        user.setNoTelp(txtNoTelp.getText().trim());
        user.setAlamat(txtAlamat.getText().trim());
        user.setStatus(cmbStatus.getSelectedItem().toString());

        if (userDAO.updateUser(user)) {
            if (isSopir) {
                Sopir existingSopir = sopirDAO.getSopirByUserId(selectedId);
                if (existingSopir != null) {
                    existingSopir.setNoSim(txtNoSim.getText().trim());
                    existingSopir.setJenisSim(txtJenisSim.getText().trim());
                    existingSopir.setMasaBerlakuSim(parseDate(txtMasaBerlakuSim.getText().trim()));
                    sopirDAO.updateSopir(existingSopir);
                }
            }
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
        String role = "";
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (Integer.parseInt(tableModel.getValueAt(i, 0).toString()) == selectedId) {
                role = tableModel.getValueAt(i, 5).toString();
                break;
            }
        }
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus user ini?\nUsername: " + txtUsername.getText(),
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = true;
            if ("sopir".equals(role)) {
                Sopir sopir = sopirDAO.getSopirByUserId(selectedId);
                if (sopir != null) {
                    success = sopirDAO.hapusSopir(sopir.getIdSopir());
                }
            }
            if (success) {
                success = userDAO.hapusUser(selectedId);
            }
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ User berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectRow() {
        int row = tableUser.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            String role = tableModel.getValueAt(row, 5).toString();

            txtUsername.setText(tableUser.getValueAt(row, 1).toString());
            txtNamaLengkap.setText(tableUser.getValueAt(row, 2).toString());
            txtEmail.setText(tableUser.getValueAt(row, 3).toString());
            txtNoTelp.setText(tableUser.getValueAt(row, 4).toString());
            cmbRole.setSelectedItem(role);
            cmbStatus.setSelectedItem(tableUser.getValueAt(row, 6).toString());

            if ("sopir".equals(role)) {
                Sopir sopir = sopirDAO.getSopirByUserId(selectedId);
                if (sopir != null) {
                    txtNoSim.setText(sopir.getNoSim());
                    txtJenisSim.setText(sopir.getJenisSim());
                    if (sopir.getMasaBerlakuSim() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        txtMasaBerlakuSim.setText(sdf.format(sopir.getMasaBerlakuSim()));
                    }
                }
            }
            
            toggleSIMFields(); // Show/hide SIM fields based on role
            txtUsername.setEditable(false);
            txtPassword.setEnabled(false);
            txtPassword.setText("");
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
        txtNoSim.setText("");
        txtJenisSim.setText("");
        txtMasaBerlakuSim.setText("");
        
        cmbRole.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        
        selectedId = -1;
        tableUser.clearSelection();
        
        txtUsername.setEditable(true);
        txtPassword.setEnabled(true);
        cmbRole.setEnabled(true);
        
        setButtonState(false);
        toggleSIMFields(); // Reset tampilan
    }

    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnHapus.setEnabled(isUpdate);
    }
}