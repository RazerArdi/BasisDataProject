package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class Sensors {

    private int sensorId;
    private String type;
    private String location;
    private String status;
    private String equipmentId;

    public Sensors(int sensorId, String type, String location, String status, String equipmentId) {
        this.sensorId = sensorId;
        this.type = type;
        this.location = location;
        this.status = status;
        this.equipmentId = equipmentId;
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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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
        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int sensorId = Integer.parseInt(sensorIdText.getText());
            String type = typeText.getText();
            String location = locationText.getText();
            String status = statusText.getText();
            String equipmentId = equipmentIdText.getText();

            Sensors sensor = new Sensors(sensorId, type, location, status, equipmentId);
            System.out.println("Sensor Created: " + sensor.getSensorId());
        });

        vbox.getChildren().addAll(sensorIdLabel, sensorIdText, typeLabel, typeText, locationLabel, locationText, statusLabel, statusText, equipmentIdLabel, equipmentIdText, createButton);

        return vbox;
    }
}
