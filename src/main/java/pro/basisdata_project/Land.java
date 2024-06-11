package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class Land {
    private String landId;
    private String task;
    private String location;
    private String communicationLogCommId;

    public Land(String landId, String task, String location, String communicationLogCommId) {
        this.landId = landId;
        this.task = task;
        this.location = location;
        this.communicationLogCommId = communicationLogCommId;
    }

    public String getLandId() {
        return landId;
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

    public static VBox getLandUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label landIdLabel = new Label("Land ID:");
        TextField landIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label commIdLabel = new Label("Communication Log ID:");
        TextField commIdText = new TextField();

        TableView<Land> tableView = new TableView<>();
        TableColumn<Land, String> landIdCol = new TableColumn<>("Land ID");
        landIdCol.setCellValueFactory(new PropertyValueFactory<>("landId"));
        TableColumn<Land, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Land, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Land, String> commIdCol = new TableColumn<>("Communication Log ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("communicationLogCommId"));

        tableView.getColumns().addAll(landIdCol, taskCol, locationCol, commIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String landId = landIdText.getText();
            String task = taskText.getText();
            String location = locationText.getText();
            String commId = commIdText.getText();

            Land land = new Land(landId, task, location, commId);
            tableView.getItems().add(land);

            landIdText.clear();
            taskText.clear();
            locationText.clear();
            commIdText.clear();
        });

        vbox.getChildren().addAll(
                landIdLabel, landIdText, taskLabel, taskText,
                locationLabel, locationText, commIdLabel, commIdText,
                tableView, createButton);

        return vbox;
    }
}

