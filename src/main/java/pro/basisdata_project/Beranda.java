package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;

public class Beranda extends StackPane {

    public Beranda() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #EEF5FF;");

        // Top bar
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setSpacing(20);
        topBar.setStyle("-fx-background-color: #DF591A;");
        topBar.setPadding(new Insets(10));

        Button homeButton = new Button("HOME");
        homeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 15;");

        // Add vertical separator next to the home button
        Separator homeSeparator = new Separator();
        homeSeparator.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Label systemLabel = new Label("SYSTEM");
        systemLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold;");
        Label accountLabel = new Label("nama account");

        HBox.setMargin(homeButton, new Insets(0, 0, 0, 40));
        HBox.setMargin(homeSeparator, new Insets(0, 10, 0, 60));
        HBox.setMargin(accountLabel, new Insets(0, 10, 0, 0));

        topBar.getChildren().addAll(homeButton, homeSeparator, systemLabel, accountLabel);
        HBox.setHgrow(systemLabel, Priority.ALWAYS);
        systemLabel.setMaxWidth(Double.MAX_VALUE);
        systemLabel.setAlignment(Pos.CENTER);

        // Separator line below top bar
        Separator topSeparator = new Separator();

        // Sidebar
        VBox sidebar = new VBox();
        sidebar.setAlignment(Pos.TOP_LEFT);
        sidebar.setStyle("-fx-background-color: #EEF5FF; -fx-font-size: 15;");
        sidebar.setPrefWidth(200);
        VBox.setVgrow(sidebar, Priority.ALWAYS);

        // Create a ListView for the features
        ListView<String> featureList = new ListView<>();
        featureList.getItems().addAll(
                "Data", "Users", "Platforms", "Personnels", "Assignments",
                "Equipments", "Missions", "Maintenancelogs", "Analysis", "CommLog"
        );

        // Set custom cell factory to adjust spacing between items
        featureList.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.setPadding(new Insets(10)); // Adjust padding as needed
                    setGraphic(label);
                }
            }
        });

        VBox.setVgrow(featureList, Priority.ALWAYS); // Ensure featureList expands vertically
        sidebar.getChildren().add(featureList);

        // Main content area (VBox instead of ListView)
        VBox mainContent = new VBox();
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 20;");
        VBox.setVgrow(mainContent, Priority.ALWAYS); // Ensure mainContent expands vertically

        Label welcomeLabel = new Label("Welcome to the Main Page");
        welcomeLabel.setStyle("-fx-font-size: 24; -fx-text-fill: black;");
        mainContent.getChildren().add(welcomeLabel);

        // BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new VBox(topBar, topSeparator));
        borderPane.setLeft(sidebar);
        borderPane.setCenter(mainContent);

        // Set alignment constraints for the components
        BorderPane.setAlignment(topBar, Pos.TOP_CENTER);
        BorderPane.setAlignment(sidebar, Pos.TOP_LEFT);

        this.getChildren().add(borderPane); // Adding the BorderPane to the StackPane
    }
}
