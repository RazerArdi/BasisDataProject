package pro.basisdata_project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.Optional;

import static pro.basisdata_project.Users.displayDataFound;
import static pro.basisdata_project.Users.isDataFound;

public class Sensors {
    private static TableView<Sensors> sensorTableView = new TableView<>();
    private int sensorId;
    private String name;
    private String type;
    private String status;

    public Sensors(int sensorId, String name, String type, String status) {
        this.sensorId = sensorId;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
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

    public static VBox getSensorsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            if (result.isPresent()) {
                String analysisId = result.get();
                if (isDataFound(analysisId)) {
                    displayDataFound();
                } else {
                    analysisIdText.setText(analysisId);
                }
            }
        });

        Label sensorIdLabel = new Label("Sensor ID:");
        TextField sensorIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        ChoiceBox<String> statusChoice = new ChoiceBox<>();
        statusChoice.getItems().addAll("Active", "Inactive", "Passive");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int sensorId = Integer.parseInt(sensorIdText.getText());
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusChoice.getValue();

            Sensors sensor = new Sensors(sensorId, name, type, status);
            System.out.println("Sensor Created: " + sensor.getSensorId());

            // Tambahkan data ke dalam ObservableList
            sensorTableView.getItems().add(sensor);

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Sensors,%d,%s,%s,%s%n", analysisIdText.getText(), sensorId, name, type, status));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        TableView<Sensors> sensorTableView = new TableView<>();
        TableColumn<Sensors, Integer> sensorIdCol = new TableColumn<>("Sensor ID");
        sensorIdCol.setCellValueFactory(new PropertyValueFactory<>("sensorId"));
        TableColumn<Sensors, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Sensors, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Sensors, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        sensorTableView.getColumns().addAll(sensorIdCol, nameCol, typeCol, statusCol);

        Button deleteSelectedButton = new Button("Delete Selected");
        deleteSelectedButton.setOnAction(e -> {
            Sensors selectedSensor = sensorTableView.getSelectionModel().getSelectedItem();
            if (selectedSensor != null) {
                int sensorIdToDelete = selectedSensor.getSensorId();
                // Code untuk menghapus sensor dari database atau daftar
                System.out.println("Deleted Sensor ID: " + sensorIdToDelete);
                sensorTableView.getItems().remove(selectedSensor); // Hapus dari tabel
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText("No Sensor Selected");
                alert.setContentText("Please select a sensor to delete.");
                alert.showAndWait();
            }
        });

        Button refreshTableButton = new Button("Refresh Table");
        refreshTableButton.setOnAction(e -> {
            loadDatabaseData(sensorTableView);
        });

        vbox.getChildren().addAll(
                analysisIdLabel, analysisIdText, searchAnalysisIdButton,
                sensorIdLabel, sensorIdText, nameLabel, nameText,
                typeLabel, typeText, statusLabel, statusChoice, createButton,
                sensorTableView, deleteSelectedButton, refreshTableButton);

        loadDatabaseData(sensorTableView); // Load initial data

        return vbox;
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }

    private static void loadDatabaseData(TableView<Sensors> sensorTableView) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Database.txt"))) {
            ObservableList<Sensors> sensorList = FXCollections.observableArrayList();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7 && parts[2].equals("Sensors")) { // Ubah panjang array menjadi 7
                    try {
                        int sensorId = Integer.parseInt(parts[3].trim());
                        String name = parts[4].trim();
                        String type = parts[5].trim();
                        String status = parts[6].trim(); // Ubah indeks menjadi 5
                        sensorList.add(new Sensors(sensorId, name, type, status));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in line: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in line: " + line);
                }
            }
            sensorTableView.setItems(sensorList);
        } catch (IOException e) {
            System.out.println("Error loading data from Database.txt.");
        }
    }


        public static void main(String[] args) {
        Application.launch(SensorsApp.class, args);
    }

    public static class SensorsApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Sensors Management");
            primaryStage.setScene(new Scene(getSensorsUI(), 800, 600));
            primaryStage.show();
        }
    }
}
