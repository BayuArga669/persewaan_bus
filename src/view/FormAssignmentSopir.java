package view;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.toedter.calendar.JDateChooser;

public class FormAssignmentSopir extends JFrame {
    private JTable tableAssignment;
    private DefaultTableModel tableModel;
    private JTextField txtFeeSopir;
    private JTextArea txtKeterangan;
    private JComboBox<String> cmbBooking, cmbSopir, cmbStatusBayar;
    private JDateChooser dateBayar;
    private JButton btnTambah, btnUpdate, btnBayar, btnBatal, btnRefresh;
    private AssignmentSopirDAO assignmentDAO;
    private BookingDAO bookingDAO;
    private SopirDAO sopirDAO;
    private int selectedId = -1;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
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
    
    public FormAssignmentSopir() {
        assignmentDAO = new AssignmentSopirDAO();
        bookingDAO = new BookingDAO();
        sopirDAO = new SopirDAO();
        initComponents();
        loadComboBoxData();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Assignment Sopir & Pembayaran");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header Panel dengan gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Layout
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Event Listeners
        btnTambah.addActionListener(e -> tambahAssignment());
        btnUpdate.addActionListener(e -> updateAssignment());
        btnBayar.addActionListener(e -> bayarSopir());
        btnBatal.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> {
            loadComboBoxData();
            loadData();
        });
        
