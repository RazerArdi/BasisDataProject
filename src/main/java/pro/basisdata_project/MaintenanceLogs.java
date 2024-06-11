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
import java.util.stream.Collectors;

public class MaintenanceLogs {

    private String maintenanceId;
    private String date;
    private String description;
    private String equipmentId;
    private String platformId;
    private String personnelId;

    public MaintenanceLogs(String maintenanceId, String date, String description, String equipmentId, String platformId, String personnelId) {
        this.maintenanceId = maintenanceId;
        this.date = date;
        this.description = description;
        this.equipmentId = equipmentId;
        this.platformId = platformId;
        this.personnelId = personnelId;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public static VBox getMaintenanceLogsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label maintenanceIdLabel = new Label("Maintenance ID:");
        TextField maintenanceIdText = new TextField();
        Label dateLabel = new Label("Date:");
        TextField dateText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionText = new TextField();
        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();

        TableView<MaintenanceLogs> tableView = new TableView<>();
        TableColumn<MaintenanceLogs, String> maintenanceIdCol = new TableColumn<>("Maintenance ID");
        maintenanceIdCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        TableColumn<MaintenanceLogs, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<MaintenanceLogs, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<MaintenanceLogs, String> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<MaintenanceLogs, String> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));
        TableColumn<MaintenanceLogs, String> personnelIdCol = new TableColumn<>("Personnel ID");
        personnelIdCol.setCellValueFactory(new PropertyValueFactory<>("personnelId"));

        tableView.getColumns().addAll(maintenanceIdCol, dateCol, descriptionCol, equipmentIdCol, platformIdCol, personnelIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String maintenanceId = maintenanceIdText.getText();
            String date = dateText.getText();
            String description = descriptionText.getText();
            String equipmentId = equipmentIdText.getText();
            String platformId = platformIdText.getText();
            String personnelId = personnelIdText.getText();

            MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
            System.out.println("Maintenance Log Created: " + maintenanceLog.getMaintenanceId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s%n", maintenanceId, date, description, equipmentId, platformId, personnelId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new maintenance log to the table view
            tableView.getItems().add(maintenanceLog);

            // Clear input fields after adding maintenance log
            maintenanceIdText.clear();
            dateText.clear();
            descriptionText.clear();
            equipmentIdText.clear();
            platformIdText.clear();
            personnelIdText.clear();
        });

        vbox.getChildren().addAll(
                maintenanceIdLabel, maintenanceIdText, dateLabel, dateText,
                descriptionLabel, descriptionText, equipmentIdLabel, equipmentIdText,
                platformIdLabel, platformIdText, personnelIdLabel, personnelIdText,
                tableView, createButton);

        return vbox;
    }
}

