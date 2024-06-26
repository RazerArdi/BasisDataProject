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

public class Sensors {
    private int sensorId;
    private String type;
    private String location;
    private String status;
    private LocalDate lastMaintenance;

    public Sensors(int sensorId, String type, String location, String status, LocalDate lastMaintenance) {
        this.sensorId = sensorId;
        this.type = type;
        this.location = location;
        this.status = status;
        this.lastMaintenance = lastMaintenance;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(LocalDate lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public static VBox getSensorsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();
        CheckBox autoGenerateIdCheckBox = new CheckBox("Auto Generate ID");
        autoGenerateIdCheckBox.setSelected(true);
        sensorIdText.setDisable(true);

        autoGenerateIdCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            sensorIdText.setDisable(newValue);
            if (newValue) {
                sensorIdText.setText("Auto Generated");
            } else {
                sensorIdText.clear();
            }
        });

        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label statusLabel = new Label("Status:");
        ChoiceBox<String> statusChoice = new ChoiceBox<>();
        statusChoice.getItems().addAll("Active", "Inactive", "Passive");

        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        DatePicker lastMaintenancePicker = new DatePicker();

        TableView<Sensors> tableView = new TableView<>();
        TableColumn<Sensors, Integer> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        TableColumn<Sensors, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Sensors, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Sensors, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Sensors, LocalDate> lastMaintenanceCol = new TableColumn<>("Last Maintenance");
        lastMaintenanceCol.setCellValueFactory(new PropertyValueFactory<>("lastMaintenance"));

        tableView.getColumns().addAll(sensorIdCol, typeCol, locationCol, statusCol, lastMaintenanceCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Sensors selectedSensor = tableView.getSelectionModel().getSelectedItem();
            if (selectedSensor != null) {
                sensorIdText.setText(String.valueOf(selectedSensor.getSensorId()));
                typeText.setText(selectedSensor.getType());
                locationText.setText(selectedSensor.getLocation());
                statusChoice.setValue(selectedSensor.getStatus());
                lastMaintenancePicker.setValue(selectedSensor.getLastMaintenance());
                autoGenerateIdCheckBox.setSelected(false);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Sensor Selected", "Please select a sensor to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Sensors selectedSensor = tableView.getSelectionModel().getSelectedItem();
            if (selectedSensor != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS WHERE SENSOR_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, selectedSensor.getSensorId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Sensor Deleted", "Sensor with ID " + selectedSensor.getSensorId() + " has been deleted.");
                        tableView.getItems().remove(selectedSensor);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete sensor from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Sensor Selected", "Please select a sensor to delete.");
            }
        });

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            Integer sensorId = autoGenerateIdCheckBox.isSelected() ? null : Integer.parseInt(sensorIdText.getText());
            String type = typeText.getText();
            String location = locationText.getText();
            String status = statusChoice.getValue();
            LocalDate lastMaintenance = lastMaintenancePicker.getValue();

            Sensors sensor = new Sensors(sensorId != null ? sensorId : 0, type, location, status, lastMaintenance);

            saveSensorToDatabase(sensor, autoGenerateIdCheckBox.isSelected());

            if (autoGenerateIdCheckBox.isSelected()) {
                sensor.setSensorId(fetchLastInsertedSensorId());
            }

            tableView.getItems().add(sensor);

            sensorIdText.clear();
            typeText.clear();
            locationText.clear();
            statusChoice.setValue(null);
            lastMaintenancePicker.getEditor().clear();
            autoGenerateIdCheckBox.setSelected(true);
        });

        HBox buttonBox = new HBox(10, editButton, deleteButton, createButton);

        ObservableList<Sensors> sensorList = fetchSensorsFromDatabase();
        tableView.setItems(sensorList);

        vbox.getChildren().addAll(
                sensorIdLabel, sensorIdText, autoGenerateIdCheckBox, typeLabel, typeText,
                locationLabel, locationText, statusLabel, statusChoice,
                lastMaintenanceLabel, lastMaintenancePicker,
                tableView, buttonBox);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static void saveSensorToDatabase(Sensors sensor, boolean autoGenerateId) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql;
            if (autoGenerateId) {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".SENSORS (TYPE, LOCATION, STATUS, LAST_MAINTENANCE) " +
                        "VALUES (?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".SENSORS (SENSOR_ID, TYPE, LOCATION, STATUS, LAST_MAINTENANCE) " +
                        "VALUES (?, ?, ?, ?, ?)";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"SENSOR_ID"});
            int parameterIndex = 1;
            if (!autoGenerateId) {
                pstmt.setInt(parameterIndex++, sensor.getSensorId());
            }
            pstmt.setString(parameterIndex++, sensor.getType());
            pstmt.setString(parameterIndex++, sensor.getLocation());
            pstmt.setString(parameterIndex++, sensor.getStatus());
            pstmt.setDate(parameterIndex, java.sql.Date.valueOf(sensor.getLastMaintenance()));
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static int fetchLastInsertedSensorId() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID_SEQ.currval FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    private static ObservableList<Sensors> fetchSensorsFromDatabase() {
        ObservableList<Sensors> sensorList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID, TYPE, LOCATION, STATUS, LAST_MAINTENANCE FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int sensorId = rs.getInt("SENSOR_ID");
                String type = rs.getString("TYPE");
                String location = rs.getString("LOCATION");
                String status = rs.getString("STATUS");
                LocalDate lastMaintenance = rs.getDate("LAST_MAINTENANCE").toLocalDate();

                Sensors sensor = new Sensors(sensorId, type, location, status, lastMaintenance);
                sensorList.add(sensor);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return sensorList;
    }
}
