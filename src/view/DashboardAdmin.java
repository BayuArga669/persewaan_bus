package view;

import service.BookingService;
import util.SessionManager;
import dao.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class DashboardAdmin extends JFrame {
    private JLabel lblWelcome, lblRole;
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
    private Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 18);
    private Font statFont = new Font("Segoe UI", Font.BOLD, 32);
    private Font statLabelFont = new Font("Segoe UI", Font.PLAIN, 14);

    // Warna
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color PRIMARY_DARK = new Color(30, 115, 170);
    private final Color SECONDARY = new Color(230, 126, 34);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color INFO = new Color(52, 152, 219);
    private final Color WARNING = new Color(241, 196, 15);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color DARK = new Color(52, 58, 64);
    private final Color GRAY = new Color(206, 212, 218);
    private final Color LIGHT = new Color(248, 249, 250);

    // Service & DAO
    private BookingService bookingService;
    private UserDAO userDAO;
    private BookingDAO bookingDAO;
    private PelangganDAO pelangganDAO;
    private SopirDAO sopirDAO;
    private BusDAO busDAO;

    // Statistik
    private int totalUser, totalBooking, totalPelanggan, totalSopir, totalBus;

    public DashboardAdmin() {
        // Inisialisasi DAO
        userDAO = new UserDAO();
        bookingDAO = new BookingDAO();
        pelangganDAO = new PelangganDAO();
        sopirDAO = new SopirDAO();
        busDAO = new BusDAO();
        
        // ✅ LOAD DULU → BARU INISIASI UI
        loadStatistics();
        
        bookingService = new BookingService();
        initComponents();
        autoUpdateBookingStatus();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Dashboard Admin - Aplikasi Penyewaan Bus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel titleLabel = new JLabel("DASHBOARD ADMIN");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(DARK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Kelola sistem penyewaan bus secara efisien");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 10));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        // Statistik Panel
        JPanel statsPanel = createStatsPanel();
        
        // Menu Panel
        JPanel cardPanel = createCardPanel();
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(LIGHT);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titlePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        centerPanel.add(statsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        centerPanel.add(cardPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(LIGHT);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2025 Aplikasi Penyewaan Bus Pariwisata | All Rights Reserved");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(GRAY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void loadStatistics() {
        try {
            // Total User (admin + kasir)
            List<User> users = userDAO.getAllUsers();
            totalUser = (int) users.stream()
                    .filter(u -> "admin".equals(u.getRole()) || "kasir".equals(u.getRole()))
                    .count();

            // Total Booking
            List<Booking> bookings = bookingDAO.getAllBooking();
            totalBooking = bookings.size();

            // Total Pelanggan
            List<Pelanggan> pelanggans = pelangganDAO.getAllPelanggan();
            totalPelanggan = pelanggans.size();

            // Total Sopir aktif
            List<Sopir> sopirs = sopirDAO.getAllSopir();
            totalSopir = (int) sopirs.stream()
                    .filter(s -> "aktif".equals(s.getStatusSopir()))
                    .count();

            // Total Bus
            List<Bus> buses = busDAO.getAllBus();
            totalBus = buses.size();

        } catch (Exception e) {
            e.printStackTrace();
            totalUser = totalBooking = totalPelanggan = totalSopir = totalBus = 0;
        }
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setOpaque(false);
        
        Object[][] stats = {
            {"👥", "User", String.valueOf(totalUser), PRIMARY},
            {"📅", "Booking", String.valueOf(totalBooking), SUCCESS},
            {"👨", "Pelanggan", String.valueOf(totalPelanggan), WARNING},
            {"🚕", "Sopir Aktif", String.valueOf(totalSopir), SECONDARY},
            {"🚌", "Bus", String.valueOf(totalBus), INFO}
        };
        
        for (Object[] stat : stats) {
            String icon = (String) stat[0];
            String label = (String) stat[1];
            String value = (String) stat[2];
            Color color = (Color) stat[3];
            
            JPanel statCard = createStatCard(icon, label, value, color);
            panel.add(statCard);
        }
        
        return panel;
    }

    private JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 12, 12));
                
                g2.setColor(new Color(230, 230, 230));
                g2.draw(rect);
            }
        };
        card.setPreferredSize(new Dimension(220, 120));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel iconPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 40;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, size, size, 8, 8);
                g2.setColor(color);
                g2.fill(rect);
            }
        };
        iconPanel.setPreferredSize(new Dimension(50, 50));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(statFont);
        valueLabel.setForeground(DARK);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(statLabelFont);
        descLabel.setForeground(GRAY);

        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setOpaque(false);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        textPanel.add(descLabel, BorderLayout.SOUTH);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
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
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("🚌");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        
        lblWelcome = new JLabel("Selamat Datang, " + SessionManager.getCurrentUserFullName());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(Color.WHITE);
        
        lblRole = new JLabel("Role: Administrator");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblRole.setForeground(new Color(230, 230, 230));
        
        textPanel.add(lblWelcome);
        textPanel.add(lblRole);
        
        titlePanel.add(logoLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(15, 0)));
        titlePanel.add(textPanel);
        
        headerPanel.add(titlePanel);
        return headerPanel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(1000, 450);
            }
        };
        panel.setLayout(new WrapLayout(WrapLayout.CENTER, 25, 25));
        panel.setOpaque(false);

        // ✅ HANYA 7 MENU (tanpa "Kelola Pembayaran")
        Object[][] menuItems = {
            {"Kelola Bus", "🚌", INFO, (ActionListener) e -> openManageBus()},
            {"Kelola User", "👥", new Color(155, 89, 182), (ActionListener) e -> openManageUser()},
            {"Kelola Booking", "📅", SUCCESS, (ActionListener) e -> openManageBooking()},
            {"Kelola Pelanggan", "👨", WARNING, (ActionListener) e -> openManagePelanggan()},
            {"Assignment Sopir", "🚕", SECONDARY, (ActionListener) e -> openManageSopir()},
            {"Laporan Keuangan", "📊", new Color(26, 188, 156), (ActionListener) e -> openLaporan()},
            {"Logout", "🚪", DANGER, (ActionListener) e -> logout()}
        };

        for (Object[] item : menuItems) {
            String title = (String) item[0];
            String icon = (String) item[1];
            Color color = (Color) item[2];
            ActionListener action = (ActionListener) item[3];
            
            JPanel card = createRoundedCard(title, icon, color, action);
            panel.add(card);
        }
        
        return panel;
    }

    private JPanel createRoundedCard(String title, String icon, Color color, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                
                g2.setColor(new Color(0, 0, 0, 20));
                RoundRectangle2D shadow = new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, 15, 15);
                g2.fill(shadow);
                
                g2.setColor(new Color(230, 230, 230));
                g2.draw(rect);
            }
        };
        card.setPreferredSize(new Dimension(300, 220));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel iconPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 60;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, size, size, 12, 12);
                g2.setColor(color);
                g2.fill(rect);
            }
        };
        iconPanel.setPreferredSize(new Dimension(70, 70));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(DARK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel descLabel = new JLabel(getDescription(title));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(iconPanel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 2),
                    BorderFactory.createEmptyBorder(23, 23, 23, 23)
                ));
                titleLabel.setForeground(color);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
                titleLabel.setForeground(DARK);
            }
        });

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return card;
    }

    private String getDescription(String title) {
        switch (title) {
            case "Kelola Bus": return "Tambah, edit, dan hapus data bus";
            case "Kelola User": return "Kelola admin, kasir, dan sopir";
            case "Kelola Booking": return "Kelola pemesanan bus pelanggan";
            case "Kelola Pelanggan": return "Kelola data pelanggan";
            case "Assignment Sopir": return "Assign sopir ke booking";
            case "Laporan Keuangan": return "Lihat laporan pendapatan";
            case "Logout": return "Keluar dari sistem";
            default: return "";
        }
    }

    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = target.getWidth() - (insets.left + insets.right + hgap * 2);
                
                int x = 0, y = insets.top, rowHeight = 0;
                for (Component c : target.getComponents()) {
                    if (c.isVisible()) {
                        Dimension d = c.getPreferredSize();
                        if (x == 0 || x + d.width <= maxWidth) {
                            x += d.width + hgap;
                            rowHeight = Math.max(rowHeight, d.height);
                        } else {
                            y += rowHeight + vgap;
                            rowHeight = d.height;
                            x = d.width + hgap;
                        }
                    }
                }
                y += rowHeight + insets.bottom;
                return new Dimension(maxWidth, y);
            }
        }
    }

    private void openManageBus() {
        SwingUtilities.invokeLater(() -> new FormBus().setVisible(true));
    }
    
    private void openManageUser() {
        SwingUtilities.invokeLater(() -> new FormUser().setVisible(true));
    }
    
    private void openManageBooking() {
        SwingUtilities.invokeLater(() -> new FormBooking().setVisible(true));
    }
    
    private void openManagePelanggan() {
        SwingUtilities.invokeLater(() -> new FormPelanggan().setVisible(true));
    }
    
    private void openManageSopir() {
        SwingUtilities.invokeLater(() -> new FormAssignmentSopir().setVisible(true));
    }
    
    private void openLaporan() {
        SwingUtilities.invokeLater(() -> new FormLaporan().setVisible(true));
    }
    
    private void autoUpdateBookingStatus() {
        bookingService.autoUpdateBookingStatus();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            SwingUtilities.invokeLater(() -> new FormLogin().setVisible(true));
        }
    }
}