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

public class Maintenancelogs {

    private int logId;
    private String description;
    private String date;
    private String userId;
    private int platformId;

    public Maintenancelogs(int logId, String description, String date, String userId, int platformId) {
        this.logId = logId;
        this.description = description;
        this.date = date;
        this.userId = userId;
        this.platformId = platformId;
    }

    public int getLogId() {
        return logId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public static VBox getMaintenancelogsUI() {
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

        Label logIdLabel = new Label("Log ID:");
        TextField logIdText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionText = new TextArea();
        Label dateLabel = new Label("Date:");
        TextField dateText = new TextField();
        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        TableView<Maintenancelogs> tableView = new TableView<>();
        ObservableList<Maintenancelogs> data = FXCollections.observableArrayList();

        TableColumn<Maintenancelogs, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        TableColumn<Maintenancelogs, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Maintenancelogs, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Maintenancelogs, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<Maintenancelogs, Integer> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));

        tableView.getColumns().addAll(logIdCol, descriptionCol, dateCol, userIdCol, platformIdCol);
        tableView.setItems(data);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int logId = Integer.parseInt(logIdText.getText());
            String description = descriptionText.getText();
            String date = dateText.getText();
            String userId = userIdText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());

            Maintenancelogs maintenancelog = new Maintenancelogs(logId, description, date, userId, platformId);
            data.add(maintenancelog);

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,Maintenancelogs,%d,%s,%s,%s,%d%n", analysisId, logId, description, date, userId, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Button deleteButton = new Button("Delete Selected Log");
        deleteButton.setOnAction(e -> {
            Maintenancelogs selectedLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedLog != null) {
                data.remove(selectedLog);
                deleteFromDatabase(selectedLog);
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, logIdLabel, logIdText, descriptionLabel, descriptionText, dateLabel, dateText, userIdLabel, userIdText, platformIdLabel, platformIdText, createButton, deleteButton, tableView);

        return vbox;
    }

    private static void deleteFromDatabase(Maintenancelogs log) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis ID,%s,Maintenancelogs,%d,%s,%s,%s,%d",
                            log.getLogId(), log.getDescription(), log.getDate(), log.getUserId(), log.getPlatformId())))
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
