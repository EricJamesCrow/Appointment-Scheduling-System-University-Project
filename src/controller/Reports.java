package controller;

import helper.JDBC;
import helper.TimeZones;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
    private ObservableList<ObservableList> appointmentsData;
    private ObservableList<String> contactOptions = FXCollections.observableArrayList();
    private ObservableList<String> monthOptions = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL",
            "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
    private ObservableList<String> typeOptions = FXCollections.observableArrayList();
    private ObservableList<String> customerOptions = FXCollections.observableArrayList();
    Calendar cal=Calendar.getInstance();
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

    public void buildAppointmentsData(String sql, TableView table, Label label) {
        /**
         *
         *  Source: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
         *  */
        table.getColumns().clear();
        appointmentsData = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             *********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));

                table.getColumns().addAll(col);
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    if(i == 6 || i == 7) {
                        row.add(TimeZones.utcToLocal(rs.getString(i)));
                        continue;
                    }
                    row.add(rs.getString(i));
                }
                appointmentsData.add(row);

            }

            //FINALLY ADDED TO TableView
            table.setItems(appointmentsData);
            label.setText(String.valueOf(appointmentsData.size()));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Appointments Data");
        }
    }
    public void onActionFilterTypes(ActionEvent actionEvent) throws SQLException {
        String selectedType = appointmentsByTypeComboBox.getValue().toString();
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Type = '"+selectedType+"'";
        buildAppointmentsData(sql, appointmentsByTypeTableView, numberOfAppointmentsByTypeLabel);
    }
    public void onActionFilterMonths(ActionEvent actionEvent) throws SQLException {
        int selectedMonth = appointmentsByMonthComboBox.getSelectionModel().getSelectedIndex() + 1;
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS where month(Start)="+selectedMonth+"";
        buildAppointmentsData(sql, appointmentsByMonthTableView, numberOfAppointmentsByMonthLabel);
    }
    public void onActionFilterContacts(ActionEvent actionEvent) throws SQLException {
        String selectedContact = appointmentsByContactComboBox.getValue().toString().split(" ", 2)[0];
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Contact_ID = "+selectedContact;
        buildAppointmentsData(sql, appointmentsByContactTableView, numberOfAppointmentsByContactLabel);
    }
    public void onActionFilterCustomers(ActionEvent actionEvent) throws SQLException {
        String customerId = (appointmentsByCustomerComboBox.getValue().toString()).split(" ", 2)[0];
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Customer_ID = "+customerId;
        buildAppointmentsData(sql, appointmentsByCustomerTableView, numberOfAppointmentsByCustomerLabel);
    }
    public void addTypes() throws SQLException {
        try {
            String sql = "SELECT DISTINCT Type FROM Appointments";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String type = rs.getString("Type");
                typeOptions.add(type);
            }
            appointmentsByTypeComboBox.setItems(typeOptions);
            appointmentsByTypeComboBox.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on adding types data");
        }
    }
    public void addMonths() {
        appointmentsByMonthComboBox.setItems(monthOptions);
        appointmentsByMonthComboBox.getSelectionModel().select(cal.get(Calendar.MONTH));
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
            appointmentsByContactComboBox.setItems(contactOptions);
            appointmentsByContactComboBox.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on adding contacts data");
        }
    }
    public void addCustomers() throws SQLException {
        try {
            String sql = "SELECT Customer_ID, Customer_Name FROM customers";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String customerId = rs.getString("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String concatenatedCustomerString = customerId + " " + customerName;
                customerOptions.add(concatenatedCustomerString);
            }
            appointmentsByCustomerComboBox.setItems(customerOptions);
            appointmentsByCustomerComboBox.getSelectionModel().select(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on adding contacts data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String sql1 = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Type = 'Planning Session'";
        String sql2 = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS where month(Start)=month(now());";
        String sql3 = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Contact_ID = 3";
        String sql4 = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS WHERE Customer_ID = 1";
        buildAppointmentsData(sql1, appointmentsByTypeTableView, numberOfAppointmentsByTypeLabel);
        buildAppointmentsData(sql2, appointmentsByMonthTableView, numberOfAppointmentsByMonthLabel);
        buildAppointmentsData(sql3, appointmentsByContactTableView, numberOfAppointmentsByContactLabel);
        buildAppointmentsData(sql4, appointmentsByCustomerTableView, numberOfAppointmentsByCustomerLabel);
        try {
            addContacts();
            addTypes();
            addCustomers();
            addMonths();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
