package main;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class main extends Application {

    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("../view/LoginForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 320);
        stage.setTitle("Appointment Scheduling System");
        stage.setScene(scene);
        stage.show();
    }
}
