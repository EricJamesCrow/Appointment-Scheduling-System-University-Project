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
import javafx.scene.Cursor;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Overview implements Initializable {
    public TableView customersTableView;
    public TableView appointmentsTableView;
    public Button reportsBtn;
    public Button addCustomerBtn;
    public Button updateCustomerBtn;
    public Button deleteCustomerBtn;
    public Button addApptBtn;
    public Button updateApptBtn;
    public Button deleteApptBtn;
    public RadioButton viewAllRadioBtn;
    public RadioButton byWeekRadioBtn;
    public RadioButton byMonthRadioBtn;
    private ObservableList<ObservableList> customersData;
    private ObservableList<ObservableList> appointmentsData;
    private ObservableList<Button> overviewBtns;;
    /**
     * the stage the application is running in
     */
    Stage stage;
    /**
     * the ui to be displayed
     */
    Parent scene;

    //CONNECTION DATABASE
    public void buildCustomerData() {
        /**
         *
         *  Source: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
         *  */
        customersData = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Customer_ID as ID, Customer_Name as Name, " +
                    "Address, Postal_Code, Phone, Division_ID from CUSTOMERS";
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

                customersTableView.getColumns().addAll(col);
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
                    row.add(rs.getString(i));
                }
                customersData.add(row);

            }

            //FINALLY ADDED TO TableView
            customersTableView.setItems(customersData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Customer Data");
        }
    }

    public void onActionDisplayAddCustomer(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("../view/AddCustomer.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void onActionDisplayModifyCustomer(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/ModifyCustomer.fxml"));
            loader.load();
            ModifyCustomer MCController = loader.getController();
            MCController.sendCustomer((ObservableList) customersTableView.getSelectionModel().getSelectedItem());

            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch(NullPointerException | SQLException e) {
            System.out.println(e);
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setContentText("Must select a customer to update");
            alert2.showAndWait();
        }
    }

    public void onActionDeleteCustomer(ActionEvent actionEvent) throws SQLException{
        try {
            ObservableList customers = (ObservableList) customersTableView.getSelectionModel().getSelectedItem();
            String customerId = customers.get(0).toString();
            String checkForeignKey = "SELECT * FROM appointments where Customer_ID = ?";
            PreparedStatement psCheck = JDBC.connection.prepareStatement(checkForeignKey);
            psCheck.setString(1, customerId);
            ResultSet rs = psCheck.executeQuery();
            if(rs.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Cannot delete customer with scheduled appointment");
                alert.showAndWait();
            } else {
                String sql = "DELETE FROM customers WHERE customer_id = ?";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, customerId);
                ps.executeUpdate();
                buildCustomerData();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Customer "+customerId+" deleted!");
                alert.showAndWait();
            }
        } catch(NullPointerException e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Must select a customer to delete");
            alert.showAndWait();
        }
    }

    public void onActionAddAppointment(ActionEvent actionEvent) throws IOException {
        stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("../view/AddAppointment.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void onActionUpdateAppointment(ActionEvent actionEvent) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/UpdateAppointment.fxml"));
            loader.load();
            UpdateAppointment UAController = loader.getController();
            UAController.sendAppointment((ObservableList) appointmentsTableView.getSelectionModel().getSelectedItem());

            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch(NullPointerException | SQLException e) {
            System.out.println(e);
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setContentText("Must select an appointment to update");
            alert2.showAndWait();
        }
    }

    public void onActionDeleteAppointment(ActionEvent actionEvent) throws SQLException {
        try {
            ObservableList appointments = (ObservableList) appointmentsTableView.getSelectionModel().getSelectedItem();
            String appointmentId = appointments.get(0).toString();
            String appointmentType = appointments.get(4).toString();
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, appointmentId);
            ps.executeUpdate();
            String sql3;
            if (byWeekRadioBtn.isSelected()) {
                sql3 = "SELECT Appointment_ID, Title, Description, Location, " +
                        "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                        "User_ID, Contact_ID from APPOINTMENTS where week(Start)=week(now());";
            } else if(byMonthRadioBtn.isSelected()) {
                sql3 = "SELECT Appointment_ID, Title, Description, Location, " +
                        "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                        "User_ID, Contact_ID from APPOINTMENTS where month(Start)=month(now());";
            } else {
                sql3 = "SELECT Appointment_ID, Title, Description, Location, " +
                        "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                        "User_ID, Contact_ID from APPOINTMENTS";
            }
            buildAppointmentsData(sql3);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Appointment "+appointmentId+" deleted!\nType of appointment: "+appointmentType);
            alert.showAndWait();
        } catch(NullPointerException e) {
            System.out.println(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Must select a customer to delete");
            alert.showAndWait();
        }
    }
    public void onActionViewAppointmentsByMonth(ActionEvent actionEvent) {
        /**
         * Source: https://ubiq.co/database-blog/how-to-get-current-week-data-in-mysql/#:~:text=It%20is%20very%20easy%20to,of%20current%20week%20in%20MySQL.&text=In%20the%20above%20query%2C%20we,week%20number%20of%20today's%20day.
         * */
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS where month(Start)=month(now());";
        buildAppointmentsData(sql);
    }
    public void onActionViewAppointmentsByWeek(ActionEvent actionEvent) {
        /**
         * Source: https://ubiq.co/database-blog/how-to-get-current-week-data-in-mysql/#:~:text=It%20is%20very%20easy%20to,of%20current%20week%20in%20MySQL.&text=In%20the%20above%20query%2C%20we,week%20number%20of%20today's%20day.
         * */
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS where week(Start)=week(now());";
        buildAppointmentsData(sql);
    }

    public void onActionViewAllAppointmentsData(ActionEvent actionEvent) {
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS";
        buildAppointmentsData(sql);
    }

    public void buildAppointmentsData(String sql) {
        /**
         *
         *  Source: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
         *  */
        appointmentsTableView.getColumns().clear();
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

                appointmentsTableView.getColumns().addAll(col);
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
            appointmentsTableView.setItems(appointmentsData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Appointments Data");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buildCustomerData();
        String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                "User_ID, Contact_ID from APPOINTMENTS";
        buildAppointmentsData(sql);

        overviewBtns = FXCollections.observableArrayList(reportsBtn, addCustomerBtn, updateCustomerBtn, deleteCustomerBtn, addApptBtn, updateApptBtn, deleteApptBtn);

        for(Button button : overviewBtns) {
            button.setOnMouseEntered(e -> {
                button.setStyle("-fx-background-color: #5E5E5E; cursor: pointer;");
                button.setCursor(Cursor.HAND);
            });
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #474747; -fx-text-fill: #fff;"));
        }
    }
}
