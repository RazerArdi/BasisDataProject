package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class Sea {
    private String seaPlatformId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Sea(String seaPlatformId, String task, String location, String communicationLogCommId) {
        this.seaPlatformId = seaPlatformId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public String getSeaPlatformId() {
        return seaPlatformId;
    }

    public String getTask() {
        return task;
    }

    public String getLocation() {
        return location;
    }

    public String getCommunicationLogCommId() {
        return communicationLogCommId;
    }

    public static VBox getSeaUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID:");
        TextField commIdText = new TextField();

        TableView<Sea> tableView = new TableView<>();
        TableColumn<Sea, String> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("seaPlatformId"));
        TableColumn<Sea, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Sea, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Sea, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(platformIdCol, taskCol, locationCol, commIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String platformId = platformIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdText.getText();

            Sea sea = new Sea(platformId, task, location, commId);
            tableView.getItems().add(sea);

            platformIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdText.clear();
        });

        vbox.getChildren().addAll(
                platformIdLabel, platformIdText, taskLabel, taskText,
                locationLabel, locationText, commIdLabel, commIdText,
                tableView, createButton);

        return vbox;
    }
}

