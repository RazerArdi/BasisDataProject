package pro.basisdata_project;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class Maintenancelogs {

    private int maintenanceLogId;
    private String description;
    private String date;
    private String equipmentId;
    private String personnelId;

    public Maintenancelogs(int maintenanceLogId, String description, String date, String equipmentId, String personnelId) {
        this.maintenanceLogId = maintenanceLogId;
        this.description = description;
        this.date = date;
        this.equipmentId = equipmentId;
        this.personnelId = personnelId;
    }

    public int getMaintenanceLogId() {
        return maintenanceLogId;
    }

    public void setMaintenanceLogId(int maintenanceLogId) {
        this.maintenanceLogId = maintenanceLogId;
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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(String personnelId) {
        this.personnelId = personnelId;
    }

    public static VBox getMaintenancelogsUI() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        Label maintenanceLogIdLabel = new Label("Maintenance Log ID:");
        TextField maintenanceLogIdText = new TextField();
        Label descriptionLabel = new Label("Description:");
        TextField descriptionText = new TextField();
        Label dateLabel = new Label("Date:");
        TextField dateText = new TextField();
        Label equipmentIdLabel = new Label("Equipment ID:");
        TextField equipmentIdText = new TextField();
        Label personnelIdLabel = new Label("Personnel ID:");
        TextField personnelIdText = new TextField();

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            int maintenanceLogId = Integer.parseInt(maintenanceLogIdText.getText());
            String description = descriptionText.getText();
            String date = dateText.getText();
            String equipmentId = equipmentIdText.getText();
            String personnelId = personnelIdText.getText();

            Maintenancelogs maintenancelog = new Maintenancelogs(maintenanceLogId, description, date, equipmentId, personnelId);
            System.out.println("Maintenance Log Created: " + maintenancelog.getMaintenanceLogId());
        });

        vbox.getChildren().addAll(maintenanceLogIdLabel, maintenanceLogIdText, descriptionLabel, descriptionText, dateLabel, dateText, equipmentIdLabel, equipmentIdText, personnelIdLabel, personnelIdText, createButton);

        return vbox;
    }
}
