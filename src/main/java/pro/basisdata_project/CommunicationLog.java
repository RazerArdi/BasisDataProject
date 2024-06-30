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

public class CommunicationLog {

    private String commId;
    private String message;
    private String platformsPlatformId;

    public CommunicationLog(String commId, String message, String platformsPlatformId) {
        this.commId = commId;
        this.message = message;
        this.platformsPlatformId = platformsPlatformId;
    }

    public String getCommId() {
        return commId;
    }

    public void setCommId(String commId) {
        this.commId = commId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlatformsPlatformId() {
        return platformsPlatformId;
    }

    public void setPlatformsPlatformId(String platformsPlatformId) {
        this.platformsPlatformId = platformsPlatformId;
    }

    public static VBox getCommunicationLogUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label commIdLabel = new Label("Communication ID *:");
        TextField commIdText = new TextField();
        CheckBox autoGenerateCheckbox = new CheckBox("Auto Generate");
        autoGenerateCheckbox.setSelected(true);
        autoGenerateCheckbox.setOnAction(e -> {
            commIdText.setDisable(autoGenerateCheckbox.isSelected());
            if (autoGenerateCheckbox.isSelected()) {
                commIdText.clear();
            }
        });

        Label messageLabel = new Label("Message *:");
        TextField messageText = new TextField();
        Label platformsPlatformIdLabel = new Label("Platform ID *:");
        ComboBox<String> platformsPlatformIdComboBox = new ComboBox<>();
        ObservableList<String> platformIds = fetchPlatformIdsFromDatabase();
        platformsPlatformIdComboBox.setItems(platformIds);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        TableView<CommunicationLog> tableView = new TableView<>();
        TableColumn<CommunicationLog, String> commIdCol = new TableColumn<>("Communication ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("commId"));
        TableColumn<CommunicationLog, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableColumn<CommunicationLog, String> platformsPlatformIdCol = new TableColumn<>("Platform ID");
        platformsPlatformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformsPlatformId"));
        tableView.getColumns().addAll(commIdCol, messageCol, platformsPlatformIdCol);

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            CommunicationLog selectedLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedLog != null) {
                String commId = selectedLog.getCommId();
                String message = messageText.getText();
                String platformsPlatformId = platformsPlatformIdComboBox.getValue();

                if (message.isEmpty() || platformsPlatformId == null) {
                    errorLabel.setText("Fields marked with * are required!");
                    return;
                }

                selectedLog.setMessage(message);
                selectedLog.setPlatformsPlatformId(platformsPlatformId);

                updateCommunicationLogInDatabase(selectedLog);
                refreshTableView(tableView);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Communication Log Updated", "Communication log details have been updated successfully.");

                commIdText.clear();
                messageText.clear();
                platformsPlatformIdComboBox.setValue(null);
                errorLabel.setText("");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Communication Log Selected", "Please select a communication log to edit.");
            }
        });



        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            CommunicationLog selectedLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedLog != null) {
                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG WHERE comm_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedLog.getCommId());
                    int affected = pstmt.executeUpdate();
                    if (affected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Delete Successful", "Communication Log Deleted", "Communication Log with ID " + selectedLog.getCommId() + " has been deleted.");
                        refreshTableView(tableView);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Delete Failed", "Delete Operation Failed", "Failed to delete communication log from database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "No Communication Log Selected", "Please select a communication log to delete.");
            }
        });



        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String commId = commIdText.getText();
            String message = messageText.getText();
            String platformsPlatformId = platformsPlatformIdComboBox.getValue();

            if (message.isEmpty() || platformsPlatformId == null || (!autoGenerateCheckbox.isSelected() && commId.isEmpty())) {
                errorLabel.setText("Fields marked with * are required!");
                return;
            }

            CommunicationLog log = new CommunicationLog(commId, message, platformsPlatformId);
            saveCommunicationLogToDatabase(log, autoGenerateCheckbox.isSelected());
            refreshTableView(tableView);

            commIdText.clear();
            messageText.clear();
            platformsPlatformIdComboBox.setValue(null);
            errorLabel.setText("");
        });

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        ObservableList<CommunicationLog> commLogList = fetchCommunicationLogsFromDatabase();
        tableView.setItems(commLogList);

        vbox.getChildren().addAll(
                commIdLabel, new HBox(10, commIdText, autoGenerateCheckbox),
                messageLabel, messageText,
                platformsPlatformIdLabel, platformsPlatformIdComboBox,
                errorLabel, tableView, buttonBox);

        return vbox;
    }

    private static void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private static void saveCommunicationLogToDatabase(CommunicationLog log, boolean autoGenerate) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG (comm_id, message, platforms_platform_id) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (autoGenerate) {
                pstmt.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(1, log.getCommId());
            }
            pstmt.setString(2, log.getMessage());
            pstmt.setString(3, log.getPlatformsPlatformId());
            pstmt.executeUpdate();
            System.out.println("Communication Log saved to database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void refreshTableView(TableView<CommunicationLog> tableView) {
        tableView.getItems().clear();
        tableView.setItems(fetchCommunicationLogsFromDatabase());
    }

    private static ObservableList<CommunicationLog> fetchCommunicationLogsFromDatabase() {
        ObservableList<CommunicationLog> commLogList = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT comm_id, message, platforms_platform_id FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String commId = rs.getString("comm_id");
                String message = rs.getString("message");
                String platformId = rs.getString("platforms_platform_id");
                CommunicationLog log = new CommunicationLog(commId, message, platformId);
                commLogList.add(log);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return commLogList;
    }

    private static ObservableList<String> fetchPlatformIdsFromDatabase() {
        ObservableList<String> platformIds = FXCollections.observableArrayList();
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT platform_id FROM \"C4ISR PROJECT (BASIC) V2\".PLATFORMS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String platformId = rs.getString("platform_id");
                platformIds.add(platformId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return platformIds;
    }

    public static void updateCommunicationLogInDatabase(CommunicationLog log) {
        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG SET message = ?, platforms_platform_id = ? WHERE comm_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, log.getMessage());
            pstmt.setString(2, log.getPlatformsPlatformId());
            pstmt.setString(3, log.getCommId());
            pstmt.executeUpdate();
            System.out.println("Communication Log updated in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
