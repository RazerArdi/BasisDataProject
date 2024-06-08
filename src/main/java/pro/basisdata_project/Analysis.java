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
    private String userId;
    private int dataId;

    public Analysis(int analysisId, String analysisType, String results, String userId, int dataId) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.results = results;
        this.userId = userId;
        this.dataId = dataId;
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

    public String getUserId() {
        return userId;
    }

    public int getDataId() {
        return dataId;
    }

    public static VBox getAnalysisUI() {
        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10));
        mainLayout.setSpacing(10);

        TableView<Analysis> tableView = new TableView<>();
        ObservableList<Analysis> data = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Analysis, Integer> analysisIdCol = new TableColumn<>("Analysis ID");
        analysisIdCol.setCellValueFactory(new PropertyValueFactory<>("analysisId"));

        TableColumn<Analysis, String> analysisTypeCol = new TableColumn<>("Analysis Type");
        analysisTypeCol.setCellValueFactory(new PropertyValueFactory<>("analysisType"));

        TableColumn<Analysis, String> resultsCol = new TableColumn<>("Results");
        resultsCol.setCellValueFactory(new PropertyValueFactory<>("results"));

        TableColumn<Analysis, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<Analysis, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));

        tableView.getColumns().addAll(analysisIdCol, analysisTypeCol, resultsCol, userIdCol, dataIdCol);
        tableView.setItems(data);

        // Form for input
        VBox formLayout = new VBox();
        formLayout.setPadding(new Insets(10));
        formLayout.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID:");
        TextField analysisIdText = new TextField();
        Label analysisTypeLabel = new Label("Analysis Type:");
        TextField analysisTypeText = new TextField();
        Label resultsLabel = new Label("Results:");
        TextArea resultsText = new TextArea();
        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label dataIdLabel = new Label("Data ID:");
        TextField dataIdText = new TextField();

        Button insertButton = new Button("Insert Data");
        insertButton.setOnAction(e -> {
            int analysisId = Integer.parseInt(analysisIdText.getText());
            String analysisType = analysisTypeText.getText();
            String results = resultsText.getText();
            String userId = userIdText.getText();
            int dataId = Integer.parseInt(dataIdText.getText());

            Analysis analysis = new Analysis(analysisId, analysisType, results, userId, dataId);
            data.add(analysis);
            saveToDatabase(analysis);
            clearForm(analysisIdText, analysisTypeText, resultsText, userIdText, dataIdText);
        });

        formLayout.getChildren().addAll(analysisIdLabel, analysisIdText, analysisTypeLabel, analysisTypeText, resultsLabel, resultsText, userIdLabel, userIdText, dataIdLabel, dataIdText, insertButton);

        HBox contentLayout = new HBox(20, tableView, formLayout);
        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.setPadding(new Insets(20));

        mainLayout.getChildren().add(contentLayout);

        return mainLayout;
    }

    private static void saveToDatabase(Analysis analysis) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
            writer.write(String.format("Analysis,%d,%s,%s,%s,%d%n", analysis.getAnalysisId(), analysis.getAnalysisType(), analysis.getResults(), analysis.getUserId(), analysis.getDataId()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearForm(TextField analysisIdText, TextField analysisTypeText, TextArea resultsText, TextField userIdText, TextField dataIdText) {
        analysisIdText.clear();
        analysisTypeText.clear();
        resultsText.clear();
        userIdText.clear();
        dataIdText.clear();
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
