package view;

import dao.AssignmentSopirDAO;
import dao.SopirDAO;
import model.AssignmentSopir;
import model.Sopir;
import util.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DashboardSopir extends JFrame {
    private JLabel lblWelcome, lblTotalPendapatan, lblBelumDibayar, lblSudahDibayar;
    private JTable tableOrders;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnLogout;
    private JComboBox<String> cmbFilterStatus;
    private AssignmentSopirDAO assignmentDAO;
    private SopirDAO sopirDAO;
    private Sopir currentSopir;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public DashboardSopir() {
        assignmentDAO = new AssignmentSopirDAO();
        sopirDAO = new SopirDAO();
        currentSopir = sopirDAO.getSopirByUserId(SessionManager.getCurrentUserId());
        initComponents();
        loadStatistics();
        loadOrders();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Dashboard Sopir - Aplikasi Penyewaan Bus");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(230, 126, 34));
        headerPanel.setPreferredSize(new Dimension(1100, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setBackground(new Color(230, 126, 34));
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        
        lblWelcome = new JLabel("Selamat Datang, " + SessionManager.getCurrentUserFullName());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22));
        lblWelcome.setForeground(Color.WHITE);
        
        JLabel lblRole = new JLabel("Role: Sopir" + (currentSopir != null ? " | SIM: " + currentSopir.getNoSim() : ""));
        lblRole.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRole.setForeground(Color.WHITE);
        
        userInfoPanel.add(lblWelcome);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(lblRole);
        
        btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogout.setFocusPainted(false);
        
        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.setBackground(Color.WHITE);
        
        lblTotalPendapatan = createStatCard("Total Pendapatan", "Rp 0", new Color(52, 152, 219));
        lblBelumDibayar = createStatCard("Belum Dibayar", "Rp 0", new Color(231, 76, 60));
        lblSudahDibayar = createStatCard("Sudah Dibayar", "Rp 0", new Color(46, 204, 113));
        
        statsPanel.add(lblTotalPendapatan);
        statsPanel.add(lblBelumDibayar);
        statsPanel.add(lblSudahDibayar);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        
        filterPanel.add(new JLabel("Status Pembayaran:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"Semua", "belum_bayar", "dibayar"});
        filterPanel.add(cmbFilterStatus);
        
        btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        filterPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Kode Booking", "Tanggal", "Tujuan", "Bus", "Pelanggan", "Fee Sopir", "Status Bayar", "Tgl Bayar"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableOrders = new JTable(tableModel);
        tableOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableOrders.setRowHeight(25);
        tableOrders.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableOrders.getColumnModel().getColumn(1).setPreferredWidth(100);
        tableOrders.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        JScrollPane scrollTable = new JScrollPane(tableOrders);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(filterPanel, BorderLayout.NORTH);
        bottomPanel.add(scrollTable, BorderLayout.CENTER);
        
        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(bottomPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Event Listeners
        btnRefresh.addActionListener(e -> {
            loadStatistics();
            loadOrders();
        });
        
        cmbFilterStatus.addActionListener(e -> loadOrders());
        
        btnLogout.addActionListener(e -> logout());
    }
    
    private JLabel createStatCard(String title, String value, Color color) {
        JLabel card = new JLabel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(true);
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblValue);
        card.add(Box.createVerticalGlue());
        
        return card;
    }
    
    private void loadStatistics() {
        if (currentSopir == null) {
            JOptionPane.showMessageDialog(this, 
                "Data sopir tidak ditemukan!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double totalPendapatan = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), null);
        double belumDibayar = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), "belum_bayar");
        double sudahDibayar = assignmentDAO.getTotalPendapatanSopir(currentSopir.getIdSopir(), "dibayar");
        
        updateStatCard(lblTotalPendapatan, "Total Pendapatan", currencyFormat.format(totalPendapatan));
        updateStatCard(lblBelumDibayar, "Belum Dibayar", currencyFormat.format(belumDibayar));
        updateStatCard(lblSudahDibayar, "Sudah Dibayar", currencyFormat.format(sudahDibayar));
    }
    
    private void updateStatCard(JLabel card, String title, String value) {
        Component[] components = ((JLabel) card).getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if (label.getFont().getSize() == 20) {
                    label.setText(value);
                }
            }
        }
    }
    
    private void loadOrders() {
        if (currentSopir == null) return;
        
        tableModel.setRowCount(0);
        List<AssignmentSopir> assignments = assignmentDAO.getAssignmentsBySopir(currentSopir.getIdSopir());
        
        String filterStatus = cmbFilterStatus.getSelectedItem().toString();
        
        for (AssignmentSopir assignment : assignments) {
            // Apply filter
            if (!filterStatus.equals("Semua") && !assignment.getStatusBayar().equals(filterStatus)) {
                continue;
            }
            
            Object[] row = {
                assignment.getIdAssignment(),
                assignment.getKodeBooking(),
                dateFormat.format(assignment.getTanggalMulai()) + " - " + dateFormat.format(assignment.getTanggalSelesai()),
                assignment.getTujuan(),
                assignment.getNoPolisi(),
                assignment.getNamaPelanggan(),
                currencyFormat.format(assignment.getFeeSopir()),
                assignment.getStatusBayar().equals("dibayar") ? "Dibayar" : "Belum Dibayar",
                assignment.getTanggalBayar() != null ? dateFormat.format(assignment.getTanggalBayar()) : "-"
            };
            tableModel.addRow(row);
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            new FormLogin().setVisible(true);
        }
    }
}