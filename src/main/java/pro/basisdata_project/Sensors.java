package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Sensors {

    private int sensorId;
    private String type;
    private String location;
    private String status;
    private long lastMaintenance;

    public Sensors(int sensorId, String type, String location, String status, long lastMaintenance) {
        this.sensorId = sensorId;
        this.type = type;
        this.location = location;
        this.status = status;
        this.lastMaintenance = lastMaintenance;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(long lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public static VBox getSensorsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        DatePicker lastMaintenancePicker = new DatePicker();

        TableView<Sensors> tableView = new TableView<>();
        TableColumn<Sensors, Integer> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        TableColumn<Sensors, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Sensors, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Sensors, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Sensors, Long> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));

        tableView.getColumns().addAll(sensorIdCol, typeCol, locationCol, statusCol, lastMaintenanceCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int sensorId = Integer.parseInt(sensorIdText.getText());
            String type = typeText.getText();
            String location = locationText.getText();
            String status = statusText.getText();
            long lastMaintenance = lastMaintenancePicker.getValue().toEpochDay();

            Sensors sensor = new Sensors(sensorId, type, location, status, lastMaintenance);
            System.out.println("Sensor Created: " + sensor.getSensorId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%d,%s,%s,%s,%d%n", sensorId, type, location, status, lastMaintenance));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new sensor to the table view
            tableView.getItems().add(sensor);

            // Clear input fields after adding sensor
            sensorIdText.clear();
            typeText.clear();
            locationText.clear();
            statusText.clear();
            lastMaintenancePicker.getEditor().clear();
        });

        vbox.getChildren().addAll(
                sensorIdLabel, sensorIdText, typeLabel, typeText,
                locationLabel, locationText, statusLabel, statusText,
                lastMaintenanceLabel, lastMaintenancePicker,
                tableView, createButton);

        return vbox;
    }
}