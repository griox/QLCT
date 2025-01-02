package com.example.qlct;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import database.DataBaseHelper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class HelloController {
    @FXML
    private TextField ghiChu;
    @FXML
    private ToggleButton thu;
    @FXML
    private ToggleButton chi;
    @FXML
    private DatePicker datePicker;
    @FXML
    private static boolean isSelected = true;
    @FXML
    public TextField inputTien;
    @FXML
    private String selectedButton = null;
    @FXML
    private String ngay = isSelected ? "ngayChi" : "ngayThu";
    @FXML
    private String tenBang = isSelected ? "TienChi" : "TienThu";

    // Khi ToggleButton thay đổi, xử lý sự kiện
    @FXML
    public void handleToggleAction(ActionEvent event) {
        if (event.getSource() == thu) {
            isSelected = false;
            // Điều hướng đến trang thu.fxml
            changeScene("thu.fxml");
        } else if (event.getSource() == chi) {
            isSelected = true;
            // Điều hướng đến trang chi.fxml
            changeScene("chi.fxml");
        }
    }

    // Phương thức chung để thay đổi giao diện
    private void changeScene(String fxmlFile) {
        try {
            // Tải file fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene newScene = new Scene(fxmlLoader.load());

            // Lấy Stage hiện tại
            Stage currentStage = (Stage) chi.getScene().getWindow(); // or thu.getScene().getWindow()

            // Đặt giao diện mới
            currentStage.setScene(newScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không thể tải giao diện từ " + fxmlFile + ".");
        }
    }

    @FXML
    public void initialize() {
        // Đảm bảo rằng các ToggleButton đã được khởi tạo
        if (chi != null && thu != null) {
            // Đặt giá trị mặc định cho DatePicker là ngày hiện tại
            datePicker.setValue(LocalDate.now());

            // Thiết lập trạng thái mặc định cho ToggleButton
            chi.setSelected(isSelected);
            thu.setSelected(!isSelected);
        }
    }

    @FXML
    public String formatDateForSQL(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    // Lưu trạng thái của button
    @FXML
    public void handleButtonAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        selectedButton = mapButtonToColumn(clickedButton.getText());
    }

    // Tiến hành nhập dữ liệu
    @FXML
    public void handleSubmitAction(ActionEvent event) {
        if (selectedButton != null) {
            try {
                int value = Integer.parseInt(inputTien.getText());
                String ghi = ghiChu.getText();
                LocalDate date = datePicker.getValue();
                if (date != null) {
                    String formattedDate = formatDateForSQL(date);
                    DataBaseHelper.insertData(selectedButton, ngay, value, formattedDate, tenBang,ghi );
                    inputTien.setText("");
                    ghiChu.setText("");
                    System.out.println("Dữ liệu được thêm vào thành công");
                }
            } catch (NumberFormatException e) {
                System.err.println("Lỗi: Vui lòng nhập giá trị hợp lệ cho tiền.");
            }
        }
    }

    // Phương thức điều hướng đến trang Lich Su


    @FXML
    public String mapButtonToColumn(String buttonText) {
        switch (buttonText) {
            // Thuộc tính bảng TienChi
            case "Ăn uống":
                return "anUong";
            case "Chi trả khoản vay":
                return "chiTraKhoanVay";
            case "Quần áo":
                return "quanAO";
            case "Mỹ phẩm":
                return "myPham";
            case "Phí giao lưu":
                return "phiGiaoLuu";
            case "Y tế":
                return "yTe";
            case "Giáo dục":
                return "giaoDuc";
            case "Tiền điện":
                return "tienDienNuoc";
            case "Đi lại":
                return "diLai";
            case "Phí liên lạc":
                return "phiLienLac";
            case "Tiền nhà":
                return "tienNha";
            case "Tiền Sữa cho con":
                return "tienSuaChoCon";

            // Thuộc tính bảng TienThu
            case "Tiền lương":
                return "tienLuong";
            case "Tiền Phụ cấp":
                return "tienPhuCap";
            case "TIền Thưởng":
                return "tienThuong";
            case "Thu nhập phụ":
                return "thuNhapPhu";
            case "Đầu tư":
                return "dauTu";
            case "Thu nhập tạm thời":
                return "thuNhapTamThoi";
            case "Tiền lì xì":
                return "tienLiXi";
            // Nếu không khớp
            default:
                System.out.println("Không tìm thấy cột tương ứng cho nút: " + buttonText);
                return null;
        }
    }
}
