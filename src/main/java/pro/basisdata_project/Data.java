package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class Data {
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
        vbox.getChildren().add(tableView);

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

            Data data = new Data(dataId, type, description, location, format, platformId);
            System.out.println("Data Created: " + data.getDataId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Data,%d,%s,%s,%s,%s,%d%n", analysisId, dataId, type, description, location, format, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, dataIdLabel, dataIdText, typeLabel, typeText, descriptionLabel, descriptionText, locationLabel, locationText, formatLabel, formatText, platformIdLabel, platformIdText, createButton);

        return vbox;
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
}
