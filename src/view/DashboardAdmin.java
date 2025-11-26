package view;

import service.BookingService;
import util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardAdmin extends JFrame {
    private JLabel lblWelcome, lblRole;
    private JButton btnManageBus, btnManageUser, btnManageBooking;
    private JButton btnManagePelanggan, btnManageSopir, btnManagePembayaran, btnLaporan, btnLogout;
    private BookingService bookingService;
    
    public DashboardAdmin() {
        bookingService = new BookingService();
        initComponents();
        autoUpdateBookingStatus(); // Auto update saat dashboard dibuka
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Dashboard Admin - Aplikasi Penyewaan Bus");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(850, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setBackground(new Color(41, 128, 185));
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        
        lblWelcome = new JLabel("Selamat Datang, " + SessionManager.getCurrentUserFullName());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        
        lblRole = new JLabel("Role: Administrator");
        lblRole.setFont(new Font("Arial", Font.PLAIN, 16));
        lblRole.setForeground(Color.WHITE);
        
        userInfoPanel.add(Box.createVerticalGlue());
        userInfoPanel.add(lblWelcome);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(lblRole);
        userInfoPanel.add(Box.createVerticalGlue());
        
        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(3, 3, 20, 20));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Create Menu Buttons
        btnManageBus = createMenuButton("Kelola Bus", new Color(52, 152, 219));
        btnManageUser = createMenuButton("Kelola User", new Color(155, 89, 182));
        btnManageBooking = createMenuButton("Kelola Booking", new Color(46, 204, 113));
        btnManagePelanggan = createMenuButton("Kelola Pelanggan", new Color(241, 196, 15));
        btnManageSopir = createMenuButton("Assignment Sopir", new Color(230, 126, 34));
        btnManagePembayaran = createMenuButton("Kelola Pembayaran", new Color(52, 73, 94));
        btnLaporan = createMenuButton("Laporan Keuangan", new Color(26, 188, 156));
        btnLogout = createMenuButton("Logout", new Color(231, 76, 60));
        
        menuPanel.add(btnManageBus);
        menuPanel.add(btnManageUser);
        menuPanel.add(btnManageBooking);
        menuPanel.add(btnManagePelanggan);
        menuPanel.add(btnManageSopir);
        menuPanel.add(btnManagePembayaran);
        menuPanel.add(btnLaporan);
        menuPanel.add(btnLogout);
        menuPanel.add(new JLabel()); // Empty cell
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Event Listeners
        btnManageBus.addActionListener(e -> openManageBus());
        btnManageUser.addActionListener(e -> openManageUser());
        btnManageBooking.addActionListener(e -> openManageBooking());
        btnManagePelanggan.addActionListener(e -> openManagePelanggan());
        btnManageSopir.addActionListener(e -> openManageSopir());
        btnManagePembayaran.addActionListener(e -> openManagePembayaran());
        btnLaporan.addActionListener(e -> openLaporan());
        btnLogout.addActionListener(e -> logout());
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void openManageBus() {
        new FormBus().setVisible(true);
    }
    
    private void openManageUser() {
        new FormUser().setVisible(true);
    }
    
    private void openManageBooking() {
        new FormBooking().setVisible(true);
    }
    
    private void openManagePelanggan() {
        new FormPelanggan().setVisible(true);
    }
    
    private void openManageSopir() {
        new FormAssignmentSopir().setVisible(true);
    }
    
    private void openManagePembayaran() {
        new FormPembayaran().setVisible(true);
    }
    
    private void openLaporan() {
        new FormLaporan().setVisible(true);
    }
    
    private void autoUpdateBookingStatus() {
        bookingService.autoUpdateBookingStatus();
        // Silent update - tidak tampilkan notifikasi
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