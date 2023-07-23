package controller;

import helper.Authentication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;
import java.util.Locale;

public class LoginForm implements Initializable {
    public TextField username;
    public TextField password;
    public Button loginBtn;
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

    public void onUserLogin(ActionEvent actionEvent) throws SQLException, IOException {
        String usernameText = username.getText();
        String passwordText = password.getText();
        User user = Authentication.login(usernameText, passwordText);
        if(user == null) {
            errorMsg.setVisible(true);
        } else {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("../view/Overview.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

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
