package controller;

import helper.Authentication;
import helper.JDBC;
import helper.TimeZones;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

public class UpdateAppointment implements Initializable {
    public ComboBox countryComboBox;
    public ComboBox stateComboBox;
    public Label errorMsg;
    public ComboBox contactComboBox;
    public TextField userId;
    public TextField title;
    public TextField description;
    public TextField type;
    public DatePicker startDate;
    public DatePicker endDate;
    public ComboBox customerIdComboBox;
    public ComboBox startHour;
    public ComboBox endHour;
    public ComboBox startMinute;
    public ComboBox endMinute;
    public ComboBox startSecond;
    public ComboBox endSecond;
    public TextField appointmentIdTextBox;
    private ObservableList<String> countryOptions = FXCollections.observableArrayList("U.S.", "UK", "Canada");
    private ObservableList<String> customerOptions = FXCollections.observableArrayList();
    private ObservableList<String> stateOptions = FXCollections.observableArrayList();
    private ObservableList<String> contactOptions = FXCollections.observableArrayList();
    /**
     * the stage the application is running in
     */
    Stage stage;
    /**
     * the ui to be displayed
     */
    Parent scene;
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
    public void addContacts() throws SQLException {
        try {
            String sql = "SELECT Contact_ID, Contact_Name FROM contacts";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String contactId = rs.getString("Contact_ID");
                String contactName = rs.getString("Contact_Name");
                String concatenatedContactString = contactId + " " + contactName;
                contactOptions.add(concatenatedContactString);
            }
            contactComboBox.setItems(contactOptions);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on adding contacts data");
        }
    }
    public void addUsers() {
        try {
            String sql = "SELECT Customer_ID, Customer_Name FROM customers";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String userId = rs.getString("Customer_ID");
                String userName = rs.getString("Customer_Name");
                String concatenatedUserString = userId + " " + userName;
                customerOptions.add(concatenatedUserString);
            }
            customerIdComboBox.setItems(customerOptions);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void onAddAppointment(ActionEvent actionEvent) throws SQLException, IOException {
        String sql = "INSERT INTO APPOINTMENTS (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, " +
                "Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        try {
            String titleText = title.getText();
            String descriptionText = description.getText();
            String location = (stateComboBox.getValue().toString()).split(" ", 2)[1];
            String typeText = type.getText();
            String startDateText = startDate.getValue().toString();
            String endDateText = endDate.getValue().toString();
            String startTimeText = startHour.getValue()+":"+startMinute.getValue()+":"+startSecond.getValue();
            String endTimeText = endHour.getValue()+":"+endMinute.getValue()+":"+endSecond.getValue();
            int customerIdText = Integer.parseInt(customerIdComboBox.getValue().toString().split(" ", 2)[0]);
            int userIdText = Integer.parseInt(userId.getText());
            int contactIdText = Integer.parseInt(contactComboBox.getValue().toString().split(" ", 2)[0]);
            if(titleText.isEmpty() || descriptionText.isEmpty() || location.isEmpty() || typeText.isEmpty() ||
                    startDateText.isEmpty() || endDateText.isEmpty() || startTimeText.isEmpty() || endTimeText.isEmpty()) {
                errorMsg.setVisible(true);
                return;
            }
            String start = startDateText + " " + startTimeText;
            String end = endDateText + " " + endTimeText;
            boolean checkOverlappingAppointments = TimeZones.checkOverlappingAppointments(start, end);
            if (!checkOverlappingAppointments) {
                errorMsg.setVisible(true);
                errorMsg.setText("Cannot schedule an appointment that overlaps with another.");
                return;
            }
            boolean checkBusinessHours = TimeZones.checkBusinessHours(start, end);
            if (!checkBusinessHours) {
                errorMsg.setVisible(true);
                errorMsg.setText("Cannot schedule an appointment outside of business hours.");
                return;
            }
            String startDateTime = TimeZones.localToUTC(TimeZones.convertToLocal(start));
            String endDateTime = TimeZones.localToUTC(TimeZones.convertToLocal(end));
            ps.setString(1, titleText);
            ps.setString(2, descriptionText);
            ps.setString(3, location);
            ps.setString(4, typeText);
            ps.setString(5, startDateTime);
            ps.setString(6, endDateTime);
            ps.setInt(7, customerIdText);
            ps.setInt(8, userIdText);
            ps.setInt(9, contactIdText);
            int rowsAffected = ps.executeUpdate();
            if(rowsAffected > 0) {
                stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        } catch (NullPointerException | ParseException e) {
            errorMsg.setVisible(true);
        }
    }
    public void onActionDisplayMenu(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void sendAppointment(ObservableList appointment) throws SQLException {
        System.out.println(appointment);
        appointmentIdTextBox.setText(appointment.get(0).toString());
        customerIdComboBox.setValue(appointment.get(7).toString());
        userId.setText(appointment.get(8).toString());
        title.setText(appointment.get(1).toString());
        description.setText(appointment.get(2).toString());
        type.setText(appointment.get(4).toString());
        String customerDivisionId = appointment.get(5).toString();
        String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerDivisionId);
        ResultSet rs = ps.executeQuery();
//        if(rs.next()) {
//            countryId = rs.getInt("Country_ID");
//            countryComboBox.getSelectionModel().select(countryId - 1);
//        }
//        String sql2 = "SELECT DIVISION_ID, Division FROM first_level_divisions WHERE Country_ID = ?";
//        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);
//        ps2.setInt(1, countryId);
//        ResultSet rs2 = ps2.executeQuery();
//        System.out.println(countryId);
//        while(rs2.next()) {
//            String divisionID = rs2.getString("Division_ID");
//            String division = rs2.getString("Division");
//            String concatenatedDivisionString = divisionID + " " + division;
//            stateOptions.add(concatenatedDivisionString);
//            if(customerDivisionId == divisionID) {
//
//            }
//        }
//        stateComboBox.setItems(stateOptions);
//        for(int i=0; i < stateOptions.size(); i++) {
//            if (stateOptions.get(i).startsWith(customerDivisionId)) {
//                stateComboBox.getSelectionModel().select(i);
//                break;
//            }
//        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(countryOptions);
        userId.setText(String.valueOf(Authentication.getCurrentUser().getUserId()));
        try {
            addContacts();
            addUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for(int i=0; i <= 59; i++) {
            String formattedNumber = String.format("%02d", i);
            startSecond.getItems().add(formattedNumber);
            startMinute.getItems().add(formattedNumber);
            endSecond.getItems().add(formattedNumber);
            endMinute.getItems().add(formattedNumber);
        }
        for(int i=1; i <= 24; i++) {
            String formattedNumber = String.format("%02d", i);
            startHour.getItems().add(formattedNumber);
            endHour.getItems().add(formattedNumber);
        }
        errorMsg.setVisible(false);
    }
}
