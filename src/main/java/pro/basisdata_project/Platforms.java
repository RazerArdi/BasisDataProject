package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Platforms {

    private String platformId;
    private String platformName;
    private String type;
    private String capability;
    private String lastMaintenance;
    private String missionsMissionId;

    public Platforms(String platformId, String platformName, String type, String capability, String lastMaintenance, String missionsMissionId) {
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

    public String getMissionsMissionId() {
        return missionsMissionId;
    }

    public void setMissionsMissionId(String missionsMissionId) {
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
        TableColumn<Platforms, String> missionsMissionIdCol = new TableColumn<>("Mission ID");
        missionsMissionIdCol.setCellValueFactory(new PropertyValueFactory<>("missionsMissionId"));

        tableView.getColumns().addAll(platformIdCol, platformNameCol, typeCol, capabilityCol, lastMaintenanceCol, missionsMissionIdCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Platforms selectedPlatform = tableView.getSelectionModel().getSelectedItem();
            if (selectedPlatform != null) {
                platformIdText.setText(selectedPlatform.getPlatformId());
                platformNameText.setText(selectedPlatform.getPlatformName());
                typeText.setText(selectedPlatform.getType());
                capabilityText.setText(selectedPlatform.getCapability());
                lastMaintenanceText.setText(selectedPlatform.getLastMaintenance());
                missionsMissionIdText.setText(selectedPlatform.getMissionsMissionId());
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Platform Selected", "Please select a platform to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Platforms selectedPlatform = tableView.getSelectionModel().getSelectedItem();
            if (selectedPlatform != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".PLATFORMS WHERE PLATFORM_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedPlatform.getPlatformId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Platform Deleted", "Platform with ID " + selectedPlatform.getPlatformId() + " has been deleted.");
                        tableView.getItems().remove(selectedPlatform);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete platform from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Platform Selected", "Please select a platform to delete.");
            }
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String platformId = platformIdText.getText();
            String platformName = platformNameText.getText();
            String type = typeText.getText();
            String capability = capabilityText.getText();
            String lastMaintenance = lastMaintenanceText.getText();
            String missionsMissionId = missionsMissionIdText.getText();

            Platforms platform = new Platforms(platformId, platformName, type, capability, lastMaintenance, missionsMissionId);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PLATFORMS (PLATFORM_ID, PLATFORM_NAME, TYPE, CAPABILITY, LAST_MAINTENANCE, MISSIONS_MISSION_ID) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, platformId);
                pstmt.setString(2, platformName);
                pstmt.setString(3, type);
                pstmt.setString(4, capability);
                pstmt.setString(5, lastMaintenance);
                pstmt.setString(6, missionsMissionId);
                pstmt.executeUpdate();
                System.out.println("Platform saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(platform);

            platformIdText.clear();
            platformNameText.clear();
            typeText.clear();
            capabilityText.clear();
            lastMaintenanceText.clear();
            missionsMissionIdText.clear();
        });

        ObservableList<Platforms> platformList = fetchPlatformsFromDatabase();
        tableView.setItems(platformList);

        vbox.getChildren().addAll(
                platformIdLabel, platformIdText, platformNameLabel, platformNameText,
                typeLabel, typeText, capabilityLabel, capabilityText,
                lastMaintenanceLabel, lastMaintenanceText,
                missionsMissionIdLabel, missionsMissionIdText,
                tableView, buttonBox, createButton);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static ObservableList<Platforms> fetchPlatformsFromDatabase() {
        ObservableList<Platforms> platformList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PLATFORM_ID, PLATFORM_NAME, TYPE, CAPABILITY, LAST_MAINTENANCE, MISSIONS_MISSION_ID FROM \"C4ISR PROJECT (BASIC) V2\".PLATFORMS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String platformId = rs.getString("PLATFORM_ID");
                String platformName = rs.getString("PLATFORM_NAME");
                String type = rs.getString("TYPE");
                String capability = rs.getString("CAPABILITY");
                String lastMaintenance = rs.getString("LAST_MAINTENANCE");
                String missionsMissionId = rs.getString("MISSIONS_MISSION_ID");

                Platforms platform = new Platforms(platformId, platformName, type, capability, lastMaintenance, missionsMissionId);
                platformList.add(platform);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return platformList;
    }
}
