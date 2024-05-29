package pro.basisdata_project;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

public class Beranda extends VBox {

    public Beranda() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setStyle("-fx-background-color: #EEF5FF; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome to the Main Page");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: black;");

        this.getChildren().add(welcomeLabel);
    }
}
