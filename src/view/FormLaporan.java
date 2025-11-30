package view;

import dao.LaporanDAO;
import model.LaporanKeuangan;
import util.ExcelExporter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FormLaporan extends JFrame {
    private JDateChooser dateStart, dateEnd;
    private JButton btnGenerate, btnExport, btnClose;
    private JLabel lblTotalPendapatan, lblPendapatanCash, lblPendapatanTransfer, lblPendapatanEwallet;
    private JLabel lblTotalBiayaSopir, lblBiayaSopirBayar, lblBiayaSopirBelum;
    private JLabel lblKeuntunganKotor, lblKeuntunganBersih, lblMarginKeuntungan;
    private JLabel lblTotalBooking, lblBookingSelesai, lblBookingKonfirmasi, lblBookingBatal;
    private JProgressBar progressBar;
    private LaporanDAO laporanDAO;
    private LaporanKeuangan currentLaporan;
    private List<Map<String, Object>> currentDetailTransaksi;
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public FormLaporan() {
        laporanDAO = new LaporanDAO();
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setTitle("Laporan Keuangan");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        JLabel lblTitle = new JLabel("LAPORAN KEUANGAN & KEUNTUNGAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        
        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        
        // Content Panel with Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ringkasan Keuangan", createRingkasanPanel());
        tabbedPane.addTab("Analisis Pendapatan", createAnalisisPanel());
        tabbedPane.addTab("Statistik Booking", createStatistikPanel());
        
        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        
        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(filterPanel, BorderLayout.NORTH);
        topPanel.add(tabbedPane, BorderLayout.CENTER);
        topPanel.add(progressBar, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Event Listeners
        btnGenerate.addActionListener(e -> generateLaporan());
        btnExport.addActionListener(e -> exportToExcel());
        btnClose.addActionListener(e -> dispose());
        
        // Set default dates (bulan ini)
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        dateStart.setDate(cal.getTime());
        dateEnd.setDate(new Date());
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Periode Laporan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tanggal Mulai
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Dari Tanggal:"), gbc);
        gbc.gridx = 1;
        dateStart = new JDateChooser();
        dateStart.setDateFormatString("dd/MM/yyyy");
        panel.add(dateStart, gbc);
        
        // Tanggal Selesai
        gbc.gridx = 2;
        panel.add(new JLabel("Sampai Tanggal:"), gbc);
        gbc.gridx = 3;
        dateEnd = new JDateChooser();
        dateEnd.setDateFormatString("dd/MM/yyyy");
        panel.add(dateEnd, gbc);
        
        // Generate Button
        gbc.gridx = 4;
        btnGenerate = createButton("Generate Laporan", new Color(46, 204, 113));
        panel.add(btnGenerate, gbc);
        
        return panel;
    }
    
    private JPanel createRingkasanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // PENDAPATAN Section
        addSectionHeader(panel, gbc, row++, "PENDAPATAN", new Color(46, 204, 113));
        lblTotalPendapatan = addDataRow(panel, gbc, row++, "Total Pendapatan:", "Rp 0");
        lblPendapatanCash = addDataRow(panel, gbc, row++, "  • Cash:", "Rp 0");
        lblPendapatanTransfer = addDataRow(panel, gbc, row++, "  • Transfer:", "Rp 0");
        lblPendapatanEwallet = addDataRow(panel, gbc, row++, "  • E-Wallet:", "Rp 0");
        row++; // Spacer
        
        // PENGELUARAN Section
        addSectionHeader(panel, gbc, row++, "PENGELUARAN", new Color(231, 76, 60));
        lblTotalBiayaSopir = addDataRow(panel, gbc, row++, "Total Biaya Operasional:", "Rp 0");
        lblBiayaSopirBayar = addDataRow(panel, gbc, row++, "  • Sudah Dibayar:", "Rp 0");
        lblBiayaSopirBelum = addDataRow(panel, gbc, row++, "  • Belum Dibayar:", "Rp 0");
        row++; // Spacer
        
        // KEUNTUNGAN Section
        addSectionHeader(panel, gbc, row++, "KEUNTUNGAN", new Color(52, 152, 219));
        lblKeuntunganKotor = addDataRow(panel, gbc, row++, "Keuntungan Kotor:", "Rp 0");
        lblKeuntunganBersih = addDataRow(panel, gbc, row++, "Keuntungan Bersih:", "Rp 0");
        lblMarginKeuntungan = addDataRow(panel, gbc, row++, "Margin Keuntungan:", "0%");
        
        return panel;
    }
    
    private JPanel analisisPanel;
    private JPanel chartPanel;
    
    private JPanel createAnalisisPanel() {
        analisisPanel = new JPanel(new BorderLayout(10, 10));
        analisisPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h2>Analisis Pendapatan Per Metode Pembayaran</h2>" +
                "<p>Generate laporan terlebih dahulu untuk melihat analisis</p></div></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        analisisPanel.add(infoLabel, BorderLayout.CENTER);
        
        return analisisPanel;
    }
    
    private void updateAnalisisPanel() {
        if (currentLaporan == null) return;
        
        // Clear panel
        analisisPanel.removeAll();
        
        // Create new content
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("ANALISIS PENDAPATAN PER METODE PEMBAYARAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        // Data Panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        
        double totalPendapatan = currentLaporan.getTotalPendapatan();
        
        // Header
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblMetodeHeader = new JLabel("Metode Pembayaran");
        lblMetodeHeader.setFont(new Font("Arial", Font.BOLD, 14));
        dataPanel.add(lblMetodeHeader, gbc);
        
        gbc.gridx = 1;
        JLabel lblJumlahHeader = new JLabel("Jumlah");
        lblJumlahHeader.setFont(new Font("Arial", Font.BOLD, 14));
        lblJumlahHeader.setHorizontalAlignment(SwingConstants.RIGHT);
        dataPanel.add(lblJumlahHeader, gbc);
        
        gbc.gridx = 2;
        JLabel lblPersenHeader = new JLabel("Persentase");
        lblPersenHeader.setFont(new Font("Arial", Font.BOLD, 14));
        lblPersenHeader.setHorizontalAlignment(SwingConstants.CENTER);
        dataPanel.add(lblPersenHeader, gbc);
        
        gbc.gridx = 3;
        JLabel lblBarHeader = new JLabel("Visualisasi");
        lblBarHeader.setFont(new Font("Arial", Font.BOLD, 14));
        dataPanel.add(lblBarHeader, gbc);
        
        // Cash
        addAnalisisRow(dataPanel, gbc, 1, "💵 Cash", 
                      currentLaporan.getPendapatanCash(), 
                      totalPendapatan,
                      new Color(46, 204, 113));
        
        // Transfer
        addAnalisisRow(dataPanel, gbc, 2, "🏦 Transfer", 
                      currentLaporan.getPendapatanTransfer(), 
                      totalPendapatan,
                      new Color(52, 152, 219));
        
        // E-Wallet
        addAnalisisRow(dataPanel, gbc, 3, "📱 E-Wallet", 
                      currentLaporan.getPendapatanEwallet(), 
                      totalPendapatan,
                      new Color(155, 89, 182));
        
        // Separator
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(0, 2));
        dataPanel.add(separator, gbc);
        gbc.gridwidth = 1;
        
        // Total
        gbc.gridy = 5;
        gbc.gridx = 0;
        JLabel lblTotalLabel = new JLabel("TOTAL");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dataPanel.add(lblTotalLabel, gbc);
        
        gbc.gridx = 1;
        JLabel lblTotalValue = new JLabel(currencyFormat.format(totalPendapatan));
        lblTotalValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalValue.setHorizontalAlignment(SwingConstants.RIGHT);
        dataPanel.add(lblTotalValue, gbc);
        
        gbc.gridx = 2;
        JLabel lblTotalPercent = new JLabel("100.00%");
        lblTotalPercent.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalPercent.setHorizontalAlignment(SwingConstants.CENTER);
        dataPanel.add(lblTotalPercent, gbc);
        
        // Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        summaryPanel.add(createSummaryCard("Metode Terpopuler", 
                                          getMetodeTerpopuler(), 
                                          new Color(241, 196, 15)));
        summaryPanel.add(createSummaryCard("Rata-rata per Transaksi", 
                                          getRataRataTransaksi(), 
                                          new Color(52, 152, 219)));
        summaryPanel.add(createSummaryCard("Total Transaksi", 
                                          getTotalTransaksi() + " transaksi", 
                                          new Color(46, 204, 113)));
        
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(dataPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        analisisPanel.add(contentPanel, BorderLayout.CENTER);
        analisisPanel.revalidate();
        analisisPanel.repaint();
    }
    
    private void addAnalisisRow(JPanel panel, GridBagConstraints gbc, int row, 
                               String metode, double jumlah, double total, Color color) {
        double persentase = total > 0 ? (jumlah / total) * 100 : 0;
        
        // Metode
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblMetode = new JLabel(metode);
        lblMetode.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lblMetode, gbc);
        
        // Jumlah
        gbc.gridx = 1;
        JLabel lblJumlah = new JLabel(currencyFormat.format(jumlah));
        lblJumlah.setFont(new Font("Arial", Font.BOLD, 14));
        lblJumlah.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblJumlah, gbc);
        
        // Persentase
        gbc.gridx = 2;
        JLabel lblPersen = new JLabel(String.format("%.2f%%", persentase));
        lblPersen.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPersen.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblPersen, gbc);
        
        // Progress Bar
        gbc.gridx = 3;
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue((int) persentase);
        progressBar.setStringPainted(true);
        progressBar.setForeground(color);
        progressBar.setPreferredSize(new Dimension(200, 25));
        panel.add(progressBar, gbc);
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);
        lblValue.setForeground(color.darker());
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
    
    private String getMetodeTerpopuler() {
        if (currentLaporan == null) return "-";
        
        double cash = currentLaporan.getPendapatanCash();
        double transfer = currentLaporan.getPendapatanTransfer();
        double ewallet = currentLaporan.getPendapatanEwallet();
        
        if (cash >= transfer && cash >= ewallet) {
            return "Cash";
        } else if (transfer >= cash && transfer >= ewallet) {
            return "Transfer";
        } else {
            return "E-Wallet";
        }
    }
    
    private String getRataRataTransaksi() {
        if (currentDetailTransaksi == null || currentDetailTransaksi.isEmpty()) {
            return "Rp 0";
        }
        
        double total = 0;
        int count = 0;
        
        for (Map<String, Object> transaksi : currentDetailTransaksi) {
            Double dibayar = (Double) transaksi.get("jumlah_dibayar");
            if (dibayar != null && dibayar > 0) {
                total += dibayar;
                count++;
            }
        }
        
        if (count > 0) {
            return currencyFormat.format(total / count);
        }
        return "Rp 0";
    }
    
    private int getTotalTransaksi() {
        if (currentDetailTransaksi == null) return 0;
        
        int count = 0;
        for (Map<String, Object> transaksi : currentDetailTransaksi) {
            Double dibayar = (Double) transaksi.get("jumlah_dibayar");
            if (dibayar != null && dibayar > 0) {
                count++;
            }
        }
        return count;
    }
    
    private JPanel createStatistikPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        
        int row = 0;
        
        addSectionHeader(panel, gbc, row++, "STATISTIK BOOKING", new Color(155, 89, 182));
        lblTotalBooking = addDataRow(panel, gbc, row++, "Total Booking:", "0");
        lblBookingSelesai = addDataRow(panel, gbc, row++, "  • Selesai:", "0");
        lblBookingKonfirmasi = addDataRow(panel, gbc, row++, "  • Dikonfirmasi:", "0");
        lblBookingBatal = addDataRow(panel, gbc, row++, "  • Dibatalkan:", "0");
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnExport = createButton("Export ke Excel", new Color(39, 174, 96));
        btnExport.setEnabled(false);
        
        btnClose = createButton("Tutup", new Color(149, 165, 166));
        
        panel.add(btnExport);
        panel.add(btnClose);
        
        return panel;
    }
    
    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String text, Color color) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel header = new JLabel(text);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setForeground(color);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, color));
        panel.add(header, gbc);
        gbc.gridwidth = 1;
    }
    
    private JLabel addDataRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(lblLabel, gbc);
        
        gbc.gridx = 1;
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 14));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblValue, gbc);
        
        return lblValue;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 35));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }
    
    private void generateLaporan() {
        if (dateStart.getDate() == null || dateEnd.getDate() == null) {
            JOptionPane.showMessageDialog(this, 
                "Pilih periode tanggal terlebih dahulu!", 
                "Validasi", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (dateStart.getDate().after(dateEnd.getDate())) {
            JOptionPane.showMessageDialog(this, 
                "Tanggal mulai tidak boleh lebih besar dari tanggal selesai!", 
                "Validasi", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show progress
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        btnGenerate.setEnabled(false);
        
        // Generate in background thread
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                java.sql.Date sqlDateStart = new java.sql.Date(dateStart.getDate().getTime());
                java.sql.Date sqlDateEnd = new java.sql.Date(dateEnd.getDate().getTime());
                
                currentLaporan = laporanDAO.getLaporanKeuangan(sqlDateStart, sqlDateEnd);
                currentDetailTransaksi = laporanDAO.getDetailTransaksi(sqlDateStart, sqlDateEnd);
                
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
                btnGenerate.setEnabled(true);
                
                updateDisplay();
                updateAnalisisPanel(); // Update tab analisis
                btnExport.setEnabled(true);
                
                JOptionPane.showMessageDialog(FormLaporan.this, 
                    "Laporan berhasil di-generate!", 
                    "Sukses", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        
        worker.execute();
    }
    
    private void updateDisplay() {
        if (currentLaporan == null) return;
        
        // Update Pendapatan
        lblTotalPendapatan.setText(currencyFormat.format(currentLaporan.getTotalPendapatan()));
        lblPendapatanCash.setText(currencyFormat.format(currentLaporan.getPendapatanCash()));
        lblPendapatanTransfer.setText(currencyFormat.format(currentLaporan.getPendapatanTransfer()));
        lblPendapatanEwallet.setText(currencyFormat.format(currentLaporan.getPendapatanEwallet()));
        
        // Update Pengeluaran
        lblTotalBiayaSopir.setText(currencyFormat.format(currentLaporan.getTotalBiayaSopir()));
        lblBiayaSopirBayar.setText(currencyFormat.format(currentLaporan.getBiayaSopirSudahBayar()));
        lblBiayaSopirBelum.setText(currencyFormat.format(currentLaporan.getBiayaSopirBelumBayar()));
        
        // Update Keuntungan
        lblKeuntunganKotor.setText(currencyFormat.format(currentLaporan.getKeuntunganKotor()));
        lblKeuntunganBersih.setText(currencyFormat.format(currentLaporan.getKeuntunganBersih()));
        
        // Update Margin
        if (currentLaporan.getTotalPendapatan() > 0) {
            double margin = (currentLaporan.getKeuntunganBersih() / currentLaporan.getTotalPendapatan()) * 100;
            lblMarginKeuntungan.setText(String.format("%.2f%%", margin));
            
            // Color code margin
            if (margin >= 30) {
                lblMarginKeuntungan.setForeground(new Color(46, 204, 113)); // Green
            } else if (margin >= 15) {
                lblMarginKeuntungan.setForeground(new Color(241, 196, 15)); // Yellow
            } else {
                lblMarginKeuntungan.setForeground(new Color(231, 76, 60)); // Red
            }
        } else {
            lblMarginKeuntungan.setText("0%");
        }
        
        // Update Statistik
        lblTotalBooking.setText(String.valueOf(currentLaporan.getTotalBooking()));
        lblBookingSelesai.setText(String.valueOf(currentLaporan.getBookingSelesai()));
        lblBookingKonfirmasi.setText(String.valueOf(currentLaporan.getBookingDikonfirmasi()));
        lblBookingBatal.setText(String.valueOf(currentLaporan.getBookingDibatalkan()));
    }
    
    private void exportToExcel() {
        if (currentLaporan == null || currentDetailTransaksi == null) {
            JOptionPane.showMessageDialog(this, 
                "Generate laporan terlebih dahulu!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        
        // Default filename
        String defaultFilename = "Laporan_Keuangan_" + 
                                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + 
                                ".xlsx";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }
            
            // Show progress
            progressBar.setVisible(true);
            progressBar.setIndeterminate(true);
            btnExport.setEnabled(false);
            
            final String finalFilePath = filePath;
            
            // Export in background thread
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return ExcelExporter.exportLaporanKeuangan(
                        currentLaporan,
                        currentDetailTransaksi,
                        finalFilePath,
                        dateStart.getDate(),
                        dateEnd.getDate()
                    );
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                    btnExport.setEnabled(true);
                    
                    try {
                        boolean success = get();
                        if (success) {
                            int open = JOptionPane.showConfirmDialog(FormLaporan.this, 
                                "Laporan berhasil di-export!\nBuka file sekarang?", 
                                "Sukses", 
                                JOptionPane.YES_NO_OPTION);
                            
                            if (open == JOptionPane.YES_OPTION) {
                                try {
                                    Desktop.getDesktop().open(new java.io.File(finalFilePath));
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(FormLaporan.this, 
                                        "File tersimpan di: " + finalFilePath, 
                                        "Info", 
                                        JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(FormLaporan.this, 
                                "Gagal export laporan!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(FormLaporan.this, 
                            "Error: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
        }
    }
}