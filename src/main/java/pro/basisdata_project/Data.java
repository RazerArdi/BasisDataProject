package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDateTime;

public class Data {

    private int dataId;
    private java.sql.Timestamp timestamp;
    private String dataType;
    private String rawData;
    private String processedData;
    private int sensorsSensorId;

    public Data(int dataId, java.sql.Timestamp timestamp, String dataType, String rawData, String processedData, int sensorsSensorId) {
        this.dataId = dataId;
        this.timestamp = timestamp;
        this.dataType = dataType;
        this.rawData = rawData;
        this.processedData = processedData;
        this.sensorsSensorId = sensorsSensorId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getProcessedData() {
        return processedData;
    }

    public void setProcessedData(String processedData) {
        this.processedData = processedData;
    }

    public int getSensorsSensorId() {
        return sensorsSensorId;
    }

    public void setSensorsSensorId(int sensorsSensorId) {
        this.sensorsSensorId = sensorsSensorId;
    }

    public static VBox getDataUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label dataIdLabel = new Label("Data ID *:");
        TextField dataIdText = new TextField();
        Label dataTypeLabel = new Label("Data Type *:");
        TextField dataTypeText = new TextField();
        Label rawDataLabel = new Label("Raw Data:");
        TextArea rawDataText = new TextArea();
        Label processedDataLabel = new Label("Processed Data:");
        TextArea processedDataText = new TextArea();
        Label sensorsSensorIdLabel = new Label("Sensor ID *:");
        ComboBox<Integer> sensorsSensorIdComboBox = new ComboBox<>();
        ObservableList<Integer> sensorIdList = fetchSensorIdsFromDatabase();
        sensorsSensorIdComboBox.setItems(sensorIdList);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);
        autoIncrementCheckBox.setOnAction(e -> {
            if (autoIncrementCheckBox.isSelected()) {
                dataIdText.setDisable(true);
                dataIdText.clear();
            } else {
                dataIdText.setDisable(false);
            }
        });

        HBox dataIdBox = new HBox(5, dataIdText, autoIncrementCheckBox);

