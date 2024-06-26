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

        Label commIdLabel = new Label("Communication ID:");
        TextField commIdText = new TextField();
        Label messageLabel = new Label("Message:");
        TextField messageText = new TextField();
        Label platformsPlatformIdLabel = new Label("Platform ID:");
        ComboBox<String> platformsPlatformIdComboBox = new ComboBox<>();
        ObservableList<String> platformIds = fetchPlatformIdsFromDatabase();
        platformsPlatformIdComboBox.setItems(platformIds);

        CheckBox autoIncrementCheckBox = new CheckBox("Auto Increment");
        autoIncrementCheckBox.setSelected(true);

        HBox commIdBox = new HBox();
        commIdBox.getChildren().addAll(
                commIdText,
                new Label("Auto Generated:"),
                createAutoGenerateCheckBox(commIdText) // Create auto-generate checkbox for Comm ID
        );
        commIdBox.setSpacing(5);

        TableView<CommunicationLog> tableView = new TableView<>();
        TableColumn<CommunicationLog, String> commIdCol = new TableColumn<>("Communication ID");
        commIdCol.setCellValueFactory(new PropertyValueFactory<>("commId"));
        TableColumn<CommunicationLog, String> messageCol = new TableColumn<>("Message");
        messageCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        TableColumn<CommunicationLog, String> platformsPlatformIdCol = new TableColumn<>("Platform ID");
        platformsPlatformIdCol.setCellValueFactory(new PropertyValueFactory<>("platformsPlatformId"));
        tableView.getColumns().addAll(commIdCol, messageCol, platformsPlatformIdCol);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String commId = commIdText.getText();
            String message = messageText.getText();
            String platformsPlatformId = platformsPlatformIdComboBox.getValue();

            if (!isAutoGenerateChecked(commIdText)) {
                commId = commIdText.getText();
            } else {
                commId = "AUTO_GENERATED"; // Set as auto-generated
            }

            CommunicationLog log = new CommunicationLog(commId, message, platformsPlatformId);

            // Save to Oracle database
            try (Connection conn = OracleAPEXConnection.getConnection()) {
                String sql = "INSERT INTO \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG (comm_id, message, platforms_platform_id) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, commId);
                pstmt.setString(2, message);
                pstmt.setString(3, platformsPlatformId);
                pstmt.executeUpdate();
                System.out.println("Communication Log saved to database.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            tableView.getItems().add(log);

            commIdText.clear();
            messageText.clear();
            platformsPlatformIdComboBox.setValue(null); // Clear ComboBox selection
        });

        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> {
            CommunicationLog selectedLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedLog != null) {
                String commId = commIdText.getText();
                String message = messageText.getText();
                String platformsPlatformId = platformsPlatformIdComboBox.getValue();

                selectedLog.setCommId(commId);
                selectedLog.setMessage(message);
                selectedLog.setPlatformsPlatformId(platformsPlatformId);

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "UPDATE \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG SET message = ?, platforms_platform_id = ? WHERE comm_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, message);
                    pstmt.setString(2, platformsPlatformId);
                    pstmt.setString(3, commId);
                    pstmt.executeUpdate();
                    System.out.println("Communication Log updated in database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.refresh();

                commIdText.clear();
                messageText.clear();
                platformsPlatformIdComboBox.setValue(null); // Clear ComboBox selection
            }
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            CommunicationLog selectedLog = tableView.getSelectionModel().getSelectedItem();
            if (selectedLog != null) {
                String commId = selectedLog.getCommId();

                try (Connection conn = OracleAPEXConnection.getConnection()) {
                    String sql = "DELETE FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG WHERE comm_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, commId);
                    pstmt.executeUpdate();
                    System.out.println("Communication Log deleted from database.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                tableView.getItems().remove(selectedLog);
            }
        });

        ObservableList<CommunicationLog> commLogList = fetchCommunicationLogsFromDatabase();
        tableView.setItems(commLogList);

        HBox buttonBox = new HBox(10, createButton, editButton, deleteButton);

        vbox.getChildren().addAll(
                commIdLabel, commIdBox, // Add commIdBox instead of commIdText
                messageLabel, messageText,
                platformsPlatformIdLabel, platformsPlatformIdComboBox,
                tableView, buttonBox);

        return vbox;
    }

    private static CheckBox createAutoGenerateCheckBox(TextField commIdText) {
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Checkbox checked, disable manual input
                commIdText.setDisable(true);
                commIdText.clear(); // Clear any possibly entered value
            } else {
                // Checkbox unchecked, enable manual input
                commIdText.setDisable(false);
            }
        });
        return checkBox;
    }

    private static boolean isAutoGenerateChecked(TextField commIdText) {
        CheckBox checkBox = (CheckBox) commIdText.getParent().getChildrenUnmodifiable().get(2); // Adjust CheckBox index
        return checkBox.isSelected();
    }

    private static ObservableList<String> fetchPlatformIdsFromDatabase() {
        ObservableList<String> platformIds = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT PLATFORM_ID FROM \"C4ISR PROJECT (BASIC) V2\".PLATFORMS";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String platformId = rs.getString("PLATFORM_ID");
                platformIds.add(platformId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return platformIds;
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
                String platformsPlatformId = rs.getString("platforms_platform_id");

                CommunicationLog log = new CommunicationLog(commId, message, platformsPlatformId);
                commLogList.add(log);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return commLogList;
    }
}
