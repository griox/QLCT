package database;

import javafx.fxml.FXML;

import java.sql.*;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseHelper {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=QLCT;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "Huy16032004@";

    // Đảm bảo rằng driver SQL Server được nạp
    static {
        try {
            // Đăng ký driver cho SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Không thể nạp driver SQL Server.");
            e.printStackTrace();
        }
    }

    // Kết nối đến cơ sở dữ liệu
    public static Connection connect() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
            }
        } catch (SQLException e) {
            System.out.println("Không thể kết nối đến cơ sở dữ liệu.");
            e.printStackTrace();
            throw e;  // Ném lại exception để caller có thể xử lý
        }
        return conn;
    }

    // Thêm dữ liệu vào bảng TienChi
    public static void insertData(String columnName,String ngay, int value, String date, String tenBang, String ghi) {


        String sql = "INSERT INTO "+ tenBang +" (" + columnName + " , " + ngay + ", ghiChu) VALUES (?, ?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Nếu không thể kết nối, không thực hiện thao tác insert
            if (conn == null) {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu, không thể thực hiện insert.");
                return;
            }

            pstmt.setInt(1, value);
            pstmt.setString(2, date);
            pstmt.setString(3,ghi);
            pstmt.executeUpdate();
            System.out.println("Inserted into TienChi successfully!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi thực hiện insert.");
            e.printStackTrace();
        }
    }
    public static int getSumValue(String date, String columnName, String tenBang, String loaiNgay){
        String sql = "SELECT SUM(" + columnName + ") AS total FROM " + tenBang + " WHERE "+loaiNgay+" = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, date); // Gán giá trị ngày vào câu lệnh SQL
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thực hiện truy vấn tổng giá trị với điều kiện ngày.");
            e.printStackTrace();
        }
        return 0;
    }
    public static List<String> getTransactionList(String date) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT loaiGiaoDich, noiDung, soTien, ghiChu FROM LichSuGiaoDich WHERE ngayGiaoDich = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String loaiGiaoDich = rs.getString("loaiGiaoDich");
                String noiDung = rs.getString("noiDung");
                int soTien = rs.getInt("soTien");
                String ghiChu = rs.getString("ghiChu");

                // Format dữ liệu giao dịch
                String record = String.format("%s:  %,dđ (%s)",  noiDung, soTien, ghiChu);
                transactions.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    public static List<String> getTransactionListThu(YearMonth month) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT " +
                "SUM(tienLuong) AS tongTienLuong, " +
                "SUM(tienThuong) AS tongTienThuong, " +
                "SUM(tienPhuCap) AS tongTienPhuCap, " +
                "SUM(tienLiXi) AS tongTienLiXi, " +
                "SUM(dauTu) AS tongDauTu, " +
                "SUM(thuNhapTamThoi) AS tongThuNhapTamThoi, " +
                "SUM(thuNhapPhu) AS tongThuNhapPhu, " +
                "SUM(tongTienThu) AS tongTienThu " +
                "FROM TienThu " +
                "WHERE MONTH(ngayThu) = ? AND YEAR(ngayThu) = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month.getMonthValue());
            pstmt.setInt(2, month.getYear());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double tongTienThu = rs.getDouble("tongTienThu");
                if (tongTienThu > 0) { // Chỉ hiển thị nếu có dữ liệu
                    // Thêm các nguồn thu nhập vào danh sách
                    addTransaction(transactions, "Tiền lương", rs.getDouble("tongTienLuong"), tongTienThu);
                    addTransaction(transactions, "Tiền Thưởng", rs.getDouble("tongTienThuong"), tongTienThu);
                    addTransaction(transactions, "Tiền lì xì", rs.getDouble("tongTienLiXi"), tongTienThu);
                    addTransaction(transactions, "Tiền Phụ cấp", rs.getDouble("tongTienPhuCap"), tongTienThu);
                    addTransaction(transactions, "Đầu tư", rs.getDouble("tongDauTu"), tongTienThu);
                    addTransaction(transactions, "Thu nhập tạm thời", rs.getDouble("tongThuNhapTamThoi"), tongTienThu);
                    addTransaction(transactions, "Thu nhập phụ", rs.getDouble("tongThuNhapPhu"), tongTienThu);
                } else {
                    transactions.add("Không có dữ liệu thu nhập trong tháng.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public static List<String> getTransactionListMonth(YearMonth month) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT " +
                "SUM(anUong) AS tongAnUong, " +
                "SUM(chiTraKhoanVay) AS tongChiTraKhoanVay, " +
                "SUM(quanAO) AS tongQuanAO, " +
                "SUM(myPham) AS tongMyPham, " +
                "SUM(phiGiaoLuu) AS tongPhiGiaoLuu, " +
                "SUM(yTe) AS tongYTe, " +
                "SUM(giaoDuc) AS tongGiaoDuc, " +
                "SUM(tienDienNuoc) AS tongTienDienNuoc, " +
                "SUM(diLai) AS tongDiLai, " +
                "SUM(phiLienLac) AS tongPhiLienLac, " +
                "SUM(tienNha) AS tongTienNha, " +
                "SUM(tienSuaChoCon) AS tongTienSuaChoCon, " +
                "SUM(tongTienChi) AS tongTienChi " +
                "FROM TienChi " +
                "WHERE MONTH(ngayChi) = ? AND YEAR(ngayChi) = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month.getMonthValue());
            pstmt.setInt(2, month.getYear());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double tongTienChi = rs.getDouble("tongTienChi");
                if (tongTienChi > 0) { // Chỉ hiển thị nếu có dữ liệu
                    // Thêm các loại chi tiêu vào danh sách
                    addTransaction(transactions, "Ăn uống", rs.getDouble("tongAnUong"), tongTienChi);
                    addTransaction(transactions, "Chi trả khoản vay", rs.getDouble("tongChiTraKhoanVay"), tongTienChi);
                    addTransaction(transactions, "Quần áo", rs.getDouble("tongQuanAO"), tongTienChi);
                    addTransaction(transactions, "Mỹ phẩm", rs.getDouble("tongMyPham"), tongTienChi);
                    addTransaction(transactions, "Phí giao lưu", rs.getDouble("tongPhiGiaoLuu"), tongTienChi);
                    addTransaction(transactions, "Y tế", rs.getDouble("tongYTe"), tongTienChi);
                    addTransaction(transactions, "Giáo dục", rs.getDouble("tongGiaoDuc"), tongTienChi);
                    addTransaction(transactions, "Tiền điện nước", rs.getDouble("tongTienDienNuoc"), tongTienChi);
                    addTransaction(transactions, "Đi lại", rs.getDouble("tongDiLai"), tongTienChi);
                    addTransaction(transactions, "Phí liên lạc", rs.getDouble("tongPhiLienLac"), tongTienChi);
                    addTransaction(transactions, "Tiền nhà", rs.getDouble("tongTienNha"), tongTienChi);
                    addTransaction(transactions, "Tiền sữa cho con", rs.getDouble("tongTienSuaChoCon"), tongTienChi);
                } else {
                    transactions.add("Không có dữ liệu chi tiêu trong tháng.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    private static void addTransaction(List<String> transactions, String category, double amount, double total) {
        if (amount > 0) { // Chỉ hiển thị nếu có giá trị
            double percentage = (amount / total) * 100;
            String formattedTransaction = String.format("%s: %,dđ (%.2f%%)", category, (long) amount, percentage);
            transactions.add(formattedTransaction);
        }
    }

    public static double getSumValueMonth(YearMonth month, String loaiGiaoDich) {
        String sql = "SELECT SUM(soTien) AS total FROM LichSuGiaoDich WHERE loaiGiaoDich = ? AND MONTH(ngayGiaoDich) = ? AND YEAR(ngayGiaoDich) = ?";
        double total = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, loaiGiaoDich);
            pstmt.setInt(2, month.getMonthValue()); // Lấy giá trị tháng
            pstmt.setInt(3, month.getYear());       // Lấy giá trị năm

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi thực hiện truy vấn tổng giá trị cho tháng và năm.");
            e.printStackTrace();
        }
        return total;
    }

    public static Map<String, Double> getChiTieuByCategory(YearMonth month) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT " +
                "SUM(anUong) AS totalAnUong, SUM(chiTraKhoanVay) AS totalChiTraKhoanVay, " +
                "SUM(quanAO) AS totalQuanAO, SUM(myPham) AS totalMyPham, " +
                "SUM(phiGiaoLuu) AS totalPhiGiaoLuu, SUM(yTe) AS totalYTe, " +
                "SUM(giaoDuc) AS totalGiaoDuc, SUM(tienDienNuoc) AS totalTienDienNuoc, " +
                "SUM(diLai) AS totalDiLai, SUM(phiLienLac) AS totalPhiLienLac, " +
                "SUM(tienNha) AS totalTienNha, SUM(tienSuaChoCon) AS totalTienSuaChoCon, " +
                "SUM(tongTienChi) AS totalChiTieu " +
                "FROM TienChi " +
                "WHERE YEAR(ngayChi) = ? AND MONTH(ngayChi) = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the year and month from the YearMonth object
            pstmt.setInt(1, month.getYear()); // Set the year
            pstmt.setInt(2, month.getMonthValue()); // Set the month

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                data.put("Ăn uống", rs.getDouble("totalAnUong"));
                data.put("Chi trả khoản vay", rs.getDouble("totalChiTraKhoanVay"));
                data.put("Quần áo", rs.getDouble("totalQuanAO"));
                data.put("Mỹ phẩm", rs.getDouble("totalMyPham"));
                data.put("Phí giao lưu", rs.getDouble("totalPhiGiaoLuu"));
                data.put("Y tế", rs.getDouble("totalYTe"));
                data.put("Giáo dục", rs.getDouble("totalGiaoDuc"));
                data.put("Điện nước", rs.getDouble("totalTienDienNuoc"));
                data.put("Đi lại", rs.getDouble("totalDiLai"));
                data.put("Phí liên lạc", rs.getDouble("totalPhiLienLac"));
                data.put("Tiền nhà", rs.getDouble("totalTienNha"));
                data.put("Tiền sữa cho con", rs.getDouble("totalTienSuaChoCon"));
                data.put("Tổng chi tiêu", rs.getDouble("totalChiTieu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    public static Map<String, Double> getThuByCategory(YearMonth month) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT " +
                "SUM(tienLuong) AS totalTienLuong, SUM(tienPhuCap) AS totalTienPhuCap, " +
                "SUM(tienThuong) AS totalTienThuong, SUM(thuNhapPhu) AS totalThuNhapPhu, " +
                "SUM(dauTu) AS totalDauTu, SUM(thuNhapTamThoi) AS totalThuNhapTamThoi, " +
                "SUM(tienLiXi) AS totalTienLiXi, SUM(tongTienThu) AS totalThuNhap " +
                "FROM TienThu " +
                "WHERE YEAR(ngayThu) = ? AND MONTH(ngayThu) = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set the year and month from the YearMonth object
            pstmt.setInt(1, month.getYear()); // Set the year
            pstmt.setInt(2, month.getMonthValue()); // Set the month

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                data.put("Tiền lương", rs.getDouble("totalTienLuong"));
                data.put("Tiền phụ cấp", rs.getDouble("totalTienPhuCap"));
                data.put("Tiền thưởng", rs.getDouble("totalTienThuong"));
                data.put("Thu nhập phụ", rs.getDouble("totalThuNhapPhu"));
                data.put("Đầu tư", rs.getDouble("totalDauTu"));
                data.put("Thu nhập tạm thời", rs.getDouble("totalThuNhapTamThoi"));
                data.put("Tiền lì xì", rs.getDouble("totalTienLiXi"));
                data.put("Tổng thu nhập", rs.getDouble("totalThuNhap"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }


    // Kiểm tra tính hợp lệ của tên cột (thêm các cột khác nếu cần)
}