        TableView<Data> tableView = new TableView<>();
        TableColumn<Data, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));
        TableColumn<Data, java.sql.Timestamp> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        TableColumn<Data, String> dataTypeCol = new TableColumn<>("Data Type");
        dataTypeCol.setCellValueFactory(new PropertyValueFactory<>("dataType"));
        TableColumn<Data, String> rawDataCol = new TableColumn<>("Raw Data");
        rawDataCol.setCellValueFactory(new PropertyValueFactory<>("rawData"));
        TableColumn<Data, String> processedDataCol = new TableColumn<>("Processed Data");
        processedDataCol.setCellValueFactory(new PropertyValueFactory<>("processedData"));
        TableColumn<Data, Integer> sensorsSensorIdCol = new TableColumn<>("Sensor ID");
        sensorsSensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorsSensorId"));

        tableView.getColumns().addAll(dataIdCol, timestampCol, dataTypeCol, rawDataCol, processedDataCol, sensorsSensorIdCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                int dataId;
                if (!autoIncrementCheckBox.isSelected()) {
                    String dataIdStr = dataIdText.getText().trim();
                    if (dataIdStr.isEmpty()) {
                        errorLabel.setText("Data ID is required!");
                        return;
                    }
                    try {
                        dataId = Integer.parseInt(dataIdStr);
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Data ID", "Data ID must be a valid number!");
                        return;
                    }
                } else {
                    dataId = getNextDataId();
                }

                java.sql.Timestamp timestamp = getCurrentTimestamp();
                String dataType = dataTypeText.getText();
                String rawData = rawDataText.getText();
                String processedData = processedDataText.getText();
                Integer sensorsSensorId = sensorsSensorIdComboBox.getValue();

                if (dataType.isEmpty() || sensorsSensorId == null) {
                    showAlert(Alert.AlertType.ERROR, "Missing Fields", "Required Fields", "Fields marked with * are required!");
                    return;
                }

                Data data = new Data(dataId, timestamp, dataType, rawData, processedData, sensorsSensorId);

                saveDataToDatabase(data);
                tableView.getItems().add(data);

                showAlert(Alert.AlertType.INFORMATION, "Data Created", "Success", "Data has been created successfully.");

                dataIdText.clear();
                dataTypeText.clear();
                rawDataText.clear();
                processedDataText.clear();
                sensorsSensorIdComboBox.setValue(null);
                errorLabel.setText("");
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Data", "Please enter valid data.");
            }
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Data selectedData = tableView.getSelectionModel().getSelectedItem();
            if (selectedData != null) {
                try {
                    int dataId;
                    if (!autoIncrementCheckBox.isSelected()) {
                        dataId = Integer.parseInt(dataIdText.getText());
                    } else {
                        dataId = selectedData.getDataId();
                    }

                    java.sql.Timestamp timestamp = getCurrentTimestamp();
                    String dataType = dataTypeText.getText();
                    String rawData = rawDataText.getText();
                    String processedData = processedDataText.getText();
                    Integer sensorsSensorId = sensorsSensorIdComboBox.getValue();

                    selectedData.setDataId(dataId);
                    selectedData.setTimestamp(timestamp);
                    selectedData.setDataType(dataType);
                    selectedData.setRawData(rawData);
                    selectedData.setProcessedData(processedData);
                    selectedData.setSensorsSensorId(sensorsSensorId);

                    updateDataInDatabase(selectedData);

                    tableView.refresh();

                    dataIdText.clear();
                    dataTypeText.clear();
                    rawDataText.clear();
                    processedDataText.clear();
                    sensorsSensorIdComboBox.setValue(null);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Data", "Please enter valid data.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Data Selected", "Please select a data to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Data selectedData = tableView.getSelectionModel().getSelectedItem();
            if (selectedData != null) {
                deleteDataFromDatabase(selectedData);

                tableView.getItems().remove(selectedData);
                showAlert(Alert.AlertType.INFORMATION, "Data Deleted", "Success", "Data has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Data Selected", "Please select a data to delete.");
            }
        });

        ObservableList<Data> dataList = fetchDataFromDatabase();
        tableView.setItems(dataList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                dataIdLabel, dataIdBox,
                dataTypeLabel, dataTypeText,
                rawDataLabel, rawDataText,
                processedDataLabel, processedDataText,
                sensorsSensorIdLabel, sensorsSensorIdComboBox,
                errorLabel,
                buttonBox, tableView
        );

        return vbox;
    }

    private static ObservableList<Integer> fetchSensorIdsFromDatabase() {
        ObservableList<Integer> sensorIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT SENSOR_ID FROM \"C4ISR PROJECT (BASIC) V2\".SENSORS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sensorIds.add(rs.getInt("SENSOR_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch sensor IDs from database.");
        }
        return sensorIds;
    }

    private static ObservableList<Data> fetchDataFromDatabase() {
        ObservableList<Data> dataList = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT * FROM \"C4ISR PROJECT (BASIC) V2\".DATA";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int dataId = rs.getInt("DATA_ID");
                java.sql.Timestamp timestamp = rs.getTimestamp("TIMESTAMP");
                String dataType = rs.getString("DATA_TYPE");
                String rawData = rs.getString("RAW_DATA");
                String processedData = rs.getString("PROCESSED_DATA");
                int sensorsSensorId = rs.getInt("SENSORS_SENSOR_ID");
                Data data = new Data(dataId, timestamp, dataType, rawData, processedData, sensorsSensorId);
                dataList.add(data);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch data from database.");
        }
        return dataList;
    }

    private static void saveDataToDatabase(Data data) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".DATA (DATA_ID, TIMESTAMP, DATA_TYPE, RAW_DATA, PROCESSED_DATA, SENSORS_SENSOR_ID) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, data.getDataId());
            pstmt.setTimestamp(2, data.getTimestamp());
            pstmt.setString(3, data.getDataType());
            pstmt.setString(4, data.getRawData());
            pstmt.setString(5, data.getProcessedData());
            pstmt.setInt(6, data.getSensorsSensorId());
            pstmt.executeUpdate();
            System.out.println("Data saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Save Operation Failed", "Failed to save data to database.");
        }
    }

    private static void updateDataInDatabase(Data data) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".DATA SET TIMESTAMP = ?, DATA_TYPE = ?, RAW_DATA = ?, PROCESSED_DATA = ?, SENSORS_SENSOR_ID = ? WHERE DATA_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setTimestamp(1, data.getTimestamp());
            pstmt.setString(2, data.getDataType());
            pstmt.setString(3, data.getRawData());
            pstmt.setString(4, data.getProcessedData());
            pstmt.setInt(5, data.getSensorsSensorId());
            pstmt.setInt(6, data.getDataId());
            pstmt.executeUpdate();
            System.out.println("Data updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Error", "Update Operation Failed", "Failed to update data in database.");
        }
    }

    private static void deleteDataFromDatabase(Data data) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".DATA WHERE DATA_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, data.getDataId());
            pstmt.executeUpdate();
            System.out.println("Data deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Delete Error", "Delete Operation Failed", "Failed to delete data from database.");
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static java.sql.Timestamp getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return java.sql.Timestamp.valueOf(now);
    }

    private static int getNextDataId() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".DATA_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ID Generation Error", "Failed to Generate ID", "Failed to generate next data ID.");
        }
        return -1;
    }
}
