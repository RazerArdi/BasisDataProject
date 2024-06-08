package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class CommLog {

    private int logId;
    private String description;
    private String date;
    private String userId;
    private int platformId;

    public CommLog(int logId, String description, String date, String userId, int platformId) {
        this.logId = logId;
        this.description = description;
        this.date = date;
        this.userId = userId;
        this.platformId = platformId;
    }

    public int getLogId() {
        return logId;
    }

    public static VBox getCommLogUI() {
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
        TextField descriptionText = new TextField();
        Label dateLabel = new Label("Date:");
        TextField dateText = new TextField();
        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        TableView<CommLog> tableView = new TableView<>();
        TableColumn<CommLog, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        TableColumn<CommLog, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<CommLog, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<CommLog, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        TableColumn<CommLog, Integer> platformIdCol = new TableColumn<>("Platform ID");
        platformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformId"));

        tableView.getColumns().addAll(logIdCol, descriptionCol, dateCol, userIdCol, platformIdCol);

        vbox.getChildren().add(tableView);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            int logId = Integer.parseInt(logIdText.getText());
            String description = descriptionText.getText();
            String date = dateText.getText();
            String userId = userIdText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());

            CommLog commLog = new CommLog(logId, description, date, userId, platformId);
            System.out.println("CommLog Created: " + commLog.getLogId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Analysis ID,%s,CommLog,%d,%s,%s,%s,%d%n", analysisId, logId, description, date, userId, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, searchAnalysisIdButton, logIdLabel, logIdText, descriptionLabel, descriptionText, dateLabel, dateText, userIdLabel, userIdText, platformIdLabel, platformIdText, createButton);

        return vbox;
    }

    private static Optional<String> showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Analysis ID");
        dialog.setHeaderText("Enter Analysis ID to search:");
        dialog.setContentText("Analysis ID:");

        return dialog.showAndWait();
    }
}
