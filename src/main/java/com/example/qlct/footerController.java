package com.example.qlct;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class footerController {
    @FXML
    private DatePicker datePicker;
    @FXML
    private void goToLichSu(Stage currentStage) throws IOException {
        AnchorPane newPage = FXMLLoader.load(getClass().getResource("lichsu.fxml"));
        Scene scene = new Scene(newPage);
        currentStage.setScene(scene); // Đặt scene mới
        currentStage.show();
    }

    // Phương thức điều hướng đến trang Bao Cao
    @FXML
    private void goToBaoCao(Stage currentStage) throws IOException {
        AnchorPane newPage = FXMLLoader.load(getClass().getResource("baocao.fxml"));
        Scene scene = new Scene(newPage);
        currentStage.setScene(scene); // Đặt scene mới
        currentStage.show();
    }
    @FXML
    private void goThuChi(Stage currentStage) throws IOException {
        AnchorPane newPage = FXMLLoader.load(getClass().getResource("chi.fxml"));
        Scene scene = new Scene(newPage);
        currentStage.setScene(scene); // Đặt scene mới
        currentStage.show();
    }

    @FXML
    private void onNhapButtonClick(ActionEvent event) {
        // Lấy Stage hiện tại từ sự kiện
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            // Chuyển đến trang lịch sử
            goThuChi(currentStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onLichSuButtonClick(ActionEvent event) {
        // Lấy Stage hiện tại từ sự kiện
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            // Chuyển đến trang lịch sử
            goToLichSu(currentStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBaoCaoButtonClick(ActionEvent event) {
        // Lấy Stage hiện tại từ sự kiện
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        try {
            // Chuyển đến trang báo cáo
            goToBaoCao(currentStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
