package com.example.qlct;

import database.DataBaseHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class baocaoController {
    @FXML
    private ToggleButton thu;
    @FXML
    private ToggleButton chi;
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
    private PieChart pieChart;
    @FXML
    private static boolean isSelected = true;
    @FXML
    public void initialize() {
        updateValuesFromDatabase();
        datePicker.setOnAction(event -> updateValuesFromDatabase());
    }
    @FXML
    public void handleToggleAction(ActionEvent event) {
        if (event.getSource() == thu) {
            isSelected = false;
        } else if (event.getSource() == chi) {
            isSelected = true;
        }
        updateValuesFromDatabase();
    }
    private void updateValuesFromDatabase() {
        // Lấy tháng từ DatePicker
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            selectedDate = LocalDate.now(); // Nếu chưa chọn ngày, mặc định là ngày hiện tại
            datePicker.setValue(selectedDate);
        }
        YearMonth selectedMonth = YearMonth.from(selectedDate);

        // Tính tổng thu nhập và chi tiêu từ LichSuGiaoDich
        double tongThu = DataBaseHelper.getSumValueMonth(selectedMonth, "Thu");
        double tongChi = DataBaseHelper.getSumValueMonth(selectedMonth, "Chi");
        double tongKet = tongThu - tongChi;

        // Cập nhật giao diện
        txtThuNhap.setText(String.format("%,.0fđ", tongThu));
        txtChiTieu.setText(String.format("%,.0fđ", tongChi));
        txtTongKet.setText(String.format("%,.0fđ", tongKet));

        // Lấy danh sách giao dịch và cập nhật ListView
        Map<String, Double> chiTieuData = isSelected? DataBaseHelper.getChiTieuByCategory(selectedMonth):DataBaseHelper.getThuByCategory(selectedMonth);

        if (chiTieuData.isEmpty()) {
            System.out.println("No data to display on PieChart.");
        } else {
            updatePieChart(chiTieuData,isSelected? tongChi:tongThu);
        }

        List<String> transactionList = isSelected? DataBaseHelper.getTransactionListMonth(selectedMonth):DataBaseHelper.getTransactionListThu(selectedMonth);
        if (transactionList.isEmpty()) {
            System.out.println("No transactions to display in ListView.");
        }
        listView.getItems().clear();
        listView.getItems().addAll(transactionList);
    }

    private void updatePieChart(Map<String, Double> data, double tongTien) {
        pieChart.getData().clear();

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String category = entry.getKey();
            double value = entry.getValue();
            double percentage = (value / tongTien) * 100;


            PieChart.Data slice = new PieChart.Data(category, value);
            pieChart.getData().add(slice);
            pieChart.setVisible(true);
            pieChart.setManaged(true);
            pieChart.setClockwise(true);
            pieChart.setLabelsVisible(true);
            pieChart.setStartAngle(50);
            Platform.runLater(() -> {
                javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                        String.format("%s: %,.0fđ (%.2f%%)", category, value, percentage)
                );
                javafx.scene.control.Tooltip.install(slice.getNode(), tooltip);
            });
        }
    }


}
