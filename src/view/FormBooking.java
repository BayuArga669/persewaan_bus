package view;

import dao.*;
import model.*;
import service.BookingService;
import util.SessionManager;
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
    private BookingService bookingService;
    private int selectedId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    
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
    private final Color MEDIUM_GRAY = new Color(200, 200, 200);
    private final Color FOCUS_BLUE = new Color(60, 140, 200);
    
    public FormBooking() {
        bookingDAO = new BookingDAO();
        pelangganDAO = new PelangganDAO();
        busDAO = new BusDAO();
        bookingService = new BookingService();
        initComponents();
        loadComboBoxData();
        loadData();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Kelola Booking & Transaksi");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Header Panel dengan gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Form Panel (tanpa tab)
        JPanel formPanel = createBookingPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Layout
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
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

        JLabel titleLabel = new JLabel("🚌 MANAJEMEN BOOKING & TRANSAKSI");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Kelola pemesanan dan transaksi bus");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }
    
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                
                g2.setColor(new Color(0, 0, 0, 8));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 12, 12));
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "📋 Form Input Booking"
        ));
        panel.setPreferredSize(new Dimension(0, 380));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 12, 6, 12);
        
        // ✅ ROW 0: Kode Booking — DIPERBESAR & MENONJOL
        gbc.gridx = 0; gbc.gridy = 0;
        txtKodeBooking = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(150, 150, 150));
                    g2.setFont(getFont().deriveFont(Font.BOLD | Font.ITALIC, 13));
                    g2.drawString("Contoh: BK-2025-001", getInsets().left + 6, getHeight()/2 + 3);
                }
            }
        };
        txtKodeBooking.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtKodeBooking.setEditable(false);
        txtKodeBooking.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MEDIUM_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        addFieldFocusListener(txtKodeBooking, true); // khusus kode booking
        panel.add(createInputField("📋 Kode Booking", txtKodeBooking), gbc);

        // Status
        gbc.gridx = 1;
        panel.add(createInputField("📊 Status", cmbStatus = createStatusComboBox()), gbc);

        // Row 1 - Pelanggan
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(createInputField("👤 Pelanggan", cmbPelanggan = createComboBox()), gbc);
        gbc.gridwidth = 1;

        // Row 2 - Bus
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(createInputField("🚌 Bus", cmbBus = createComboBox()), gbc);
        gbc.gridwidth = 1;

        // Row 3 - Tanggal Mulai & Tanggal Selesai — UKURAN SAMA PERSIS
        gbc.gridx = 0; gbc.gridy = 3;
        dateStart = createDateChooser();
        panel.add(createInputField("📅 Tgl Mulai", dateStart), gbc);

        gbc.gridx = 1;
        dateEnd = createDateChooser();
        panel.add(createInputField("📅 Tgl Selesai", dateEnd), gbc);

        // Row 4 - Tujuan & Jumlah Penumpang
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createInputField("📍 Tujuan", txtTujuan = createTextField(true)), gbc);

        gbc.gridx = 1;
        panel.add(createInputField("👥 Jml Penumpang", txtJumlahPenumpang = createTextField(true)), gbc);

        // Row 5 - Lama Sewa & Total Harga
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(createInputField("⏱️ Lama Sewa (hari)", txtLamaSewa = createTextField(false)), gbc);

        gbc.gridx = 1;
        panel.add(createInputField("💰 Total Harga", txtTotalHarga = createTextField(false)), gbc);

        // Row 6 - Catatan
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(createInputField("📝 Catatan", txtCatatan = createTextArea()), gbc);
        gbc.gridwidth = 1;

        // Row 7 - Buttons
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));

        btnTambah = createModernButton("➕ Buat", SUCCESS);
        btnUpdate = createModernButton("✏️ Ubah", INFO);
        btnHapus = createModernButton("🗑️ Hapus", DANGER);
        btnBatal = createModernButton("↩️ Batal", new Color(149, 165, 166));
        btnRefresh = createModernButton("⟳ Segarkan", WARNING);
        btnHitung = createModernButton("💱 Hitung", new Color(230, 126, 34));

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        buttonPanel.add(btnHitung);
        buttonPanel.add(btnRefresh);
        panel.add(buttonPanel, gbc);

        // Hover effect
        addHoverEffect(btnTambah, SUCCESS);
        addHoverEffect(btnUpdate, INFO);
        addHoverEffect(btnHapus, DANGER);
        addHoverEffect(btnBatal, new Color(149, 165, 166));
        addHoverEffect(btnRefresh, WARNING);
        addHoverEffect(btnHitung, new Color(230, 126, 34));

        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"ID", "Kode", "Pelanggan", "Bus", "Tgl Mulai", "Tgl Selesai", "Tujuan", "Lama", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableBooking = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };

        tableBooking.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableBooking.setRowHeight(30);
        tableBooking.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableBooking.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableBooking.getTableHeader().setBackground(PRIMARY);
        tableBooking.getTableHeader().setForeground(Color.WHITE);
        tableBooking.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tableBooking.setGridColor(new Color(235, 235, 235));

        int[] widths = {40, 90, 150, 150, 80, 80, 120, 60, 100, 90};
        for (int i = 0; i < widths.length; i++) {
            tableBooking.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        tableBooking.getColumnModel().getColumn(9).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollTable = new JScrollPane(tableBooking);
        scrollTable.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            "📊 Daftar Booking"
        ));
        
        panel.add(scrollTable, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createInputField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JLabel lbl = new JLabel(label);
        // Jika field adalah txtKodeBooking, gunakan font lebih besar
        if (field == txtKodeBooking) {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        } else {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
        panel.add(lbl, BorderLayout.WEST);
        
        if (field instanceof JTextArea) {
            JTextArea textArea = (JTextArea) field;
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(250, 50));
            panel.add(scrollPane, BorderLayout.CENTER);
            return panel;
        }
        
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
    
    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(getFont().deriveFont(Font.ITALIC, 11));
                    g2.drawString("Ketik di sini...", getInsets().left + 4, getHeight()/2 + 2);
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setEditable(editable);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        addFieldFocusListener(field);
        return field;
    }
    
    private JTextArea createTextArea() {
        JTextArea area = new JTextArea(2, 25);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        addFieldFocusListener(area);
        return area;
    }
    
    private JComboBox<String> createComboBox() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(250, 26));
        return combo;
    }
    
    private JComboBox<String> createStatusComboBox() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"pending", "dikonfirmasi", "selesai", "dibatalkan"});
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        combo.setPreferredSize(new Dimension(250, 26));
        return combo;
    }
    
    private JDateChooser createDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // ✅ UKURAN EKSAK & KONSISTEN
        Dimension size = new Dimension(250, 26);
        dateChooser.setPreferredSize(size);
        dateChooser.setMinimumSize(size);
        dateChooser.setMaximumSize(size);
        return dateChooser;
    }
    
    // ⭐ Versi standar (untuk field biasa)
    private void addFieldFocusListener(JComponent field) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
    }
    
    // ⭐ Versi khusus untuk Kode Booking (lebih tebal & mencolok)
    private void addFieldFocusListener(JComponent field, boolean isKodeBooking) {
        if (isKodeBooking) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FOCUS_BLUE, 2),
                        BorderFactory.createEmptyBorder(7, 11, 7, 11)
                    ));
                }
                @Override
                public void focusLost(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(MEDIUM_GRAY, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                }
            });
        } else {
            addFieldFocusListener(field);
        }
    }
    
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(baseColor.darker().darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(button.getModel().isRollover() ? baseColor.darker() : baseColor);
            }
        });
    }
    
    private static class StatusCellRenderer extends JLabel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            setOpaque(true);
            
            String status = value.toString();
            if ("pending".equals(status)) {
                setBackground(new Color(252, 248, 227));
                setForeground(new Color(153, 126, 12));
            } else if ("dikonfirmasi".equals(status)) {
                setBackground(new Color(217, 237, 247));
                setForeground(new Color(49, 112, 143));
            } else if ("selesai".equals(status)) {
                setBackground(new Color(223, 240, 216));
                setForeground(new Color(46, 107, 46));
            } else if ("dibatalkan".equals(status)) {
                setBackground(new Color(248, 215, 218));
                setForeground(new Color(169, 68, 66));
            }
            
            setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            return this;
        }
    }
    
    private void loadComboBoxData() {
        cmbPelanggan.removeAllItems();
        List<Pelanggan> pelangganList = pelangganDAO.getAllPelanggan();
        for (Pelanggan p : pelangganList) {
            cmbPelanggan.addItem(p.getIdPelanggan() + " - " + p.getNamaPelanggan());
        }
        
        cmbBus.removeAllItems();
        List<Bus> busList = busDAO.getBusTersedia();
        for (Bus b : busList) {
            cmbBus.addItem(b.getIdBus() + " - " + b.getNoPolisi() + " (" + b.getTipeBus() + ")");
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
                new SimpleDateFormat("dd/MM/yy").format(booking.getTanggalMulai()),
                new SimpleDateFormat("dd/MM/yy").format(booking.getTanggalSelesai()),
                booking.getTujuan(),
                booking.getLamaSewa(),
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
        booking.setTotalHarga(Double.parseDouble(txtTotalHarga.getText()
            .replace("Rp", "").replace(".", "").replace(",", ".").trim()));
        booking.setStatusBooking(cmbStatus.getSelectedItem().toString());
        booking.setCatatan(txtCatatan.getText().trim());
        
        if (bookingDAO.tambahBooking(booking)) {
            busDAO.updateStatusBus(booking.getIdBus(), "disewa");
            
            JOptionPane.showMessageDialog(this, 
                "✅ Booking berhasil dibuat!\nKode: " + booking.getKodeBooking(), 
                "Sukses", 
                JOptionPane.INFORMATION_MESSAGE);
            loadData();
            loadComboBoxData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal membuat booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateBooking() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateInputUpdate()) return;
        
        Booking oldBooking = bookingDAO.getBookingById(selectedId);
        if (oldBooking == null) return;
        
        Booking booking = new Booking();
        booking.setIdBooking(selectedId);
        booking.setIdBus(oldBooking.getIdBus());
        booking.setTanggalMulai(dateStart.getDate());
        booking.setTanggalSelesai(dateEnd.getDate());
        booking.setTujuan(txtTujuan.getText().trim());
        booking.setJumlahPenumpang(Integer.parseInt(txtJumlahPenumpang.getText().trim()));
        booking.setLamaSewa(Integer.parseInt(txtLamaSewa.getText().trim()));
        booking.setTotalHarga(Double.parseDouble(txtTotalHarga.getText()
            .replace("Rp", "").replace(".", "").replace(",", ".").trim()));
        booking.setStatusBooking(cmbStatus.getSelectedItem().toString());
        booking.setCatatan(txtCatatan.getText().trim());
        
        if (bookingDAO.updateBooking(booking)) {
            String msg = "✅ Update berhasil!";
            String status = booking.getStatusBooking();
            if ("selesai".equals(status) || "dibatalkan".equals(status)) {
                busDAO.updateStatusBus(booking.getIdBus(), "tersedia");
                msg += "\nBus dikembalikan ke status 'tersedia'.";
            } else if ("dikonfirmasi".equals(status)) {
                busDAO.updateStatusBus(booking.getIdBus(), "disewa");
            }
            JOptionPane.showMessageDialog(this, msg, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            loadComboBoxData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal mengupdate booking!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hapusBooking() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih booking yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Ingin menghapus booking ini?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, 
                "ℹ️ Hapus booking dinonaktifkan.\nUbah status ke 'dibatalkan' saja.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void hitungLamaSewa() {
        if (dateStart.getDate() != null && dateEnd.getDate() != null) {
            long diff = dateEnd.getDate().getTime() - dateStart.getDate().getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            txtLamaSewa.setText(days > 0 ? String.valueOf(days) : "0");
        }
    }
    
    private void hitungTotal() {
        if (cmbBus.getSelectedItem() == null || txtLamaSewa.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bus dan isi lama sewa!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idBus = getSelectedIdFromCombo(cmbBus);
        Bus bus = busDAO.getBusById(idBus);
        
        if (bus != null) {
            int lama = Integer.parseInt(txtLamaSewa.getText());
            double total = bus.getHargaPerHari() * lama;
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
        txtKodeBooking.setText(bookingDAO.generateKodeBooking()); // ✅ Auto-generate kode baru
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
        if (selected != null && selected.contains(" - ")) {
            try {
                return Integer.parseInt(selected.split(" - ")[0]);
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
    
    private boolean validateInput() {
        if (cmbPelanggan.getSelectedItem() == null || cmbBus.getSelectedItem() == null ||
            dateStart.getDate() == null || dateEnd.getDate() == null ||
            txtTujuan.getText().trim().isEmpty() || txtJumlahPenumpang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            Integer.parseInt(txtJumlahPenumpang.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Jumlah penumpang harus angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private boolean validateInputUpdate() {
        if (dateStart.getDate() == null || dateEnd.getDate() == null ||
            txtTujuan.getText().trim().isEmpty() || txtJumlahPenumpang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Semua field wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}