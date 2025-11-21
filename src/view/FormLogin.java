package view;

import dao.UserDAO;
import model.User;
import util.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class FormLogin extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError, lblLogo;
    private UserDAO userDAO;
    
    // Warna
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color PRIMARY_DARK = new Color(30, 115, 170);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color LIGHT = new Color(248, 249, 250);
    private final Color DARK = new Color(52, 58, 64);
    private final Color GRAY = new Color(206, 212, 218);
    private final Color LIGHT_GRAY = new Color(233, 236, 239);

    public FormLogin() {
        userDAO = new UserDAO();
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Login - Aplikasi Penyewaan Bus");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main Panel
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Logo Panel
        JPanel logoPanel = createLogoPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.3;
        gbc.weighty = 0.0;
        mainPanel.add(logoPanel, gbc);

        // Content Panel
        JPanel contentPanel = createContentPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        mainPanel.add(contentPanel, gbc);

        add(mainPanel);
        
        txtPassword.addActionListener(e -> login());
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Load logo
        lblLogo = new JLabel();
        try {
            URL logoUrl = FormLogin.class.getResource("/img/logo.png");
            if (logoUrl != null) {
                BufferedImage logoImage = ImageIO.read(logoUrl);
                Image scaled = logoImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaled));
            } else {
                lblLogo.setText("🚌");
                lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 64));
                lblLogo.setForeground(Color.WHITE);
            }
        } catch (IOException e) {
            lblLogo.setText("🚌");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 64));
            lblLogo.setForeground(Color.WHITE);
            e.printStackTrace();
        }
        
        JLabel titleLabel = new JLabel("SISTEM PENYEWAAN BUS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("Masuk untuk mengelola sistem");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        panel.add(lblLogo);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-4, getHeight()-4, 20, 20));
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(400, 350));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        // Username field
        txtUsername = createTextField("👤 Username");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(txtUsername, gbc);

        // Password field
        txtPassword = createPasswordField("🔒 Password");
        gbc.gridy = 1;
        panel.add(txtPassword, gbc);

        // Error label
        lblError = new JLabel();
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(DANGER);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panel.add(lblError, gbc);

        // Login button
        btnLogin = createModernButton("MASUK", PRIMARY);
        gbc.gridy = 3;
        panel.add(btnLogin, gbc);

        // Event
        btnLogin.addActionListener(e -> login());

        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left + 5, getHeight()/2 + 5);
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(DARK);
        field.setCaretColor(PRIMARY);
        field.setPreferredSize(new Dimension(280, 40));
        field.setMinimumSize(new Dimension(280, 40));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(10, 13, 10, 13)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                validateField(field);
            }
        });
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(GRAY);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, getInsets().left + 5, getHeight()/2 + 5);
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(DARK);
        field.setCaretColor(PRIMARY);
        field.setPreferredSize(new Dimension(280, 40));
        field.setMinimumSize(new Dimension(280, 40));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY, 2),
                    BorderFactory.createEmptyBorder(10, 13, 10, 13)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                validateField(field);
            }
        });
        return field;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(bgColor);
                g2.fill(rect);
                
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(280, 50));
        button.setMinimumSize(new Dimension(280, 50));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void validateField(JComponent field) {
        if (field instanceof JTextField && ((JTextField) field).getText().trim().isEmpty()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DANGER, 2),
                BorderFactory.createEmptyBorder(10, 13, 10, 13)
            ));
        } else if (field instanceof JPasswordField && ((JPasswordField) field).getPassword().length == 0) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DANGER, 2),
                BorderFactory.createEmptyBorder(10, 13, 10, 13)
            ));
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
        }
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        lblError.setText("");
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("⚠️ Username dan Password wajib diisi");
            if (username.isEmpty()) validateField(txtUsername);
            if (password.isEmpty()) validateField(txtPassword);
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            SessionManager.setCurrentUser(user);
            
            btnLogin.setText("✓ Berhasil!");
            btnLogin.setBackground(SUCCESS);
            btnLogin.setEnabled(false);
            
            Timer timer = new Timer(1000, e -> {
                dispose();
                if ("admin".equals(user.getRole())) {
                    new DashboardAdmin().setVisible(true);
                } else if ("kasir".equals(user.getRole())) {
                    new DashboardKasir().setVisible(true);
                } else if ("sopir".equals(user.getRole())) {
                    new DashboardSopir().setVisible(true);
                }
                ((Timer)e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
            
        } else {
            lblError.setText("❌ Username atau Password salah");
            
            Timer shake = new Timer(50, new ActionListener() {
                int count = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    int x = getLocation().x + (count % 2 == 0 ? 10 : -10);
                    setLocation(x, getLocation().y);
                    count++;
                    if (count >= 6) {
                        setLocationRelativeTo(null);
                        ((Timer)e.getSource()).stop();
                    }
                }
            });
            shake.start();
            
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}
