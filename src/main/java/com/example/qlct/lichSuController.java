package com.example.qlct;

import database.DataBaseHelper;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.util.List;

public class lichSuController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private ListView<String> listView;

    @FXML
    private Text txtThuNhap;

    @FXML
    private Text txtChiTieu;

    @FXML
    private Text txtTongKet;
    @FXML
    public void initialize() {
        updateValuesFromDatabase();

        // Thêm sự kiện để cập nhật dữ liệu khi người dùng chọn ngày
        datePicker.setOnAction(event -> updateValuesFromDatabase());
    }
    private void updateValuesFromDatabase() {
        // Lấy ngày từ DatePicker
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now(); // Nếu chưa chọn ngày, mặc định là ngày hiện tại
            datePicker.setValue(selectedDate);
        }

        String date = selectedDate.toString(); // Chuyển đổi LocalDate sang String (YYYY-MM-DD)

        // Lấy dữ liệu từ cơ sở dữ liệu với điều kiện ngày
        double tongThu = DataBaseHelper.getSumValue(date,"tongTienThu","TienThu","ngayThu");
        double tongChi = DataBaseHelper.getSumValue(date,"tongTienChi","TienChi","ngayChi");
        double tongKet = tongThu - tongChi;

        // Cập nhật giao diện
        txtThuNhap.setText(String.format("%,.0fđ", tongThu));
        txtChiTieu.setText(String.format("%,.0fđ", tongChi));
        txtTongKet.setText(String.format("%,.0fđ", tongKet));
        List<String> giaoDichList = DataBaseHelper.getTransactionList(date);

        // Cập nhật ListView
        listView.getItems().setAll(giaoDichList);
    }

}
