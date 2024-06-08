package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Platforms {

    private int platformId;
    private String name;
    private String type;
    private String status;

    public Platforms(int platformId, String name, String type, String status) {
        this.platformId = platformId;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
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

    public static VBox getPlatformsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("active", "disabled", "passive");
        statusComboBox.setValue("active"); // Default value

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                int platformId = Integer.parseInt(platformIdText.getText());
                String name = nameText.getText();
                String type = typeText.getText();
                String status = statusComboBox.getValue();

                Platforms platform = new Platforms(platformId, name, type, status);
                System.out.println("Platform Created: " + platform.getPlatformId());

                // Save to Database.txt
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                    writer.write(String.format("Platforms,%d,%s,%s,%s%n", platformId, name, type, status));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid input: Platform ID must be a number.");
            }
        });

        vbox.getChildren().addAll(platformIdLabel, platformIdText, nameLabel, nameText, typeLabel, typeText, statusLabel, statusComboBox, createButton);

        return vbox;
    }
}
