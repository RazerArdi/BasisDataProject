package pro.basisdata_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CommLog extends Application {

    private int logId;
    private String specialId;
    private String task;
    private String location;

    public CommLog(int logId, String specialId, String task, String location) {
        this.logId = logId;
        this.specialId = specialId;
        this.task = task;
        this.location = location;
    }

    public int getLogId() {
        return logId;
    }

    public String getSpecialId() {
        return specialId;
    }

    public String getTask() {
        return task;
    }

    public String getLocation() {
        return location;
    }

    public static VBox getCommLogUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<CommLog> tableView = new TableView<>();
        ObservableList<CommLog> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<CommLog, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));

        TableColumn<CommLog, String> specialIdCol = new TableColumn<>("Special ID");
        specialIdCol.setCellValueFactory(new PropertyValueFactory<>("specialId"));

        TableColumn<CommLog, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));

        TableColumn<CommLog, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(logIdCol, specialIdCol, taskCol, locationCol);
        tableView.setItems(data);

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label domainLabel = new Label("Domain Type:");
        ChoiceBox<String> domainChoiceBox = new ChoiceBox<>();
        domainChoiceBox.getItems().addAll("Air", "Sea", "Land", "Space");
        domainChoiceBox.setValue("Air");

        Label logIdLabel = new Label("Log ID:");
        TextField logIdText = new TextField();
        Label specialIdLabel = new Label("Special ID:");
        TextField specialIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();

        VBox specificFields = new VBox();
        specificFields.setSpacing(10);

        domainChoiceBox.setOnAction(e -> {
            String domainType = domainChoiceBox.getValue();
            specificFields.getChildren().clear();

            switch (domainType) {
                case "Air":
                    Label airIdLabel = new Label("Air ID:");
                    TextField airIdText = new TextField();
                    specificFields.getChildren().addAll(airIdLabel, airIdText);
                    break;
                case "Sea":
                    Label seaPlatformIdLabel = new Label("Sea Platform ID:");
                    TextField seaPlatformIdText = new TextField();
                    specificFields.getChildren().addAll(seaPlatformIdLabel, seaPlatformIdText);
                    break;
                case "Land":
                    Label landIdLabel = new Label("Land ID:");
                    TextField landIdText = new TextField();
                    specificFields.getChildren().addAll(landIdLabel, landIdText);
                    break;
                case "Space":
                    Label spaceIdLabel = new Label("Space ID:");
                    TextField spaceIdText = new TextField();
                    specificFields.getChildren().addAll(spaceIdLabel, spaceIdText);
                    break;
                default:
                    break;
            }
        });

        Button createButton = new Button("Insert Data");
        createButton.setOnAction(e -> {
            int logId = Integer.parseInt(logIdText.getText());
            String specialId = specialIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();

            CommLog commLog = new CommLog(logId, specialId, task, location);
            data.add(commLog);
            saveToDatabase(commLog);
            clearForm(logIdText, specialIdText, taskText, locationText);
        });

        formLayout.getChildren().addAll(domainLabel, domainChoiceBox, logIdLabel, logIdText, specialIdLabel, specialIdText, taskLabel, taskText, locationLabel, locationText, specificFields, createButton);

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().add(contentLayout);

        return mainLayout;
    }

    private static void saveToDatabase(CommLog commLog) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("CommLog,%d,%s,%s,%s%n", commLog.getLogId(), commLog.getSpecialId(), commLog.getTask(), commLog.getLocation()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearForm(TextField logIdText, TextField specialIdText, TextField taskText, TextField locationText) {
        logIdText.clear();
        specialIdText.clear();
        taskText.clear();
        locationText.clear();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CommLog Management");
        primaryStage.setScene(new Scene(getCommLogUI(), 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
