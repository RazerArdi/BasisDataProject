package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        TableColumn<Analysis, String> usersUserIdCol = new TableColumn<>("User ID");
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

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".ANALYSIS (ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, USERS_USER_ID, DATA_DATA_ID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, analysisId);
                pstmt.setString(2, analysisType);
                pstmt.setString(3, results);
                pstmt.setString(4, usersUserId);
                pstmt.setInt(5, dataDataId);
                pstmt.executeUpdate();
                System.out.println("Analysis saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(analysis);

            analysisIdText.clear();
            analysisTypeText.clear();
            resultsText.clear();
            usersUserIdText.clear();
            dataDataIdText.clear();
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                int analysisId = Integer.parseInt(analysisIdText.getText());
                String analysisType = analysisTypeText.getText();
                String results = resultsText.getText();
                String usersUserId = usersUserIdText.getText();
                int dataDataId = Integer.parseInt(dataDataIdText.getText());

                selectedAnalysis.setAnalysisId(analysisId);
                selectedAnalysis.setAnalysisType(analysisType);
                selectedAnalysis.setResults(results);
                selectedAnalysis.setUsersUserId(usersUserId);
                selectedAnalysis.setDataDataId(dataDataId);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".ANALYSIS SET ANALYSIS_TYPE = ?, RESULTS = ?, USERS_USER_ID = ?, DATA_DATA_ID = ? WHERE ANALYSIS_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, analysisType);
                    pstmt.setString(2, results);
                    pstmt.setString(3, usersUserId);
                    pstmt.setInt(4, dataDataId);
                    pstmt.setInt(5, analysisId);
                    pstmt.executeUpdate();
                    System.out.println("Analysis updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                analysisIdText.clear();
                analysisTypeText.clear();
                resultsText.clear();
                usersUserIdText.clear();
                dataDataIdText.clear();
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                int analysisId = selectedAnalysis.getAnalysisId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS WHERE ANALYSIS_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, analysisId);
                    pstmt.executeUpdate();
                    System.out.println("Analysis deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedAnalysis);
            }
        });

        ObservableList<Analysis> analysisList = fetchAnalysisFromDatabase();
        tableView.setItems(analysisList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                analysisIdLabel, analysisIdText, analysisTypeLabel, analysisTypeText,
                resultsLabel, resultsText, usersUserIdLabel, usersUserIdText,
                dataDataIdLabel, dataDataIdText,
                tableView, buttonBox);

        return vbox;
    }

    private static ObservableList<Analysis> fetchAnalysisFromDatabase() {
        ObservableList<Analysis> analysisList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, USERS_USER_ID, DATA_DATA_ID FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int analysisId = rs.getInt("ANALYSIS_ID");
                String analysisType = rs.getString("ANALYSIS_TYPE");
                String results = rs.getString("RESULTS");
                String usersUserId = rs.getString("USERS_USER_ID");
                int dataDataId = rs.getInt("DATA_DATA_ID");

                Analysis analysis = new Analysis(analysisId, analysisType, results, usersUserId, dataDataId);
                analysisList.add(analysis);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return analysisList;
    }
}
