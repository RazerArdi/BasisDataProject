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
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label statusLabel = new Label("Status:");
        ChoiceBox<String> statusChoice = new ChoiceBox<>();
        statusChoice.getItems().addAll("Active", "Inactive", "Passive"); // Menambahkan opsi status

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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int sensorId = Integer.parseInt(sensorIdText.getText());
            String type = typeText.getText();
            String location = locationText.getText();
            String status = statusChoice.getValue();
            LocalDate lastMaintenance = lastMaintenancePicker.getValue();

            Sensors sensor = new Sensors(sensorId, type, location, status, lastMaintenance);
            System.out.println("Sensor Created: " + sensor.getSensorId());

            saveSensorToDatabase(sensor);

            tableView.getItems().add(sensor);

            sensorIdText.clear();
            typeText.clear();
            locationText.clear();
            statusChoice.setValue(null);
            lastMaintenancePicker.getEditor().clear();
        });

        ObservableList<Sensors> sensorList = fetchSensorsFromDatabase();
        tableView.setItems(sensorList);

        vbox.getChildren().addAll(
                sensorIdLabel, sensorIdText, typeLabel, typeText,
                locationLabel, locationText, statusLabel, statusChoice,
                lastMaintenanceLabel, lastMaintenancePicker,
                tableView, createButton);

        return vbox;
    }

    private static void saveSensorToDatabase(Sensors sensor) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".SENSORS (SENSOR_ID, TYPE, LOCATION, STATUS, LAST_MAINTENANCE) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sensor.getSensorId());
            pstmt.setString(2, sensor.getType());
            pstmt.setString(3, sensor.getLocation());
            pstmt.setString(4, sensor.getStatus());
            pstmt.setDate(5, java.sql.Date.valueOf(sensor.getLastMaintenance()));
            pstmt.executeUpdate();
            System.out.println("Sensor saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
