package pro.basisdata_project;

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

        // Define table columns
        TableView<Analysis> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Add this line
        ObservableList<Analysis> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Analysis, Integer> analysisIdCol = new TableColumn<>("Analysis ID");
        analysisIdCol.setCellValueFactory(new PropertyValueFactory<>("analysisId"));

        TableColumn<Analysis, String> analysisTypeCol = new TableColumn<>("Analysis Type");
        analysisTypeCol.setCellValueFactory(new PropertyValueFactory<>("analysisType"));

        TableColumn<Analysis, String> resultsCol = new TableColumn<>("Results");
        resultsCol.setCellValueFactory(new PropertyValueFactory<>("results"));

        resultsCol.setMinWidth(500); // Set minimum width
        resultsCol.setMaxWidth(800); // Set maximum width

        tableView.getColumns().addAll(analysisIdCol, analysisTypeCol, resultsCol);
        tableView.setItems(data);


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

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().add(contentLayout);

        return mainLayout;
    }

    private static void saveToDatabase(Analysis analysis) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Analysis,%d,%s,%s%n", analysis.getAnalysisId(), analysis.getAnalysisType(), analysis.getResults()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearForm(TextField analysisIdText, ChoiceBox<String> analysisTypeChoice, TextArea resultsText) {
        analysisIdText.clear();
        analysisTypeChoice.getSelectionModel().clearSelection();
        resultsText.clear();
    }

    public static void main(String[] args) {
        // Create a JavaFX application to display the UI
        javafx.application.Application.launch(AnalysisApp.class, args);
    }

    public static class AnalysisApp extends javafx.application.Application {
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Analysis Management");
            primaryStage.setScene(new Scene(getAnalysisUI(), 800, 600));
            primaryStage.show();
        }
    }
}
