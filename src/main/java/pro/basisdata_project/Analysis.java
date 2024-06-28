package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pro.basisdata_project.OracleAPEXConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Analysis {
    private String analysisId;
    private String analysisType;
    private String results;
    private String usersUserId;
    private int dataDataId;

    public Analysis(String analysisId, String analysisType, String results, String usersUserId, int dataDataId) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.results = results;
        this.usersUserId = usersUserId;
        this.dataDataId = dataDataId;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getResults() {
        return results;
    }

    public String getUsersUserId() {
        return usersUserId;
    }

    public int getDataDataId() {
        return dataDataId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public void setUsersUserId(String usersUserId) {
        this.usersUserId = usersUserId;
    }

    public void setDataDataId(int dataDataId) {
        this.dataDataId = dataDataId;
    }

    public static VBox getAnalysisUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label analysisIdLabel = new Label("Analysis ID *:");
        TextField analysisIdText = new TextField();
        Label analysisTypeLabel = new Label("Analysis Type:");
        TextField analysisTypeText = new TextField();
        Label resultsLabel = new Label("Results:");
        TextField resultsText = new TextField();
        Label usersUserIdLabel = new Label("Users User ID *:");
        ComboBox<String> usersUserIdComboBox = new ComboBox<>();
        ObservableList<String> usersUserIdList = fetchUserIdsFromDatabase();
        usersUserIdComboBox.setItems(usersUserIdList);
        Label dataDataIdLabel = new Label("Data ID *:");
        ComboBox<Integer> dataDataIdComboBox = new ComboBox<>();
        ObservableList<Integer> dataDataIdList = fetchDataIdsFromDatabase();
        dataDataIdComboBox.setItems(dataDataIdList);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);
        HBox analysisIdBox = new HBox();
        analysisIdBox.getChildren().addAll(
                analysisIdText,
                new Label("Auto Generated:"),
                createAutoGenerateCheckBox(analysisIdText)
        );
        analysisIdBox.setSpacing(5);

        TableView<Analysis> tableView = new TableView<>();
        TableColumn<Analysis, String> analysisIdCol = new TableColumn<>("Analysis ID");
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

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String analysisId = analysisIdText.getText();
            String analysisType = analysisTypeText.getText();
            String results = resultsText.getText();
            String usersUserId = usersUserIdComboBox.getValue();
            Integer dataDataId = dataDataIdComboBox.getValue();

            if (analysisId.isEmpty() || usersUserId.isEmpty() || dataDataId == null) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            if (!isAutoGenerateChecked(analysisIdText)) {
                analysisId = analysisIdText.getText();
            } else {
                // Mode auto-generate, tandai sebagai "AUTO_GENERATED"
                analysisId = "AUTO_GENERATED";
            }

            Analysis analysis = new Analysis(analysisId, analysisType, results, usersUserId, dataDataId);

            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".ANALYSIS (ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, USERS_USER_ID, DATA_DATA_ID) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, analysisId);
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
            usersUserIdComboBox.setValue(null);
            dataDataIdComboBox.setValue(null);
            errorLabel.setText("");
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                String analysisId = analysisIdText.getText();
                String analysisType = analysisTypeText.getText();
                String results = resultsText.getText();
                String usersUserId = usersUserIdComboBox.getValue();
                int dataDataId = dataDataIdComboBox.getValue();

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
                    pstmt.setString(5, analysisId);
                    pstmt.executeUpdate();
                    System.out.println("Analysis updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                analysisIdText.clear();
                analysisTypeText.clear();
                resultsText.clear();
                usersUserIdComboBox.setValue(null);
                dataDataIdComboBox.setValue(null);
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                String analysisId = selectedAnalysis.getAnalysisId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS WHERE ANALYSIS_ID = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, analysisId);
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
                analysisIdLabel, analysisIdBox,
                analysisTypeLabel, analysisTypeText,
                resultsLabel, resultsText,
                usersUserIdLabel, usersUserIdComboBox,
                dataDataIdLabel, dataDataIdComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField analysisIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                analysisIdText.setDisable(true);
                analysisIdText.clear();
            } else {
                analysisIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField analysisIdText) {
        CheckBox checkBox = (CheckBox) analysisIdText.getParent().getChildrenUnmodifiable().get(2);
        return checkBox.isSelected();
    }

    private static ObservableList<Analysis> fetchAnalysisFromDatabase() {
        ObservableList<Analysis> analysisList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, USERS_USER_ID, DATA_DATA_ID FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String analysisId = rs.getString("ANALYSIS_ID");
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

    private static ObservableList<String> fetchUserIdsFromDatabase() {
        ObservableList<String> usersUserIdList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT USER_ID FROM \"C4ISR PROJECT (BASIC) V2\".USERS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("USER_ID");
                usersUserIdList.add(userId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return usersUserIdList;
    }

    private static ObservableList<Integer> fetchDataIdsFromDatabase() {
        ObservableList<Integer> dataDataIdList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT DATA_ID FROM \"C4ISR PROJECT (BASIC) V2\".DATA";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int dataId = rs.getInt("DATA_ID");
                dataDataIdList.add(dataId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return dataDataIdList;
    }
}
