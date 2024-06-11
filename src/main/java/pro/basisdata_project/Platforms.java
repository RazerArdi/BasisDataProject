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

public class Platforms {

    private String platformId;
    private String platformName;
    private String type;
    private String capability;
    private String lastMaintenance;
    private int missionsMissionId;

    public Platforms(String platformId, String platformName, String type, String capability, String lastMaintenance, int missionsMissionId) {
        this.platformId = platformId;
        this.platformName = platformName;
        this.type = type;
        this.capability = capability;
        this.lastMaintenance = lastMaintenance;
        this.missionsMissionId = missionsMissionId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCapability() {
        return capability;
    }

    public void setCapability(String capability) {
        this.capability = capability;
    }

    public String getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(String lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public int getMissionsMissionId() {
        return missionsMissionId;
    }

    public void setMissionsMissionId(int missionsMissionId) {
        this.missionsMissionId = missionsMissionId;
    }

    public static VBox getPlatformsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label platformNameLabel = new Label("Platform Name:");
        TextField platformNameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label capabilityLabel = new Label("Capability:");
        TextField capabilityText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        TextField lastMaintenanceText = new TextField();
        Label missionsMissionIdLabel = new Label("Mission ID:");
        TextField missionsMissionIdText = new TextField();

        TableView<Platforms> tableView = new TableView<>();
        TableColumn<Platforms, String> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));
        TableColumn<Platforms, String> platformNameCol = new TableColumn<>("Platform Name");
        platformNameCol.setCellValueFactory(new PropertyValueFactory<>("platformName"));
        TableColumn<Platforms, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Platforms, String> capabilityCol = new TableColumn<>("Capability");
        capabilityCol.setCellValueFactory(new PropertyValueFactory<>("capability"));
        TableColumn<Platforms, String> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));
        TableColumn<Platforms, Integer> missionsMissionIdCol = new TableColumn<>("Mission ID");
        missionsMissionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionsMissionId"));

        tableView.getColumns().addAll(platformIdCol, platformNameCol, typeCol, capabilityCol, lastMaintenanceCol, missionsMissionIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String platformId = platformIdText.getText();
            String platformName = platformNameText.getText();
            String type = typeText.getText();
            String capability = capabilityText.getText();
            String lastMaintenance = lastMaintenanceText.getText();
            int missionsMissionId = Integer.parseInt(missionsMissionIdText.getText());

            Platforms platform = new Platforms(platformId, platformName, type, capability, lastMaintenance, missionsMissionId);
            System.out.println("Platform Created: " + platform.getPlatformId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s,%d%n", platformId, platformName, type, capability, lastMaintenance, missionsMissionId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new platform to the table view
            tableView.getItems().add(platform);

            // Clear input fields after adding platform
            platformIdText.clear();
            platformNameText.clear();
            typeText.clear();
            capabilityText.clear();
            lastMaintenanceText.clear();
            missionsMissionIdText.clear();
        });

        vbox.getChildren().addAll(
                platformIdLabel, platformIdText, platformNameLabel, platformNameText,
                typeLabel, typeText, capabilityLabel, capabilityText,
                lastMaintenanceLabel, lastMaintenanceText,
                missionsMissionIdLabel, missionsMissionIdText,
                tableView, createButton);

        return vbox;
    }
}
