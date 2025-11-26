package view;

import dao.BusDAO;
import model.Bus;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FormBus extends JFrame {
    private JTable tableBus;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtNoPolisi, txtTipeBus, txtMerk, txtKapasitas, txtHarga;
    private BusDAO busDAO;
    private JTextArea txtFasilitas;
    private JComboBox<String> cmbStatus;
    private boolean isAdmin;
    private int selectedId = -1;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal;
    private JPanel formPanel; // Untuk sembunyikan form saat kasir

    // Formatter
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public FormBus() {
        busDAO = new BusDAO();
        isAdmin = SessionManager.isAdmin();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Data Bus Pariwisata");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Search Panel (selalu tampil)
        JPanel searchPanel = createSearchPanel();

        // Form Panel (hanya untuk admin)
        formPanel = createFormPanel();
        if (!isAdmin) {
            formPanel.setVisible(false);
            formPanel.setPreferredSize(new Dimension(0, 0));
        }

        // Table Panel
        JPanel tablePanel = createTablePanel();

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(5, 15));
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);

        contentPanel.add(topPanel, BorderLayout.NORTH);
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
                
                Color color = isAdmin ? new Color(52, 152, 219) : new Color(46, 204, 113);
                GradientPaint gradient = new GradientPaint(0, 0, color, getWidth(), getHeight(), color.darker());
                g2.setPaint(gradient);
                g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 90));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel titleLabel = new JLabel(isAdmin ? "KELOLA DATA BUS" : "DATA BUS TERSEDIA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel(isAdmin ? 
            "Tambah, edit, dan hapus data bus" : 
            "Daftar bus yang tersedia untuk pemesanan");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "🔍 Pencarian Cepat"
        ));

        JLabel iconLabel = new JLabel("🔍");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(iconLabel);

        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Cari no. polisi, tipe, atau merk...");
        panel.add(txtSearch);

        // ✅ Live Search: pencarian real-time saat ketik
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { cariBus(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { cariBus(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { cariBus(); }
        });

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "📝 Form Input Bus"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Row 0 - No Polisi & Tipe Bus
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabeledField("No. Polisi:", txtNoPolisi = new JTextField(15)), gbc);
        gbc.gridx = 1;
        panel.add(createLabeledField("Tipe Bus:", txtTipeBus = new JTextField(15)), gbc);

        // Row 1 - Merk & Kapasitas
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabeledField("Merk:", txtMerk = new JTextField(15)), gbc);
        gbc.gridx = 1;
        panel.add(createLabeledField("Kapasitas:", txtKapasitas = new JTextField(15)), gbc);

        // Row 2 - Harga & Status
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabeledField("Harga/Hari:", txtHarga = new JTextField(15)), gbc);
        gbc.gridx = 1;
        panel.add(createLabeledField("Status:", cmbStatus = new JComboBox<>(new String[]{"tersedia", "disewa", "maintenance"})), gbc);

        // Row 3 - Fasilitas
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel fasilitasPanel = new JPanel(new BorderLayout());
        fasilitasPanel.add(new JLabel("Fasilitas:"), BorderLayout.WEST);
        txtFasilitas = new JTextArea(3, 25);
        txtFasilitas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFasilitas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane scrollFasilitas = new JScrollPane(txtFasilitas);
        fasilitasPanel.add(scrollFasilitas, BorderLayout.CENTER);
        panel.add(fasilitasPanel, gbc);

        // Button Panel
        gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnTambah = createModernButton("➕ Tambah Bus", new Color(46, 204, 113));
        btnUpdate = createModernButton("✏️ Update", new Color(52, 152, 219));
        btnHapus = createModernButton("🗑️ Hapus", new Color(231, 76, 60));
        btnBatal = createModernButton("↩️ Batal", new Color(149, 165, 166));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        panel.add(buttonPanel, gbc);

        // Event Listeners
        btnTambah.addActionListener(e -> tambahBus());
        btnUpdate.addActionListener(e -> updateBus());
        btnHapus.addActionListener(e -> hapusBus());
        btnBatal.addActionListener(e -> clearForm());

        // Mouse hover effect untuk button
        addHoverEffect(btnTambah, new Color(46, 204, 113));
        addHoverEffect(btnUpdate, new Color(52, 152, 219));
        addHoverEffect(btnHapus, new Color(231, 76, 60));
        addHoverEffect(btnBatal, new Color(149, 165, 166));

        setButtonState(false);

        return panel;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTablePanel() {
    // Custom table model
    String[] columns = {"No. Polisi", "Tipe Bus", "Merk", "Kapasitas", "Harga/Hari", "Status", "Fasilitas"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tableBus = new JTable(tableModel) {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (!isRowSelected(row)) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
            }
            return c;
        }
    };

    tableBus.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableBus.setRowHeight(30);
    tableBus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    tableBus.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    tableBus.getTableHeader().setBackground(new Color(52, 152, 219));
    tableBus.getTableHeader().setForeground(Color.WHITE);
    tableBus.setGridColor(new Color(230, 230, 230));

    // Adjust column widths
    tableBus.getColumnModel().getColumn(0).setPreferredWidth(120);
    tableBus.getColumnModel().getColumn(1).setPreferredWidth(100);
    tableBus.getColumnModel().getColumn(2).setPreferredWidth(100);
    tableBus.getColumnModel().getColumn(3).setPreferredWidth(90);
    tableBus.getColumnModel().getColumn(4).setPreferredWidth(120);
    tableBus.getColumnModel().getColumn(5).setPreferredWidth(100);
    tableBus.getColumnModel().getColumn(6).setPreferredWidth(200);

    // Custom renderer
    tableBus.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

    JScrollPane scrollTable = new JScrollPane(tableBus);
    scrollTable.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

    tableBus.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isAdmin) selectRow();
        }
    });

    // FIX: wrap scrollTable in JPanel
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(scrollTable, BorderLayout.CENTER);

    return panel;
    }


    // Custom renderer untuk status
    private static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            
            // Warna berdasarkan status
            String status = value.toString();
            if ("tersedia".equals(status)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(46, 107, 46));
            } else if ("disewa".equals(status)) {
                setBackground(new Color(217, 237, 247));
                setForeground(new Color(49, 112, 143));
            } else if ("maintenance".equals(status)) {
                setBackground(new Color(252, 248, 227));
                setForeground(new Color(153, 126, 12));
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        // Custom painting for rounded button
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(bgColor);
        
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

    private void loadData() {
        tableModel.setRowCount(0);
        List<Bus> busList = busDAO.getAllBus();
        
        for (Bus bus : busList) {
            Object[] row = {
                bus.getNoPolisi(),
                bus.getTipeBus(),
                bus.getMerk(),
                bus.getKapasitas() + " seat",
                currencyFormat.format(bus.getHargaPerHari()),
                bus.getStatus(),
                bus.getFasilitas()
            };
            tableModel.addRow(row);
        }
    }

    private void cariBus() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        
        tableModel.setRowCount(0);
        List<Bus> busList = busDAO.getAllBus();
        
        for (Bus bus : busList) {
            // Filter berdasarkan keyword
            boolean match = keyword.isEmpty() ||
                bus.getNoPolisi().toLowerCase().contains(keyword) ||
                bus.getTipeBus().toLowerCase().contains(keyword) ||
                bus.getMerk().toLowerCase().contains(keyword);
            
            if (match) {
                Object[] row = {
                    bus.getNoPolisi(),
                    bus.getTipeBus(),
                    bus.getMerk(),
                    bus.getKapasitas() + " seat",
                    currencyFormat.format(bus.getHargaPerHari()),
                    bus.getStatus(),
                    bus.getFasilitas()
                };
                tableModel.addRow(row);
            }
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
            JOptionPane.showMessageDialog(this, "✅ Bus berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menambahkan bus!", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "✅ Bus berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal mengupdate bus!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusBus() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih bus yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus bus ini?\nNo. Polisi: " + txtNoPolisi.getText(),
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (busDAO.hapusBus(selectedId)) {
                JOptionPane.showMessageDialog(this, "✅ Bus berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus bus!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectRow() {
        int row = tableBus.getSelectedRow();
        if (row != -1) {
            String noPolisi = tableModel.getValueAt(row, 0).toString();
            Bus bus = busDAO.getBusByNoPolisi(noPolisi);
            
            if (bus != null) {
                selectedId = bus.getIdBus();
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
            int kapasitas = Integer.parseInt(txtKapasitas.getText().trim());
            if (kapasitas <= 0) {
                JOptionPane.showMessageDialog(this, "Kapasitas harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Kapasitas harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            double harga = Double.parseDouble(txtHarga.getText().trim());
            if (harga <= 0) {
                JOptionPane.showMessageDialog(this, "Harga harus lebih dari 0!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Harga harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    
}