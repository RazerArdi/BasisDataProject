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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Equipments {

    private String equipmentId;
    private String name;
    private String type;
    private String status;
    private String location;
    private String lastMaintenance;
    private String sensorId;

    public Equipments(String equipmentId, String name, String type, String status, String location, String lastMaintenance, String sensorId) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.location = location;
        this.lastMaintenance = lastMaintenance;
        this.sensorId = sensorId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(String lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public static VBox getEquipmentsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        TextField lastMaintenanceText = new TextField();
        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();

        TableView<Equipments> tableView = new TableView<>();
        TableColumn<Equipments, String> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<Equipments, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Equipments, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Equipments, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Equipments, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Equipments, String> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));
        TableColumn<Equipments, String> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));

        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol, locationCol, lastMaintenanceCol, sensorIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String equipmentId = equipmentIdText.getText();
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            String location = locationText.getText();
            String lastMaintenance = lastMaintenanceText.getText();
            String sensorId = sensorIdText.getText();

            Equipments equipment = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
            System.out.println("Equipment Created: " + equipment.getEquipmentId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s%n", equipmentId, name, type, status, location, lastMaintenance, sensorId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new equipment to the table view
            tableView.getItems().add(equipment);

            // Clear input fields after adding equipment
            equipmentIdText.clear();
            nameText.clear();
            typeText.clear();
            statusText.clear();
            locationText.clear();
            lastMaintenanceText.clear();
            sensorIdText.clear();
        });

        vbox.getChildren().addAll(
                equipmentIdLabel, equipmentIdText, nameLabel, nameText,
                typeLabel, typeText, statusLabel, statusText, locationLabel,
                locationText, lastMaintenanceLabel, lastMaintenanceText,
                sensorIdLabel, sensorIdText, tableView, createButton);

        return vbox;
    }
}
