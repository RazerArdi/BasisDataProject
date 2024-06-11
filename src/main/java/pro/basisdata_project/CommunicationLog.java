package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("%s,%s,%s%n", commId, message, platformsPlatformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Add new communication log to the table view
            tableView.getItems().add(log);

            // Clear input fields after adding log
            commIdText.clear();
            messageText.clear();
            platformsPlatformIdText.clear();
        });

        vbox.getChildren().addAll(
                commIdLabel, commIdText, messageLabel, messageText,
                platformsPlatformIdLabel, platformsPlatformIdText,
                tableView, createButton);

        return vbox;
    }
}
