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
import java.time.format.DateTimeFormatter;

public class Equipments {

    private String equipmentId;
    private String name;
    private String type;
    private String status;
    private String location;
    private String lastMaintenance;
    private String sensorId;

    public Equipments(String equipmentId, String name, String type, String status, String location, String lastMaintenance, String sensorId) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.location = location;
        this.lastMaintenance = lastMaintenance;
        this.sensorId = sensorId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(String lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public static VBox getEquipmentsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);
        equipmentIdText.setDisable(true);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            equipmentIdText.setDisable(newValue);
            if (newValue) {
                equipmentIdText.setText("Auto Generated");
            } else {
                equipmentIdText.clear();
            }
        });

        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        DatePicker lastMaintenancePicker = new DatePicker();
        Label sensorIdLabel = new Label("Sensor ID:");
        ComboBox<String> sensorIdComboBox = new ComboBox<>();

        // Populate sensor ID ComboBox
        ObservableList<String> sensorIdList = fetchSensorIdsFromDatabase();
        sensorIdComboBox.setItems(sensorIdList);

        TableView<Equipments> tableView = new TableView<>();
        TableColumn<Equipments, String> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<Equipments, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Equipments, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Equipments, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Equipments, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Equipments, String> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));
        TableColumn<Equipments, String> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));

        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol, locationCol, lastMaintenanceCol, sensorIdCol);

        Button createButton = new Button("Create");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(createButton, editButton, deleteButton);

        createButton.setOnAction(e -> {
            String equipmentId = autoGenerateIdCheckBox.isSelected() ? null : equipmentIdText.getText();
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            String location = locationText.getText();
            LocalDate lastMaintenance = lastMaintenancePicker.getValue();
            String sensorId = sensorIdComboBox.getValue();

            String lastMaintenanceStr = lastMaintenance != null ? lastMaintenance.format(DateTimeFormatter.ISO_DATE) : "";

            Equipments equipment = new Equipments(equipmentId != null ? equipmentId : "", name, type, status, location, lastMaintenanceStr, sensorId);

            saveEquipmentToDatabase(equipment, autoGenerateIdCheckBox.isSelected());

            if (autoGenerateIdCheckBox.isSelected()) {
                equipment.setEquipmentId(fetchLastInsertedEquipmentId());
            }

            tableView.getItems().add(equipment);

            equipmentIdText.clear();
            nameText.clear();
            typeText.clear();
            statusText.clear();
            locationText.clear();
            lastMaintenancePicker.setValue(null);
            sensorIdComboBox.setValue(null); // Clear ComboBox selection
            autoGenerateIdCheckBox.setSelected(true);
        });

        editButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                equipmentIdText.setText(selectedEquipment.getEquipmentId());
                nameText.setText(selectedEquipment.getName());
                typeText.setText(selectedEquipment.getType());
                statusText.setText(selectedEquipment.getStatus());
                locationText.setText(selectedEquipment.getLocation());
                lastMaintenancePicker.setValue(LocalDate.parse(selectedEquipment.getLastMaintenance(), DateTimeFormatter.ISO_DATE));
                sensorIdComboBox.setValue(selectedEquipment.getSensorId());
                autoGenerateIdCheckBox.setSelected(false);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Equipment Selected", "Please select an equipment to edit.");
            }
        });

        deleteButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT WHERE EQUIPMENT_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedEquipment.getEquipmentId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Equipment Deleted", "Equipment with ID " + selectedEquipment.getEquipmentId() + " has been deleted.");
                        tableView.getItems().remove(selectedEquipment);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete equipment from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Equipment Selected", "Please select an equipment to delete.");
            }
        });

        ObservableList<Equipments> equipmentList = fetchEquipmentsFromDatabase();
        tableView.setItems(equipmentList);

        vbox.getChildren().addAll(
                equipmentIdLabel, equipmentIdText, autoGenerateIdCheckBox, nameLabel, nameText,
                typeLabel, typeText, statusLabel, statusText, locationLabel,
                locationText, lastMaintenanceLabel, lastMaintenancePicker,
                sensorIdLabel, sensorIdComboBox, tableView, buttonBox);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static void saveEquipmentToDatabase(Equipments equipment, boolean autoGenerateId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql;
            if (autoGenerateId) {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT (NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSORS_SENSOR_ID) VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT (EQUIPMENT_ID, NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSORS_SENSOR_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
            }

            PreparedStatement pstmt = conn.prepareStatement(sql, autoGenerateId ? new String[]{"EQUIPMENT_ID"} : null);

            int parameterIndex = 1;
            if (!autoGenerateId) {
                pstmt.setString(parameterIndex++, equipment.getEquipmentId());
            }
            pstmt.setString(parameterIndex++, equipment.getName());
            pstmt.setString(parameterIndex++, equipment.getType());
            pstmt.setString(parameterIndex++, equipment.getStatus());
            pstmt.setString(parameterIndex++, equipment.getLocation());
            pstmt.setString(parameterIndex++, equipment.getLastMaintenance());
            pstmt.setString(parameterIndex++, equipment.getSensorId());

            pstmt.executeUpdate();

            if (autoGenerateId) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    equipment.setEquipmentId(generatedKeys.getString(1));
                }
            }

            System.out.println("Equipment saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static ObservableList<Equipments> fetchEquipmentsFromDatabase() {
        ObservableList<Equipments> equipmentList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID, NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSORS_SENSOR_ID FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("EQUIPMENT_ID");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String status = rs.getString("STATUS");
                String location = rs.getString("LOCATION");
                String lastMaintenance = rs.getString("LAST_MAINTENANCE");
                String sensorId = rs.getString("SENSORS_SENSOR_ID");

                Equipments equipment = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
                equipmentList.add(equipment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return equipmentList;
    }

    private static ObservableList<String> fetchSensorIdsFromDatabase() {
        ObservableList<String> sensorIdList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String sensorId = rs.getString("SENSOR_ID");
                sensorIdList.add(sensorId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return sensorIdList;
    }

    private static String fetchLastInsertedEquipmentId() {
        String lastInsertedId = null;

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT ORDER BY EQUIPMENT_ID DESC FETCH FIRST 1 ROW ONLY";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                lastInsertedId = rs.getString("EQUIPMENT_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lastInsertedId;
    }
}
