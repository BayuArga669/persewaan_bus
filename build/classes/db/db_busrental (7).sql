-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 02, 2025 at 06:52 PM
-- Server version: 10.4.32-MariaDB-log
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_busrental`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_assignment_sopir`
--

CREATE TABLE `tbl_assignment_sopir` (
  `id_assignment` int(11) NOT NULL,
  `id_booking` int(11) NOT NULL,
  `id_sopir` int(11) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_biaya_operasional`
--

CREATE TABLE `tbl_biaya_operasional` (
  `id_biaya` int(11) NOT NULL,
  `id_booking` int(11) NOT NULL,
  `tanggal_biaya` datetime DEFAULT current_timestamp(),
  `jenis_biaya` enum('gaji_sopir','bbm','tol','parkir','makan_sopir','maintenance','lainnya') NOT NULL,
  `keterangan` text DEFAULT NULL,
  `jumlah` decimal(12,2) NOT NULL,
  `bukti` varchar(255) DEFAULT NULL,
  `status_bayar` enum('belum_bayar','sudah_bayar') DEFAULT 'belum_bayar',
  `tanggal_bayar` datetime DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_biaya_operasional`
--

INSERT INTO `tbl_biaya_operasional` (`id_biaya`, `id_booking`, `tanggal_biaya`, `jenis_biaya`, `keterangan`, `jumlah`, `bukti`, `status_bayar`, `tanggal_bayar`, `created_by`, `created_at`, `updated_at`) VALUES
(1, 1, '2025-11-30 17:03:08', 'gaji_sopir', 'Gaji sopir untuk trip Jakarta', 300000.00, NULL, 'sudah_bayar', '2025-11-21 10:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(2, 1, '2025-11-30 17:03:08', 'bbm', 'BBM Solar 50 liter', 450000.00, NULL, 'sudah_bayar', '2025-11-21 10:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(3, 1, '2025-11-30 17:03:08', 'tol', 'Tol Jakarta-Bandung PP', 150000.00, NULL, 'sudah_bayar', '2025-11-21 10:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(4, 1, '2025-11-30 17:03:08', 'makan_sopir', 'Uang makan sopir', 100000.00, NULL, 'sudah_bayar', '2025-11-21 10:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(5, 2, '2025-11-30 17:03:08', 'gaji_sopir', 'Gaji sopir untuk trip Pacet', 250000.00, NULL, 'sudah_bayar', '2025-11-21 15:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(6, 2, '2025-11-30 17:03:08', 'bbm', 'BBM Solar 30 liter', 270000.00, NULL, 'sudah_bayar', '2025-11-21 15:00:00', 1, '2025-11-30 10:03:08', '2025-11-30 10:03:08'),
(7, 10, '2025-11-30 17:04:23', 'bbm', 'BBM', 10000.00, NULL, 'sudah_bayar', '2025-11-30 00:00:00', 1, '2025-11-30 10:04:23', '2025-11-30 10:04:23'),
(8, 10, '2025-11-30 17:36:56', 'bbm', 'Biaya Slar 100L', 500000.00, NULL, 'sudah_bayar', '2025-11-30 17:36:54', 1, '2025-11-30 10:36:56', '2025-11-30 10:36:56'),
(9, 10, '2025-11-30 19:28:17', 'maintenance', 'Service Rutin Bulanan', 1000000.00, NULL, 'sudah_bayar', '2025-11-30 19:28:15', 1, '2025-11-30 12:28:17', '2025-11-30 12:28:28');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_booking`
--

CREATE TABLE `tbl_booking` (
  `id_booking` int(11) NOT NULL,
  `kode_booking` varchar(20) NOT NULL,
  `id_pelanggan` int(11) NOT NULL,
  `id_bus` int(11) NOT NULL,
  `id_kasir` int(11) NOT NULL,
  `tanggal_booking` datetime DEFAULT current_timestamp(),
  `tanggal_mulai` date NOT NULL,
  `tanggal_selesai` date NOT NULL,
  `tujuan` text NOT NULL,
  `jumlah_penumpang` int(11) NOT NULL,
  `lama_sewa` int(11) NOT NULL,
  `total_harga` decimal(12,2) NOT NULL,
  `status_booking` enum('pending','dikonfirmasi','selesai','dibatalkan') DEFAULT 'pending',
  `catatan` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_booking`
--

INSERT INTO `tbl_booking` (`id_booking`, `kode_booking`, `id_pelanggan`, `id_bus`, `id_kasir`, `tanggal_booking`, `tanggal_mulai`, `tanggal_selesai`, `tujuan`, `jumlah_penumpang`, `lama_sewa`, `total_harga`, `status_booking`, `catatan`, `created_at`, `updated_at`) VALUES
(1, 'BKG00001', 1, 1, 2, '2025-11-21 07:25:35', '2025-11-21', '2025-11-22', 'Jakarta', 20, 1, 1500000.00, 'selesai', 'awdawd', '2025-11-21 00:25:35', '2025-11-21 08:21:44'),
(2, 'BKG00002', 2, 4, 1, '2025-11-21 08:40:50', '2025-11-21', '2025-11-22', 'Pacet', 15, 1, 800000.00, 'selesai', 'Test2', '2025-11-21 01:40:50', '2025-11-23 09:04:59'),
(3, 'BKG00003', 1, 2, 1, '2025-11-22 15:31:50', '2025-11-22', '2025-11-23', 'Jayakarta', 100, 1, 2500000.00, 'dibatalkan', '', '2025-11-22 08:31:50', '2025-11-21 09:02:19'),
(4, 'BKG00004', 1, 3, 1, '2025-11-21 16:03:30', '2025-11-21', '2025-11-22', 'Jakarta', 10, 1, 3500000.00, 'dibatalkan', '', '2025-11-21 09:03:30', '2025-11-23 09:04:59'),
(5, 'BKG00005', 1, 2, 1, '2025-11-23 17:28:06', '2025-11-23', '2025-11-24', 'Majalengka', 10, 1, 2500000.00, 'selesai', 'TESTTTTT', '2025-11-23 10:28:06', '2025-11-25 10:30:51'),
(6, 'BKG00006', 2, 3, 1, '2025-11-26 17:30:22', '2025-11-26', '2025-11-27', 'Surabaya', 40, 1, 3500000.00, 'selesai', 'test', '2025-11-26 10:30:22', '2025-11-30 10:47:23'),
(7, 'BKG00007', 1, 2, 1, '2025-11-26 10:23:42', '2025-11-28', '2025-11-29', 'Malang', 30, 1, 2500000.00, 'selesai', '', '2025-11-26 03:23:42', '2025-11-30 10:47:23'),
(8, 'BKG00008', 1, 1, 1, '2025-11-26 16:23:24', '2025-11-28', '2025-11-29', 'Jakarta', 10, 1, 1500000.00, 'dibatalkan', '', '2025-11-26 09:23:24', '2025-12-03 05:36:08'),
(9, 'BKG00009', 2, 1, 1, '2025-11-26 16:50:17', '2025-11-30', '2025-12-02', 'Jakarta', 10, 2, 3000000.00, 'dikonfirmasi', '', '2025-11-26 09:50:17', '2025-11-30 10:46:34'),
(10, 'BKG00010', 2, 5, 1, '2025-11-28 12:51:30', '2025-11-28', '2025-11-29', 'Jawa', 10, 1, 100000.00, 'selesai', '', '2025-11-28 05:51:30', '2025-11-30 10:47:23'),
(11, 'BKG00011', 1, 5, 2, '2025-12-02 18:39:25', '2025-12-02', '2025-12-03', 'Jakarta', 10, 1, 100000.00, 'dikonfirmasi', '', '2025-12-02 11:39:25', '2025-12-02 11:39:25'),
(12, 'BKG00012', 3, 5, 2, '2025-12-02 18:39:59', '2025-12-05', '2025-12-06', 'Jakarta', 20, 1, 100000.00, 'pending', '', '2025-12-02 11:39:59', '2025-12-02 11:39:59');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_bus`
--

CREATE TABLE `tbl_bus` (
  `id_bus` int(11) NOT NULL,
  `no_polisi` varchar(20) NOT NULL,
  `tipe_bus` varchar(50) NOT NULL,
  `merk` varchar(50) DEFAULT NULL,
  `tahun_pembuatan` int(11) DEFAULT NULL,
  `kapasitas` int(11) NOT NULL,
  `fasilitas` text DEFAULT NULL,
  `harga_per_hari` decimal(12,2) NOT NULL,
  `foto` varchar(255) DEFAULT NULL,
  `status` enum('tersedia','disewa','maintenance') DEFAULT 'tersedia',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_bus`
--

INSERT INTO `tbl_bus` (`id_bus`, `no_polisi`, `tipe_bus`, `merk`, `tahun_pembuatan`, `kapasitas`, `fasilitas`, `harga_per_hari`, `foto`, `status`, `created_at`, `updated_at`) VALUES
(1, 'B 1234 ABC', 'Medium', 'Mercedes-Benz', 2020, 35, 'AC, TV, Audio System, Reclining Seat', 1500000.00, '', 'tersedia', '2025-11-21 00:22:18', '2025-11-30 10:47:23'),
(2, 'B 5678 DEF', 'Big', 'Hino', 2019, 50, 'AC, TV, Karaoke, Toilet, Reclining Seat', 2500000.00, '', 'tersedia', '2025-11-21 00:22:18', '2025-11-30 10:47:23'),
(3, 'B 9012 GHI', 'VIP', 'Scania', 2021, 30, 'AC, TV, Karaoke, Toilet, Full Reclining Seat, WiFi', 3500000.00, '', 'tersedia', '2025-11-21 00:22:18', '2025-11-30 10:47:23'),
(4, 'B 3456 JKL', 'Mini', 'Isuzu Elf', 2022, 15, 'AC, Audio System', 800000.00, '', 'tersedia', '2025-11-21 00:22:18', '2025-11-26 13:00:16'),
(5, 'AG 8921 DC', 'Medium', 'Hino', 2020, 35, 'Ac, Kipas, Jendela, Dll', 100000.00, 'bus_1764163044199.png', 'disewa', '2025-11-26 13:18:20', '2025-12-02 11:39:25');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_pelanggan`
--

CREATE TABLE `tbl_pelanggan` (
  `id_pelanggan` int(11) NOT NULL,
  `nama_pelanggan` varchar(100) NOT NULL,
  `no_telp` varchar(20) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `no_ktp` varchar(20) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_pelanggan`
--

INSERT INTO `tbl_pelanggan` (`id_pelanggan`, `nama_pelanggan`, `no_telp`, `email`, `alamat`, `no_ktp`, `created_at`, `updated_at`) VALUES
(1, 'Ahmad Wijaya', '081234567891', 'ahmad@email.com', 'Jl. Sudirman No. 10, Jakarta', '3174012345670001', '2025-11-21 00:22:18', '2025-11-21 00:22:18'),
(2, 'Siti Nurhaliza', '081234567892', 'siti@email.com', 'Jl. Gatot Subroto No. 25, Jakarta', '3174012345670002', '2025-11-21 00:22:18', '2025-11-21 00:22:18'),
(3, 'Albert Welder', '08595219213', 'AlbertWelder@gmail.com', 'Jl Raya Surabaya Sidoarjo', '31440123456700001', '2025-11-30 11:53:17', '2025-11-30 11:53:17');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_pembayaran`
--

CREATE TABLE `tbl_pembayaran` (
  `id_pembayaran` int(11) NOT NULL,
  `id_booking` int(11) NOT NULL,
  `tanggal_bayar` datetime DEFAULT current_timestamp(),
  `jumlah_bayar` decimal(12,2) NOT NULL,
  `metode_bayar` enum('cash','transfer','ewallet') NOT NULL,
  `bukti_transfer` varchar(255) DEFAULT NULL,
  `status_bayar` enum('belum_bayar','dp','lunas') NOT NULL,
  `keterangan` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_pembayaran`
--

INSERT INTO `tbl_pembayaran` (`id_pembayaran`, `id_booking`, `tanggal_bayar`, `jumlah_bayar`, `metode_bayar`, `bukti_transfer`, `status_bayar`, `keterangan`, `created_at`, `updated_at`) VALUES
(1, 5, '2025-11-25 17:42:17', 25000000.00, 'cash', '', 'lunas', 'test', '2025-11-25 10:42:43', '2025-11-25 10:54:13'),
(2, 2, '2025-11-25 18:03:56', 800000.00, 'cash', '', 'lunas', 'test', '2025-11-25 11:04:12', '2025-11-25 11:04:42'),
(3, 1, '2025-11-25 18:37:05', 1500000.00, 'cash', '', 'lunas', 'testttttt', '2025-11-25 11:37:33', '2025-11-25 11:37:43'),
(4, 10, '2025-11-30 17:43:51', 10000.00, 'cash', NULL, 'lunas', '', '2025-11-30 10:44:16', '2025-11-30 10:44:16'),
(5, 10, '2025-11-30 17:44:23', 90000.00, 'cash', NULL, 'lunas', '', '2025-11-30 10:44:41', '2025-11-30 10:44:41'),
(6, 6, '2025-11-30 17:44:43', 3500000.00, 'cash', NULL, 'lunas', '', '2025-11-30 10:44:56', '2025-11-30 10:44:56'),
(7, 9, '2025-11-30 17:44:58', 3000000.00, 'cash', NULL, 'lunas', '', '2025-11-30 10:46:34', '2025-11-30 10:46:34'),
(8, 7, '2025-11-30 17:46:36', 2500000.00, 'cash', NULL, 'lunas', '', '2025-11-30 10:46:46', '2025-11-30 10:46:46'),
(9, 11, '2025-12-02 18:40:54', 100000.00, 'cash', NULL, 'lunas', '', '2025-12-02 11:41:18', '2025-12-02 11:41:18');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_sopir`
--

CREATE TABLE `tbl_sopir` (
  `id_sopir` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `no_sim` varchar(20) NOT NULL,
  `jenis_sim` varchar(10) NOT NULL,
  `masa_berlaku_sim` date NOT NULL,
  `status_sopir` enum('aktif','nonaktif','cuti') DEFAULT 'aktif',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_users`
--

CREATE TABLE `tbl_users` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `no_telp` varchar(20) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `role` enum('admin','kasir','sopir') NOT NULL,
  `status` enum('aktif','nonaktif') DEFAULT 'aktif',
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbl_users`
--

INSERT INTO `tbl_users` (`id_user`, `username`, `password`, `nama_lengkap`, `email`, `no_telp`, `alamat`, `role`, `status`, `created_at`, `updated_at`) VALUES
(1, 'admin', 'admin123', 'Administrator', 'admin@buspariwisata.com', NULL, NULL, 'admin', 'aktif', '2025-11-21 00:22:18', '2025-11-21 00:22:18'),
(2, 'kasir1', 'kasir123', 'Budi Santoso', 'budi@buspariwisata.com', '081234567890', NULL, 'kasir', 'aktif', '2025-11-21 00:22:18', '2025-11-21 00:22:18');

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_biaya_operasional_booking`
-- (See below for the actual view)
--
CREATE TABLE `view_biaya_operasional_booking` (
`id_biaya` int(11)
,`id_booking` int(11)
,`kode_booking` varchar(20)
,`tanggal_mulai` date
,`tanggal_selesai` date
,`total_harga` decimal(12,2)
,`nama_pelanggan` varchar(100)
,`no_polisi` varchar(20)
,`tipe_bus` varchar(50)
,`jenis_biaya` enum('gaji_sopir','bbm','tol','parkir','makan_sopir','maintenance','lainnya')
,`keterangan` text
,`jumlah` decimal(12,2)
,`status_bayar` enum('belum_bayar','sudah_bayar')
,`tanggal_biaya` datetime
,`tanggal_bayar` datetime
,`created_by_name` varchar(100)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_dashboard_stats`
-- (See below for the actual view)
--
CREATE TABLE `view_dashboard_stats` (
`total_booking_aktif` bigint(21)
,`total_bus_tersedia` bigint(21)
,`total_bus_disewa` bigint(21)
,`pendapatan_bulan_ini` decimal(34,2)
,`pendapatan_hari_ini` decimal(34,2)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_laporan_transaksi`
-- (See below for the actual view)
--
CREATE TABLE `view_laporan_transaksi` (
`kode_booking` varchar(20)
,`tanggal_booking` datetime
,`nama_pelanggan` varchar(100)
,`no_telp` varchar(20)
,`no_polisi` varchar(20)
,`tipe_bus` varchar(50)
,`tanggal_mulai` date
,`tanggal_selesai` date
,`lama_sewa` int(11)
,`total_harga` decimal(12,2)
,`status_booking` enum('pending','dikonfirmasi','selesai','dibatalkan')
,`status_bayar` enum('belum_bayar','dp','lunas')
,`jumlah_dibayar` decimal(12,2)
,`nama_kasir` varchar(100)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_ringkasan_biaya_per_booking`
-- (See below for the actual view)
--
CREATE TABLE `view_ringkasan_biaya_per_booking` (
`id_booking` int(11)
,`kode_booking` varchar(20)
,`total_harga` decimal(12,2)
,`total_gaji_sopir` decimal(34,2)
,`total_bbm` decimal(34,2)
,`total_tol` decimal(34,2)
,`total_parkir` decimal(34,2)
,`total_makan_sopir` decimal(34,2)
,`total_maintenance` decimal(34,2)
,`total_lainnya` decimal(34,2)
,`total_biaya_operasional` decimal(34,2)
,`sudah_bayar` decimal(34,2)
,`belum_bayar` decimal(34,2)
,`keuntungan_bersih` decimal(35,2)
);

-- --------------------------------------------------------

--
-- Stand-in structure for view `view_sopir_order_pendapatan`
-- (See below for the actual view)
--
CREATE TABLE `view_sopir_order_pendapatan` (
`id_assignment` int(11)
,`id_booking` int(11)
,`id_sopir` int(11)
,`kode_booking` varchar(20)
,`tanggal_mulai` date
,`tanggal_selesai` date
,`tujuan` text
,`status_booking` enum('pending','dikonfirmasi','selesai','dibatalkan')
,`nama_pelanggan` varchar(100)
,`no_polisi` varchar(20)
,`nama_sopir` varchar(100)
,`keterangan` text
);

-- --------------------------------------------------------

--
-- Structure for view `view_biaya_operasional_booking`
--
DROP TABLE IF EXISTS `view_biaya_operasional_booking`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_biaya_operasional_booking`  AS SELECT `bo`.`id_biaya` AS `id_biaya`, `bo`.`id_booking` AS `id_booking`, `b`.`kode_booking` AS `kode_booking`, `b`.`tanggal_mulai` AS `tanggal_mulai`, `b`.`tanggal_selesai` AS `tanggal_selesai`, `b`.`total_harga` AS `total_harga`, `p`.`nama_pelanggan` AS `nama_pelanggan`, `bus`.`no_polisi` AS `no_polisi`, `bus`.`tipe_bus` AS `tipe_bus`, `bo`.`jenis_biaya` AS `jenis_biaya`, `bo`.`keterangan` AS `keterangan`, `bo`.`jumlah` AS `jumlah`, `bo`.`status_bayar` AS `status_bayar`, `bo`.`tanggal_biaya` AS `tanggal_biaya`, `bo`.`tanggal_bayar` AS `tanggal_bayar`, `u`.`nama_lengkap` AS `created_by_name` FROM ((((`tbl_biaya_operasional` `bo` join `tbl_booking` `b` on(`bo`.`id_booking` = `b`.`id_booking`)) join `tbl_pelanggan` `p` on(`b`.`id_pelanggan` = `p`.`id_pelanggan`)) join `tbl_bus` `bus` on(`b`.`id_bus` = `bus`.`id_bus`)) join `tbl_users` `u` on(`bo`.`created_by` = `u`.`id_user`)) ORDER BY `bo`.`created_at` DESC ;

-- --------------------------------------------------------

--
-- Structure for view `view_dashboard_stats`
--
DROP TABLE IF EXISTS `view_dashboard_stats`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_dashboard_stats`  AS SELECT (select count(0) from `tbl_booking` where `tbl_booking`.`status_booking` = 'dikonfirmasi') AS `total_booking_aktif`, (select count(0) from `tbl_bus` where `tbl_bus`.`status` = 'tersedia') AS `total_bus_tersedia`, (select count(0) from `tbl_bus` where `tbl_bus`.`status` = 'disewa') AS `total_bus_disewa`, (select coalesce(sum(`tbl_booking`.`total_harga`),0) from `tbl_booking` where month(`tbl_booking`.`tanggal_booking`) = month(curdate()) and year(`tbl_booking`.`tanggal_booking`) = year(curdate())) AS `pendapatan_bulan_ini`, (select coalesce(sum(`tbl_booking`.`total_harga`),0) from `tbl_booking` where cast(`tbl_booking`.`tanggal_booking` as date) = curdate()) AS `pendapatan_hari_ini` ;

-- --------------------------------------------------------

--
-- Structure for view `view_laporan_transaksi`
--
DROP TABLE IF EXISTS `view_laporan_transaksi`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_laporan_transaksi`  AS SELECT `b`.`kode_booking` AS `kode_booking`, `b`.`tanggal_booking` AS `tanggal_booking`, `p`.`nama_pelanggan` AS `nama_pelanggan`, `p`.`no_telp` AS `no_telp`, `bus`.`no_polisi` AS `no_polisi`, `bus`.`tipe_bus` AS `tipe_bus`, `b`.`tanggal_mulai` AS `tanggal_mulai`, `b`.`tanggal_selesai` AS `tanggal_selesai`, `b`.`lama_sewa` AS `lama_sewa`, `b`.`total_harga` AS `total_harga`, `b`.`status_booking` AS `status_booking`, `pby`.`status_bayar` AS `status_bayar`, coalesce(`pby`.`jumlah_bayar`,0) AS `jumlah_dibayar`, `u`.`nama_lengkap` AS `nama_kasir` FROM ((((`tbl_booking` `b` join `tbl_pelanggan` `p` on(`b`.`id_pelanggan` = `p`.`id_pelanggan`)) join `tbl_bus` `bus` on(`b`.`id_bus` = `bus`.`id_bus`)) join `tbl_users` `u` on(`b`.`id_kasir` = `u`.`id_user`)) left join `tbl_pembayaran` `pby` on(`b`.`id_booking` = `pby`.`id_booking`)) ORDER BY `b`.`tanggal_booking` DESC ;

-- --------------------------------------------------------

--
-- Structure for view `view_ringkasan_biaya_per_booking`
--
DROP TABLE IF EXISTS `view_ringkasan_biaya_per_booking`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_ringkasan_biaya_per_booking`  AS SELECT `b`.`id_booking` AS `id_booking`, `b`.`kode_booking` AS `kode_booking`, `b`.`total_harga` AS `total_harga`, coalesce(sum(case when `bo`.`jenis_biaya` = 'gaji_sopir' then `bo`.`jumlah` else 0 end),0) AS `total_gaji_sopir`, coalesce(sum(case when `bo`.`jenis_biaya` = 'bbm' then `bo`.`jumlah` else 0 end),0) AS `total_bbm`, coalesce(sum(case when `bo`.`jenis_biaya` = 'tol' then `bo`.`jumlah` else 0 end),0) AS `total_tol`, coalesce(sum(case when `bo`.`jenis_biaya` = 'parkir' then `bo`.`jumlah` else 0 end),0) AS `total_parkir`, coalesce(sum(case when `bo`.`jenis_biaya` = 'makan_sopir' then `bo`.`jumlah` else 0 end),0) AS `total_makan_sopir`, coalesce(sum(case when `bo`.`jenis_biaya` = 'maintenance' then `bo`.`jumlah` else 0 end),0) AS `total_maintenance`, coalesce(sum(case when `bo`.`jenis_biaya` = 'lainnya' then `bo`.`jumlah` else 0 end),0) AS `total_lainnya`, coalesce(sum(`bo`.`jumlah`),0) AS `total_biaya_operasional`, coalesce(sum(case when `bo`.`status_bayar` = 'sudah_bayar' then `bo`.`jumlah` else 0 end),0) AS `sudah_bayar`, coalesce(sum(case when `bo`.`status_bayar` = 'belum_bayar' then `bo`.`jumlah` else 0 end),0) AS `belum_bayar`, `b`.`total_harga`- coalesce(sum(`bo`.`jumlah`),0) AS `keuntungan_bersih` FROM (`tbl_booking` `b` left join `tbl_biaya_operasional` `bo` on(`b`.`id_booking` = `bo`.`id_booking`)) GROUP BY `b`.`id_booking` ;

-- --------------------------------------------------------

--
-- Structure for view `view_sopir_order_pendapatan`
--
DROP TABLE IF EXISTS `view_sopir_order_pendapatan`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `view_sopir_order_pendapatan`  AS SELECT `a`.`id_assignment` AS `id_assignment`, `a`.`id_booking` AS `id_booking`, `a`.`id_sopir` AS `id_sopir`, `b`.`kode_booking` AS `kode_booking`, `b`.`tanggal_mulai` AS `tanggal_mulai`, `b`.`tanggal_selesai` AS `tanggal_selesai`, `b`.`tujuan` AS `tujuan`, `b`.`status_booking` AS `status_booking`, `p`.`nama_pelanggan` AS `nama_pelanggan`, `bus`.`no_polisi` AS `no_polisi`, `u_sopir`.`nama_lengkap` AS `nama_sopir`, `a`.`keterangan` AS `keterangan` FROM (((((`tbl_assignment_sopir` `a` join `tbl_booking` `b` on(`a`.`id_booking` = `b`.`id_booking`)) join `tbl_pelanggan` `p` on(`b`.`id_pelanggan` = `p`.`id_pelanggan`)) join `tbl_bus` `bus` on(`b`.`id_bus` = `bus`.`id_bus`)) join `tbl_sopir` `s` on(`a`.`id_sopir` = `s`.`id_sopir`)) join `tbl_users` `u_sopir` on(`s`.`id_user` = `u_sopir`.`id_user`)) ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_assignment_sopir`
--
ALTER TABLE `tbl_assignment_sopir`
  ADD PRIMARY KEY (`id_assignment`),
  ADD KEY `id_booking` (`id_booking`),
  ADD KEY `id_sopir` (`id_sopir`);

--
-- Indexes for table `tbl_biaya_operasional`
--
ALTER TABLE `tbl_biaya_operasional`
  ADD PRIMARY KEY (`id_biaya`),
  ADD KEY `id_booking` (`id_booking`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `tbl_booking`
--
ALTER TABLE `tbl_booking`
  ADD PRIMARY KEY (`id_booking`),
  ADD UNIQUE KEY `kode_booking` (`kode_booking`),
  ADD KEY `id_pelanggan` (`id_pelanggan`),
  ADD KEY `id_bus` (`id_bus`),
  ADD KEY `id_kasir` (`id_kasir`);

--
-- Indexes for table `tbl_bus`
--
ALTER TABLE `tbl_bus`
  ADD PRIMARY KEY (`id_bus`),
  ADD UNIQUE KEY `no_polisi` (`no_polisi`);

--
-- Indexes for table `tbl_pelanggan`
--
ALTER TABLE `tbl_pelanggan`
  ADD PRIMARY KEY (`id_pelanggan`);

--
-- Indexes for table `tbl_pembayaran`
--
ALTER TABLE `tbl_pembayaran`
  ADD PRIMARY KEY (`id_pembayaran`),
  ADD KEY `id_booking` (`id_booking`);

--
-- Indexes for table `tbl_sopir`
--
ALTER TABLE `tbl_sopir`
  ADD PRIMARY KEY (`id_sopir`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `tbl_users`
--
ALTER TABLE `tbl_users`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_assignment_sopir`
--
ALTER TABLE `tbl_assignment_sopir`
  MODIFY `id_assignment` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tbl_biaya_operasional`
--
ALTER TABLE `tbl_biaya_operasional`
  MODIFY `id_biaya` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `tbl_booking`
--
ALTER TABLE `tbl_booking`
  MODIFY `id_booking` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `tbl_bus`
--
ALTER TABLE `tbl_bus`
  MODIFY `id_bus` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `tbl_pelanggan`
--
ALTER TABLE `tbl_pelanggan`
  MODIFY `id_pelanggan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tbl_pembayaran`
--
ALTER TABLE `tbl_pembayaran`
  MODIFY `id_pembayaran` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `tbl_sopir`
--
ALTER TABLE `tbl_sopir`
  MODIFY `id_sopir` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `tbl_users`
--
ALTER TABLE `tbl_users`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tbl_assignment_sopir`
--
ALTER TABLE `tbl_assignment_sopir`
  ADD CONSTRAINT `tbl_assignment_sopir_ibfk_1` FOREIGN KEY (`id_booking`) REFERENCES `tbl_booking` (`id_booking`) ON DELETE CASCADE,
  ADD CONSTRAINT `tbl_assignment_sopir_ibfk_2` FOREIGN KEY (`id_sopir`) REFERENCES `tbl_sopir` (`id_sopir`) ON DELETE CASCADE;

--
-- Constraints for table `tbl_biaya_operasional`
--
ALTER TABLE `tbl_biaya_operasional`
  ADD CONSTRAINT `tbl_biaya_operasional_ibfk_1` FOREIGN KEY (`id_booking`) REFERENCES `tbl_booking` (`id_booking`) ON DELETE CASCADE,
  ADD CONSTRAINT `tbl_biaya_operasional_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `tbl_users` (`id_user`);

--
-- Constraints for table `tbl_booking`
--
ALTER TABLE `tbl_booking`
  ADD CONSTRAINT `tbl_booking_ibfk_1` FOREIGN KEY (`id_pelanggan`) REFERENCES `tbl_pelanggan` (`id_pelanggan`),
  ADD CONSTRAINT `tbl_booking_ibfk_2` FOREIGN KEY (`id_bus`) REFERENCES `tbl_bus` (`id_bus`),
  ADD CONSTRAINT `tbl_booking_ibfk_3` FOREIGN KEY (`id_kasir`) REFERENCES `tbl_users` (`id_user`);

--
-- Constraints for table `tbl_pembayaran`
--
ALTER TABLE `tbl_pembayaran`
  ADD CONSTRAINT `tbl_pembayaran_ibfk_1` FOREIGN KEY (`id_booking`) REFERENCES `tbl_booking` (`id_booking`);

--
-- Constraints for table `tbl_sopir`
--
ALTER TABLE `tbl_sopir`
  ADD CONSTRAINT `tbl_sopir_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `tbl_users` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
