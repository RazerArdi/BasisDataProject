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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Equipments {

    private int equipmentId;
    private String name;
    private String type;
    private String status;
    private int platformId;

    public Equipments(int equipmentId, String name, String type, String status, int platformId) {
        this.equipmentId = equipmentId;
        this.name = name;
        this.type = type;
        this.status = status;
        this.platformId = platformId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getPlatformId() {
        return platformId;
    }

    public static VBox getEquipmentUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Button searchAnalysisIdButton = new Button("Search Analysis ID");

        searchAnalysisIdButton.setOnAction(e -> {
            Optional<String> result = showSearchDialog();
            result.ifPresent(analysisIdText::setText);
        });

        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        Label nameLabel = new Label("Name:");
        TextField nameText = new TextField();
        Label typeLabel = new Label("Type:");
        TextField typeText = new TextField();
        Label statusLabel = new Label("Status:");
        TextField statusText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        TableView<Equipments> tableView = new TableView<>();
        ObservableList<Equipments> data = FXCollections.observableArrayList();

        TableColumn<Equipments, Integer> equipmentIdCol = new TableColumn<>("Equipment ID");
        equipmentIdCol.setCellValueFactory(new PropertyValueFactory<>("equipmentId"));
        TableColumn<Equipments, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Equipments, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Equipments, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<Equipments, Integer> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));

        tableView.getColumns().addAll(equipmentIdCol, nameCol, typeCol, statusCol, platformIdCol);
        tableView.setItems(data);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int equipmentId = Integer.parseInt(equipmentIdText.getText());
            String name = nameText.getText();
            String type = typeText.getText();
            String status = statusText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());

            Equipments equipment = new Equipments(equipmentId, name, type, status, platformId);
            data.add(equipment);

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Equipment,%d,%s,%s,%s,%d%n", analysisId, equipmentId, name, type, status, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteButton = new Button("Delete Selected Equipment");
        deleteButton.setOnAction(e -> {
            Equipments selectedEquipment = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                data.remove(selectedEquipment);
                deleteFromDatabase(selectedEquipment);
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, equipmentIdLabel, equipmentIdText, nameLabel, nameText, typeLabel, typeText, statusLabel, statusText, platformIdLabel, platformIdText, createButton, deleteButton, tableView);

        return vbox;
    }

    private static void deleteFromDatabase(Equipments equipment) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Equipment,%d,%s,%s,%s,%d",
                            equipment.getEquipmentId(), equipment.getName(), equipment.getType(), equipment.getStatus(), equipment.getPlatformId())))
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

