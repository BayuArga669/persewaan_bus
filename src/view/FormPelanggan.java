package view;

import dao.PelangganDAO;
import model.Pelanggan;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.event.DocumentEvent;

public class FormPelanggan extends JFrame {
    private JTable tablePelanggan;
    private DefaultTableModel tableModel;
    private JTextField txtNama, txtNoTelp, txtEmail, txtNoKtp, txtSearch;
    private JTextArea txtAlamat;
    private JButton btnTambah, btnUpdate, btnHapus, btnBatal, btnCari, btnRefresh;
    private PelangganDAO pelangganDAO;
    private int selectedId = -1;

    // Warna tema — konsisten dengan FormBooking
    private final Color PRIMARY = new Color(41, 128, 185);
    private final Color PRIMARY_DARK = new Color(31, 97, 141);
    private final Color SUCCESS = new Color(46, 204, 113);
    private final Color INFO = new Color(52, 152, 219);
    private final Color DANGER = new Color(231, 76, 60);
    private final Color WARNING = new Color(241, 196, 15);
    private final Color LIGHT_GRAY = new Color(233, 236, 239);
    private final Color MEDIUM_GRAY = new Color(206, 212, 218);
    private final Color FOCUS_BLUE = new Color(60, 140, 200);

    public FormPelanggan() {
        pelangganDAO = new PelangganDAO();
        initComponents();
        loadData();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("📱 Kelola Data Pelanggan");
        // Mengatur form menjadi full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Panel dengan Gradient
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Konten Utama
        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Form + Search Panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(createFormPanel(), BorderLayout.NORTH);
        topPanel.add(createSearchPanel(), BorderLayout.SOUTH);

        // Tabel
        JPanel tablePanel = createTablePanel();

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Event Listeners
        btnTambah.addActionListener(e -> tambahPelanggan());
        btnUpdate.addActionListener(e -> updatePelanggan());
        btnHapus.addActionListener(e -> hapusPelanggan());
        btnBatal.addActionListener(e -> clearForm());
        //btnCari.addActionListener(e -> cariPelanggan());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData();
        });

        // 🔍 Live Search
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterData(); }
            @Override public void removeUpdate(DocumentEvent e) { filterData(); }
            @Override public void changedUpdate(DocumentEvent e) { filterData(); }
        });

        tablePelanggan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow();
            }
        });

        setButtonState(false);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), getHeight(), PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
            }
        };
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JLabel title = new JLabel("👥 MANAJEMEN DATA PELANGGAN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Kelola data pelanggan: tambah, edit, dan cari dengan cepat");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 220, 220));

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);

        return panel;
    }

    private JPanel createFormPanel() {
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
                g2.draw(rect);
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), "📋 Form Input Pelanggan"
        ));
        panel.setPreferredSize(new Dimension(0, 320)); // Diperbesar untuk menampung alamat yang lebih besar

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);

        // Nama & No KTP
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabeledField("👤 Nama Lengkap", txtNama = createTextField("Nama pelanggan", true)), gbc);
        gbc.gridx = 1;
        panel.add(createLabeledField("🆔 No. KTP", txtNoKtp = createTextField("1234567890123456", false)), gbc);

        // No Telp & Email
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabeledField("📱 No. Telepon", txtNoTelp = createTextField("0812-3456-7890", true)), gbc);
        gbc.gridx = 1;
        panel.add(createLabeledField("✉️ Email", txtEmail = createTextField("contoh@email.com", false)), gbc);

        // Alamat
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(createLabeledField("🏠 Alamat", txtAlamat = createTextArea("Alamat lengkap pelanggan", true)), gbc); // Diperbesar
        gbc.gridwidth = 1;

        // Tombol
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        btnTambah = createModernButton("➕ Tambah", SUCCESS);
        btnUpdate = createModernButton("✏️ Update", INFO);
        btnHapus = createModernButton("🗑️ Hapus", DANGER);
        btnBatal = createModernButton("↩️ Batal", new Color(149, 165, 166));

        btnPanel.add(btnTambah);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);
        btnPanel.add(btnBatal);

        panel.add(btnPanel, gbc);

        // Hover effect
        addHoverEffect(btnTambah, SUCCESS);
        addHoverEffect(btnUpdate, INFO);
        addHoverEffect(btnHapus, DANGER);
        addHoverEffect(btnBatal, new Color(149, 165, 166));

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.fill(rect);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 8, 8));
            }
        };
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)), "🔍 Pencarian Cepat"
        ));

        JLabel lbl = new JLabel("Cari (nama/no telp/email):");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl);

        txtSearch = createTextField("Ketik untuk cari...", false);
        txtSearch.setColumns(28);
        panel.add(txtSearch);

        btnCari = createModernButton("🔎 Cari", WARNING);
        btnRefresh = createModernButton("🔄 Segarkan", new Color(149, 165, 166));

        //panel.add(btnCari);
        panel.add(btnRefresh);

        addHoverEffect(btnCari, WARNING);
        addHoverEffect(btnRefresh, new Color(149, 165, 166));

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"ID", "Nama", "No. Telepon", "Email", "Alamat", "No. KTP"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablePelanggan = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return c;
            }
        };

        tablePelanggan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePelanggan.setRowHeight(32);
        tablePelanggan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablePelanggan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablePelanggan.getTableHeader().setBackground(PRIMARY);
        tablePelanggan.getTableHeader().setForeground(Color.WHITE);
        tablePelanggan.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tablePelanggan.setGridColor(new Color(235, 235, 235));

        // Lebar kolom
        tablePelanggan.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePelanggan.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablePelanggan.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablePelanggan.getColumnModel().getColumn(3).setPreferredWidth(180);
        tablePelanggan.getColumnModel().getColumn(4).setPreferredWidth(220);
        tablePelanggan.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(tablePelanggan);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), "📊 Daftar Pelanggan"
        ));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, BorderLayout.WEST);

        if (field instanceof JTextArea) {
            JScrollPane sp = new JScrollPane((JTextArea) field);
            // Ukuran JScrollPane akan mengikuti ukuran JTextArea yang telah ditentukan
            panel.add(sp, BorderLayout.CENTER);
        } else {
            panel.add(field, BorderLayout.CENTER);
        }

        return panel;
    }

    private JTextField createTextField(String placeholder, boolean isLarge) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(getFont().deriveFont(Font.ITALIC, 11));
                    g2.drawString(placeholder, getInsets().left + 5, getHeight() / 2 + 2);
                }
            }
        };
        
        // Fokus pada ukuran kotak input, bukan teks
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Font size tetap normal
        
        // 🔥 PERUBAHAN UTAMA: Memperbesar kotak input
        if (isLarge) {
            field.setColumns(35); // Kotak input jauh lebih lebar
            field.setPreferredSize(new Dimension(0, 32)); // Tinggi kotak sedikit lebih besar
        } else {
            field.setColumns(20); // Ukuran normal untuk field lainnya
            field.setPreferredSize(new Dimension(0, 28));
        }
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOCUS_BLUE, 2),
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
        return field;
    }

    private JTextArea createTextArea(String placeholder, boolean isLarge) {
        JTextArea area = new JTextArea(isLarge ? 4 : 2, isLarge ? 50 : 25) { // 🔥 Kolom diperbesar jadi 50
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(180, 180, 180));
                    g2.setFont(getFont().deriveFont(Font.ITALIC, 11));
                    g2.drawString(placeholder + " (opsional)", getInsets().left + 5, 18);
                }
            }
        };
        area.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Font size tetap normal
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(FOCUS_BLUE, 2),
                    BorderFactory.createEmptyBorder(5, 9, 5, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                area.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        return area;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setBackground(bgColor);
        return btn;
    }

    private void addHoverEffect(JButton btn, Color base) {
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(base.darker()); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(base); }
            @Override public void mousePressed(MouseEvent e) { btn.setBackground(base.darker().darker()); }
            @Override public void mouseReleased(MouseEvent e) { btn.setBackground(btn.getModel().isRollover() ? base.darker() : base); }
        });
    }

    // ✅ Live search tanpa button (opsional: tetap pakai button jika lebih stabil)
    private void filterData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        List<Pelanggan> list = pelangganDAO.getAllPelanggan();
        for (Pelanggan p : list) {
            boolean match = p.getNamaPelanggan().toLowerCase().contains(keyword) ||
                           p.getNoTelp().toLowerCase().contains(keyword) ||
                           (p.getEmail() != null && p.getEmail().toLowerCase().contains(keyword)) ||
                           (p.getNoKtp() != null && p.getNoKtp().toLowerCase().contains(keyword));

            if (keyword.isEmpty() || match) {
                tableModel.addRow(new Object[]{
                    p.getIdPelanggan(),
                    p.getNamaPelanggan(),
                    p.getNoTelp(),
                    p.getEmail() == null ? "" : p.getEmail(),
                    p.getAlamat() == null ? "" : p.getAlamat(),
                    p.getNoKtp() == null ? "" : p.getNoKtp()
                });
            }
        }
    }

    private void loadData() {
        txtSearch.setText("");
        filterData(); // reload semua
    }

    private void tambahPelanggan() {
        if (!validateInput()) return;

        Pelanggan p = new Pelanggan();
        p.setNamaPelanggan(txtNama.getText().trim());
        p.setNoTelp(txtNoTelp.getText().trim());
        p.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        p.setAlamat(txtAlamat.getText().trim().isEmpty() ? null : txtAlamat.getText().trim());
        p.setNoKtp(txtNoKtp.getText().trim().isEmpty() ? null : txtNoKtp.getText().trim());

        if (pelangganDAO.tambahPelanggan(p)) {
            JOptionPane.showMessageDialog(this,
                "✅ Pelanggan berhasil ditambahkan!\nNama: " + p.getNamaPelanggan(),
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal menambahkan data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePelanggan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Pilih pelanggan yang akan diupdate!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        Pelanggan p = new Pelanggan();
        p.setIdPelanggan(selectedId);
        p.setNamaPelanggan(txtNama.getText().trim());
        p.setNoTelp(txtNoTelp.getText().trim());
        p.setEmail(txtEmail.getText().trim().isEmpty() ? null : txtEmail.getText().trim());
        p.setAlamat(txtAlamat.getText().trim().isEmpty() ? null : txtAlamat.getText().trim());
        p.setNoKtp(txtNoKtp.getText().trim().isEmpty() ? null : txtNoKtp.getText().trim());

        if (pelangganDAO.updatePelanggan(p)) {
            JOptionPane.showMessageDialog(this, "✅ Data pelanggan berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Gagal mengupdate data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusPelanggan() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Pilih pelanggan yang akan dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Pelanggan p = pelangganDAO.getPelangganById(selectedId);
        if (p == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Yakin hapus pelanggan?<br><b>" + p.getNamaPelanggan() + "</b><br>No. Telp: " + p.getNoTelp() + "</html>",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (pelangganDAO.hapusPelanggan(selectedId)) {
                JOptionPane.showMessageDialog(this, "✅ Pelanggan berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus. Mungkin sedang digunakan di booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void selectRow() {
        int row = tablePelanggan.getSelectedRow();
        if (row != -1) {
            selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            txtNama.setText(tableModel.getValueAt(row, 1).toString());
            txtNoTelp.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtAlamat.setText(tableModel.getValueAt(row, 4).toString());
            txtNoKtp.setText(tableModel.getValueAt(row, 5).toString());
            setButtonState(true);
        }
    }

    private void clearForm() {
        txtNama.setText("");
        txtNoTelp.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        txtNoKtp.setText("");
        selectedId = -1;
        tablePelanggan.clearSelection();
        setButtonState(false);
    }

    private void setButtonState(boolean isUpdate) {
        btnTambah.setEnabled(!isUpdate);
        btnUpdate.setEnabled(isUpdate);
        btnHapus.setEnabled(isUpdate);
        btnBatal.setEnabled(true);
    }

    private boolean validateInput() {
        String nama = txtNama.getText().trim();
        String telp = txtNoTelp.getText().trim();

        if (nama.isEmpty() || telp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Nama dan No. Telepon wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validasi no telp: minimal 10 angka
        if (!telp.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(this, "⚠️ No. Telepon harus 10–15 digit angka (tanpa spasi/huruf)!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validasi email (opsional)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            JOptionPane.showMessageDialog(this, "⚠️ Format email tidak valid!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }
}