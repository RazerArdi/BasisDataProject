package pro.basisdata_project;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        InputStream iconStream = Main.class.getResourceAsStream("Database.jpg");
        if (iconStream == null) {
            System.err.println("Unable to find target file");
        } else {
            stage.getIcons().add(new Image(iconStream));
        }

        stage.setTitle("SYSTEM");

        Login login = new Login(this);
        login.initializeScenes(stage);

        stage.show();
    }

    public void showMainScene() {
        Beranda mainPage = new Beranda();
        Scene mainScene = new Scene(mainPage, 600, 700);
        primaryStage.setScene(mainScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
