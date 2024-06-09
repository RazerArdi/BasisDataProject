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
import java.util.List;
import java.util.stream.Collectors;

public class Analysis {

    private int analysisId;
    private String analysisType;
    private String results;

    public Analysis(int analysisId, String analysisType, String results) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.results = results;
    }

    public int getAnalysisId() {
        return analysisId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getResults() {
        return results;
    }

    public static VBox getAnalysisUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<Analysis> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Set resize policy
        ObservableList<Analysis> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Analysis, Integer> analysisIdCol = new TableColumn<>("Analysis ID");
        analysisIdCol.setCellValueFactory(new PropertyValueFactory<>("analysisId"));
        analysisIdCol.setMinWidth(100); // Set minimum width for Analysis ID column

        TableColumn<Analysis, String> analysisTypeCol = new TableColumn<>("Analysis Type");
        analysisTypeCol.setCellValueFactory(new PropertyValueFactory<>("analysisType"));
        analysisTypeCol.setMinWidth(150); // Set minimum width for Analysis Type column

        TableColumn<Analysis, String> resultsCol = new TableColumn<>("Results");
        resultsCol.setCellValueFactory(new PropertyValueFactory<>("results"));
        resultsCol.setMinWidth(250); // Set minimum width for Results column

        tableView.getColumns().addAll(analysisIdCol, analysisTypeCol, resultsCol);
        tableView.setItems(data);

        Button deleteButton = new Button("Delete Selected Data");
        deleteButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                data.remove(selectedAnalysis);
                deleteFromDatabase(selectedAnalysis);
            }
        });

        Button refreshButton = new Button("Refresh Table");
        refreshButton.setOnAction(e -> {
            data.clear(); // Clear existing data
            loadData(data); // Load fresh data to the table view
        });

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Label analysisTypeLabel = new Label("Analysis Type:");
        ChoiceBox<String> analysisTypeChoice = new ChoiceBox<>();
        analysisTypeChoice.getItems().addAll("Diagnostic", "Predictive", "Prescriptive");
        Label resultsLabel = new Label("Results:");
        TextArea resultsText = new TextArea();

        Button insertButton = new Button("Insert Data");
        insertButton.setOnAction(e -> {
            int analysisId = Integer.parseInt(analysisIdText.getText());
            String analysisType = analysisTypeChoice.getValue();
            String results = resultsText.getText();

            Analysis analysis = new Analysis(analysisId, analysisType, results);
            data.add(analysis);
            saveToDatabase(analysis);
            clearForm(analysisIdText, analysisTypeChoice, resultsText);
        });

        formLayout.getChildren().addAll(analysisIdLabel, analysisIdText, analysisTypeLabel, analysisTypeChoice, resultsLabel, resultsText, insertButton);

        HBox buttonLayout = new HBox(20, deleteButton, refreshButton);
        buttonLayout.setAlignment(Pos.CENTER_RIGHT);

        mainLayout.getChildren().addAll(tableView, buttonLayout, formLayout);

        // Load initial data to the table view
        loadData(data);

        return mainLayout;
    }


    private static void saveToDatabase(Analysis analysis) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Analysis,%d,%s,%s%n", analysis.getAnalysisId(), analysis.getAnalysisType(), analysis.getResults()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteFromDatabase(Analysis analysis) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("Database.txt"));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.equals(String.format("Analysis,%d,%s,%s", analysis.getAnalysisId(), analysis.getAnalysisType(), analysis.getResults())))
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

    private static void clearForm(TextField analysisIdText, ChoiceBox<String> analysisTypeChoice, TextArea resultsText) {
        analysisIdText.clear();
        analysisTypeChoice.getSelectionModel().clearSelection();
        resultsText.clear();
    }

    private static void loadData(ObservableList<Analysis> data) {
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("Database.txt"));

            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        int analysisId = Integer.parseInt(parts[1].trim());
                        String analysisType = parts[2].trim();
                        String results = parts[3].trim();
                        data.add(new Analysis(analysisId, analysisType, results));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid data format in line: " + line);
                    }
                } else {
                    System.out.println("Invalid data format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading data from Database.txt.");
        }
    }

    public static void main(String[] args) {
        // Create a JavaFX application to display the UI
        Application.launch(AnalysisApp.class, args);
    }

    public static class AnalysisApp extends Application {
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Analysis Management");
            primaryStage.setScene(new Scene(getAnalysisUI(), 800, 600));
            primaryStage.show();
        }
    }
}
