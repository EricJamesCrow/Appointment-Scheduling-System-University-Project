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

public class AddCustomer implements Initializable {
    public ComboBox stateComboBox;
    public TextField customerName;
    public TextField customerAddress;
    public TextField customerPostalCode;
    public TextField customerPhone;
    public Label errorMsg;
    private ObservableList<String> countryOptions = FXCollections.observableArrayList("U.S.", "UK", "Canada");
    private ObservableList<String> stateOptions = FXCollections.observableArrayList();
    public ComboBox countryComboBox;
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

    public void onAddCustomer(ActionEvent actionEvent) throws SQLException, IOException {
        String sql = "INSERT INTO CUSTOMERS (Customer_Name, Address, Postal_Code, Phone, Division_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            String name = customerName.getText();
            String address = customerAddress.getText();
            String postal = customerPostalCode.getText();
            String phone = customerPhone.getText();
            String selectedState = stateComboBox.getValue().toString();
            if(name.isEmpty() || address.isEmpty() || postal.isEmpty() || phone.isEmpty()) {
                errorMsg.setVisible(true);
                return;
            }
            int divisionId = Integer.parseInt(selectedState.split(" ", 2)[0]);
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, postal);
            ps.setString(4, phone);
            ps.setInt(5, divisionId);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        } catch (NullPointerException e) {
            errorMsg.setVisible(true);
        }
        
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(countryOptions);
        errorMsg.setVisible(false);
    }
}
