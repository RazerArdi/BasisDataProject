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

public class MaintenanceLogs {
    private String maintenanceId;
    private LocalDate date;
    private String description;
    private String equipmentId;
    private String platformId;
    private String personnelId;

    public MaintenanceLogs(String maintenanceId, LocalDate date, String description, String equipmentId, String platformId, String personnelId) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

        Label maintenanceIdLabel = new Label("Maintenance ID *:");
        TextField maintenanceIdText = new TextField();
        CheckBox autoGenerateCheckbox = new CheckBox("Auto Generate");
        autoGenerateCheckbox.setSelected(true); // Default auto-generate is checked
        autoGenerateCheckbox.setOnAction(e -> {
            maintenanceIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                maintenanceIdText.setText(""); // Clear text field if auto-generate is checked
            }
        });

        Label dateLabel = new Label("Date *:");
        DatePicker dateText = new DatePicker();
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionText = new TextArea();
        descriptionText.setWrapText(true);
        descriptionText.setMaxHeight(100);
        Label equipmentIdLabel = new Label("Equipment ID *:");
        ComboBox<String> equipmentIdCombo = new ComboBox<>();
        Label platformIdLabel = new Label("Platform ID *:");
        ComboBox<String> platformIdCombo = new ComboBox<>();
        Label personnelIdLabel = new Label("Personnel ID *:");
        ComboBox<String> personnelIdCombo = new ComboBox<>();

        ObservableList<String> equipmentIds = fetchEquipmentIds();
        equipmentIdCombo.setItems(equipmentIds);

        ObservableList<String> platformIds = fetchPlatformIds();
        platformIdCombo.setItems(platformIds);

        ObservableList<String> personnelIds = fetchPersonnelIds();
        personnelIdCombo.setItems(personnelIds);

        TableView<MaintenanceLogs> tableView = new TableView<>();
        TableColumn<MaintenanceLogs, String> maintenanceIdCol = new TableColumn<>("Maintenance ID");
        maintenanceIdCol.setCellValueFactory(new PropertyValueFactory<>("maintenanceId"));
        TableColumn<MaintenanceLogs, LocalDate> dateCol = new TableColumn<>("Date");
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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String maintenanceId = maintenanceIdText.getText();
            LocalDate date = dateText.getValue();
            String description = descriptionText.getText();
            String equipmentId = equipmentIdCombo.getValue();
            String platformId = platformIdCombo.getValue();
            String personnelId = personnelIdCombo.getValue();

            if (maintenanceId.isEmpty() || date == null || equipmentId.isEmpty() || platformId.isEmpty() || personnelId.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (autoGenerateCheckbox.isSelected()) {
                maintenanceId = "AUTO_GENERATED"; // Set to AUTO_GENERATED for auto-generation
            }

            MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
            System.out.println("Maintenance Log Created: " + maintenanceLog.getMaintenanceId());

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS (MAINTENANCE_ID, \"DATE\", DESCRIPTION, EQUIPMENT_EQUIPMENT_ID, PLATFORMS_PLATFORM_ID, PERSONNEL_PERSONNEL_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, maintenanceId);
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                pstmt.setString(3, description);
                pstmt.setString(4, equipmentId);
                pstmt.setString(5, platformId);
                pstmt.setString(6, personnelId);
                pstmt.executeUpdate();
                System.out.println("Maintenance log saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(maintenanceLog);

            maintenanceIdText.clear();
            dateText.getEditor().clear();
            descriptionText.clear();
            equipmentIdCombo.getSelectionModel().clearSelection();
            platformIdCombo.getSelectionModel().clearSelection();
            personnelIdCombo.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            MaintenanceLogs selectedMaintenanceLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedMaintenanceLog == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Maintenance Log Selected", "Please select a maintenance log to edit.");
                return;
            }

            String newMaintenanceId = maintenanceIdText.getText();
            LocalDate newDate = dateText.getValue();
            String newDescription = descriptionText.getText();
            String newEquipmentId = equipmentIdCombo.getValue();
            String newPlatformId = platformIdCombo.getValue();
            String newPersonnelId = personnelIdCombo.getValue();

            if (autoGenerateCheckbox.isSelected()) {
                newMaintenanceId = "AUTO_GENERATED"; // Set to AUTO_GENERATED for auto-generation
            }

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS SET MAINTENANCE_ID = ?, \"DATE\" = ?, " +
                        "DESCRIPTION = ?, EQUIPMENT_EQUIPMENT_ID = ?, PLATFORMS_PLATFORM_ID = ?, PERSONNEL_PERSONNEL_ID = ? " +
                        "WHERE MAINTENANCE_ID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newMaintenanceId);
                pstmt.setDate(2, java.sql.Date.valueOf(newDate));
                pstmt.setString(3, newDescription);
                pstmt.setString(4, newEquipmentId);
                pstmt.setString(5, newPlatformId);
                pstmt.setString(6, newPersonnelId);
                pstmt.setString(7, selectedMaintenanceLog.getMaintenanceId());
                int updated = pstmt.executeUpdate();

                if (updated > 0) {
                    selectedMaintenanceLog.setMaintenanceId(newMaintenanceId);
                    selectedMaintenanceLog.setDate(newDate);
                    selectedMaintenanceLog.setDescription(newDescription);
                    selectedMaintenanceLog.setEquipmentId(newEquipmentId);
                    selectedMaintenanceLog.setPlatformId(newPlatformId);
                    selectedMaintenanceLog.setPersonnelId(newPersonnelId);
                    tableView.refresh();
                    System.out.println("Maintenance log updated: " + selectedMaintenanceLog.getMaintenanceId());
                } else {
                    System.out.println("Failed to update maintenance log.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            maintenanceIdText.clear();
            dateText.getEditor().clear();
            descriptionText.clear();
            equipmentIdCombo.getSelectionModel().clearSelection();
            platformIdCombo.getSelectionModel().clearSelection();
            personnelIdCombo.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            MaintenanceLogs selectedMaintenanceLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedMaintenanceLog == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Maintenance Log Selected", "Please select a maintenance log to delete.");
                return;
            }

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS WHERE MAINTENANCE_ID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, selectedMaintenanceLog.getMaintenanceId());
                int deleted = pstmt.executeUpdate();

                if (deleted > 0) {
                    tableView.getItems().remove(selectedMaintenanceLog);
                    System.out.println("Maintenance log deleted: " + selectedMaintenanceLog.getMaintenanceId());
                } else {
                    System.out.println("Failed to delete maintenance log.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            maintenanceIdText.clear();
            dateText.getEditor().clear();
            descriptionText.clear();
            equipmentIdCombo.getSelectionModel().clearSelection();
            platformIdCombo.getSelectionModel().clearSelection();
            personnelIdCombo.getSelectionModel().clearSelection();
            errorLabel.setText("");
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(createButton, editButton, deleteButton);

        vbox.getChildren().addAll(maintenanceIdLabel, new HBox(maintenanceIdText, autoGenerateCheckbox),
                dateLabel, dateText, descriptionLabel, descriptionText,
                equipmentIdLabel, equipmentIdCombo, platformIdLabel, platformIdCombo,
                personnelIdLabel, personnelIdCombo, errorLabel, buttonBox, tableView);

        return vbox;
    }

    private static ObservableList<String> fetchEquipmentIds() {
        ObservableList<String> equipmentIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                equipmentIds.add(rs.getString("EQUIPMENT_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return equipmentIds;
    }

    private static ObservableList<String> fetchPlatformIds() {
        ObservableList<String> platformIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PLATFORM_ID FROM \"C4ISR PROJECT (BASIC) V2\".PLATFORMS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                platformIds.add(rs.getString("PLATFORM_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return platformIds;
    }

    private static ObservableList<String> fetchPersonnelIds() {
        ObservableList<String> personnelIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC) V2\".PERSONNEL";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                personnelIds.add(rs.getString("PERSONNEL_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return personnelIds;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
