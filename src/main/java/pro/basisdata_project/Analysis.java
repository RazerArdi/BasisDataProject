package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDateTime;

public class Analysis {

    private int analysisId;
    private String analysisType;
    private String result;
    private int dataId;
    private int usersUserId;

    public Analysis(int analysisId, String analysisType, String result, int dataId, int usersUserId) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.result = result;
        this.dataId = dataId;
        this.usersUserId = usersUserId;
    }




    public int getUsersUserId() {
        return usersUserId;
    }

    public void setUsersUserId(int usersUserId) {
        this.usersUserId = usersUserId;
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

        Label analysisIdLabel = new Label("Analysis ID *:");
        TextField analysisIdText = new TextField();
        Label analysisTypeLabel = new Label("Analysis Type *:");
        TextField analysisTypeText = new TextField();
        Label resultLabel = new Label("Result:");
        TextArea resultText = new TextArea();
        Label dataIdLabel = new Label("Data ID *:");
        ComboBox<Integer> dataIdComboBox = new ComboBox<>();
        ObservableList<Integer> dataIdList = fetchAvailableDataIds();
        dataIdComboBox.setItems(dataIdList);
        Label usersUserIdLabel = new Label("User ID *:");
        ComboBox<Integer> usersUserIdComboBox = new ComboBox<>();
        ObservableList<Integer> usersUserIdList = fetchAvailableUserIds();
        usersUserIdComboBox.setItems(usersUserIdList);

        CheckBox autoGenerateCheckBox = new CheckBox("Auto Generate");
        autoGenerateCheckBox.setSelected(true);
        autoGenerateCheckBox.setOnAction(e -> {
            if (autoGenerateCheckBox.isSelected()) {
                analysisIdText.setDisable(true);
                analysisIdText.clear();
            } else {
                analysisIdText.setDisable(false);
            }
        });

        HBox analysisIdBox = new HBox(5, analysisIdText, autoGenerateCheckBox);

        TableView<Analysis> tableView = new TableView<>();
        TableColumn<Analysis, Integer> analysisIdCol = new TableColumn<>("Analysis ID");
        analysisIdCol.setCellValueFactory(new PropertyValueFactory<>("analysisId"));
        TableColumn<Analysis, String> analysisTypeCol = new TableColumn<>("Analysis Type");
        analysisTypeCol.setCellValueFactory(new PropertyValueFactory<>("analysisType"));
        TableColumn<Analysis, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        TableColumn<Analysis, Integer> dataIdCol = new TableColumn<>("Data ID");
        dataIdCol.setCellValueFactory(new PropertyValueFactory<>("dataId"));
        TableColumn<Analysis, Integer> usersUserIdCol = new TableColumn<>("User ID");
        usersUserIdCol.setCellValueFactory(new PropertyValueFactory<>("usersUserId"));

        tableView.getColumns().addAll(analysisIdCol, analysisTypeCol, resultCol, dataIdCol, usersUserIdCol);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            try {
                int analysisId;
                if (!autoGenerateCheckBox.isSelected()) {
                    String analysisIdStr = analysisIdText.getText().trim();
                    if (analysisIdStr.isEmpty()) {
                        errorLabel.setText("Analysis ID is required!");
                        return;
                    }
                    try {
                        analysisId = Integer.parseInt(analysisIdStr);
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Analysis ID", "Analysis ID must be a valid number!");
                        return;
                    }
                } else {
                    analysisId = getNextAnalysisIdFromDatabase();
                }

                String analysisType = analysisTypeText.getText();
                String result = resultText.getText();
                Integer dataId = dataIdComboBox.getValue();
                Integer usersUserId = usersUserIdComboBox.getValue();

                if (analysisType.isEmpty() || dataId == null || usersUserId == null) {
                    showAlert(Alert.AlertType.ERROR, "Missing Fields", "Required Fields", "Fields marked with * are required!");
                    return;
                }

                Analysis analysis = new Analysis(analysisId, analysisType, result, dataId, usersUserId);

                saveAnalysisToDatabase(analysis);
                tableView.getItems().add(analysis);

                showAlert(Alert.AlertType.INFORMATION, "Analysis Created", "Success", "Analysis has been created successfully.");

                analysisIdText.clear();
                analysisTypeText.clear();
                resultText.clear();
                dataIdComboBox.setValue(null);
                usersUserIdComboBox.setValue(null);
                errorLabel.setText("");
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Analysis Data", "Please enter valid data.");
            }
        });


        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                try {
                    int analysisId;
                    if (!autoGenerateCheckBox.isSelected()) {
                        analysisId = Integer.parseInt(analysisIdText.getText());
                    } else {
                        analysisId = selectedAnalysis.getAnalysisId();
                    }

                    String analysisType = analysisTypeText.getText();
                    String result = resultText.getText();
                    Integer dataId = dataIdComboBox.getValue();

                    selectedAnalysis.setAnalysisId(analysisId);
                    selectedAnalysis.setAnalysisType(analysisType);
                    selectedAnalysis.setResult(result);
                    selectedAnalysis.setDataId(dataId);

                    updateAnalysisInDatabase(selectedAnalysis);

                    tableView.refresh();

                    analysisIdText.clear();
                    analysisTypeText.clear();
                    resultText.clear();
                    dataIdComboBox.setValue(null);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid Analysis Data", "Please enter valid data.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Analysis Selected", "Please select an analysis to edit.");
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Analysis selectedAnalysis = tableView.getSelectionModel().getSelectedItem();
            if (selectedAnalysis != null) {
                deleteAnalysisFromDatabase(selectedAnalysis);

                tableView.getItems().remove(selectedAnalysis);
                showAlert(Alert.AlertType.INFORMATION, "Analysis Deleted", "Success", "Analysis has been deleted successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Analysis Selected", "Please select an analysis to delete.");
            }
        });

        ObservableList<Analysis> analysisList = fetchAnalysisFromDatabase();
        tableView.setItems(analysisList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                analysisIdLabel, analysisIdBox,
                analysisTypeLabel, analysisTypeText,
                resultLabel, resultText,
                dataIdLabel, dataIdComboBox, usersUserIdLabel, usersUserIdComboBox,
                errorLabel, buttonBox, tableView
        );

        return vbox;
    }

    private static ObservableList<Integer> fetchAvailableUserIds() {
        ObservableList<Integer> userIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT USER_ID FROM \"C4ISR PROJECT (BASIC) V2\".USERS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("USER_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch user IDs from database.");
        }
        return userIds;
    }


    private static ObservableList<Integer> fetchAvailableDataIds() {
        ObservableList<Integer> dataIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT DATA_ID FROM \"C4ISR PROJECT (BASIC) V2\".DATA";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                dataIds.add(rs.getInt("DATA_ID"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch data IDs from database.");
        }
        return dataIds;
    }

    private static ObservableList<Analysis> fetchAnalysisFromDatabase() {
        ObservableList<Analysis> analysisList = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, DATA_DATA_ID, USERS_USER_ID FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int analysisId = rs.getInt("ANALYSIS_ID");
                String analysisType = rs.getString("ANALYSIS_TYPE");
                String result = rs.getString("RESULTS");
                int dataId = rs.getInt("DATA_DATA_ID");
                int usersUserId = rs.getInt("USERS_USER_ID");
                Analysis analysis = new Analysis(analysisId, analysisType, result, dataId, usersUserId);
                analysisList.add(analysis);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Fetch Error", "Fetch Operation Failed", "Failed to fetch analysis data from database.");
        }
        return analysisList;
    }


    private static void saveAnalysisToDatabase(Analysis analysis) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".ANALYSIS (ANALYSIS_ID, ANALYSIS_TYPE, RESULTS, DATA_DATA_ID, USERS_USER_ID) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, analysis.getAnalysisId());
            pstmt.setString(2, analysis.getAnalysisType());
            pstmt.setString(3, analysis.getResult());
            pstmt.setInt(4, analysis.getDataId());
            pstmt.setInt(5, analysis.getUsersUserId());
            pstmt.executeUpdate();
            System.out.println("Analysis saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Save Error", "Save Operation Failed", "Failed to save analysis data to database.");
        }
    }

    private static void updateAnalysisInDatabase(Analysis analysis) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".ANALYSIS SET ANALYSIS_TYPE = ?, RESULTS = ?, DATA_DATA_ID = ?, USERS_USER_ID = ? WHERE ANALYSIS_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, analysis.getAnalysisType());
            pstmt.setString(2, analysis.getResult());
            pstmt.setInt(3, analysis.getDataId());
            pstmt.setInt(4, analysis.getUsersUserId());
            pstmt.setInt(5, analysis.getAnalysisId());
            pstmt.executeUpdate();
            System.out.println("Analysis updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Error", "Update Operation Failed", "Failed to update analysis data in database.");
        }
    }


    private static void deleteAnalysisFromDatabase(Analysis analysis) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".ANALYSIS WHERE ANALYSIS_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, analysis.getAnalysisId());
            pstmt.executeUpdate();
            System.out.println("Analysis deleted from database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Delete Error", "Delete Operation Failed", "Failed to delete analysis data from database.");
        }
    }

    private static void showAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static int getNextAnalysisIdFromDatabase() {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT \"C4ISR PROJECT (BASIC) V2\".ANALYSIS_SEQ.NEXTVAL FROM dual";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "ID Generation Error", "Failed to Generate ID", "Failed to generate next analysis ID.");
        }
        return -1;
    }
}
