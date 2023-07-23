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
    public TextField customerIdDisabledTextField;
    private int countryId;
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
        String sql = "UPDATE CUSTOMERS SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? " +
                "WHERE Customer_ID= ?";
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
            ps.setString(6, customerId);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        } catch (NullPointerException | IOException e) {
            errorMsg.setVisible(true);
        }
    }

    public void sendCustomer(ObservableList customer) throws SQLException {
        customerId = customer.get(0).toString();
        customerIdDisabledTextField.setText(customerId);
        customerName.setText(customer.get(1).toString());
        customerAddress.setText(customer.get(2).toString());
        customerPostalCode.setText(customer.get(3).toString());
        customerPhone.setText(customer.get(4).toString());
        String customerDivisionId = customer.get(5).toString();
        String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerDivisionId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            countryId = rs.getInt("Country_ID");
            countryComboBox.getSelectionModel().select(countryId - 1);
        }
        String sql2 = "SELECT DIVISION_ID, Division FROM first_level_divisions WHERE Country_ID = ?";
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);
        ps2.setInt(1, countryId);
        ResultSet rs2 = ps2.executeQuery();
        System.out.println(countryId);
        while(rs2.next()) {
            String divisionID = rs2.getString("Division_ID");
            String division = rs2.getString("Division");
            String concatenatedDivisionString = divisionID + " " + division;
            stateOptions.add(concatenatedDivisionString);
            if(customerDivisionId == divisionID) {

            }
        }
        stateComboBox.setItems(stateOptions);
        for(int i=0; i < stateOptions.size(); i++) {
            if (stateOptions.get(i).startsWith(customerDivisionId)) {
                stateComboBox.getSelectionModel().select(i);
                break;
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(countryOptions);
        errorMsg.setVisible(false);
    }
}
