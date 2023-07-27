package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Reports implements Initializable {
    public ComboBox appointmentsByTypeComboBox;
    public Label numberOfAppointmentsByTypeLabel;
    public TableView appointmentsByTypeTableView;
    public ComboBox appointmentsByMonthComboBox;
    public Label numberOfAppointmentsByMonthLabel;
    public TableView appointmentsByMonthTableView;
    public ComboBox appointmentsByContactComboBox;
    public Label numberOfAppointmentsByContactLabel;
    public TableView appointmentsByContactTableView;
    public ComboBox appointmentsByCustomerComboBox;
    public Label numberOfAppointmentsByCustomerLabel;
    public TableView appointmentsByCustomerTableView;
    /**
     * the stage the application is running in
     */
    Stage stage;
    /**
     * the ui to be displayed
     */
    Parent scene;
    public void onActionDisplayMenu(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
}
