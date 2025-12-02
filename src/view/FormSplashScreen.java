package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Splash Screen with loading bar
 * @author bayu
 */
public class FormSplashScreen extends JWindow {
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel logoLabel;
    private final int SPLASH_WIDTH = 500;
    private final int SPLASH_HEIGHT = 350;
    
    public FormSplashScreen() {
        initComponents();
        setLocationRelativeTo(null);
        
        // Set rounded corners (optional)
        setShape(new RoundRectangle2D.Double(0, 0, SPLASH_WIDTH, SPLASH_HEIGHT, 30, 30));
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    0, getHeight(), new Color(109, 213, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(SPLASH_WIDTH, SPLASH_HEIGHT));
        mainPanel.setOpaque(true); // Pastikan panel tidak transparan
        mainPanel.setBackground(new Color(41, 128, 185)); // Warna awal sebelum gradient
        
        // Logo
        logoLabel = new JLabel();
        logoLabel.setBounds(150, 40, 200, 120);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
            // Perbaikan: gunakan SCALE_SMOOTH dan pertahankan aspect ratio
            Image originalImage = logoIcon.getImage();
            
            // Hitung ukuran dengan mempertahankan aspect ratio
            int maxWidth = 180;
            int maxHeight = 100;
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);
            
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            int newWidth = (int) (originalWidth * ratio);
            int newHeight = (int) (originalHeight * ratio);
            
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // Fallback if logo not found
            logoLabel.setText("ANTASENA");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
        }
        
        mainPanel.add(logoLabel);
        
        // Application name
        JLabel appNameLabel = new JLabel("ANTASENA SYSTEM", SwingConstants.CENTER);
        appNameLabel.setBounds(100, 170, 300, 30);
        appNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appNameLabel.setForeground(Color.WHITE);
        mainPanel.add(appNameLabel);
        
        // Version label
        JLabel versionLabel = new JLabel("Version 3.0", SwingConstants.CENTER);
        versionLabel.setBounds(100, 200, 300, 20);
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(255, 255, 255, 200));
        mainPanel.add(versionLabel);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(75, 250, 350, 20);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113));
        progressBar.setBackground(new Color(255, 255, 255, 100));
        progressBar.setBorderPainted(false);
        progressBar.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        mainPanel.add(progressBar);
        
        // Status label
        statusLabel = new JLabel("Initializing...", SwingConstants.CENTER);
        statusLabel.setBounds(100, 280, 300, 20);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(Color.WHITE);
        mainPanel.add(statusLabel);
        
        // Copyright
        JLabel copyrightLabel = new JLabel("© 2024 Antasena. All rights reserved.", SwingConstants.CENTER);
        copyrightLabel.setBounds(100, 310, 300, 20);
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        copyrightLabel.setForeground(new Color(255, 255, 255, 150));
        mainPanel.add(copyrightLabel);
        
        add(mainPanel);
        pack();
        
        // Set background window color (perbaikan layar putih)
        setBackground(new Color(41, 128, 185));
    }
    
    public void startLoading() {
        // Tampilkan splash screen dulu
        setVisible(true);
        
        // Paksa repaint untuk menghindari layar putih
        repaint();
        
        // Delay kecil untuk memastikan UI ter-render
        Timer initialTimer = new Timer(50, e -> {
            ((Timer)e.getSource()).stop();
            startLoadingProcess();
        });
        initialTimer.setRepeats(false);
        initialTimer.start();
    }
    
    private void startLoadingProcess() {
        // Loading simulation in background thread
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                String[] tasks = {
                    "Loading resources...",
                    "Connecting to database...",
                    "Initializing components...",
                    "Loading user interface...",
                    "Preparing application..."
                };
                
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30); // Adjust speed here
                    publish(i);
                    
                    // Update status based on progress
                    if (i < 20) {
                        updateStatus(tasks[0]);
                    } else if (i < 40) {
                        updateStatus(tasks[1]);
                    } else if (i < 60) {
                        updateStatus(tasks[2]);
                    } else if (i < 80) {
                        updateStatus(tasks[3]);
                    } else {
                        updateStatus(tasks[4]);
                    }
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                progressBar.setValue(progress);
            }
            
            @Override
            protected void done() {
                dispose();
                // Open login form
                java.awt.EventQueue.invokeLater(() -> {
                    new FormLogin().setVisible(true);
                });
            }
        };
        
        worker.execute();
    }
    
    private void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }
}