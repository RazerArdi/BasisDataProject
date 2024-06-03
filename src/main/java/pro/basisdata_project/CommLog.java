package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class CommLog {

    private int commLogId;
    private String message;
    private String timestamp;
    private String senderId;
    private String receiverId;

    public CommLog(int commLogId, String message, String timestamp, String senderId, String receiverId) {
        this.commLogId = commLogId;
        this.message = message;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public int getCommLogId() {
        return commLogId;
    }

    public void setCommLogId(int commLogId) {
        this.commLogId = commLogId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public static VBox getCommLogUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label commLogIdLabel = new Label("CommLog ID:");
        TextField commLogIdText = new TextField();
        Label messageLabel = new Label("Message:");
        TextField messageText = new TextField();
        Label timestampLabel = new Label("Timestamp:");
        TextField timestampText = new TextField();
        Label senderIdLabel = new Label("Sender ID:");
        TextField senderIdText = new TextField();
        Label receiverIdLabel = new Label("Receiver ID:");
        TextField receiverIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int commLogId = Integer.parseInt(commLogIdText.getText());
            String message = messageText.getText();
            String timestamp = timestampText.getText();
            String senderId = senderIdText.getText();
            String receiverId = receiverIdText.getText();

            CommLog commLog = new CommLog(commLogId, message, timestamp, senderId, receiverId);
            System.out.println("CommLog Created: " + commLog.getCommLogId());
        });

        vbox.getChildren().addAll(commLogIdLabel, commLogIdText, messageLabel, messageText, timestampLabel, timestampText, senderIdLabel, senderIdText, receiverIdLabel, receiverIdText, createButton);

        return vbox;
    }
}
