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

public class Equipments {
    private String equipmentId;
    private String name;
    private String type;
    private String status;
    private String location;
    private LocalDate lastMaintenance;
    private String sensorId;

    public Equipments(String equipmentId, String name, String type, String status, String location, LocalDate lastMaintenance, String sensorId) {
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

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
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

        Label equipmentIdLabel = new Label("Equipment ID *:");
        TextField equipmentIdText = new TextField();
        CheckBox autoIncrementCheckBox = new CheckBox("AUTO-GENERATED");
        autoIncrementCheckBox.setSelected(true);
        autoIncrementCheckBox.setOnAction(e -> {
            equipmentIdText.setDisable(autoIncrementCheckBox.isSelected());
            if (autoIncrementCheckBox.isSelected()) {
                equipmentIdText.clear();
            }
        });

        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        ObservableList<String> statusList = FXCollections.observableArrayList("Active", "Inactive");
        statusComboBox.setItems(statusList);

        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        DatePicker lastMaintenancePicker = new DatePicker();

        Label sensorLabel = new Label("Sensor:");
        ComboBox<String> sensorComboBox = new ComboBox<>();
        ObservableList<String> sensorList = fetchSensorNamesFromDatabase();
        sensorComboBox.setItems(sensorList);

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
        TableColumn<Equipments, LocalDate> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));
        TableColumn<Equipments, String> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol, locationCol, lastMaintenanceCol, sensorIdCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String equipmentId;
            if (autoIncrementCheckBox.isSelected()) {
                equipmentId = "AUTO_GENERATED";
            } else {
                equipmentId = equipmentIdText.getText();
            }
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusComboBox.getValue();
            String location = locationText.getText();
            LocalDate lastMaintenance = lastMaintenancePicker.getValue();
            String sensorId = getSensorIdFromName(sensorComboBox.getValue());

            if (equipmentId.isEmpty() || status.isEmpty() || sensorId.isEmpty()) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            Equipments equipment = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
            saveEquipmentToDatabase(equipment, autoIncrementCheckBox.isSelected());

            tableView.getItems().add(equipment);

            equipmentIdText.clear();
            nameText.clear();
            typeText.clear();
            statusComboBox.setValue(null);
            locationText.clear();
            lastMaintenancePicker.setValue(null);
            sensorComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                String equipmentId = equipmentIdText.getText();
                String name = nameText.getText();
                String type = typeText.getText();
                String status = statusComboBox.getValue();
                String location = locationText.getText();
                LocalDate lastMaintenance = lastMaintenancePicker.getValue();
                String sensorId = getSensorIdFromName(sensorComboBox.getValue());

                selectedEquipment.setEquipmentId(equipmentId);
                selectedEquipment.setName(name);
                selectedEquipment.setType(type);
                selectedEquipment.setStatus(status);
                selectedEquipment.setLocation(location);
                selectedEquipment.setLastMaintenance(lastMaintenance);
                selectedEquipment.setSensorId(sensorId);

                updateEquipmentInDatabase(selectedEquipment);

                tableView.refresh();

                equipmentIdText.clear();
                nameText.clear();
                typeText.clear();
                statusComboBox.setValue(null);
                locationText.clear();
                lastMaintenancePicker.setValue(null);
                sensorComboBox.setValue(null);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                deleteEquipmentFromDatabase(selectedEquipment);
                tableView.getItems().remove(selectedEquipment);
            }
        });

        ObservableList<Equipments> equipmentList = fetchEquipmentsFromDatabase();
        tableView.setItems(equipmentList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                equipmentIdLabel, new HBox(10, equipmentIdText, autoIncrementCheckBox),
                nameLabel, nameText,
                typeLabel, typeText,
                statusLabel, statusComboBox,
                locationLabel, locationText,
                lastMaintenanceLabel, lastMaintenancePicker,
                sensorLabel, sensorComboBox,
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

    private static void saveEquipmentToDatabase(Equipments equipment, boolean autoIncrementId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql;
            if (autoIncrementId) {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT (NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSORS_SENSOR_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT (EQUIPMENT_ID, NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSORS_SENSOR_ID) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int parameterIndex = 1;
            if (!autoIncrementId) {
                pstmt.setString(parameterIndex++, equipment.getEquipmentId());
            }
            pstmt.setString(parameterIndex++, equipment.getName());
            pstmt.setString(parameterIndex++, equipment.getType());
            pstmt.setString(parameterIndex++, equipment.getStatus());
            pstmt.setString(parameterIndex++, equipment.getLocation());
            pstmt.setDate(parameterIndex++, java.sql.Date.valueOf(equipment.getLastMaintenance()));
            pstmt.setString(parameterIndex, equipment.getSensorId());
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment Saved", "Equipment has been saved successfully.");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Save Equipment", "An error occurred while saving equipment.");
            ex.printStackTrace();
        }
    }

    private static void updateEquipmentInDatabase(Equipments equipment) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT SET NAME = ?, TYPE = ?, STATUS = ?, LOCATION = ?, LAST_MAINTENANCE = ?, SENSORS_SENSOR_ID = ? " +
                    "WHERE EQUIPMENT_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getType());
            pstmt.setString(3, equipment.getStatus());
            pstmt.setString(4, equipment.getLocation());
            pstmt.setDate(5, java.sql.Date.valueOf(equipment.getLastMaintenance()));
            pstmt.setString(6, equipment.getSensorId());
            pstmt.setString(7, equipment.getEquipmentId());
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment Updated", "Equipment has been updated successfully.");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Equipment", "An error occurred while updating equipment.");
            ex.printStackTrace();
        }
    }

    private static void deleteEquipmentFromDatabase(Equipments equipment) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".EQUIPMENT WHERE EQUIPMENT_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, equipment.getEquipmentId());
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment Deleted", "Equipment has been deleted successfully.");
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Delete Equipment", "An error occurred while deleting equipment.");
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
                LocalDate lastMaintenance = rs.getDate("LAST_MAINTENANCE").toLocalDate();
                String sensorId = rs.getString("SENSORS_SENSOR_ID");
                equipmentList.add(new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return equipmentList;
    }

    private static ObservableList<String> fetchSensorNamesFromDatabase() {
        ObservableList<String> sensorList = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String sensorName = rs.getString("SENSOR_ID");
                sensorList.add(sensorName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sensorList;
    }

    private static String getSensorIdFromName(String sensorName) {
        String sensorId = "";
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS WHERE SENSOR_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sensorName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                sensorId = rs.getString("SENSOR_ID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return sensorId;
    }
}
