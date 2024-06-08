package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Sensors {

    private int sensorId;
    private String name;
    private String type;
    private String status;
    private int platformId;

    public Sensors(int sensorId, String name, String type, String status, int platformId) {
        this.sensorId = sensorId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.platformId = platformId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
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

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public static VBox getSensorsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                int sensorId = Integer.parseInt(sensorIdText.getText());
                String name = nameText.getText();
                String type = typeText.getText();
                String status = statusText.getText();
                int platformId = Integer.parseInt(platformIdText.getText());

                Sensors sensor = new Sensors(sensorId, name, type, status, platformId);
                System.out.println("Sensor Created: " + sensor.getSensorId());

                // Save to Database.txt
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                    writer.write(String.format("Sensors,%d,%s,%s,%s,%d%n", sensorId, name, type, status, platformId));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid input: Sensor ID and Platform ID must be numbers.");
            }
        });

        vbox.getChildren().addAll(sensorIdLabel, sensorIdText, nameLabel, nameText, typeLabel, typeText, statusLabel, statusText, platformIdLabel, platformIdText, createButton);

        return vbox;
    }
}
