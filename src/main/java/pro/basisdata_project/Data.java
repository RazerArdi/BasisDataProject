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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Data {

    private int dataId;
    private String timestamp;
    private String dataType;
    private String rawData;
    private String processedData;
    private int sensorsSensorId;

    public Data(int dataId, String timestamp, String dataType, String rawData, String processedData, int sensorsSensorId) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

        Label dataIdLabel = new Label("Data ID:");
        TextField dataIdText = new TextField();
        Label timestampLabel = new Label("Timestamp:");
        TextField timestampText = new TextField();
        Label dataTypeLabel = new Label("Data Type:");
        TextField dataTypeText = new TextField();
        Label rawDataLabel = new Label("Raw Data:");
        TextArea rawDataText = new TextArea();
        Label processedDataLabel = new Label("Processed Data:");
        TextArea processedDataText = new TextArea();
        Label sensorsSensorIdLabel = new Label("Sensor ID:");
        TextField sensorsSensorIdText = new TextField();

        TableView<Data> tableView = new TableView<>();
        TableColumn<Data, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));
        TableColumn<Data, String> timestampCol = new TableColumn<>("Timestamp");
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

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int dataId = Integer.parseInt(dataIdText.getText());
            String timestamp = timestampText.getText();
            String dataType = dataTypeText.getText();
            String rawData = rawDataText.getText();
            String processedData = processedDataText.getText();
            int sensorsSensorId = Integer.parseInt(sensorsSensorIdText.getText());

            Data data = new Data(dataId, timestamp, dataType, rawData, processedData, sensorsSensorId);
            System.out.println("Data Created: " + data.getDataId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%d,%s,%s,%s,%s,%d%n", dataId, timestamp, dataType, rawData, processedData, sensorsSensorId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new data to the table view
            tableView.getItems().add(data);

            // Clear input fields after adding data
            dataIdText.clear();
            timestampText.clear();
            dataTypeText.clear();
            rawDataText.clear();
            processedDataText.clear();
            sensorsSensorIdText.clear();
        });

        vbox.getChildren().addAll(
                dataIdLabel, dataIdText, timestampLabel, timestampText,
                dataTypeLabel, dataTypeText, rawDataLabel, rawDataText,
                processedDataLabel, processedDataText,
                sensorsSensorIdLabel, sensorsSensorIdText,
                tableView, createButton);

        return vbox;
    }
}
