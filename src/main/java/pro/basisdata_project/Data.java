package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Data {
    private static TableView<Data> tableviewDATA = new TableView<Data>();
    private int dataId;
    private String type;
    private String description;
    private String location;
    private String format;
    private int platformId;

    public Data(int dataId, String type, String description, String location, String format, int platformId) {
        this.dataId = dataId;
        this.type = type;
        this.description = description;
        this.location = location;
        this.format = format;
        this.platformId = platformId;
    }

    public int getDataId() {
        return dataId;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getFormat() {
        return format;
    }

    public int getPlatformId() {
        return platformId;
    }

    public static VBox getDataUI() {
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

        TableView<Data> tableView = new TableView<>();
        ObservableList<Data> data = FXCollections.observableArrayList();

        TableColumn<Data, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));
        TableColumn<Data, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Data, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Data, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TableColumn<Data, String> formatCol = new TableColumn<>("Format");
        formatCol.setCellValueFactory(new PropertyValueFactory<>("format"));
        TableColumn<Data, Integer> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));

        tableView.getColumns().addAll(dataIdCol, typeCol, descriptionCol, locationCol, formatCol, platformIdCol);
        tableView.setItems(data);

        Label dataIdLabel = new Label("Data ID:");
        TextField dataIdText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionText = new TextField();
        Label locationLabel = new Label("Location:");
        TextField locationText = new TextField();
        Label formatLabel = new Label("Format:");
        TextField formatText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int dataId = Integer.parseInt(dataIdText.getText());
            String type = typeText.getText();
            String description = descriptionText.getText();
            String location = locationText.getText();
            String format = formatText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());

            Data newData = new Data(dataId, type, description, location, format, platformId);
            data.add(newData);

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Data,%d,%s,%s,%s,%s,%d%n", analysisId, dataId, type, description, location, format, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteButton = new Button("Delete Selected Data");
        deleteButton.setOnAction(e -> {
            Data selectedData = tableView.getSelectionModel().getSelectedItem();
            if (selectedData != null) {
                data.remove(selectedData);
                deleteFromDatabase(selectedData);
            }
        });

        Button refreshButton = new Button("Refresh Data");
        refreshButton.setOnAction(e -> loadData());

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, dataIdLabel, dataIdText, typeLabel, typeText, descriptionLabel, descriptionText, locationLabel, locationText, formatLabel, formatText, platformIdLabel, platformIdText, createButton, deleteButton, refreshButton, tableView);

        return vbox;
    }

    private static void deleteFromDatabase(Data data) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Data,%d,%s,%s,%s,%s,%d",
                            data.getDataId(), data.getType(), data.getDescription(), data.getLocation(), data.getFormat(), data.getPlatformId())))                    .collect(Collectors.toList());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt"))) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }

    private static boolean isDataFound(String analysisId) {
        // Implement logic to check if data with the given analysisId exists in the database
        return true; // For demonstration purposes, always return true
    }

    private static void displayDataFound() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Data Found");
        alert.setHeaderText(null);
        alert.setContentText("Data Found!");

        alert.showAndWait();
    }

    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Database.txt"))) {
            ObservableList<Data> dataList = FXCollections.observableArrayList();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 8 && parts[2].equals("Data")) {
                    try {
                        int dataId = Integer.parseInt(parts[3].trim());
                        String type = parts[4].trim();
                        String description = parts[5].trim();
                        String location = parts[6].trim();
                        String format = parts[7].trim();
                        int platformId = Integer.parseInt(parts[8].trim());
                        dataList.add(new Data(dataId, type, description, location, format, platformId));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in line: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in line: " + line);
                }
            }
            tableviewDATA.setItems(dataList);
        } catch (IOException e) {
            System.out.println("Error loading data from Database.txt.");
        }
    }
}

