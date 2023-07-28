package controller;

import helper.Authentication;
import helper.JDBC;
import helper.TimeZones;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Locale;
/**
 * This class is a controller for the LoginForm view.
 * It contains the methods onUserLogin, recentAppointmentsCheck and initialize
 */
public class LoginForm implements Initializable {
    /**
     * TextField elements for the gui
     */
    public TextField username;
    public TextField password;
    /**
     * Button element for the gui
     */
    public Button loginBtn;
    /**
     * Label elements for the gui
     */
    public Label errorMsg;
    public Label title;
    public Label usernameLabel;
    public Label passwordLabel;
    public Label zoneIdLabel;
    public Label zoneIdText;
    /**
     * the stage the application is running in
     */
    Stage stage;
    /**
     * the ui to be displayed
     */
    Parent scene;
    /**
     * this method is triggered when the user attempts to log in. It checks to see if the user is valid
     * and then writes whether the login attempt was successful to a text file named login_activity.txt.
     * Source: https://www.w3schools.com/java/java_files_create.asp
     * @params actionEvent
     * @throws IOException
     * @throws SQLException
     */
    public void onUserLogin(ActionEvent actionEvent) throws SQLException, IOException {
        boolean loginSuccess;
        String usernameText = username.getText();
        String passwordText = password.getText();
        User user = Authentication.login(usernameText, passwordText);
        if(user == null) {
            errorMsg.setVisible(true);
            loginSuccess = false;
        } else {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
            loginSuccess = true;
            try {
                recentAppointmentsCheck();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedPassword = md.digest(passwordText.getBytes(StandardCharsets.UTF_8));
            FileWriter myWriter = new FileWriter("login_activity.txt", true);
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = now.format(formatter);
            if(loginSuccess) {
                myWriter.write(formatDateTime+" - LOGIN SUCCESS - Username: "+usernameText+" Password: "+hashedPassword+"\n");
            } else {
                myWriter.write(formatDateTime+" - LOGIN FAILURE - Username: "+usernameText+" Password: "+hashedPassword+"\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to login_activity.txt.");
        } catch (IOException e) {
            System.out.println("An error occurred when attempting to write to the file.");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * this method checks for recent appointments and shows an alert displaying whether
     * there are any scheduled appointments within 15 minutes of the user's login.
     * @throws SQLException
     */
    public void recentAppointmentsCheck() throws SQLException {
        /** https://www.reddit.com/r/learnprogramming/comments/80921j/mysql_select_appointments_starting_in_the_next_15/ */
        String sql = "SELECT Appointment_ID, Start FROM appointments WHERE Start BETWEEN ? AND DATE_ADD(?, INTERVAL 15 MINUTE)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentDate = TimeZones.localToUTC(TimeZones.convertToLocal(LocalDateTime.now().format(formatter)));
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, currentDate);
        ps.setString(2, currentDate);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            String appointmentId = rs.getString("Appointment_ID");
            String[] appointmentStart = TimeZones.utcToLocal(rs.getString("Start")).split(" ");
            String appointmentDate = appointmentStart[0];
            String appointmentTime = appointmentStart[1];
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Appointment: "+appointmentId+" scheduled on "+appointmentDate+" at "+appointmentTime+".");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("There are no upcoming appointments.");
            alert.showAndWait();
        }
    }
    /**
     * initializes the controller
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMsg.setVisible(false);
        Locale defaultLocale = Locale.getDefault();
        String language = defaultLocale.getLanguage();
        if (language.equals(new Locale("fr").getLanguage())) {
            errorMsg.setText("L'identifiant ou le mot de passe est incorrect");
            title.setText("Système de prise de rendez-vous");
            usernameLabel.setText("Nom d'utilisateur");
            passwordLabel.setText("Mot de passe");
            zoneIdLabel.setText("ID de zone:");
            loginBtn.setText("Connectez-vous");
        }
        ZoneId zoneId = ZoneId.systemDefault();
        zoneIdText.setText(zoneId.toString());
        loginBtn.setOnMouseEntered(e -> {
                loginBtn.setStyle("-fx-background-color: #5E5E5E; cursor: pointer;");
                loginBtn.setCursor(Cursor.HAND);
                });
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #474747; -fx-text-fill: #fff;"));
    }
}
