package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;

public class Data {

    private int dataId;
    private String timestamp;
    private String dataType;
    private String rawData;
    private String processedData;
    private int sensorId;

    // Constructor
    public Data(int dataId, String timestamp, String dataType, String rawData, String processedData, int sensorId) {
        this.dataId = dataId;
        this.timestamp = timestamp;
        this.dataType = dataType;
        this.rawData = rawData;
        this.processedData = processedData;
        this.sensorId = sensorId;
    }

    // Getters and Setters
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

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    // Method to display the Data GUI
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
        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int dataId = Integer.parseInt(dataIdText.getText());
            String timestamp = timestampText.getText();
            String dataType = dataTypeText.getText();
            String rawData = rawDataText.getText();
            String processedData = processedDataText.getText();
            int sensorId = Integer.parseInt(sensorIdText.getText());

            Data data = new Data(dataId, timestamp, dataType, rawData, processedData, sensorId);
            System.out.println("Data Created: " + data.getDataId());
        });

        vbox.getChildren().addAll(dataIdLabel, dataIdText, timestampLabel, timestampText, dataTypeLabel, dataTypeText, rawDataLabel, rawDataText, processedDataLabel, processedDataText, sensorIdLabel, sensorIdText, createButton);

        return vbox;
    }
}
