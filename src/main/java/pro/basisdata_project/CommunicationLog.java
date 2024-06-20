package pro.basisdata_project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
        TextField platformsPlatformIdText = new TextField();

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
            String platformsPlatformId = platformsPlatformIdText.getText();

            CommunicationLog log = new CommunicationLog(commId, message, platformsPlatformId);
            System.out.println("Communication Log Created: " + log.getCommId());

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
            platformsPlatformIdText.clear();
        });

        ObservableList<CommunicationLog> commLogList = fetchCommunicationLogsFromDatabase();
        tableView.setItems(commLogList);

        vbox.getChildren().addAll(
                commIdLabel, commIdText, messageLabel, messageText,
                platformsPlatformIdLabel, platformsPlatformIdText,
                tableView, createButton);

        return vbox;
    }

    private static ObservableList<CommunicationLog> fetchCommunicationLogsFromDatabase() {
        ObservableList<CommunicationLog> commLogList = FXCollections.observableArrayList();

        try (Connection conn = OracleAPEXConnection.getConnection()) {
            String sql = "SELECT COMM_ID, SOURCE_ID, DESTINATION_ID, TIMESTAMP, MESSAGE, STATUS FROM \"C4ISR PROJECT (BASIC) V2\".COMMUNICATION_LOG";
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
