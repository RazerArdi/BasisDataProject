package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Maintenancelogs {

    private int logId;
    private String description;
    private String date;
    private String userId;
    private int platformId;

    public Maintenancelogs(int logId, String description, String date, String userId, int platformId) {
        this.logId = logId;
        this.description = description;
        this.date = date;
        this.userId = userId;
        this.platformId = platformId;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public static VBox getMaintenancelogsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label logIdLabel = new Label("Log ID:");
        TextField logIdText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextArea descriptionText = new TextArea();
        Label dateLabel = new Label("Date:");
        TextField dateText = new TextField();
        Label userIdLabel = new Label("User ID:");
        TextField userIdText = new TextField();
        Label platformIdLabel = new Label("Platform ID:");
        TextField platformIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int logId = Integer.parseInt(logIdText.getText());
            String description = descriptionText.getText();
            String date = dateText.getText();
            String userId = userIdText.getText();
            int platformId = Integer.parseInt(platformIdText.getText());

            Maintenancelogs maintenancelog = new Maintenancelogs(logId, description, date, userId, platformId);
            System.out.println("Maintenancelog Created: " + maintenancelog.getLogId());

            // Save to Database.txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Database.txt", true))) {
                writer.write(String.format("Maintenancelogs,%d,%s,%s,%s,%d%n", logId, description, date, userId, platformId));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vbox.getChildren().addAll(logIdLabel, logIdText, descriptionLabel, descriptionText, dateLabel, dateText, userIdLabel, userIdText, platformIdLabel, platformIdText, createButton);

        return vbox;
    }
}
