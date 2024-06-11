package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Space {

    private int spaceId;
    private String task;
    private String location;

    public Space(int spaceId, String task, String location) {
        this.spaceId = spaceId;
        this.task = task;
        this.location = location;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static VBox getSpaceUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label spaceIdLabel = new Label("Space ID:");
        TextField spaceIdText = new TextField();
        Label taskLabel = new Label("Task:");
        TextField taskText = new TextField();
        Label locationLabel = new Label("Location (Optional):");
        TextField locationText = new TextField();

        TableView<Space> tableView = new TableView<>();
        TableColumn<Space, Integer> spaceIdCol = new TableColumn<>("Space ID");
        spaceIdCol.setCellValueFactory(new PropertyValueFactory<>("spaceId"));
        TableColumn<Space, String> taskCol = new TableColumn<>("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<>("task"));
        TableColumn<Space, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        tableView.getColumns().addAll(spaceIdCol, taskCol, locationCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int spaceId = Integer.parseInt(spaceIdText.getText());
            String task = taskText.getText();
            String location = locationText.getText();

            Space space = new Space(spaceId, task, location);
            System.out.println("Space Created: " + space.getSpaceId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%d,%s,%s%n", spaceId, task, location));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new space to the table view
            tableView.getItems().add(space);

            // Clear input fields after adding space
            spaceIdText.clear();
            taskText.clear();
            locationText.clear();
        });

        vbox.getChildren().addAll(
                spaceIdLabel, spaceIdText, taskLabel, taskText,
                locationLabel, locationText, tableView, createButton);

        return vbox;
    }
}

