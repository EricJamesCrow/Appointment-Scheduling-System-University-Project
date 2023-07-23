package controller;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyCustomer implements Initializable {
    public String customerId;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPostalCode;
    public TextField customerPhone;
    public ComboBox countryComboBox;
    public ComboBox stateComboBox;
    public Label errorMsg;
    private ObservableList<String> countryOptions = FXCollections.observableArrayList("U.S.", "UK", "Canada");
    private ObservableList<String> stateOptions = FXCollections.observableArrayList();
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

    public void onActionAddFirstLevelDivisions(ActionEvent actionEvent) throws SQLException {
        stateOptions.clear();
        String countryId = Integer.toString(countryComboBox.getSelectionModel().getSelectedIndex() + 1);
        String sql = "SELECT DIVISION_ID, Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, countryId);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            String divisionID = rs.getString("Division_ID");
            String division = rs.getString("Division");
            String concatenatedDivisionString = divisionID + " " + division;
            stateOptions.add(concatenatedDivisionString);
        }
        stateComboBox.setItems(stateOptions);
    }

    public void onUpdateCustomer(ActionEvent actionEvent) throws SQLException {

    }

    public void sendCustomer(ObservableList customer) {
        customerId = customer.get(0).toString();
        customerName.setText(customer.get(1).toString());
        customerAddress.setText(customer.get(2).toString());
        customerPostalCode.setText(customer.get(3).toString());
        customerPhone.setText(customer.get(4).toString());
        Object customerDivisionId = customer.get(5);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMsg.setVisible(false);
    }
}
