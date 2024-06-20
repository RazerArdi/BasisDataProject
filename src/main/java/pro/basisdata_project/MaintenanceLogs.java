package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        DatePicker dateText = new DatePicker(); // Changed to DatePicker for date input
        Label descriptionLabel = new Label("Description:");
        TextField descriptionText = new TextField();
        Label equipmentIdLabel = new Label("Equipment ID:");
        ComboBox<String> equipmentIdCombo = new ComboBox<>();
        Label platformIdLabel = new Label("Platform ID:");
        ComboBox<String> platformIdCombo = new ComboBox<>();
        Label personnelIdLabel = new Label("Personnel ID:");
        ComboBox<String> personnelIdCombo = new ComboBox<>();

        // Fetching data from database for ComboBoxes
        ObservableList<String> equipmentIds = fetchEquipmentIds();
        equipmentIdCombo.setItems(equipmentIds);

        ObservableList<String> platformIds = fetchPlatformIds();
        platformIdCombo.setItems(platformIds);

        ObservableList<String> personnelIds = fetchPersonnelIds();
        personnelIdCombo.setItems(personnelIds);

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
            String date = dateText.getValue().toString(); // Convert DatePicker value to String
            String description = descriptionText.getText();
            String equipmentId = equipmentIdCombo.getValue();
            String platformId = platformIdCombo.getValue();
            String personnelId = personnelIdCombo.getValue();

            MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
            System.out.println("Maintenance Log Created: " + maintenanceLog.getMaintenanceId());

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC)\".MAINTENANCELOGS (MAINTENANCE_ID, \"DATE\", DESCRIPTION, EQUIPMENT_ID, PLATFORM_ID, PERSONNEL_ID) " +
                        "VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, maintenanceId);
                pstmt.setString(2, date);
                pstmt.setString(3, description);
                pstmt.setString(4, equipmentId);
                pstmt.setString(5, platformId);
                pstmt.setString(6, personnelId);
                pstmt.executeUpdate();
                System.out.println("Maintenance log saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Add new maintenance log to the table view
            tableView.getItems().add(maintenanceLog);

            // Clear input fields after adding maintenance log
            maintenanceIdText.clear();
            dateText.getEditor().clear(); // Clear DatePicker editor
            descriptionText.clear();
            equipmentIdCombo.getSelectionModel().clearSelection();
            platformIdCombo.getSelectionModel().clearSelection();
            personnelIdCombo.getSelectionModel().clearSelection();
        });

        // Fetch and display data from Oracle database
        ObservableList<MaintenanceLogs> maintenanceLogsList = fetchMaintenanceLogsFromDatabase();
        tableView.setItems(maintenanceLogsList);

        vbox.getChildren().addAll(
                maintenanceIdLabel, maintenanceIdText, dateLabel, dateText,
                descriptionLabel, descriptionText, equipmentIdLabel, equipmentIdCombo,
                platformIdLabel, platformIdCombo, personnelIdLabel, personnelIdCombo,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<String> fetchEquipmentIds() {
        ObservableList<String> equipmentIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID FROM \"C4ISR PROJECT (BASIC)\".EQUIPMENT";
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
            String sql = "SELECT PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC)\".PERSONNEL";
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

    private static ObservableList<MaintenanceLogs> fetchMaintenanceLogsFromDatabase() {
        ObservableList<MaintenanceLogs> maintenanceLogsList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT MAINTENANCE_ID, \"DATE\", DESCRIPTION, EQUIPMENT_ID, PLATFORM_ID, PERSONNEL_ID FROM \"C4ISR PROJECT (BASIC) V2\".MAINTENANCELOGS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String maintenanceId = rs.getString("MAINTENANCE_ID");
                String date = rs.getString("DATE");
                String description = rs.getString("DESCRIPTION");
                String equipmentId = rs.getString("EQUIPMENT_ID");
                String platformId = rs.getString("PLATFORM_ID");
                String personnelId = rs.getString("PERSONNEL_ID");

                MaintenanceLogs maintenanceLog = new MaintenanceLogs(maintenanceId, date, description, equipmentId, platformId, personnelId);
                maintenanceLogsList.add(maintenanceLog);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return maintenanceLogsList;
    }
}
