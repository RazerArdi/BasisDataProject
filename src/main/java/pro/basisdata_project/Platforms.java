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
import java.util.stream.Collectors;

public class Platforms {

    private int platformId;
    private String name;
    private String type;
    private String status;

    public Platforms(int platformId, String name, String type, String status) {
        this.platformId = platformId;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
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

    public static VBox getPlatformsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            if (result.isPresent()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Data Found");
                alert.setHeaderText(null);
                alert.setContentText("Data Found for Analysis ID: " + result.get());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Data Not Found");
                alert.setHeaderText(null);
                alert.setContentText("No Data Found for the specified Analysis ID.");
                alert.showAndWait();
            }
        });

        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("active", "disabled", "passive");
        statusComboBox.setValue("active"); // Default value

        TableView<Platforms> tableView = new TableView<>();
        TableColumn<Platforms, Integer> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));
        TableColumn<Platforms, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Platforms, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Platforms, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        tableView.getColumns().addAll(platformIdCol, nameCol, typeCol, statusCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusComboBox.getValue();

            Platforms platform = new Platforms(platformId, name, type, status);
            System.out.println("Platform Created: " + platform.getPlatformId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Platform,%d,%s,%s,%s%n", analysisId, platformId, name, type, status));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button refreshButton = new Button("Refresh Table");
        refreshButton.setOnAction(e -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(getSamplePlatformsData());
        });

        vbox.getChildren().addAll(
                analysisIdLabel, analysisIdText, searchAnalysisIdButton,
                platformIdLabel, platformIdText, nameLabel, nameText,
                typeLabel, typeText, statusLabel, statusComboBox,
                tableView, createButton, refreshButton
        );

        return vbox;
    }

    private static ObservableList<Platforms> getSamplePlatformsData() {
        ObservableList<Platforms> data = FXCollections.observableArrayList();
        data.add(new Platforms(1, "Platform 1", "Type 1", "active"));
        data.add(new Platforms(2, "Platform 2", "Type 2", "disabled"));
        data.add(new Platforms(3, "Platform 3", "Type 3", "passive"));
        return data;
    }

    private static void deleteFromDatabase(Platforms platform) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Platform,%d,%s,%s,%s",
                            platform.getPlatformId(), platform.getName(), platform.getType(), platform.getStatus())))
                    .collect(Collectors.toList());
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
}