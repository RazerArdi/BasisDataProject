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

public class Analysis {

    private int analysisId;
    private String analysisType;
    private String results;
    private String usersUserId;
    private int dataDataId;

    public Analysis(int analysisId, String analysisType, String results, String usersUserId, int dataDataId) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.results = results;
        this.usersUserId = usersUserId;
        this.dataDataId = dataDataId;
    }

    public int getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(int analysisId) {
        this.analysisId = analysisId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getUsersUserId() {
        return usersUserId;
    }

    public void setUsersUserId(String usersUserId) {
        this.usersUserId = usersUserId;
    }

    public int getDataDataId() {
        return dataDataId;
    }

    public void setDataDataId(int dataDataId) {
        this.dataDataId = dataDataId;
    }

    public static VBox getAnalysisUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Label analysisTypeLabel = new Label("Analysis Type:");
        TextField analysisTypeText = new TextField();
        Label resultsLabel = new Label("Results:");
        TextField resultsText = new TextField();
        Label usersUserIdLabel = new Label("Users User ID:");
        TextField usersUserIdText = new TextField();
        Label dataDataIdLabel = new Label("Data ID:");
        TextField dataDataIdText = new TextField();

        TableView<Analysis> tableView = new TableView<>();
        TableColumn<Analysis, Integer> analysisIdCol = new TableColumn<>("Analysis ID");
        analysisIdCol.setCellValueFactory(new PropertyValueFactory<>("analysisId"));
        TableColumn<Analysis, String> analysisTypeCol = new TableColumn<>("Analysis Type");
        analysisTypeCol.setCellValueFactory(new PropertyValueFactory<>("analysisType"));
        TableColumn<Analysis, String> resultsCol = new TableColumn<>("Results");
        resultsCol.setCellValueFactory(new PropertyValueFactory<>("results"));
        TableColumn<Analysis, String> usersUserIdCol = new TableColumn<>("Users User ID");
        usersUserIdCol.setCellValueFactory(new PropertyValueFactory<>("usersUserId"));
        TableColumn<Analysis, Integer> dataDataIdCol = new TableColumn<>("Data ID");
        dataDataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataDataId"));

        tableView.getColumns().addAll(analysisIdCol, analysisTypeCol, resultsCol, usersUserIdCol, dataDataIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int analysisId = Integer.parseInt(analysisIdText.getText());
            String analysisType = analysisTypeText.getText();
            String results = resultsText.getText();
            String usersUserId = usersUserIdText.getText();
            int dataDataId = Integer.parseInt(dataDataIdText.getText());

            Analysis analysis = new Analysis(analysisId, analysisType, results, usersUserId, dataDataId);
            System.out.println("Analysis Created: " + analysis.getAnalysisId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%d,%s,%s,%s,%d%n", analysisId, analysisType, results, usersUserId, dataDataId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new analysis to the table view
            tableView.getItems().add(analysis);

            // Clear input fields after adding analysis
            analysisIdText.clear();
            analysisTypeText.clear();
            resultsText.clear();
            usersUserIdText.clear();
            dataDataIdText.clear();
        });

        vbox.getChildren().addAll(
                analysisIdLabel, analysisIdText, analysisTypeLabel, analysisTypeText,
                resultsLabel, resultsText, usersUserIdLabel, usersUserIdText,
                dataDataIdLabel, dataDataIdText,
                tableView, createButton);

        return vbox;
    }
}
