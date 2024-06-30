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
        autoGenerateCheckbox.setSelected(true);
        HBox maintenanceIdBox = new HBox(10);
        maintenanceIdBox.getChildren().addAll(maintenanceIdText, autoGenerateCheckbox);
        autoGenerateCheckbox.setOnAction(e -> {
            maintenanceIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                maintenanceIdText.setText("");
            }
        });
        Label dateLabel = new Label("DATE MAINTENANCE *:");
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
        TableColumn<MaintenanceLogs, LocalDate> dateCol = new TableColumn<>("DATE MAINTENANCE");
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
        tableView.setItems(MaintenanceLogs.fetchAllMaintenanceLogs());

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

            if (autoGenerateCheckbox.isSelected()) {
                maintenanceId = getNextMaintenanceId();
            }

            if (maintenanceId.isEmpty() || date == null || equipmentId.isEmpty() || platformId.isEmpty() || personnelId.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
            System.out.println("Maintenance Log Created: " + maintenanceLog.getMaintenanceId());

            saveMaintenanceLogToDatabase(maintenanceLog);

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
                newMaintenanceId = getNextMaintenanceId();
            }

            updateMaintenanceLogInDatabase(selectedMaintenanceLog, newMaintenanceId, newDate, newDescription, newEquipmentId, newPlatformId, newPersonnelId);

            selectedMaintenanceLog.setMaintenanceId(newMaintenanceId);
            selectedMaintenanceLog.setDate(newDate);
            selectedMaintenanceLog.setDescription(newDescription);
            selectedMaintenanceLog.setEquipmentId(newEquipmentId);
            selectedMaintenanceLog.setPlatformId(newPlatformId);
            selectedMaintenanceLog.setPersonnelId(newPersonnelId);

            tableView.refresh();

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

            deleteMaintenanceLogFromDatabase(selectedMaintenanceLog);

            tableView.getItems().remove(selectedMaintenanceLog);

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

        vbox.getChildren().addAll(maintenanceIdLabel, maintenanceIdBox,
                dateLabel, dateText, descriptionLabel, descriptionText,
                equipmentIdLabel, equipmentIdCombo, platformIdLabel, platformIdCombo,
                personnelIdLabel, personnelIdCombo, errorLabel, buttonBox, tableView);

        return vbox;
    }

    private static String getNextMaintenanceId() {
        String maintenanceId = null;
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".rel_10_seq.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                maintenanceId = rs.getString(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return maintenanceId;
    }

    private static void saveMaintenanceLogToDatabase(MaintenanceLogs maintenanceLog) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS (MAINTENANCE_ID, DATE_MAINTENANCE, DESCRIPTION, EQUIPMENT_EQUIPMENT_ID, PLATFORMS_PLATFORM_ID, PERSONNEL_PERSONNEL_ID) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maintenanceLog.getMaintenanceId());
            pstmt.setDate(2, java.sql.Date.valueOf(maintenanceLog.getDate()));
            pstmt.setString(3, maintenanceLog.getDescription());
            pstmt.setString(4, maintenanceLog.getEquipmentId());
            pstmt.setString(5, maintenanceLog.getPlatformId());
            pstmt.setString(6, maintenanceLog.getPersonnelId());
            pstmt.executeUpdate();
            System.out.println("Maintenance log saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void updateMaintenanceLogInDatabase(MaintenanceLogs selectedMaintenanceLog, String newMaintenanceId, LocalDate newDate, String newDescription, String newEquipmentId, String newPlatformId, String newPersonnelId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS SET MAINTENANCE_ID = ?, DATE_MAINTENANCE = ?, " +
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
                System.out.println("Maintenance log updated: " + selectedMaintenanceLog.getMaintenanceId());
            } else {
                System.out.println("Failed to update maintenance log.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteMaintenanceLogFromDatabase(MaintenanceLogs selectedMaintenanceLog) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS WHERE MAINTENANCE_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, selectedMaintenanceLog.getMaintenanceId());
            int deleted = pstmt.executeUpdate();

            if (deleted > 0) {
                System.out.println("Maintenance log deleted: " + selectedMaintenanceLog.getMaintenanceId());
            } else {
                System.out.println("Failed to delete maintenance log.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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

    public static ObservableList<MaintenanceLogs> fetchAllMaintenanceLogs() {
        ObservableList<MaintenanceLogs> maintenanceLogsList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT MAINTENANCE_ID, DATE_MAINTENANCE, DESCRIPTION, EQUIPMENT_EQUIPMENT_ID, PLATFORMS_PLATFORM_ID, PERSONNEL_PERSONNEL_ID " +
                    "FROM \"C4ISR PROJECT (BASIC) V2\".MAINTENANCE_LOGS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String maintenanceId = rs.getString("MAINTENANCE_ID");
                LocalDate date = rs.getDate("DATE_MAINTENANCE").toLocalDate();
                String description = rs.getString("DESCRIPTION");
                String equipmentId = rs.getString("EQUIPMENT_EQUIPMENT_ID");
                String platformId = rs.getString("PLATFORMS_PLATFORM_ID");
                String personnelId = rs.getString("PERSONNEL_PERSONNEL_ID");

                MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
                maintenanceLogsList.add(maintenanceLog);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return maintenanceLogsList;
    }
}
