package util;

import model.LaporanKeuangan;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExcelExporter {
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Export laporan keuangan ke Excel
     */
    public static boolean exportLaporanKeuangan(
            LaporanKeuangan laporan,
            List<Map<String, Object>> detailTransaksi,
            String filePath,
            Date tanggalMulai,
            Date tanggalSelesai) {
        
        try (Workbook workbook = new XSSFWorkbook()) {
            
            // Sheet 1: Ringkasan
            createRingkasanSheet(workbook, laporan, tanggalMulai, tanggalSelesai);
            
            // Sheet 2: Detail Transaksi
            createDetailTransaksiSheet(workbook, detailTransaksi);
            
            // Sheet 3: Analisis Pendapatan
            createAnalisisPendapatanSheet(workbook, laporan);
            
            // Write to file
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                return true;
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static void createRingkasanSheet(Workbook workbook, LaporanKeuangan laporan, Date tanggalMulai, Date tanggalSelesai) {
        Sheet sheet = workbook.createSheet("Ringkasan Laporan");
        
        // Styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("LAPORAN KEUANGAN RENTAL BUS");
        titleCell.setCellStyle(titleStyle);
        
        // Periode
        Row periodeRow = sheet.createRow(rowNum++);
        periodeRow.createCell(0).setCellValue("Periode: " + dateFormat.format(tanggalMulai) + " s/d " + dateFormat.format(tanggalSelesai));
        rowNum++; // Empty row
        
        // PENDAPATAN
        Row pendapatanHeaderRow = sheet.createRow(rowNum++);
        Cell pendapatanHeaderCell = pendapatanHeaderRow.createCell(0);
        pendapatanHeaderCell.setCellValue("PENDAPATAN");
        pendapatanHeaderCell.setCellStyle(headerStyle);
        
        createDataRow(sheet, rowNum++, "Total Pendapatan", laporan.getTotalPendapatan(), currencyStyle);
        createDataRow(sheet, rowNum++, "  - Cash", laporan.getPendapatanCash(), currencyStyle);
        createDataRow(sheet, rowNum++, "  - Transfer", laporan.getPendapatanTransfer(), currencyStyle);
        createDataRow(sheet, rowNum++, "  - E-Wallet", laporan.getPendapatanEwallet(), currencyStyle);
        rowNum++; // Empty row
        
        // PENGELUARAN
        Row pengeluaranHeaderRow = sheet.createRow(rowNum++);
        Cell pengeluaranHeaderCell = pengeluaranHeaderRow.createCell(0);
        pengeluaranHeaderCell.setCellValue("PENGELUARAN");
        pengeluaranHeaderCell.setCellStyle(headerStyle);
        
        createDataRow(sheet, rowNum++, "Total Biaya Sopir", laporan.getTotalBiayaSopir(), currencyStyle);
        createDataRow(sheet, rowNum++, "  - Sudah Dibayar", laporan.getBiayaSopirSudahBayar(), currencyStyle);
        createDataRow(sheet, rowNum++, "  - Belum Dibayar", laporan.getBiayaSopirBelumBayar(), currencyStyle);
        rowNum++; // Empty row
        
        // KEUNTUNGAN
        Row keuntunganHeaderRow = sheet.createRow(rowNum++);
        Cell keuntunganHeaderCell = keuntunganHeaderRow.createCell(0);
        keuntunganHeaderCell.setCellValue("KEUNTUNGAN");
        keuntunganHeaderCell.setCellStyle(headerStyle);
        
        createDataRow(sheet, rowNum++, "Keuntungan Kotor", laporan.getKeuntunganKotor(), currencyStyle);
        createDataRow(sheet, rowNum++, "Keuntungan Bersih", laporan.getKeuntunganBersih(), currencyStyle);
        
        // Margin keuntungan
        if (laporan.getTotalPendapatan() > 0) {
            double marginPersen = (laporan.getKeuntunganBersih() / laporan.getTotalPendapatan()) * 100;
            Row marginRow = sheet.createRow(rowNum++);
            marginRow.createCell(0).setCellValue("Margin Keuntungan");
            Cell marginCell = marginRow.createCell(1);
            marginCell.setCellValue(marginPersen / 100);
            marginCell.setCellStyle(percentStyle);
        }
        rowNum++; // Empty row
        
        // STATISTIK
        Row statsHeaderRow = sheet.createRow(rowNum++);
        Cell statsHeaderCell = statsHeaderRow.createCell(0);
        statsHeaderCell.setCellValue("STATISTIK BOOKING");
        statsHeaderCell.setCellStyle(headerStyle);
        
        createDataRow(sheet, rowNum++, "Total Booking", laporan.getTotalBooking());
        createDataRow(sheet, rowNum++, "  - Selesai", laporan.getBookingSelesai());
        createDataRow(sheet, rowNum++, "  - Dikonfirmasi", laporan.getBookingDikonfirmasi());
        createDataRow(sheet, rowNum++, "  - Dibatalkan", laporan.getBookingDibatalkan());
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private static void createDetailTransaksiSheet(Workbook workbook, List<Map<String, Object>> transaksiList) {
        Sheet sheet = workbook.createSheet("Detail Transaksi");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        
        // Header
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "Kode Booking", "Tanggal", "Pelanggan", "Bus", "Tipe", 
            "Tgl Mulai", "Tgl Selesai", "Lama Sewa", "Total Harga", 
            "Dibayar", "Metode", "Status Bayar", "Biaya Sopir", "Sopir", "Keuntungan"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data
        int rowNum = 1;
        for (Map<String, Object> transaksi : transaksiList) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue((String) transaksi.get("kode_booking"));
            
            Cell dateCell = row.createCell(1);
            dateCell.setCellValue((Date) transaksi.get("tanggal_booking"));
            dateCell.setCellStyle(dateStyle);
            
            row.createCell(2).setCellValue((String) transaksi.get("nama_pelanggan"));
            row.createCell(3).setCellValue((String) transaksi.get("no_polisi"));
            row.createCell(4).setCellValue((String) transaksi.get("tipe_bus"));
            
            Cell startDateCell = row.createCell(5);
            startDateCell.setCellValue((Date) transaksi.get("tanggal_mulai"));
            startDateCell.setCellStyle(dateStyle);
            
            Cell endDateCell = row.createCell(6);
            endDateCell.setCellValue((Date) transaksi.get("tanggal_selesai"));
            endDateCell.setCellStyle(dateStyle);
            
            row.createCell(7).setCellValue((Integer) transaksi.get("lama_sewa"));
            
            Cell totalHargaCell = row.createCell(8);
            totalHargaCell.setCellValue((Double) transaksi.get("total_harga"));
            totalHargaCell.setCellStyle(currencyStyle);
            
            Cell dibayarCell = row.createCell(9);
            dibayarCell.setCellValue((Double) transaksi.get("jumlah_dibayar"));
            dibayarCell.setCellStyle(currencyStyle);
            
            row.createCell(10).setCellValue((String) transaksi.get("metode_bayar"));
            row.createCell(11).setCellValue((String) transaksi.get("status_bayar"));
            
            Cell biayaSopirCell = row.createCell(12);
            biayaSopirCell.setCellValue((Double) transaksi.get("biaya_sopir"));
            biayaSopirCell.setCellStyle(currencyStyle);
            
            row.createCell(13).setCellValue((String) transaksi.get("nama_sopir"));
            
            Cell keuntunganCell = row.createCell(14);
            keuntunganCell.setCellValue((Double) transaksi.get("keuntungan"));
            keuntunganCell.setCellStyle(currencyStyle);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private static void createAnalisisPendapatanSheet(Workbook workbook, LaporanKeuangan laporan) {
        Sheet sheet = workbook.createSheet("Analisis Pendapatan");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle percentStyle = createPercentStyle(workbook);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.createCell(0).setCellValue("ANALISIS PENDAPATAN PER METODE PEMBAYARAN");
        rowNum++; // Empty row
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Metode Pembayaran");
        Cell headerCell1 = headerRow.createCell(1);
        headerCell1.setCellValue("Jumlah");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = headerRow.createCell(2);
        headerCell2.setCellValue("Persentase");
        headerCell2.setCellStyle(headerStyle);
        
        // Data
        double totalPendapatan = laporan.getTotalPendapatan();
        
        // Cash
        Row cashRow = sheet.createRow(rowNum++);
        cashRow.createCell(0).setCellValue("Cash");
        Cell cashCell = cashRow.createCell(1);
        cashCell.setCellValue(laporan.getPendapatanCash());
        cashCell.setCellStyle(currencyStyle);
        Cell cashPercentCell = cashRow.createCell(2);
        if (totalPendapatan > 0) {
            cashPercentCell.setCellValue(laporan.getPendapatanCash() / totalPendapatan);
        } else {
            cashPercentCell.setCellValue(0);
        }
        cashPercentCell.setCellStyle(percentStyle);
        
        // Transfer
        Row transferRow = sheet.createRow(rowNum++);
        transferRow.createCell(0).setCellValue("Transfer");
        Cell transferCell = transferRow.createCell(1);
        transferCell.setCellValue(laporan.getPendapatanTransfer());
        transferCell.setCellStyle(currencyStyle);
        Cell transferPercentCell = transferRow.createCell(2);
        if (totalPendapatan > 0) {
            transferPercentCell.setCellValue(laporan.getPendapatanTransfer() / totalPendapatan);
        } else {
            transferPercentCell.setCellValue(0);
        }
        transferPercentCell.setCellStyle(percentStyle);
        
        // E-Wallet
        Row ewalletRow = sheet.createRow(rowNum++);
        ewalletRow.createCell(0).setCellValue("E-Wallet");
        Cell ewalletCell = ewalletRow.createCell(1);
        ewalletCell.setCellValue(laporan.getPendapatanEwallet());
        ewalletCell.setCellStyle(currencyStyle);
        Cell ewalletPercentCell = ewalletRow.createCell(2);
        if (totalPendapatan > 0) {
            ewalletPercentCell.setCellValue(laporan.getPendapatanEwallet() / totalPendapatan);
        } else {
            ewalletPercentCell.setCellValue(0);
        }
        ewalletPercentCell.setCellStyle(percentStyle);
        
        rowNum++; // Empty row
        
        // Total
        Row totalRow = sheet.createRow(rowNum++);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(headerStyle);
        Cell totalCell = totalRow.createCell(1);
        totalCell.setCellValue(totalPendapatan);
        totalCell.setCellStyle(currencyStyle);
        Cell totalPercentCell = totalRow.createCell(2);
        totalPercentCell.setCellValue(1.0);
        totalPercentCell.setCellStyle(percentStyle);
        
        // Auto-size columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }
    
    private static void createDataRow(Sheet sheet, int rowNum, String label, double value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);
    }
    
    private static void createDataRow(Sheet sheet, int rowNum, String label, int value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }
    
    private static CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("\"Rp\"#,##0.00"));
        return style;
    }
    
    private static CellStyle createPercentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }
    
    private static CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }
}