        tableAssignment.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                selectRow();
            }
        });
        
        setButtonState(false);
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

        JLabel titleLabel = new JLabel("👨‍💼 ASSIGNMENT SOPIR & PEMBAYARAN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Kelola penugasan sopir dan pembayaran fee");
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
            "📋 Form Assignment Sopir"
        ));
        panel.setPreferredSize(new Dimension(0, 500)); // 🔥 Diperbesar lagi
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 20, 15, 20);
        
        // Row 0 - Booking (full width)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(createInputField("📋 Booking", cmbBooking = createTallComboBox()), gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        
        // Row 1 - Sopir (full width)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(createInputField("👨‍💼 Sopir", cmbSopir = createTallComboBox()), gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        
        // Row 2 - Fee Sopir & Status Bayar
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        panel.add(createInputField("💰 Fee Sopir", txtFeeSopir = createLargeTextField()), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5;
        panel.add(createInputField("💳 Status Bayar", cmbStatusBayar = createTallComboBox()), gbc);
        
        // Row 3 - Tanggal Bayar (full width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(createInputField("📅 Tanggal Bayar", dateBayar = createTallDateChooser()), gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        
        // Row 4 - Keterangan (full width)
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        panel.add(createInputField("📝 Keterangan", txtKeterangan = createLargeTextArea()), gbc);
        gbc.gridwidth = 1; gbc.weightx = 0;
        
        // Row 5 - Buttons
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        btnTambah = createModernButton("➕ Assign Sopir", SUCCESS);
        btnUpdate = createModernButton("✏️ Update", INFO);
        btnBayar = createModernButton("💳 Bayar", WARNING);
        btnBatal = createModernButton("↩️ Batal", new Color(149, 165, 166));
        btnRefresh = createModernButton("⟳ Refresh", PRIMARY);
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnBayar);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, gbc);
        
        // Hover effect
        addHoverEffect(btnTambah, SUCCESS);
        addHoverEffect(btnUpdate, INFO);
        addHoverEffect(btnBayar, WARNING);
        addHoverEffect(btnBatal, new Color(149, 165, 166));
        addHoverEffect(btnRefresh, PRIMARY);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Kode Booking", "Sopir", "Pelanggan", "Tujuan", "Tanggal", "Fee", "Status", "Tgl Bayar"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableAssignment = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };

        tableAssignment.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAssignment.setRowHeight(35);
        tableAssignment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableAssignment.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableAssignment.getTableHeader().setBackground(PRIMARY);
        tableAssignment.getTableHeader().setForeground(Color.WHITE);
        tableAssignment.getTableHeader().setPreferredSize(new Dimension(0, 40));
        tableAssignment.setGridColor(new Color(230, 230, 230));

        tableAssignment.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableAssignment.getColumnModel().getColumn(1).setPreferredWidth(120);
        tableAssignment.getColumnModel().getColumn(2).setPreferredWidth(180);
        tableAssignment.getColumnModel().getColumn(3).setPreferredWidth(180);
        tableAssignment.getColumnModel().getColumn(4).setPreferredWidth(150);
        tableAssignment.getColumnModel().getColumn(5).setPreferredWidth(150);
        tableAssignment.getColumnModel().getColumn(6).setPreferredWidth(100);
        tableAssignment.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableAssignment.getColumnModel().getColumn(8).setPreferredWidth(100);

        tableAssignment.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableAssignment);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "📊 Daftar Assignment Sopir"
        ));
        
        panel.add(scrollTable, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createInputField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl, BorderLayout.WEST);
        
        if (field instanceof JTextArea) {
            JScrollPane sp = new JScrollPane((JTextArea) field);
            sp.setPreferredSize(new Dimension(800, 120));
            panel.add(sp, BorderLayout.CENTER);
        } else {
            panel.add(field, BorderLayout.CENTER);
        }

        return panel;
    }
    
    // 🔥 Method khusus untuk ComboBox yang sangat TINGGI
    private JComboBox<String> createTallComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 🔥 Font lebih besar
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20) // 🔥 Padding sangat besar
        ));
        // 🔥 Ukuran sangat tinggi
        combo.setPreferredSize(new Dimension(800, 60));
        combo.setMinimumSize(new Dimension(800, 60)); // 🔥 Minimum size
        combo.setMaximumSize(new Dimension(2000, 60)); // 🔥 Maximum size
        return combo;
    }
    
    // 🔥 Method khusus untuk TextField yang besar
    private JTextField createLargeTextField() {
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
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 🔥 Font lebih besar
        field.setPreferredSize(new Dimension(800, 60));
        field.setMinimumSize(new Dimension(800, 60));
        field.setMaximumSize(new Dimension(2000, 60));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20) // 🔥 Padding sangat besar
        ));
        addFieldFocusListener(field);
        return field;
    }
    
    // 🔥 Method khusus untuk TextArea yang besar
    private JTextArea createLargeTextArea() {
        JTextArea area = new JTextArea(5, 50);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 🔥 Font lebih besar
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20) // 🔥 Padding sangat besar
        ));
        addFieldFocusListener(area);
        return area;
    }
    
    // 🔥 Method khusus untuk DateChooser yang tinggi
    private JDateChooser createTallDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 🔥 Font lebih besar
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20) // 🔥 Padding sangat besar
        ));
        dateChooser.setPreferredSize(new Dimension(800, 60));
        dateChooser.setMinimumSize(new Dimension(800, 60));
        dateChooser.setMaximumSize(new Dimension(2000, 60));
        return dateChooser;
    }
    
    private void addFieldFocusListener(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(14, 19, 14, 19)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });
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
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
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
    
    // Custom renderer untuk status
    private static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            
            String status = value.toString();
            if ("dibayar".equals(status)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(46, 107, 46));
            } else if ("belum_bayar".equals(status)) {
                setBackground(new Color(252, 248, 227));
                setForeground(new Color(153, 126, 12));
            }
            
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            return this;
        }
    }
    
    private void loadComboBoxData() {
        // Load Booking (hanya yang sudah dikonfirmasi dan belum ada assignment)
        cmbBooking.removeAllItems();
        List<Booking> bookingList = bookingDAO.getAllBooking();
        for (Booking b : bookingList) {
            if (b.getStatusBooking().equals("dikonfirmasi")) {
                // Check if already has assignment
                if (assignmentDAO.getAssignmentByBooking(b.getIdBooking()) == null) {
                    cmbBooking.addItem(b.getIdBooking() + " - " + b.getKodeBooking() + " (" + b.getNamaPelanggan() + ")");
                }
            }
        }
        
        // Load Sopir Aktif
        cmbSopir.removeAllItems();
        List<Sopir> sopirList = sopirDAO.getSopirAktif();
        for (Sopir s : sopirList) {
            cmbSopir.addItem(s.getIdSopir() + " - " + s.getNamaSopir() + " (" + s.getNoSim() + ")");
        }
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<AssignmentSopir> assignments = assignmentDAO.getAllAssignments();
        
        for (AssignmentSopir assignment : assignments) {
            Object[] row = {
                assignment.getIdAssignment(),
                assignment.getKodeBooking(),
                assignment.getNamaSopir(),
                assignment.getNamaPelanggan(),
                assignment.getTujuan(),
                dateFormat.format(assignment.getTanggalMulai()) + " - " + dateFormat.format(assignment.getTanggalSelesai()),
                currencyFormat.format(assignment.getFeeSopir()),
                assignment.getStatusBayar().equals("dibayar") ? "Dibayar" : "Belum Bayar",
                assignment.getTanggalBayar() != null ? dateFormat.format(assignment.getTanggalBayar()) : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    private void tambahAssignment() {
        if (!validateInput()) return;
        
        int idBooking = getSelectedIdFromCombo(cmbBooking);
        int idSopir = getSelectedIdFromCombo(cmbSopir);
        
        // Get tanggal booking
        java.sql.Date[] dates = assignmentDAO.getBookingDates(idBooking);
        if (dates == null) {
            JOptionPane.showMessageDialog(this, "Data booking tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cek availability sopir
        if (!assignmentDAO.isSopirAvailable(idSopir, dates[0], dates[1])) {
            JOptionPane.showMessageDialog(this, 
                "Sopir tidak tersedia!\n\nSopir ini sudah memiliki jadwal perjalanan yang bentrok.\n" +
                "Tanggal: " + new SimpleDateFormat("dd/MM/yyyy").format(dates[0]) + 
                " - " + new SimpleDateFormat("dd/MM/yyyy").format(dates[1]) +
                "\n\nSilakan pilih sopir lain atau tunggu jadwal sopir ini selesai.", 
                "Sopir Tidak Tersedia", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        AssignmentSopir assignment = new AssignmentSopir();
        assignment.setIdBooking(idBooking);
        assignment.setIdSopir(idSopir);
        assignment.setFeeSopir(Double.parseDouble(txtFeeSopir.getText().trim()));
        assignment.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        assignment.setKeterangan(txtKeterangan.getText().trim());
        
        if (assignmentDAO.tambahAssignment(assignment)) {
            JOptionPane.showMessageDialog(this, 
                "✅ Sopir berhasil di-assign!", 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            loadComboBoxData();
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal assign sopir!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateAssignment() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Pilih assignment yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        AssignmentSopir assignment = new AssignmentSopir();
        assignment.setIdAssignment(selectedId);
        assignment.setIdSopir(getSelectedIdFromCombo(cmbSopir));
        assignment.setFeeSopir(Double.parseDouble(txtFeeSopir.getText().trim()));
        assignment.setStatusBayar(cmbStatusBayar.getSelectedItem().toString());
        assignment.setTanggalBayar(dateBayar.getDate());
        assignment.setKeterangan(txtKeterangan.getText().trim());
        
        if (assignmentDAO.updateAssignment(assignment)) {
            JOptionPane.showMessageDialog(this, 
                "✅ Assignment berhasil diupdate!", 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal update assignment!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void bayarSopir() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Pilih assignment yang akan dibayar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Konfirmasi pembayaran fee sopir?", 
            "Konfirmasi Bayar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (assignmentDAO.updateStatusBayar(selectedId, "dibayar", new java.sql.Date(System.currentTimeMillis()))) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Pembayaran berhasil!", 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal melakukan pembayaran!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void selectRow() {
        int row = tableAssignment.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            AssignmentSopir assignment = assignmentDAO.getAssignmentById(selectedId);
            
            if (assignment != null) {
                txtFeeSopir.setText(String.valueOf(assignment.getFeeSopir()));
                cmbStatusBayar.setSelectedItem(assignment.getStatusBayar());
                dateBayar.setDate(assignment.getTanggalBayar());
                txtKeterangan.setText(assignment.getKeterangan());
                
                cmbBooking.setEnabled(false);
                setButtonState(true);
            }
        }
    }
    
    private void clearForm() {
        txtFeeSopir.setText("");
        txtKeterangan.setText("");
        dateBayar.setDate(null);
        cmbBooking.setSelectedIndex(-1);
        cmbSopir.setSelectedIndex(-1);
        cmbStatusBayar.setSelectedIndex(0);
        selectedId = -1;
        tableAssignment.clearSelection();
        cmbBooking.setEnabled(true);
        setButtonState(false);
    }
    
    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnBayar.setEnabled(isUpdate);
    }
    
    private int getSelectedIdFromCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        if (selected != null) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }
    
    private boolean validateInput() {
        if (cmbBooking.getSelectedItem() == null || cmbSopir.getSelectedItem() == null ||
            txtFeeSopir.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Booking, Sopir, dan Fee wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Double.parseDouble(txtFeeSopir.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Fee harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (txtFeeSopir.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Fee wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}