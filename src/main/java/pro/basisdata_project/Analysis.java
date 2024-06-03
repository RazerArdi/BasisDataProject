package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
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
        TextArea resultsText = new TextArea();
        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label dataIdLabel = new Label("Data ID:");
        TextField dataIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int analysisId = Integer.parseInt(analysisIdText.getText());
            String analysisType = analysisTypeText.getText();
            String results = resultsText.getText();
            String userId = userIdText.getText();
            int dataId = Integer.parseInt(dataIdText.getText());

            Analysis analysis = new Analysis(analysisId, analysisType, results, userId, dataId);
            System.out.println("Analysis Created: " + analysis.getAnalysisId());
        });

        vbox.getChildren().addAll(analysisIdLabel, analysisIdText, analysisTypeLabel, analysisTypeText, resultsLabel, resultsText, userIdLabel, userIdText, dataIdLabel, dataIdText, createButton);

        return vbox;
    }
}
