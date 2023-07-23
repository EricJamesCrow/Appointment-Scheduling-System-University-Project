package controller;

import helper.JDBC;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class Overview implements Initializable {
    public TableView customersTableView;
    public TableView appointmentsTableView;
    private ObservableList<ObservableList> customersData;
    private ObservableList<ObservableList> appointmentsData;
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
            System.out.println(customersTableView.getSelectionModel().getSelectedItem());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/ModifyCustomer.fxml"));
            loader.load();
            ModifyCustomer MCController = loader.getController();
            MCController.sendCustomer((ObservableList) customersTableView.getSelectionModel().getSelectedItem());

            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch(NullPointerException e) {
            Alert alert2 = new Alert(Alert.AlertType.ERROR);
            alert2.setContentText("Must select a customer to update");
            alert2.showAndWait();
        }
    }

    public void buildAppointmentsData() {
        /**
         *
         *  Source: https://blog.ngopal.com.np/2011/10/19/dyanmic-tableview-data-from-database/
         *  */
        appointmentsData = FXCollections.observableArrayList();
        try {
            String sql = "SELECT Appointment_ID, Title, Description, Location, " +
                    "Type, Start as Start_Date_and_Time, End as End_Date_and_Time, Customer_ID, " +
                    "User_ID from APPOINTMENTS";
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
        buildAppointmentsData();
    }
}
