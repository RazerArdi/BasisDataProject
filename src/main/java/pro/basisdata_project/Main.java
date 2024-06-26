package pro.basisdata_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        try (InputStream iconStream = Main.class.getResourceAsStream("Database.jpg")) {
            if (iconStream == null) {
                System.err.println("Unable to find target file");
            } else {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        stage.setTitle("SYSTEM");

        boolean isConnected = OracleAPEXConnection.isConnectionSuccessful();
        Alert alert = new Alert(isConnected ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle("Database Connection Status");
        alert.setHeaderText(null);
        alert.setContentText(isConnected ? "Connection to the database was successful." : "Failed to connect to the database.");
        alert.showAndWait();

        Login login = new Login(this);
        login.initializeScenes(stage);

        stage.show();
    }

    public void showMainScene(String accountName, Scene loginScene) {
        MainPage mainPage = new MainPage(accountName, primaryStage, loginScene);
        Scene mainScene = new Scene(mainPage, 900, 700);
        primaryStage.setScene(mainScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
