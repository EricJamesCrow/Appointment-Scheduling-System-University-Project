package main;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * this is the main class for running the program.
 * it inherits from the javafx.application.Application class.
 * it contains the methods start and main.
 * Note: The Javadoc folder is located at the root of the directory for this project
 */
public class main extends Application {
    /**
     * this method starts the program
     * @param args
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
    }
    /**
     * this method is called by launch() in the main method.
     * it sets the scene to the LoginForm view.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("../view/LoginForm.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 320);
        stage.setTitle("Appointment Scheduling System");
        stage.setScene(scene);
        stage.show();
    }
}
