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
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label lastMaintenanceLabel = new Label("Last Maintenance:");
        TextField lastMaintenanceText = new TextField();
        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();

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
        createButton.setOnAction(e -> {
            String equipmentId = equipmentIdText.getText();
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            String location = locationText.getText();
            String lastMaintenance = lastMaintenanceText.getText();
            String sensorId = sensorIdText.getText();

            Equipments equipment = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
            System.out.println("Equipment Created: " + equipment.getEquipmentId());

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC)\".EQUIPMENTS (EQUIPMENT_ID, NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSOR_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, equipmentId);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setString(4, status);
                pstmt.setString(5, location);
                pstmt.setString(6, lastMaintenance);
                pstmt.setString(7, sensorId);
                pstmt.executeUpdate();
                System.out.println("Equipment saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Add new equipment to the table view
            tableView.getItems().add(equipment);

            // Clear input fields after adding equipment
            equipmentIdText.clear();
            nameText.clear();
            typeText.clear();
            statusText.clear();
            locationText.clear();
            lastMaintenanceText.clear();
            sensorIdText.clear();
        });

        // Fetch and display data from Oracle database
        ObservableList<Equipments> equipmentList = fetchEquipmentsFromDatabase();
        tableView.setItems(equipmentList);

        vbox.getChildren().addAll(
                equipmentIdLabel, equipmentIdText, nameLabel, nameText,
                typeLabel, typeText, statusLabel, statusText, locationLabel,
                locationText, lastMaintenanceLabel, lastMaintenanceText,
                sensorIdLabel, sensorIdText, tableView, createButton);

        return vbox;
    }

    private static ObservableList<Equipments> fetchEquipmentsFromDatabase() {
        ObservableList<Equipments> equipmentList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT EQUIPMENT_ID, NAME, TYPE, STATUS, LOCATION, LAST_MAINTENANCE, SENSOR_ID FROM \"C4ISR PROJECT (BASIC)\".EQUIPMENTS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String equipmentId = rs.getString("EQUIPMENT_ID");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String status = rs.getString("STATUS");
                String location = rs.getString("LOCATION");
                String lastMaintenance = rs.getString("LAST_MAINTENANCE");
                String sensorId = rs.getString("SENSOR_ID");

                Equipments equipment = new Equipments(equipmentId, name, type, status, location, lastMaintenance, sensorId);
                equipmentList.add(equipment);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return equipmentList;
    }
}
