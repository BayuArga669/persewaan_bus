package view;

import util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardKasir extends JFrame {
    private JLabel lblWelcome, lblRole;
    private JButton btnBuatBooking, btnLihatBus, btnManagePelanggan;
    private JButton btnRiwayatTransaksi, btnLogout;
    
    public DashboardKasir() {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Dashboard Kasir - Aplikasi Penyewaan Bus");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setPreferredSize(new Dimension(700, 100));
        headerPanel.setLayout(new BorderLayout());
        
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setBackground(new Color(52, 152, 219));
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        
        lblWelcome = new JLabel("Selamat Datang, " + SessionManager.getCurrentUserFullName());
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 22));
        lblWelcome.setForeground(Color.WHITE);
        
        lblRole = new JLabel("Role: Kasir");
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
        menuPanel.setLayout(new GridLayout(3, 2, 15, 15));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Create Menu Buttons
        btnBuatBooking = createMenuButton("Buat Booking Baru", new Color(46, 204, 113));
        btnLihatBus = createMenuButton("Lihat Daftar Bus", new Color(52, 152, 219));
        btnManagePelanggan = createMenuButton("Kelola Pelanggan", new Color(241, 196, 15));
        btnRiwayatTransaksi = createMenuButton("Riwayat Transaksi", new Color(155, 89, 182));
        btnLogout = createMenuButton("Logout", new Color(231, 76, 60));
        
        menuPanel.add(btnBuatBooking);
        menuPanel.add(btnLihatBus);
        menuPanel.add(btnManagePelanggan);
        menuPanel.add(btnRiwayatTransaksi);
        menuPanel.add(btnLogout);
        menuPanel.add(new JLabel()); // Empty cell
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Event Listeners
        btnBuatBooking.addActionListener(e -> buatBooking());
        btnLihatBus.addActionListener(e -> lihatBus());
        btnManagePelanggan.addActionListener(e -> managePelanggan());
        btnRiwayatTransaksi.addActionListener(e -> riwayatTransaksi());
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
    
    private void buatBooking() {
        new FormBooking().setVisible(true);
    }
    
    private void lihatBus() {
        new FormBus().setVisible(true);
    }
    
    private void managePelanggan() {
        new FormPelanggan().setVisible(true);
    }
    
    private void riwayatTransaksi() {
        JOptionPane.showMessageDialog(this, 
            "Menampilkan riwayat transaksi...", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
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