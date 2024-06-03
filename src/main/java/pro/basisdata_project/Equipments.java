package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class Equipments {

    private String equipmentId;
    private String name;
    private String type;
    private String status;
    private String location;
    private String lastMaintenance;
    private int sensorId;

    public Equipments(String equipmentId, String name, String type, String status, String location, String lastMaintenance, int sensorId) {
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

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public static VBox getEquipmentUI() {
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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String equipmentId = equipmentIdText.getText();
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            String location = locationText.getText();
            String lastMaintenance = lastMaintenanceText.getText();
            int sensorId = Integer.parseInt(sensorIdText.getText());

            Equipments equipments = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
            System.out.println("Equipment Created: " + equipments.getEquipmentId());
        });

        vbox.getChildren().addAll(equipmentIdLabel, equipmentIdText, nameLabel, nameText, typeLabel, typeText, statusLabel, statusText, locationLabel, locationText, lastMaintenanceLabel, lastMaintenanceText, sensorIdLabel, sensorIdText, createButton);

        return vbox;
    }
}

