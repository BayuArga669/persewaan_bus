//package view;
//
//import dao.AssignmentSopirDAO;
//import dao.SopirDAO;
//import model.AssignmentSopir;
//import model.Sopir;
//import util.SessionManager;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.JTableHeader;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.geom.RoundRectangle2D;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class DashboardSopir extends JFrame {
//    // PERBAIKAN: Ubah tipe data dari JLabel ke JPanel karena createModernStatCard sekarang mengembalikan JPanel
//    private JPanel pnlTotalPendapatan, pnlBelumDibayar, pnlSudahDibayar;
//    private JTable tableOrders;
//    private DefaultTableModel tableModel;
//    private JButton btnRefresh, btnLogout;
//    private JComboBox<String> cmbFilterStatus;
//    private AssignmentSopirDAO assignmentDAO;
//    private SopirDAO sopirDAO;
//    private Sopir currentSopir;
//    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
//    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//    
//    // Modern color scheme
//    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
//    private final Color SECONDARY_COLOR = new Color(230, 126, 34);
//    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
//    private final Color DANGER_COLOR = new Color(231, 76, 60);
//    private final Color WARNING_COLOR = new Color(241, 196, 15);
//    private final Color INFO_COLOR = new Color(52, 152, 219);
//    private final Color LIGHT_COLOR = new Color(248, 249, 250);
//    private final Color DARK_COLOR = new Color(44, 62, 80);
//    private final Color BACKGROUND_COLOR = new Color(240, 242, 245);
//    
//    public DashboardSopir() {
//        assignmentDAO = new AssignmentSopirDAO();
//        sopirDAO = new SopirDAO();
//        currentSopir = sopirDAO.getSopirByUserId(SessionManager.getCurrentUserId());
//        initComponents();
//        loadStatistics();
//        loadOrders();
//        setLocationRelativeTo(null);
//    }
//    
//    private void initComponents() {
//        setTitle("Dashboard Sopir - Aplikasi Penyewaan Bus");
//        setSize(1200, 750);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        
//        // Main Panel with modern background
//        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//        mainPanel.setBackground(BACKGROUND_COLOR);
//        
//        // Modern Header Panel with gradient
//        JPanel headerPanel = createHeaderPanel();
//        
//        // Statistics Panel with modern cards
//        JPanel statsPanel = createStatsPanel();
//        
//        // Filter Panel with modern design
//        JPanel filterPanel = createFilterPanel();
//        
//        // Modern Table
//        JPanel tablePanel = createTablePanel();
//        
//        // Layout components
//        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
//        centerPanel.setBackground(BACKGROUND_COLOR);
//        centerPanel.add(statsPanel, BorderLayout.NORTH);
//        centerPanel.add(filterPanel, BorderLayout.CENTER);
//        centerPanel.add(tablePanel, BorderLayout.SOUTH);
//        
//        mainPanel.add(headerPanel, BorderLayout.NORTH);
//        mainPanel.add(centerPanel, BorderLayout.CENTER);
//        add(mainPanel);
//        
//        // Event Listeners
//        btnRefresh.addActionListener(e -> {
//            loadStatistics();
//            loadOrders();
//        });
//        
//        cmbFilterStatus.addActionListener(e -> loadOrders());
//        
//        btnLogout.addActionListener(e -> logout());
//    }
//    
//    private JPanel createHeaderPanel() {
//        JPanel headerPanel = new JPanel(new BorderLayout()) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                
//                // Create gradient
//                GradientPaint gradient = new GradientPaint(
//                    0, 0, SECONDARY_COLOR,
//                    getWidth(), getHeight(), new Color(192, 57, 43)
//                );
//                g2.setPaint(gradient);
//                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
//            }
//        };
//        headerPanel.setPreferredSize(new Dimension(1200, 100));
//        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
//        headerPanel.setOpaque(false);
//        
//        // User info panel
//        JPanel userInfoPanel = new JPanel();
//        userInfoPanel.setOpaque(false);
//        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
//        
//        JLabel lblWelcome = new JLabel("Selamat Datang, " + SessionManager.getCurrentUserFullName());
//        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
//        lblWelcome.setForeground(Color.WHITE);
//        
//        JLabel lblRole = new JLabel("Role: Sopir" + (currentSopir != null ? " | SIM: " + currentSopir.getNoSim() : ""));
//        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        lblRole.setForeground(new Color(255, 255, 255, 200));
//        
//        userInfoPanel.add(lblWelcome);
//        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
//        userInfoPanel.add(lblRole);
//        
//        // Modern logout button
//        btnLogout = createModernButton("Logout", DANGER_COLOR);
//        
//        headerPanel.add(userInfoPanel, BorderLayout.WEST);
//        headerPanel.add(btnLogout, BorderLayout.EAST);
//        
//        return headerPanel;
//    }
//    
//    private JPanel createStatsPanel() {
//        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
//        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        statsPanel.setBackground(BACKGROUND_COLOR);
//        
//        // PERBAIKAN: Simpan referensi ke JPanel yang dikembalikan
//        pnlTotalPendapatan = createModernStatCard("💰 Total Pendapatan", "Rp 0", INFO_COLOR);
//        pnlBelumDibayar = createModernStatCard("⏳ Belum Dibayar", "Rp 0", WARNING_COLOR);
//        pnlSudahDibayar = createModernStatCard("✅ Sudah Dibayar", "Rp 0", SUCCESS_COLOR);
//        
//        statsPanel.add(pnlTotalPendapatan);
//        statsPanel.add(pnlBelumDibayar);
//        statsPanel.add(pnlSudahDibayar);
//        
//        return statsPanel;
//    }
//    
//    // PERBAIKAN: Ubah tipe return dari JLabel ke JPanel
//    private JPanel createModernStatCard(String title, String value, Color color) {
//        JPanel cardPanel = new JPanel(new BorderLayout()) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                
//                // Draw shadow
//                g2.setColor(new Color(0, 0, 0, 30));
//                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 15, 15);
//                
//                // Draw card background
//                g2.setColor(Color.WHITE);
//                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 15, 15);
//                
//                // Draw accent line
//                g2.setColor(color);
//                g2.fillRoundRect(0, 0, 5, getHeight()-10, 15, 15);
//            }
//        };
//        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//        cardPanel.setOpaque(false);
//        
//        JLabel titleLabel = new JLabel(title);
//        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
//        titleLabel.setForeground(DARK_COLOR);
//        
//        JLabel valueLabel = new JLabel(value);
//        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
//        valueLabel.setForeground(color);
//        // PERBAIKAN: Beri nama pada valueLabel untuk memudahkan pencarian
//        valueLabel.setName("valueLabel");
//
//        cardPanel.add(titleLabel, BorderLayout.NORTH);
//        cardPanel.add(valueLabel, BorderLayout.CENTER);
//        
//        return cardPanel;
//    }
//    
//    private JPanel createFilterPanel() {
//        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15)) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                
//                // Draw shadow
//                g2.setColor(new Color(0, 0, 0, 15));
//                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 10, 10);
//                
//                // Draw panel background
//                g2.setColor(Color.WHITE);
//                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 10, 10);
//            }
//        };
//        filterPanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createEmptyBorder(10, 15, 10, 15), 
//            "🔍 Filter Data",
//            Font.BOLD | Font.ITALIC, 
//            0, 
//            new Font("Segoe UI", Font.PLAIN, 14),
//            DARK_COLOR
//        ));
//        filterPanel.setOpaque(false);
//        
//        JLabel filterLabel = new JLabel("Status Pembayaran:");
//        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        filterPanel.add(filterLabel);
//        
//        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "belum_bayar", "dibayar"});
//        cmbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        cmbFilterStatus.setBackground(Color.WHITE);
//        cmbFilterStatus.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(200, 200, 200)),
//            BorderFactory.createEmptyBorder(5, 10, 5, 10)
//        ));
//        filterPanel.add(cmbFilterStatus);
//        
//        btnRefresh = createModernButton("🔄 Refresh", PRIMARY_COLOR);
//        filterPanel.add(btnRefresh);
//        
//        return filterPanel;
//    }
//    
//    private JPanel createTablePanel() {
//        JPanel tablePanel = new JPanel(new BorderLayout()) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                
//                // Draw shadow
//                g2.setColor(new Color(0, 0, 0, 15));
//                g2.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 10, 10);
//                
//                // Draw panel background
//                g2.setColor(Color.WHITE);
//                g2.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 10, 10);
//            }
//        };
//        tablePanel.setBorder(BorderFactory.createTitledBorder(
//            BorderFactory.createEmptyBorder(10, 15, 10, 15), 
//            "📋 Data Pesanan",
//            Font.BOLD | Font.ITALIC, 
//            0, 
//            new Font("Segoe UI", Font.PLAIN, 14),
//            DARK_COLOR
//        ));
//        tablePanel.setOpaque(false);
//        
//        // Create modern table
//        String[] columns = {"ID", "Kode Booking", "Tanggal", "Tujuan", "Bus", "Pelanggan", "Fee Sopir", "Status Bayar", "Tgl Bayar"};
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        
//        tableOrders = new JTable(tableModel) {
//            @Override
//            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
//                Component c = super.prepareRenderer(renderer, row, column);
//                if (!isRowSelected(row)) {
//                    c.setBackground(row % 2 == 0 ? Color.WHITE : LIGHT_COLOR);
//                }
//                return c;
//            }
//        };
//        
//        tableOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        tableOrders.setRowHeight(30);
//        tableOrders.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        tableOrders.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
//        tableOrders.getTableHeader().setPreferredSize(new Dimension(0, 40));
//        tableOrders.getTableHeader().setBackground(PRIMARY_COLOR);
//        tableOrders.getTableHeader().setForeground(Color.WHITE);
//        
//        // Set column widths
//        tableOrders.getColumnModel().getColumn(0).setPreferredWidth(40);
//        tableOrders.getColumnModel().getColumn(1).setPreferredWidth(100);
//        tableOrders.getColumnModel().getColumn(3).setPreferredWidth(150);
//        
//        JScrollPane scrollTable = new JScrollPane(tableOrders);
//        scrollTable.setBorder(BorderFactory.createEmptyBorder());
//        scrollTable.getViewport().setBackground(Color.WHITE);
//        
//        tablePanel.add(scrollTable, BorderLayout.CENTER);
//        
//        return tablePanel;
//    }
//    
//    private JButton createModernButton(String text, Color bgColor) {
//        JButton button = new JButton(text) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g;
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                
//                // Draw button shadow
//                g2.setColor(new Color(0, 0, 0, 30));
//                g2.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 8, 8);
//                
//                // Draw button background
//                g2.setColor(bgColor);
//                g2.fillRoundRect(0, 0, getWidth()-6, getHeight()-6, 8, 8);
//                
//                // Draw text
//                super.paintComponent(g);
//            }
//        };
//        
//        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        button.setForeground(Color.WHITE);
//        button.setContentAreaFilled(false);
//        button.setBorderPainted(false);
//        button.setFocusPainted(false);
//        button.setOpaque(false);
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
//        
//        // Add hover effect
//        button.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                button.setBackground(bgColor.darker());
//                button.repaint();
//            }
//            
//            @Override
//            public void mouseExited(MouseEvent e) {
//                button.setBackground(bgColor);
//                button.repaint();
//            }
//        });
//        
//        return button;
//    }
//    
//    private void loadStatistics() {
//        if (currentSopir == null) {
//            JOptionPane.showMessageDialog(this, 
//                "Data sopir tidak ditemukan!", 
//                "Error", 
//                JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        
//        double totalPendapatan = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), null);
//        double belumDibayar = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), "belum_bayar");
//        double sudahDibayar = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), "dibayar");
//        
//        // PERBAIKAN: Panggil metode updateStatCard dengan parameter yang benar
//        updateStatCard(pnlTotalPendapatan, currencyFormat.format(totalPendapatan));
//        updateStatCard(pnlBelumDibayar, currencyFormat.format(belumDibayar));
//        updateStatCard(pnlSudahDibayar, currencyFormat.format(sudahDibayar));
//    }
//    
//    // PERBAIKAN: Ubah parameter dari JLabel ke JPanel
//    private void updateStatCard(JPanel cardPanel, String value) {
//        // PERBAIKAN: Cari label berdasarkan nama yang sudah diset
//        for (Component comp : cardPanel.getComponents()) {
//            if (comp instanceof JLabel) {
//                JLabel label = (JLabel) comp;
//                if ("valueLabel".equals(label.getName())) {
//                    label.setText(value);
//                    break;
//                }
//            }
//        }
//    }
//    
//    private void loadOrders() {
//        if (currentSopir == null) return;
//        
//        tableModel.setRowCount(0);
//        List<AssignmentSopir> assignments = assignmentDAO.getAssignmentsBySopir(currentSopir.getIdSopir());
//        
//        String filterStatus = cmbFilterStatus.getSelectedItem().toString();
//        
//        for (AssignmentSopir assignment : assignments) {
//            // Apply filter
//            if (!filterStatus.equals("Semua") && !assignment.getStatusBayar().equals(filterStatus)) {
//                continue;
//            }
//            
//            Object[] row = {
//                assignment.getIdAssignment(),
//                assignment.getKodeBooking(),
//                dateFormat.format(assignment.getTanggalMulai()) + " - " + dateFormat.format(assignment.getTanggalSelesai()),
//                assignment.getTujuan(),
//                assignment.getNoPolisi(),
//                assignment.getNamaPelanggan(),
//                currencyFormat.format(assignment.getFeeSopir()),
//                assignment.getStatusBayar().equals("dibayar") ? "Dibayar" : "Belum Dibayar",
//                assignment.getTanggalBayar() != null ? dateFormat.format(assignment.getTanggalBayar()) : "-"
//            };
//            tableModel.addRow(row);
//        }
//    }
//    
//    private void logout() {
//        int confirm = JOptionPane.showConfirmDialog(this, 
//            "Apakah Anda yakin ingin logout?", 
//            "Konfirmasi Logout", 
//            JOptionPane.YES_NO_OPTION);
//        
//        if (confirm == JOptionPane.YES_OPTION) {
//            SessionManager.logout();
//            this.dispose();
//            new FormLogin().setVisible(true);
//        }
//    }
//}