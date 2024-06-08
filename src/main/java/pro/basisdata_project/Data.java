package pro.basisdata_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Data extends Application {

    private int dataId;
    private String dataType;
    private String content;
    private int sensorId;

    public Data(int dataId, String dataType, String content, int sensorId) {
        this.dataId = dataId;
        this.dataType = dataType;
        this.content = content;
        this.sensorId = sensorId;
    }

    public int getDataId() {
        return dataId;
    }

    public String getDataType() {
        return dataType;
    }

    public String getContent() {
        return content;
    }

    public int getSensorId() {
        return sensorId;
    }

    public static VBox getDataUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<Data> tableView = new TableView<>();
        ObservableList<Data> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Data, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));

        TableColumn<Data, String> dataTypeCol = new TableColumn<>("Data Type");
        dataTypeCol.setCellValueFactory(new PropertyValueFactory<>("dataType"));

        TableColumn<Data, String> contentCol = new TableColumn<>("Content");
        contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

        TableColumn<Data, Integer> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));

        tableView.getColumns().addAll(dataIdCol, dataTypeCol, contentCol, sensorIdCol);
        tableView.setItems(data);

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label dataIdLabel = new Label("Data ID:");
        TextField dataIdText = new TextField();
        Label dataTypeLabel = new Label("Data Type:");
        TextField dataTypeText = new TextField();
        Label contentLabel = new Label("Content:");
        TextArea contentText = new TextArea();
        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();

        Button createButton = new Button("Insert Data");
        createButton.setOnAction(e -> {
            int dataId = Integer.parseInt(dataIdText.getText());
            String dataType = dataTypeText.getText();
            String content = contentText.getText();
            int sensorId = Integer.parseInt(sensorIdText.getText());

            Data dataEntry = new Data(dataId, dataType, content, sensorId);
            data.add(dataEntry);
            saveToDatabase(dataEntry);
            clearForm(dataIdText, dataTypeText, contentText, sensorIdText);
        });

        formLayout.getChildren().addAll(dataIdLabel, dataIdText, dataTypeLabel, dataTypeText, contentLabel, contentText, sensorIdLabel, sensorIdText, createButton);

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().add(contentLayout);

        return mainLayout;
    }

    private static void saveToDatabase(Data data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Data,%d,%s,%s,%d%n", data.getDataId(), data.getDataType(), data.getContent(), data.getSensorId()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearForm(TextField dataIdText, TextField dataTypeText, TextArea contentText, TextField sensorIdText) {
        dataIdText.clear();
        dataTypeText.clear();
        contentText.clear();
        sensorIdText.clear();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Data Management");
        primaryStage.setScene(new Scene(getDataUI(), 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
