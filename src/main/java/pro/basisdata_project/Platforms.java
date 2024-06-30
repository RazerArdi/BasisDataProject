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
import java.time.LocalDate;

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

        Label platformIdLabel = new Label("Platform ID *:");
        TextField platformIdText = new TextField();
        CheckBox autoGenerateCheckbox = new CheckBox("Auto Generate");
        autoGenerateCheckbox.setSelected(true);
        autoGenerateCheckbox.setOnAction(e -> {
            platformIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                platformIdText.clear();
            }
        });

        Label platformNameLabel = new Label("Platform Name *:");
        TextField platformNameText = new TextField();
        Label typeLabel = new Label("Type *:");
        TextField typeText = new TextField();
        Label capabilityLabel = new Label("Capability *:");
        TextField capabilityText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        DatePicker lastMaintenancePicker = new DatePicker();
        Label missionsMissionIdLabel = new Label("Mission ID *:");
        ComboBox<String> missionsMissionIdComboBox = new ComboBox<>();
        ObservableList<String> missionIds = fetchMissionIdsFromDatabase();
        missionsMissionIdComboBox.setItems(missionIds);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

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
                lastMaintenancePicker.setValue(selectedPlatform.getLastMaintenance() != null ? LocalDate.parse(selectedPlatform.getLastMaintenance()) : null);
                missionsMissionIdComboBox.setValue(selectedPlatform.getMissionsMissionId());
                autoGenerateCheckbox.setSelected(false);
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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String platformId = autoGenerateCheckbox.isSelected() ? "AUTO_GENERATED" : platformIdText.getText();
            String platformName = platformNameText.getText();
            String type = typeText.getText();
            String capability = capabilityText.getText();
            String lastMaintenance = lastMaintenancePicker.getValue() != null ? lastMaintenancePicker.getValue().toString() : null;
            String missionsMissionId = missionsMissionIdComboBox.getValue();

            if (platformName.isEmpty() || type.isEmpty() || capability.isEmpty() || missionsMissionId == null) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            Platforms platform = new Platforms(platformId, platformName, type, capability, lastMaintenance, missionsMissionId);
            System.out.println("Platform Created: " + platform.getPlatformId());

            savePlatformToDatabase(platform);

            tableView.getItems().add(platform);

            platformIdText.clear();
            platformNameText.clear();
            typeText.clear();
            capabilityText.clear();
            lastMaintenancePicker.setValue(null);
            missionsMissionIdComboBox.setValue(null);
            autoGenerateCheckbox.setSelected(true);
            errorLabel.setText("");
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton, createButton);

        ObservableList<Platforms> platformList = fetchPlatformsFromDatabase();
        tableView.setItems(platformList);

        vbox.getChildren().addAll(
                platformIdLabel, new HBox(10, platformIdText, autoGenerateCheckbox),
                platformNameLabel, platformNameText,
                typeLabel, typeText,
                capabilityLabel, capabilityText,
                lastMaintenanceLabel, lastMaintenancePicker,
                missionsMissionIdLabel, missionsMissionIdComboBox,
                errorLabel, tableView, buttonBox);

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

    private static ObservableList<String> fetchMissionIdsFromDatabase() {
        ObservableList<String> missionIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT MISSION_ID FROM \"C4ISR PROJECT (BASIC) V2\".MISSIONS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String missionId = rs.getString("MISSION_ID");
                missionIds.add(missionId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return missionIds;
    }

    private static void savePlatformToDatabase(Platforms platform) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql;
            if (platform.getPlatformId().equals("AUTO_GENERATED")) {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PLATFORMS (PLATFORM_NAME, TYPE, CAPABILITY, LAST_MAINTENANCE, MISSIONS_MISSION_ID) VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
            } else {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".PLATFORMS (PLATFORM_ID, PLATFORM_NAME, TYPE, CAPABILITY, LAST_MAINTENANCE, MISSIONS_MISSION_ID) VALUES (?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            int parameterIndex = 1;
            if (!platform.getPlatformId().equals("AUTO_GENERATED")) {
                pstmt.setString(parameterIndex++, platform.getPlatformId());
            }
            pstmt.setString(parameterIndex++, platform.getPlatformName());
            pstmt.setString(parameterIndex++, platform.getType());
            pstmt.setString(parameterIndex++, platform.getCapability());
            pstmt.setString(parameterIndex++, platform.getLastMaintenance());
            pstmt.setString(parameterIndex++, platform.getMissionsMissionId());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Platform Saved", "Platform has been saved successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Save Failed", "Failed to save platform.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


